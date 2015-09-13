package com.microcaliperdevices.saje.io.machine;

import java.io.IOException;
import java.util.Date;

import com.microcaliperdevices.saje.MachineNotReadyException;
import com.microcaliperdevices.saje.Props;
import com.microcaliperdevices.saje.ThreadPoolManager;
import com.microcaliperdevices.saje.config.ContinuousRunConfig;
import com.microcaliperdevices.saje.io.machine.bridge.MachineBridge;
import com.microcaliperdevices.saje.io.machine.bridge.MachineReading;
import com.microcaliperdevices.saje.io.machine.dataport.ByteSerialDataPort;
import com.microcaliperdevices.saje.run.ContinuousRun;

	/**
     *     
	 * @author jg
	 * Copyright 2012,2014 Microcaliper Devices, LLC
	 */
    public class ContinuousMachine extends AbstractMachine implements ContinuousInterface
    {
        private ContinuousRun currentRun;
	    private int group = 0;
	    
	    public ContinuousMachine(ContinuousRun currentRun) throws IOException
	    {
	    	//if( Props.DEBUG ) System.out.println("Construct ContinuousMachine");
	    	this.currentRun = currentRun;
	    	setDataPort(ByteSerialDataPort.getInstance());

	    }
        
	    /**
	     * Test to see if comm ready: open port, write our custom M700. Read line via asynch reader, readLine
	     * waits until termination char encountered. Machine should respond with current status
	     * @throws IOException 
	     *  
	     */
	    public void waitReady() throws IOException
	    {
	    	//if( Props.DEBUG ) System.out.println("waitReady");
	    	//test if Communication port open, on timeout throw exception
	        write("M700");
	        do {
	            	String s = ((ByteSerialDataPort) getDataPort()).readLine();
	            	System.out.println(s);
	        } while(!((ByteSerialDataPort) getDataPort()).isEOT());
	    }
	    

	    /**
	     * Get all the data from the 210R and put it into the MachineBridge
	     * @throws IOException 
	     * @throws MachineNotReadyException 
	     */
	    private boolean retrieveMachineData() throws IOException
	    {
            currentRun.setRunDate(DiscreteMachine.f.format(new Date()));
            currentRun.setActiveGroupNum(1);
 
		    int stepsBetweenGroups = (int)((ContinuousRunConfig)currentRun.getRunConfig()).getGroupSpacing();
		    int stepsBetweenSamples = (int)((ContinuousRunConfig)currentRun.getRunConfig()).getSampleSpacing();

	          	//if( Props.DEBUG ) System.out.println("Groups="+currentRun.getNumGroups()+" Samples per group="+currentRun.getSamplesPerGroup());
				for (group = 0; group < currentRun.getRunConfig().getNumGroups(); group++) {
				    currentRun.setActiveGroupNum(group+1);
				    //signal machine for each group
				    write("M802 P62 S" + currentRun.getRunConfig().getSamplesPerGroup() + " M" + stepsBetweenSamples + "\r");
				    //get the readings for this group
				    //"Retrieving Group " + group + " data.";
				    ThreadPoolManager.getInstance().waitGroup("dataset");
				    // send dwell after each group
				    if( group+1 < currentRun.getRunConfig().getNumGroups() ) {
				    	write("G04 M"+stepsBetweenGroups+"\r");
				    }
				}
			return true;
	    }
		
	    public void postProcess() throws Exception {
	    	currentRun.postProcess();
	    }
	    
	    public String stringSettings()
	    {
	            String msg = "Machine\n";
	            msg = msg + getMachineType() + "\n";
	            msg = msg + getDataPort().stringSettings();
	            return msg;
	    }
	    
		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				retrieveMachineData();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

    }