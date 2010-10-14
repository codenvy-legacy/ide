/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ide.client.framework.application.event;

import java.util.Map;

import org.exoplatform.ide.client.framework.module.vfs.api.File;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class InitializeApplicationEvent extends GwtEvent<InitializeApplicationHandler>
{

   public static final GwtEvent.Type<InitializeApplicationHandler> TYPE =
      new GwtEvent.Type<InitializeApplicationHandler>();

   private Map<String, File> openedFiles;

   private String activeFile;

   public InitializeApplicationEvent(Map<String, File> openedFiles, String activeFile)
   {
      this.openedFiles = openedFiles;
      this.activeFile = activeFile;
   }

   public Map<String, File> getOpenedFiles()
   {
      return openedFiles;
   }

   public String getActiveFile()
   {
      return activeFile;
   }

   @Override
   protected void dispatch(InitializeApplicationHandler handler)
   {
      handler.onInitializeApplication(this);
   }

   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<InitializeApplicationHandler> getAssociatedType()
   {
      return TYPE;
   }

}
