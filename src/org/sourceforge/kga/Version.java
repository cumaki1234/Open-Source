package org.sourceforge.kga;

public class Version
{

    public static final  String value;
    
    static{
    	//read the version from our jar manifest
    	String value_helper  = Version.class.getPackage().getImplementationVersion();
    	value=(value_helper==null)?"UNKNOWN":value_helper;
    }
    
}
