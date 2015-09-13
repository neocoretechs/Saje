/**
 * 
 */
package com.microcaliperdevices.saje;

import java.net.Socket;
import java.util.List;

import com.microcaliperdevices.saje.CoreContainer.ContainerProcessInterface;
import com.microcaliperdevices.saje.io.machine.bridge.RawDataXmlAdapter;


/**
 * The function of this module is to set data subset properties
 * @author jg
 * RunDataUp - 4447, RunDataDown - 4448, LicenseDown - 4451, 
 * HistoryUp - 4445, HistoryDown - 4446, ConfigUp - 4449, ConfigDown - 4450
 * ReadMachineData - 4440 SetSubsetUp - 4452
 *
 */
public final class SetSubsetUp implements ContainerProcessInterface {
	private static SetSubsetUp instance=null;
	private SetSubsetUp() {}
	public static SetSubsetUp getInstance() {
		if( instance == null ) {
			instance = new SetSubsetUp();
		}
		return instance;
	}

	@Override
	public void processAndRespond(Socket os) throws Exception {
		List<String> opts = MessageTransport.fromXml(new String(CoreContainer.getDataStream(os)));
		switch( opts.get(0) ) {
			case "all":
				RawDataXmlAdapter.setPageMode(RawDataXmlAdapter.PageMode.PAGE);
				RawDataXmlAdapter.setPage(0);	
				break;
			case "page": // Page mode page with start page 0 = all
				RawDataXmlAdapter.setPageMode(RawDataXmlAdapter.PageMode.PAGE);
				RawDataXmlAdapter.setPage(Integer.parseInt(opts.get(1)));
				if( opts.size() > 2 ) {
					RawDataXmlAdapter.setNumberPerPage(Integer.parseInt(opts.get(2)));
				}
				break;
			case "offset":
				RawDataXmlAdapter.setPageMode(RawDataXmlAdapter.PageMode.OFFSET);
				RawDataXmlAdapter.setStart(Integer.parseInt(opts.get(1)));
				RawDataXmlAdapter.setEnd(Integer.parseInt(opts.get(2)));
				break;
			default:
				throw new Exception("Data subset page mode "+opts.get(0)+" not valid");
		}
	
		//if( Props.DEBUG ) System.out.println("Page="+opts.get(0)+" number="+opts.get(1));
	}

	public static int getPort() {
		return 4452;
	}
	
}
