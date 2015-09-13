package com.microcaliperdevices.saje.io.machine;

import java.io.IOException;

import com.microcaliperdevices.saje.io.machine.dataport.DataPortInterface;

	/**
	 * Interface to encapsulate the functions of input/output on a remote device
	 * The concept of a 'dataPort' which implements DataPortInterface abstracts the actual device.
	 * A dataPort may be a filePort, or a serialPort, etc.
	 * @author jg
	 * Copyright ©  2012 Microcaliper Devices, LLC
	 */
    public interface MachineInterface extends Runnable
    {
        public String stringSettings();
        public void waitMilliSecs(long miliSecs);

 		public String getMachineType();
 		public void setMachineType(String machineType);
 
 		public DataPortInterface getDataPort();
 		public void setDataPort(DataPortInterface dataPort);
 		public String getPortName();

 		public void connect(boolean writeable) throws IOException;
        public void write(String output) throws IOException;
        public int read() throws IOException;
 		public String getUnitsOfMeasure();
        public void close();
        
        public void postProcess() throws Exception;
    }
