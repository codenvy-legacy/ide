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
package org.exoplatform.ide.client.module.edit.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorDeleteCurrentLineEvent;
import org.exoplatform.ide.client.framework.vfs.Version;
import org.exoplatform.ide.editor.api.EditorCapability;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
@RolesAllowed({"administrators", "developers"})
public class DeleteCurrentLineControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler
{

   public static final String ID = "Edit/Delete Current Line";

   public DeleteCurrentLineControl()
   {
      super(ID);
      setTitle("Delete Current Line");
      setPrompt("Delete Current Line");
      setImages(IDEImageBundle.INSTANCE.deleteCurrentLine(), IDEImageBundle.INSTANCE.deleteCurrentLineDisabled());
      setEvent(new EditorDeleteCurrentLineEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {

      if (event.getFile() == null || event.getEditor() == null || (event.getFile() instanceof Version))
      {
         setVisible(false);
         setEnabled(false);
         return;
      }

      if (event.getEditor().isCapable(EditorCapability.DELETE_CURRENT_LINE))
      {
         setVisible(true);
         setEnabled(true);
      }
      else
      {
         setVisible(false);
         setEnabled(false);
      }

      if (event.getEditor().isReadOnly())
      {
         setEnabled(false);
         return;
      }
   }
}
