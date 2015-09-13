/**
 * 
 */
package com.microcaliperdevices.saje.run;

import java.io.IOException;
import java.util.List;

import com.microcaliperdevices.saje.CoreContainer;
import com.microcaliperdevices.saje.SetRuntypeUp;
import com.microcaliperdevices.saje.config.AbstractRunConfig;
import com.microcaliperdevices.saje.cyclic.FFT;
import com.microcaliperdevices.saje.cyclic.Cycle;
import com.microcaliperdevices.saje.history.CyclicRunHistoryEntry;
import com.microcaliperdevices.saje.io.file.DirectoryNotFoundException;
import com.microcaliperdevices.saje.license.License;

/**
 * @author jg
 *
 */
public class CyclicRun extends ContinuousRun {
    private CyclicRunHistoryEntry[] histe;
    // FFT transformation variables
    private FFT transform;
	private double[] cyclicIndex;
    private Cycle cycle; 

    // Outputs
    public double[] F;
    public double[] Y;
    public double[] Gain;
    public double[] Phase;
    public double[] PhaseU;
	/**
	 * @param lic
	 * @param arc
	 * @throws IOException
	 * @throws DirectoryNotFoundException
	 */
	public CyclicRun(License lic, AbstractRunConfig arc) throws IOException, DirectoryNotFoundException {
		super(lic, arc);
		SetRuntypeUp.setDataSource("Machine");
        transform = new FFT(runConfig.getSamplesPerGroup());
        cycle = new Cycle();
	}
    public double[] getCyclicIndex() {
		return cyclicIndex;
	}
	public void setCyclicIndex(float cyclicIndex) {
		this.cyclicIndex[activeGroupNum] = cyclicIndex;
	}

    public Cycle getCycle() {
		return cycle;
	}
    
	public void setCycle(Cycle cycle) {
		this.cycle = cycle;
	}
	
	public void setCycleLabel(String fLabel) {
		this.cycle.setLabel(fLabel);
	}
	
	public void setCyclesPerFoot(String fFoot) {
		this.cycle.setCyclesPerFoot(new Float(fFoot));
	}
	
    public float getCyclesPerFoot() {
			return this.cycle.getCyclesPerFoot();
	}
    
    public void setOptimalCycleLength(String string) {
    	cycle.setOptimalLength(new Float(string));
	}
    
    public String getCycleLabel() {
    	return cycle.getLabel();
    }
    public FFT getTransform() {
			return transform;
	}
	public void setTransform(FFT transform) {
			this.transform = transform;
	}
    protected void setRunConfig(AbstractRunConfig arc) throws IOException {
    	if( arc.getNumGroups() > 1 ) { // total if > 1
    		cyclicIndex = new double[arc.getNumGroups()+1];
       		histe = new CyclicRunHistoryEntry[arc.getNumGroups()+1];
    		for(int i = 0; i <= arc.getNumGroups(); i++) {
    			histe[i] = (CyclicRunHistoryEntry)CoreContainer.getHistory().addHistoryRow(this);
    		}
    	} else {
    		cyclicIndex = new double[arc.getNumGroups()];
       		histe = new CyclicRunHistoryEntry[arc.getNumGroups()];
    		histe[0] = (CyclicRunHistoryEntry)CoreContainer.getHistory().addHistoryRow(this);
    	}
    }
	/**
	 * Calculate FFT array values
	 */
	public void calculateFFT()
    {
        // Estimate spectrum using FFT
        // Real-valued input
        //
		double[] X = getRawData();
        if(isValidSampleSize(runConfig.getSamplesPerGroup())) {
            if (Y.length == 0 )
            {
                //Y = X;
            	System.arraycopy(X, 0, Y, 0, X.length);
                //X.CopyTo(Y, 0);
                //System.appDoEvents();
            }
            //do the major calculations  
            //transform.setHanning(X, Y, 1);
            //transform.FFT(ref Y, ref F);
            //transform.Gain2(ref F, ref Gain);
            //transform.Phase2(ref F, ref Phase);
            //transform.Unwrap(ref Phase, ref PhaseU, 0);
            transform.makeHanningWindow();
            transform.fft(X, Y);
 
        }
        //super.calculateMicroDeviation();
        //me.dataGridView2.Rows.Item(dataGridView2.Rows.Count - 2).Selected = true
    }
	
	public void updateRunState(String runDate, String WashboardIndex) {
        this.setCyclicIndex(new Float(WashboardIndex));
	}
	

    public String[][] setDisplay()
    {
    	String[][] disPout = new String[14][2];
 
        disPout[1][0] = "Inches";
        disPout[1][1] = String.valueOf(cycle.getOptimalLength());
        
        disPout[2][0] = "Cycle/Foot";
        disPout[2][1] = String.valueOf(cycle.getCyclesPerFoot());
        
        disPout[11][0] = "Cyclic Index";
        disPout[11][1] = String.valueOf(cyclicIndex);
        
        return disPout;
    }
	
	/**
	 *  Power of 2 check range 120-4096
	 * @param samplesPerGroup
	 * @return
	 */
    public static boolean isValidSampleSize(int samplesPerGroup)
    {
        switch (samplesPerGroup)
        {
            case 128:
            case 256:
            case 512:
            case 1024:
            case 2048:
            case 4096:
                return true;
            default:
                return false;
        }
    }
    
    /**
     * Power of 2 ceiling function (?) range 128 to 2048
     * @param inputSize String rep of int
     * @return If param inputSize LE 128->128, LE 256->256, LE 512->512, LE 1024->1024, LE 2048->2048 -- else if GT 2048->0
     */
    public long adjustSampleSize(String inputSize)
    {
        if (Integer.valueOf(inputSize).intValue() <= 128)
            return 128;
        if (Integer.valueOf(inputSize).intValue() <= 256)
            return 256;
        if (Integer.valueOf(inputSize).intValue() <= 512)
            return 512;
        if (Integer.valueOf(inputSize).intValue() <= 1024)
            return 1024;
        if (Integer.valueOf(inputSize).intValue() <= 2048)
            return 2048;
        return 0;
    }

    /**
     * Calls isValidSampleSize
     * @return
     */
    public boolean isValidSize()
    {
        return isValidSampleSize(runConfig.getSamplesPerGroup());
    }
    /**
     * Is length of trimmed cycle label not equal 1?
     * @return true if length ne 1
     */
    public boolean validCycleData()
    {
    	return (cycle.getLabel().trim().length() == 1);
    }

    public void calculateCyclicIndex(List<List<String>> dTable)
    {
        if ( !isValidSampleSize(runConfig.getSamplesPerGroup()) )
        {
            this.cyclicIndex[activeGroupNum] = 0;
            return;
        }
        double highestVal = 0;
        double top = 0;
        double bottom = 0;
        int i = 0;

        switch (cycle.getLabel())
        {
            case "A":
                top = 3.0;
                bottom = 2.5;
                break;
            case "B":
                top = 4.167;
                bottom = 3.667;
                break;
            case "C":
                top = 3.5;
                bottom = 3.0;
                break;
        }

        // assume dTable(0) has fields ?
        //List<String> fields = dTable.get(0);
        for (i = 1; i <= 149; i++) {
                List<String> dtRow = dTable.get(i);
                double field = new Double(dtRow.get(6)).doubleValue() ;
                if (field >= bottom && field <= top)
                {
                	double field7 = new Double(dtRow.get(7)).doubleValue();
                    if (field7 > highestVal)
                    {
                        highestVal = field7;
                    }
                }
        }  
        cyclicIndex[activeGroupNum] = highestVal * 2.0;
        //((CyclicRunHistoryEntry)history).setWashboardIndex(cyclicIndex);
    }
	/* (non-Javadoc)
	 * @see com.microcaliperdevices.saje.run.AbstractRun#postProcess()
	 */
	@Override
	public void postProcess() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
