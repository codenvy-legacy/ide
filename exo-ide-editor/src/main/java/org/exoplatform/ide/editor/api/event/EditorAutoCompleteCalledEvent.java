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

package org.exoplatform.ide.editor.api.event;

import java.util.List;

import org.exoplatform.ide.editor.api.codeassitant.Token;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version @version $Id: $
 */

public class EditorAutoCompleteCalledEvent extends GwtEvent<EditorAutoCompleteCalledHandler>
{

   public static final GwtEvent.Type<EditorAutoCompleteCalledHandler> TYPE =
      new GwtEvent.Type<EditorAutoCompleteCalledHandler>();

   private String editorId;

   private String mimeType;

   private int cursorOffsetX;

   private int cursorOffsetY;

   private String lineContent;

   private int cursorPositionX;

   private int cursorPositionY;

   private List<Token> tokenList;
   
   private Token currentToken;
   
   private String lineMimeType;
   
   private String fqn;

   /**
    * 
    * @param editorId
    * @param mimeType
    * @param cursorOffsetX
    * @param cursorOffsetY
    * @param lineContent
    * @param cursorPositionX inline cursor position starting from 1
    * @param tokenList
    * @param lineMimeType
    * @param fqn  
    */
   public EditorAutoCompleteCalledEvent(String editorId, String mimeType, int cursorOffsetX, int cursorOffsetY,
      String lineContent, int cursorPositionX, int cursorPositionY, List<Token> tokenList, String lineMimeType, Token currentToken)
   {
      this.editorId = editorId;
      this.mimeType = mimeType;
      this.cursorOffsetX = cursorOffsetX;
      this.cursorOffsetY = cursorOffsetY;
      this.lineContent = lineContent;
      this.cursorPositionX = cursorPositionX;
      this.cursorPositionY = cursorPositionY;
      this.tokenList = tokenList;
      this.lineMimeType = lineMimeType;
      this.currentToken = currentToken;
   }

   public String getEditorId()
   {
      return this.editorId;
   }

   @Override
   protected void dispatch(EditorAutoCompleteCalledHandler handler)
   {
      handler.onEditorAutoCompleteCalled(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<EditorAutoCompleteCalledHandler> getAssociatedType()
   {
      return TYPE;
   }

   public String getLineContent()
   {
      return this.lineContent;
   }

   public String getMimeType()
   {
      return this.mimeType;
   }

   public int getCursorOffsetX()
   {
      return this.cursorOffsetX;
   }

   public int getCursorOffsetY()
   {
      return this.cursorOffsetY;
   }

   /**
    * @return the tokenList
    */
   public List<Token> getTokenList()
   {
      return tokenList;
   }

   /**
    * 
    * @return inline cursor position starting from 1
    */
   public int getCursorPositionX()
   {
      return cursorPositionX;
   }

   public int getCursorPositionY()
   {
      return cursorPositionY;
   }

   public String getLineMimeType()
   {
      return lineMimeType;
   }
   
   /**
    * 
    * @return <b>Fully qualified name (fqn)</b> of token before "." within the groove-code, or <b>null</b>, if fqn is unknown.
    */
   public String getFqn()
   {
//      return currentToken.getFqn();
      //TODO
      return null;
   }
   
   /**
    * Returns token before cursor
    * @param currentToken
    */
   public Token getCurrentToken()
   {
      return currentToken;
   }
}
