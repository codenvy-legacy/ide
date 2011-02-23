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
import java.util.List;

import org.exoplatform.ide.editor.api.Parser;
import org.exoplatform.ide.editor.codemirror.CodeMirrorTokenImpl;
import org.exoplatform.ide.editor.codemirror.Node;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class CodeMirrorParser extends Parser
{

   /** 
    * @param node
    * @param lineNumber
    * @param tokenList
    * @param hasParentParser indicates is parser calles by another parser, e.g. JavaScriptParser is called by HtmlParser
    * @return token list with tokens gathered from node chains from start node to <br> node
    */
   CodeMirrorTokenImpl parseLine(JavaScriptObject node, int lineNumber, CodeMirrorTokenImpl currentToken, boolean hasParentParser)
   {
      return currentToken;
   }

   public void init()
   {
   }

   @Override  
   public List<CodeMirrorTokenImpl> getTokenList(JavaScriptObject editor)
   {
      List<CodeMirrorTokenImpl> emptyTokenList = new ArrayList<CodeMirrorTokenImpl>();

      if (editor == null)
         return emptyTokenList;

      init();

      JavaScriptObject node;

      CodeMirrorTokenImpl rootToken = new CodeMirrorTokenImpl();
      CodeMirrorTokenImpl currentToken = rootToken;
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
   public static String getLineMimeType(int targetLineNumber, List<CodeMirrorTokenImpl> tokenList)
   {
      if (tokenList == null || tokenList.size() == 0)
         return null;

      possibleMimeType = tokenList.get(0).getMimeType();
      nearestTokenLineNumber = tokenList.get(0).getLineNumber();

      for (CodeMirrorTokenImpl token : tokenList)
      {
         if (token.getLineNumber() > targetLineNumber)
            break;

         searchLineMimeType(targetLineNumber, token);
      }

      return possibleMimeType;
   }

   private static void searchLineMimeType(int targetLineNumber, CodeMirrorTokenImpl currentToken)
   {
      // search appropriate token among the sub token
      List<CodeMirrorTokenImpl> subTokenList = currentToken.getSubTokenList();

      if (subTokenList != null && subTokenList.size() != 0)
      {
         for (CodeMirrorTokenImpl token : subTokenList)
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
