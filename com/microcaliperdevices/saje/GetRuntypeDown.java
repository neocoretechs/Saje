/**
 * 
 */
package com.microcaliperdevices.saje;

import java.net.Socket;
import com.microcaliperdevices.saje.CoreContainer.ContainerProcessInterface;

/**
 * The function of this module is to set the type of run for further operations
 * @author jg
 *
 */
public final class GetRuntypeDown implements ContainerProcessInterface {
	private static GetRuntypeDown instance=null;
	private GetRuntypeDown() {}
	public static GetRuntypeDown getInstance() {
		if( instance == null ) {
			instance = new GetRuntypeDown();
		}
		return instance;
	}

	@Override
	public void processAndRespond(Socket os) throws Exception {
		MessageTransport mt = new MessageTransport();
		mt.message.add(SetRuntypeUp.getRuntype());
		//String xml = mt.toXml();
		CoreContainer.putDataStream(os, mt.toXml().getBytes());
	}

	public static int getPort() {
		return 4457;
	}
	
}
