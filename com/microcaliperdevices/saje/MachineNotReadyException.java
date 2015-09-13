package com.microcaliperdevices.saje;

import java.io.IOException;

@SuppressWarnings("serial")
public class MachineNotReadyException extends IOException {

	public MachineNotReadyException(String string) {
		super(string);
	}
	
	public MachineNotReadyException() {
			    super("Either a cable is not attached or\n" + 
                      "your machine is turned off or\n" + 
                      "its control switch is not in the 'Run' position.\n\n");
	}

}
