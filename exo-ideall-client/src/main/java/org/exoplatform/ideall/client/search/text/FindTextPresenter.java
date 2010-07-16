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

import java.util.HashMap;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ideall.client.editor.event.EditorCloseFileEvent;
import org.exoplatform.ideall.client.editor.event.EditorCloseFileHandler;
import org.exoplatform.ideall.client.editor.event.EditorFindReplaceTextEvent;
import org.exoplatform.ideall.client.editor.event.EditorFindTextEvent;
import org.exoplatform.ideall.client.editor.event.EditorReplaceTextEvent;
import org.exoplatform.ideall.client.form.event.OpenedFormsStateChangedEvent;
import org.exoplatform.ideall.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.framework.editor.event.EditorActiveFileChangedHandler;
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
public class FindTextPresenter implements FindTextResultHandler, EditorActiveFileChangedHandler, EditorCloseFileHandler
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

   private final String STRING_NOT_FOUND = "String not found.";

   private HashMap<String, FindTextState> filesFindState;

   /**
    * @param eventBus
    * @param context
    */
   public FindTextPresenter(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      filesFindState = new HashMap<String, FindTextState>();
      handlers = new Handlers(eventBus);
      handlers.addHandler(FindTextResultEvent.TYPE, this);
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      handlers.addHandler(EditorCloseFileEvent.TYPE, this);
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
      
      context.getOpenedForms().add(FindTextForm.ID);
      eventBus.fireEvent(new OpenedFormsStateChangedEvent());
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
      context.getOpenedForms().remove(FindTextForm.ID);
      eventBus.fireEvent(new OpenedFormsStateChangedEvent());
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
      FindTextState findTextState = new FindTextState(false, "", findText);
      changeState(findTextState);
      filesFindState.put(path, findTextState);
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
      String resultString = (event.isFound()) ? "" : STRING_NOT_FOUND;
      String findText = display.getFindField().getValue();
      FindTextState findTextState = new FindTextState(event.isFound(), resultString, findText);
      changeState(findTextState);
      filesFindState.put(context.getActiveFile().getHref(), findTextState);
   }

   /**
    * @see org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null){
         display.closeForm();
         return;
      }
      
      String path = event.getFile().getHref();
      FindTextState findTextState = filesFindState.get(path);
      if (filesFindState.get(path) == null)
      {
         String findText = display.getFindField().getValue();
         findTextState = new FindTextState(false, "", findText);
         filesFindState.put(path, findTextState);
      }
      changeState(findTextState);
   }

   private void changeState(FindTextState state)
   {
      display.enableReplaceButton(state.isReplaceEnabled());
      display.enableReplaceFindButton(state.isReplaceEnabled());
      display.getResultLabel().setValue(state.getResultText());
      display.getFindField().setValue(state.getFindText());
   }

   /**
    * @see org.exoplatform.ideall.client.editor.event.EditorCloseFileHandler#onEditorCloseFile(org.exoplatform.ideall.client.editor.event.EditorCloseFileEvent)
    */
   public void onEditorCloseFile(EditorCloseFileEvent event)
   {
      filesFindState.remove(event.getFile().getHref());
   }

   private class FindTextState
   {
      private boolean replaceEnabled;

      private String resultText;

      private String findText;

      public FindTextState(boolean replaceEnabled, String resutlText, String findText)
      {
         this.replaceEnabled = replaceEnabled;
         this.resultText = resutlText;
         this.findText = findText;
      }

      /**
       * @return the replaceEnabled
       */
      public boolean isReplaceEnabled()
      {
         return replaceEnabled;
      }

      /**
       * @param replaceEnabled the replaceEnabled to set
       */
      public void setReplaceEnabled(boolean replaceEnabled)
      {
         this.replaceEnabled = replaceEnabled;
      }

      /**
       * @return the resultText
       */
      public String getResultText()
      {
         return resultText;
      }

      /**
       * @param resultText the resultText to set
       */
      public void setResultText(String resultText)
      {
         this.resultText = resultText;
      }

      /**
       * @return the findText
       */
      public String getFindText()
      {
         return findText;
      }

      /**
       * @param findText the findText to set
       */
      public void setFindText(String findText)
      {
         this.findText = findText;
      }
   }
}
