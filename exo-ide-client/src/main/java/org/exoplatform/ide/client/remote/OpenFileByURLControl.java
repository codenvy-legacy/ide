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
package org.exoplatform.ide.client.remote;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Control for opening file by URL.
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@RolesAllowed({"administrators", "developers"})
public class OpenFileByURLControl extends SimpleControl implements IDEControl, VfsChangedHandler
{

   private VirtualFileSystemInfo vfsInfo;

   /**
    * Creates a new instance of this control.
    */
   public OpenFileByURLControl()
   {
      super(IDE.IDE_LOCALIZATION_CONSTANT.openFileByURLControlId());
      setTitle(IDE.IDE_LOCALIZATION_CONSTANT.openFileByURLControlTitle());
      setPrompt(IDE.IDE_LOCALIZATION_CONSTANT.openFileByURLControlPrompt());
      setImages(IDEImageBundle.INSTANCE.url(), IDEImageBundle.INSTANCE.urlDisabled());
      setEvent(new OpenFileByURLEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   @Override
   public void initialize(HandlerManager eventBus)
   {
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      update();
   }

   private void update()
   {
      if (vfsInfo == null)
      {
         setVisible(false);
         setEnabled(false);
         return;
      }

      setVisible(true);
      setEnabled(true);
   }

   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      vfsInfo = event.getVfsInfo();
      update();
   }

}
