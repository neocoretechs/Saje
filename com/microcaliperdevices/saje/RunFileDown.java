/**
 * 
 */
package com.microcaliperdevices.saje;

import java.net.Socket;

import com.microcaliperdevices.saje.CoreContainer.ContainerProcessInterface;
import com.microcaliperdevices.saje.license.LicenseFailException;
import com.microcaliperdevices.saje.run.AbstractRun;
import com.microcaliperdevices.saje.run.AbstractRunFactory;

/**
 * @author jg
 * RunDataUp - 4447, RunDataDown - 4448, LicenseDown - 4451, 
 * HistoryUp - 4445, HistoryDown - 4446, ConfigUp - 4449, ConfigDown - 4450
 * ReadMachineData - 4440
 *
 */
public class RunFileDown implements ContainerProcessInterface {
	private static RunFileDown instance=null;
	private RunFileDown() {}
	public static RunFileDown getInstance() {
		if( instance == null ) {
			instance = new RunFileDown();
		}
		return instance;
	}
	/**
	 * 
	 */
	@Override
	public void processAndRespond(Socket os) throws Exception {
        AbstractRun currentRun = CoreContainer.getCurrentRun();
		LicenseDown lic = LicenseDown.getInstance();
		if( lic.isFail() )
				throw new LicenseFailException("FAIL!");
	    if( currentRun == null) {
	        AbstractRunFactory factory = AbstractRunFactory.createFactory(SetRuntypeUp.getRuntype(),"Machine");
	        currentRun = factory.createRun(lic.getLicense(), CoreContainer.getCurrentConfig());
	        CoreContainer.setCurrentRun(currentRun);
		}
		String rFile = currentRun.createRunFile(SetRuntypeUp.getRuntype());
		CoreContainer.putDataStream(os, rFile.getBytes());
	}

	
	public static int getPort() {
		return 4455;
	}
	
	public static void main(String[] args) throws Exception {

	}

}
