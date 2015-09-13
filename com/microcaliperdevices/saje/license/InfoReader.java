package com.microcaliperdevices.saje.license;

import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;

import com.microcaliperdevices.saje.Props;

/**
 * @author jg
 *
 */
public final class InfoReader {

	public static String readInfo() throws IOException {
		FileReader fr = new FileReader("/proc/cpuinfo");
		CharBuffer barg = CharBuffer.allocate(512);
		while( fr.read(barg) != -1);
		fr.close();
		String bargs = new String(barg.array());
		int serPos = bargs.indexOf("Serial");
		if( serPos == -1) {
			throw new IOException("Can't find CPU serial number for license verification");
		}
		int colPos = bargs.indexOf(':',serPos)+1;
		if( colPos == -1) {
			throw new IOException("Can't find CPU serial number for license verification");
		}
		String bser = bargs.substring(colPos+1,colPos+17);
		return Long.toHexString(Long.decode("0x"+bser).longValue()); // has effect of removing leading 0
	
	}
	public static void main(String[] args) throws Exception{
		if( Props.DEBUG ) System.out.println(InfoReader.readInfo());
	}
}
