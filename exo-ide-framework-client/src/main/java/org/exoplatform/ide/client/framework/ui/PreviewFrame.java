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
package org.exoplatform.ide.client.framework.ui;

import com.google.gwt.event.dom.client.HasLoadHandlers;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Frame;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: PreviewFrame Feb 17, 2011 3:07:09 PM evgen $
 * 
 */
public class PreviewFrame extends Frame implements HasLoadHandlers
{
   /**
    * 
    */
   public PreviewFrame()
   {
      super();
   }

   public PreviewFrame(String url)
   {
      super(url);
   }

   /**
    * @see com.google.gwt.event.dom.client.HasLoadHandlers#addLoadHandler(com.google.gwt.event.dom.client.LoadHandler)
    */
   @Override
   public HandlerRegistration addLoadHandler(LoadHandler handler)
   {
      return addDomHandler(handler, LoadEvent.getType());
   }

}
