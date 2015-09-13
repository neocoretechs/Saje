package com.microcaliperdevices.saje.run;

import java.io.IOException;

import com.microcaliperdevices.saje.config.AbstractRunConfig;
import com.microcaliperdevices.saje.io.file.DirectoryNotFoundException;
import com.microcaliperdevices.saje.io.machine.ContinuousInterface;
import com.microcaliperdevices.saje.io.machine.ContinuousMachine;
import com.microcaliperdevices.saje.license.License;

/*
 * Copyright Microcaliper Devices, LLC
 * Date: 2/17/2013
 * 
 */
	public class CyclicRunFactory extends AbstractRunFactory
	{
		public  AbstractRun createRun(License lic, AbstractRunConfig arc) throws IOException, DirectoryNotFoundException { 
			return new ContinuousRun(lic, arc); 
		}
		public  ContinuousInterface createMachine(AbstractRun currentRun) throws IOException {
			return new ContinuousMachine((ContinuousRun)currentRun);
		}
	}

