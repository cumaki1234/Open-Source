package org.sourceforge.kga.io;

import org.sourceforge.kga.Project;
import org.sourceforge.kga.wrappers.XmlException;
import org.sourceforge.kga.wrappers.XmlReader;
import org.sourceforge.kga.wrappers.XmlWriter;

public interface ProjectChild {
	
	public String getFileTag();
	
	public void load(Project project, XmlReader xml, int version) throws XmlException;
	

	public void save(Project project, XmlWriter xml) throws XmlException;

}
