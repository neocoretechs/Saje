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
public class CyclicStreamRunFactory extends AbstractRunFactory {

	/* (non-Javadoc)
	 * @see com.microcaliperdevices.saje.run.AbstractRunFactory#createRun(com.microcaliperdevices.saje.license.License, java.lang.String)
	 */
	@Override
	public AbstractRun createRun(License lic, AbstractRunConfig arc) throws IOException, DirectoryNotFoundException {
		return new CyclicStreamRun(lic, arc);
	}

	/* (non-Javadoc)
	 * @see com.microcaliperdevices.saje.run.AbstractRunFactory#createMachine(com.microcaliperdevices.saje.run.AbstractRun)
	 */
	@Override
	public MachineInterface createMachine(AbstractRun currentRun) {
		return new StreamMachine(currentRun);
	}

}
