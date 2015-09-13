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
import com.microcaliperdevices.saje.io.machine.dataport.ByteSerialDataPort;
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
 * ReadMachineData - 4440, StreamMachineData 4441
 *
 */
public final class StreamMachineData implements ContainerProcessInterface, Observer {
	String returnStatus;
	private boolean shouldRun = true;
	private Object signalEnd = new Object();
	private static StreamMachineData instance=null;
	private static ByteSerialDataPort dataport = ByteSerialDataPort.getInstance();
	private StreamMachineData() {}
	public static StreamMachineData getInstance() {
		if( instance == null ) {
			instance = new StreamMachineData();
		}
		return instance;
	}
	public static int getPort() { return 4441; }
	
	/**
	 * Stream the data, we dont actually return unless there is a major fault
	 * @see com.microcaliperdevices.saje.CoreContainer.ContainerProcessInterface#processAndRespond(java.net.Socket)
	 */
	@Override
	public void processAndRespond(Socket os) throws Exception {
		while(shouldRun ) {
			byte[] serialBytes = dataport.readLine().getBytes();
			if( serialBytes != null )
				CoreContainer.putDataStream(os, serialBytes);
		}
	    synchronized(signalEnd) {
	    	signalEnd.notifyAll(); // tell any shutdown waiters
	    }
	    //if( Props.DEBUG ) System.out.println("Exited main read loop");	
	}
		

	/**
	 * See if we get shutdown message
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	@Override
	public void update(Observable arg0, Object arg1) {
			//if( Props.DEBUG ) System.out.println("update "+arg0);
			shouldRun = false;
			synchronized(signalEnd) {
				try {
					signalEnd.wait();
				} catch (InterruptedException e) {
				}
			}
			//if( Props.DEBUG ) System.out.println("Signal sent from update");
	}


}
