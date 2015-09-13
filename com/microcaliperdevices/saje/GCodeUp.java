/**
 * 
 */
package com.microcaliperdevices.saje;

import java.net.Socket;

import com.microcaliperdevices.saje.CoreContainer.ContainerProcessInterface;
import com.microcaliperdevices.saje.io.machine.dataport.ByteSerialDataPort;


/**
 * @author jg
 * RunDataUp - 4447, RunDataDown - 4448, LicenseDown - 4451, 
 * HistoryUp - 4445, HistoryDown - 4446, ConfigUp - 4449, ConfigDown - 4450
 * ReadMachineData - 4440
 *
 */
public final class GCodeUp implements ContainerProcessInterface {
	private static GCodeUp instance = null;
	private GCodeUp() {};
	public static GCodeUp getInstance() {
		if( instance == null ) {
			instance = new GCodeUp();
			//if( Props.DEBUG ) System.out.println(CoreContainer.getCurrentConfig().toXml());
		}
		return instance;
	}
	/* (non-Javadoc)
	 * @see com.microcaliperdevices.saje.ContainerProcessInterface#processAndRespond(byte[], java.io.OutputStream)
	 */
	@Override
	public void processAndRespond(Socket os) throws Exception {
		String gcode = new String(CoreContainer.getDataStream(os));
		System.out.println("Writing GCode:"+gcode+" len:"+gcode.length());
		ByteSerialDataPort.getInstance().writeLine(gcode);
		//FileIOUtilities.writeAllLines(FileIOUtilities.dataDirectory, "sa.conf", new String[]{xml});
	}


	public static int getPort() {
		return 4500;
	}
	

}
