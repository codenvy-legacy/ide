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
package org.exoplatform.ide.editor.xml.client.codemirror;

import com.google.gwt.core.client.JavaScriptObject;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codemirror.CodeMirrorParserImpl;
import org.exoplatform.ide.editor.codemirror.Node;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public class XmlParser extends CodeMirrorParserImpl {
    private String lastNodeContent;

    private String lastNodeType;

    @Override
    public TokenBeenImpl parseLine(JavaScriptObject node, int lineNumber, TokenBeenImpl currentToken,
                                   boolean hasParentParser) {
        // interrupt at the end of the line or content
        if ((node == null) || Node.getName(node).equals("BR"))
            return currentToken;

        String nodeContent = Node.getContent(node).trim(); // returns text without ended space " " in the text
        String nodeType = Node.getType(node);

        // recognize CDATA open tag "<![CDATA[" not in the CDATA section
        if (XmlParser.isCDATAOpenNode(nodeContent)) {
            TokenBeenImpl newToken = new TokenBeenImpl("CDATA", TokenType.CDATA, lineNumber, MimeType.TEXT_XML);
            if (currentToken != null) {
                currentToken.addSubToken(newToken);
            }

            currentToken = newToken;
        }

        // recognize CDATA close tag "]]>" into the CDATA section
        else if (XmlParser.isCDATACloseNode(nodeContent)) {
            if (currentToken != null) {
                currentToken = currentToken.getParentToken();
            }
        }

        // recognize tag node not in the CDATA section
        if (isTagNode(nodeType)) {
            // recognize open tag starting with "<"
            if (isOpenTagNode(lastNodeType, lastNodeContent)) {
                currentToken = addTag(currentToken, nodeContent, lineNumber, MimeType.TEXT_XML);
            }

            // recognize close tag starting with "</"
            else if (isCloseStartTagNode(lastNodeType, lastNodeContent)) {
                currentToken = closeTag(lineNumber, currentToken);
            }
        }

        // recognize close tag starting with "/>" out of
        else if (isCloseFinishTagNode(nodeType, nodeContent)) {
            currentToken = closeTag(lineNumber, currentToken);
        }

        lastNodeContent = nodeContent;
        lastNodeType = nodeType;

        if (hasParentParser) {
            return currentToken; // return current token to parent parser
        }

        return parseLine(Node.getNext(node), lineNumber, currentToken, false);
    }

    /**
     * recognize "</" node
     *
     * @param nodeType
     * @param nodeContent
     * @return
     */
    public static boolean isCloseStartTagNode(String nodeType, String nodeContent) {
        return (nodeType != null) && (nodeContent != null)
               && (nodeType.equals("xml-punctuation") && nodeContent.equals("&lt;/"));
    }

    /**
     * recognize "/>" node
     *
     * @param nodeType
     * @param nodeContent
     * @return
     */
    public static boolean isCloseFinishTagNode(String nodeType, String nodeContent) {
        return (nodeType != null) && (nodeContent != null)
               && (nodeType.equals("xml-punctuation") && nodeContent.equals("/&gt;"));
    }

    /**
     * recognize "<" node
     *
     * @param nodeType
     * @param nodeContent
     * @return
     */
    public static boolean isOpenTagNode(String nodeType, String nodeContent) {
        return (nodeType != null) && (nodeContent != null) && nodeType.equals("xml-punctuation")
               && nodeContent.equals("&lt;");
    }

    ;

    /**
     * recognize "<" node
     *
     * @param nodeType
     * @return
     */
    public static boolean isTagNode(String nodeType) {
        return (nodeType != null) && nodeType.equals("xml-tagname");
    }

    ;

    public static boolean isCDATAOpenNode(String nodeContent) {
        return nodeContent.matches("&lt;!\\[CDATA\\[.*(\\n)*.*");
    }

    ;

    public static boolean isCDATACloseNode(String nodeContent) {
        return nodeContent.matches("\\]\\]&gt;.*");
    }

    ;

    public static TokenBeenImpl addTag(TokenBeenImpl currentToken, String tagName, int lineNumber, String contentMimeType) {
        TokenBeenImpl newToken = new TokenBeenImpl(tagName, TokenType.TAG, lineNumber, contentMimeType);
        if (currentToken != null) {
            currentToken.addSubToken(newToken);
        }

        return newToken;
    }

    // close tag
    public static TokenBeenImpl closeTag(int lineNumber, TokenBeenImpl currentToken) {
        if (currentToken != null) {
            currentToken.setLastLineNumber(lineNumber);
        }

        if (currentToken.getParentToken() != null) {
            return currentToken.getParentToken();
        }

        return currentToken;
    }
}