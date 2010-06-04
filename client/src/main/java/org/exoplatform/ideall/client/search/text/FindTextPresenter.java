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
package org.exoplatform.ideall.client.search.text;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ideall.client.editor.event.EditorFindReplaceTextEvent;
import org.exoplatform.ideall.client.editor.event.EditorFindTextEvent;
import org.exoplatform.ideall.client.editor.event.EditorReplaceTextEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.search.text.event.FindTextResultEvent;
import org.exoplatform.ideall.client.search.text.event.FindTextResultHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class FindTextPresenter implements FindTextResultHandler
{
   interface Display
   {
      HasValue<Boolean> getCaseSensitiveField();

      HasValue<String> getResultLabel();

      TextFieldItem getFindField();

      TextFieldItem getReplaceField();

      HasClickHandlers getFindButton();

      HasClickHandlers getReplaceButton();

      HasClickHandlers getReplaceFindButton();

      HasClickHandlers getReplaceAllButton();

      HasClickHandlers getCancelButton();

      void enableFindButton(boolean isEnable);

      void enableReplaceFindButton(boolean isEnable);

      void enableReplaceButton(boolean isEnable);

      void enableReplaceAllButton(boolean isEnable);

      void closeForm();
   }

   private Display display;

   private HandlerManager eventBus;

   private Handlers handlers;

   private ApplicationContext context;

   public FindTextPresenter(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);
      handlers.addHandler(FindTextResultEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      this.display = d;

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent arg0)
         {
            display.closeForm();
         }
      });

      display.getFindButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent arg0)
         {
            String findText = display.getFindField().getValue();
            if (findText != null && findText.length() > 0)
            {
               doFind(findText);
            }
         }

      });

      display.getReplaceButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent arg0)
         {
            String findText = display.getFindField().getValue();
            String replaceText =
               (display.getReplaceField().getValue() == null) ? "" : display.getReplaceField().getValue();
            if (findText != null && findText.length() > 0)
            {
               doReplace(findText, replaceText);
            }
         }

      });

      display.getReplaceFindButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent arg0)
         {
            String findText = display.getFindField().getValue();
            String replaceText =
               (display.getReplaceField().getValue() == null) ? "" : display.getReplaceField().getValue();
            if (findText != null && findText.length() > 0)
            {
               doFindReplace(findText, replaceText);
            }
         }

      });

      display.getReplaceAllButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent arg0)
         {
            String findText = display.getFindField().getValue();
            String replaceText =
               (display.getReplaceField().getValue() == null) ? "" : display.getReplaceField().getValue();
            if (findText != null && findText.length() > 0)
            {
               doReplaceAll(findText, replaceText);
            }
         }
      });

      display.getFindField().addKeyPressHandler(new KeyPressHandler()
      {

         public void onKeyPress(KeyPressEvent arg0)
         {
            display.enableReplaceButton(false);
            display.enableReplaceFindButton(false);
            Timer timer = new Timer()
            {
               @Override
               public void run()
               {
                  String findText = display.getFindField().getValue();
                  if (findText != null && findText.length() > 0)
                  {
                     display.enableFindButton(true);
                     display.enableReplaceAllButton(true);
                  }
                  else
                  {
                     disableAllButtons();
                  }
               }
            };
            timer.schedule(10);
         }
      });

      disableAllButtons();
   }

   private void disableAllButtons()
   {
      display.enableFindButton(false);
      display.enableReplaceFindButton(false);
      display.enableReplaceAllButton(false);
      display.enableReplaceButton(false);
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   private void doFind(String findText)
   {
      boolean caseSensitive = display.getCaseSensitiveField().getValue();
      String path = context.getActiveFile().getHref();
      eventBus.fireEvent(new EditorFindTextEvent(findText, caseSensitive, path));
   }

   /**
    * @param findText text to find
    * @param replaceText text to replace with
    */
   private void doReplace(String findText, String replaceText)
   {
      boolean caseSensitive = display.getCaseSensitiveField().getValue();
      String path = context.getActiveFile().getHref();
      eventBus.fireEvent(new EditorReplaceTextEvent(findText, replaceText, caseSensitive, path));
      display.enableReplaceButton(false);
      display.enableReplaceFindButton(false);
   }
   
   private void doFindReplace(String findText, String replaceText)
   {
      boolean caseSensitive = display.getCaseSensitiveField().getValue();
      String path = context.getActiveFile().getHref();
      eventBus.fireEvent(new EditorFindReplaceTextEvent(findText, replaceText, caseSensitive, path));
   }

   /**
    * @param findText text to find
    * @param replaceText text to replace with
    */
   private void doReplaceAll(String findText, String replaceText)
   {
      boolean caseSensitive = display.getCaseSensitiveField().getValue();
      String path = context.getActiveFile().getHref();
      eventBus.fireEvent(new EditorReplaceTextEvent(findText, replaceText, caseSensitive, path, true));
   }

   /**
    * @see org.exoplatform.ideall.client.search.text.event.FindTextResultHandler#onFindTextResult(org.exoplatform.ideall.client.search.text.event.FindTextResultEvent)
    */
   public void onFindTextResult(FindTextResultEvent event)
   {
      display.enableReplaceFindButton(event.isFound());
      display.enableReplaceButton(event.isFound());
      String resultString = (event.isFound()) ? "" : "String not found.";
      display.getResultLabel().setValue(resultString);
   }
}
