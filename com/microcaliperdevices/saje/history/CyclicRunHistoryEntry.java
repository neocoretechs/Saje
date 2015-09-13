/**
 * 
 */
package com.microcaliperdevices.saje.history;

import com.microcaliperdevices.saje.cyclic.Cycle;

/**
 * @author jg
 *
 */
public class CyclicRunHistoryEntry extends ContinuousRunHistoryEntry {
	private String cyclicIndex;
	private Cycle cycle; 
	   
	public String getCyclicIndex() {
		return cyclicIndex;
	}

	public void setCyclicIndex(String washboardIndex) {
		this.cyclicIndex = washboardIndex;
	}

	public Cycle getCycle() {
		return cycle;
	}

	public void setCycle(Cycle cycle) {
		this.cycle = cycle;
	}
}