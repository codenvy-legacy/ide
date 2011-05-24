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
package org.exoplatform.ide.client.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.edit.event.FindTextEvent;
import org.exoplatform.ide.client.edit.event.FindTextHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFindTextEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceAndFindTextEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorReplaceTextEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorTextFoundEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorTextFoundHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.vfs.File;

import java.util.HashMap;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class FindTextPresenter implements EditorTextFoundHandler, EditorActiveFileChangedHandler,
   EditorFileClosedHandler, ViewClosedHandler, FindTextHandler
{

   public interface Display extends IsView
   {

      String ID = "ideFindReplaceTextView";

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
      
      void focusInFindField();

   }

   private Display display;

   private HandlerManager eventBus;

   private final String STRING_NOT_FOUND = "String not found.";

   private HashMap<String, FindTextState> filesFindState;

   private File activeFile;

   public FindTextPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      filesFindState = new HashMap<String, FindTextState>();

      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
      eventBus.addHandler(FindTextEvent.TYPE, this);
      eventBus.addHandler(EditorTextFoundEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();

      if (display == null)
      {
         return;
      }

      if (activeFile == null)
      {
         IDE.getInstance().closeView(Display.ID);
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

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   @Override
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      filesFindState.remove(event.getFile().getHref());
   }

   @Override
   public void onFindText(FindTextEvent event)
   {
      if (display == null)
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView(d.asView());
         bindDisplay(d);
         display.focusInFindField();
      }

   }

   public void bindDisplay(Display d)
   {
      this.display = d;

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(Display.ID);
         }
      });

      display.getFindButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            doFind();
         }
      });

      display.getReplaceButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            doReplace();
         }
      });

      display.getReplaceFindButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            doFindReplace();
         }
      });

      display.getReplaceAllButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            doReplaceAll();
         }
      });

      display.getFindField().addKeyPressHandler(new KeyPressHandler()
      {
         public void onKeyPress(KeyPressEvent event)
         {
            findTextFieldKeyPressed();
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

   private void doFind()
   {
      String findText = display.getFindField().getValue();
      if (findText == null || findText.isEmpty())
      {
         return;
      }

      boolean caseSensitive = display.getCaseSensitiveField().getValue();
      String path = activeFile.getHref();
      eventBus.fireEvent(new EditorFindTextEvent(findText, caseSensitive, path));
   }

   private void doReplace()
   {
      String findText = display.getFindField().getValue();
      if (findText == null || findText.isEmpty())
      {
         return;
      }

      String replaceText = display.getReplaceField().getValue();
      if (replaceText == null)
      {
         replaceText = "";
      }

      boolean caseSensitive = display.getCaseSensitiveField().getValue();
      String path = activeFile.getHref();
      eventBus.fireEvent(new EditorReplaceTextEvent(findText, replaceText, caseSensitive, path));
      FindTextState findTextState = new FindTextState(false, "", findText);
      changeState(findTextState);
      filesFindState.put(path, findTextState);
   }

   private void doFindReplace()
   {
      String findText = display.getFindField().getValue();
      if (findText == null || findText.isEmpty())
      {
         return;
      }

      String replaceText = display.getReplaceField().getValue();
      if (replaceText == null)
      {
         replaceText = "";
      }

      boolean caseSensitive = display.getCaseSensitiveField().getValue();
      String path = activeFile.getHref();
      eventBus.fireEvent(new EditorReplaceAndFindTextEvent(findText, replaceText, caseSensitive, path));
   }

   private void doReplaceAll()
   {
      String findText = display.getFindField().getValue();
      if (findText == null || findText.isEmpty())
      {
         return;
      }

      String replaceText = display.getReplaceField().getValue();
      if (replaceText == null)
      {
         replaceText = "";
      }

      boolean caseSensitive = display.getCaseSensitiveField().getValue();
      String path = activeFile.getHref();
      eventBus.fireEvent(new EditorReplaceTextEvent(findText, replaceText, caseSensitive, path, true));
   }

   private void findTextFieldKeyPressed()
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

   /**
    * @see org.exoplatform.ide.client.search.text.event.EditorTextFoundHandler#onEditorTextFound(org.exoplatform.ide.client.search.text.event.EditorTextFoundEvent)
    */
   public void onEditorTextFound(EditorTextFoundEvent event)
   {
      String resultString = (event.isTextFound()) ? "" : STRING_NOT_FOUND;
      String findText = display.getFindField().getValue();
      FindTextState findTextState = new FindTextState(event.isTextFound(), resultString, findText);
      changeState(findTextState);
      filesFindState.put(activeFile.getHref(), findTextState);
   }

   private void changeState(FindTextState state)
   {
      display.enableReplaceButton(state.isReplaceEnabled());
      display.enableReplaceFindButton(state.isReplaceEnabled());
      display.getResultLabel().setValue(state.getResultText());
      display.getFindField().setValue(state.getFindText());
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
       * @return the resultText
       */
      public String getResultText()
      {
         return resultText;
      }

      /**
       * @return the findText
       */
      public String getFindText()
      {
         return findText;
      }

   }

}
