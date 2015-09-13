package com.microcaliperdevices.saje.io.machine;

import java.io.IOException;

public interface ContinuousInterface extends MachineInterface {
    	void waitReady() throws IOException;
}
