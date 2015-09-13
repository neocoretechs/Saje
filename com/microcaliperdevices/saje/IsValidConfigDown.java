/**
 * 
 */
package com.microcaliperdevices.saje;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import com.microcaliperdevices.saje.CoreContainer.ContainerProcessInterface;
import com.microcaliperdevices.saje.config.AbstractConfigFactory;
import com.microcaliperdevices.saje.config.AbstractRunConfig;
import com.microcaliperdevices.saje.io.file.DirectoryNotFoundException;
import com.microcaliperdevices.saje.io.file.FileIOUtilities;


/**
 * @author jg
 * RunDataUp - 4447, RunDataDown - 4448, LicenseDown - 4451, 
 * HistoryUp - 4445, HistoryDown - 4446, ConfigUp - 4449, ConfigDown - 4450
 * ReadMachineData - 4440
 *
 */
public final class IsValidConfigDown implements ContainerProcessInterface {
	private static IsValidConfigDown instance=null;
	private IsValidConfigDown() {}
	public static IsValidConfigDown getInstance() {
		if( instance == null ) {
			instance = new IsValidConfigDown();
		}
		return instance;
	}
	/* (non-Javadoc)
	 * @see com.microcaliperdevices.saje.ContainerProcessInterface#processAndRespond(byte[], java.io.OutputStream)
	 */
	@Override
	public void processAndRespond(Socket os) throws Exception {
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"yes\"?><validationerror>";
		xml += CoreContainer.getWhyInvalid();
		xml += "</validationerror>";
		CoreContainer.putDataStream(os, xml.getBytes());
	}
	
	/* (non-Javadoc)
	 * @see com.microcaliperdevices.saje.ContainerProcessInterface#getPort()
	 */
	public static int getPort() {
		return 4454;
	}
	

}
