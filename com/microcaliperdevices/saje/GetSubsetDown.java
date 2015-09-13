/**
 * 
 */
package com.microcaliperdevices.saje;

import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.microcaliperdevices.saje.CoreContainer.ContainerProcessInterface;
import com.microcaliperdevices.saje.io.machine.MachineInterface;
import com.microcaliperdevices.saje.io.machine.StreamMachine;
import com.microcaliperdevices.saje.io.machine.bridge.MachineBridge;
import com.microcaliperdevices.saje.io.machine.bridge.MachineReading;
import com.microcaliperdevices.saje.io.machine.bridge.RawDataXmlAdapter;
import com.microcaliperdevices.saje.license.LicenseFailException;
import com.microcaliperdevices.saje.run.AbstractRun;
import com.microcaliperdevices.saje.run.AbstractRunFactory;


/**
 * The function of this module is to return data subset info
 * @author jg
 * RunDataUp - 4447, RunDataDown - 4448, LicenseDown - 4451, 
 * HistoryUp - 4445, HistoryDown - 4446, ConfigUp - 4449, ConfigDown - 4450
 * ReadMachineData - 4440 SetSubsetUp - 4452 GetSubsetDown - 4453 
 *
 */
public final class GetSubsetDown implements ContainerProcessInterface {
	private static GetSubsetDown instance=null;
	private GetSubsetDown() {}
	public static GetSubsetDown getInstance() {
		if( instance == null ) {
			instance = new GetSubsetDown();
		}
		return instance;
	}
	/**
	 * Page, number per page, total size
	 */
	@Override
	public void processAndRespond(Socket os) throws Exception {
		MessageTransport mt = new MessageTransport();
		mt.message.add(String.valueOf(RawDataXmlAdapter.getPage()));
		mt.message.add(String.valueOf(RawDataXmlAdapter.getNumberPerPage()));
		mt.message.add(String.valueOf(MachineBridge.getInstance("dataset").size()));
		CoreContainer.putDataStream(os, mt.toXml().getBytes());
	}

	public static int getPort() {
		return 4453;
	}
	
}
