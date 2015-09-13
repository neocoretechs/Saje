/*
 * Copyright Microcaliper Devices, LLC
 * Date: 2/16/2013
 * 
 */
package com.microcaliperdevices.saje.history;

import java.io.IOException;

import com.microcaliperdevices.saje.NotSupportedException;
import com.microcaliperdevices.saje.io.file.DirectoryNotFoundException;

	/**
	 * Abstract factory pattern to create the proper history type based on machine mode
	 * @author jg
	 *
	 */
	public abstract class AbstractHistoryFactory
	{
		// Default configuration
		public abstract AbstractRunHistory createHistory() throws IOException, DirectoryNotFoundException;
		/**
		 * Even though when using a 'file' machine type the configs for the run come from the file itself,
		 * we still need to be able to deliver a global config
		 * @param runType
		 * @return
		 * @throws NotSupportedException
		 */
		public static AbstractHistoryFactory createFactory(String runType) throws NotSupportedException {
			switch(runType) {
				case "Discrete":// 
					return new DiscreteHistoryFactory();
				case "Continuous": // 
					return new ContinuousHistoryFactory();
				case "Cyclic":
					return new CyclicHistoryFactory();
        	    default:
                    throw new NotSupportedException("The run type "+runType+" is not supported.");
			}
		}
		
	}