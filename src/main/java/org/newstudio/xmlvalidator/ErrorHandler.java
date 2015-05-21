package org.newstudio.xmlvalidator;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX ���~�B�z����A��ܸԲ����ҥ��Ѹ�T
 *
 * @author Scribe Huang
 */
public class ErrorHandler extends DefaultHandler {
	/** �O�_�b�J�����ҿ��~�N���� */
	private boolean stopOnError = false;
	/** ���~�T���w�İ� */
	private StringBuffer errorMessage = new StringBuffer();

	// �зǫغc��
	public ErrorHandler() {
		this(false);
	}

	// �a�]�w���غc��:
	public ErrorHandler(boolean stopOnError) {
		this.stopOnError = stopOnError;
	}

	// �榡�Ƭ����������~�T��
	private String getMsg(SAXParseException spe) {
		String msg = String.format(
			"\t<line>%d</line>\n\t<column>%d</column>\n\t<message><![CDATA[ %s ]]></message>\n",
			spe.getLineNumber(), spe.getColumnNumber(), spe.getMessage());
		return msg;
	}

	// �O�_��������~�T��
	public boolean caughtError() {
		return errorMessage.length() > 0;
	}

	// �^�ǿ��~�T��
	public String flushErrorMessage() {
		String err = "<errors>\n" + errorMessage.toString() + "</errors>\n";
		errorMessage.setLength(0); // �M��
		return err;
	}

	// ���~�d�I: ĵ�i�h��
	@Override
	public void warning(SAXParseException spe) throws SAXException {
		String err = "<error>\n\t<level>warning</level>\n" + getMsg(spe) + "</error>\n";
		errorMessage.append(err);
		if (stopOnError) {
			throw new SAXException(err);
		}
	}

	// ���~�d�I: ���~�h��
	@Override
	public void error(SAXParseException spe) throws SAXException {
		String err = "<error>\n\t<level>error</level>\n" + getMsg(spe) + "</error>\n";
		errorMessage.append(err);
		if (stopOnError) {
			throw new SAXException(err);
		}
	}

	// ���~�d�I: �Y�����~�h��
	@Override
	public void fatalError(SAXParseException spe) throws SAXException {
		String err = "<error>\n\t<level>fatal</level>\n" + getMsg(spe) + "</error>\n";
		// �Y�����~�N�|�j��_����
		throw new SAXException(err);
	}
}
