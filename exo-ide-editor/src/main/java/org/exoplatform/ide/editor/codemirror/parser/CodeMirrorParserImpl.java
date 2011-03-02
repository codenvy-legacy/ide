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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.Parser;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.codemirror.Node;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class CodeMirrorParserImpl extends Parser
{
   // it is needed for complex files like HTML, GoogleGadget etc.
   private static HashMap<String, CodeMirrorParserImpl> factory = new HashMap<String, CodeMirrorParserImpl>();
   
   static
   {
      factory.put(MimeType.APPLICATION_JAVASCRIPT, new JavaScriptParser());
      factory.put(MimeType.TEXT_CSS, new CssParser());
      factory.put(MimeType.TEXT_HTML, new HtmlParser());
      factory.put(MimeType.TEXT_XML, new XmlParser());   
      factory.put(MimeType.APPLICATION_GROOVY, new GroovyParser());
   }

   protected static CodeMirrorParserImpl getParser(String mimeType)
   {
      if (factory.containsKey(mimeType))
      {
         return factory.get(mimeType);
      }

      return new DefaultParser();
   }
   
   /** 
    * @param node
    * @param lineNumber
    * @param tokenList
    * @param hasParentParser indicates is parser calles by another parser, e.g. JavaScriptParser is called by HtmlParser
    * @return token list with tokens gathered from node chains from start node to <br> node
    */
   TokenBeenImpl parseLine(JavaScriptObject node, int lineNumber, TokenBeenImpl currentToken, boolean hasParentParser)
   {
      return currentToken;
   }

   public void init()
   {
   }

   @Override  
   public List<TokenBeenImpl> getTokenList(JavaScriptObject editor)
   {
      List<TokenBeenImpl> emptyTokenList = new ArrayList<TokenBeenImpl>();

      if (editor == null)
         return emptyTokenList;

      init();

      JavaScriptObject node;

      TokenBeenImpl rootToken = new TokenBeenImpl();
      TokenBeenImpl currentToken = rootToken;
      currentToken.setSubTokenList(emptyTokenList);

      // fix error when editor.nthLine(1) = null
      // parse first line
      if (Node.getFirstLine(editor) != null)
      {
         currentToken = parseLine(Node.getFirstLine(editor), 1, currentToken, false);
      }

      // parse lines from second
      if (Node.getLastLineNumber(editor) > 1)
      {
         for (int lineNumber = 2; lineNumber <= Node.getLastLineNumber(editor); lineNumber++)
         {
            node = Node.get(editor, lineNumber);

            // fix error when editor.nthLine(1) = null
            if (node == null)
            {
               continue;
            }

            currentToken = parseLine(Node.getNext(node), lineNumber, currentToken, false);
         }
      }

      return rootToken.getSubTokenList();
   };
   
   private static String possibleMimeType;

   private static int nearestTokenLineNumber;
   
   /**
    * Recognize mimeType of line with lineNumber.  
    * @param targetLineNumber
    * @param tokenList
    * @return Returns mimeType of closes token.START_DELIMITER with token.lineNumber <= lineNumber. If there is no such START_DELIMITER in the tokenList, then returns mimeType of last token.FINISH_DELIMITER with token.lineNumber > lineNumber, or MimeType of firstToken, or null if TokenList is empty.
    */
   public static String getLineMimeType(int targetLineNumber, List<TokenBeenImpl> tokenList)
   {
      if (tokenList == null || tokenList.size() == 0)
         return null;

      possibleMimeType = tokenList.get(0).getMimeType();
      nearestTokenLineNumber = tokenList.get(0).getLineNumber();

      for (TokenBeenImpl token : tokenList)
      {
         if (token.getLineNumber() > targetLineNumber)
            break;

         searchLineMimeType(targetLineNumber, token);
      }

      return possibleMimeType;
   }

   private static void searchLineMimeType(int targetLineNumber, TokenBeenImpl currentToken)
   {
      // search appropriate token among the sub token
      List<TokenBeenImpl> subTokenList = currentToken.getSubTokenList();

      if (subTokenList != null && subTokenList.size() != 0)
      {
         for (TokenBeenImpl token : subTokenList)
         {
            if (token.getLineNumber() > targetLineNumber)
               break;

            searchLineMimeType(targetLineNumber, token);
         }
      }

      int currentTokenLineNumber = currentToken.getLineNumber();
      if ((currentTokenLineNumber <= targetLineNumber) && (currentTokenLineNumber >= nearestTokenLineNumber) // taking in mind the last token among them in the line
      )
      {
         nearestTokenLineNumber = currentTokenLineNumber;
         possibleMimeType = currentToken.getMimeType();
      }
   }
}
