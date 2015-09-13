package com.microcaliperdevices.saje.io.machine.dataport;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.microcaliperdevices.saje.io.file.FileIOUtilities;
/**
 * Functions as any DataPort, but reads from file.
 * Provides raw data to/from and abstracted IO channel.
 * Intent is to read a .prn and extract vals, putting them to the device
 * @author jg
 *
 */
public class FileDataPort extends StreamDataPort {

	public FileDataPort(String filePath, String fileName) {
		super();
		port = FileIOUtilities.dataDirectory + filePath + fileName;
	}
	
	@Override
	public void connect(boolean writeable) throws IOException { 
		this.writeable = writeable;
		//if( Props.DEBUG ) System.out.println("FileDataPort connect");
      	fin = new FileInputStream(port);
        if( writeable )
        	fout = new FileOutputStream(port);
	}

}
