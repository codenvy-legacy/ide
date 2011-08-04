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
package org.exoplatform.ide.editor.jsp.client.codemirror;

import java.util.HashMap;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codemirror.CodeMirrorParserImpl;
import org.exoplatform.ide.editor.codemirror.Node;
import org.exoplatform.ide.editor.html.client.codemirror.HtmlParser;
import org.exoplatform.ide.editor.java.client.codemirror.JavaParser;
import org.exoplatform.ide.editor.xml.client.codemirror.XmlParser;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 *
 */
public class JspParser extends CodeMirrorParserImpl
{

   String currentContentMimeType;

   private static HashMap<String, CodeMirrorParserImpl> factory = new HashMap<String, CodeMirrorParserImpl>();
   
   static
   {
      factory.put(MimeType.TEXT_HTML, new HtmlParser());   
      factory.put(MimeType.APPLICATION_JAVA, new JavaParser());      
   }

   protected static CodeMirrorParserImpl getParser(String mimeType)
   {
      if (factory.containsKey(mimeType))
      {
         return factory.get(mimeType);
      }

      return null;
   }   
   
   @Override
   public void init() 
   {
      currentContentMimeType = MimeType.TEXT_HTML;
   }   

   @Override
   public TokenBeenImpl parseLine(JavaScriptObject node, int lineNumber, TokenBeenImpl currentToken, boolean hasParentParser)
   {
      // interrupt at the end of the document
      if (node == null)
         return currentToken;
      
      String nodeContent = Node.getContent(node).trim(); // returns text without ended space " " in the text
      String nodeType = Node.getType(node);       
      
      // recognize "<%" open tag within the TEXT_HTML content
      if (isJavaOpenNode(nodeType, nodeContent) && MimeType.TEXT_HTML.equals(currentContentMimeType))
      {
         TokenBeenImpl newToken = new TokenBeenImpl("java code", TokenType.JSP_TAG, lineNumber, MimeType.APPLICATION_JAVA);
         if (currentToken != null)
         {
            currentToken.addSubToken(newToken);
         }
         currentToken = newToken;

         currentContentMimeType = MimeType.APPLICATION_JAVA;
         getParser(currentContentMimeType).init();
      }

      // recognize "%>" close tag
      else if (isJavaCloseNode(nodeType, nodeContent) && !MimeType.TEXT_HTML.equals(currentContentMimeType))
      {
         currentToken = XmlParser.closeTag(lineNumber, currentToken);

         currentContentMimeType = MimeType.TEXT_HTML;
         getParser(currentContentMimeType).init();
      }
      
      currentToken = getParser(currentContentMimeType).parseLine(node, lineNumber, currentToken, true);  // call child parser
      
      if (node == null || Node.getName(node).equals("BR")) 
      {
         return currentToken;
      }

      return parseLine(Node.getNext(node), lineNumber, currentToken, false);  // call itself 

   }

   private boolean isJavaOpenNode(String nodeType, String nodeContent)
   {
      return (nodeType != null) && (nodeContent != null) && nodeType.equals("jsp-java") && nodeContent.equals("&lt;%");
   }

   private boolean isJavaCloseNode(String nodeType, String nodeContent)
   {
      return (nodeType != null) && (nodeContent != null) && nodeType.equals("jsp-java") && nodeContent.equals("%&gt;");
   };
}