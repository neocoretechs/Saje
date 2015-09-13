/**
 * 
 */
package com.microcaliperdevices.saje.io.machine;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import com.microcaliperdevices.saje.ThreadPoolManager;
import com.microcaliperdevices.saje.io.machine.bridge.MachineBridge;
import com.microcaliperdevices.saje.io.machine.bridge.MachineReading;

/**
 * Motor control status returned as a string describing condition
 * @author jg
 *
 */
public class MotorFaultListener implements Runnable {
	public static ArrayBlockingQueue<String> data = new ArrayBlockingQueue<String>(1024);
	public static int deleteThreshold = 24; // number of readings before clear, 8 possible fields in response
	private static MotorFaultListener instance = null;
	public static MotorFaultListener getInstance() {
		if( instance == null )
			instance = new MotorFaultListener();
		return instance;
	}
	public MotorFaultListener() {
		ThreadPoolManager.getInstance().spin(this, "motorfault");
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while(true) {
			ThreadPoolManager.getInstance().waitGroup("motorfault");
				if( data.size() > deleteThreshold ) {
					for(int i = 0; i < deleteThreshold; i++)
						if( data.size() > 0) data.remove();
				}
				try {
					// put everything on the publish queue
					/*
					List<MachineReading> lim = MachineBridge.getInstance("motorfault").get();
					synchronized(lim) {
						for(int i = 0; i < lim.size(); i++) {
							if( lim.get(i) != null) {
								String sdata = lim.get(i).getReadingValString();
								if( sdata != null )
									data.add(sdata);
							}
						}
					}
					*/
					MachineReading mr = MachineBridge.getInstance("motorfault").take();
					if( mr != null ) {
						String sdata = mr.getReadingValString();
						if( sdata != null )
							data.add(sdata);
					}
				} catch(IndexOutOfBoundsException ioobe) {}
		}
	}

}
