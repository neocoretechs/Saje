/**
 * 
 */
package com.microcaliperdevices.saje.config;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

import javax.xml.bind.annotation.XmlTransient;

import com.microcaliperdevices.saje.ConfigDown;
import com.microcaliperdevices.saje.Props;
import com.microcaliperdevices.saje.io.file.DirectoryNotFoundException;
import com.microcaliperdevices.saje.io.file.FileIOUtilities;
import com.microcaliperdevices.saje.io.file.IniFile;
import com.microcaliperdevices.saje.license.License;

/**
 * Support a number of possible connected 'machines' each enumerated by a [name] category header in
 * config file. such things as port name and baud rate enumerated
 * @author jg
 *
 */
public class BaseMachineConfig {
	private String port;
	private int[] portSettings;
    private String machineType;
    Hashtable ht; // holds categories
	private static BaseMachineConfig instance=null;
	private BaseMachineConfig() {}
	public static BaseMachineConfig getInstance() {
		if( instance == null ) {
			instance = new BaseMachineConfig();
		}
		return instance;
	}
	@XmlTransient
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	@XmlTransient
	public int[] getPortSettings() {
		return portSettings;
	}
	public void setPortSettings(int[] portSettings) {
		this.portSettings = portSettings;
	}
	@XmlTransient
	public String getMachineType() {
		return machineType;
	}
	public void setMachineType(String machineType) {
		this.machineType = machineType;
	}
	public int getBaudRate() {
		return portSettings[0];
	}
	public int getDataBits() {
		return portSettings[1];
	}
	public int getParityBits() {
		return portSettings[2];
	}
	public int getStopBits() {
		return portSettings[3];
	}
	 /**
     * Loads up the basic machine config.; MachineType, Model, Port, Port Settings.
     * Sets up the hashtable with ini file categories
     * @return
     * @throws IOException
     * @throws DirectoryNotFoundException
     */
    public void getBaseMachineConfig(License lic) throws IOException, DirectoryNotFoundException {
        String fileName = FileIOUtilities.dataDirectory + FileIOUtilities.portSettingsFile;
        File f = new File(fileName);
        if( !f.exists() )
        	FileIOUtilities.writePortSettings(lic);
        // Get the target machine from the license file
        String targetMach = lic.getFirstMachineType();      
        //set up instance variables from ini file and current settings
        ht = IniFile.getCategories(FileIOUtilities.portSettingsFile);
        List<String> categories = IniFile.getCategories(ht);
        for(int i = 0; i < categories.size(); i++) {
        	machineType = categories.get(i);
            if( Props.DEBUG ) System.out.println("Base Machine Config "+targetMach+" Machine="+machineType);
        	if( machineType.equals(targetMach) )
        		break;
        }
        if( !machineType.equals(targetMach) )
        	throw new IOException("Unknown machine type "+machineType+" in configuration");          
        port = IniFile.getIniFileString(ht, machineType, "Port");
        portSettings = parsePortSettings(IniFile.getIniFileString(ht, machineType, "PortSettings"));
    }
	/**
     * Pass the comma sep list of port params, assume order: baud, parity(n,1,2), databits, stopbits
     * @param iniFileString
     * @return Integer array of baud,parity,data,stop
     */
    private int[] parsePortSettings(String iniFileString) {
    	String[] sets = iniFileString.split("[,]");
    	int[] retArray = new int[sets.length];
    	for(int i = 0; i < sets.length; i++) {
    		if( sets[i].equals("n") )
    			sets[i] = "0";
    		retArray[i] = Integer.valueOf(sets[i]).intValue();
    	}
    	return retArray;
	}

}
