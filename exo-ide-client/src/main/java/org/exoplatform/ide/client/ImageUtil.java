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
package org.exoplatform.ide.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ImageUtil
{

   private static FlowPanel imagePanel;

   public static final String getHTML(Image image)
   {
      if (imagePanel == null)
      {
         imagePanel = new FlowPanel();
         DOM.setStyleAttribute(imagePanel.getElement(), "left", "-1000px");
         DOM.setStyleAttribute(imagePanel.getElement(), "top", "-1000px");
         DOM.setStyleAttribute(imagePanel.getElement(), "width", "16px");
         DOM.setStyleAttribute(imagePanel.getElement(), "height", "16px");
         DOM.setStyleAttribute(imagePanel.getElement(), "overflow", "hidden");
         RootPanel.get().add(imagePanel);
      }

      imagePanel.add(image);
      String imageHTML = DOM.getInnerHTML(imagePanel.getElement());
      imagePanel.clear();
      return imageHTML;
   }

}
