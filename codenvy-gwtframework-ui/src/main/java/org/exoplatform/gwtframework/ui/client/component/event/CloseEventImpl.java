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
package org.exoplatform.gwtframework.ui.client.component.event;

import com.google.gwt.event.logical.shared.CloseEvent;


/**
 * Implementation of {@link CloseEvent}.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jan 17, 2011 4:45:49 PM anya $
 *
 * @param <T>
 */
public class CloseEventImpl<T> extends CloseEvent<T>
{
   /**
    * @param target closed target
    */
   public CloseEventImpl(T target)
   {
      super(target, false);
   }
}
