/**
 * 
 */
package com.microcaliperdevices.saje.config;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;

import com.microcaliperdevices.saje.Props;
import com.microcaliperdevices.saje.SetRuntypeUp;
import com.microcaliperdevices.saje.io.file.DirectoryNotFoundException;
import com.microcaliperdevices.saje.io.file.FileIOUtilities;
import com.microcaliperdevices.saje.io.file.IniFile;
import com.microcaliperdevices.saje.license.License;

/**
 * @author jg
 *
 */
@XmlRootElement(name="runconfig")
//@XmlType( propOrder={ "samplesPerGroup", "Number_of_Groups", "Baseline_Measurement","Upper_Deviation","Lower_Deviation","Upper_Rejection","Lower_Rejection" } )
//@XmlType( propOrder={ "beginIgnore","cutoff","endIgnore" } )
public class DiscreteRunConfig extends AbstractRunConfig {
	public String toXml() {
		StringWriter writer = new StringWriter();
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(DiscreteRunConfig.class);
			Marshaller m = context.createMarshaller();
			m.marshal(this, writer);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		//if( Props.DEBUG ) System.out.println(writer);
		return writer.toString();
	}
	public Object fromXml(String xml) {
		StringReader reader = new StringReader(xml);
		JAXBContext context;
		Object o = null;
		try {
			context = JAXBContext.newInstance(DiscreteRunConfig.class);
			Unmarshaller m = context.createUnmarshaller();
			o = m.unmarshal(reader);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return o;
	}
	
	private static DiscreteRunConfig instance = null;
	public static DiscreteRunConfig newInstance(License lic) throws IOException, DirectoryNotFoundException {
		if( instance == null ) {
			instance = new DiscreteRunConfig(lic);
		}
		return instance;
	}
	
	// JAXB demands default no arg
	public DiscreteRunConfig() {}
	
	private DiscreteRunConfig(License lic) throws IOException, DirectoryNotFoundException {
		String section = BaseMachineConfig.getInstance().getMachineType();
		FileIOUtilities.createDefaultConfig(SetRuntypeUp.getRuntype());
		ht = IniFile.getCategories(FileIOUtilities.getConfigfileName(SetRuntypeUp.getRuntype()));
        setCutoff(new Float(IniFile.getIniFileString(ht, section, "Cutoff")));
        setNumGroups(new Integer(IniFile.getIniFileString(ht, section, "NumGroups")).intValue());
        setSamplesPerGroup(new Integer(IniFile.getIniFileString(ht, section, "SamplesPerGroup")).intValue());
        setBaseline(new Float(IniFile.getIniFileString(ht, section, "Baseline")));
        setUpperDeviation(new Float(IniFile.getIniFileString(ht, section, "UpperDeviation")));
        setLowerDeviation(new Float(IniFile.getIniFileString(ht, section, "LowerDeviation")));
        setUpperReject(new Float(IniFile.getIniFileString(ht, section, "UpperReject")));
        setLowerReject(new Float(IniFile.getIniFileString(ht, section, "LowerReject")));
        setBeginIgnore(new Integer(IniFile.getIniFileString(ht, section, "BeginIgnore")).intValue());
        setEndIgnore(new Integer(IniFile.getIniFileString(ht, section, "EndIgnore")).intValue());
	}
	
	public void validate() {
		super.validate();   

	}
	
	public static void main(String[] args) throws Exception {
		License lic = new License();
		DiscreteRunConfig cfg = DiscreteRunConfig.newInstance(lic);
		if( Props.DEBUG ) System.out.println(cfg.toXml());
		Object o = cfg.fromXml(cfg.toXml());
		if( Props.DEBUG ) System.out.println(o);
	}
	/* (non-Javadoc)
	 * @see com.microcaliperdevices.saje.config.RunConfig#clone()
	 */
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return null;
	}
}
