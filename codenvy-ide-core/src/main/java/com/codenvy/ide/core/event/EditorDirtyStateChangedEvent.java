/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.codenvy.ide.core.event;

import com.codenvy.ide.editor.EditorPartPresenter;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fires by editor when change dirty state(content modified or saved)
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class EditorDirtyStateChangedEvent extends GwtEvent<EditorDirtyStateChangedHandler>
{

   public static final GwtEvent.Type<EditorDirtyStateChangedHandler> TYPE = new Type<EditorDirtyStateChangedHandler>();

   private EditorPartPresenter editor;

   /**
    * @param editor
    */
   public EditorDirtyStateChangedEvent(EditorPartPresenter editor)
   {
      super();
      this.editor = editor;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<EditorDirtyStateChangedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void dispatch(EditorDirtyStateChangedHandler handler)
   {
      handler.onEditorDirtyStateChanged(this);
   }

   /**
    * @return the editor
    */
   public EditorPartPresenter getEditor()
   {
      return editor;
   }
}
