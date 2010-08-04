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
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.editor.api.TextEditor;
import org.exoplatform.gwtframework.editor.api.Token;
import org.exoplatform.gwtframework.editor.api.Token.TokenType;
import org.exoplatform.gwtframework.editor.event.EditorActivityEvent;
import org.exoplatform.gwtframework.editor.event.EditorActivityHandler;
import org.exoplatform.gwtframework.editor.event.EditorContentChangedEvent;
import org.exoplatform.gwtframework.editor.event.EditorContentChangedHandler;
import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorGoToLineEvent;
import org.exoplatform.ide.client.module.vfs.api.File;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class OutlinePresenter implements EditorActiveFileChangedHandler, EditorContentChangedHandler,
   EditorActivityHandler
{
   interface Display
   {
      TreeGridItem<Token> getBrowserTree();

      void selectToken(Token token);

      boolean isFormVisible();
   }

   private HandlerManager eventBus;

   private Handlers handlers;

   private Display display;

   private List<Token> tokens = new ArrayList<Token>();

   private int currentRow;

   private boolean goToLine;

   private Token currentToken;
   
   private File activeFile;
   
   private TextEditor activeTextEditor;

   public OutlinePresenter(HandlerManager bus)
   {
      eventBus = bus;

      handlers = new Handlers(eventBus);
      handlers.addHandler(EditorContentChangedEvent.TYPE, this);
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      handlers.addHandler(EditorActivityEvent.TYPE, this);
      
   }

   public void bindDisplay(Display d)
   {
      display = d;

      display.getBrowserTree().addSelectionHandler(new SelectionHandler<Token>()
      {
         public void onSelection(SelectionEvent<Token> event)
         {
            if (currentToken != null && event.getSelectedItem().getName().equals(currentToken.getName()))
            {
               return;
            }

            currentToken = event.getSelectedItem();

            if (goToLine)
            {
               int line = event.getSelectedItem().getLineNumber();
               int maxLineNumber = activeFile.getContent().split("\n").length;
               eventBus.fireEvent(new EditorGoToLineEvent(line < maxLineNumber ? line : maxLineNumber));
            }
            goToLine = true;
         }
      });

      currentRow = 0;
      goToLine = true;
   }

   private void refreshOutline(TextEditor editor)
   {
      tokens = editor.getTokenList();
      display.getBrowserTree().setValue(new Token("", null, -1, tokens));
      currentRow = editor.getCursorRow();
      currentToken = null;
      selectTokenByRow(tokens);
   }

   public void onEditorContentChanged(EditorContentChangedEvent event)
   {
      if (isShowOutline(activeTextEditor, activeFile))
      {
         refreshOutlineTimer.cancel();
         refreshOutlineTimer.schedule(2000);
      }
   }

   private boolean isShowOutline(TextEditor editor, File file)
   {
      if (editor == null || file == null || file.getContentType() == null)
      {
         refreshOutlineTimer.cancel();
         selectOutlineTimer.cancel();
         return false;
      }

      return OutlineTreeGrid.haveOutline(file);
   }

   private Timer refreshOutlineTimer = new Timer()
   {
      @Override
      public void run()
      {
         try
         {
            refreshOutline(activeTextEditor);
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
      activeTextEditor = event.getEditor();
      
      File file = event.getFile();
      TextEditor editor = event.getEditor();
      if (isShowOutline(editor, file))
      {
         refreshOutline(editor);
      }
      else
      {
         tokens = null;
         display.getBrowserTree().setValue(new Token("", null));
      }
   }
   
   private boolean selectTokenByRow(List<Token> tokens)
   {
      if (tokens == null || tokens.size() == 0)
      {
         return false;
      }

      //if one token in list
      if (tokens.size() == 1)
      {
         Token token = tokens.get(0);
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
         Token token = tokens.get(i);
         Token next = tokens.get(i + 1);

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

   private void selectToken(Token token)
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
   private boolean isCurrentTokenSelected(Token token)
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
    * @see org.exoplatform.gwtframework.editor.event.EditorActivityHandler#onEditorActivity(org.exoplatform.gwtframework.editor.event.EditorActivityEvent)
    */
   public void onEditorActivity(EditorActivityEvent event)
   {
      if (currentRow == event.getRow())
      {
         return;
      }

      currentRow = event.getRow();

      selectOutlineTimer.cancel();
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
