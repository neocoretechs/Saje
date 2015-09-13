/**
 * 
 */
package com.microcaliperdevices.saje;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


import com.microcaliperdevices.saje.CoreContainer.ContainerProcessInterface;
import com.microcaliperdevices.saje.io.machine.bridge.MachineBridge;
import com.microcaliperdevices.saje.io.machine.bridge.MachineReading;
import com.microcaliperdevices.saje.io.machine.bridge.RawDataXmlAdapter;


/**
 * @author jg
 * RunDataUp - 4447, RunDataDown - 4448, LicenseDown - 4451, 
 * HistoryUp - 4445, HistoryDown - 4446, ConfigUp - 4449, ConfigDown - 4450
 * ReadMachineData - 4440
 *
 */
public class RunDataDown implements ContainerProcessInterface {
	private static RunDataDown instance=null;
	private RunDataDown() {}
	public static RunDataDown getInstance() {
		if( instance == null ) {
			instance = new RunDataDown();
		}
		return instance;
	}
	/**
	 * Send a transport consisting of <page> <number_per_page>
	 */
	@Override
	public void processAndRespond(Socket os) throws Exception {
		CoreContainer.putDataStream(os, MachineBridge.toXml("dataset").getBytes());
	}
	/**
	 * Return new ArrayList of MachineReading
	 * @return
	 */
	public static List<MachineReading> getReadings() {
		List<MachineReading> coll = new ArrayList<MachineReading>();
		MachineBridge mb = MachineBridge.getInstance("dataset");
		for(int i = 0 ; i< mb.size(); i++) {
			coll.add(mb.get(i));
		}
		return coll;
	}
	
	public static int getPort() {
		return 4448;
	}
	
	public static void main(String[] args) throws Exception {
		MachineBridge mb = MachineBridge.getInstance("dataset");
		for(int i = 0; i < 100; i++) {
			MachineReading mr = new MachineReading(1, i, i+1, (.1 * i) );
			mb.add(mr);
		}
		RawDataXmlAdapter.setPage(2);
		String xml = MachineBridge.toXml("dataset");
		MachineBridge.fromXml(xml,"dataset");
		for( MachineReading mrr : getReadings()) {
			if( Props.DEBUG ) System.out.println(mrr.toString());
		}
		
		
	}

}
