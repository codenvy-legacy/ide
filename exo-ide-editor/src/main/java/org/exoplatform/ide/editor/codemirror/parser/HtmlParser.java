/*
 * Copyright (C) 2011 eXo Platform SAS.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codemirror.Node;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class HtmlParser extends CodeMirrorParserImpl
{
   private String currentContentMimeType;
   
   private String lastNodeContent;
   
   private String lastNodeType;
   
   static List<String> autoSelfClosers;
   
   static {
      String[] autoSelfClosersArray = {"br", "img", "hr", "link", "input", "meta", "col", "frame", "base", "area"};      
      autoSelfClosers = new ArrayList<String>(Arrays.asList(autoSelfClosersArray));
   }
   
   @Override
   public void init()
   {
      currentContentMimeType = MimeType.TEXT_HTML;
   }
   
   @Override
   TokenBeenImpl parseLine(JavaScriptObject node, int lineNumber, TokenBeenImpl currentToken, boolean hasParentParser)
   {
      // interrupt at the end of the document
      if (node == null) 
         return currentToken;

      String nodeContent = Node.getContent(node).trim(); // returns text without ended space " " in the text
      String nodeType = Node.getType(node);    
      
      // recognize tag node
      if (XmlParser.isTagNode(nodeType)) 
      {            
         // recognize open tag starting with "<"
         if (XmlParser.isOpenTagNode(lastNodeType, lastNodeContent))  
         {  
            // recognize <script> or </script> tags
            if (isScriptTagNode(nodeType, nodeContent)) 
            {
               currentToken = XmlParser.addTag(currentToken, "script", lineNumber, MimeType.APPLICATION_JAVASCRIPT);
               currentContentMimeType = MimeType.APPLICATION_JAVASCRIPT;
               CodeMirrorParserImpl.getParser(currentContentMimeType).init();
               // node = getNext(node);     // pass parsed node
            }

            // recognize <style> or </style> tags
            else if (isStyleTagNode(nodeType, nodeContent))
            {
               currentToken = XmlParser.addTag(currentToken, "style", lineNumber, MimeType.TEXT_CSS);
               currentContentMimeType = MimeType.TEXT_CSS;
               CodeMirrorParserImpl.getParser(currentContentMimeType).init();
               // node = getNext(node);     // pass parsed node
            }            
            
            else  
            {
               currentToken = XmlParser.addTag(currentToken, nodeContent, lineNumber, MimeType.TEXT_HTML);
               
               // recognize autoSelfClosers tags and add tag break
               if (autoSelfClosers.contains(nodeContent))
               {
                  currentToken = XmlParser.closeTag(lineNumber, currentToken);                  
               }
            }            
         }
         
         // recognize close tag starting with "</"
         else if (XmlParser.isCloseStartTagNode(lastNodeType, lastNodeContent)
                     && ! (lastSubTokenIsAutoSelfClosersTag(currentToken))  // filter autoSelfClosers tag 
                 )
         {
            currentToken = XmlParser.closeTag(lineNumber, currentToken);
            init();
         }
      }
      
      // recognize close tag finished with "/>"
      else if (XmlParser.isCloseFinishTagNode(nodeType, nodeContent) 
               && ! (lastSubTokenIsAutoSelfClosersTag(currentToken))  // filter autoSelfClosers tag 
              )
      {
         currentToken = XmlParser.closeTag(lineNumber, currentToken);
         init();         
      }
      
      lastNodeContent = nodeContent;
      lastNodeType = nodeType;      

      // interrupt at the end of the line      
      if (node == null || Node.getName(node).equals("BR"))
      {
         return currentToken;
      }
      
      if (! MimeType.TEXT_HTML.equals(currentContentMimeType))
      {
         currentToken = CodeMirrorParserImpl.getParser(currentContentMimeType).parseLine(node, lineNumber, currentToken, true);  // call child parser
      }

      if (hasParentParser) 
      {
         return currentToken;  // return current token to parent parser
      } 
      
      return parseLine(Node.getNext(node), lineNumber, currentToken, false);  // call itself 
   }

   private boolean lastSubTokenIsAutoSelfClosersTag(TokenBeenImpl currentToken)
   {
      List<TokenBeenImpl> subTokenList = currentToken.getSubTokenList();
      if (subTokenList == null)
         return false;
      
      TokenBeenImpl lastSubToken = subTokenList.get(subTokenList.size() - 1);
      
      return subTokenList != null 
               && lastSubToken.getType().equals(TokenType.TAG)
               && autoSelfClosers.contains(lastSubToken.getName());
   }

   private static boolean isStyleTagNode(String nodeType, String nodeContent)
   {
      return XmlParser.isTagNode(nodeType) && (nodeContent != null) && nodeContent.equals("style");
   }

   private static boolean isScriptTagNode(String nodeType, String nodeContent)
   {
      return XmlParser.isTagNode(nodeType) && (nodeContent != null) && nodeContent.equals("script");
   }

}