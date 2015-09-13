package com.microcaliperdevices.saje.run;

import java.io.IOException;

import com.microcaliperdevices.saje.SetRuntypeUp;
import com.microcaliperdevices.saje.config.AbstractRunConfig;
import com.microcaliperdevices.saje.config.ContinuousRunConfig;
import com.microcaliperdevices.saje.io.file.DirectoryNotFoundException;
import com.microcaliperdevices.saje.io.machine.AbstractMachine;
import com.microcaliperdevices.saje.io.machine.PreProcessInterface;
import com.microcaliperdevices.saje.license.License;

public class CyclicStreamRun extends CyclicRun {

	/**
	 * @param lic
	 * @throws IOException
	 * @throws DirectoryNotFoundException
	 */
	public CyclicStreamRun(License lic, AbstractRunConfig arc) throws IOException, DirectoryNotFoundException {
		super(lic, arc);
		// mod machine type to reflect file use
		SetRuntypeUp.setDataSource("Stream");
	}
}
