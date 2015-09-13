package com.microcaliperdevices.saje.cyclic;


	/**
	* Represents cycles, i.e. the indentations in a surface such as corrugation 
	* Copyright 2012,2014 Microcaliper Devices, LLC
	* 
	* @author jg
	*
	*/
    public class Cycle
    {
        private String m_Label = "";

        private float cyclesPerFoot;
        public float getCyclesPerFoot()
        {
            return cyclesPerFoot; 
        }
        
        public void setCyclesPerFoot(float value) {
            cyclesPerFoot = value; 
        }

        private float optimalLength;
        public float getOptimalLength() {
            return optimalLength; 
        }
        public void setOptimalLength(float value) { 
        	optimalLength = value;
        }

        private float highUpperLimit;
        public float getHighUpperLimit() {
            return highUpperLimit; 
        }
        public void setHighUpperLimit(float value) { 
        	highUpperLimit = value; 
        }

        private float medUpperLimit;
        public float getMedUpperLimit() {
            return medUpperLimit; 
        }
        public void setMedUpperLimit(float value) {
            medUpperLimit = value;
        }

        private float lowUpperLimit;
        public float getLowUpperLimit() {
            return lowUpperLimit; 
        }
        public void setLowUpperLimit(float value) {
            lowUpperLimit = value;
        }

        public String getLabel() {
            return m_Label; 
        }
        
        public void setLabel(String value) {
                m_Label = value;
                switch (value.trim())
                {
                    case "A":
                        cyclesPerFoot = new Float(33);
                        optimalLength = new Float(36.86);
                        break;
                    case "B":
                        cyclesPerFoot = new Float(47);
                        optimalLength = new Float(26.62);
                        break;
                    case "C":
                        cyclesPerFoot = new Float(39);
                        optimalLength = new Float(31.74);
                        break;
                }
        }
    }