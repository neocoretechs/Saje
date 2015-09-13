/**
 * 
 */
package com.microcaliperdevices.saje;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author jg
 *
 */
@XmlRootElement(name="MessageTransport")
@XmlAccessorType(XmlAccessType.FIELD)
public class MessageTransport {
	
	public List<String> message = new ArrayList<String>();
	
	public MessageTransport() {}
	
	public static List<String> fromXml(String xml) {
		if( Props.DEBUG ) System.out.println("Converting to message: "+xml);
		StringReader reader = new StringReader(xml);
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(MessageTransport.class);
			Unmarshaller m = context.createUnmarshaller();
			return ((MessageTransport)m.unmarshal(reader)).message;
		} catch (JAXBException /*| SAXException*/ e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String toXml() {
		StringWriter writer = new StringWriter();
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(MessageTransport.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(this, writer);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		if( Props.DEBUG ) System.out.println("Message: "+writer.toString());
		return writer.toString();
	}
	
	public static void main(String[] args) throws Exception {
		MessageTransport mt = new MessageTransport();
		mt.message.add("foo");
		mt.message.add("1");
		mt.message.add("Barbitchuate");
		String xml = mt.toXml();
		for(String xs : MessageTransport.fromXml(xml)) {
			if( Props.DEBUG ) System.out.println(xs);
		}
	}

	
}
