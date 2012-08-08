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
package org.exoplatform.ide.extension.samples.client.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.samples.client.GetCollboratorsEvent;
import org.exoplatform.ide.extension.samples.client.SamplesClientBundle;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.extension.samples.client.startpage.OpenStartPageEvent;
import org.exoplatform.ide.extension.samples.client.startpage.WelcomePageOpenedEvent;
import org.exoplatform.ide.extension.samples.client.startpage.WelcomePageOpenedHandler;
import org.exoplatform.ide.extension.samples.client.startpage.StartPagePresenter.Display;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

/**
 * Control to show welcome page.
 * <p/>
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ImportFromGithubControl.java Nov 18, 2011 5:06:02 PM vereshchaka $
 */
@RolesAllowed({"administrators", "developers"})
public class ColaboratorsControl extends SimpleControl implements IDEControl, VfsChangedHandler, ViewClosedHandler,
ProjectOpenedHandler, ProjectClosedHandler
   
{

   private static final String ID = "Help/Collaborators";

   private static final String TITLE = "Collaborators";

   private static final String PROMPT = "Collaborators";

   private VirtualFileSystemInfo vfsInfo;

   private boolean opened;

   /**
    * @param id
    */
   public ColaboratorsControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(SamplesClientBundle.INSTANCE.welcome(), SamplesClientBundle.INSTANCE.welcomeDisabled());
      setEvent(new GetCollboratorsEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      setVisible(true);

      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);

      updateEnabling();
   }

   private void updateEnabling()
   {
      if (vfsInfo == null)
      {
         setEnabled(false);
         return;
      }
      setEnabled(!opened);
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      vfsInfo = event.getVfsInfo();
      updateEnabling();
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         opened = false;
         updateEnabling();
      }
   }

   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      
      setEnabled(false);
   }

   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      setEnabled(true);
      
   }

}
