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

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FocusWidget;

import org.exoplatform.gwtframework.commons.util.BrowserResolver;
import org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser;

/**
 * @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a>
 * @version $Revision$
 */
final class TermText extends FocusWidget implements KeyDownHandler, KeyPressHandler
{

   /** . */
   private final StringBuilder buffer;

   /** The state. */
   private final StringBuilder state;

   private String afterCursor = "";

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

      if (BrowserResolver.CURRENT_BROWSER == Browser.FIREFOX)
         addKeyPressHandler(this);
      else
         addKeyDownHandler(this);

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
      String s = buffer.toString() + afterCursor;
      state.append(afterCursor).append('\n');
      buffer.setLength(0);
      afterCursor = "";
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

   void printToBuffer(String text)
   {
      state.append(text);
   }

   void printPrompt()
   {
      String path = Environment.get().getCurrentFolder().getPath();

      if (!path.equals("/"))
      {
//         path = path.substring(0, path.lastIndexOf("/"));
         path = path.substring(path.lastIndexOf("/") + 1, path.length());
      }
      //      if (Environment.get().getValue(EnvironmentVariables.ENTRY_POINT).endsWith(path + "/"))
      //         path = "/";
      path = Environment.get().getValue(EnvironmentVariables.USER_NAME) + ":" + path;
      state.append(path + "$ ");
   }

   void repaint()
   {

      //
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

      markup.append("<span id=\"crash-cursor\" class=\"crash-cursor\">" + c + "</span>");
      markup.append(after);

      getElement().setInnerHTML(markup.toString());
      Document.get().setScrollTop(Document.get().getScrollHeight());
   }

   private void handleKeyEvent(int keyCode, DomEvent<?> event)
   {
      if (keyCode == KeyCodes.KEY_LEFT)
      {
         if (buffer.length() > 0)
         {
            buffer.deleteCharAt(buffer.length() - 1);
            afterCursor = state.charAt(state.length() - 1) + afterCursor;
            state.deleteCharAt(state.length() - 1);
            repaint();
         }
         event.stopPropagation();
         event.preventDefault();
      }
      else if (keyCode == KeyCodes.KEY_RIGHT)
      {
         if (!afterCursor.isEmpty())
         {
            String c = afterCursor.substring(0, 1);
            buffer.append(c);
            state.append(c);
            afterCursor = afterCursor.substring(1);
            repaint();
         }
         event.stopPropagation();
         event.preventDefault();
      }
      else if (keyCode == KeyCodes.KEY_DELETE)
      {
         if (!afterCursor.isEmpty())
         {
            afterCursor = afterCursor.substring(1);
            repaint();
         }
         event.stopPropagation();
         event.preventDefault();
      }
      else if (keyCode == KeyCodes.KEY_HOME)
      {
         afterCursor = buffer.toString() + afterCursor;
         state.delete(state.length() - buffer.length(), state.length());
         buffer.setLength(0);
         repaint();
         event.stopPropagation();
         event.preventDefault();
      }
      else if (keyCode == KeyCodes.KEY_END)
      {
         buffer.append(afterCursor);
         state.append(afterCursor);
         afterCursor = "";
         repaint();
         event.stopPropagation();
         event.preventDefault();
      }
   }

   /**
    * @see com.google.gwt.event.dom.client.KeyDownHandler#onKeyDown(com.google.gwt.event.dom.client.KeyDownEvent)
    */
   @Override
   public void onKeyDown(KeyDownEvent event)
   {
      int code = event.getNativeKeyCode();
      handleKeyEvent(code, event);
   }

   /**
    * @see com.google.gwt.event.dom.client.KeyPressHandler#onKeyPress(com.google.gwt.event.dom.client.KeyPressEvent)
    */
   @Override
   public void onKeyPress(KeyPressEvent event)
   {
      int code = event.getNativeEvent().getKeyCode();
      handleKeyEvent(code, event);
   }

}
