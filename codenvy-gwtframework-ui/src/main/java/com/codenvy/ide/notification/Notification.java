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

import com.codenvy.ide.client.util.Elements;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.AnchorElement;
import elemental.html.DivElement;
import elemental.html.Element;
import elemental.html.ParagraphElement;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class Notification
{

   public interface ClickCallback
   {
      void onClick(Notification notification);
   }

   private ClickCallback callback;

   private final int duration;

   private final Element element;


   public Notification(final String message, final int duration)
   {
      this(message, null, duration);
   }

   public Notification(String message, ClickCallback callback, final int duration)
   {
      this.callback = callback;
      this.duration = duration;
      element = createAnchorElement(message);
   }

   /**
    * @return the duration
    */
   public int getDuration()
   {
      return duration;
   }

   public Element getElement()
   {
      return element;
   }

   private AnchorElement createAnchorElement(final String message)
   {
      AnchorElement anchorElement = Elements.createAnchorElement(/*css.anchor()*/);
      anchorElement.setHref("javascript:;");
      DivElement messageDiv = Elements.createDivElement(NotificationManager.resources.styles().message());
      ParagraphElement paragraphElement = Elements.createParagraphElement();
      paragraphElement.appendChild(Elements.createTextNode(message));
      messageDiv.appendChild(paragraphElement);
      anchorElement.appendChild(messageDiv);
      if (callback != null)
      {
         anchorElement.addEventListener(Event.CLICK, new EventListener()
         {
            @Override
            public void handleEvent(Event event)
            {
               callback.onClick(Notification.this);
            }
         }, false);
      }
      return anchorElement;
   }
}
