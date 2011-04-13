/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.editor.codemirror.parser;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codemirror.Node;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 *
 */
public class GoogleGadgetParser extends CodeMirrorParserImpl
{

   String currentContentMimeType;

   @Override
   public void init()
   {
      currentContentMimeType = MimeType.TEXT_XML;
   }

   @Override
   TokenBeenImpl parseLine(JavaScriptObject node, int lineNumber, TokenBeenImpl currentToken, boolean hasParentParser)
   {
      // interrupt at the end of the document
      if (node == null)
         return currentToken;

      String nodeContent = Node.getContent(node).trim(); // returns text without ended space " " in the text

      // recognize CDATA open tag "<![CDATA["  within the TEXT_XML content
      if (XmlParser.isCDATAOpenNode(nodeContent) && MimeType.TEXT_XML.equals(currentContentMimeType))
      {
         TokenBeenImpl newToken = new TokenBeenImpl("CDATA", TokenType.CDATA, lineNumber, MimeType.TEXT_HTML);
         if (currentToken != null)
         {
            currentToken.addSubToken(newToken);
         }
         currentToken = newToken;

         currentContentMimeType = MimeType.TEXT_HTML;
         CodeMirrorParserImpl.getParser(currentContentMimeType).init();
         node = Node.getNext(node); // pass parsed node
      }

      // recognize CDATA close tag "]]>" within the CDATA content
      else if (XmlParser.isCDATACloseNode(nodeContent) && !MimeType.TEXT_XML.equals(currentContentMimeType))
      {
         currentToken = XmlParser.closeTag(lineNumber, currentToken);

         currentContentMimeType = MimeType.TEXT_XML;
         CodeMirrorParserImpl.getParser(currentContentMimeType).init();
         node = Node.getNext(node); // pass parsed node
      }

      currentToken = CodeMirrorParserImpl.getParser(currentContentMimeType).parseLine(node, lineNumber, currentToken, true); // call child parser

      if (node == null || Node.getName(node).equals("BR"))
      {
         return currentToken;
      }

      return parseLine(Node.getNext(node), lineNumber, currentToken, false); // call itself 

   };
}