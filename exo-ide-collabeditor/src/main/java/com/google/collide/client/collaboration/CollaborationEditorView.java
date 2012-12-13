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
package com.google.collide.client.collaboration;

import com.google.collide.client.CollabEditor;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.vfs.shared.File;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 
 */
public class CollaborationEditorView extends ViewImpl
{
   private CollabEditor editor;

   private DockLayoutPanel dockLayoutPanel;

   public CollaborationEditorView(CollabEditor editor, File file)
   {
      super("collaborationEditor","editor", getFileTitle(file));
      this.editor = editor;
      dockLayoutPanel = new DockLayoutPanel(Style.Unit.PX);
      add(dockLayoutPanel);
      dockLayoutPanel.add(editor);
   }

   private static String getFileTitle(File file)
   {
      return Utils.unescape(file.getName()) + " [Collaboration Mode]";
   }

   public CollabEditor getEditor()
   {
      return editor;
   }
}
