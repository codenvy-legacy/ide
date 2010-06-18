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
package org.exoplatform.ideall.client.autocompletion;

import java.util.HashMap;

import org.exoplatform.gwtframework.editor.api.Token.TokenType;
import org.exoplatform.ideall.client.IDEImageBundle;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class TokenImageResolver
{
   
   private static HashMap<TokenType, ImageResource> images = new HashMap<TokenType, ImageResource>();
   
   static
   {
      images.put(TokenType.FUNCTION, IDEImageBundle.INSTANCE.functionItem());
      images.put(TokenType.VARIABLE, IDEImageBundle.INSTANCE.varItem());
      images.put(TokenType.METHOD, IDEImageBundle.INSTANCE.methodItem());
      images.put(TokenType.PROPERTY, IDEImageBundle.INSTANCE.propertyItem());
      images.put(TokenType.TEMPLATE, IDEImageBundle.INSTANCE.templateItem());
      images.put(TokenType.KEYWORD, IDEImageBundle.INSTANCE.keywordItem());
   }
   
   public static Image getImage(TokenType type)
   {
      ImageResource resource = images.get(type);
      if(resource == null)
      {
         resource = IDEImageBundle.INSTANCE.copy();
      }
     
      Image image = new Image(resource);
      return image;
   }

   /**
    * @return the images
    */
   public static HashMap<TokenType, ImageResource> getImages()
   {
      return images;
   }
   
   
}
