/**
 * 
 */
package com.microcaliperdevices.saje;

import java.net.Socket;
import java.util.List;

import com.microcaliperdevices.saje.CoreContainer.ContainerProcessInterface;
import com.microcaliperdevices.saje.config.AbstractConfigFactory;
import com.microcaliperdevices.saje.config.AbstractRunConfig;
import com.microcaliperdevices.saje.history.AbstractHistoryFactory;
import com.microcaliperdevices.saje.io.file.FileIOUtilities;


/**
 * The function of this module is to set the type of run for further operations.
 * When run type is changed we change config and history to apply
 * @author jg
 *
 */
public final class SetRuntypeUp implements ContainerProcessInterface {
	private static String runType = "Discrete";
	private static String dataSource = "Machine";
	// Stream, file
	public static String getDataSource() {
		return dataSource;
	}
	public static void setDataSource(String dataSource) {
		SetRuntypeUp.dataSource = dataSource;
	}
	public static String getConfigFileName() {
		return FileIOUtilities.getConfigfileName(runType);
	}
	private static SetRuntypeUp instance=null;
	private SetRuntypeUp() {}
	public static SetRuntypeUp getInstance() {
		if( instance == null ) {
			instance = new SetRuntypeUp();
		}
		return instance;
	}

	public static String getRuntype() { return runType; }
	
	@Override
	public void processAndRespond(Socket os) throws Exception {
		List<String> opts = MessageTransport.fromXml(new String(CoreContainer.getDataStream(os)));
		runType = opts.get(0);
		AbstractConfigFactory acf = AbstractConfigFactory.createFactory(runType);
		AbstractRunConfig arc = acf.createConfig(LicenseDown.getInstance().getLicense());
		CoreContainer.setCurrentConfig(arc);
		AbstractHistoryFactory ahf = AbstractHistoryFactory.createFactory(runType);
		CoreContainer.setHistory(ahf.createHistory());
	}

	public static int getPort() {
		return 4456;
	}
	
}
