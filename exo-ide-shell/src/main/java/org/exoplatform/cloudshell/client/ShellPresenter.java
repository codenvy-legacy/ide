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
package org.exoplatform.cloudshell.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyDownHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;

import org.exoplatform.cloudshell.client.crash.CRaSHClientService;
import org.exoplatform.cloudshell.client.crash.CRaSHCompleteListAsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;

import java.util.HashMap;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Aug 2, 2011 11:03:32 AM anya $
 *
 */
public class ShellPresenter implements ConsoleWriter
{
   interface Display extends ConsoleWriter
   {
      HasClickHandlers getClearButton();

      HasKeyPressHandlers getKeyPressHandler();

      HasKeyDownHandlers getKeyDownHandler();

      void appendBuffer(char c);

      void appendBuffer(CharSequence c);

      void removeFromBuffer();

      void clearBuffer();

      String submitBuffer();

      void refreshConsole();

      String getBuffer();

      void focusInConsole();

      void printPrompt();
   }

   private Display display;

   private ShellComandBuffer buffer;

   public ShellPresenter()
   {
      display = GWT.create(Display.class);
      bindDisplay();
      buffer = new ShellComandBuffer();
   }

   public void bindDisplay()
   {
      display.getClearButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            clearConsole();
            display.focusInConsole();
         }
      });

      display.getKeyPressHandler().addKeyPressHandler(new KeyPressHandler()
      {

         @Override
         public void onKeyPress(KeyPressEvent event)
         {
            int code = event.getNativeEvent().getKeyCode();
            boolean handled = false;
            if (code == KeyCodes.KEY_BACKSPACE)
            {
               display.removeFromBuffer();
               handled = true;
            }
            else if (code == KeyCodes.KEY_ENTER)
            {
               String s = display.submitBuffer();
               if (!s.isEmpty())
               {
                  processCommand(s);
               }
               else
                  display.printPrompt();

               handled = true;
            }
            else if (code == KeyCodes.KEY_TAB)
            {
               performComplete();
               handled = true;
            }
            else if ((event.getCharCode() == 'X') && event.isShiftKeyDown() && event.isControlKeyDown())
            {
               clearConsole();
               handled = true;
            }
            else if (code == KeyCodes.KEY_UP)
            {
               goUp();
               handled = true;
            }
            else if (code == KeyCodes.KEY_DOWN)
            {
               goDown();
               handled = true;
            }
            else
            {
               char c = event.getCharCode();
//               if (Character.isLetterOrDigit(c) || c == ' ' || c == '-' || c == '=' || c == '"' || c == ':' || c == '|'
//                  || c == '.' || c == '*')
//               {
                  display.appendBuffer(c);
                  handled = true;
//               }
            }
            //
            if (handled)
            {
               display.refreshConsole();
               event.preventDefault();
               event.stopPropagation();
            }
         }
      });

      display.getKeyDownHandler().addKeyDownHandler(new KeyDownHandler()
      {
         @Override
         public void onKeyDown(KeyDownEvent event)
         {
            int code = event.getNativeKeyCode();
            if (code == KeyCodes.KEY_LEFT)
            {
               event.preventDefault();
               event.stopPropagation();
            }
            else if (code == KeyCodes.KEY_RIGHT)
            {
               event.preventDefault();
               event.stopPropagation();
            }
         }
      });

      display.print("Welcome to eXo IDE Shell\n");
   }

   /**
    * @see org.exoplatform.cloudshell.client.ConsoleWriter#print(java.lang.String)
    */
   @Override
   public void print(String str)
   {
      display.print(str);
   }

   /**
    * @see org.exoplatform.cloudshell.client.ConsoleWriter#clearConsole()
    */
   @Override
   public void clearConsole()
   {
      display.clearConsole();
   }

   /**
    * Process user command.
    * 
    * @param command
    */
   public void processCommand(String command)
   {
      buffer.add(command);
      ShellService.getService().login(command, new AsyncRequestCallback<String>()
      {

         @Override
         protected void onSuccess(String result)
         {
            CloudShell.console().print(result);
         }
      });
      //      CRaSHClientService.getService().processCommand(command, new CRaSHOutputAsyncRequestCallback());
   }

   /**
    * Perform the autocomplete of the command.
    */
   public void performComplete()
   {
      final String prefix = display.getBuffer();
      CRaSHClientService.getService().complete(prefix, new CRaSHCompleteListAsyncRequestCallback()
      {

         @Override
         protected void onSuccess(HashMap<String, String> result)
         {
            if (result.size() <= 0)
            {
               return;
            }
            else if (result.size() == 1)
            {
               String key = result.keySet().iterator().next();
               display.appendBuffer(key + result.get(key));
               display.refreshConsole();
            }
            else
            {
               display.appendBuffer("\n");
               for (String key : result.keySet())
               {
                  display.appendBuffer(prefix + key + " | ");
               }
               display.submitBuffer();
               display.refreshConsole();
            }
         }
      });
   }

   /**
    * Navigate the shell command buffer in up direction.
    */
   public void goUp()
   {
      String command = buffer.goUp();
      if (command == null)
         return;

      display.clearBuffer();
      display.appendBuffer(command);
      display.refreshConsole();
   }

   /**
    * Navigate the shell command buffer in down direction.
    */
   public void goDown()
   {
      String command = buffer.goDown();

      if (command == null)
         return;

      display.clearBuffer();
      display.appendBuffer(command);
      display.refreshConsole();
   }
}
