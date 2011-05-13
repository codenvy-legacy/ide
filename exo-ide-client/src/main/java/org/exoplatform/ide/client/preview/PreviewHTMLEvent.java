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
package org.exoplatform.ide.client.preview;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS        .
 * @version $Id: $
 */

public class PreviewHTMLEvent extends GwtEvent<PreviewHTMLHandler>
{

   public static final GwtEvent.Type<PreviewHTMLHandler> TYPE = new GwtEvent.Type<PreviewHTMLHandler>();

   private boolean showPreview;

   public PreviewHTMLEvent(boolean showPreview)
   {
      this.showPreview = showPreview;
   }

   public boolean isShowPreview()
   {
      return showPreview;
   }

   @Override
   protected void dispatch(PreviewHTMLHandler handler)
   {
      handler.onPreviewHTMLFile(this);
   }

   @Override
   public GwtEvent.Type<PreviewHTMLHandler> getAssociatedType()
   {
      return TYPE;
   }

}
