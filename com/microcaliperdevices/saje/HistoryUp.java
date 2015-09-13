/**
 * 
 */
package com.microcaliperdevices.saje;

import java.net.Socket;

import com.microcaliperdevices.saje.CoreContainer.ContainerProcessInterface;

/**
 * @author jg
 * RunDataUp - 4447, RunDataDown - 4448, LicenseDown - 4451, 
 * HistoryUp - 4445, HistoryDown - 4446, ConfigUp - 4449, ConfigDown - 4450
 * ReadMachineData - 4440
 *
 */
public final class HistoryUp implements ContainerProcessInterface {
	private static HistoryUp instance=null;
	private HistoryUp() {}
	public static HistoryUp getInstance() {
		if( instance == null ) {
			instance = new HistoryUp();
		}
		return instance;
	}

	/* (non-Javadoc)
	 * @see com.microcaliperdevices.saje.ContainerProcessInterface#processAndRespond(byte[], java.io.OutputStream)
	 */
	@Override
	public void processAndRespond(Socket os) {
		// TODO Auto-generated method stub

	}

	public static int getPort() {
		return 4445;
	}

}
