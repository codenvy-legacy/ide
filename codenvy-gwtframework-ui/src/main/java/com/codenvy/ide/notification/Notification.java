/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.notification;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class Notification extends Composite
{

   public enum NotificationType
   {
      INFO, MESSAGE, ERROR
   }


   private final int duration;

   private NotificationType type;

   public Notification(final String message, NotificationType type, final int duration)
   {
      this(new HTML(message, true), type, duration);

   }

   public Notification(final Widget widget, NotificationType type, final int duration)
   {
      this.duration = duration;
      super.initWidget(widget);
      this.type = type;
   }

   /**
    * @return the duration
    */
   public int getDuration()
   {
      return duration;
   }

   public NotificationType getType()
   {
      return type;
   }
}
