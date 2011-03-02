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
package org.exoplatform.ide.editor.codevalidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.CodeValidator;
import org.exoplatform.ide.editor.api.DefaultCodeValidator;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.codeassitant.CodeError;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;


/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
@SuppressWarnings("serial")
public abstract class CodeValidatorImpl extends CodeValidator
{
   private static HashMap<String, CodeValidator> factory = new HashMap<String, CodeValidator>() {{
      put(MimeType.APPLICATION_GROOVY, new GroovyCodeValidator());
   }};  
   
   public static CodeValidator getValidator(String mimeType)
   {
      if (factory.containsKey(mimeType))
      {
         return factory.get(mimeType);
      }

      return new DefaultCodeValidator();
   }

   protected static List<CodeError> codeErrorList = new ArrayList<CodeError>();
   
   /**
    * Returns list of code error in line with lineNumber
    * @param lineNumber
    * @return
    */
   public List<CodeError> getCodeErrorList(int lineNumber)
   {
      return  getCodeErrorList(lineNumber, codeErrorList);
   }

   /**
    * Returns list of code error in line with lineNumber
    * @param lineNumber
    * @return
    */
   private List<CodeError> getCodeErrorList(int lineNumber, List<CodeError> codeErrors)
   {
      List<CodeError> lineCodeErrorList = new ArrayList<CodeError>();
      
      for (CodeError codeError: codeErrors)
      {
         if (codeError.getLineNumber() == lineNumber)
         {            
            lineCodeErrorList.add(codeError);
         }
      }

      return lineCodeErrorList;
   }
   
   /**
    * Get text summary of registered errors from the lineCodeErrorList within the line 
    * @param lineCodeErrorList
    * @return text summary of errors within the line
    */
   String getErrorSummary(List<CodeError> lineCodeErrorList)
   {
      String errorSummary = "";
      
      for (CodeError codeError: lineCodeErrorList)
      {
         switch(codeError.getType()) {
            case TYPE_ERROR:
               errorSummary += "'" + codeError.getIncorrectToken() + "' cannot be resolved to a type; ";
               break;
               
            default:
         }
      }
      
      return errorSummary;
   }
   
   /**
    * Update list of code errors and error marks
    * @param tokenList 
    * @param editor Code Editor
    */
   public abstract void validateCode(List<? extends Token> tokenList, Editor editor);

   /**
    * 
    * @param lineNumber
    * @return true if there is at list one code error in the line with lineNumber
    */
   public boolean isExistedCodeError(int lineNumber)
   {
      for (CodeError codeError: codeErrorList)
      {
         if (codeError.getLineNumber() == lineNumber)
         {            
            return true;
         }
      }

      return false;
   }
   
   void udpateErrorMarks(List<CodeError> newCodeErrorList, Editor editor)
   {        
      for (CodeError lastCodeError : codeErrorList)
      {
         editor.clearErrorMark(lastCodeError.getLineNumber());
      }

      List<CodeError> lineCodeErrorList;
      for (CodeError newCodeError : newCodeErrorList)
      {
         // TODO supress repetitevly setting error mark if there are several errors in the one line         
         lineCodeErrorList = getCodeErrorList(newCodeError.getLineNumber(), newCodeErrorList);
         editor.setErrorMark(newCodeError.getLineNumber(), getErrorSummary(lineCodeErrorList));
      }
      
//         if (newCodeErrorList != null && newCodeErrorList.size() != 0)
//         {
//            eventBus.fireEvent(new EditorCodeErrorFound(newCodeErrorList));
//         }
      
      codeErrorList = newCodeErrorList;
   }

   /**
    * Insert import statement "import <fqn>;" in the appropriate place of file
    * @param tokenList
    * @param fqn
    * @param editor
    */
   public abstract void insertImportStatement(List<TokenBeenImpl> tokenList, String fqn, Editor editor);

   /**
    * Extract tokens with mimeType from tokenList
    * @param tokenList
    * @param code
    * @param mimeType
    * @return
    */
   public static List<TokenBeenImpl> extractCode(List<TokenBeenImpl> tokenList, List<TokenBeenImpl> code, String mimeType)
   {
      for (TokenBeenImpl token : tokenList)
      {
         analizeToken(token, code, mimeType);  // update groovyCode
      }
      
      return code;
   }

   private static void analizeToken(TokenBeenImpl currentToken, List<TokenBeenImpl> code, String mimeType)
   {
      if (currentToken == null) 
      {
         return;
      }
      
      if (currentToken.getSubTokenList() != null)
      {
         for (TokenBeenImpl token : currentToken.getSubTokenList())
         {
            analizeToken(token, code, mimeType);
         }         
      }
      

      if (currentToken.getSubTokenList() != null)
      {
         if (MimeType.APPLICATION_GROOVY.equals(mimeType))
         {
            if (!TokenType.GROOVY_TAG.equals(currentToken.getType()))
            {
               return;
            }
         }
         
         for (TokenBeenImpl subtoken : currentToken.getSubTokenList())
         {
            if (mimeType.equals(subtoken.getMimeType())) 
            {
               code.add(subtoken);
            }
         }
      }   

   }
   
}
