/**
 * 
 */
package com.microcaliperdevices.saje.history;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author jg
 *
 */
@XmlRootElement
public abstract class AbstractRunHistoryEntry {
	private String runDate;
	private String notes;
	
	@XmlElement
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	@XmlElement
	public String getRunDate() {
		return runDate;
	}
	public void setRunDate(String runDate) {
		this.runDate = runDate;
	}
}
