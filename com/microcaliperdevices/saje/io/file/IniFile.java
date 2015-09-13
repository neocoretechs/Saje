package com.microcaliperdevices.saje.io.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import com.microcaliperdevices.saje.io.machine.bridge.MachineBridge;
import com.microcaliperdevices.saje.license.License;

	/**
	 * The file, typically sa.ini contains all the configs for the various machines
	 * The license matches its designated product to the proper entry in the sa.ini
	 * @author jg
	 * Copyright 2012,2014 Microcaliper Devices, LLC
	 */
	 public class IniFile {

        public static String getIniFileString(Hashtable categories, String category, String key)
        {
            return (String)((Hashtable)categories.get(category)).get(key);
        }
   

        /**
         * Gets the categories. Create a category hashtable of entries hashtables
         * @param iniFileName The ini file.
         * @return A Hashtable of categories as keys, entries Hashtable as value
         * @throws IOException
         */
        public static Hashtable getCategories(String iniFileName) throws IOException
        {
            Hashtable ht = new Hashtable();
            Hashtable htCatElems;
            String[] felems = FileIOUtilities.readAllLines("",iniFileName, "-");
            int ielem = 0, catIndex = 0;
            while( ielem < felems.length ) {
                if (felems[ielem].charAt(0) == '[')
                {
                    htCatElems = new Hashtable(); // set up new table of entries to follow this category
                    // parse out starting [ and ending ] from category and use it as key 
                    //if( Props.DEBUG ) System.out.println("Category="+felems[ielem].substring(1, felems[ielem].length() - 1));
                    ht.put(felems[ielem].substring(1, felems[ielem].length() - 1), htCatElems);
                    
                    catIndex = ielem;
                    ++ielem;
                    if (ielem >= felems.length) {
                            return ht;
                    }
                    while(ielem < felems.length && felems[ielem].charAt(0) != '[') {
                    	if (felems[ielem].trim().length() == 0)
                    	{
                    		continue;

                    	}
                    	String key = felems[ielem].substring(0, felems[ielem].indexOf("="));
                    	String val = felems[ielem].substring(felems[ielem].indexOf("=") + 1);
                    	//if( Props.DEBUG ) System.out.println("Placing val "+key+","+val);
                    	htCatElems.put(key, val);
                    	++ielem;
                    }
                }
                else
                {
                    ++ielem;
                }
            }
            return ht;
        }
     
        
        public static List<String> getCategories(Hashtable categories)
        {
            List<String> olist = new ArrayList<String>();
            Enumeration ecatKeys = categories.keys();
            
            while(ecatKeys.hasMoreElements()) {
            	String catKeys = (String)ecatKeys.nextElement();
                olist.add(catKeys);
            }
            return olist;
        }
  
    }
   


