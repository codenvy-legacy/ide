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
package org.exoplatform.ide.client.framework.ui.gwt;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public abstract class AbstractView extends FlowPanel implements ViewEx, ViewDisplay
{

   private String id;

   private String type;

   private String tiltle;

   private Image icon;
   
   private boolean hasCloseButton = true;
   
   protected int defaultWidth = 300;
   
   protected int defaultHeight = 200;
   
   private boolean canResize = true;

   public AbstractView(String id, String type, String title)
   {
      this(id, type, title, null);
   }

   public AbstractView(String id, String type, String title, Image icon)
   {
      this(id, type, title, icon, 300, 200);
   }

   public AbstractView(String id, String type, String title, Image icon, int defaultWidth, int defaultHeight)
   {
      this.id = id;
      this.type = type;
      this.tiltle = title;
      this.icon = icon;
      this.defaultWidth = defaultWidth;
      this.defaultHeight = defaultHeight;
   }

   @Override
   public ViewEx getView()
   {
      return this;
   }

   @Override
   public String getId()
   {
      return id;
   }

   @Override
   public String getType()
   {
      return type;
   }

   @Override
   public String getTitle()
   {
      return tiltle;
   }

   @Override
   public void setTitle(String title)
   {
      this.tiltle = title;
   }

   @Override
   public Image getIcon()
   {
      return icon;
   }

   @Override
   public void setIcon(Image icon)
   {
      this.icon = icon;
   }

   @Override
   public boolean hasCloseButton()
   {
      return hasCloseButton;
   }

   @Override
   public boolean canResize()
   {
      return canResize;
   }

   @Override
   public boolean isActive()
   {
      return false;
   }

   @Override
   public void setActive()
   {
   }

   @Override
   public int getDefaultWidth()
   {
      return defaultWidth;
   }

   @Override
   public int getDefaultHeight()
   {
      return defaultHeight;
   }

}
