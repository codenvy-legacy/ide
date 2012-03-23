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
package org.exoplatform.ide.client.outline;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Timer;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorGoToLineEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.outline.ui.OutlineDisplay;
import org.exoplatform.ide.client.framework.outline.ui.ShowOutlineEvent;
import org.exoplatform.ide.client.framework.outline.ui.ShowOutlineHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.client.model.settings.SettingsService;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorCapability;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.event.EditorContentChangedEvent;
import org.exoplatform.ide.editor.api.event.EditorContentChangedHandler;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityEvent;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityHandler;
import org.exoplatform.ide.editor.api.event.EditorTokenListPreparedEvent;
import org.exoplatform.ide.editor.api.event.EditorTokenListPreparedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.List;

/**
 * Presenter for Outline Panel.
 * 
 * Handlers editor and outline panel activity and synchronize cursor position in editor with current token in outline.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 * 
 */
public class OutlinePresenter implements EditorActiveFileChangedHandler, EditorContentChangedHandler,
   EditorCursorActivityHandler, ShowOutlineHandler, ViewClosedHandler, ViewOpenedHandler,
   ApplicationSettingsReceivedHandler, EditorTokenListPreparedHandler
{

   /**
    * View for outline panel.
    */
   public interface Display extends OutlineDisplay
   {

      /**
       * Select row with token in outline tree.
       * 
       * @param token - token to select
       */
      void selectToken(TokenBeenImpl token);

      /**
       * Sets is outline available
       * 
       * @param available
       */
      void setOutlineAvailable(boolean available);

      /**
       * Remove selection from any token
       */
      void deselectAllTokens();

      void setValue(List<TokenBeenImpl> tokens);

      SingleSelectionModel<Object> getSingleSelectionModel();

      void focusInTree();
   }

   private Display display;

   private List<TokenBeenImpl> tokens = null;

   private int currentRow;

   private FileModel activeFile;

   private Editor activeEditor;

   /**
    * Outline selection must be processed or not.
    */
   private boolean processSelection = true;

   /**
    * Editor activity must be processed or not.
    */
   private boolean processEditorActivity = true;

   JavaScriptObject lastFocusedElement;

   private ApplicationSettings applicationSettings;

   private boolean isOutlineViewOpened = false;

   private Timer selectOutlineTimer = new Timer()
   {
      @Override
      public void run()
      {
         if (tokens != null && !tokens.isEmpty())
         {
            selectToken(currentRow);
         }
      }
   };

   public OutlinePresenter()
   {
      IDE.addHandler(ShowOutlineEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ViewOpenedEvent.TYPE, this);
      IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(EditorContentChangedEvent.TYPE, this);
      IDE.addHandler(EditorCursorActivityEvent.TYPE, this);
      IDE.addHandler(EditorTokenListPreparedEvent.TYPE, this);

      IDE.getInstance().addControl(new ShowOutlineControl(), Docking.TOOLBAR);
   }

   @Override
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      this.applicationSettings = event.getApplicationSettings();

      boolean showOutline = false;
      if (applicationSettings.getValueAsBoolean("outline") == null)
      {
         applicationSettings.setValue("outline", false, Store.COOKIES);
      }
      else
      {
         showOutline = applicationSettings.getValueAsBoolean("outline");
      }

      if (showOutline)
      {
         // TODO temporary solution not to open Outline for Java files, but save settings:
         if (activeFile != null && !MimeType.APPLICATION_JAVA.equals(activeFile.getMimeType()))
         {
            Display d = GWT.create(Display.class);
            IDE.getInstance().openView((View)d);
            bindDisplay(d);
         }
      }
   }

   @Override
   public void onShowOutline(ShowOutlineEvent event)
   {
      applicationSettings.setValue("outline", Boolean.valueOf(event.isShow()), Store.COOKIES);
      SettingsService.getInstance().saveSettingsToCookies(applicationSettings);
      if (event.isShow() && display == null)
      {
         // TODO temporary solution not to open Outline for Java files, but save settings:
         if (activeFile != null && !MimeType.APPLICATION_JAVA.equals(activeFile.getMimeType()))
         {
            Display d = GWT.create(Display.class);
            IDE.getInstance().openView((View)d);
            bindDisplay(d);
         }
         return;
      }

      if (!event.isShow())
      {
         if (display != null)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      }
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }

      if (event.getView() instanceof OutlineDisplay)
      {
         isOutlineViewOpened = false;
      }
   }

   public void bindDisplay(Display d)
   {
      display = d;

      display.getSingleSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler()
      {

         @Override
         public void onSelectionChange(SelectionChangeEvent event)
         {
            if (!processSelection)
            {
               processSelection = true;
               return;
            }

            if (display.getSingleSelectionModel().getSelectedObject() instanceof TokenBeenImpl)
            {
               selectEditorLine(((TokenBeenImpl)display.getSingleSelectionModel().getSelectedObject()).getLineNumber());
            }
         }
      });

      currentRow = 0;

      if (canShowOutline())
      {
         display.setOutlineAvailable(true);
         refreshOutlineTree();
      }
      else
      {
         tokens = null;
         display.setOutlineAvailable(false);
      }
   }

   public void selectEditorLine(int line)
   {
      processEditorActivity = false;
      IDE.fireEvent(new EditorGoToLineEvent(line));
   }

   /**
    * Refresh Outline Tree
    * 
    * @param scheduledEditor
    */
   private void refreshOutlineTree()
   {
      if (activeEditor == null)
      {
         return;
      }
      activeEditor.getTokenListInBackground();
   }

   public void onEditorContentChanged(EditorContentChangedEvent event)
   {
      if (display == null || !canShowOutline())
      {
         return;
      }

      refreshOutlineTimer.cancel();
      refreshOutlineTimer.schedule(2000);
   }

   private boolean canShowOutline()
   {
      if (activeEditor == null || activeFile == null || activeFile.getMimeType() == null)
      {
         refreshOutlineTimer.cancel();
         selectOutlineTimer.cancel();
         return false;
      }

      return activeEditor.isCapable(EditorCapability.CAN_BE_OUTLINED);
   }

   private Timer refreshOutlineTimer = new Timer()
   {
      @Override
      public void run()
      {
         try
         {
            refreshOutlineTree();
         }
         catch (Throwable e)
         {
            Dialogs.getInstance().showError(e.getMessage());
         }
      }
   };

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
      activeEditor = event.getEditor();
      // TODO temporary solution to close Outline for Java files:
      if (activeFile != null && MimeType.APPLICATION_JAVA.equals(activeFile.getMimeType()))
      {
         if (display != null)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
         return;
      }

      if (display == null)
      {
         if (isOutlineViewOpened)
         {
            Display d = GWT.create(Display.class);
            IDE.getInstance().openView((View)d);
            bindDisplay(d);
         }
         else
         {
            return;
         }
      }

      refreshOutlineTimer.cancel();

      if (canShowOutline())
      {
         display.setOutlineAvailable(true);
         display.setValue(null);
         refreshOutlineTree();
      }
      else
      {
         tokens = null;
         display.setOutlineAvailable(false);
      }
   }

   private void selectToken(int lineNumber)
   {
      TokenBeenImpl token = getNodeByLineNumber(lineNumber, tokens);
      if (token != null)
      {
         processSelection = false;
         display.selectToken(token);
      }
   }

   protected TokenBeenImpl getNodeByLineNumber(int lineNumber, List<TokenBeenImpl> tokens)
   {
      for (TokenBeenImpl token : tokens)
      {
         int startLineNumber = token.getLineNumber();
         int endLineNumber = token.getLastLineNumber();

         if (startLineNumber == lineNumber)
         {
            return token;
         }

         // Check current line is between node's start and end lines:
         if (startLineNumber <= lineNumber && lineNumber <= endLineNumber)
         {

            // If there are no children - return this node
            if (token.getSubTokenList() == null || token.getSubTokenList().isEmpty())
            {
               return token;
            }
            // Checking line ranges of children, if no proper is found - return parent node:
            else
            {
               TokenBeenImpl foundToken = getNodeByLineNumber(lineNumber, token.getSubTokenList());
               return (foundToken == null) ? token : foundToken;
            }
         }
      }
      // Nothing was found:
      return null;
   }

   /**
    * @see org.exoplatform.gwtframework.editor.event.EditorCursorActivityHandler#onEditorCursorActivity(org.exoplatform.gwtframework.editor.event.EditorCursorActivityEvent)
    */
   public void onEditorCursorActivity(EditorCursorActivityEvent event)
   {
      if (display == null)
      {
         return;
      }
      if (!processEditorActivity)
      {
         display.focusInTree();
         processEditorActivity = true;
         return;
      }

      if (currentRow == event.getRow())
      {
         return;
      }
      currentRow = event.getRow();
      selectOutlineTimer.cancel();
      selectOutlineTimer.schedule(100);
   }

   @SuppressWarnings("unchecked")
   public void onEditorTokenListPrepared(EditorTokenListPreparedEvent event)
   {
      if (event.getTokenList() == null || display == null || !activeEditor.getEditorId().equals(event.getEditorId()))
      {
         return;
      }

      tokens = (List<TokenBeenImpl>)event.getTokenList();
      display.setValue(tokens);
      if (activeEditor != null)
      {
         selectToken(activeEditor.getCursorRow());
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler#onViewOpened(org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent)
    */
   @Override
   public void onViewOpened(ViewOpenedEvent event)
   {
      if (event.getView() instanceof OutlineDisplay)
      {
         isOutlineViewOpened = true;
      }
   }
}
