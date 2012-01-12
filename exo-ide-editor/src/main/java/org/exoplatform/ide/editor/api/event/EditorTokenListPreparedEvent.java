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
 * Fires just after opened in editor content had been changed.
 * Created by The eXo Platform SAS .
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version @version $Id: $
 */

public class EditorTokenListPreparedEvent extends GwtEvent<EditorTokenListPreparedHandler>
{

   public static final GwtEvent.Type<EditorTokenListPreparedHandler> TYPE =
      new GwtEvent.Type<EditorTokenListPreparedHandler>();

   private String editorId;

   private List<? extends Token> tokenList;

   public EditorTokenListPreparedEvent(String editorId, List<? extends Token> tokenList)
   {
      this.editorId = editorId;
      this.tokenList = tokenList;
   }

   public String getEditorId()
   {
      return editorId;
   }

   public List<? extends Token> getTokenList()
   {
      return tokenList;
   }

   @Override
   protected void dispatch(EditorTokenListPreparedHandler handler)
   {
      handler.onEditorTokenListPrepared(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<EditorTokenListPreparedHandler> getAssociatedType()
   {
      return TYPE;
   }

}
