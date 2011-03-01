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
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codemirror.CodeMirrorTokenImpl;
import org.exoplatform.ide.editor.codemirror.Node;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 *
 */
public class GroovyTemplateParser extends CodeMirrorParser
{

   String currentContentMimeType;
   
   @Override
   public void init() 
   {
      currentContentMimeType = MimeType.TEXT_HTML;
   }   

   @Override
   CodeMirrorTokenImpl parseLine(JavaScriptObject node, int lineNumber, CodeMirrorTokenImpl currentToken, boolean hasParentParser)
   {
      // interrupt at the end of the document
      if (node == null)
         return currentToken;
      
      String nodeContent = Node.getContent(node).trim(); // returns text without ended space " " in the text
      String nodeType = Node.getType(node);       
      
      // recognize "<%" open tag within the TEXT_HTML content
      if (isGroovyOpenNode(nodeType, nodeContent) && MimeType.TEXT_HTML.equals(currentContentMimeType))
      {
         CodeMirrorTokenImpl newToken = new CodeMirrorTokenImpl("groovy code", TokenType.GROOVY_TAG, lineNumber, MimeType.APPLICATION_GROOVY);
         if (currentToken != null)
         {
            currentToken.addSubToken(newToken);
         }
         currentToken = newToken;

         currentContentMimeType = MimeType.APPLICATION_GROOVY;
         CodeMirrorParser.getParser(currentContentMimeType).init();
         node = Node.getNext(node); // pass parsed node
      }

      // recognize "%>" close tag
      else if (isGroovyCloseNode(nodeType, nodeContent) && !MimeType.TEXT_HTML.equals(currentContentMimeType))
      {
         currentToken = XmlParser.addTagBreak(lineNumber, currentToken, MimeType.TEXT_HTML);

         currentContentMimeType = MimeType.TEXT_HTML;
         CodeMirrorParser.getParser(currentContentMimeType).init();
         node = Node.getNext(node); // pass parsed node
      }
      
      currentToken = CodeMirrorParser.getParser(currentContentMimeType).parseLine(node, lineNumber, currentToken, true);  // call child parser
      
      if (node == null || Node.getName(node).equals("BR")) 
      {
         return currentToken;
      }

      return parseLine(Node.getNext(node), lineNumber, currentToken, false);  // call itself 

   }

   private boolean isGroovyOpenNode(String nodeType, String nodeContent)
   {
      return (nodeType != null) && (nodeContent != null) && nodeType.equals("gtmpl-groovy") && nodeContent.equals("&lt;%");
   }

   private boolean isGroovyCloseNode(String nodeType, String nodeContent)
   {
      return (nodeType != null) && (nodeContent != null) && nodeType.equals("gtmpl-groovy") && nodeContent.equals("%&gt;");
   };
}