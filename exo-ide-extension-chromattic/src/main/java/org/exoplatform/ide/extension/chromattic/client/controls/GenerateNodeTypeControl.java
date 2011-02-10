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
package org.exoplatform.ide.extension.chromattic.client.controls;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.extension.chromattic.client.Images;
import org.exoplatform.ide.extension.chromattic.client.event.GenerateNodeTypeEvent;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Control is used for previewing new node type definition.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 6, 2010 $
 *
 */
public class GenerateNodeTypeControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler
{
   /**
    * Control's ID.
    */
   public static final String ID = "Run/Preview node type";

   /**
    * Control's title.
    */
   public static final String TITLE = "Preview node type";

   /**
   * Control's prompt, when user hovers the mouse on it.
   */
   public static final String PROMPT = "Preview node type";

   /**
    * Default constructor.
    */
   public GenerateNodeTypeControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setIcon(Images.Controls.PREVIEW_NODE_TYPE);
      setEvent(new GenerateNodeTypeEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null)
      {
         setEnabled(false);
         setVisible(false);
         return;
      }
      //Visible if file MIME type is "application/x-chromattic+groovy":
      boolean isVisible = MimeType.CHROMATTIC_DATA_OBJECT.equals(event.getFile().getContentType());
      //Enabled if file is saved:
      boolean isEnabled = (isVisible && !event.getFile().isNewFile());
      setVisible(isVisible);
      setEnabled(isEnabled);
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   @Override
   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }
}
