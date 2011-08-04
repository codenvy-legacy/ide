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
package org.exoplatform.ide.editor.jsp.client.codemirror;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.CodeLine;
import org.exoplatform.ide.editor.api.CodeLine.CodeType;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.codemirror.CodeValidator;
import org.exoplatform.ide.editor.java.client.codemirror.JavaCodeValidator;


/**
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class JspCodeValidator extends CodeValidator
{
    
   List<? extends Token> javaCode;
   
   JavaCodeValidator javaCodeValidator = new JavaCodeValidator();
   
   /**
    * Updates list of code errors and error marks. Also updates the fqn of tokens within the tokenList 
    * @param tokenList 
    */
   public List<CodeLine> getCodeErrorList(List<? extends Token> tokenList)
   {
      if (tokenList == null || tokenList.isEmpty())
      {
         return new ArrayList<CodeLine>();
      }

      javaCode = extractCode((List<TokenBeenImpl>)tokenList, new LinkedList<TokenBeenImpl>(), MimeType.APPLICATION_JAVA);

      return javaCodeValidator.getCodeErrorList(javaCode);
   }

   @Override
   public CodeLine getImportStatement(List<? extends Token> tokenList, String fqn)
   {
      if (this.javaCode == null)
      {
         this.javaCode = extractCode((List<TokenBeenImpl>)tokenList, new LinkedList<TokenBeenImpl>(), MimeType.APPLICATION_JAVA);
      }
      
      if (javaCodeValidator.shouldImportStatementBeInsterted((List<TokenBeenImpl>) javaCode, fqn))
      {      
         int appropriateLineNumber = JavaCodeValidator.getAppropriateLineNumberToInsertImportStatement((List<TokenBeenImpl>)tokenList);
         
         if (appropriateLineNumber > 1)
         {
            return new CodeLine(CodeType.IMPORT_STATEMENT, "import " + fqn + ";\n", appropriateLineNumber);
         }
         else
         {
            return new CodeLine(CodeType.IMPORT_STATEMENT, "<%\n  import " + fqn + ";\n%>\n", 1);
         }
      }
      
      return null;
   } 
}
