package com.microcaliperdevices.saje.run;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.microcaliperdevices.saje.MachineNotReadyException;
import com.microcaliperdevices.saje.NotSupportedException;
import com.microcaliperdevices.saje.Props;
import com.microcaliperdevices.saje.CoreContainer;
import com.microcaliperdevices.saje.SetRuntypeUp;
import com.microcaliperdevices.saje.ThreadPoolManager;
import com.microcaliperdevices.saje.config.AbstractConfigFactory;
import com.microcaliperdevices.saje.config.AbstractRunConfig;
import com.microcaliperdevices.saje.config.ContinuousRunConfig;
import com.microcaliperdevices.saje.history.AbstractRunHistoryEntry;
import com.microcaliperdevices.saje.history.ContinuousRunHistoryEntry;
import com.microcaliperdevices.saje.io.file.DirectoryNotFoundException;
import com.microcaliperdevices.saje.io.machine.MachineInterface;
import com.microcaliperdevices.saje.io.machine.bridge.MachineBridge;
import com.microcaliperdevices.saje.io.machine.bridge.MachineReading;
import com.microcaliperdevices.saje.license.License;
import com.microcaliperdevices.saje.license.LicenseFailException;

	/**
	 * Perform a run treating results as continuous dataset
	 * @author jg
	 *
	 */
    public class ContinuousRun extends AbstractRun
    {
        private ContinuousRunHistoryEntry[] histe;
    	  /**
         * Calculate Average using safeDivide
         * startIndex, endIndex define the location of the data elements for the particular group
         * as constructed by nuildOffsetMatrix
         * endIndex is the last element of the group, or size-1
         * resultIndex is the group array for the run position for the result to be placed
         * @param dblData
         * @return
         */
        public void calcAverage(List<MachineReading> dblData, int startIndex, int endIndex, int resultIndex)
        {
       		double dataTotal = 0;
        	synchronized(dblData) {
         		if (dblData.size() == 0) {
         			if( resultIndex == 0 )
         				histe[histe.length-1].setAverage("0");
         			else
         				histe[resultIndex-1].setAverage("0");
         			return;
         		}
        		for (int i = startIndex; i < endIndex; i++)
        		{
        			dataTotal += dblData.get(i).getReadingValDouble();
        		}
        		if( resultIndex == 0 )
        			histe[histe.length-1].setAverage( String.valueOf(safeDivide(dataTotal, dblData.size())));
        		else
        			histe[resultIndex-1].setAverage( String.valueOf(safeDivide(dataTotal, dblData.size())));
        	}
        }

        /**
         * Calculate standard deviation
         * startIndex, endIndex define the location of the data elements for the particular group
         * as constructed by buildOffsetMatrix
         * endIndex is the last element of the group, or size-1
         * @param dblData
         * @return
         */
        public void calcStandardDeviation(List<MachineReading> dblData, int startIndex, int endIndex, int resultIndex)
        {
            double dblDataAverage = 0;
            double totalVariance = 0;
        	synchronized(dblData) {
        		if (dblData.size() == 0) {
          			if( resultIndex == 0 ) {
         				histe[histe.length-1].setAverage("0");
          				histe[histe.length-1].setStandardDeviation("0");
          			} else {
           				histe[resultIndex-1].setAverage("0");
        				histe[resultIndex-1].setStandardDeviation("0");
          			}
          			return;
        		}
        		calcAverage(dblData, startIndex, endIndex, resultIndex);
        		dblDataAverage = Double.valueOf(getAverage(resultIndex));
        		for (int i = startIndex; i < endIndex; i++)
        		{
        			totalVariance += Math.pow(dblData.get(i).getReadingValDouble() - dblDataAverage, 2);
        		}
        		setStandardDeviation(resultIndex, Math.sqrt(safeDivide(totalVariance, endIndex - startIndex)));
        	}
        }
    	public String getAverage() {
    		if( activeGroupNum == 0 )
    			activeGroupNum = 1;
    		return histe[activeGroupNum-1].getAverage();
    	}
    	public String getAverage(int group) {
    		if( group == 0 )
    			return histe[histe.length-1].getAverage();
    		return histe[group-1].getAverage();
    	}
    	public void setAverage(double avg) {
    		if( activeGroupNum == 0 )
    			activeGroupNum = 1;
    		histe[activeGroupNum-1].setAverage(String.valueOf(avg));
    	}
    	public void setAverage(int group, double avg) {
    		if( group == 0 )
    			histe[histe.length-1].setAverage(String.valueOf(avg));
    		else
    			histe[group-1].setAverage(String.valueOf(avg));
    	}
    	public String getStandardDeviation() {
    		if( activeGroupNum == 0 )
    			activeGroupNum = 1;
    		return histe[activeGroupNum-1].getStandardDeviation();
    	}
    	public void setStandardDeviation(double double1) {
    		if( activeGroupNum == 0 )
    			activeGroupNum = 1;
    		histe[activeGroupNum-1].setStandardDeviation(String.valueOf(double1));
    	}
    	public void setStandardDeviation(int group, double double1) {
    		if( group == 0 )
    			histe[histe.length-1].setStandardDeviation(String.valueOf(double1));
    		else
    			histe[group-1].setStandardDeviation(String.valueOf(double1));			
    	}

		public String getSampleLength() {
			if( activeGroupNum == 0 )
				activeGroupNum = 1;
			return histe[activeGroupNum].getSampleLength();
		}
		
		public String getSampleLength(int i) {
			return histe[i].getSampleLength();
		}
		public void setSampleLength(Float sampleLength) {
			if( activeGroupNum == 0 )
				activeGroupNum = 1;
			histe[activeGroupNum].setSampleLength(String.valueOf(sampleLength));		
		}
		public void setSampleLength(float sampleLength, int resultIndex) {
			histe[resultIndex].setSampleLength(String.valueOf(sampleLength));
		}
		
		/**
		 * 
		 * @param lic
		 * @throws IOException
		 * @throws DirectoryNotFoundException
		 */
		public ContinuousRun(License lic, AbstractRunConfig arc) throws IOException, DirectoryNotFoundException
        {
			super(lic, arc);
        	setRunConfig(arc);
        	SetRuntypeUp.setDataSource("Machine");
	        activeGroupNum = 1;
        }

        /**
         * Save the name of the last run opened
         * @param runDate
         * @param Smoothness
         * @return
         * @throws IOException
         */
        public void updateRunState(String runDate, String Smoothness) throws IOException
        {
            this.setRunDate(runDate);
            //this.setMicroDeviation(new Float(Smoothness));
  
            //update currentRun
            //setLastRunDate(f.format(new Date()));
            setSampleLength(
                            new Float( (runConfig.getSamplesPerGroup() * 
                            		((ContinuousRunConfig)runConfig).getSampleSpacing()) + 
                            		((ContinuousRunConfig)runConfig).getGroupSpacing()), activeGroupNum);
 
        }
   
    	/**
		 * Called whenever postprocessing is needed, typically after getting signal_end from MachineBridge
		 * MachineInterface also implements for call thru this instance
		 */
		public void postProcess() throws Exception {
			MachineBridge mb = MachineBridge.getInstance("dataset");
			int[][] matx = buildOffsetMatrix(mb.get());
			int groups = runConfig.getNumGroups();
			if( groups > 1 ) {
				for(int i = 0; i < groups; i++) {
					calcStandardDeviation(mb.get(), matx[0][i], matx[1][i], i+1);
					CoreContainer.getHistory().updateHistoryRow((AbstractRun)this, (AbstractRunHistoryEntry)histe[i], i+1);
				}
			}
			// use entry 0, totals, to calculate total for all groups as offset matrix has beg,end in last entry
			calcStandardDeviation(mb.get(), matx[0][groups], matx[1][groups], 0);
			// use entry 0, totals, to calculate total for all groups as offset matrix has beg,end in last entry
			if( groups == 1 )
				CoreContainer.getHistory().updateHistoryRow((AbstractRun)this, (AbstractRunHistoryEntry)histe[0], 1);
			else
				CoreContainer.getHistory().updateHistoryRow((AbstractRun)this,(AbstractRunHistoryEntry) histe[groups], groups);
		}
        
	    protected void setRunConfig(AbstractRunConfig arc) throws IOException {    	
	       	this.runConfig = arc;
	    	if( arc.getNumGroups() > 1 ) { // total if > 1
	    		histe = new ContinuousRunHistoryEntry[arc.getNumGroups()+1];
	    		for(int i = 0; i <= arc.getNumGroups(); i++) {
	    			histe[i] = (ContinuousRunHistoryEntry)CoreContainer.getHistory().addHistoryRow(this);
	    		}
	    	} else {
	    		histe = new ContinuousRunHistoryEntry[arc.getNumGroups()];
	    		histe[0] = (ContinuousRunHistoryEntry)CoreContainer.getHistory().addHistoryRow(this);
	    	}
	    }
	    
        public String[][] setDisplay()
        {
        	String[][] disPout = new String[14][2];
            disPout[0][0] = "Notes";
            disPout[0][1] = "";

            disPout[4][0] = "Sample spacing";
            disPout[4][1] = String.valueOf(((ContinuousRunConfig)runConfig).getSampleSpacing());

            disPout[5][0] = "Groups @ spacing";
            disPout[5][1] = String.valueOf(runConfig.getNumGroups()) + "@" + String.valueOf(((ContinuousRunConfig)runConfig).getGroupSpacing());
              
            disPout[6][0] = "Mean";
            disPout[6][1] = String.valueOf(getAverage());
            
            disPout[7][0] = "Standard Deviation";
            disPout[7][1] = String.valueOf(getStandardDeviation());

            disPout[9][0] = "Run Date";
            disPout[9][1] = getRunDate();

            disPout[12][0] = "Group";
            disPout[12][1] = String.valueOf(getActiveGroupNum());

            disPout[13][0] = "File Name";
            disPout[13][1] = getFileName();
            
            return disPout;
        }

		public static void main(String[] args) throws IOException, LicenseFailException, NotSupportedException, DirectoryNotFoundException {
			License lic = new License();
			AbstractRunFactory factory = AbstractRunFactory.createFactory(args[0],"Machine");
			//AbstractRunFactory factory = AbstractRunFactory.createFactory(machineType);
			AbstractConfigFactory acf = AbstractConfigFactory.createFactory(args[1]);
			AbstractRunConfig arc = (acf.createConfig(lic));
			AbstractRun currentRun = factory.createRun(lic, arc); // start in default location otherwise like "todaysrun/"
			MachineInterface machine = factory.createMachine(currentRun);
			//boolean rtn = machine.retrieveMachineData(currentRun);
			//MachineBridge.getInstance().init();
			//
			//Thread t = new Thread(machine);
			//t.setDaemon(true);
			//t.start();
		    //DaemonThreadFactory dtf = ((ContinuousRun)currentRun).new DaemonThreadFactory();
		    //ExecutorService executor = Executors.newSingleThreadExecutor(dtf);
	        //MachineBridge.getInstance().init();
	        ThreadPoolManager.getInstance().spin(machine, "dataset");
	        //executor.execute(machine);
	        //
			MachineReading mr;
			AtomicInteger getVal = new AtomicInteger();
			while(true) {
        		mr = MachineBridge.getInstance("dataset").waitForNewReading(getVal);	
        		if( mr == null )
        			break;
        		if( Props.DEBUG ) System.out.println(getVal.get()+"="+mr);
			}
			//if( Props.DEBUG ) System.out.println("Units="+machine.getUnitsOfMeasure());
			if( Props.DEBUG ) System.out.println("Exited main read loop");
			if( MachineBridge.getInstance("dataset").size() == 0 ) {
				if( Props.DEBUG ) System.out.println(new MachineNotReadyException().getMessage());
			}
			//double[][] data = machine.getRawData();
			//for(int i = 0; i < data.length; i++) { // groups
			//	for(int j = 0; j < data[i].length; j++) { // reading seq
			//		if( Props.DEBUG ) System.out.println("Group="+i+" Reading Seq.="+j+" Reading Num="+data[i][j]+" Value="+data[i][j]);
			//	}
			//}
		}
	

    }
