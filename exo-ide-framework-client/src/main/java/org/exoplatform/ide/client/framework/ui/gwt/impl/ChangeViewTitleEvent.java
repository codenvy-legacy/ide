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
package org.exoplatform.ide.client.framework.ui.gwt.impl;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ChangeViewTitleEvent extends GwtEvent<ChangeViewTitleHandler>
{

   public static final GwtEvent.Type<ChangeViewTitleHandler> TYPE = new GwtEvent.Type<ChangeViewTitleHandler>();

   private String viewId;

   private String title;

   public ChangeViewTitleEvent(String viewId, String title)
   {
      this.viewId = viewId;
      this.title = title;
   }

   public String getViewId()
   {
      return viewId;
   }

   public String getTitle()
   {
      return title;
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ChangeViewTitleHandler> getAssociatedType()
   {
      return TYPE;
   }

   @Override
   protected void dispatch(ChangeViewTitleHandler handler)
   {
      handler.onChangeViewTitle(this);
   }

}
