package com.microcaliperdevices.saje.license;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.util.Locale;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.microcaliperdevices.saje.Props;
import com.microcaliperdevices.saje.io.file.FileIOUtilities;

/**
 * Object representing a license to use the product
 * @author jg
 * Copyright 2012,2014 Microcaliper Devices, LLC
 */
@XmlRootElement
public final class License {
	//instance variables
	public String company;
	public String spackage;
	public String expires;
	@XmlTransient
	public String license;
	 
    static DateFormat f = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH);
	private static final byte[] pork = {'H','a','R','d','A','r','t','I','c','H','o','K','E','s','r','A','R','E','l','y','k','E','E','p'};

	public License() throws IOException, LicenseFailException {
		String indata = null;
		String fname = FileIOUtilities.dataDirectory + "sa.license";
		//if( Props.DEBUG ) System.out.println("License file="+fname);

		try {
			FileReader fre = new FileReader(fname);
			BufferedReader fr = new BufferedReader(fre);
			indata = fr.readLine();
			company = indata.substring(indata.indexOf("=") + 1);
			indata = fr.readLine();
			spackage = indata.substring(indata.indexOf("=") + 1);
			indata = fr.readLine();
			expires = indata.substring(indata.indexOf("=") + 1);
			//indata = fr.readLine();
			//serial = indata.substring(indata.indexOf("=") + 1);
			indata = fr.readLine();
			license = indata.substring(indata.indexOf("=") + 1);
			fr.close();
		} catch(FileNotFoundException fnfe) {
			throw new LicenseFailException(fnfe.getMessage());
		}
		
		String info = InfoReader.readInfo();
		String os = System.getProperty("os.name");
		//if( !info.equals(serial)) {
		//	throw new LicenseFailException("Software attachment error! - L990");
		//}
 
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IOException(e);
		}
		String chop = new String(pork);
		
		//if( Props.DEBUG ) System.out.println(company);
		byte[] bytesOfMessage0 = (info+chop).getBytes("UTF-8");
		byte[] bytesOfMessage1 = (company+chop).getBytes("UTF-8");
		byte[] bytesOfMessage2 = (spackage+chop).getBytes("UTF-8");
		byte[] bytesOfMessage3 = (expires+chop).getBytes("UTF-8");
		byte[] bytesOfMessage4 = (os+chop).getBytes("UTF-8");
		md.update(bytesOfMessage0);
		md.update(bytesOfMessage1);
		md.update(bytesOfMessage2);
		md.update(bytesOfMessage3);
		md.update(bytesOfMessage4);
		byte[] thedigest = md.digest();
		StringBuilder sb = new StringBuilder();
	    for (byte b : thedigest) {
	        sb.append(String.format("%02X", b));
	    }
		String licenseDerive = sb.toString();
		//if( Props.DEBUG ) System.out.println(companyx);

		byte[] licenseb = license.getBytes();
		byte[] derivedb = licenseDerive.getBytes();
		if( derivedb.length != licenseb.length ) {
			//if( Props.DEBUG ) System.out.println("Lengths differ deriv="+derivedb.length+" orig="+licenseb.length);
			throw new IOException("License verification fault");
		}
		for(int j = 0 ; j < derivedb.length; j++) {
			if( derivedb[j] != licenseb[j] ) {
				//if( Props.DEBUG ) System.out.println("Discrepency at pos "+j);
				throw new LicenseFailException("License verification fault");
			}
		}
		if (!expires.equals("none")) {
			Thread t = new Watch(Long.parseLong(expires));
			t.setDaemon(false);
			t.start();
		}

	}

	public String toXml() {
		StringWriter writer = new StringWriter();
		JAXBContext context;
		try {
			context = JAXBContext.newInstance(License.class);
			Marshaller m = context.createMarshaller();
			m.marshal(this, writer);
		} catch (JAXBException e) {
		}
		//if( Props.DEBUG ) System.out.println(writer);
		return writer.toString();
	}
	
	public static void main(String[] args) throws Exception {
		License lic = new License();
		if( Props.DEBUG ) System.out.println(lic.toXml());
	}


	/**
	 * @return
	 */
	public String getFirstMachineType() {
		return "Mega2560";
	}
	
}
