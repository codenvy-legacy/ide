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

package org.exoplatform.gwtframework.ui.client.component;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TextArea;

/**
 * Text area HTML element.
 * Fixes firing value change event on paste and key up.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Oct 21, 2011 10:45:47 AM anya $
 *
 */
public class TextAreaInput extends TextArea
{

   /**
    * Default constructor.
    */
   public TextAreaInput()
   {
      addKeyUpHandler(new KeyUpHandler()
      {

         @Override
         public void onKeyUp(KeyUpEvent event)
         {
            valueChange();
         }
      });

      sinkEvents(Event.ONPASTE);
   }

   /**
    * @param name form element's name
    */
   public TextAreaInput(String name)
   {
      this();
      setName(name);
   }

   /**
    * @see com.google.gwt.user.client.ui.ValueBoxBase#onBrowserEvent(com.google.gwt.user.client.Event)
    */
   @Override
   public void onBrowserEvent(Event event)
   {
      if (!isEnabled())
         return;
      int type = DOM.eventGetType(event);
      switch (type)
      {
         case Event.ONPASTE :
            Scheduler.get().scheduleDeferred(new ScheduledCommand()
            {
               @Override
               public void execute()
               {
                  valueChange();
               }
            });
            break;
      }
      super.onBrowserEvent(event);
   }

   /**
    * Fires value changed event for the input.
    */
   public void valueChange()
   {
      ValueChangeEvent.fire(this, getText());
   }

   /**
    * Sets focus in text input.
    */
   public void focus()
   {
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            setFocus(true);
         }
      });
   }

}
