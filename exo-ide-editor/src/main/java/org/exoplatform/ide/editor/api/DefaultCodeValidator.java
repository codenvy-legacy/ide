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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ide.editor.api.codeassitant.CodeError;
import org.exoplatform.ide.editor.api.codeassitant.Token;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: DefaultCodeValidator Feb 10, 2011 9:54:23 AM evgen $
 *
 */
public class DefaultCodeValidator extends CodeValidator
{

   /**
    * @see org.exoplatform.ide.editor.api.CodeValidator#validateCode(java.util.List, org.exoplatform.ide.editor.api.Editor)
    */
   @Override
   public void validateCode(List<? extends Token> tokenList, Editor editor)
   {
   }

   /**
    * @see org.exoplatform.ide.editor.api.CodeValidator#isExistedCodeError(int)
    */
   @Override
   public boolean isExistedCodeError(int lineNumber)
   {
      return false;
   }

   /**
    * @see org.exoplatform.ide.editor.api.CodeValidator#getCodeErrorList(int)
    */
   @Override
   public List<CodeError> getCodeErrorList(int lineNumber)
   {
      return new ArrayList<CodeError>();
   }

}
