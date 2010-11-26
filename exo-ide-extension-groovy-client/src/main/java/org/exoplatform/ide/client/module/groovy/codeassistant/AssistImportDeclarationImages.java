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
package org.exoplatform.ide.client.module.groovy.codeassistant;

import java.util.HashMap;

import org.exoplatform.ide.client.framework.codeassistant.TokenExtType;
import org.exoplatform.ide.client.module.groovy.GroovyPluginImageBundle;

import com.google.gwt.resources.client.ImageResource;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 22, 2010 3:34:11 PM evgen $
 *
 */
public class AssistImportDeclarationImages
{
   private static HashMap<TokenExtType, ImageResource> images = new HashMap<TokenExtType, ImageResource>();

   static
   {
      images.put(TokenExtType.CLASS,  GroovyPluginImageBundle.INSTANCE.classItem());
      images.put(TokenExtType.ANNOTATION, GroovyPluginImageBundle.INSTANCE.annotationItem());
      images.put(TokenExtType.ENUM, GroovyPluginImageBundle.INSTANCE.enumItem());
      images.put(TokenExtType.INTERFACE, GroovyPluginImageBundle.INSTANCE.intrfaceItem());
   }
   
   /**
    * @return the images
    */
   public static HashMap<TokenExtType, ImageResource> getImages()
   {
      return images;
   }
   
}
