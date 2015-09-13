package com.microcaliperdevices.saje.license;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import com.microcaliperdevices.saje.Props;
import com.microcaliperdevices.saje.io.file.FileIOUtilities;

/**
 * @author jg
 *
 */
public final class Watch extends Thread {
	private static long systime = System.currentTimeMillis();
	private static long eltime;
	private static long exptime = 0;
	private static String seltime;
	static final byte[] pork = {'N','o','R','W','e','I','g','a','N','e','l','E','p','H','a','N','t','s','S','i','N','G'};
	protected static boolean shouldRun = true;
	private static boolean isRunning = false;
	private static long timeUnitsToMillis = 1000; // const to conv whatever units are being used to millis, 1000 = seconds
	
	public Watch(long exptime) {
		this.exptime = exptime;
		if( Props.DEBUG ) System.out.println("Bringing up watch with exry="+exptime);
	}
	public boolean watching() {
		return isRunning;
	}
	public void run() {
		String smd = null, seltime = null;
		if(isRunning)
			return; // failsafe
		isRunning= true;
		while(shouldRun) {
			String fname = FileIOUtilities.dataDirectory + "sa.elapse";
			String indata;
			try {
				FileReader fre = new FileReader(fname);
				BufferedReader fr = new BufferedReader(fre);
				indata = fr.readLine();
				seltime = indata.substring(indata.indexOf("=") + 1);
				indata = fr.readLine();
				smd = indata.substring(indata.indexOf("=") + 1);
				fr.close();
			} catch(IOException fnfe) {
				System.exit(911);
			}
	 
			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				System.exit(912);
				
			}
			String chop = new String(pork);
			
			if( Props.DEBUG ) System.out.println("Current elapsed="+seltime);
			if( Props.DEBUG ) System.out.println("Current md5="+smd);
			
			byte[] bytesOfMessage = null;
			try {
				bytesOfMessage = (seltime+chop).getBytes("UTF-8");
			} catch (UnsupportedEncodingException e1) {
				System.exit(913);
			}
			byte[] thedigest = md.digest(bytesOfMessage);
			StringBuilder sb = new StringBuilder();
		    for (byte b : thedigest) {
		        sb.append(String.format("%02X", b));
		    }
			String seltimex = sb.toString();
			if( Props.DEBUG ) System.out.println("Computed md5="+seltimex);
			if( !seltimex.equals(smd) ) { // hash no match
				System.exit(911);
			}
			// update time
			eltime = Long.parseLong(seltime);
			long meltime = eltime * timeUnitsToMillis ; // conv to millis
			// get current runtime span for this module
			long runtime = System.currentTimeMillis() - systime;
			// and add it to our elapsed time from file, then divide by units so we have same range of vals as from file
			meltime = (long) Math.floor( ( meltime + runtime ) / (double)timeUnitsToMillis );
			if( Props.DEBUG ) System.out.println("Computed elapsed="+meltime);
			// if they wind up the same we have not yet reached an interval so we can go
			// dont update systime as our interval is in progress
			if( meltime == eltime ) {
				try {
					Thread.sleep(timeUnitsToMillis);
				} catch (InterruptedException e) {}
				if( Props.DEBUG ) System.out.println("Times are the same, continue");
				continue;
			}
			// regen the file before the check, so you cant just keep restarting it on long duration check intervals
			try {
				FileWriter fre = new FileWriter(fname);
				BufferedWriter fr = new BufferedWriter(fre);
				fr.write(String.valueOf(meltime)+"\n");
				bytesOfMessage = null;
				try {
					bytesOfMessage = (meltime+chop).getBytes("UTF-8");
				} catch (UnsupportedEncodingException e1) {
					System.exit(913);
				}
				thedigest = md.digest(bytesOfMessage);
				sb = new StringBuilder();
			    for (byte b : thedigest) {
			        sb.append(String.format("%02X", b));
			    }
				fr.write(sb.toString()+"\n");
				fr.flush();
				fr.close();
			} catch(IOException fnfe) {
				System.exit(911);
			}
			if( meltime > exptime ) {
				if( Props.DEBUG ) System.out.println("Saje license has now expired...");
				System.exit(900);
			}
			// reset this module runtime because its logged
			systime = System.currentTimeMillis();
			try {
				Thread.sleep(timeUnitsToMillis);
			} catch (InterruptedException e) {}
		}
		isRunning = false;
	}
	
	public static void main(String[] args) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
		}
		String chop = new String(pork);	
		byte[] bytesOfMessage = null;
		try {
			bytesOfMessage = (args[0]+chop).getBytes("UTF-8");
		} catch (UnsupportedEncodingException e1) {
		}
		byte[] thedigest = md.digest(bytesOfMessage);
		StringBuilder sb = new StringBuilder();
	    for (byte b : thedigest) {
	        sb.append(String.format("%02X", b));
	    }
		String seltimex = sb.toString();
		if( Props.DEBUG ) System.out.println(seltimex);
	}
}
