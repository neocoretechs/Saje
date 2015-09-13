package com.microcaliperdevices.saje.history;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Date;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import com.microcaliperdevices.saje.Props;
import com.microcaliperdevices.saje.run.AbstractRun;
import com.microcaliperdevices.saje.run.ContinuousRun;

@XmlRootElement(name="runhistory")
@XmlSeeAlso(ContinuousRunHistoryEntry.class)
public class ContinuousRunHistory extends DiscreteRunHistory
{
		private final String historyFile = "ContinuousHistory.xml";
        String[] heads = {"RunDate","Group","Notes"};
        
        public ContinuousRunHistory()
        {
        	super();
        }
        
        public String getHistoryfile() { return historyFile; }
        
        public AbstractRunHistoryEntry addHistoryRow(AbstractRun currentRun) throws IOException
        {      
            ContinuousRunHistoryEntry newRow = new ContinuousRunHistoryEntry();
            adjustHistorySize();
            hTable.add(0,newRow); //push
            return newRow;
        }

        public void setCurrentRunDefaultState(AbstractRun currentRun)
        {
            currentRun.setRunDate(f.format(new Date()));
            currentRun.setActiveGroupNum(1);
            currentRun.getRunConfig().setNumGroups(1);
            currentRun.getRunConfig().setSamplesPerGroup(500);
        }
        
     	/**
    	* Use currentRun to extract stats and place them in a new history row
    	* @return 
    	* 
    	*/
    	public void updateHistoryRow(AbstractRun currentRun, AbstractRunHistoryEntry ahre, int row) throws IOException {
    		super.updateHistoryRow(currentRun, ahre, row);
       	    ((ContinuousRunHistoryEntry)ahre).setAverage(fixField("R", 8, 4, String.valueOf(((ContinuousRun)currentRun).getAverage())));
    	    ((ContinuousRunHistoryEntry)ahre).setStandardDeviation(fixField("R",8,4,String.valueOf(((ContinuousRun)currentRun).getStandardDeviation())));
    	}
      	
        @Override
 		public AbstractRunHistory fromXml(String xml) {
 			StringReader reader = new StringReader(xml);
 			JAXBContext context;
 			try {
 				context = JAXBContext.newInstance(ContinuousRunHistory.class, ContinuousRunHistoryEntry.class);
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
 				context = JAXBContext.newInstance(ContinuousRunHistory.class, ContinuousRunHistoryEntry.class);
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
			ContinuousRunHistory crh = new ContinuousRunHistory();
		    ContinuousRunHistoryEntry newRow = new ContinuousRunHistoryEntry();
		    newRow.setAverage("1.0");
		    newRow.setGroup("1");
		    newRow.setRunDate("1/1/13");
		    newRow.setNotes("notee");
		    newRow.setStandardDeviation("1.1");
		    crh.getHtable().add(newRow);
			crh.fromXml(crh.toXml());
		}
    }
