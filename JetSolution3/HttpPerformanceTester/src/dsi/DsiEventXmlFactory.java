/*
* Licensed Materials - Property of IBM
* 5725-B69 5655-Y17 5655-Y31 5724-X98 5724-Y15 5655-V82 
* Copyright IBM Corp. 1987, 2013. All Rights Reserved.
*
* Note to U.S. Government Users Restricted Rights: 
* Use, duplication or disclosure restricted by GSA ADP Schedule 
* Contract with IBM Corp.
*/

package dsi;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.output.XMLOutputter;

/**
 * Sets DSI XML payload
 */
public class DsiEventXmlFactory {
	
	private String timestamp= "";		
	
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	private void addXmlElement(Element root,  Namespace ns, String name, String value) {
		Element e = new Element(name);
		e.setNamespace(ns);
		e.setText(value);
		root.addContent(e);
	}	
	
	public String createEngineEventXml(String airplaneId, String engineId) {
		String xmlString = null;

		final int NUMBER_OF_CLONES = 10; 
		final int engineCloneId = (int) (Math.random() * NUMBER_OF_CLONES);

		final int MAX_TEMPERATURE = 1000; 
		final int temp = (int) (Math.random() * MAX_TEMPERATURE);
		
		final int MAX_RPM = 1000; 
		final int rpm = (int) (Math.random() * MAX_RPM);

		final int MAX_PRESSURE = 100; 
		final int pressure = (int) (Math.random() * MAX_PRESSURE);
		
		Element root = new Element("EngineEvent");
		
		// Set the namespace.
		Namespace xsi =
		 Namespace.getNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance");
		
		Namespace ns = Namespace.getNamespace("m", "http://www.ibm.com/ia/xmlns/default/JetModel/model");
		root.setNamespace(ns);
		root.addNamespaceDeclaration(xsi);
		root.addNamespaceDeclaration(ns);	
		
		addXmlElement(root, ns, "aircraftId", airplaneId);
		addXmlElement(root, ns, "engineId", engineId);
		addXmlElement(root, ns, "engineCloneId", engineId + "-Clone" + engineCloneId);
		addXmlElement(root, ns, "exhaustTemperature", String.valueOf(temp));
		addXmlElement(root, ns, "pressureRatio", String.valueOf(pressure));
		addXmlElement(root, ns, "rpm", String.valueOf(rpm));			

		addXmlElement(root, ns, "timestamp", timestamp);
		
		Document doc = new Document(root);
		XMLOutputter serializer = new XMLOutputter();
		
		// Convert the DOM to a string.
		xmlString = serializer.outputString(doc);

		return xmlString;
	}

}