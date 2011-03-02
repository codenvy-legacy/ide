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

import java.util.LinkedList;
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.codeassitant.CodeError;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;


/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class GroovyTemplateCodeValidator extends CodeValidatorImpl
{
    
   List<TokenBeenImpl> groovyCode;
   
   /**
    * Updates list of code errors and error marks. Also updates the fqn of tokens within the tokenList 
    * @param tokenList 
    * @param editor Code Editor
    */
   public void validateCode(List<? extends Token> tokenList, Editor editor)
   {          
      if (tokenList == null || tokenList.isEmpty())
      {
         // clear code error marks
         for (CodeError lastCodeError : codeErrorList)
         {
            editor.clearErrorMark(lastCodeError.getLineNumber());
         }
         return;
      }   
           
      groovyCode = extractCode((List<TokenBeenImpl>)tokenList, new LinkedList<TokenBeenImpl>(), MimeType.APPLICATION_GROOVY);

      CodeValidatorImpl.getValidator(MimeType.APPLICATION_GROOVY).validateCode(groovyCode, editor);
   }

   @Override
   public void insertImportStatement(List<TokenBeenImpl> tokenList, String fqn, Editor editor)
   {
      if (this.groovyCode == null)
      {
         this.groovyCode = extractCode(tokenList, new LinkedList<TokenBeenImpl>(), MimeType.APPLICATION_GROOVY);
      }
      
      if (GroovyCodeValidator.shouldImportStatementBeInsterted(groovyCode, fqn))
      {      
         int appropriateLineNumber = GroovyCodeValidator.getAppropriateLineNumberToInsertImportStatement(tokenList);
         
         if (appropriateLineNumber > 1)
         {
            editor.insertIntoLine("import " + fqn + "\n", appropriateLineNumber);
         }
         else
         {
            editor.insertIntoLine("<%\n  import " + fqn + "\n%>\n", 1);
         }
      }
   }
}
