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

package org.exoplatform.ide.shell.client;

import com.google.gwt.dom.client.PreElement;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
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
   private static PreElement preElement = Document.get().createPreElement();

   static
   {
      preElement.setId("termContent");
   }

   /** Text entered by user. */
   private final StringBuilder buffer;

   /** The state of the term (prompt and entered text). */
   private final StringBuilder state;

   /**
    * Text righter after cursor.
    */
   private String afterCursor = "";

   /** The blinking. */
   private boolean on;

   /**
    * The focused state.
    */
   private boolean isFocused = false;

   /**
    * Cursor element.
    */
   private Element cursor;

   /**
    * Timer for blinking cursor.
    */
   private Timer timer = new Timer()
   {
      public void run()
      {
         on = !on;
         if (on && isFocused)
         {
            addStyleName(ShellClientBundle.INSTANCE.css().blink());
         }
         else
         {
            removeStyleName(ShellClientBundle.INSTANCE.css().blink());
         }
      }
   };

   TermText()
   {
      super(preElement);

      this.state = new StringBuilder();
      this.on = false;
      this.buffer = new StringBuilder();
      cursor = Document.get().createSpanElement();
      cursor.setId("crashCursor");
      cursor.setClassName(ShellClientBundle.INSTANCE.css().cursor());

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
   }

   /**
    * @see com.google.gwt.user.client.ui.FocusWidget#onAttach()
    */
   @Override
   protected void onAttach()
   {
      super.onAttach();

      timer.scheduleRepeating(500);
   }

   /**
    * Clear the term.
    */
   void clear()
   {
      state.setLength(0);
   }

   /**
    * @return {@link String} buffer's value
    */
   String getBuffer()
   {
      return buffer.length() > 0 ? buffer.toString() : "";
   }

   /**
    * Append string to buffer.
    * 
    * @param s string to append
    */
   void bufferAppend(CharSequence s)
   {
      buffer.append(s);
      state.append(s);
   }

   /**
    * Append char to buffer.
    * 
    * @param c char to append
    */
   void bufferAppend(char c)
   {
      buffer.append(c);
      state.append(c);
   }

   /**
    * Remove symbol from buffer.
    */
   void bufferDrop()
   {
      if (buffer.length() > 0)
      {
         buffer.setLength(buffer.length() - 1);

         if (state.length() > 0)
         {
            state.setLength(state.length() - 1);
         }
      }
   }

   /**
    * Clear buffer value.
    */
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

   /**
    * Submit buffer.
    * 
    * @return String buffer's value.
    */
   String bufferSubmit()
   {
      String s = buffer.toString() + afterCursor;
      state.append(afterCursor).append('\n');
      buffer.setLength(0);
      afterCursor = "";
      return s;
   }

   /**
    * Print text to term.
    * 
    * @param text
    */
   void printToTerm(String text)
   {
      state.append(text);
   }

   /**
    * Redraw term.
    */
   void repaint()
   {
      StringBuilder markup = new StringBuilder();
      int from = 0;
      while (true)
      {
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
      String c = "&nbsp;";
      String after = afterCursor;
      if (!afterCursor.isEmpty())
      {
         c = afterCursor.substring(0, 1);
         after = afterCursor.substring(1);
      }

      cursor.setInnerHTML(c);
      markup.append(cursor.getString());
      markup.append(after);

      getElement().setInnerHTML(markup.toString());
      Document.get().setScrollTop(Document.get().getScrollHeight());
   }

   /**
    * Move cursor to the left.
    */
   public void moveLeft()
   {
      if (buffer.length() > 0)
      {
         buffer.deleteCharAt(buffer.length() - 1);
         afterCursor = state.charAt(state.length() - 1) + afterCursor;
         state.deleteCharAt(state.length() - 1);
         repaint();
      }
   }

   /**
    * Move cursor to the right.
    */
   public void moveRight()
   {
      if (!afterCursor.isEmpty())
      {
         String c = afterCursor.substring(0, 1);
         buffer.append(c);
         state.append(c);
         afterCursor = afterCursor.substring(1);
         repaint();
      }
   }

   /**
    * Delete symbol.
    */
   public void deleteSymbol()
   {
      if (!afterCursor.isEmpty())
      {
         afterCursor = afterCursor.substring(1);
         repaint();
      }
   }

   /**
    * Move cursor to home of the line.
    */
   public void moveHome()
   {
      afterCursor = buffer.toString() + afterCursor;
      state.delete(state.length() - buffer.length(), state.length());
      buffer.setLength(0);
      repaint();
   }

   /**
    * Move cursor to the end of the line.
    */
   public void moveEnd()
   {
      buffer.append(afterCursor);
      state.append(afterCursor);
      afterCursor = "";
      repaint();
   }

   /**
    * Returns the term's state.
    * 
    * @return {@link String} term's state
    */
   public String getState()
   {
      return state.toString();
   }

   /**
    * Returns cursor's element.
    * 
    * @return {@link Element} cursors element
    */
   public Element getCursor()
   {
      return cursor;
   }
}
