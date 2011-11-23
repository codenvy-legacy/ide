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

import java.util.List;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorGoToLineEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.model.settings.SettingsService;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorCapability;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.api.event.EditorContentChangedEvent;
import org.exoplatform.ide.editor.api.event.EditorContentChangedHandler;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityEvent;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityHandler;
import org.exoplatform.ide.editor.api.event.EditorTokenListPreparedEvent;
import org.exoplatform.ide.editor.api.event.EditorTokenListPreparedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
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
   EditorCursorActivityHandler, ShowOutlineHandler, ViewClosedHandler, ApplicationSettingsReceivedHandler,  
   EditorTokenListPreparedHandler
{

   /**
    * View for outline panel.
    */
   public interface Display extends IsView
   {

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

      void setRefreshingMarkInTitle();

      void removeRefreshingMarkFromTitle();

      void clearOutlineTree();
   }

   private Display display;

   private List<TokenBeenImpl> tokens = null;

   private int currentRow;

   private TokenBeenImpl currentToken;

   private FileModel activeFile;

   private Editor activeEditor;

   private boolean onItemClicked = false;

   JavaScriptObject lastFocusedElement;
   
   private ApplicationSettings applicationSettings;

   public OutlinePresenter()
   {
      IDE.addHandler(ShowOutlineEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
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
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView((View)d);
         bindDisplay(d);
      }
   }

   @Override
   public void onShowOutline(ShowOutlineEvent event)
   {
      if (event.isShow() && display == null)
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView((View)d);
         bindDisplay(d);

         applicationSettings.setValue("outline", Boolean.valueOf(event.isShow()), Store.COOKIES);
         SettingsService.getInstance().saveSettingsToCookies(applicationSettings);

         return;
      }

      if (!event.isShow() && display != null)
      {
         IDE.getInstance().closeView(display.asView().getId());
         applicationSettings.setValue("outline", Boolean.valueOf(event.isShow()), Store.COOKIES);
         SettingsService.getInstance().saveSettingsToCookies(applicationSettings);

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

      display.getOutlineTree().addKeyPressHandler(new KeyPressHandler()
      {
         public void onKeyPress(KeyPressEvent event)
         {
            onItemClicked();
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

   private void onItemSelected(TokenBeenImpl selectedItem)
   {
      if (!onItemClicked ||
               currentToken != null && selectedItem.getName().equals(currentToken.getName())
               && selectedItem.getLineNumber() == currentToken.getLineNumber())
      {
         return;
      }

      currentToken = selectedItem;

      setEditorCursorPosition(selectedItem.getLineNumber());
      
      onItemClicked = false;
   }

   /**
    * Update cursor position in editor
    */
   private void onItemClicked()
   {
      onItemClicked = true;
      
      if (display.getSelectedTokens().size() > 0)
      {
         currentToken = display.getSelectedTokens().get(0);
         setEditorCursorPosition(currentToken.getLineNumber());
      }
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
    
      display.setRefreshingMarkInTitle();
      activeEditor.getTokenListInBackground();
   }

   /**
    * Set cursor within the Editor Line into the line with lineNumber and then return focus to the Outline Tree Grid
    * @param lineNumber
    */
   private void setEditorCursorPosition(int lineNumber)
   {
      int maxLineNumber = activeFile.getContent().split("\n").length;

      // restore focus on OutlinePanel
      lastFocusedElement = getActiveElement();
      IDE.fireEvent(new EditorGoToLineEvent(lineNumber < maxLineNumber ? lineNumber : maxLineNumber));
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

      if (display == null)
      {
         return;
      }

      refreshOutlineTimer.cancel();
      
      if (canShowOutline())
      {
         display.clearOutlineTree();
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
      if (tokens == null || tokens.isEmpty())
      {
         return false;
      }
      
      for (int i = 0; i < tokens.size(); i++)
      {
         TokenBeenImpl token = tokens.get(i);
         if (currentRow < token.getLineNumber()
               || ! shouldBeDisplayed(token)
             )
         {
            continue;
         }

         TokenBeenImpl next = null;
         if ((i + 1) != tokens.size())
         {
            next = tokens.get(i + 1);
         }

         if (isCurrentToken(currentRow, token, next))
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

   /**
    * Test if current line within the token's area (currentLineNumber >= token.lineNumber) and (currentLineNumber <= token.lastLineNumber)
    * or current line is before nextToken
    * or current line is after last token 
    * @param currentLineNumber 
    * @param token
    * @return
    */
   private boolean isCurrentToken(int currentLineNumber, TokenBeenImpl token, TokenBeenImpl nextToken)
   {      
      if (currentLineNumber == token.getLineNumber())
      {
         return true;
      }
      
      if (token.getLastLineNumber() != 0)
      {
         return currentLineNumber >= token.getLineNumber()
                  && currentLineNumber <= token.getLastLineNumber();         
      }
         
      // test if currentLineNumber before nextToken
      if (nextToken != null)
      {
         return currentLineNumber < nextToken.getLineNumber();
      }

      return currentLineNumber >= token.getLineNumber();
   }
   
   /**
    * Test should token be displayed in outline tree.
    * @param token
    * @return true only if token should be displayed in outline tree
    */
   private boolean shouldBeDisplayed(TokenBeenImpl token)
   {
      return ! (token.getType().equals(TokenType.IMPORT));
   }

   private void selectToken(TokenBeenImpl token)
   {
      if (!isCurrentTokenSelected(token))
      {
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

      if (!onItemClicked)
      {   
         if (currentRow == event.getRow())
         {
            return;
         }
   
         currentRow = event.getRow();

         selectOutlineTimer.cancel();
         selectOutlineTimer.schedule(100);
      }
      else 
      {
         // restore focus on OutlinePanel
         if (lastFocusedElement != null)
         {
            setElementFocus(lastFocusedElement);   
         }
         
         onItemClicked = false;
      }
   }

   private Timer selectOutlineTimer = new Timer()
   {
      @Override
      public void run()
      {
         if (tokens != null && !tokens.isEmpty())
         {
            // restore focus of FileTab
            JavaScriptObject lastFocusedElement = getActiveElement();
            
            if (!selectTokenByRow(tokens))
            {
               display.deselectAllTokens();
            }
            else
            {
               setElementFocus(lastFocusedElement);
            }
         }
      }
   };
   
   private native JavaScriptObject getActiveElement() /*-{
      return $doc.activeElement;
   }-*/;
   
   private native void setElementFocus(JavaScriptObject element) /*-{
      element.focus();
   }-*/;

   public void onEditorTokenListPrepared(EditorTokenListPreparedEvent event)
   {      
      if (event.getTokenList() == null 
               || display == null
               || ! activeEditor.getEditorId().equals(event.getEditorId()))
      {
         return;
      }

      display.removeRefreshingMarkFromTitle();
      
      tokens = (List<TokenBeenImpl>) event.getTokenList();
      
      // restore focus of FileTab
      JavaScriptObject lastFocusedElement = getActiveElement();        

      TokenBeenImpl token = new TokenBeenImpl();
      token.setSubTokenList(tokens);
      display.getOutlineTree().setValue(token);
      currentRow = activeEditor.getCursorRow();
      currentToken = null;
      
      if (!selectTokenByRow(tokens))
      {
         display.deselectAllTokens();
      }

      // restore focus of FileTab
      setElementFocus(lastFocusedElement);      
   }
}
