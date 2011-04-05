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
package org.exoplatform.ide.client.app.impl.layer;

import org.exoplatform.gwtframework.ui.client.toolbar.Toolbar;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.app.impl.Layer;

import com.google.gwt.user.client.DOM;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ToolbarsLayer extends Layer
{

   private Toolbar toolbar;

   private Toolbar statusbar;

   public ToolbarsLayer()
   {
      super("toolbars");
      toolbar = new Toolbar("exoIDEToolbar");
      add(toolbar, 0, 20);

      statusbar = new Toolbar("exoIDEStatusbar");
      statusbar.setHeight("30px");
      String background =
         UIHelper.getGadgetImagesURL() + "../eXoStyle/skin/default/images/component/toolbar/statusbar_Background.png";
      statusbar.setBackgroundImage(background);
      statusbar.setItemsTopPadding(3);
      add(statusbar, 0, 100);
   }

   @Override
   public void resize(int width, int height)
   {
      super.resize(width, height);

      toolbar.setWidth("" + width + "px");

      statusbar.setWidth("" + width + "px");
      DOM.setStyleAttribute(statusbar.getElement(), "top", "" + (height - 30) + "px");
   }
   
   public Toolbar getToolbar() {
      return toolbar;
   }
   
   public Toolbar getStatusbar() {
      return statusbar;
   }

}
