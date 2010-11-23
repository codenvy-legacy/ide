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

package org.exoplatform.ide.client.panel.event;

import com.google.gwt.user.client.ui.Image;

import org.exoplatform.ide.client.framework.ui.View;

import com.google.gwt.event.shared.GwtEvent;

/**
 * 
 * Generated to open view (as tab) in Code Helper Panel.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class OpenPanelEvent extends GwtEvent<OpenPanelHandler>
{

   public static final GwtEvent.Type<OpenPanelHandler> TYPE = new GwtEvent.Type<OpenPanelHandler>();

   private View view;
   
   private Image tabIcon;
   
   private String title;

   public OpenPanelEvent(View view, Image tabIcon, String title)
   {
      this.view = view;
      this.tabIcon = tabIcon;
      this.title = title;
   }

   /**
    * View to open.
    * 
    * @return the view
    */
   public View getView()
   {
      return view;
   }
   
   /**
    * Image for icon of tab.
    * 
    * @return the tabIcon
    */
   public Image getTabIcon()
   {
      return tabIcon;
   }
   
   /**
    * Get the title of tab.
    * 
    * @return the title
    */
   public String getTitle()
   {
      return title;
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<OpenPanelHandler> getAssociatedType()
   {
      return TYPE;
   }

   @Override
   protected void dispatch(OpenPanelHandler handler)
   {
      handler.onOpenPanel(this);
   }

}
