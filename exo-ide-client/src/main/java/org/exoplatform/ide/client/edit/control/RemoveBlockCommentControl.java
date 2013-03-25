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
package org.exoplatform.ide.client.edit.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorRemoveBlockCommentEvent;
import org.exoplatform.ide.editor.client.api.EditorCapability;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Apr 6, 2012 10:56:42 AM anya $
 * 
 */
@RolesAllowed({"developer"})
public class RemoveBlockCommentControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler
{

   public static final String ID = "Edit/Remove Block Comment";

   private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.removeBlockCommentControl();

   public RemoveBlockCommentControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(TITLE);
      setEvent(new EditorRemoveBlockCommentEvent());
      setHotKey("Ctrl+Shift+\\");
      setImages(IDEImageBundle.INSTANCE.removeBlockComment(), IDEImageBundle.INSTANCE.removeBlockCommentDisabled());
      setGroupName(GroupNames.COMMENT);
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      boolean isEnabled = event.getFile() != null && event.getEditor() != null && event.getEditor().isCapable(EditorCapability.COMMENT_SOURCE);
      setVisible(isEnabled);
      setEnabled(isEnabled);
   }
}
