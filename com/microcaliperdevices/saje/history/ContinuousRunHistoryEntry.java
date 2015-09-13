/**
 * 
 */
package com.microcaliperdevices.saje.history;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author jg
 *
 */
@XmlRootElement
public class ContinuousRunHistoryEntry extends DiscreteRunHistoryEntry {
 
    private String sampleLength;
    private String average;
    private String standardDev;
    @XmlElement
	public String getSampleLength() {
		return sampleLength;
	}
	public void setSampleLength(String sampleLength) {
		this.sampleLength = sampleLength;
	}
	@XmlElement
	public String getAverage() {
		return average;
	}
	public void setAverage(String average) {
		this.average = average;
	}
	@XmlElement
	public String getStandardDeviation() {
		return standardDev;
	}
	public void setStandardDeviation(String sd) {
		this.standardDev = sd;
	}
	
}
