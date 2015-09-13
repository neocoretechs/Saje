package com.microcaliperdevices.saje;

import java.net.*;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormat;
import java.util.Locale;
import java.io.*;

import com.microcaliperdevices.saje.config.AbstractRunConfig;
import com.microcaliperdevices.saje.config.BaseMachineConfig;
import com.microcaliperdevices.saje.history.AbstractRunHistory;
import com.microcaliperdevices.saje.io.machine.AsynchDemuxer;

import com.microcaliperdevices.saje.ros.Pubs;
import com.microcaliperdevices.saje.run.AbstractRun;
/**
 * Truly the heart of RoboCore, this is the standalone container that performs the majority of processing and
 * exchanges data with UI web server via XML. Each thread represents a waiting port that performs context-specific
 * processing when that port is accessed, thus foregoing command processing logic al la REST. The logic here encapsulates
 * that which is best served outside of web container sandbox. Those modules participating in this container implement the
 * ContainerProcessInterface method processAndRespond and provide a getPort method to indicate which port they listen on.
 * @author jg
 *
 */
public final class CoreContainer {
    	static DateFormat f = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH);
		private static AbstractRun currentRun = null;
		private static AbstractRunConfig currentConfig = null;
		private static AbstractRunHistory history = null;
		private static AsynchDemuxer asynchDemuxer;
		private static boolean isConfigValid = true;
		private static String whyInvalid = "";
		
		public static AbstractRun getCurrentRun() {
			return currentRun;
		}
		public static void setCurrentRun(AbstractRun currentRun) {
			CoreContainer.currentRun = currentRun;
		}

		protected static AbstractRunConfig getCurrentConfig() {
			return currentConfig;
		}
		
		public static AbstractRunHistory getHistory() {
			return history;
		}
		public static void setHistory(AbstractRunHistory history) {
			CoreContainer.history = history;
		}

		protected static void setCurrentConfig(AbstractRunConfig currentConfig) {
			CoreContainer.currentConfig = currentConfig;
		}		

		protected interface ContainerProcessInterface {
			public void processAndRespond(Socket os) throws Exception;
		}
		/**
		 * Check the license and start the server threads
		 * @param args
		 * @throws Exception
		 */
	    public static void main(String[] args) throws Exception {
	    	// start publishers of data from attached mega
	    	if( args.length < 2 ) {
	    		System.out.println("usage: java com.microcaliperdevices.saje.CoreContainer __ip:=<hostThisNode> __master:=<master host>");
	    		return;
	    	}
	    	
	    	CoreContainer sc = new CoreContainer();
	    	sc.startServer(LicenseDown.getInstance(), LicenseDown.getPort());
	    	// If the license server didnt fly, dont start the rest
	    	if(LicenseDown.getInstance().isFail()) {
	    		throw new Exception("License issue caused server start abort...");
	    	}
	    	// start the ROS publisher
	    	new Pubs(args);//new Pubs(args[0],new InetSocketAddress(args[1],Integer.valueOf(args[2])));
	    	
	    	BaseMachineConfig.getInstance().getBaseMachineConfig(LicenseDown.getInstance().getLicense());
	    	// start demuxer
	    	asynchDemuxer = AsynchDemuxer.getInstance();
	    	sc.startServer(ConfigUp.getInstance(), ConfigUp.getPort());
	    	sc.startServer(ConfigDown.getInstance(), ConfigDown.getPort());
	    	//sc.startServer(ReadMachineData.getInstance(), ReadMachineData.getPort());
	    	sc.startServer(RunDataDown.getInstance(), RunDataDown.getPort());
	    	sc.startServer(RunDataUp.getInstance(), RunDataUp.getPort());
	    	sc.startServer(SetSubsetUp.getInstance(), SetSubsetUp.getPort());
	    	sc.startServer(GetSubsetDown.getInstance(), GetSubsetDown.getPort());
	    	sc.startServer(IsValidConfigDown.getInstance(), IsValidConfigDown.getPort());
	    	sc.startServer(RunFileDown.getInstance(), RunFileDown.getPort());
	    	sc.startServer(HistoryUp.getInstance(), HistoryUp.getPort());
	    	sc.startServer(HistoryDown.getInstance(), HistoryDown.getPort()); 
	    	sc.startServer(SetRuntypeUp.getInstance(), SetRuntypeUp.getPort()); 
	    	sc.startServer(GetRuntypeDown.getInstance(), GetRuntypeDown.getPort());
	    	//sc.startServer(StreamMachineData.getInstance(),  StreamMachineData.getPort());
	    	sc.startServer(GCodeUp.getInstance(), GCodeUp.getPort());
	    	Date date = new Date();
	    	Calendar cal = Calendar.getInstance();
	    	cal.setTime(date);
	    	int hour = cal.get(Calendar.HOUR_OF_DAY);
	    	int minute = cal.get(Calendar.MINUTE);
	    	// configure with startup params
	    	asynchDemuxer.config();

	    	if( Props.DEBUG ) System.out.println("..."+f.format(date)+" All RoboCore Container Services Bootstrapped @ "+hour+":"+minute+"...");
	    }
	    
		/**
		 * In order to use proper overload xml conversion we create instance from factory
		 * and use fromXml on that
		 * @param xml
		 * @throws NotSupportedException
		 */
		public static void setConfigFromXml(String xml) throws NotSupportedException {
			if( Props.DEBUG ) System.out.println("Setting config "+xml);
			//AbstractConfigFactory acf = AbstractConfigFactory.createFactory(LicenseDown.getInstance().getMachineType());
			//AbstractRunConfig arc = acf.createConfig();
			AbstractRunConfig varc = (AbstractRunConfig) currentConfig.fromXml(xml);
			varc.validate();
			isConfigValid = varc.isValid();
			whyInvalid = varc.whyInvalid();
			if( isConfigValid )
				setCurrentConfig(varc);
		}
		
		public static String getWhyInvalid() {
			if( isConfigValid )
				return "";
			return whyInvalid;
		}
	    /**
	     * Method reads from instream and outputs to output target
	     * @param fis
	     * @param fos
	     * @throws IOException
	     */
		protected static void getDataStream(InputStream fis, OutputStream fos) throws IOException {
			byte[] filebuf = new byte[1024];
			int in = 1024;
			//if( Props.DEBUG ) System.out.println("avail="+fis.available());
			while( (in = fis.read(filebuf)) != -1 ) {
				//if( Props.DEBUG ) System.out.println("in="+in+" avail="+fis.available());
				fos.write(filebuf, 0, in);
			}
			fis.close();
			fos.flush();
			fos.close();
		}
		
		protected static void putDataStream(Socket clientSocket, byte[] load) throws IOException {
			OutputStream os = null;
			os = clientSocket.getOutputStream();
			ByteArrayInputStream bload = new ByteArrayInputStream(load);
			getDataStream(bload, os);
		}
		/**
		 * From the server, get the client socket and payload
		 * @param clientSocket
		 * @return The byte array of client data up
		 */
		protected static byte[] getDataStream(Socket clientSocket) {
			InputStream in = null;
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try {
					in = clientSocket.getInputStream();
					getDataStream(in, baos);
			} catch (IOException e) {
					e.printStackTrace();
			}
			return baos.toByteArray();
		}
		
		public static class LocalClient {
			private int port;
			public LocalClient(int port) {
				this.port = port;
			}
			public String get() {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try {
					Socket s = new Socket("localhost",port);
					InputStream sis = s.getInputStream();
					CoreContainer.getDataStream(sis, baos);
					s.close();
				} catch(IOException ioe) {
					if( Props.DEBUG ) System.out.println("Can't read from socket to RoboCore Container process due to "+ioe);	
				} 
				return new String(baos.toByteArray());
			}
			public Socket getSocket() {
				try {
					return new Socket("localhost",port);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
			public void put(String xml) {
				ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
				try {
					Socket s = new Socket("localhost",port);
					OutputStream sos = s.getOutputStream();
					CoreContainer.getDataStream(bais, sos);
					s.close();
				} catch(IOException ioe) {
					if( Props.DEBUG ) System.out.println("Can't write to socket on RoboCore process due to "+ioe);
				}
			}
		}

		public static class RemoteClient {
			private String host;
			private int port;
			public RemoteClient(String host, int port) {
				this.host = host;
				this.port = port;
			}
			public String get() {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try {
					Socket s = new Socket(host,port);
					InputStream sis = s.getInputStream();
					CoreContainer.getDataStream(sis, baos);
					s.close();
				} catch(IOException ioe) {
					if( Props.DEBUG ) System.out.println("Can't read from socket to RoboCore Container process due to "+ioe);	
				} 
				return new String(baos.toByteArray());
			}
			public Socket getSocket() {
				try {
					return new Socket(host,port);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
			public void put(String xml) {
				ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
				try {
					Socket s = new Socket(host,port);
					OutputStream sos = s.getOutputStream();
					CoreContainer.getDataStream(bais, sos);
					s.close();
				} catch(IOException ioe) {
					if( Props.DEBUG ) System.out.println("Can't write to socket on RoboCore process due to "+ioe);
				}
			}
		}
		/**
		 * Start a thread to collect remote data issued over socket, place
		 * in bounded array which can be collected by calls
		 * @author jg
		 *
		 */
		public static class StreamClient {
			private String host;
			private int port;
			public StreamClient(final String host, final int port) {
				this.host = host;
				this.port = port;
	    		ThreadPoolManager.getInstance().spin( new Runnable() {
		    		public void run() {
		    			ByteArrayOutputStream baos = new ByteArrayOutputStream();
						try {
							Socket s = new Socket(host,port);
							InputStream sis = s.getInputStream();
							while( true ) {
								CoreContainer.getDataStream(sis, baos);
							}
						} catch(IOException ioe) {
							if( Props.DEBUG ) System.out.println("Can't read from socket to RoboCore Container process due to "+ioe);	
						} 
		    		}
		    	}, "SYSTEM");

			}
			public String get() {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try {
					Socket s = new Socket(host,port);
					InputStream sis = s.getInputStream();
					CoreContainer.getDataStream(sis, baos);
					
				} catch(IOException ioe) {
					if( Props.DEBUG ) System.out.println("Can't read from socket to RoboCore Container process due to "+ioe);	
				} 
				return new String(baos.toByteArray());
			}
			public Socket getSocket() {
				try {
					return new Socket(host,port);
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
			public void put(String xml) {
				ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
				try {
					Socket s = new Socket(host,port);
					OutputStream sos = s.getOutputStream();
					CoreContainer.getDataStream(bais, sos);
					s.close();
				} catch(IOException ioe) {
					if( Props.DEBUG ) System.out.println("Can't write to socket on RoboCore process due to "+ioe);
				}
			}
		}
		
		/**
		 * Initial bootup of server for a particular ContainerProcessInterface on a particular port.
		 * Starts a ServerSocket listening and calls 'processAndRespond' for the interface on connections
		 * @param cpi
		 * @param port
		 * @throws Exception
		 */
	    private void startServer(final ContainerProcessInterface cpi, final int port) throws Exception {
	    	//Thread t = new Thread( 
	    		ThreadPoolManager.getInstance().spin( new Runnable() {
	    		public void run() {
	    			Socket clientSocket = null;
	    			ServerSocket serverSocket = null;
	
	    			while(true) {
	    	    		try {
	    	    				serverSocket = new ServerSocket(port);
	    	    		} catch (IOException e) {
	    	    				e.printStackTrace();
	    	    				if( Props.DEBUG ) System.out.println("Could not listen on port:"+port+".");
	    	    				System.exit(1);
	    	    		}
		    			try {
	    					clientSocket = serverSocket.accept();
	    					clientSocket.setTcpNoDelay(true);
	    					//InetAddress csocka = clientSocket.getInetAddress();
	    					//if( csocka == null ) {
	    					//	throw new IOException("inability to determine remote client address, feels insecure");
	    					//}
	    					// Make sure we are not connecting from insecure location outside local network
	    					//if( !csocka.isLoopbackAddress() && !csocka.isLinkLocalAddress() && 
	    					//	!csocka.isAnyLocalAddress() && !csocka.isSiteLocalAddress()) {
	    					//	throw new IOException("possibly malicious external connection request from: "+csocka.getCanonicalHostName());
	    					//}
	    				} catch (IOException e) {
	    					if( Props.DEBUG ) System.out.println("Accept failed due to "+e.getMessage());
	    					System.exit(1);
	    				}
		    			try {
	
	    					cpi.processAndRespond(clientSocket);
	    					
		    			} catch (Exception e) {
							e.printStackTrace();
						} finally {
	    					try {
								serverSocket.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
	    				}
	    			}
	    		}

	    	}, "SYSTEM");

	    	//t.start();
	    }

	}

