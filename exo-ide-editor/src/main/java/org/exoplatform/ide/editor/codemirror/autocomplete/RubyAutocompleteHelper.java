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
package org.exoplatform.ide.editor.codemirror.autocomplete;

import java.util.List;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codemirror.Node;
import org.exoplatform.ide.editor.codemirror.parser.RubyParser;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class RubyAutocompleteHelper extends AutocompleteHelper
{

   public Token getTokenBeforeCursor(JavaScriptObject node, int lineNumber, int cursorPosition, List<? extends Token> tokenList, String currentLineMimeType)
   {
      return getTokenBeforeCursor(node, lineNumber, cursorPosition, tokenList);
   }   
   
   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.autocompletehelper.AutoCompleteHelper#getTokenBeforeCursor(com.google.gwt.core.client.JavaScriptObject, int, int, java.util.List)
    */
   public Token getTokenBeforeCursor(JavaScriptObject javaScriptNode, int lineNumber, int cursorPosition, List<? extends Token> tokenList)
   {
      // interrupt at the end of the line or content
      if (javaScriptNode == null)
      {
         return null;
      }

      Node nodeBeforeCursor = getNodeBeforePoint(javaScriptNode, cursorPosition);
      
      TokenBeenImpl tokenBeforeCursor = null;
      
      if (nodeBeforeCursor != null 
               && nodeBeforeCursor.getContent() != null
               && !nodeBeforeCursor.getContent().isEmpty())
      {       
         int numberOfChainsBetweenPoint = nodeBeforeCursor.getContent().split("[.]").length;   // nodeContent.split("[.]") returns 1 for "name", and 3 for "java.lang.Integer"         

         // search token for variables like "name._" or "name.ch_"
         if (numberOfChainsBetweenPoint == 1)
         {
            tokenBeforeCursor = getGenericToken(nodeBeforeCursor.getContent(), lineNumber, (List<TokenBeenImpl>) tokenList, RubyParser.isVariable(nodeBeforeCursor.getType()));
//            switch (RubyParser.isVariable(nodeBeforeCursor.getType()))
//            {
//               case LOCAL_VARIABLE:
//                  tokenBeforeCursor = getLocalVariableGenericToken(nodeBeforeCursor.getContent(), lineNumber, (List<TokenBeenImpl>) tokenList);
//                  break;
//                  
//               case GLOBAL_VARIABLE:
//                  break;
//                  
//               case CLASS_VARIABLE:
//                  break;
//                  
//               case INSTANCE_VARIABLE:
//                  break;
//                  
//               case CONSTANT:
//                  break;
//                  
//               default:
//                  return null;
//            }
                        
            if (tokenBeforeCursor != null) 
            {
               TokenBeenImpl newToken = new TokenBeenImpl(
                  tokenBeforeCursor.getName(), 
                  tokenBeforeCursor.getType(), 
                  lineNumber, 
                  tokenBeforeCursor.getMimeType(), 
                  tokenBeforeCursor.getElementType()
               );               
               
               return (Token) newToken;
            }
         }
      }
         
      return null;
   }
   
   private TokenBeenImpl getGenericToken(String nodeContent, int targetLineNumber, List<TokenBeenImpl> tokenList, TokenType variableType)
   {
      if (tokenList == null || tokenList.size() == 0)
         return null;

      nearestToken = tokenList.get(0);
            
      for (TokenBeenImpl token : tokenList)
      {
         if (isContainerTokenAfterTheCurrentLine(targetLineNumber, token.getLineNumber()))
            break;

         searchNearestToken(targetLineNumber, token);
      }
      
      TokenBeenImpl genericToken = null;
      
      if (nearestToken != null)
      {
         if (nearestToken.getParentToken() != null)
         {
            // search as local variables among the subTokens
            for (TokenBeenImpl subtoken: nearestToken.getParentToken().getSubTokenList())
            {
               if (variableType.equals(subtoken.getType())
                      && nodeContent.equals(subtoken.getName()))
               {
                  genericToken = subtoken;
               }
               
               // test if this is last node before target node
               if (subtoken.equals(nearestToken))
               {
                  break;
               }
            }

            if (genericToken != null) 
            {
               return genericToken;
            }
            else
            {
               if (nearestToken.getParentToken().getParentToken() != null)
               {
                  return getGenericToken(nodeContent, nearestToken.getParentToken().getLineNumber() - 1, nearestToken.getParentToken().getParentToken().getSubTokenList(), variableType);
               }
            }
         }
         
         if (variableType.equals(nearestToken.getType())
                     && nodeContent.equals(nearestToken.getName()))
         {
           return nearestToken;
         }
         

      } 
         
         
      return null;
   }

   /**
    * 
    * @param javaScriptNode
    * @param cursorPosition within the line
    * @return  line content node " java.lang.String.ch" -> "java.lang.String",  "<End-Of-Line>address.tes_" -> "address"
    */
   protected static Node getNodeBeforePoint(JavaScriptObject javaScriptNode, int cursorPosition)
   {
      String nodeContent;
      String nodeType;
      
      String statement = "";
      
      while (javaScriptNode != null && !Node.isLineBreak(javaScriptNode))
      {         
         // pass nodes after the cursor
         if (Node.getNodePositionInLine(javaScriptNode) >= cursorPosition) 
         {
            // get previous token
            javaScriptNode = Node.getPrevious(javaScriptNode);
         }
         else
         {
            nodeContent = Node.getContent(javaScriptNode);
            nodeType = Node.getType(javaScriptNode);
   
            if (((RubyParser.isVariable(nodeType) != null) && !isPoint(nodeType, nodeContent.trim()))  // filter part with non-variable and non-point symbols, not ". " symbol
                   || (
                         nodeContent.indexOf(" ") != -1  // filter nodes like "String " in sentence "String name._", or like ". " in sentence ". String_", or like ". _" in sentence like "String. _", or like "ch " in sentence like "name.ch _"  
                         && (statement.length() > 0  // filter nodes like "name ._" or "name. ch._"
                               || (Node.getNodePositionInLine(javaScriptNode) + nodeContent.length()) <= cursorPosition  // filter nodes like "name. _" or "name.ch _"
                             ) 
                      ) 
               )   
            {
               break;
            }
   
            statement = nodeContent + statement;
            
            // get previous token
            javaScriptNode = Node.getPrevious(javaScriptNode);
         }
      }      
      
      if (statement.lastIndexOf(".") == -1)
      {
         // return "" for statement like "name_"
         return null;
      }
      else
      {
         // clear last chain like ".ch_" in node "java.lang.String.ch_", or "." in node "name.", or statement without point like "name_"         
         return new Node(javaScriptNode);
      }
   }

}