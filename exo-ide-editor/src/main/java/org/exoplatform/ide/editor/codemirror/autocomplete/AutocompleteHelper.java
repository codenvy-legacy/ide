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

import java.util.List;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codemirror.Node;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
@SuppressWarnings("serial")
public abstract class AutocompleteHelper
{
   
   public abstract Token getTokenBeforeCursor(JavaScriptObject node, int lineNumber, int cursorPosition, List<? extends Token> tokenList, String currentLineMimeType);
   
   /**
    * 
    * @param node
    * @param cursorPosition within the line
    * @return  line content node " java.lang.String.ch" -> "java.lang.String",  "<End-Of-Line>address.tes_" -> "address"
    */
   public String getStatementBeforePoint(JavaScriptObject node, int cursorPosition)
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

   /**
    * Is node type of variable
    * @see org.exoplatform.ide.editor.codemirror.autocomplete.AutocompleteHelper#isVariable(java.lang.String)
    */
   public abstract boolean isVariable(String nodeType);

   /**
    * Is node type of point
    * @see org.exoplatform.ide.editor.codemirror.autocomplete.AutocompleteHelper#isVariable(java.lang.String)
    */
   public abstract boolean isPoint(String nodeType, String nodeContent);
  
   public static TokenBeenImpl nearestToken;
   
   public static void searchNearestToken(int targetLineNumber, TokenBeenImpl currentToken)
   {
      // test if this is function and it ended not before target line
      if (TokenType.FUNCTION.equals(currentToken.getType()) 
               // test is Container Ended Before The CurrentLine
               && (targetLineNumber >= currentToken.getLastLineNumber()))
      {
         return;
      }
    
      // search nearest token among the sub token
      List<TokenBeenImpl> subTokenList = currentToken.getSubTokenList();

         
      if (subTokenList != null && subTokenList.size() != 0)
      {
         for (TokenBeenImpl token : subTokenList)
         {
            // test is Container Token After The Current Line
            if (token.getLineNumber() > targetLineNumber)
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
   
   public static TokenBeenImpl searchGenericTokenAmongMethodVariables(String nodeContent, TokenBeenImpl nearestToken, TokenBeenImpl methodToken)
   {
      for (TokenBeenImpl subtoken: methodToken.getSubTokenList())
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
   
   public static TokenBeenImpl searchGenericTokenAmongProperties(String nodeContent, TokenBeenImpl classToken)
   {
      for (TokenBeenImpl subtoken: classToken.getSubTokenList())
      {
         if (TokenType.PROPERTY.equals(subtoken.getType())
                && nodeContent.equals(subtoken.getName()))
         {
            return subtoken;
         }
      }
   
      return null;
   }

   public static TokenBeenImpl searchGenericTokenAmongParameters(String nodeContent, List<TokenBeenImpl> parameters)
   {
      if (parameters == null)
         return null;
      
      for (TokenBeenImpl parameter: parameters)
      {
         if (nodeContent.equals(parameter.getName()))
         {
            return parameter;
         }
      }
   
      return null;
   }


   public static TokenBeenImpl searchGenericTokenAmongVariables(String nodeContent, TokenBeenImpl parentToken)
   {
      for (TokenBeenImpl subtoken: parentToken.getSubTokenList())
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


   static TokenBeenImpl possibleContainerToken;
   static int nearestTokenLineNumber;   
   
   /**
    * Recognize container token of line with lineNumber.  
    * @param targetLineNumber
    * @param tokenList
    * @return container token with token.lineNumber <= targetLineNumber < token.lastLineNumber.
    */
   public TokenBeenImpl getContainerToken(int targetLineNumber, List<TokenBeenImpl> tokenList)
   {
      if (tokenList == null || tokenList.size() == 0)
         return null;
   
      possibleContainerToken = null;
      nearestTokenLineNumber = 0;
   
      for (TokenBeenImpl token : tokenList)
      {
         // break if token is started at the line after the targetLine, that is Container Token After The CurrentLine
         if (token.getLineNumber() > targetLineNumber)
         {
            break;
         }
         
         // Test if (token.lineNumber > targetLineNumber) or (targetLineNumber >= token.lastLineNumber), that is Current Line After The Container Token 
         else if (targetLineNumber >= token.getLastLineNumber())
         {
            continue;
         }
         
         else if (isPossibleContainerTokenType(token)) 
         {
            searchContainerToken(targetLineNumber, token);
         }
      }
   
      return possibleContainerToken;
   }

   public boolean isPossibleContainerTokenType(TokenBeenImpl token)
   {
      return TokenType.CLASS.equals(token.getType()) || TokenType.METHOD.equals(token.getType()) || TokenType.INTERFACE.equals(token.getType());
   }

   void searchContainerToken(int targetLineNumber, TokenBeenImpl currentToken)
   {
      // search appropriate token among the sub token
      List<TokenBeenImpl> subTokenList = currentToken.getSubTokenList();
   
      if (subTokenList != null && subTokenList.size() != 0)
      {
         for (TokenBeenImpl token : subTokenList)
         {
            // break if token is started at the line after the targetLine, that is Container Token After The CurrentLine
            if (token.getLineNumber() > targetLineNumber)
            {
               break;
            }
            
            // Test if (token.lineNumber > targetLineNumber) or (targetLineNumber >= token.lastLineNumber), that is CurrentLine After The Container Token 
            else if (targetLineNumber >= token.getLastLineNumber())
            {
               continue;
            }
            
            else if (isPossibleContainerTokenType(token)) 
            {
               searchContainerToken(targetLineNumber, token);
            }
         }
      }
   
      int currentTokenLineNumber = currentToken.getLineNumber();
      if ((currentTokenLineNumber <= targetLineNumber) && (currentTokenLineNumber >= nearestTokenLineNumber) // taking in mind the last token among them in the line
      )
      {
         nearestTokenLineNumber = currentTokenLineNumber;
         possibleContainerToken = currentToken;
      }
   }

}