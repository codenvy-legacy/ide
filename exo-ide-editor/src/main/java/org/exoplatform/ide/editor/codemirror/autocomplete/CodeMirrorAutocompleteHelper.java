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
package org.exoplatform.ide.editor.codemirror.autocomplete;

import java.util.HashMap;
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.api.codeassitant.autocompletehelper.AutoCompleteHelper;
import org.exoplatform.ide.editor.api.codeassitant.autocompletehelper.DefaultAutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.CodeMirrorTokenImpl;
import org.exoplatform.ide.editor.codemirror.Node;
import org.exoplatform.ide.editor.codemirror.parser.GroovyParser;
import org.exoplatform.ide.editor.codemirror.parser.JavaScriptParser;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
@SuppressWarnings("serial")
public abstract class CodeMirrorAutocompleteHelper extends AutoCompleteHelper
{
   private static HashMap<String, AutoCompleteHelper> factory = new HashMap<String, AutoCompleteHelper>() {{
//      put(MimeType.APPLICATION_GROOVY, new GroovyAutocompleteHelper());
      put(MimeType.APPLICATION_JAVASCRIPT, new JavaScriptAutocompleteHelper());
   }};  
   
   protected static AutoCompleteHelper getAutocompleteHelper(String mimeType)
   {
      if (factory.containsKey(mimeType))
      {
         return factory.get(mimeType);
      }

      return new DefaultAutocompleteHelper();
   }  
   
   /**
    * 
    * @param node
    * @param cursorPosition within the line
    * @return  line content node " java.lang.String.ch" -> "java.lang.String",  "<End-Of-Line>address.tes_" -> "address"
    */
   protected static String getStatementBeforePoint(JavaScriptObject node, int cursorPosition)
   {
      String nodeContent;
      String nodeType;
      
      String statement = "";
      
      while (node != null && !(node).equals("BR"))
      {         
         // pass nodes after the cursor
         if (Node.getNodePositionInLine(node) >= cursorPosition) 
         {
            // get previous token
            node = Node.getPrevious(node);
         }
         else
         {
            nodeContent = Node.getContent(node);
            nodeType = Node.getType(node);
   
            if ((!isVariable(nodeType) && !isPoint(nodeType, nodeContent.trim()))  // filter part with non-variable and non-point symbols, not ". " symbol
                   || (
                         nodeContent.indexOf(" ") != -1  // filter nodes like "String " in sentence "String name._", or like ". " in sentence ". String_", or like ". _" in sentence like "String. _", or like "ch " in sentence like "name.ch _"  
                         && (statement.length() > 0  // filter nodes like "name ._" or "name. ch._"
                               || (Node.getNodePositionInLine(node) + nodeContent.length()) <= cursorPosition  // filter nodes like "name. _" or "name.ch _"
                             ) 
                      ) 
               )   
            {
               break;
            }
   
            statement = nodeContent + statement;
            
            // get previous token
            node = Node.getPrevious(node);
         }
      }      
      
      if (statement.lastIndexOf(".") == -1)
      {
         // return "" for statement like "name_"
         return "";
      }
      else
      {
         // clear last chain like ".ch_" in node "java.lang.String.ch_", or "." in node "name.", or statement without point like "name_"         
         return statement.substring(0, statement.lastIndexOf("."));
      }
   }

   private static boolean isVariable(String nodeType)
   {
      return GroovyParser.isGroovyVariable(nodeType) || JavaScriptParser.isJsVariable(nodeType) || JavaScriptParser.isJsLocalVariable(nodeType);
   }

   private static boolean isPoint(String nodeType, String nodeContent)
   {
      return GroovyParser.isPoint(nodeType, nodeContent) || JavaScriptParser.isPoint(nodeType, nodeContent);
   }
   
   
   /**
    * @param targetLineNumber
    * @param firstContainerLineNumber
    * @return true if firstContainterLine number > targetLineNumber
    */   
   protected static boolean isContainerTokenAfterTheCurrentLine(int targetLineNumber, int firstContainerLineNumber)
   {
      return firstContainerLineNumber > targetLineNumber;
   }   

   
   /**
    * @param targetLineNumber
    * @param lastContainerLineNumber
    * @return true if targetLineNumber >= lastContainerLineNumber
    */   
   protected static boolean isContainerEndedBeforeTheCurrentLine(int targetLineNumber, int lastContainerLineNumber)
   {
      return targetLineNumber >= lastContainerLineNumber;
   }      

   
   protected static CodeMirrorTokenImpl nearestToken;
   
   protected static void searchNearestToken(int targetLineNumber, CodeMirrorTokenImpl currentToken)
   {
      // test if this is function and it ended not before target line
      if (TokenType.FUNCTION.equals(currentToken.getType()) 
               && isContainerEndedBeforeTheCurrentLine(targetLineNumber, currentToken.getLastLineNumber()))
      {
         return;
      }
    
      // search nearest token among the sub token
      List<CodeMirrorTokenImpl> subTokenList = currentToken.getSubTokenList();

         
      if (subTokenList != null && subTokenList.size() != 0)
      {
         for (CodeMirrorTokenImpl token : subTokenList)
         {
            if (isContainerTokenAfterTheCurrentLine(targetLineNumber, token.getLineNumber()))
               break;

            searchNearestToken(targetLineNumber, token);
         }
      }

      int currentTokenLineNumber = currentToken.getLineNumber();
      int nearestTokenLineNumber = nearestToken.getLineNumber();
      // taking in mind the last token among them in the line
      if ((currentTokenLineNumber <= targetLineNumber) && (currentTokenLineNumber >= nearestTokenLineNumber))
      {
         nearestToken = currentToken;
      }
   }   
   
   protected static CodeMirrorTokenImpl searchGenericTokenAmongLocalVariables(String nodeContent, CodeMirrorTokenImpl nearestToken, CodeMirrorTokenImpl methodToken)
   {
      for (CodeMirrorTokenImpl subtoken: methodToken.getSubTokenList())
      {
         if (TokenType.VARIABLE.equals(subtoken.getType())
                && nodeContent.equals(subtoken.getName()))
         {
            return subtoken;
         }
         
         // test if this is last node before target node
         if (subtoken.equals(nearestToken))
         {
           return null;
         }
      }
      
      return null;
   } 
     
}
