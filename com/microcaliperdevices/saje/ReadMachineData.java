/**
 * 
 */
package com.microcaliperdevices.saje;

import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.microcaliperdevices.saje.CoreContainer.ContainerProcessInterface;
import com.microcaliperdevices.saje.io.machine.MachineInterface;
import com.microcaliperdevices.saje.io.machine.bridge.MachineBridge;
import com.microcaliperdevices.saje.io.machine.bridge.MachineReading;
import com.microcaliperdevices.saje.license.LicenseFailException;
import com.microcaliperdevices.saje.run.AbstractRun;
import com.microcaliperdevices.saje.run.AbstractRunFactory;

/**
 * The objective is to provide a controller that will wait for the MachineBridge to finish filling the
 * ArrayList with data from some source machine, and then signal the finish and at that point
 * data can be retrieved from the MachineBridge ArrayList
 * @author jg
 * RunDataUp - 4447, RunDataDown - 4448, LicenseDown - 4451, 
 * HistoryUp - 4445, HistoryDown - 4446, ConfigUp - 4449, ConfigDown - 4450
 * ReadMachineData - 4440
 *
 */
public final class ReadMachineData implements ContainerProcessInterface, Observer {
	String returnStatus;
	private static ReadMachineData instance=null;
	private ReadMachineData() {}
	public static ReadMachineData getInstance() {
		if( instance == null ) {
			instance = new ReadMachineData();
		}
		return instance;
	}
	public static int getPort() { return 4440; }
	
	/* (non-Javadoc)
	 * @see com.microcaliperdevices.saje.CoreContainer.ContainerProcessInterface#processAndRespond(java.net.Socket)
	 */
	@Override
	public void processAndRespond(Socket os) throws Exception {
	        LicenseDown lic = LicenseDown.getInstance();
			if( lic.isFail() )
				throw new LicenseFailException("FAIL!");
	        //String machineType =  lic.getMachineType();
	        //String model = lic.getMachineModel();
	        //if( Props.DEBUG ) System.out.println("Machine Type="+machineType+" "+model);
	        
	        //AbstractRunFactory factory = AbstractRunFactory.createFactory(machineType+"File");
	        AbstractRunFactory factory = AbstractRunFactory.createFactory(SetRuntypeUp.getRuntype(), SetRuntypeUp.getDataSource());
	        // start in default location, else we can name a directory to store run files i.e. "todaysrun/"
	        AbstractRun currentRun = factory.createRun(lic.getLicense(), CoreContainer.getCurrentConfig());
	        CoreContainer.setCurrentRun(currentRun);
	        MachineInterface machine = factory.createMachine(currentRun);
	        //((AbstractMachine)machine).addObserver(this);
	        // may get update from observer setting return status
	        returnStatus = "Read Complete";
	        exec(machine);
	        CoreContainer.putDataStream(os, returnStatus.getBytes());
	        
	        //if( Props.DEBUG ) System.out.println("Exited main read loop");	
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
		        System.out.println("Awaiting readings...");
		        MachineBridge mb = MachineBridge.getInstance("dataset");
		        while(true) {
		        		mr = mb.waitForNewReading(getVal);	
		        		if( mr == null )
		        			break;
		        		//if( Props.DEBUG ) System.out.println(getVal.get()+"="+mr);
		        }
		        System.out.println("...End of Readings");
		        // calc the values for the run and place then into a history row
		        try {
		        	if( mb.size() > 0 ) {
		        		machine.postProcess(); // compute, update history entry
		           		//CoreContainer.updateHistory();
		           		CoreContainer.getHistory().saveState(); // save current history state
		        	} else
		        		returnStatus = "No Data Returned from Machine";
				} catch (Exception e) {
					returnStatus = e.getMessage();
					e.printStackTrace();
				}
		}
		/**
		 * See if we get a not ready message from reader thread
		 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
		 */
		@Override
		public void update(Observable arg0, Object arg1) {
			//if( Props.DEBUG ) System.out.println("update "+arg0);
			returnStatus = new MachineNotReadyException().getMessage();
			// tell the waiters that no readings present
	        MachineBridge mb = MachineBridge.getInstance("dataset");
			mb.signalEnd();
			//if( Props.DEBUG ) System.out.println("Signal sent from update");
			//if( Props.DEBUG ) System.out.println("Machine not ready: "+new MachineNotReadyException().getMessage());		
		}


}
