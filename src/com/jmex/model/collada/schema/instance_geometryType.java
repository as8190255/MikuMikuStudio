/**
 * instance_geometryType.java
 *
 * This file was generated by XMLSpy 2007sp2 Enterprise Edition.
 *
 * YOU SHOULD NOT MODIFY THIS FILE, BECAUSE IT WILL BE
 * OVERWRITTEN WHEN YOU RE-RUN CODE GENERATION.
 *
 * Refer to the XMLSpy Documentation for further details.
 * http://www.altova.com/xmlspy
 */


package com.jmex.model.collada.schema;

import com.jmex.xml.types.SchemaNCName;
import com.jmex.xml.types.SchemaString;

public class instance_geometryType extends com.jmex.xml.xml.Node {

	public instance_geometryType(instance_geometryType node) {
		super(node);
	}

	public instance_geometryType(org.w3c.dom.Node node) {
		super(node);
	}

	public instance_geometryType(org.w3c.dom.Document doc) {
		super(doc);
	}

	public instance_geometryType(com.jmex.xml.xml.Document doc, String namespaceURI, String prefix, String name) {
		super(doc, namespaceURI, prefix, name);
	}
	
	public void adjustPrefix() {
		for (	org.w3c.dom.Node tmpNode = getDomFirstChild( Attribute, null, "url" );
				tmpNode != null;
				tmpNode = getDomNextChild( Attribute, null, "url", tmpNode )
			) {
			internalAdjustPrefix(tmpNode, false);
		}
		for (	org.w3c.dom.Node tmpNode = getDomFirstChild( Attribute, null, "sid" );
				tmpNode != null;
				tmpNode = getDomNextChild( Attribute, null, "sid", tmpNode )
			) {
			internalAdjustPrefix(tmpNode, false);
		}
		for (	org.w3c.dom.Node tmpNode = getDomFirstChild( Attribute, null, "name" );
				tmpNode != null;
				tmpNode = getDomNextChild( Attribute, null, "name", tmpNode )
			) {
			internalAdjustPrefix(tmpNode, false);
		}
		for (	org.w3c.dom.Node tmpNode = getDomFirstChild( Element, "http://www.collada.org/2005/11/COLLADASchema", "bind_material" );
				tmpNode != null;
				tmpNode = getDomNextChild( Element, "http://www.collada.org/2005/11/COLLADASchema", "bind_material", tmpNode )
			) {
			internalAdjustPrefix(tmpNode, true);
			new bind_materialType(tmpNode).adjustPrefix();
		}
		for (	org.w3c.dom.Node tmpNode = getDomFirstChild( Element, "http://www.collada.org/2005/11/COLLADASchema", "extra" );
				tmpNode != null;
				tmpNode = getDomNextChild( Element, "http://www.collada.org/2005/11/COLLADASchema", "extra", tmpNode )
			) {
			internalAdjustPrefix(tmpNode, true);
			new extraType(tmpNode).adjustPrefix();
		}
	}
	public void setXsiType() {
 		org.w3c.dom.Element el = (org.w3c.dom.Element) domNode;
		el.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", "instance_geometry");
	}

	public static int geturlMinCount() {
		return 1;
	}

	public static int geturlMaxCount() {
		return 1;
	}

	public int geturlCount() {
		return getDomChildCount(Attribute, null, "url");
	}

	public boolean hasurl() {
		return hasDomChild(Attribute, null, "url");
	}

	public SchemaString newurl() {
		return new SchemaString();
	}

	public SchemaString geturlAt(int index) throws Exception {
		return new SchemaString(getDomNodeValue(getDomChildAt(Attribute, null, "url", index)));
	}

	public org.w3c.dom.Node getStartingurlCursor() throws Exception {
		return getDomFirstChild(Attribute, null, "url" );
	}

	public org.w3c.dom.Node getAdvancedurlCursor( org.w3c.dom.Node curNode ) throws Exception {
		return getDomNextChild( Attribute, null, "url", curNode );
	}

	public SchemaString geturlValueAtCursor( org.w3c.dom.Node curNode ) throws Exception {
		if( curNode == null )
			throw new com.jmex.xml.xml.XmlException("Out of range");
		else
			return new SchemaString(getDomNodeValue(curNode));
	}

	public SchemaString geturl() throws Exception 
 {
		return geturlAt(0);
	}

	public void removeurlAt(int index) {
		removeDomChildAt(Attribute, null, "url", index);
	}

	public void removeurl() {
		removeurlAt(0);
	}

	public org.w3c.dom.Node addurl(SchemaString value) {
		if( value.isNull() )
			return null;

		return  appendDomChild(Attribute, null, "url", value.toString());
	}

	public org.w3c.dom.Node addurl(String value) throws Exception {
		return addurl(new SchemaString(value));
	}

	public void inserturlAt(SchemaString value, int index) {
		insertDomChildAt(Attribute, null, "url", index, value.toString());
	}

	public void inserturlAt(String value, int index) throws Exception {
		inserturlAt(new SchemaString(value), index);
	}

	public void replaceurlAt(SchemaString value, int index) {
		replaceDomChildAt(Attribute, null, "url", index, value.toString());
	}

	public void replaceurlAt(String value, int index) throws Exception {
		replaceurlAt(new SchemaString(value), index);
	}

	public static int getsidMinCount() {
		return 0;
	}

	public static int getsidMaxCount() {
		return 1;
	}

	public int getsidCount() {
		return getDomChildCount(Attribute, null, "sid");
	}

	public boolean hassid() {
		return hasDomChild(Attribute, null, "sid");
	}

	public SchemaNCName newsid() {
		return new SchemaNCName();
	}

	public SchemaNCName getsidAt(int index) throws Exception {
		return new SchemaNCName(getDomNodeValue(getDomChildAt(Attribute, null, "sid", index)));
	}

	public org.w3c.dom.Node getStartingsidCursor() throws Exception {
		return getDomFirstChild(Attribute, null, "sid" );
	}

	public org.w3c.dom.Node getAdvancedsidCursor( org.w3c.dom.Node curNode ) throws Exception {
		return getDomNextChild( Attribute, null, "sid", curNode );
	}

	public SchemaNCName getsidValueAtCursor( org.w3c.dom.Node curNode ) throws Exception {
		if( curNode == null )
			throw new com.jmex.xml.xml.XmlException("Out of range");
		else
			return new SchemaNCName(getDomNodeValue(curNode));
	}

	public SchemaNCName getsid() throws Exception 
 {
		return getsidAt(0);
	}

	public void removesidAt(int index) {
		removeDomChildAt(Attribute, null, "sid", index);
	}

	public void removesid() {
		removesidAt(0);
	}

	public org.w3c.dom.Node addsid(SchemaNCName value) {
		if( value.isNull() )
			return null;

		return  appendDomChild(Attribute, null, "sid", value.toString());
	}

	public org.w3c.dom.Node addsid(String value) throws Exception {
		return addsid(new SchemaNCName(value));
	}

	public void insertsidAt(SchemaNCName value, int index) {
		insertDomChildAt(Attribute, null, "sid", index, value.toString());
	}

	public void insertsidAt(String value, int index) throws Exception {
		insertsidAt(new SchemaNCName(value), index);
	}

	public void replacesidAt(SchemaNCName value, int index) {
		replaceDomChildAt(Attribute, null, "sid", index, value.toString());
	}

	public void replacesidAt(String value, int index) throws Exception {
		replacesidAt(new SchemaNCName(value), index);
	}

	public static int getnameMinCount() {
		return 0;
	}

	public static int getnameMaxCount() {
		return 1;
	}

	public int getnameCount() {
		return getDomChildCount(Attribute, null, "name");
	}

	public boolean hasname() {
		return hasDomChild(Attribute, null, "name");
	}

	public SchemaNCName newname() {
		return new SchemaNCName();
	}

	public SchemaNCName getnameAt(int index) throws Exception {
		return new SchemaNCName(getDomNodeValue(getDomChildAt(Attribute, null, "name", index)));
	}

	public org.w3c.dom.Node getStartingnameCursor() throws Exception {
		return getDomFirstChild(Attribute, null, "name" );
	}

	public org.w3c.dom.Node getAdvancednameCursor( org.w3c.dom.Node curNode ) throws Exception {
		return getDomNextChild( Attribute, null, "name", curNode );
	}

	public SchemaNCName getnameValueAtCursor( org.w3c.dom.Node curNode ) throws Exception {
		if( curNode == null )
			throw new com.jmex.xml.xml.XmlException("Out of range");
		else
			return new SchemaNCName(getDomNodeValue(curNode));
	}

	public SchemaNCName getname() throws Exception 
 {
		return getnameAt(0);
	}

	public void removenameAt(int index) {
		removeDomChildAt(Attribute, null, "name", index);
	}

	public void removename() {
		removenameAt(0);
	}

	public org.w3c.dom.Node addname(SchemaNCName value) {
		if( value.isNull() )
			return null;

		return  appendDomChild(Attribute, null, "name", value.toString());
	}

	public org.w3c.dom.Node addname(String value) throws Exception {
		return addname(new SchemaNCName(value));
	}

	public void insertnameAt(SchemaNCName value, int index) {
		insertDomChildAt(Attribute, null, "name", index, value.toString());
	}

	public void insertnameAt(String value, int index) throws Exception {
		insertnameAt(new SchemaNCName(value), index);
	}

	public void replacenameAt(SchemaNCName value, int index) {
		replaceDomChildAt(Attribute, null, "name", index, value.toString());
	}

	public void replacenameAt(String value, int index) throws Exception {
		replacenameAt(new SchemaNCName(value), index);
	}

	public static int getbind_materialMinCount() {
		return 0;
	}

	public static int getbind_materialMaxCount() {
		return 1;
	}

	public int getbind_materialCount() {
		return getDomChildCount(Element, "http://www.collada.org/2005/11/COLLADASchema", "bind_material");
	}

	public boolean hasbind_material() {
		return hasDomChild(Element, "http://www.collada.org/2005/11/COLLADASchema", "bind_material");
	}

	public bind_materialType newbind_material() {
		return new bind_materialType(domNode.getOwnerDocument().createElementNS("http://www.collada.org/2005/11/COLLADASchema", "bind_material"));
	}

	public bind_materialType getbind_materialAt(int index) throws Exception {
		return new bind_materialType(getDomChildAt(Element, "http://www.collada.org/2005/11/COLLADASchema", "bind_material", index));
	}

	public org.w3c.dom.Node getStartingbind_materialCursor() throws Exception {
		return getDomFirstChild(Element, "http://www.collada.org/2005/11/COLLADASchema", "bind_material" );
	}

	public org.w3c.dom.Node getAdvancedbind_materialCursor( org.w3c.dom.Node curNode ) throws Exception {
		return getDomNextChild( Element, "http://www.collada.org/2005/11/COLLADASchema", "bind_material", curNode );
	}

	public bind_materialType getbind_materialValueAtCursor( org.w3c.dom.Node curNode ) throws Exception {
		if( curNode == null )
			throw new com.jmex.xml.xml.XmlException("Out of range");
		else
			return new bind_materialType(curNode);
	}

	public bind_materialType getbind_material() throws Exception 
 {
		return getbind_materialAt(0);
	}

	public void removebind_materialAt(int index) {
		removeDomChildAt(Element, "http://www.collada.org/2005/11/COLLADASchema", "bind_material", index);
	}

	public void removebind_material() {
		removebind_materialAt(0);
	}

	public org.w3c.dom.Node addbind_material(bind_materialType value) {
		return appendDomElement("http://www.collada.org/2005/11/COLLADASchema", "bind_material", value);
	}

	public void insertbind_materialAt(bind_materialType value, int index) {
		insertDomElementAt("http://www.collada.org/2005/11/COLLADASchema", "bind_material", index, value);
	}

	public void replacebind_materialAt(bind_materialType value, int index) {
		replaceDomElementAt("http://www.collada.org/2005/11/COLLADASchema", "bind_material", index, value);
	}

	public static int getextraMinCount() {
		return 0;
	}

	public static int getextraMaxCount() {
		return Integer.MAX_VALUE;
	}

	public int getextraCount() {
		return getDomChildCount(Element, "http://www.collada.org/2005/11/COLLADASchema", "extra");
	}

	public boolean hasextra() {
		return hasDomChild(Element, "http://www.collada.org/2005/11/COLLADASchema", "extra");
	}

	public extraType newextra() {
		return new extraType(domNode.getOwnerDocument().createElementNS("http://www.collada.org/2005/11/COLLADASchema", "extra"));
	}

	public extraType getextraAt(int index) throws Exception {
		return new extraType(getDomChildAt(Element, "http://www.collada.org/2005/11/COLLADASchema", "extra", index));
	}

	public org.w3c.dom.Node getStartingextraCursor() throws Exception {
		return getDomFirstChild(Element, "http://www.collada.org/2005/11/COLLADASchema", "extra" );
	}

	public org.w3c.dom.Node getAdvancedextraCursor( org.w3c.dom.Node curNode ) throws Exception {
		return getDomNextChild( Element, "http://www.collada.org/2005/11/COLLADASchema", "extra", curNode );
	}

	public extraType getextraValueAtCursor( org.w3c.dom.Node curNode ) throws Exception {
		if( curNode == null )
			throw new com.jmex.xml.xml.XmlException("Out of range");
		else
			return new extraType(curNode);
	}

	public extraType getextra() throws Exception 
 {
		return getextraAt(0);
	}

	public void removeextraAt(int index) {
		removeDomChildAt(Element, "http://www.collada.org/2005/11/COLLADASchema", "extra", index);
	}

	public void removeextra() {
		while (hasextra())
			removeextraAt(0);
	}

	public org.w3c.dom.Node addextra(extraType value) {
		return appendDomElement("http://www.collada.org/2005/11/COLLADASchema", "extra", value);
	}

	public void insertextraAt(extraType value, int index) {
		insertDomElementAt("http://www.collada.org/2005/11/COLLADASchema", "extra", index, value);
	}

	public void replaceextraAt(extraType value, int index) {
		replaceDomElementAt("http://www.collada.org/2005/11/COLLADASchema", "extra", index, value);
	}

}
