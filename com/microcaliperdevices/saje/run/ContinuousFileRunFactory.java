package com.microcaliperdevices.saje.run;

import java.io.IOException;

import com.microcaliperdevices.saje.config.AbstractRunConfig;
import com.microcaliperdevices.saje.io.file.DirectoryNotFoundException;
import com.microcaliperdevices.saje.io.machine.FileMachine;
import com.microcaliperdevices.saje.io.machine.MachineInterface;
import com.microcaliperdevices.saje.license.License;

public class ContinuousFileRunFactory extends AbstractRunFactory {

	@Override
	public AbstractRun createRun(License lic, AbstractRunConfig arc) throws IOException, DirectoryNotFoundException {
		return new ContinuousFileRun(lic, arc);
	}

	@Override
	public MachineInterface createMachine(AbstractRun currentRun) {
		return new FileMachine((DiscreteFileRun) currentRun);
	}

}
