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

import com.google.gwt.resources.client.ImageResource;


/**
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class GroovyOutlineItemCreator extends JavaOutlineItemCreator
{  

   @Override
   public ImageResource getTokenIcon(TokenBeenImpl token)
   {
      switch (token.getType())
      {
         case VARIABLE :
            return JavaClientBundle.INSTANCE.variable();
            
         case PROPERTY :            
            if (isPrivate(token))
            {
               return JavaClientBundle.INSTANCE.privateField();
            }
            
            else if (isProtected(token))
            {
               return JavaClientBundle.INSTANCE.protectedField();
            }
            
            else if (isPublic(token))
            {
               return JavaClientBundle.INSTANCE.publicField();
            }

            return JavaClientBundle.INSTANCE.publicField();
            
         case METHOD :
            if (isPrivate(token))
            {
               return JavaClientBundle.INSTANCE.privateMethod();
            }
            
            else if (isProtected(token))
            {
               return JavaClientBundle.INSTANCE.protectedMethod();
            }

            else if (isPublic(token))
            {
               return JavaClientBundle.INSTANCE.publicMethod();
            }

            return JavaClientBundle.INSTANCE.publicMethod();
            
            
         case CLASS :
            return JavaClientBundle.INSTANCE.classItem();

         case INTERFACE :
            return JavaClientBundle.INSTANCE.interfaceItem();              
            
         case GROOVY_TAG :
            return JavaClientBundle.INSTANCE.groovyTagItem();
            
         default :
            return null;
      }
   }
   
}