/**
 * 
 */
package com.microcaliperdevices.saje.run;

import java.io.IOException;

import com.microcaliperdevices.saje.SetRuntypeUp;
import com.microcaliperdevices.saje.config.AbstractRunConfig;
import com.microcaliperdevices.saje.config.DiscreteRunConfig;
import com.microcaliperdevices.saje.io.file.DirectoryNotFoundException;
import com.microcaliperdevices.saje.io.machine.AbstractMachine;
import com.microcaliperdevices.saje.io.machine.PreProcessInterface;
import com.microcaliperdevices.saje.license.License;

/**
 * @author jg
 *
 */
public class DiscreteStreamRun extends DiscreteRun implements PreProcessInterface {
	// the machine carries this, but for a file we have to keep it here
	// because we store values in normalized form and so cant determine their units
	// we get it from the run settings in the [SETTINGS] section, so we are setting it up here
	// even though conceptually its a machine attribute
	private String unitsOfMeasure;
	/**
	 * @param lic
	 * @throws DirectoryNotFoundException 
	 * @throws IOException 
	 */
	public DiscreteStreamRun(License lic, AbstractRunConfig arc) throws IOException, DirectoryNotFoundException {
		super(lic, arc);
		SetRuntypeUp.setDataSource("Stream");
	}
	public String getUnitsOfMeasure() {
		return unitsOfMeasure;
	}
	public void setUnitsOfMeasure(String unitsOfMeasure) {
		this.unitsOfMeasure = unitsOfMeasure;
	}
	/**
	 * Reset run params from prn file
	 * @param ifm
	 * @return The starting group, the first/next GROUP encountered or -1 if at EOF
	 */
	public int preProcess(AbstractMachine ifm) {
		//if( Props.DEBUG ) System.out.println("Reset run data "+ifm.getMachineType()+" "+ifm.getPortName());
		String inLine;
		boolean isRun = true;
		while( (inLine = ifm.getDataPort().readLine()) != null && isRun ) {
			//if( Props.DEBUG ) System.out.println("Main iter="+inLine);
			if( inLine.trim().length() == 0 )
				continue;
			if( inLine.charAt(0) == '[') {
				String setting = inLine.substring(1, inLine.length() - 1);
				switch(setting) {
					case "SETTINGS":
						while( (inLine = ifm.getDataPort().readLine()) != null ) {
							//if( Props.DEBUG ) System.out.println("settings iter");
							if( inLine.trim().length() == 0 || inLine.charAt(0) == '[' )
								break;
							int ieq = inLine.indexOf("=");
							String field = inLine.substring(0,ieq);
							String val = inLine.substring(ieq+1);
							switch(field) {
								case "RunDate" :
									setRunDate(val);
									break;
								case "SamplesPerGroup" :
									getRunConfig().setSamplesPerGroup(new Integer(val));
									break;
								case "ReadingsSpace":
								case "GroupsSpace":
								case "NumGroups":
									getRunConfig().setNumGroups(new Integer(val));
									break;
								case "Baseline":
									getRunConfig().setBaseline(new Float(val));
									break;
								case "UpperDeviation":
									getRunConfig().setUpperDeviation(new Float(val));
									break;
								case "LowerDeviation":
									getRunConfig().setLowerDeviation(new Float(val));
									break;
								case "UnitsOfMeasure":
									setUnitsOfMeasure(val);
									break;
								case "BeginIgnore":
									((DiscreteRunConfig)getRunConfig()).setBeginIgnore(new Integer(val));
									break;
								case "EndIgnore":
									((DiscreteRunConfig)getRunConfig()).setEndIgnore(new Integer(val));
									break;
								default:
									break;
							}
				        //setCutoff(new Float(IniFile.getIniFileString(ht, section, "Cutoff")));
				        //beginIgnore = new Integer(IniFile.getIniFileString(ht, section, "BeginIgnore")).intValue();
				        //endIgnore = new Integer(IniFile.getIniFileString(ht, section, "EndIgnore")).intValue();
				        //upperReject = new Float(IniFile.getIniFileString(ht, section, "UpperReject"));
				        //lowerReject = new Float(IniFile.getIniFileString(ht, section, "LowerReject"));
						} //while
						break; // case settings
					case "FIELDS":
						while( (inLine = ifm.getDataPort().readLine()) != null ) {
							//if( Props.DEBUG ) System.out.println("Fields iter");
							int trimLine = inLine.trim().length();
							if( trimLine == 0 || inLine.charAt(0) == '[' )
								break;
							int ieq = inLine.indexOf("=");
							String field = inLine.substring(0,ieq);
							int ival = ieq+1;
							String val = "";
							if( ival != trimLine )
								val = inLine.substring(ival);
							switch(field) {
								case "Fld0Label" :
									getRunConfig().setReportHeading1(val);
									break;
								case "Fld0Val" :
									getRunConfig().setReportValue1(val);
									break;
								case "Fld1Label" :
									getRunConfig().setReportHeading2(val);
									break;
								case "Fld1Val" :
									getRunConfig().setReportValue2(val);
									break;
								default:
									break;
							}
						}
						break;
					default:
						if( setting.startsWith("GROUP") ) {
							isRun = false;
							return (new Integer(setting.substring(5)).intValue());
						}
				} // switch SETTINGS
			} // if charAt(0) = [
		} // while inLine
		return -1;
	}

}
