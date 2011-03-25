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
package org.exoplatform.ide.client.test;

import java.util.LinkedHashMap;
import java.util.Map;

import org.exoplatform.gwtframework.ui.client.button.IconButton;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;
import org.exoplatform.ide.client.framework.ui.gwt.ViewEx;
import org.exoplatform.ide.client.test.ui.ViewManagerForm;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ViewManagerPresenter
{

   public interface Display
   {

   }

   private Map<String, ViewEx> views = new LinkedHashMap<String, ViewEx>();

   private Display display;

   public ViewManagerPresenter(HandlerManager eventBus)
   {
      String image = ImageHelper.getImageHTML("debug-icon.png");
      IconButton button = new IconButton(image, image);
      DOM.setStyleAttribute(button.getElement(), "zIndex", "100000");
      RootPanel.get().add(button, 0, 20 + 32);
   }

   protected void showOrHideManager()
   {
      if (display == null)
      {
         ViewManagerForm form = new ViewManagerForm();
         bindDisplay(form);
      }
      else
      {
         ((ViewManagerForm)display).removeFromParent();
         display = null;
      }
   }

   public void bindDisplay(Display d)
   {
      display = d;
   }

}
