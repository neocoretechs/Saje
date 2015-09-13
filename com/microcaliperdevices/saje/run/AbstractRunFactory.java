/*
 * Copyright Microcaliper Devices, LLC
 * Date: 2/16/2013
 * 
 */
package com.microcaliperdevices.saje.run;

import java.io.IOException;

import com.microcaliperdevices.saje.NotSupportedException;
import com.microcaliperdevices.saje.config.AbstractRunConfig;
import com.microcaliperdevices.saje.io.file.DirectoryNotFoundException;
import com.microcaliperdevices.saje.io.machine.MachineInterface;
import com.microcaliperdevices.saje.license.License;

	/**
	 * Abstract factory pattern to create the proper Run type based on machine type, caliper or smoothness
	 * @author jg
	 *
	 */
	public abstract class AbstractRunFactory
	{
		public abstract AbstractRun createRun(License lic, AbstractRunConfig arc) throws IOException, DirectoryNotFoundException;
		public abstract MachineInterface createMachine(AbstractRun currentRun) throws IOException;
	
		public static AbstractRunFactory createFactory(String runType, String dataSource) throws NotSupportedException {
			switch(runType) {
				case "Discrete":
					switch(dataSource) {
						case "Machine":
							return new DiscreteRunFactory();
						case "File":
							return new DiscreteFileRunFactory();
						case "Stream":
							return new DiscreteStreamRunFactory();
						default:
							throw new NotSupportedException(dataSource);
					}
				case "Continuous":
					switch(dataSource) {
						case "Machine":
							return new ContinuousRunFactory();
						case "File":
							return new ContinuousFileRunFactory();
						case "Stream":
							return new ContinuousStreamRunFactory();
						default:
							throw new NotSupportedException(dataSource);
					}
				case "Cyclic":
					switch(dataSource) {
						case "Machine":
							return new CyclicRunFactory();
						case "File":
							return new CyclicFileRunFactory();
						case "Stream":
							return new CyclicStreamRunFactory();
						default:
							throw new NotSupportedException(dataSource);
					}
        	    default:
                    throw new NotSupportedException("The Machine mode "+runType+" is not supported.");
			}
		}
		
	}