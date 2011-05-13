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
package org.exoplatform.ide.client.versioning.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Version;

/**
 * Event occurs, when version is restored.
 * Implement {@link VersionRestoredHandler} to handle event.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 12, 2011 2:32:23 PM anya $
 *
 */
public class VersionRestoredEvent extends GwtEvent<VersionRestoredHandler>
{

   /**
    * Type used to register this event.
    */
   public static final GwtEvent.Type<VersionRestoredHandler> TYPE = new GwtEvent.Type<VersionRestoredHandler>();

   /**
    * Version, to file file was restored.
    */
   private Version version;

   /**
    * File with versions.
    */
   private File file;

   /**
    * @param version version, to file file was restored
    * @param file files
    */
   public VersionRestoredEvent(Version version, File file)
   {
      this.version = version;
      this.file = file;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<VersionRestoredHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(VersionRestoredHandler handler)
   {
      handler.onVersionRestored(this);
   }

   /**
    * @return the version
    */
   public Version getVersion()
   {
      return version;
   }

   /**
    * @return the file
    */
   public File getFile()
   {
      return file;
   }
}
