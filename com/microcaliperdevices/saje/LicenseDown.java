/**
 * 
 */
package com.microcaliperdevices.saje;

import java.io.IOException;
import java.net.Socket;

import com.microcaliperdevices.saje.CoreContainer.ContainerProcessInterface;
import com.microcaliperdevices.saje.license.License;
import com.microcaliperdevices.saje.license.LicenseFailException;

/**
 * @author jg
 * RunDataUp - 4447, RunDataDown - 4448, LicenseDown - 4451, 
 * HistoryUp - 4445, HistoryDown - 4446, ConfigUp - 4449, ConfigDown - 4450
 * ReadMachineData - 4440
 *
 */
public final class LicenseDown implements ContainerProcessInterface {
	private static LicenseDown instance = null;
	private static boolean isfail = true;
	private static String xml = null;
	private static License license;
	private LicenseDown() {}
	public static LicenseDown getInstance() {
		if( instance == null ) {
			instance = new LicenseDown();
			try {
				license = new License();
				xml = license.toXml();
				isfail = false;
			} catch (IOException | LicenseFailException e) {
			xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"+
			"<license><company>THE LICENSE FOR THIS PRODUCT IS INVALID. "+e.getMessage()+"</company><spackage></spackage>" +
			"<expires></expires></license>";
			System.out.println(e.getMessage());
			}
		}
		return instance;
	}
	
	public boolean isFail() { return isfail; }
	
	public License getLicense() {
		return license;
	}
	public String getXml() {
		return xml;
	}
	/* (non-Javadoc)
	 * @see com.microcaliperdevices.saje.ContainerProcessInterface#processAndRespond(byte[], java.io.OutputStream)
	 */
	@Override
	public void processAndRespond(Socket os) throws Exception {
		CoreContainer.putDataStream(os, xml.getBytes());
	}

	public static int getPort() {
		return 4451;
	}
	
}
