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
package org.exoplatform.ide.editor.codeassistant.java;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.codeassistant.java.ui.JavaClassTokenWidget;
import org.exoplatform.ide.editor.codeassistant.java.ui.JavaConstructorWidget;
import org.exoplatform.ide.editor.codeassistant.java.ui.JavaFieldWidget;
import org.exoplatform.ide.editor.codeassistant.java.ui.JavaKeyWordWidget;
import org.exoplatform.ide.editor.codeassistant.java.ui.JavaMethodWidget;
import org.exoplatform.ide.editor.codeassistant.java.ui.JavaVariableWidget;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 25, 2010 4:50:06 PM evgen $
 *
 */
public class JavaTokenWidgetFactory implements TokenWidgetFactory
{

   private String restContext;
   

   /**
    * @param context
    */
   public JavaTokenWidgetFactory(String context)
   {
      restContext = context;
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.api.TokenWidgetFactory#getTokenWidget(java.lang.Object, int)
    */
   @Override
   public TokenWidget buildTokenWidget(Token token)
   {
      switch (token.getType())
      {
         case CLASS :
         case INTERFACE :
         case ANNOTATION :
            return new JavaClassTokenWidget(token, restContext);

         case CONSTRUCTOR :
            return new JavaConstructorWidget(token, restContext);

         case METHOD :
            return new JavaMethodWidget(token, restContext);

         case FIELD :
         case PROPERTY :
            return new JavaFieldWidget(token, restContext);
            
         case VARIABLE :
         case PARAMETER :
            return new JavaVariableWidget(token, restContext);

         case KEYWORD :
            return new JavaKeyWordWidget(token);

         default :
            return new JavaClassTokenWidget(token, restContext);

      }
   }

}
