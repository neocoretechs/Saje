/**
 * 
 */
package com.microcaliperdevices.saje.run;

import java.io.IOException;

import com.microcaliperdevices.saje.config.AbstractRunConfig;
import com.microcaliperdevices.saje.io.file.DirectoryNotFoundException;
import com.microcaliperdevices.saje.io.machine.MachineInterface;
import com.microcaliperdevices.saje.io.machine.StreamMachine;
import com.microcaliperdevices.saje.license.License;

/**
 * @author jg
 *
 */
public class DiscreteStreamRunFactory extends AbstractRunFactory {

	/* (non-Javadoc)
	 * @see com.microcaliperdevices.saje.run.AbstractRunFactory#createRun(com.microcaliperdevices.saje.license.License, java.lang.String)
	 */
	@Override
	public DiscreteRun createRun(License lic, AbstractRunConfig arc) throws IOException, DirectoryNotFoundException {
		return new DiscreteStreamRun(lic, arc);
	}

	/* (non-Javadoc)
	 * @see com.microcaliperdevices.saje.run.AbstractRunFactory#createMachine(com.microcaliperdevices.saje.run.AbstractRun)
	 */
	@Override
	public MachineInterface createMachine(AbstractRun currentRun) {
		return new StreamMachine(currentRun);
	}

}
