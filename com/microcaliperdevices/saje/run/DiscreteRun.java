package com.microcaliperdevices.saje.run;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

import com.microcaliperdevices.saje.LicenseDown;
import com.microcaliperdevices.saje.Props;
import com.microcaliperdevices.saje.CoreContainer;
import com.microcaliperdevices.saje.SetRuntypeUp;
import com.microcaliperdevices.saje.ThreadPoolManager;
import com.microcaliperdevices.saje.config.AbstractConfigFactory;
import com.microcaliperdevices.saje.config.AbstractRunConfig;
import com.microcaliperdevices.saje.history.AbstractRunHistory;
import com.microcaliperdevices.saje.history.AbstractRunHistoryEntry;
import com.microcaliperdevices.saje.history.ContinuousRunHistoryEntry;
import com.microcaliperdevices.saje.history.DiscreteRunHistory;
import com.microcaliperdevices.saje.history.DiscreteRunHistoryEntry;
import com.microcaliperdevices.saje.io.file.DirectoryNotFoundException;
import com.microcaliperdevices.saje.io.machine.MachineInterface;
import com.microcaliperdevices.saje.io.machine.bridge.MachineBridge;
import com.microcaliperdevices.saje.io.machine.bridge.MachineReading;
import com.microcaliperdevices.saje.license.License;

 
/**
 * Represents a 'Run'
 * Definitions: Number of steps = speed at which sampling progresses
 * speed is in terms of steps per inch.
 * The space between readings is distance - in inches
 * number of steps determine speed
 * steps are fixed and a value around 1000 steps per inch is nominal.
 * A normal run (sample size) is 500 readings at .01 inches per reading.
 * Copyright 2012 Microcaliper Devices, LLC
 * @author jg
 *
 */
    public class DiscreteRun extends AbstractRun
    {
        private DiscreteRunHistoryEntry[] histe;
		//instance variables
  
        static DateFormat f = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH);
        
        /**
         * Construct the sa.ini file if not existing, calls createIniFile.
         * @param lic license instance
         * @param filePath
         * @throws IOException
         * @throws DirectoryNotFoundException
         */
        public DiscreteRun(License lic, AbstractRunConfig arc) throws IOException, DirectoryNotFoundException
        {
        	super(lic, arc);
        	setRunConfig(arc);
        	SetRuntypeUp.setDataSource("Machine");
           	activeGroupNum = 1;
        }

		   /**
	     * This is important because it sets up history arrays based on config
	     * @param arc
	     * @throws IOException 
	     */
	    protected void setRunConfig(AbstractRunConfig arc) throws IOException {
	    	this.runConfig = arc;
	    	if( arc.getNumGroups() > 1 ) { // total if > 1
	    		histe = new DiscreteRunHistoryEntry[arc.getNumGroups()+1];
	    		for(int i = 0; i <= arc.getNumGroups(); i++) {
	    			histe[i] = (DiscreteRunHistoryEntry)CoreContainer.getHistory().addHistoryRow(this);
	    		}
	    	} else {
	    		histe = new DiscreteRunHistoryEntry[arc.getNumGroups()];
	    		histe[0] = (DiscreteRunHistoryEntry)CoreContainer.getHistory().addHistoryRow(this);
	    	}
	    	
	    }
	    
		/**
		 * Called whenever postprocessing is needed, typically after getting signal_end from MachineBridge
		 * MachineInterface also implements for call thru this instance
		 */
		public void postProcess() throws Exception {
			//int[][] matx = buildOffsetMatrix(MachineBridge.getInstance().get());
			int groups = runConfig.getNumGroups();
			if( groups > 1 ) {
				for(int i = 0; i < groups; i++) {
					CoreContainer.getHistory().updateHistoryRow(this, histe[i], i+1);
				}
			}
			if( groups == 1 )
				CoreContainer.getHistory().updateHistoryRow(this, histe[0], 1);
			else
				CoreContainer.getHistory().updateHistoryRow(this, histe[groups], groups);
				
		}
        
        protected int findField(String target, String[][] dtRow) {
        	for(int j = 0; j < dtRow.length; j++) {
        		if( dtRow[j][0].equals(target)) return j;
        	}
        	return -1;
        }

        protected int findField(String target, List<String> dtRow) {
        	for(int j = 0; j < dtRow.size(); j++) {
        		if( dtRow.get(j).equals(target)) return j;
        	}
        	return -1;
        }
 
        
        public static void main(String[] args) throws Exception {
            License lic = new License();
 
            AbstractRunFactory factory = AbstractRunFactory.createFactory(args[0],"File");
            //AbstractRunFactory factory = AbstractRunFactory.createFactory(machineType);
			AbstractConfigFactory acf = AbstractConfigFactory.createFactory(args[1]);
			AbstractRunConfig arc = (acf.createConfig(lic));
            DiscreteRun currentRun = (DiscreteRun) factory.createRun(lic, arc); // start in default location otherwise like "todaysrun/"
            MachineInterface machine = factory.createMachine(currentRun);
            //boolean rtn = machine.retrieveMachineData(currentRun);
            //MachineBridge.getInstance().init();
            //Thread t = new Thread(machine);
            //t.setDaemon(true);
            //t.start();
            ThreadPoolManager.getInstance().spin(machine, "dataset");
            MachineReading mr;
            AtomicInteger getVal = new AtomicInteger();
            while(true) {
            		mr = MachineBridge.getInstance("dataset").waitForNewReading(getVal);	
            		if( mr == null )
            			break;
            		if( Props.DEBUG ) System.out.println(getVal.get()+"="+mr);
            }
            if( Props.DEBUG ) System.out.println("Units="+machine.getUnitsOfMeasure());
            if( Props.DEBUG ) System.out.println("Exited main read loop");
            //double[][] data = machine.getRawData();
            //for(int i = 0; i < data.length; i++) { // groups
            //	for(int j = 0; j < data[i].length; j++) { // reading seq
            //		if( Props.DEBUG ) System.out.println("Group="+i+" Reading Seq.="+j+" Reading Num="+data[i][j]+" Value="+data[i][j]);
            //	}
            //}
        }



    }