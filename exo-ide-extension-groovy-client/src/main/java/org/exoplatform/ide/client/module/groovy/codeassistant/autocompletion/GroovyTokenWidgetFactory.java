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
package org.exoplatform.ide.client.module.groovy.codeassistant.autocompletion;

import org.exoplatform.ide.client.framework.codeassistant.TokenExt;
import org.exoplatform.ide.client.framework.codeassistant.TokenWidget;
import org.exoplatform.ide.client.framework.codeassistant.api.TokenWidgetFactory;
import org.exoplatform.ide.client.module.groovy.codeassistant.ui.GroovyClassTokenWidget;
import org.exoplatform.ide.client.module.groovy.codeassistant.ui.GroovyConstructorWidget;
import org.exoplatform.ide.client.module.groovy.codeassistant.ui.GroovyFieldWidget;
import org.exoplatform.ide.client.module.groovy.codeassistant.ui.GroovyMethodWidget;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 25, 2010 4:50:06 PM evgen $
 *
 */
public class GroovyTokenWidgetFactory implements TokenWidgetFactory<TokenExt>
{
   
   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.api.TokenWidgetFactory#getTokenWidget(java.lang.Object, int)
    */
   @Override
   public TokenWidget<TokenExt> buildTokenWidget(TokenExt token)
   {
      switch (token.getType())
      {
         case CLASS :
            return new GroovyClassTokenWidget(token);
            
         case CONSTRUCTOR :
            return new GroovyConstructorWidget(token);
            
         case METHOD :
            return new GroovyMethodWidget(token);
            
         case FIELD :
            return new GroovyFieldWidget(token);
                     
         default :
            return new GroovyConstructorWidget(token);
            
      }
   }

}
