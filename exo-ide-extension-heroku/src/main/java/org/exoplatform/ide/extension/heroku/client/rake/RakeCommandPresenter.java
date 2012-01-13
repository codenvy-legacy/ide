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
package org.exoplatform.ide.extension.heroku.client.rake;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.heroku.client.HerokuClientService;
import org.exoplatform.ide.extension.heroku.client.RakeCommandAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ItemContext;

/**
 * Presenter of the view for executing rake command. View must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 17, 2011 10:55:15 AM anya $
 * 
 */
public class RakeCommandPresenter extends GitPresenter implements RakeCommandHandler, ViewClosedHandler,
   LoggedInHandler
{
   interface Display extends IsView
   {
      /**
       * Get rake command text field.
       * 
       * @return {@link TextFieldItem}
       */
      TextFieldItem getCommandField();

      /**
       * Get run button's click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getRunButton();

      /**
       * Get close button's click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getCloseButton();

      /**
       * Get help button's click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getHelpButton();

      /**
       * Change the enable state of the run command button.
       * 
       * @param isEnabled
       */
      void enableRunButton(boolean isEnabled);

      /**
       * Give focus to command field.
       */
      void focusInCommandField();
   }

   /**
    * Presenter's display.
    */
   private Display display;

   /**
    * If <code>true</code> help command was called before logged in.
    */
   private boolean isHelp = false;

   /**
    *
    */
   public RakeCommandPresenter()
   {
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(RakeCommandEvent.TYPE, this);
   }

   /**
    * Bind presenter with display.
    */
   protected void bindDisplay()
   {
      display.getCloseButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getRunButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doRunCommand();
         }
      });

      display.getHelpButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doHelp();
         }
      });

      display.getCommandField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            boolean isNotEmpty = (event.getValue() != null && !event.getValue().isEmpty());
            display.enableRunButton(isNotEmpty);
         }
      });

      display.getCommandField().addKeyUpHandler(new KeyUpHandler()
      {
         @Override
         public void onKeyUp(KeyUpEvent event)
         {
            if (event.getNativeKeyCode() == 13)
            {
               doRunCommand();
            }
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.rake.RakeCommandHandler#onRakeCommand(org.exoplatform.ide.extension.heroku.client.rake.RakeCommandEvent)
    */
   @Override
   public void onRakeCommand(RakeCommandEvent event)
   {
      // if (makeSelectionCheck())
      // {

      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();
         IDE.getInstance().openView(display.asView());
         display.enableRunButton(false);
         display.focusInCommandField();
      }
      // }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   /**
    * Perform executing rake command.
    */
   public void doRunCommand()
   {
      String command = (display.getCommandField() != null) ? display.getCommandField().getValue().trim() : null;
      if (command == null)
         return;
      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      command = (command.startsWith("rake")) ? command : "rake " + command;
      isHelp = false;
      try
      {
         HerokuClientService.getInstance().run(null, vfs.getId(), projectId, command,
            new RakeCommandAsyncRequestCallback(this)
            {
               @Override
               protected void onSuccess(RakeCommandResult result)
               {
                  String message = formMessage(result.getResult());
                  IDE.fireEvent(new OutputEvent(message, Type.OUTPUT));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Gets help.
    */
   public void doHelp()
   {
      isHelp = true;
      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      try
      {
         HerokuClientService.getInstance().help(null, vfs.getId(), projectId, new RakeCommandAsyncRequestCallback(this)
         {
            @Override
            protected void onSuccess(RakeCommandResult result)
            {
               String message = formMessage(result.getResult());
               IDE.fireEvent(new OutputEvent(message, Type.INFO));
            }
         });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent)
    */
   @Override
   public void onLoggedIn(LoggedInEvent event)
   {
      IDE.removeHandler(LoggedInEvent.TYPE, this);
      if (!event.isFailed())
      {
         if (isHelp)
         {
            doHelp();
         }
         else
         {
            doRunCommand();
         }
      }
   }

   /**
    * Form message to display in output - replace spaces and ends of line.
    * 
    * @param text
    * @return {@link String}
    */
   private String formMessage(String text)
   {
      if (text == null || text.isEmpty())
         return "";

      String message = text.replaceAll("\n", "<br>");
      message = message.replaceAll(" ", "&nbsp;");
      return message;
   }

}
