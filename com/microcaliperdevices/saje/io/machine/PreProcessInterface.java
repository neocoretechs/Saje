/**
 * 
 */
package com.microcaliperdevices.saje.io.machine;


/**
 * For those data sources which have at their beginning a set of data which may include configuration etc
 * we can implement this interface for a particular type of run and do preprocessing for that run.
 * An example is a StreamMachine, which takes a file and reads the config at the beginning and then processes the raw data
 * so for stream runs we implement this such that the machine can call it befor eits main data acquisition loop.  
 * @author jg
 *
 */
public interface PreProcessInterface {
	public int preProcess(AbstractMachine machine);
}
