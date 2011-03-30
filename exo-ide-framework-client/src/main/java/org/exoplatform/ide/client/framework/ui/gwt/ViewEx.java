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

import com.google.gwt.user.client.ui.Image;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public interface ViewEx
{
   
   /**
    * Get ID of this view.
    * 
    * @return ID of this view
    */
   String getId();
   
   /**
    * Get type of this view.
    * 
    * @return type of this view
    */
   String getType();

   /**
    * Get title of this view.
    * 
    * @return title of this view
    */
   String getTitle();
   
   /**
    * Sets the new title of this view.
    * 
    * @param title new title of this view 
    */
   void setTitle(String title);
   
   /**
    * Get icon of this view.
    * This icon shows in the window title or in the tab title.  
    * 
    * @return icon of this view.
    */
   Image getIcon();
   
   /**
    * Sets the new icon of this view.
    * 
    * @param icon icon of this view
    */
   void setIcon(Image icon);
   
   /**
    * Determines whether or not this button can close button. 
    * 
    * @return <b>true</b> if this view has close button ( can be closed ), <b>false</b> otherwise
    */
   boolean hasCloseButton();

   /**
    * Get is this view is visible.
    * This method actual only when this view is attached in the Panel.
    * 
    * @return <b>true</b> when this view is visible, <b>false</b> otherwise
    */
   boolean isViewVisible();
   
   /**
    * This method actual only when this view is attached in the Panel.
    */
   void setViewVisible();
   
   /**
    * Get default width of this view.
    * 
    * @return default width of this view
    */
   int getDefaultWidth();
   
   /**
    * Get default height of this view.
    * 
    * @return default height of this view
    */
   int getDefaultHeight();
   
   /**
    * Determines whether or not this button is visible. 
    * 
    * @return <b>true</b> if this view can resize
    */
   boolean canResize();
   
   /**
    * Sets this view as active.
    */
   void activate();
   
}
