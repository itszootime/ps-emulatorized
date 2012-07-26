package org.uncertweb.ps.encoding.xml;

import org.jdom.Element;
import org.uncertweb.et.emulator.Emulator;

public class EmulatorEncoding extends AbstractXMLEncoding {

	public boolean isSupportedClass(Class<?> classOf) {
		if (classOf.equals(Emulator.class)) {
			return true;
		}
		return false;
	}

	public Object parse(Element element, Class<?> classOf) {
		return null;
	}

	public Element encode(Object object) {
		return null;
	}

	public String getNamespace() {
		return "http://www.opengis.net/gml/3.2";
	}
	
	public String getSchemaLocation() {
		return "http://uncertws.aston.ac.uk/schema/profiles/GML/UncertWeb_GML.xsd";
	}

	public Include getIncludeForClass(Class<?> classOf) {
		if (classOf.equals(Emulator.class)) {
			return new IncludeRef("Point");
		}
		return null;
	}
}
