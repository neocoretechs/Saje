package com.microcaliperdevices.saje.io.machine;

import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import com.microcaliperdevices.saje.Props;
import com.microcaliperdevices.saje.ThreadPoolManager;
import com.microcaliperdevices.saje.config.ContinuousRunConfig;
import com.microcaliperdevices.saje.io.machine.bridge.MachineBridge;
import com.microcaliperdevices.saje.io.machine.bridge.MachineReading;
import com.microcaliperdevices.saje.io.machine.dataport.ByteSerialDataPort;
import com.microcaliperdevices.saje.run.DiscreteRun;

	/**
	 * Gets the data from the serial port of the machine which represent readings from the requested operation.
	 * This class and its relatives provide the linkage between the 'run' and the 'device'
	 * @author jg
	 * Copyright 2012,2014 Microcaliper Devices, LLC
	 */
    public class DiscreteMachine extends AbstractMachine implements Runnable
    {
        public static DateFormat f = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH);
    	private DiscreteRun currentRun;

		/**
		 * 
		 * @param currentRun
		 * @throws IOException 
		 */
        public DiscreteMachine(DiscreteRun currentRun) throws IOException {
        	this.currentRun = currentRun;
            setDataPort(ByteSerialDataPort.getInstance());
            if( Props.DEBUG ) System.out.println("initPort DataPort set "+stringSettings());
        }
 
        
    	/**
    	 * Get all the data from the machine and put it into the MachineBridge
         *
    	 */
        private boolean retrieveMachineData() throws IOException, UnsupportedCommOperationException
        {
        	//boolean isSetUnits = false; // each run should check for units switch flip
            currentRun.setRunDate(f.format(new Date()));
            currentRun.setActiveGroupNum(1);
		    int stepsBetweenGroups = (int)((ContinuousRunConfig)currentRun.getRunConfig()).getGroupSpacing();
		    int stepsBetweenSamples = (int)((ContinuousRunConfig)currentRun.getRunConfig()).getSampleSpacing();
        	try {
            	String readLine = "";
    		    ((ByteSerialDataPort) getDataPort()).clear();
            	//boolean isSetUnits = (getUnitsOfMeasure().length() > 0);
            	//if( Props.DEBUG ) System.out.println("Groups="+currentRun.getNumGroups()+" Samples per group="+currentRun.getSamplesPerGroup());
            	for(int inumGrps = 0; inumGrps < currentRun.getRunConfig().getNumGroups() ; inumGrps++) {
				    currentRun.setActiveGroupNum(inumGrps+1);
				    //signal machine for each group
				    write("M802 P62 S" + currentRun.getRunConfig().getSamplesPerGroup() + " M" + stepsBetweenSamples + "\r");
            		//for(int jnumSample = 0; jnumSample < currentRun.getRunConfig().getSamplesPerGroup(); jnumSample++) {
            		//	readLine = getDataPort().readLine();
            			//if( Props.DEBUG ) System.out.println(readLine);
               		//	MachineReading mr = new MachineReading(inumGrps+1, jnumSample, getReadingNumber(readLine), getReadingValueDouble(readLine));
               		//	MachineBridge.getInstance().add(mr);		
            		//}
				    // wait for <dataset> to stream back asynch
				    ThreadPoolManager.getInstance().waitGroup("dataset");
				    // send dwell after each group
				    if( inumGrps+1 < currentRun.getRunConfig().getNumGroups() ) {
				    	write("G04 M"+stepsBetweenGroups+"\r");
				    }
            	}
        	} finally {
            	//MachineBridge.getInstance().signalEnd();
        		//Close();
        	}
        	return true;
        }

  
        
    	/* (non-Javadoc)
		 * @see com.microcaliperdevices.saje.io.machine.MachineInterface#postProcess()
		 */
		@Override
		public void postProcess() throws Exception {
			currentRun.postProcess();
		}

		/* (non-Javadoc)
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				retrieveMachineData();
			} catch (IOException | UnsupportedCommOperationException e) {
			}
		}
	
    }
