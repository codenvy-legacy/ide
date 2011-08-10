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
import com.google.gwt.user.client.Timer;

import org.exoplatform.cloudshell.client.crash.CRaSHClientService;
import org.exoplatform.cloudshell.client.crash.CRaSHCompleteListAsyncRequestCallback;
import org.exoplatform.cloudshell.shared.CLIResource;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.util.BrowserResolver;
import org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

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

      void preparePaste();

      void finishPaste();
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
//               performComplete();
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
            else if (code == KeyCodes.KEY_LEFT)
            {
               handled = true;
            }
            else if (code == KeyCodes.KEY_RIGHT)
            {
               handled = true;
            }
            else
            {
               char c = event.getCharCode();
               // if 
               if ((int)c != 0)
                  display.appendBuffer(c);
               handled = true;
            }

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

            // key code 86 is 'v'
            if (event.getNativeEvent().getKeyCode() == 86 && event.isControlKeyDown())
            {
               display.preparePaste();
               new Timer()
               {

                  @Override
                  public void run()
                  {
                     display.finishPaste();
                  }
               }.schedule(10);
            }
            //key code '88' is x   
            else if (event.getNativeEvent().getKeyCode() == 88 && event.isControlKeyDown() && event.isShiftKeyDown())
            {
               clearConsole();
            }
            else if (BrowserResolver.CURRENT_BROWSER != Browser.FIREFOX)
            {
               boolean handled = false;
               if (code == KeyCodes.KEY_BACKSPACE)
               {
                  display.removeFromBuffer();
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
               if (handled)
               {
                  display.refreshConsole();
                  event.preventDefault();
                  event.stopPropagation();
               }
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
      if (command.equals("help"))
      {
         Map<String, String> commands = new TreeMap<String, String>();
         int max = 0;
         String tab = "  ";
         for (CLIResource res : CloudShell.getCommands())
         {
            for (String s : res.getCommand())
            {
               commands.put(s, res.getDescription() == null ? "" : res.getDescription());
               if (s.length() > max)
                  max = s.length();
            }
         }
         StringBuilder help = new StringBuilder();
         for (String name : commands.keySet())
         {
            char chars[] = new char[tab.length() + max - name.length()];
            Arrays.fill(chars, (char)' ');
            String s = new String(chars);
            help.append(tab);
            help.append(name);
            help.append(s);
            help.append(commands.get(name));
            help.append("\n");
         }
         display.print(help.toString());
         return;
      }
      ShellService.getService().processCommand(command, new AsyncRequestCallback<String>()
      {

         @Override
         protected void onSuccess(String result)
         {
            display.print(result);
         }

         /**
          * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
          */
         @Override
         protected void onFailure(Throwable exception)
         {
            //TODO
            display.print((exception.getMessage() != null) ? exception.getMessage() + "\n"
               : "Unknown error in processing the command.\n");
         }
      });
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
      {
         command = "";
      }
      display.clearBuffer();
      display.appendBuffer(command);
      display.refreshConsole();
   }
}
