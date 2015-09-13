package com.microcaliperdevices.saje.run;

import java.io.IOException;

import com.microcaliperdevices.saje.SetRuntypeUp;
import com.microcaliperdevices.saje.config.AbstractRunConfig;
import com.microcaliperdevices.saje.config.ContinuousRunConfig;
import com.microcaliperdevices.saje.io.file.DirectoryNotFoundException;
import com.microcaliperdevices.saje.io.machine.AbstractMachine;
import com.microcaliperdevices.saje.io.machine.PreProcessInterface;
import com.microcaliperdevices.saje.license.License;

public class ContinuousStreamRun extends ContinuousRun implements PreProcessInterface {

	/**
	 * @param lic
	 * @throws IOException
	 * @throws DirectoryNotFoundException
	 */
	public ContinuousStreamRun(License lic, AbstractRunConfig arc) throws IOException, DirectoryNotFoundException {
		super(lic, arc);
		// mod machine type to reflect file use
		SetRuntypeUp.setDataSource("Stream");
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
							String field = inLine.substring(0,inLine.indexOf("="));
							String val = inLine.substring(inLine.indexOf("=")+1);
							switch(field) {
								case "RunDate" :
									setRunDate(val);
									break;
								case "SamplesPerGroup" :
									getRunConfig().setSamplesPerGroup(new Integer(val));
									break;
								case "ReadingsSpace":
									((ContinuousRunConfig)getRunConfig()).setSampleSpacing(new Float(val));
									break;
								case "GroupsSpace":
									((ContinuousRunConfig)getRunConfig()).setGroupSpacing(new Float(val));
									break;
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
