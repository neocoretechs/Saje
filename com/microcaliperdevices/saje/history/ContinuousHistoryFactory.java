/**
 * 
 */
package com.microcaliperdevices.saje.history;

import java.io.IOException;

import com.microcaliperdevices.saje.io.file.DirectoryNotFoundException;

/**
 * @author jg
 *
 */
public class ContinuousHistoryFactory extends AbstractHistoryFactory {

	/* (non-Javadoc)
	 * @see com.microcaliperdevices.saje.config.AbstractConfigFactory#createConfig(com.microcaliperdevices.saje.license.License)
	 */
	@Override
	public AbstractRunHistory createHistory() throws IOException, DirectoryNotFoundException {
		ContinuousRunHistory crh = new ContinuousRunHistory();
		crh.loadHistory();
		return crh;
	}


}
