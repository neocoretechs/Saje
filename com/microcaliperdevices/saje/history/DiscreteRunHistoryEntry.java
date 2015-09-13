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
public class DiscreteRunHistoryEntry extends AbstractRunHistoryEntry {

	private String group;
    
	@XmlElement
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}

}
