/**
 * 
 */
package com.microcaliperdevices.saje;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.microcaliperdevices.saje.CoreContainer.ContainerProcessInterface;
import com.microcaliperdevices.saje.io.machine.MachineInterface;
import com.microcaliperdevices.saje.io.machine.StreamMachine;
import com.microcaliperdevices.saje.io.machine.bridge.MachineBridge;
import com.microcaliperdevices.saje.io.machine.bridge.MachineReading;
import com.microcaliperdevices.saje.license.LicenseFailException;
import com.microcaliperdevices.saje.run.AbstractRun;
import com.microcaliperdevices.saje.run.AbstractRunFactory;


/**
 * The function of this module is to upload .prn files
 * @author jg
 * RunDataUp - 4447, RunDataDown - 4448, LicenseDown - 4451, 
 * HistoryUp - 4445, HistoryDown - 4446, ConfigUp - 4449, ConfigDown - 4450
 * ReadMachineData - 4440
 *
 */
public final class RunDataUp implements ContainerProcessInterface {
	private String returnStatus;
	private static RunDataUp instance=null;
	private RunDataUp() {}
	public static RunDataUp getInstance() {
		if( instance == null ) {
			instance = new RunDataUp();
		}
		return instance;
	}

	@Override
	public void processAndRespond(Socket os) throws Exception {
		LicenseDown lic = LicenseDown.getInstance();
		if( lic.isFail() )
				throw new LicenseFailException("FAIL!");
        
        AbstractRunFactory factory = AbstractRunFactory.createFactory(SetRuntypeUp.getRuntype(),"Stream");
        // start in default location, else we can name a directory to store run files i.e. "todaysrun/"
        AbstractRun currentRun = factory.createRun(lic.getLicense(), CoreContainer.getCurrentConfig());
        CoreContainer.setCurrentRun(currentRun);
        MachineInterface machine = factory.createMachine(currentRun);
        ((StreamMachine)machine).setDataPort(os.getInputStream(), null);
        //((AbstractMachine)machine).addObserver(this);
        // may get update from observer setting return status
        returnStatus = "Read Complete";
        exec(machine);
        //CoreContainer.putDataStream(os, returnStatus.getBytes());
        
        //if( Props.DEBUG ) System.out.println("Units="+machine.getUnitsOfMeasure());
        //if( Props.DEBUG ) System.out.println("Exited main read loop");
        //double[][] data = machine.getRawData();
        //for(int i = 0; i < data.length; i++) { // groups
        //	for(int j = 0; j < data[i].length; j++) { // reading seq
        //		if( Props.DEBUG ) System.out.println("Group="+i+" Reading Seq.="+j+" Reading Num="+data[i][j]+" Value="+data[i][j]);
        //	}
        //}

	}

	public static int getPort() {
		return 4447;
	}
	
	private void exec(MachineInterface machine) {
	    //DaemonThreadFactory dtf = new DaemonThreadFactory();
	    //ExecutorService executor = Executors.newSingleThreadExecutor(dtf);
        //MachineBridge.getInstance().init();
        //executor.execute(machine);
		ThreadPoolManager.getInstance().spin(machine, "dataset");
        MachineReading mr;   
        AtomicInteger getVal = new AtomicInteger();
        // we are going to load up the collection in MachineBridge, then get it later
        // this loop basically waits until thats done.
        MachineBridge mb = MachineBridge.getInstance("dataset");
        while(true) {
        		mr = mb.waitForNewReading(getVal);	
        		if( mr == null )
        			break;
        		//if( Props.DEBUG ) System.out.println(getVal.get()+"="+mr);
        }
        // calc the values for the run and place then into a history row
        try {
        	if( mb.size() > 0 ) {
        		machine.postProcess();
        		//CoreContainer.updateHistory();
        		CoreContainer.getHistory().saveState();
        	} else
        		returnStatus = "No Data Returned from Machine";
		} catch (Exception e) {
			returnStatus = e.getMessage();
			e.printStackTrace();
		}
	}
	

}
