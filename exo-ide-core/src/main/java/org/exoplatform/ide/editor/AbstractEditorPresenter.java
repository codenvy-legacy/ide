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
package org.exoplatform.ide.editor;

import com.google.gwt.user.client.Window;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.part.AbstractPartPresenter;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public abstract class AbstractEditorPresenter extends AbstractPartPresenter implements EditorPartPresenter
{

   protected boolean dirtyState;

   protected EditorInput input;

   protected final JsonArray<EditorPartCloseHandler> closeHandlers = JsonCollections.createArray();


   /**
    * {@inheritDoc}
    */
   @Override
   public void init(EditorInput input) throws EditorInitException
   {
      this.input = input;
      initializeEditor();
   }

   /**
    * Initializes this editor.
    */
   protected abstract void initializeEditor();

   /**
    * Set dirty state and notify expressions 
    * @param dirty
    */
   protected void updateDirtyState(boolean dirty)
   {
      dirtyState = dirty;
      firePropertyChange(EditorPartPresenter.PROP_TITLE);
      firePropertyChange(EditorPartPresenter.PROP_DIRTY);
   }

   /**
    * @see org.exoplatform.ide.editor.EditorPartPresenter#isDirty()
    */
   @Override
   public boolean isDirty()
   {
      return dirtyState;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addCloseHandler(EditorPartCloseHandler closeHandler)
   {
      if (!closeHandlers.contains(closeHandler))
      {
         closeHandlers.add(closeHandler);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public EditorInput getEditorInput()
   {
      return input;
   }

   /**
    * @see org.exoplatform.ide.part.PartPresenter#onClose()
    */
   @Override
   public boolean onClose()
   {
      if (isDirty())
      {
         if (Window.confirm("'" + getEditorInput().getName() + "' has been modified. Save changes?"))
         {
            doSave();
         }
      }
      handleClose();
      return true;
   }

   protected void handleClose()
   {
      for (int i = 0; i < closeHandlers.size(); i++)
      {
         EditorPartCloseHandler handler = closeHandlers.get(i);
         handler.onClose(this);
      }
   }


}
