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
package org.exoplatform.gwtframework.ui.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ImageFactory
{

   private static Map<String, ImageProducer> images = new HashMap<String, ImageProducer>();

   private static ImageProducer defaultImageProducer = new ImageProducer("gwtframework-images/default-image.png");

   //   private static ImageProducer defaultImageProducer = new ImageProducer(ShowCaseImageBundle.INSTANCE.add(),
   //      ShowCaseImageBundle.INSTANCE.addDisabled());

   public static List<String> getImageNames()
   {
      return new ArrayList<String>(images.keySet());
   }

   public static Image getImage(String imageName)
   {
      ImageProducer producer = images.get(imageName);
      if (producer != null)
      {
         return producer.getImage();
      }

      return defaultImageProducer.getImage();
   }

   public static Image getDisabledImage(String imageName)
   {
      ImageProducer producer = images.get(imageName);
      if (producer != null)
      {
         return producer.getDisabledImage();
      }

      return defaultImageProducer.getImage();
   }

   public static void addImage(String imageName, ImageResource imageResource)
   {
      images.put(imageName, new ImageProducer(imageResource));
   }

   public static void addImage(String imageName, ImageResource imageResource, ImageResource disabledImageResource)
   {
      images.put(imageName, new ImageProducer(imageResource, disabledImageResource));
   }

}
