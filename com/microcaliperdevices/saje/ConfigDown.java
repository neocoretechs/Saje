/**
 * 
 */
package com.microcaliperdevices.saje;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import com.microcaliperdevices.saje.CoreContainer.ContainerProcessInterface;
import com.microcaliperdevices.saje.config.AbstractConfigFactory;
import com.microcaliperdevices.saje.config.AbstractRunConfig;
import com.microcaliperdevices.saje.io.file.DirectoryNotFoundException;
import com.microcaliperdevices.saje.io.file.FileIOUtilities;


/**
 * @author jg
 * RunDataUp - 4447, RunDataDown - 4448, LicenseDown - 4451, 
 * HistoryUp - 4445, HistoryDown - 4446, ConfigUp - 4449, ConfigDown - 4450
 * ReadMachineData - 4440
 *
 */
public final class ConfigDown implements ContainerProcessInterface {
	private static ConfigDown instance=null;
	private ConfigDown() {}
	public static ConfigDown getInstance() {
		if( instance == null ) {
			instance = new ConfigDown();
			  try {
					// no config file found so set config to base
					AbstractConfigFactory acf = AbstractConfigFactory.createFactory(SetRuntypeUp.getRuntype());
					CoreContainer.setCurrentConfig(acf.createConfig(LicenseDown.getInstance().getLicense()));
			  } catch (IOException | NotSupportedException | DirectoryNotFoundException e) {
				e.printStackTrace();
			  }
		}
		return instance;
	}
	/* (non-Javadoc)
	 * @see com.microcaliperdevices.saje.ContainerProcessInterface#processAndRespond(byte[], java.io.OutputStream)
	 */
	@Override
	public void processAndRespond(Socket os) throws Exception {
		if( Props.DEBUG ) System.out.println("Configuration:"+CoreContainer.getCurrentConfig().toXml());
		CoreContainer.putDataStream(os, CoreContainer.getCurrentConfig().toXml().getBytes());
	}
	
	/* (non-Javadoc)
	 * @see com.microcaliperdevices.saje.ContainerProcessInterface#getPort()
	 */
	public static int getPort() {
		return 4450;
	}
	
	public static void main(String[] args) throws Exception {
		CoreContainer.setConfigFromXml("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?><runconfig><User_Report_Heading_1>time</User_Report_Heading_1><User_Report_Heading_2>operator</User_Report_Heading_2><User_Report_Value_1>10:37:15</User_Report_Value_1><User_Report_Value_2></User_Report_Value_2><Samples_per_Group>500</Samples_per_Group><Baseline_Measurement>0.25</Baseline_Measurement><Number_of_Groups>2</Number_of_Groups><Upper_Deviation>1.0</Upper_Deviation><Upper_Rejection>1.0</Upper_Rejection><Lower_Deviation>-1.0</Lower_Deviation><Lower_Rejection>-10.0</Lower_Rejection><Begin_Ignore>100</Begin_Ignore><Cutoff>0.35</Cutoff><End_Ignore>300</End_Ignore></runconfig>");
		AbstractRunConfig arc = CoreContainer.getCurrentConfig();
		if( Props.DEBUG ) System.out.println(arc.toXml());
	}

}
