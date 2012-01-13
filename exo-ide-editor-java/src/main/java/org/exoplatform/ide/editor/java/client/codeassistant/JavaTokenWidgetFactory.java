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
package org.exoplatform.ide.editor.java.client.codeassistant;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.java.client.codeassistant.ui.JavaClassTokenWidget;
import org.exoplatform.ide.editor.java.client.codeassistant.ui.JavaConstructorWidget;
import org.exoplatform.ide.editor.java.client.codeassistant.ui.JavaFieldWidget;
import org.exoplatform.ide.editor.java.client.codeassistant.ui.JavaKeyWordWidget;
import org.exoplatform.ide.editor.java.client.codeassistant.ui.JavaMethodWidget;
import org.exoplatform.ide.editor.java.client.codeassistant.ui.JavaVariableWidget;

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

   private String projectId;

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
            return new JavaClassTokenWidget(token, restContext, projectId);

         case CONSTRUCTOR :
            return new JavaConstructorWidget(token, restContext, projectId);

         case METHOD :
            return new JavaMethodWidget(token, restContext, projectId);

         case FIELD :
         case PROPERTY :
            return new JavaFieldWidget(token, restContext, projectId);

         case VARIABLE :
         case PARAMETER :
            return new JavaVariableWidget(token);

         case KEYWORD :
            return new JavaKeyWordWidget(token);

         default :
            return new JavaClassTokenWidget(token, restContext, projectId);

      }
   }

   /**
    * @return the projectId
    */
   public String getProjectId()
   {
      return projectId;
   }

   /**
    * @param projectId the projectId to set
    */
   public void setProjectId(String projectId)
   {
      this.projectId = projectId;
   }

}
