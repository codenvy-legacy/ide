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
package org.exoplatform.ide.editor.api.codeassitant;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class CodeError
{
   CodeErrorType type; 
   
   String incorrectToken; 
   
   int lineNumber; 

//   // it is hard to get this value based on tokenList within the Parser.validateCode() method   
//   int indexOfIncorrectToken; 
   
   public CodeError(CodeErrorType type, String incorrectToken, int lineNumber)
   {
      this.type = type;
      this.incorrectToken = incorrectToken;
      this.lineNumber = lineNumber;
   }
   
   public CodeErrorType getType()
   {
      return type;
   }
   
   public String getIncorrectToken()
   {
      return incorrectToken;
   }
   
   public int getLineNumber()
   {
      return lineNumber;
   }
   
   public enum CodeErrorType 
   {
      TYPE_ERROR;
   }
}
