/**
 * 
 */
package com.microcaliperdevices.saje.run;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.microcaliperdevices.saje.Props;
import com.microcaliperdevices.saje.config.AbstractRunConfig;
import com.microcaliperdevices.saje.config.ContinuousRunConfig;
import com.microcaliperdevices.saje.io.machine.bridge.MachineBridge;
import com.microcaliperdevices.saje.io.machine.bridge.MachineReading;
import com.microcaliperdevices.saje.license.License;

/**
 * @author jg
 *
 */
public abstract class AbstractRun {
    protected int activeGroupNum;
    protected String runDate;
    static DateFormat f = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH);
    private final String fileName ="sa.prn";
	protected License lic;
    protected AbstractRunConfig runConfig;
    //protected AbstractRunHistoryEntry[] history;
    
	public License getLicense() {
		return lic;
	}
	public String getFileName() {
		return fileName;
	}
	public int getActiveGroupNum() {
		return activeGroupNum;
	}
	public void setActiveGroupNum(int i) {
		activeGroupNum = i;		
	}
	
    public AbstractRunConfig getRunConfig() { return runConfig; }
    
    /**
     * c'tor here
     * @param lic
     * @param arc
     */
    public AbstractRun(License lic, AbstractRunConfig arc) {
    	this.lic = lic;
    	this.runConfig = arc;
    }
    /**
     * This is important because it sets up history arrays based on config
     * @param arc
     * @throws IOException 
     */
    protected abstract void setRunConfig(AbstractRunConfig arc) throws IOException;
 	public abstract void postProcess() throws Exception;
    
	public String getRunDate() {
		return runDate;
	}
	public void setRunDate(String runDate) {
		this.runDate = runDate;
	}
	/*
	public AbstractRunHistoryEntry[] getHistory() {
		return history;
	}
	public AbstractRunHistoryEntry getHistory(int i) {
		return history[i];
	}
	public void setHistory(AbstractRunHistoryEntry history) {
		if( activeGroupNum == 0 )
			activeGroupNum = 1;
		this.history[activeGroupNum] = history;
	}
	public void setHistory(AbstractRunHistoryEntry history, int i) {
		this.history[i] = history;
	}
	*/
 
    /**
     * Div accounting for div by 0
     * @param dbl1
     * @param dbl2
     * @return 0 if numerator or denominator 0
     */
    public static double safeDivide(double dbl1, double dbl2)
    {
        if ((dbl1 == 0) || (dbl2 == 0)) return 0;
        else return dbl1 / dbl2;
    }
    
    /**
     * Build a 2D offset matrix representing the start and end of the data elements in the data array returned from
     * the machine bridge. These elements will correspond to the array of values for each group by defining the array length i.e.
     * number of groups. NOTE: 1 additional element is added at the end for totals of all the groups UNLESS there is
     * but one group
     * @param mX
     * @return
     */
    public int[][] buildOffsetMatrix(List<MachineReading> mX) {	
    	int[][] retMx;
    	int startReadingNum = 0;
    	int prevGroupNum = 1;
    	int currentGroupIndex = 0;
    	int numGroups = runConfig.getNumGroups();
    	if( numGroups > 1) // 1 entry and total looks a bit sloppy if they are both the same so dont
    		retMx = new int[2][runConfig.getNumGroups()+1]; // total too
    	else
    		retMx = new int[2][2];
    	if( Props.DEBUG ) System.out.println("History array size:" + retMx[0].length);
    	retMx[0][0] = 0;
        synchronized(mX) {
        	if( numGroups > 1 ) {
        		for(int readingNumber = startReadingNum; readingNumber < mX.size()-1; readingNumber++) { // remember, theres a null at end of list
        			int currentGroupNum = mX.get(readingNumber).getRawGroup();
        			if( currentGroupNum != prevGroupNum) {
        				retMx[1][currentGroupIndex] = readingNumber-1; // fill in end of prev, we are at next after last in group
        				if( Props.DEBUG ) System.out.println("Group change: "+retMx[0][currentGroupIndex]+" "+retMx[1][currentGroupIndex]);
        				prevGroupNum = currentGroupNum; // set prev to curr
        				++currentGroupIndex;
        				retMx[0][currentGroupIndex] = readingNumber; // set next up with this
        			}
        		}
        		retMx[1][currentGroupIndex] = mX.size()-1; // last entry
        	}
        	
        	retMx[0][runConfig.getNumGroups()] = 0; // total start
        	retMx[1][runConfig.getNumGroups()] = mX.size()-1; // total end
        }
        if( Props.DEBUG ) System.out.println("Returning offset matrix:");
        for(int i = 0; i < retMx[0].length; i++)
        	if( Props.DEBUG ) System.out.println(retMx[0][i]+" "+retMx[1][i]);
        return retMx;
    }

    /**
     * Return double array of acquired data with indices [group][reading sequence] = reading value
     * @return
     */
	public double[] getRawData() {// [group][reading sequence] = reading value
		//double[] rawData = new double[getRunConfig().getNumGroups()*getRunConfig().getSamplesPerGroup()];
		MachineBridge mb = MachineBridge.getInstance("dataset");
		List<MachineReading> xM = mb.get();
		double[] rawData = new double[mb.size()];
		synchronized(xM) {
			for(int i = 0 ; i < mb.size(); i++) {
				MachineReading mr = xM.get(i);
				if( mr == null) break;
				//rawData[mr.getRawGroup()-1][mr.getRawSeq()] = mr.getReadingVal();
				rawData[i] = mr.getReadingValDouble();
			}
		}
		return rawData;	
	}
	
    /**
     *  Create run file if it doesn't exist.
     * @param fileName Write a file of this name, use dir path for sa.ini
     * @param machine Machine interface
     * @throws IOException
     */
    public String createRunFile(String machineType) throws IOException
    {
        int i = 0;
        //write settings section
        StringBuffer fw = new StringBuffer();
        fw.append("[" + machineType + "]\n");
        if( getRunDate() == null )
        	setRunDate(f.format(new Date()));
        fw.append("RunDate=" + getRunDate()+"\n");
        fw.append("SamplesPerGroup=" + runConfig.getSamplesPerGroup()+"\n");
        fw.append("ReadingsSpace=" + ((ContinuousRunConfig)runConfig).getSampleSpacing()+"\n");
        fw.append("NumGroups=" + runConfig.getNumGroups()+"\n");
        fw.append("GroupsSpace=" + ((ContinuousRunConfig)runConfig).getGroupSpacing()+"\n");
        fw.append("Baseline=" + runConfig.getBaseline()+"\n");
        fw.append("UpperDeviation="+runConfig.getUpperDeviation()+"\n");
        fw.append("LowerDeviation="+runConfig.getLowerDeviation()+"\n");
        fw.append("UpperReject="+runConfig.getUpperReject()+"\n");
        fw.append("LowerReject="+runConfig.getLowerReject()+"\n");
        fw.append("\n");
        //write fields section
        fw.append("[FIELDS]\n");
        fw.append("Fld0Label="+runConfig.getReportHeading1()+"\n");
        fw.append("Fld0Val=" + runConfig.getReportValue1()+"\n");
        fw.append("Fld1Label="+runConfig.getReportHeading2()+"\n");
        fw.append("Fld1Val=" + runConfig.getReportValue2()+"\n");
        fw.append("\n");
        //write data section
        List<MachineReading> xM = MachineBridge.getInstance("dataset").get();
        synchronized(xM) {
        	int gr = 1;//xM.get(0).getRawGroup();
            fw.append("[GROUP" + gr + "]\n");
        	for (i = 0; i < xM.size(); i++) {
        		if( xM.get(i) == null) break; // end of raw list
        		if( xM.get(i).getRawGroup() != gr) {
        			gr = xM.get(i).getRawGroup();
                    fw.append("[GROUP" + gr + "]\n");
        		}
        		fw.append(xM.get(i).getRawReadingNum()+"="+xM.get(i).getReadingValDouble()+"\n");
        	}
        }
        return fw.toString();
    }
    

}
