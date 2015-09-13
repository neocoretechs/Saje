package com.microcaliperdevices.saje.io.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.microcaliperdevices.saje.config.BaseMachineConfig;
import com.microcaliperdevices.saje.license.License;

	/**
	 * Copyright Microcaliper Devices, LLC
	 * @author jg
	 *
	 */
	public class FileIOUtilities
	{
        static DateFormat f = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ENGLISH);
        public static final String dataDirectory = "/home/pi/"; // this is our extended partition mount on debian, our bogus dirs on Win for dev
        public static final String portSettingsFile = "rcportsettings.cfg";
        public static final String discreteSettingsFile = "rcdiscrete.cfg";
        public static final String continuousSettingsFile = "rccontinuous.cfg";
        public static final String cyclicSettingsFile = "rccyclic.cfg";
		
        public static String getConfigfileName(String runType) {
        	switch(runType) {
        	case "Discrete":
        		return discreteSettingsFile;
        	case "Continuous":
        		return continuousSettingsFile;
        	case "Cyclic":
        		return cyclicSettingsFile;
        	}
        	return discreteSettingsFile;
		}
        
        public static void createDefaultConfig(String runType) throws IOException {
        	switch(runType) {
        	case "Discrete":
        		createDefaultDiscreteCfg();
        		return;
        	case "Continuous":
         		createDefaultContinuousCfg();
        		return;
        	case "Cyclic":
         		createDefaultCyclicCfg();
        		return;
        	}
		}
    	/**
		 * Generates a default representation of discrete configs
		 * @param filePath
		 * @throws IOException
		 */
        public static void createDefaultDiscreteCfg() throws IOException
        {
            String fileName = dataDirectory + discreteSettingsFile;
            if( new File(fileName).exists() )
            	return;
            FileWriter lstFile = new FileWriter(fileName);
            lstFile.write("["+BaseMachineConfig.getInstance().getMachineType()+"]\r\n");
            lstFile.write("NumGroups=1\r\n");
            lstFile.write("SamplesPerGroup=10\r\n");
            lstFile.write("ReadingsSpace=0.01\r\n");
            lstFile.write("GroupsSpace=0.01\r\n");
            lstFile.write("Baseline=0\r\n");
            lstFile.write("BeginIgnore=0\r\n");
            lstFile.write("EndIgnore=0\r\n");
            lstFile.write("UpperDeviation=1\r\n");
            lstFile.write("LowerDeviation=-10\r\n");
            lstFile.write("UpperReject=1\r\n");
            lstFile.write("LowerReject=-10\r\n");
            lstFile.write("Cutoff=.35\r\n");
            lstFile.flush();
            lstFile.close();
        }
		/**
		 * Generates a default representation of discrete configs
		 * @param filePath
		 * @throws IOException
		 */
        public static void createDefaultDiscreteDataFile() throws IOException
        {
            String fileName = dataDirectory + discreteSettingsFile;
            FileWriter lstFile = new FileWriter(fileName);
            lstFile.write("[SETTINGS]\r\n");
            lstFile.write("RunDate="+f.format(new Date())+"\r\n");
            lstFile.write("NumGroups=1\r\n");
            lstFile.write("SamplesPerGroup=10\r\n");
            lstFile.write("ReadingsSpace=0.01\r\n");
            lstFile.write("GroupsSpace=0.01\r\n");
            lstFile.write("Baseline=0\r\n");
            lstFile.write("BeginIgnore=0\r\n");
            lstFile.write("EndIgnore=0\r\n");
            lstFile.write("UpperDeviation=1\r\n");
            lstFile.write("LowerDeviation=-10\r\n");
            lstFile.write("UpperReject=1\r\n");
            lstFile.write("LowerReject=-10\r\n");
            lstFile.write("Cutoff=.35\r\n");
            lstFile.write("[FIELDS]\r\n");
            lstFile.write("[GROUP1]\r\n");
            lstFile.write("1=1.0\r\n");
            lstFile.write("2=1.0\r\n");
            lstFile.write("3=1.0\r\n");
            lstFile.write("4=1.0\r\n");
            lstFile.write("5=1.0\r\n");
            lstFile.write("6=1.0\r\n");
            lstFile.write("7=1.0\r\n");
            lstFile.write("8=1.0\r\n");
            lstFile.write("9=1.0\r\n");
            lstFile.write("10=1.0\r\n"); 
            lstFile.flush();
            lstFile.close();
        }

        
      	/**
      	 * Generates a default representation of discrete configs
    	 * @param filePath
    	 * @throws IOException
   		 */
         public static void createDefaultContinuousCfg() throws IOException  {
            String fileName = dataDirectory + continuousSettingsFile;
            if( new File(fileName).exists() )
            	return;
            FileWriter lstFile = new FileWriter(fileName);
            lstFile.write("["+BaseMachineConfig.getInstance().getMachineType()+"]\r\n");
            lstFile.write("NumGroups=1\r\n");
            lstFile.write("SamplesPerGroup=10\r\n");
            lstFile.write("ReadingsSpace=0.01\r\n");
            lstFile.write("GroupsSpace=0.01\r\n");
            lstFile.write("BeginIgnore=0\r\n");
            lstFile.write("EndIgnore=0\r\n");
            lstFile.write("Baseline=0\r\n");
            lstFile.write("UpperDeviation=1\r\n");
            lstFile.write("LowerDeviation=-10\r\n");
            lstFile.write("UpperReject=1\r\n");
            lstFile.write("LowerReject=-10\r\n");
            lstFile.write("Cutoff=.35\r\n");
            lstFile.flush();
            lstFile.close();
        }
         
     	/**
       	 * Generates a default representation of discrete configs
     	 * @param filePath
     	 * @throws IOException
    	 */
          public static void createDefaultCyclicCfg() throws IOException  {
             String fileName = dataDirectory + cyclicSettingsFile;
             if( new File(fileName).exists() )
             	return;
             FileWriter lstFile = new FileWriter(fileName);
             lstFile.write("["+BaseMachineConfig.getInstance().getMachineType()+"]\r\n");
             lstFile.write("NumGroups=1\r\n");
             lstFile.write("SamplesPerGroup=10\r\n");
             lstFile.write("ReadingsSpace=0.01\r\n");
             lstFile.write("GroupsSpace=0.01\r\n");
             lstFile.write("BeginIgnore=0\r\n");
             lstFile.write("EndIgnore=0\r\n");
             lstFile.write("Baseline=0\r\n");
             lstFile.write("UpperDeviation=1\r\n");
             lstFile.write("LowerDeviation=-10\r\n");
             lstFile.write("UpperReject=1\r\n");
             lstFile.write("LowerReject=-10\r\n");
             lstFile.write("Cutoff=.35\r\n");
             lstFile.flush();
             lstFile.close();
         }
    	/**
		 * Generates a default representation of discrete configs
		 * @param filePath
		 * @throws IOException
		 */
        public static void createDefaultContinuousDataFile() throws IOException
        {
            String fileName = dataDirectory + continuousSettingsFile;
            FileWriter lstFile = new FileWriter(fileName);
            lstFile.write("[SETTINGS]\r\n");
            lstFile.write("RunDate="+f.format(new Date())+"\r\n");
            lstFile.write("NumGroups=1\r\n");
            lstFile.write("SamplesPerGroup=10\r\n");
            lstFile.write("ReadingsSpace=0.01\r\n");
            lstFile.write("GroupsSpace=0.01\r\n");
            lstFile.write("BeginIgnore=0\r\n");
            lstFile.write("EndIgnore=0\r\n");
            lstFile.write("Baseline=0\r\n");
            lstFile.write("UpperDeviation=1\r\n");
            lstFile.write("LowerDeviation=-10\r\n");
            lstFile.write("UpperReject=1\r\n");
            lstFile.write("LowerReject=-10\r\n");
            lstFile.write("Cutoff=.35\r\n");
            lstFile.write("[FIELDS]\r\n");
            lstFile.write("[GROUP1]\r\n");
            lstFile.write("1=1.0\r\n");
            lstFile.write("2=1.0\r\n");
            lstFile.write("3=1.0\r\n");
            lstFile.write("4=1.0\r\n");
            lstFile.write("5=1.0\r\n");
            lstFile.write("6=1.0\r\n");
            lstFile.write("7=1.0\r\n");
            lstFile.write("8=1.0\r\n");
            lstFile.write("9=1.0\r\n");
            lstFile.write("10=1.0\r\n"); 
            lstFile.flush();
            lstFile.close();
        }
        /**
         * 
         * @param lstFile
         * @throws IOException
         */
        public static void writePortSettings(License lic) throws IOException
        {
            String fileName = dataDirectory + portSettingsFile;
            FileWriter lstFile = new FileWriter(fileName);
            lstFile.write("["+lic.getFirstMachineType()+"]\r\n");
            lstFile.write("Port=/dev/ttyACM0\r\n");
            lstFile.write("PortSettings=115200,8,n,1\r\n");
            lstFile.flush();
            lstFile.close();
        }

        public static String[] readAllLines(String filePath, String fileName) throws IOException {
        	FileReader fr = new FileReader(dataDirectory + filePath + fileName);
            BufferedReader bufferedReader = new BufferedReader(fr);
            List<String> lines = new ArrayList<String>();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            bufferedReader.close();
            return lines.toArray(new String[lines.size()]);
        }
  
        public static void writeAllLines(String filePath, String fileName, String[] lines) throws IOException {
        	FileWriter fw = new FileWriter(dataDirectory + filePath + fileName);
            BufferedWriter bufferedWriter = new BufferedWriter(fw);
            for(String line : lines) {
                bufferedWriter.write(line);
            }
            bufferedWriter.flush();
            bufferedWriter.close();
            fw.flush();
            fw.close();
        }
        
        public static String[] readAllLines(String filePath, String fileName, String commentLineDelim) throws IOException {
        	FileReader fr = new FileReader(dataDirectory + filePath + fileName);
            BufferedReader bufferedReader = new BufferedReader(fr);
            List<String> lines = new ArrayList<String>();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
            	//System.out.println("Startup :"+line);
                if( !line.startsWith(commentLineDelim)) {
                	lines.add(line);
                }
            }
            bufferedReader.close();
            return lines.toArray(new String[lines.size()]);
        }
        
        public static List<List<String>> readToList(String filePath, String fileName, String delim) throws IOException { 	
        	BufferedReader input =  new BufferedReader(new FileReader(dataDirectory + fileName));
            List<List<String>> mainContainer = new ArrayList<List<String>>();
        	String line = null;
        	while (( line = input.readLine()) != null)
        	{
        		String[] data = line.split(delim);
                List<String> lines = new ArrayList<String>();
        		lines.addAll(Arrays.asList(data));
        		mainContainer.add(lines);
        	}
        	input.close();
        	return mainContainer;
        }
        
		public static List<List<String>> readToList(String filePath, String fileName) throws IOException {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(dataDirectory + filePath));
            List<List<String>> mainContainer = new ArrayList<List<String>>();
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                List<String> lines = new ArrayList<String>();
                lines.add(line);
                mainContainer.add(lines);
            }
            bufferedReader.close();
            return mainContainer;
		}
		
		public static String read(String fileName) throws IOException {
	          BufferedReader bufferedReader = new BufferedReader(new FileReader(dataDirectory + fileName));
	          StringBuffer line = new StringBuffer();
	          String in;
	          while ( (in = bufferedReader.readLine()) != null) {
	        	   line.append(in);
	          }
	          bufferedReader.close();
	          return line.toString();
		}
		
		public static void write(String fileName, String load) throws IOException {
			FileWriter fw = new FileWriter(dataDirectory+fileName);
			fw.write(load);
			fw.flush();
			fw.close();
		}
		
		public static void writeFromList(String string, List<List<String>> hTable) {
			// TODO Auto-generated method stub
			
		}
	}
