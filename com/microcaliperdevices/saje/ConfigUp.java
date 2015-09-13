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
public final class ConfigUp implements ContainerProcessInterface {
	private static ConfigUp instance = null;
	private ConfigUp() {};
	public static ConfigUp getInstance() {
		if( instance == null ) {
			instance = new ConfigUp();
			//if( Props.DEBUG ) System.out.println(CoreContainer.getCurrentConfig().toXml());
		}
		return instance;
	}
	/* (non-Javadoc)
	 * @see com.microcaliperdevices.saje.ContainerProcessInterface#processAndRespond(byte[], java.io.OutputStream)
	 */
	@Override
	public void processAndRespond(Socket os) throws Exception {
		String xml = new String(CoreContainer.getDataStream(os));
		CoreContainer.setConfigFromXml(xml);
		//FileIOUtilities.writeAllLines(FileIOUtilities.dataDirectory, "sa.conf", new String[]{xml});
	}


	public static int getPort() {
		return 4449;
	}
	

}
