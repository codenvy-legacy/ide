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
package org.exoplatform.ide.client.project.explorer;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.OpenProjectEvent;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Aug 20, 2012 3:46:43 PM anya $
 * 
 */
public class OpenProjectControl extends SimpleControl implements IDEControl, ProjectSelectedHandler
{
   public static final String ID = "Project/Open.";

   private static final String TITLE = "Open";

   private static final String PROMPT = "Open Project";

   private ProjectModel selectedProject;

   public OpenProjectControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(IDEImageBundle.INSTANCE.projectOpened(), IDEImageBundle.INSTANCE.projectOpenedDisabled());
      setShowInMenu(false);
   }

   /**
    * @see org.exoplatform.ide.client.project.explorer.ProjectSelectedHandler#onProjectSelected(org.exoplatform.ide.client.project.explorer.ProjectSelectedEvent)
    */
   @Override
   public void onProjectSelected(ProjectSelectedEvent event)
   {
      this.selectedProject = event.getProject();
      boolean selected = (selectedProject != null);
      setShowInContextMenu(selected);
      setEnabled(selected);
      setVisible(selected);
      setEvent(new OpenProjectEvent(selectedProject));
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(ProjectSelectedEvent.TYPE, this);
   }
}
