/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.event.file;

import org.exoplatform.ide.client.module.vfs.api.File;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class FileSavedEvent extends GwtEvent<FileSavedHandler>
{

   public static GwtEvent.Type<FileSavedHandler> TYPE = new GwtEvent.Type<FileSavedHandler>();

   private File file;

   private String sourceHref;

   public FileSavedEvent(File file, String sourceHref)
   {
      this.file = file;
      this.sourceHref = sourceHref;
   }

   public File getFile()
   {
      return file;
   }

   public String getSourceHref()
   {
      return sourceHref;
   }

   @Override
   protected void dispatch(FileSavedHandler handler)
   {
      handler.onFileSaved(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<FileSavedHandler> getAssociatedType()
   {
      return TYPE;
   }

}
