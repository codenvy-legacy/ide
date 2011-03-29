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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorGoToLineEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.settings.event.SaveApplicationSettingsEvent;
import org.exoplatform.ide.client.framework.settings.event.SaveApplicationSettingsEvent.SaveType;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.gwt.ViewDisplay;
import org.exoplatform.ide.client.framework.ui.gwt.ViewEx;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.outline.event.ShowOutlineEvent;
import org.exoplatform.ide.client.outline.event.ShowOutlineHandler;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.api.event.EditorContentChangedEvent;
import org.exoplatform.ide.editor.api.event.EditorContentChangedHandler;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityEvent;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;

/**
 * Presenter for Outline Panel.
 * 
 * Handlers editor and outline panel activity 
 * and synchronize cursor position in editor with current token in outline.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class OutlinePresenter implements EditorActiveFileChangedHandler, EditorContentChangedHandler,
   EditorCursorActivityHandler, ShowOutlineHandler, ViewClosedHandler, ApplicationSettingsReceivedHandler
{

   /**
    * View for outline panel.
    */
   public interface Display extends ViewDisplay
   {

      String ID = "ideOutlineView";

      /**
       * Get outline tree grid
       * @return {@link TreeGridItem}
       */
      TreeGridItem<TokenBeenImpl> getOutlineTree();

      /**
       * Select row with token in outline tree.
       * 
       * @param token - token to select
       */
      void selectToken(TokenBeenImpl token);

      /**
       * Get the list of selected tokens.
       * 
       * @return {@link List}
       */
      List<TokenBeenImpl> getSelectedTokens();

      void setOutlineAvailable(boolean available);

   }

   private HandlerManager eventBus;

   /**
    * Used to remove handlers when they are no longer needed.
    */
   private Map<GwtEvent.Type<?>, HandlerRegistration> handlerRegistrations =
      new HashMap<GwtEvent.Type<?>, HandlerRegistration>();

   private Display display;

   private List<TokenBeenImpl> tokens = new ArrayList<TokenBeenImpl>();

   private int currentRow;

   private boolean goToLine;

   private TokenBeenImpl currentToken;

   private File activeFile;

   private Editor activeEditor;

   private boolean afterChangineCursorFromOutline = false;

   private ApplicationSettings applicationSettings;

   public OutlinePresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(ShowOutlineEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(EditorContentChangedEvent.TYPE, this);
      eventBus.addHandler(EditorCursorActivityEvent.TYPE, this);
   }

   @Override
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      this.applicationSettings = event.getApplicationSettings();

      boolean showOutline = applicationSettings.getValueAsBoolean("outline");
      if (showOutline)
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView((ViewEx)d);
         bindDisplay(d);
      }
   }

   @Override
   public void onShowOutline(ShowOutlineEvent event)
   {
      if (event.isShow() && display == null)
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView((ViewEx)d);
         bindDisplay(d);

         applicationSettings.setValue("outline", new Boolean(event.isShow()), Store.COOKIES);
         eventBus.fireEvent(new SaveApplicationSettingsEvent(applicationSettings, SaveType.COOKIES));

         return;
      }

      if (!event.isShow() && display != null)
      {
         IDE.getInstance().closeView(Display.ID);

         applicationSettings.setValue("outline", new Boolean(event.isShow()), Store.COOKIES);
         eventBus.fireEvent(new SaveApplicationSettingsEvent(applicationSettings, SaveType.COOKIES));

         return;
      }
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   public void bindDisplay(Display d)
   {
      display = d;

      display.getOutlineTree().addSelectionHandler(new SelectionHandler<TokenBeenImpl>()
      {
         public void onSelection(SelectionEvent<TokenBeenImpl> event)
         {
            onItemSelected(event.getSelectedItem());
         }
      });

      display.getOutlineTree().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            onItemClicked();
         }
      });

      currentRow = 0;
      goToLine = true;

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

   private void onItemSelected(TokenBeenImpl selectedItem)
   {
      if (currentToken != null && selectedItem.getName().equals(currentToken.getName())
         && selectedItem.getLineNumber() == currentToken.getLineNumber())
      {
         return;
      }

      currentToken = selectedItem;

      if (goToLine)
      {
         setEditorCursorPosition(selectedItem.getLineNumber());
      }
      goToLine = true;
   }

   /**
    * Update cursor position in editor
    */
   private void onItemClicked()
   {
      if (display.getSelectedTokens().size() > 0)
      {
         currentToken = display.getSelectedTokens().get(0);
         setEditorCursorPosition(currentToken.getLineNumber());
      }
   }

   /**
    * Refresh Outline Tree
    * 
    * @param editor
    */
   private void refreshOutlineTree()
   {
      if (activeEditor == null)
      {
         return;
      }

      tokens = (List<TokenBeenImpl>)activeEditor.getTokenList();
      TokenBeenImpl token = new TokenBeenImpl();
      token.setSubTokenList(tokens);
      display.getOutlineTree().setValue(token);
      currentRow = activeEditor.getCursorRow();
      currentToken = null;
      selectTokenByRow(tokens);
   }

   /**
    * Set cursor within the Editor Line into the line with lineNumber and then return focus to the Outline Tree Grid
    * @param lineNumber
    */
   private void setEditorCursorPosition(int lineNumber)
   {
      int maxLineNumber = activeFile.getContent().split("\n").length;

      afterChangineCursorFromOutline = true;
      eventBus.fireEvent(new EditorGoToLineEvent(lineNumber < maxLineNumber ? lineNumber : maxLineNumber));
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
      if (activeEditor == null || activeFile == null || activeFile.getContentType() == null)
      {
         refreshOutlineTimer.cancel();
         selectOutlineTimer.cancel();
         return false;
      }

      return OutlineSupporting.isOutlineSupported(activeFile.getContentType());
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

      if (display == null)
      {
         return;
      }

      if (canShowOutline())
      {
         display.setOutlineAvailable(true);
         refreshOutlineTree();
      }
      else
      {
         tokens = null;
         //display.getOutlineTree().setValue(new TokenBeenImpl("An Outline is not available.", null));
         display.setOutlineAvailable(false);
      }
   }

   private boolean selectTokenByRow(List<TokenBeenImpl> tokens)
   {
      if (tokens == null || tokens.size() == 0)
      {
         return false;
      }

      //if one token in list
      if (tokens.size() == 1)
      {
         TokenBeenImpl token = tokens.get(0);
         if (token.getType().equals(TokenType.TAG_BREAK))
         {
            return false;
         }
         else
         {
            if (selectTokenByRow(token.getSubTokenList()))
            {
               return true;
            }
            else
            {
               selectToken(token);
               return true;
            }
         }
      }

      //if more then one token in list
      for (int i = 0; i < tokens.size() - 1; i++)
      {
         TokenBeenImpl token = tokens.get(i);
         TokenBeenImpl next = tokens.get(i + 1);

         if (currentRow == token.getLineNumber())
         {
            selectToken(token);
            return true;
         }
         if (currentRow == next.getLineNumber())
         {
            if (next.getType().equals(TokenType.TAG_BREAK))
            {
               return false;
            }
            else
            {
               selectToken(next);
               return true;
            }
         }

         //check is to select last token or may be it has subtokens
         if (currentRow > next.getLineNumber() && (i + 1 == tokens.size() - 1))
         {
            if (next.getType().equals(TokenType.TAG_BREAK))
            {
               return false;
            }
            else
            {
               if (selectTokenByRow(next.getSubTokenList()))
               {
                  return true;
               }
               else
               {
                  selectToken(next);
                  return true;
               }
            }
         }

         if (currentRow > token.getLineNumber() && currentRow < next.getLineNumber())
         {
            if (selectTokenByRow(token.getSubTokenList()))
            {
               return true;
            }
            else
            {
               selectToken(token);
               return true;
            }
         }
      }

      return false;
   }

   private void selectToken(TokenBeenImpl token)
   {
      if (!isCurrentTokenSelected(token))
      {
         goToLine = false;
         display.selectToken(token);
      }
   }

   /**
    * Check, is token is current and is it selected.
    * 
    * @param token token to check
    * @return boolean
    */
   private boolean isCurrentTokenSelected(TokenBeenImpl token)
   {
      if (currentToken == null)
      {
         return false;
      }

      if (token == null || token.getName() == null)
      {
         return false;
      }

      return token.getName().equals(currentToken.getName()) && token.getLineNumber() == currentToken.getLineNumber();
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

      if (currentRow == event.getRow())
      {
         return;
      }

      currentRow = event.getRow();

      selectOutlineTimer.cancel();

      // return focus to the outline panel after the setting of cursor position in the Editor
      if (afterChangineCursorFromOutline)
      {
         afterChangineCursorFromOutline = false;
      }

      selectOutlineTimer.schedule(1000);
   }

   private Timer selectOutlineTimer = new Timer()
   {
      @Override
      public void run()
      {
         selectTokenByRow(tokens);
      }
   };

}
