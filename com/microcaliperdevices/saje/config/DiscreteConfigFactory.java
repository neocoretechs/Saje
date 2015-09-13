/**
 * 
 */
package com.microcaliperdevices.saje.config;

import java.io.IOException;

import com.microcaliperdevices.saje.io.file.DirectoryNotFoundException;
import com.microcaliperdevices.saje.license.License;

/**
 * @author jg
 *
 */
public class DiscreteConfigFactory extends AbstractConfigFactory {

	/* (non-Javadoc)
	 * @see com.microcaliperdevices.saje.config.AbstractConfigFactory#createConfig(com.microcaliperdevices.saje.license.License)
	 */
	@Override
	public AbstractRunConfig createConfig(License lic) throws IOException, DirectoryNotFoundException {
		return DiscreteRunConfig.newInstance(lic);
	}

	/* (non-Javadoc)
	 * @see com.microcaliperdevices.saje.config.AbstractConfigFactory#createConfig()
	 */
	@Override
	public AbstractRunConfig createConfig() {
		return new DiscreteRunConfig();
	}

}
