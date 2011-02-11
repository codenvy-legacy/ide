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
package org.exoplatform.ide.editor.api;

import java.util.List;

import org.exoplatform.ide.editor.api.codeassitant.Token;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Parser Feb 9, 2011 4:55:57 PM evgen $
 *
 */
public abstract class Parser
{
   private static String possibleMimeType;

   private static int nearestTokenLineNumber;
   
   public abstract List<Token> getTokenList(JavaScriptObject editor);
   
   /**
    * Recognize mimeType of line with lineNumber.  
    * @param targetLineNumber
    * @param tokenList
    * @return Returns mimeType of closes token.START_DELIMITER with token.lineNumber <= lineNumber. If there is no such START_DELIMITER in the tokenList, then returns mimeType of last token.FINISH_DELIMITER with token.lineNumber > lineNumber, or MimeType of firstToken, or null if TokenList is empty.
    */
   public static String getLineMimeType(int targetLineNumber, List<Token> tokenList)
   {
//      if (tokenList == null || tokenList.size() == 0)
//         return null;
//
//      possibleMimeType = tokenList.get(0).getMimeType();
//      nearestTokenLineNumber = tokenList.get(0).getLineNumber();
//
//      for (Token token : tokenList)
//      {
//         if (token.getLineNumber() > targetLineNumber)
//            break;
//
//         searchLineMimeType(targetLineNumber, token);
//      }
//
//      return possibleMimeType;
      //TODO
      return null;
   }

//   private static void searchLineMimeType(int targetLineNumber, Token currentToken)
//   {
//      // search appropriate token among the sub token
//      List<Token> subTokenList = currentToken.getSubTokenList();
//
//      if (subTokenList != null && subTokenList.size() != 0)
//      {
//         for (Token token : subTokenList)
//         {
//            if (token.getLineNumber() > targetLineNumber)
//               break;
//
//            searchLineMimeType(targetLineNumber, token);
//         }
//      }
//
//      int currentTokenLineNumber = currentToken.getLineNumber();
//      if ((currentTokenLineNumber <= targetLineNumber) && (currentTokenLineNumber >= nearestTokenLineNumber) // taking in mind the last token among them in the line
//      )
//      {
//         nearestTokenLineNumber = currentTokenLineNumber;
//         possibleMimeType = currentToken.getMimeType();
//      }
//   }
}
