package com.microcaliperdevices.saje.run;

import java.io.IOException;

import com.microcaliperdevices.saje.config.AbstractRunConfig;
import com.microcaliperdevices.saje.io.file.DirectoryNotFoundException;
import com.microcaliperdevices.saje.io.machine.DiscreteMachine;
import com.microcaliperdevices.saje.io.machine.MachineInterface;
import com.microcaliperdevices.saje.license.License;

/*
 * Copyright Microcaliper Devices, LLC
 * Date: 2/17/2013
 * 
 */
	public class DiscreteRunFactory extends AbstractRunFactory
	{
		public DiscreteRun createRun(License lic, AbstractRunConfig arc) throws IOException, DirectoryNotFoundException { 
			return new DiscreteRun(lic, arc); 
		}
		public MachineInterface createMachine(AbstractRun currentRun) throws IOException {
			return new DiscreteMachine((DiscreteRun)currentRun);
		}
	}
