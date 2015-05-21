package org.newstudio.xmlvalidator;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * �ϥ� JAXP �s���O (javax.xml.validation) ���� XML (�ϥ� W3C XML Schema Definition)
 *
 * @author Scribe Huang
 */
public class XsdXmlValidator implements XmlValidator {
	/** Error handler */
	private ErrorHandler e;
	/** schemaFactory ���� */
	private SchemaFactory schemaFactory = SchemaFactory.newInstance(
		javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI
	);
	/** Schema */
	private Schema schema;

	public XsdXmlValidator(ErrorHandler e) {
		setErrorHandler(e);
	}

	@Override
	public void setErrorHandler(ErrorHandler e) {
		this.e = e;
	}

	// �]�w Schema
	public void setSchema(InputStream schema) {
		try {
			this.schema = schemaFactory.newSchema(new StreamSource(schema));
		} catch(Exception e) {
			this.schema = null;
		}
	}

	@Override
	public String validate(String fileXML) {
		try {
			// �w�]���ҥ� Schema (�� XML �ɮפ��P�_)
			if (schema == null) {
				schema = schemaFactory.newSchema();
			}

			Validator validator = schema.newValidator();
			validator.setErrorHandler(e);
			validator.validate(new StreamSource(new StringReader(fileXML)));

			// �J��������B�z���~�T���覡 (�]���Lthrows Exception)
			if (e != null && e.caughtError()) {
				return toXML("-ERR", fileXML, e.flushErrorMessage());
			}
			return toXML("+OK", fileXML, "���Ҧ��\!");
		} catch (IOException ie) { // IO ���~
			ie.printStackTrace();
			return toXML("-ERR", fileXML, "�ɮ׳B�z���~!" + ie.getMessage());
		} catch (SAXException se) { // SAX ���ҿ��~
			return toXML("-ERR", fileXML, "<errors>" + se.getMessage() + "</errors>");
		}
	}

	private String toXML(String status, String xml, String errormsg) {
		StringBuffer body = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		body.append("<result>\n");
		body.append("\t<status>" + status + "</status>\n");
		body.append("\t<xml><![CDATA[" + xml + "]]></xml>\n");
		body.append("\t<message>\n" + errormsg + "</message>\n");
		body.append("</result>\n");
		return body.toString();
	}
}
