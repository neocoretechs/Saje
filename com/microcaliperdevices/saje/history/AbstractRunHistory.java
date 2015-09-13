/**
 * 
 */
package com.microcaliperdevices.saje.history;

import java.io.IOException;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import javax.xml.bind.annotation.XmlElementRef;
import com.microcaliperdevices.saje.run.AbstractRun;


/**
 * @author jg
 *
 */
public abstract class AbstractRunHistory {

	private static final int MAX_HISTORY_ENTRIES = 100;

	public abstract String toXml();

	public abstract AbstractRunHistory fromXml(String xml);

	public abstract void saveState() throws IOException;

	public abstract void loadHistory() throws IOException;
	
	public abstract String getHistoryFile();

	protected List<AbstractRunHistoryEntry> hTable;
	protected static DateFormat f = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH);

	@XmlElementRef
	public List<AbstractRunHistoryEntry> getHtable() {
	        return hTable;
	}

	public int getHistoryTableRowCount() {
		return hTable.size();
	}

	/**
	 * 
	 */
	public AbstractRunHistory() {}

	public abstract AbstractRunHistoryEntry addHistoryRow(AbstractRun currentRun) throws IOException;
   	public abstract void updateHistoryRow(AbstractRun currentRun, AbstractRunHistoryEntry arhe, int group) throws IOException;
	
	protected void adjustHistorySize() {
		if( hTable.size() > MAX_HISTORY_ENTRIES) {
			hTable.remove(hTable.size()-1);
		}
	}
	/**
	 * Set s to indata, existingDecPoints to 0
	 * Process indata, if align == "R" see if decimal point in indata,
	 * if not check numDecPoints > 0, if numDecPoints > 0 add decimal point to s, dotLoc = indata + length+1
	 * else dotloc = indata.length.  
	 * if dotLoc != 0, that is, there was a decimal point, set existingDecPoints to s.Len - position of float dotLoc.
	 * if !(maxSize - s.Length - numDecPoints + existingDecPoints < 1)
	 * s = Space(new Integer(maxSize - s.Length - numDecPoints + existingDecPoints)) + s,
	 * then if s.Len >= maxSize
	 * return s.substr(0,maxSize) else return s + Space(numDecPoints - existingDecPoints)
	 * ---
	 * If align is not "R" either trim to maxSize or add spaces to end to space out to maxSize: 
	 * if (s.Length >= maxSize) return s.substring(0, maxSize);
	 * else return s + Space(maxSize - s.length);
	 * @param align
	 * @param maxSize
	 * @param numDecPoints
	 * @param indata
	 * @return
	 */
	public String fixField(String align, int maxSize, int numDecPoints, String indata) {
	     int dotLoc = 0;
	     int existingDecPoints = 0;
	     String s = "";
	
	     s = indata;
	     if (align == "R")
	     {
	         dotLoc = indata.indexOf(".") + 1;
	         if (dotLoc == 0)
	         {
	             if (numDecPoints > 0)
	             {
	                 s = indata + ".";
	                 dotLoc = indata.length() + 1;
	             }
	             else
	             {
	                 dotLoc = indata.length();
	             }
	         }
	         else
	         {
	             existingDecPoints = s.length() - dotLoc;
	         }
	         if ( !(maxSize - s.length() - numDecPoints + existingDecPoints < 1) )
	         {
	             s = Space(maxSize - s.length() - numDecPoints + existingDecPoints) + s;
	         }
	         if (s.length() >= maxSize)
	         {
	             return s.substring(0, maxSize);
	         }
	         else
	         {
	             return (s + Space( (int)(numDecPoints - existingDecPoints)) );
	         }
	     }
	
	     if (s.length() >= maxSize)
	     {
	             return s.substring(0, maxSize);
	     }
	     return (s + Space(maxSize - s.length()));
	 }

	/**
	  * Create a line of spaces
	  * @param numSpaces
	  * @return
	  */
	private String Space(int numSpaces) {
	     StringBuffer output = new StringBuffer();
	     int cnt = 0;
	     while (cnt < numSpaces)
	     {
	         output.append(" ");
	         ++cnt;
	     }
	     return output.toString();
	 }


}