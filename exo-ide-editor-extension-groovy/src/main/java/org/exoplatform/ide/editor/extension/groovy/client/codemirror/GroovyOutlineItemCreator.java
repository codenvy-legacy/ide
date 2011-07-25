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
package org.exoplatform.ide.editor.extension.groovy.client.codemirror;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.extension.groovy.client.Images;
import org.exoplatform.ide.editor.extension.java.client.JavaClientBundle;
import org.exoplatform.ide.editor.extension.java.client.codemirror.JavaOutlineItemCreator;


/**
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class GroovyOutlineItemCreator extends JavaOutlineItemCreator
{  

   @Override
   public String getTokenIcon(TokenBeenImpl token)
   {
      switch (token.getType())
      {
         case VARIABLE :
            return JavaClientBundle.INSTANCE.variable().getURL();
            
         case PROPERTY :            
            if (isPrivate(token))
            {
               return JavaClientBundle.INSTANCE.privateField().getURL();
            }
            
            else if (isProtected(token))
            {
               return JavaClientBundle.INSTANCE.protectedField().getURL();
            }
            
            else if (isPublic(token))
            {
               return JavaClientBundle.INSTANCE.publicField().getURL();
            }

            return JavaClientBundle.INSTANCE.publicField().getURL();
            
         case METHOD :
            if (isPrivate(token))
            {
               return JavaClientBundle.INSTANCE.privateMethod().getURL();
            }
            
            else if (isProtected(token))
            {
               return JavaClientBundle.INSTANCE.protectedMethod().getURL();
            }

            else if (isPublic(token))
            {
               return JavaClientBundle.INSTANCE.publicMethod().getURL();
            }

            return JavaClientBundle.INSTANCE.publicMethod().getURL();
            
            
         case CLASS :
            return JavaClientBundle.INSTANCE.classItem().getURL();

         case INTERFACE :
            return JavaClientBundle.INSTANCE.interfaceItem().getURL();              
            
         case GROOVY_TAG :
            return Images.GROOVY_TAG;
            
         default :
            return "";
      }
   }
   
}