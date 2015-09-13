package com.microcaliperdevices.saje.io.machine;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.microcaliperdevices.saje.Props;
import com.microcaliperdevices.saje.io.machine.AbstractMachine;
import com.microcaliperdevices.saje.io.machine.bridge.MachineBridge;
import com.microcaliperdevices.saje.io.machine.bridge.MachineReading;
import com.microcaliperdevices.saje.io.machine.dataport.StreamDataPort;
import com.microcaliperdevices.saje.run.AbstractRun;

public class StreamMachine extends AbstractMachine {
	protected AbstractRun currentRun;
	
	public StreamMachine(AbstractRun currentRun) {
		this.currentRun = currentRun;
        setMachineType("Stream");
	}
	
	public StreamMachine(AbstractRun currentRun, InputStream in, OutputStream out) {
		this.currentRun = currentRun;
        setDataPort(new StreamDataPort(in, out));
        setMachineType("Stream");
	}
	
	public void setDataPort(InputStream in, OutputStream out) {
	    setDataPort(new StreamDataPort(in, out));
	}
	
	/* (non-Javadoc)
	 * @see com.microcaliperdevices.saje.io.machine.MachineInterface#postProcess()
	 */
	@Override
	public void postProcess() throws Exception {
		currentRun.postProcess();		
	}

	/* (non-Javadoc)
	 * @see com.microcaliperdevices.saje.io.machine.IO_machineInterface#stringSettings()
	 */
	@Override
	public String stringSettings() {
        return getDataPort().stringSettings();
	}

	/**
	 * Retrieve the machine data from specified file and place in MachineBridge
	 */
	protected boolean retrieveMachineData() throws IOException {
    	try {
        	String readLine = "";
        	int group = 1;
        	if( currentRun instanceof PreProcessInterface )
        		group = ((PreProcessInterface)currentRun).preProcess(this); // read to first group, leave file position
        	if( group == -1 )
        		throw new IOException("Premature and of .prn, never found a group");
        	if( Props.DEBUG ) System.out.println("Groups="+currentRun.getRunConfig().getNumGroups()+" Samples per group="+currentRun.getRunConfig().getSamplesPerGroup());
        	MachineBridge mb = MachineBridge.getInstance("dataset");
        	for(int inumGrps = 0; inumGrps < currentRun.getRunConfig().getNumGroups() ; inumGrps++) {
        		for(int jnumSample = 0; jnumSample < currentRun.getRunConfig().getSamplesPerGroup(); ) {
        			readLine = getDataPort().readLine();
        			if( Props.DEBUG ) System.out.println("StreamMachine retrieveMachineData "+readLine);
        			if( readLine == null ) {
        	        	mb.signalEnd();
        	        	return false;
        			}
        			if( readLine.length() == 0 )
        				continue;
        			if( readLine.charAt(0) == '[') {
        				String setting = readLine.substring(1, readLine.length() - 1);
        				//if( Props.DEBUG ) System.out.println("Setting "+setting);
        				if( setting.startsWith("GROUP")) {
        					group = (new Integer(setting.substring(5)).intValue());
        					continue; // new group
        				}
        			}
           			MachineReading mr = new MachineReading(group, jnumSample, getReadingNumber(readLine),getReadingValueDouble(readLine));
           			mb.add(mr);
           			jnumSample++;
        		}
        		//if( Props.DEBUG ) System.out.println("End sample loop");
        	}
        	mb.signalEnd();
    	} finally {
			if( Props.DEBUG ) System.out.println("StreamMachine retrieveMachineData read "+MachineBridge.getInstance("dataset").size());
    		close();
    	}
    	return true;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			retrieveMachineData();
		} catch (IOException e) {
		}	
	}

}
