/**
 * 
 */
package com.microcaliperdevices.saje.config;

import java.util.Hashtable;
import java.util.Map;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * @author jg
 * Property order of XmlType sets up configuration menu display order on client
 */
@XmlType( propOrder={ "reportHeading1","reportHeading2","reportValue1","reportValue2",
		"samplesPerGroup", "baseline", "numGroups","upperDeviation",
		"upperReject", "lowerDeviation","lowerReject","beginIgnore","cutoff","endIgnore",
		"groupSpacing","sampleSpacing"} )
public abstract class AbstractRunConfig {
	@XmlTransient
	protected Hashtable ht = new Hashtable();
	
	private int samplesPerGroup;
    private int numGroups; 
    private float baseline;
    private float upperDeviation;
    private float lowerDeviation;
    private float upperReject;
    private float lowerReject;
    private int beginIgnore;
    private int endIgnore;
    private float groupSpacing;
    private float sampleSpacing;
    private float cutoff;
    private String fld0Label = "";
    private String fld0Val = "";
    private String fld1Label = "";
    private String fld1Val = "";
 
    protected Map<QName, String> fieldDescriptions;
    
    protected boolean isValid; // field validation
    protected String whyInvalid; // reason for invalidity
    
    @XmlElement(name="Chart_Begin_Ignore")
    public int getBeginIgnore() {
		return beginIgnore;
	}
	public void setBeginIgnore(int beginIgnore) {
		this.beginIgnore = beginIgnore;
	}
	@XmlElement(name="Chart_End_Ignore")
	public int getEndIgnore() {
		return endIgnore;
	}
	public void setEndIgnore(int endIgnore) {
		this.endIgnore = endIgnore;
	}
	@XmlElement(name="Run_Cutoff")
	public float getCutoff() {
		return cutoff;
	}
	public void setCutoff(float cutoff) {
		this.cutoff = cutoff;
	}   
	@XmlElement(name="Run_Group_Spacing")
	public float getGroupSpacing() {
		return groupSpacing;
	}
	public void setGroupSpacing(float groupSpacing) {
		this.groupSpacing = groupSpacing;
	}
	@XmlElement(name="Run_Sample_Spacing")
	public float getSampleSpacing() {
		return sampleSpacing;
	}
	public void setSampleSpacing(float sampleSpacing) {
		this.sampleSpacing = sampleSpacing;
	}
	@XmlElement(name="Run_Samples_per_Group")
    public int getSamplesPerGroup() {
		return samplesPerGroup;
	}
	public void setSamplesPerGroup(int samplesPerGroup) {
		this.samplesPerGroup = samplesPerGroup;
	}
	@XmlElement(name="Run_Number_of_Groups")
	public int getNumGroups() {
		return numGroups;
	}
	public void setNumGroups(int numGroups) {
		this.numGroups = numGroups;
	}
	@XmlElement(name="Chart_Baseline_Measurement")
	public float getBaseline() {
		return baseline;
	}
	public void setBaseline(float baseline) {
		this.baseline = baseline;
	}
	@XmlElement(name="Chart_Upper_Deviation")
	public float getUpperDeviation() {
		return upperDeviation;
	}
	public void setUpperDeviation(float upperDeviation) {
		this.upperDeviation = upperDeviation;
	}
	@XmlElement(name="Chart_Lower_Deviation")
	public float getLowerDeviation() {
		return lowerDeviation;
	}
	public void setLowerDeviation(float lowerDeviation) {
		this.lowerDeviation = lowerDeviation;
	}
	@XmlElement(name="Chart_Upper_Rejection")
	public float getUpperReject() {
		return upperReject;
	}
	public void setUpperReject(float upperReject) {
		this.upperReject = upperReject;
	}
	@XmlElement(name="Chart_Lower_Rejection")
	public float getLowerReject() {
		return lowerReject;
	}
	public void setLowerReject(float lowerReject) {
		this.lowerReject = lowerReject;
	}
	@XmlElement(name="User_Report_Heading_1")
	public String getReportHeading1() {
		return fld0Label;
	}
	public void setReportHeading1(String reportHeading1) {
		this.fld0Label = reportHeading1;
	}
	@XmlElement(name="User_Report_Heading_2")
	public String getReportHeading2() {
		return fld1Label;
	}
	public void setReportHeading2(String reportHeading2) {
		this.fld1Label = reportHeading2;
	}
	@XmlElement(name="User_Report_Value_1")
	public String getReportValue1() {
		return fld0Val;
	}
	public void setReportValue1(String reportValue1) {
		this.fld0Val = reportValue1;
	}
	@XmlElement(name="User_Report_Value_2")
	public String getReportValue2() {
		return fld1Val;
	}
	public void setReportValue2(String reportValue2) {
		this.fld1Val = reportValue2;
	}
	
	public abstract String toXml();
	public abstract Object fromXml(String xml);
	public abstract Object clone();

	public boolean isValid() { return isValid; }
	public String whyInvalid() { return whyInvalid; }

	@XmlAnyAttribute
	public Map<QName, String> getFieldDescriptions() {
	        return fieldDescriptions;
	}

    public void validate() {
    	isValid = true;
    	whyInvalid = "";
    	if( samplesPerGroup <= 0 ) {
    		isValid = false;
    		whyInvalid += "Samples Per Group must be greater than zero\n";
    	}
    	if( numGroups <= 0 ) {
    		isValid = false;
    		whyInvalid += "Number of Groups must be greater than zero\n";
    	}
    	if( baseline < lowerDeviation || baseline > upperDeviation ) {
    		isValid = false;
    		whyInvalid += "Chart Baseline must be between Upper Deviation and Lower Deviation\n";
    	}
    	if( upperDeviation < lowerDeviation ) {
    		isValid = false;
    		whyInvalid += "Chart Upper Deviation cannot be less than Chart Lower Deviation\n";
    	}
    	if( upperReject < lowerReject ) {
    		isValid = false;
    		whyInvalid += "Chart Upper Reject cannot be less than Chart Lower Reject range\n";
    	}
    	if( upperReject > upperDeviation ) {
    		isValid = false;
    		whyInvalid += "Chart Upper Reject cannot be greater than Chart Upper Deviation\n";
    	}
    	if( lowerReject < lowerDeviation ) {
    		isValid = false;
    		whyInvalid += "Chart Lower Reject cannot be less than Chart Lower Deviation\n";
    	}
	    if( beginIgnore > endIgnore ) {
	    	isValid = false;
	    	whyInvalid += "Chart Begin Ignore cannot be greater than Chart End Ignore\n";
	    }
	    if( cutoff < 0 ) {
	    	isValid = false;
	    	whyInvalid += "Cutoff value must be greater than zero\n";
	    }
	    if( groupSpacing < 0 ) {
	    	isValid = false;
	    	whyInvalid += "Minimum Group Spacing is zero\n";
	    }
	    if( sampleSpacing < 0 ) {
	    	isValid = false;
	    	whyInvalid += "Run Sample Spacing must be zero or greater\n";
	    }
    }

}
