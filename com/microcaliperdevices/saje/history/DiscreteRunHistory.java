package com.microcaliperdevices.saje.history;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.microcaliperdevices.saje.Props;
import com.microcaliperdevices.saje.io.file.FileIOUtilities;
import com.microcaliperdevices.saje.run.AbstractRun;


	/**
	 * Maintains data related to run history
	 * @author jg
	 *
	 */
	@XmlRootElement(name="runhistory")
	@XmlSeeAlso(DiscreteRunHistoryEntry.class)
    public class DiscreteRunHistory extends AbstractRunHistory
    {
        String[] heads = {"RunDate","Group","Notes"};
    	private final String historyFile = "DiscreteHistory.xml";
        /**
         * 
         * @param currentRun
         */
        public DiscreteRunHistory()
        {
        	super();
        }
        /**
         * Load history data from history file
         * @throws IOException
         */
        @Override
		public void loadHistory() throws IOException
        {
            // we always history at top level default dir so dont use path
            if (new File(getHistoryFile()).exists())
            {
            	// we always history at top level default dir so dont use path
            	String xml = FileIOUtilities.read(getHistoryFile());
            	AbstractRunHistory crh = fromXml(xml);
            	hTable = crh.getHtable();
            } else {
            	hTable =(List<AbstractRunHistoryEntry>) new ArrayList<AbstractRunHistoryEntry>();
            }
        }
        
    	@XmlElement(name = "historyentries")
    	public List<AbstractRunHistoryEntry> getHtable() {
    	        return hTable;
    	}
    	
    	/**
    	* Use currentRun to extract stats and place them in a new history row
    	* @return 
    	* 
    	*/
    	public DiscreteRunHistoryEntry addHistoryRow(AbstractRun currentRun, boolean before) throws IOException {
    	    DiscreteRunHistoryEntry newRow = new DiscreteRunHistoryEntry();
    	    adjustHistorySize();
    	    if( before ) 
    	    	hTable.add(0,newRow); // push down
    	    else
    	    	hTable.add(newRow);
    	    return newRow;
    	}
    	
      	public AbstractRunHistoryEntry addHistoryRow(AbstractRun currentRun) throws IOException {
      		return addHistoryRow(currentRun, true);
      	}
       	/**
    	* Use currentRun to extract stats and place them in a new history row
    	* @return 
    	* 
    	*/
    	public void updateHistoryRow(AbstractRun currentRun, AbstractRunHistoryEntry arhe, int group) throws IOException {
    	    arhe.setRunDate(currentRun.getRunDate());       
    	    ((DiscreteRunHistoryEntry)arhe).setGroup(String.valueOf(group));
    	    ((DiscreteRunHistoryEntry)arhe).setNotes("");
    	}
    	
      	//public void updateHistoryRow(AbstractRun currentRun) throws IOException {
      	//	updateHistoryRow(currentRun, 0);
      	//}
        /**
         * Save the contents of hData (History) to file 'history'
         * @throws IOException 
         */
        @Override
		public void saveState() throws IOException
        {
            // we always history at top level default dir so dont use path
            FileIOUtilities.write(getHistoryFile(), toXml());
        }
        @Override
		public AbstractRunHistory fromXml(String xml) {
			StringReader reader = new StringReader(xml);
			JAXBContext context;
			try {
				context = JAXBContext.newInstance(DiscreteRunHistory.class, DiscreteRunHistoryEntry.class);
				Unmarshaller m = context.createUnmarshaller();
				return (AbstractRunHistory)m.unmarshal(reader);
			} catch (JAXBException /*| SAXException*/ e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		public String toXml() {
			StringWriter writer = new StringWriter();
			JAXBContext context;
			try {
				context = JAXBContext.newInstance(DiscreteRunHistory.class, DiscreteRunHistoryEntry.class);
				Marshaller m = context.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
				m.marshal(this, writer);
			} catch (JAXBException e) {
				e.printStackTrace();
			}
			if( Props.DEBUG ) System.out.println("History: "+writer.toString());
			return writer.toString();
		}
		
		public static void main(String[] args) throws Exception {
			DiscreteRunHistory crh = new DiscreteRunHistory();
		    DiscreteRunHistoryEntry newRow = new DiscreteRunHistoryEntry();
		    //newRow.setAverage("1.0");
		    newRow.setGroup("1");
		    newRow.setRunDate("1/1/13");
		    newRow.setNotes("notee");
		    //newRow.setStandardDeviation("1.1");
		    crh.getHtable().add(newRow);
			if( Props.DEBUG ) System.out.println(crh.toXml());
		}
		/* (non-Javadoc)
		 * @see com.microcaliperdevices.saje.history.AbstractRunHistory#getHistoryFile()
		 */
		@Override
		public String getHistoryFile() {
			return historyFile;
		}
        
    }
