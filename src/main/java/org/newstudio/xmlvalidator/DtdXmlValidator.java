package org.newstudio.xmlvalidator;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

/**
 * Use SAX Parser ���� XML (�ϥ� W3C XML DTD)
 *
 * @author Scribe Huang
 */
public class DtdXmlValidator implements XmlValidator {
	/** Error Handler */
	private ErrorHandler e;
	/** SAXParserFactory ���� */
	private SAXParserFactory spf = SAXParserFactory.newInstance();
	/**  */
	private boolean isValidating = true;

	public DtdXmlValidator(ErrorHandler e) {
		setErrorHandler(e);
	}

	@Override
	public void setErrorHandler(ErrorHandler e) {
		this.e = e;
	}

	/**
	 * Disable the validator. For XPath.
	 */
	public void disableValidating() {
		isValidating = false;
	}

	@Override
	public String validate(String fileXML) {
		try {
			spf.setValidating(isValidating);
			SAXParser sp = spf.newSAXParser();
			sp.parse(
				IOUtils.toInputStream(fileXML, "UTF-8"),
				e != null ? e : null
			);

			// �J��������B�z���~�T���覡 (�]���Lthrows Exception)
			if (e != null && e.caughtError()){
				return toXML("-ERR", fileXML, e.flushErrorMessage());
			}
			return toXML("+OK", fileXML, "���Ҧ��\!");
		} catch (ParserConfigurationException pe) { // parser �]�w���~
			return toXML("-ERR", fileXML, "��R���]�w���~!");
		} catch (IOException ie) { // IO ���~
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
