/**
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
 *
 */

package org.exoplatform.gwtframework.ui.client.util;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ImageHelper
{

   /**
    * 
    */
   private static AbsolutePanel imagePanel;

   /**
    * @param image
    * @return
    */
   public static final String getImageHTML(Image image)
   {
      if (imagePanel == null)
      {
         imagePanel = new AbsolutePanel();
         imagePanel.getElement().getStyle().setWidth(16, Unit.PX);
         imagePanel.getElement().getStyle().setHeight(16, Unit.PX);
         imagePanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
         RootPanel.get().add(imagePanel, -10000, -10000);
      }

      imagePanel.add(image);
      String imageHTML = DOM.getInnerHTML(imagePanel.getElement());
      imagePanel.clear();
      return imageHTML;
   }

   /**
    * @param imageResource
    * @return
    */
   public static final String getImageHTML(ImageResource imageResource)
   {
      Image image = new Image(imageResource);
      return getImageHTML(image);
   }

   /**
    * @param imageURL
    * @return
    */
   public static String getImageHTML(String imageURL)
   {
      if (imageURL == null) {
         imageURL = "";
      }
      return "<img src=\"" + imageURL + "\" />";
   }

}
