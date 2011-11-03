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
package org.exoplatform.ide.extension.groovy.client.controls;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.event.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.groovy.client.Images;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Control for calling the dialog for configuring classpath file.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jan 6, 2011 $
 *
 */
public class ConfigureBuildPathCommand extends SimpleControl implements IDEControl, VfsChangedHandler
{

   private static final String ID = "File/Configure Classpath...";

   private final String TITLE = "Configure Classpath...";

   private final String PROMPT = "Configure Groovy Classpath...";

   private VirtualFileSystemInfo vfsInfo;

   public ConfigureBuildPathCommand()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setIcon(Images.Controls.CONFIGURE_BUILD_PATH);
      setEvent(new ProjectCreatedEvent());
      setDelimiterBefore(true);
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
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
