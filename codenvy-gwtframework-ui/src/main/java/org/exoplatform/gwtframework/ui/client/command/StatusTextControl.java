/**
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.exoplatform.gwtframework.ui.client.command;

import org.exoplatform.gwtframework.ui.client.component.TextButton.TextAlignment;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class StatusTextControl extends Control<StatusTextControl>
{

   private int size = -1;

   private String text = "";

   private TextAlignment textAlignment = TextAlignment.LEFT;

   private GwtEvent<?> event;

   private boolean fireEventOnSingleClick = false;

   public StatusTextControl(String id)
   {
      super(id);
   }

   public int getSize()
   {
      return size;
   }

   public StatusTextControl setSize(int size)
   {
      this.size = size;
      return this;
   }

   public String getText()
   {
      return text;
   }

   public StatusTextControl setText(String text)
   {
      if (this.text.equals(text))
      {
         return this;
      }

      this.text = text;

      for (ControlStateListener listener : getStateListeners())
      {
         ((StatusTextControlStateListener)listener).updateStatusText(text);
      }

      return this;
   }

   /**
    * @return the event
    */
   public GwtEvent<?> getEvent()
   {
      return event;
   }

   /**
    * @param event the event to set
    */
   public StatusTextControl setEvent(GwtEvent<?> event)
   {
      this.event = event;
      return this;
   }

   public boolean isFireEventOnSingleClick()
   {
      return fireEventOnSingleClick;
   }

   public StatusTextControl setFireEventOnSingleClick(boolean fireEventOnSingleClick)
   {
      this.fireEventOnSingleClick = fireEventOnSingleClick;
      return this;
   }

   public TextAlignment getTextAlignment()
   {
      return textAlignment;
   }

   public StatusTextControl setTextAlignment(TextAlignment textAlignment)
   {
      this.textAlignment = textAlignment;
      return this;
   }

}
