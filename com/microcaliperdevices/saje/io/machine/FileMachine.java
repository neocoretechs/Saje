package com.microcaliperdevices.saje.io.machine;

import com.microcaliperdevices.saje.io.file.FileIOUtilities;
import com.microcaliperdevices.saje.io.machine.dataport.FileDataPort;
import com.microcaliperdevices.saje.run.DiscreteFileRun;
/**
 * Extends StreamMachine to manipulate file streams.
 * We are imitating a machine but instead of all the IO operations just read from file and
 * populate data.
 * @author jg
 *
 */
public class FileMachine extends StreamMachine {
	
	public FileMachine(DiscreteFileRun currentRun) {
		super(currentRun);
        setDataPort(new FileDataPort(FileIOUtilities.dataDirectory, currentRun.getFileName()));
        setMachineType("File");
	}
	
}
