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
package org.exoplatform.ide.editor.php.client.codeassistant;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.php.client.codeassistant.ui.PhpClassWidget;
import org.exoplatform.ide.editor.php.client.codeassistant.ui.PhpConstantWidget;
import org.exoplatform.ide.editor.php.client.codeassistant.ui.PhpFunctionWidget;
import org.exoplatform.ide.editor.php.client.codeassistant.ui.PhpPropertyWidget;
import org.exoplatform.ide.editor.php.client.codeassistant.ui.PhpTokenKeyWordWidget;
import org.exoplatform.ide.editor.php.client.codeassistant.ui.PhpVariableWidget;

/**
 * Factory of {@link TokenWidget}, need to build token UI representation.
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class PhpTokenWidgetFactory implements TokenWidgetFactory
{

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory#buildTokenWidget(org.exoplatform.ide.editor.api.codeassitant.Token)
    */
   @Override
   public TokenWidget buildTokenWidget(Token token)
   {
      switch (token.getType())
      {
         case KEYWORD :
            return new PhpTokenKeyWordWidget(token);
            
         case FUNCTION:
         case METHOD:
            return new  PhpFunctionWidget(token);
            
         case PROPERTY:
            return new PhpPropertyWidget(token);
            
         case CONSTANT:
         case CLASS_CONSTANT:
            return new PhpConstantWidget(token);
            
         case PARAMETER:
         case VARIABLE:
         case LOCAL_VARIABLE:
            return new PhpVariableWidget(token);
            
         case CLASS:
         case INTERFACE:
            return new PhpClassWidget(token);

         default :
            return new PhpTokenKeyWordWidget(token);
      }

   }

}
