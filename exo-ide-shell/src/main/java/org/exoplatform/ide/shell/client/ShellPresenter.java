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
package org.exoplatform.ide.shell.client;

import com.google.gwt.http.client.RequestException;

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

import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.util.BrowserResolver;
import org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser;
import org.exoplatform.ide.shell.client.commands.Utils;
import org.exoplatform.ide.shell.client.marshal.GenericJsonUnmarshaller;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Aug 2, 2011 11:03:32 AM anya $
 *
 */
public class ShellPresenter
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

   public ShellPresenter(Display display)
   {
      this.display = display;
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
            display.clearConsole();
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

               if ((int)c != 0 && !event.isControlKeyDown())
               {
                  display.appendBuffer(c);
                  handled = true;
               }
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
               display.clearConsole();
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
               else if (code == KeyCodes.KEY_TAB)
               {
                  performComplete();
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

   }

   /**
    * Process user command.
    * 
    * @param command
    */
   public void processCommand(String command)
   {
      buffer.add(command);
      ShellService.getService().processCommand(command,
         new AsyncRequestCallback<StringBuilder>(new GenericJsonUnmarshaller(new StringBuilder()))
         {

            @Override
            protected void onSuccess(StringBuilder result)
            {
               String res = result.toString();
               res = (res.endsWith("\n") || res.isEmpty()) ? res : res + "\n";
               display.print(res);
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
      String prefix = display.getBuffer();
      if (prefix.isEmpty())
         return;

      List<String> commands = CLIResourceUtil.getAllCommandNames(CloudShell.getCommands());
      List<String> suggestions = new ArrayList<String>();
      for (String name : commands)
      {
         if (name.startsWith(prefix))
         {
            suggestions.add(name);
         }
      }

      if (suggestions.isEmpty())
      {
         performFolderNameComplete();
         return;
      }

      if (suggestions.size() == 1)
      {
         display.clearBuffer();
         display.appendBuffer(suggestions.get(0) + " ");
         display.refreshConsole();
      }
      else
      {
         display.appendBuffer("\n");
         for (String key : suggestions)
         {
            display.appendBuffer(key + " ");
         }
         display.submitBuffer();
         display.printPrompt();
         display.appendBuffer(prefix);
         display.refreshConsole();
      }

   }

   /**
    * 
    */
   private void performFolderNameComplete()
   {
      final String prefix =
         display.getBuffer().substring(display.getBuffer().lastIndexOf(" ") + 1, display.getBuffer().length());
      final String namePrefix;
      if(prefix.contains("/"))
      {
         namePrefix = prefix.substring(prefix.lastIndexOf("/") + 1, prefix.length());
      }
      else
         namePrefix = prefix;
      if (prefix.isEmpty())
      {
         try
         {
            VirtualFileSystem.getInstance().getChildren(Environment.get().getCurrentFolder(),
               new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>()))
               {

                  @Override
                  protected void onSuccess(List<Item> result)
                  {
                     List<Item> folders = new ArrayList<Item>();
                     for (Item i : result)
                     {
                        if (i instanceof FolderModel)
                        {
                           folders.add(i);
                        }
                     }

                     display.appendBuffer("\n");
                     display.appendBuffer(Utils.fomatItems(folders));
                     display.submitBuffer();
                     display.printPrompt();
                     display.appendBuffer(prefix);
                     display.refreshConsole();
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     CloudShell.console().println(
                        CloudShell.messages.lsError(Environment.get().getCurrentFolder().getPath()));
                  }
               });
         }
         catch (RequestException e)
         {
            e.printStackTrace();
            CloudShell.console().println(CloudShell.messages.lsError(Environment.get().getCurrentFolder().getPath()));
         }
      }
      else
      {
         try
         {
            VirtualFileSystem.getInstance().getChildren(Environment.get().getCurrentFolder(),
               new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>()))
               {

                  @Override
                  protected void onSuccess(List<Item> result)
                  {
                     List<Item> folders = new ArrayList<Item>();
                     for (Item i : result)
                     {
                        if (i.getName().startsWith(namePrefix))
                        {
                           folders.add(i);
                        }
                     }
                     if (folders.isEmpty())
                     {
                        return;
                     }
                     if (folders.size() == 1)
                     {
                        String buf = display.getBuffer();
                        display.clearBuffer();
                        display.appendBuffer(buf + folders.get(0).getName().substring(namePrefix.length()) + "/");
                        display.refreshConsole();
                     }
                     else
                     {
                        display.appendBuffer("\n");
                        display.appendBuffer(Utils.fomatItems(folders));
                        display.submitBuffer();
                        display.printPrompt();
                        display.appendBuffer(prefix);
                        display.refreshConsole();
                     }
                  }

                  @Override
                  protected void onFailure(Throwable exception)
                  {
                     CloudShell.console().println(
                        CloudShell.messages.lsError(Environment.get().getCurrentFolder().getPath()));
                  }
               });
         }
         catch (RequestException e)
         {
            e.printStackTrace();
            CloudShell.console().println(CloudShell.messages.lsError(Environment.get().getCurrentFolder().getPath()));
         }
      }

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
