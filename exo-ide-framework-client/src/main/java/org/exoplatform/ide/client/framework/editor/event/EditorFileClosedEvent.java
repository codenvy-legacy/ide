/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.framework.editor.event;

import java.util.Map;

import org.exoplatform.ide.client.module.vfs.api.File;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class EditorFileClosedEvent extends GwtEvent<EditorFileClosedHandler>
{

   public static final GwtEvent.Type<EditorFileClosedHandler> TYPE = new GwtEvent.Type<EditorFileClosedHandler>();

   private File file;

   private Map<String, File> openedFiles;

   public EditorFileClosedEvent(File file, Map<String, File> openedFiles)
   {
      this.file = file;
      this.openedFiles = openedFiles;
   }

   public File getFile()
   {
      return file;
   }

   public Map<String, File> getOpenedFiles()
   {
      return openedFiles;
   }

   @Override
   protected void dispatch(EditorFileClosedHandler handler)
   {
      handler.onEditorFileClosed(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<EditorFileClosedHandler> getAssociatedType()
   {
      return TYPE;
   }

}
