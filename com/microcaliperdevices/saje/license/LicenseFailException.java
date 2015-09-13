/**
 * 
 */
package com.microcaliperdevices.saje.license;

/**
 * @author jg
 *
 */
public class LicenseFailException extends Exception {
	public LicenseFailException(Exception e) { super(e) ; }
	public LicenseFailException(String e) { super(e) ; }
}
