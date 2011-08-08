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

package org.exoplatform.cloudshell.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FocusWidget;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
final class TermText extends FocusWidget
{

   /** . */
   private final StringBuilder buffer;

   /** The state. */
   private final StringBuilder state;

   /** The blinking. */
   private boolean on;

   private boolean isFocused = false;

   /** . */
   private int height;

   TermText(int height)
   {
      super(Document.get().createElement("pre"));

      //
      if (height <= 0)
      {
         throw new IllegalArgumentException("Cannot give a non positive height");
      }

      //
      addMouseDownHandler(new MouseDownHandler()
      {
         public void onMouseDown(MouseDownEvent event)
         {
            setFocus(true);
         }
      });

      addBlurHandler(new BlurHandler()
      {

         @Override
         public void onBlur(BlurEvent event)
         {
            isFocused = false;
         }
      });

      addFocusHandler(new FocusHandler()
      {

         @Override
         public void onFocus(FocusEvent event)
         {
            isFocused = true;
         }
      });

      //
      setStyleName("crash-term");

      //
      this.state = new StringBuilder();
      this.on = false;
      this.buffer = new StringBuilder();
      this.height = height;
   }

   @Override
   protected void onAttach()
   {
      super.onAttach();

      // Blinking cursor
      Timer t = new Timer()
      {
         public void run()
         {
            on = !on;
            if (on && isFocused)
            {
               addStyleName("crash-blink");
            }
            else
            {
               removeStyleName("crash-blink");
            }
         }
      };

      //
      t.scheduleRepeating(500);
   }

   void clear()
   {
      int index = state.lastIndexOf("\n");
      if (index >= 0)
      {
         state.delete(0, index + 1);
      }
   }

   String getBuffer()
   {
      return buffer.length() > 0 ? buffer.toString() : "";
   }

   void bufferAppend(CharSequence s)
   {
      buffer.append(s);
      state.append(s);
   }

   void bufferAppend(char c)
   {
      buffer.append(c);
      state.append(c);
   }

   void bufferDrop()
   {
      if (buffer.length() > 0)
      {

         //
         buffer.setLength(buffer.length() - 1);

         // Buffer could be zero because of reset button
         // anyway better safe than sorry
         if (state.length() > 0)
         {
            state.setLength(state.length() - 1);
         }
      }
   }

   void bufferClear()
   {
      if (buffer.length() > 0)
      {
         // Buffer could be zero because of reset button
         // anyway better safe than sorry
         if (state.length() > 0)
         {
            state.setLength(state.length() - buffer.length());
         }
         buffer.setLength(0);
      }
   }

   String bufferSubmit()
   {
      String s = buffer.toString();
      buffer.setLength(0);
      state.append('\n');
      return s;
   }

   void print(char c)
   {
      state.append(c);
      printPrompt();
   }

   void print(CharSequence text)
   {
      state.append(text);
      printPrompt();
   }
   
   void printPrompt()
   {
      state.append("$ ");
   }

   
   void repaint()
   {

      //
      StringBuilder markup = new StringBuilder();

      //
      int lines = 0;
      int from = 0;
      while (true)
      {
         lines++;
         int to = state.indexOf("\n", from);
         markup.append(state, from, to == -1 ? state.length() : to);
         if (to == -1)
         {
            break;
         }
         else
         {
            markup.append("\n");
            from = to + 1;
         }
      }

      // The cursor
      markup.append("<span id=\"crash-cursor\" class=\"crash-cursor\">&nbsp;</span>");

      // Add missing lines
      while (lines++ < height)
      {
         markup.append("&nbsp;\n");
      }

      // Update markup state
      getElement().setInnerHTML(markup.toString());
      getElement().setScrollTop(getElement().getScrollHeight());
   }
}
