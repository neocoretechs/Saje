/**
 * 
 */
package com.microcaliperdevices.saje;

import java.io.IOException;
import java.net.Socket;

import com.microcaliperdevices.saje.CoreContainer.ContainerProcessInterface;
import com.microcaliperdevices.saje.history.AbstractHistoryFactory;
import com.microcaliperdevices.saje.io.file.DirectoryNotFoundException;

/**
 * @author jg
 * RunDataUp - 4447, RunDataDown - 4448, LicenseDown - 4451, 
 * HistoryUp - 4445, HistoryDown - 4446, ConfigUp - 4449, ConfigDown - 4450
 * ReadMachineData - 4440
 * Set the Sajecontainer var when we construct service
 */
public final class HistoryDown implements ContainerProcessInterface {
	private static HistoryDown instance=null;
	private HistoryDown() throws NotSupportedException, IOException, DirectoryNotFoundException {	
		AbstractHistoryFactory ahf = AbstractHistoryFactory.createFactory(SetRuntypeUp.getRuntype());
		CoreContainer.setHistory(ahf.createHistory());
	}
	public static HistoryDown getInstance() throws NotSupportedException, IOException, DirectoryNotFoundException {
		if( instance == null ) {
			instance = new HistoryDown();
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see com.microcaliperdevices.saje.ContainerProcessInterface#processAndRespond(byte[], java.io.OutputStream)
	 */
	@Override
	public void processAndRespond(Socket os) throws IOException {
		CoreContainer.putDataStream(os, CoreContainer.getHistory().toXml().getBytes());
	}

	public static int getPort() {
		return 4446;
	}

}
