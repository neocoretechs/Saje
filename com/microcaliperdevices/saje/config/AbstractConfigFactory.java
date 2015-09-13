/*
 * Copyright Microcaliper Devices, LLC
 * Date: 2/16/2013
 * 
 */
package com.microcaliperdevices.saje.config;

import java.io.IOException;

import com.microcaliperdevices.saje.NotSupportedException;
import com.microcaliperdevices.saje.io.file.DirectoryNotFoundException;
import com.microcaliperdevices.saje.license.License;

	/**
	 * Abstract factory pattern to create the proper Run type based on run type
	 * @author jg
	 *
	 */
	public abstract class AbstractConfigFactory
	{
		// Default configuration based on license
		public abstract AbstractRunConfig createConfig(License lic) throws IOException, DirectoryNotFoundException;
		// User mods to config loaded via this
		public abstract AbstractRunConfig createConfig();
		/**
		 * Even though when using a 'file' datasource type the configs for the run come from the file itself,
		 * we still need to be able to deliver a global config
		 * @param runType
		 * @return
		 * @throws NotSupportedException
		 */
		public static AbstractConfigFactory createFactory(String runType) throws NotSupportedException {
			switch(runType) {
				case "Discrete":
					return new DiscreteConfigFactory();
				case "Continuous":
					return new ContinuousConfigFactory();
				case "Cyclic":
					return new CyclicConfigFactory(); // no special config
        	    default:
                    throw new NotSupportedException("The Run type "+runType+" is not supported.");
			}
		}
		
	}