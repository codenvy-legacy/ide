/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.editor.gadget.client.codemirror;

import com.google.gwt.core.client.JavaScriptObject;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codemirror.CodeMirrorParserImpl;
import org.exoplatform.ide.editor.codemirror.Node;
import org.exoplatform.ide.editor.html.client.codemirror.HtmlParser;
import org.exoplatform.ide.editor.xml.client.codemirror.XmlParser;

import java.util.HashMap;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public class GoogleGadgetParser extends CodeMirrorParserImpl {

    String currentContentMimeType;

    private static HashMap<String, CodeMirrorParserImpl> factory = new HashMap<String, CodeMirrorParserImpl>();

    static {
        factory.put(MimeType.TEXT_HTML, new HtmlParser());
        factory.put(MimeType.TEXT_XML, new XmlParser());
    }

    protected static CodeMirrorParserImpl getParser(String mimeType) {
        if (factory.containsKey(mimeType)) {
            return factory.get(mimeType);
        }

        return null;
    }

    @Override
    public void init() {
        currentContentMimeType = MimeType.TEXT_XML;
    }

    @Override
    public TokenBeenImpl parseLine(JavaScriptObject node, int lineNumber, TokenBeenImpl currentToken, boolean hasParentParser) {
        // interrupt at the end of the document
        if (node == null)
            return currentToken;

        String nodeContent = Node.getContent(node).trim(); // returns text without ended space " " in the text

        // recognize CDATA open tag "<![CDATA["  within the TEXT_XML content
        if (XmlParser.isCDATAOpenNode(nodeContent) && MimeType.TEXT_XML.equals(currentContentMimeType)) {
            TokenBeenImpl newToken = new TokenBeenImpl("CDATA", TokenType.CDATA, lineNumber, MimeType.TEXT_HTML);
            if (currentToken != null) {
                currentToken.addSubToken(newToken);
            }
            currentToken = newToken;

            currentContentMimeType = MimeType.TEXT_HTML;
            getParser(currentContentMimeType).init();
            node = Node.getNext(node); // pass parsed node
        }

        // recognize CDATA close tag "]]>" within the CDATA content
        else if (XmlParser.isCDATACloseNode(nodeContent) && !MimeType.TEXT_XML.equals(currentContentMimeType)) {
            currentToken = XmlParser.closeTag(lineNumber, currentToken);

            currentContentMimeType = MimeType.TEXT_XML;
            getParser(currentContentMimeType).init();
            node = Node.getNext(node); // pass parsed node
        }

        currentToken = getParser(currentContentMimeType).parseLine(node, lineNumber, currentToken, true); // call child parser

        if (node == null || Node.getName(node).equals("BR")) {
            return currentToken;
        }

        return parseLine(Node.getNext(node), lineNumber, currentToken, false); // call itself

    }

    ;
}