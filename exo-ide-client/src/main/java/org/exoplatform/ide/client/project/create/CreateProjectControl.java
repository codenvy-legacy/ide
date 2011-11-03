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
package org.exoplatform.ide.client.project.create;

import java.util.List;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
@RolesAllowed({"administrators", "developers"})
public class CreateProjectControl extends SimpleControl implements IDEControl, VfsChangedHandler
{

   public static final String ID = "Project/New/Empty Project...";

   private static final String TITLE = "Empty Project...";

   private static final String PROMPT = "Create Empty Project...";

   private List<Item> selectedItems;

   private VirtualFileSystemInfo vfsInfo;

   public CreateProjectControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setEvent(new CreateProjectEvent());
      setImages(IDEImageBundle.INSTANCE.newProject(), IDEImageBundle.INSTANCE.newProjectDisabled());
      setGroup(0);
   }

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
