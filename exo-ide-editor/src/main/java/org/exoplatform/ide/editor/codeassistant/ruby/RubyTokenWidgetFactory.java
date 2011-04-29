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
package org.exoplatform.ide.editor.codeassistant.ruby;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.codeassistant.ruby.ui.RubyClassWidget;
import org.exoplatform.ide.editor.codeassistant.ruby.ui.RubyKeyWordWidget;
import org.exoplatform.ide.editor.codeassistant.ruby.ui.RubyMethodWidget;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: RubyTokenWidgetFactory Apr 28, 2011 5:37:38 PM evgen $
 *
 */
public class RubyTokenWidgetFactory implements TokenWidgetFactory
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
            return new RubyKeyWordWidget(token);

         case METHOD :
            return new RubyMethodWidget(token);

         case CLASS :
            return new RubyClassWidget(token);

         default :
            return new RubyKeyWordWidget(token);
      }
   }

}
