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
package org.eclipse.jdt.client;

import org.eclipse.jdt.client.event.CleanProjectEvent;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.util.ProjectResolver;

import java.util.HashSet;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 9:57:22 AM Mar 5, 2012 evgen $
 * 
 */
public class CleanProjectControl extends SimpleControl implements IDEControl, ProjectOpenedHandler,
   ProjectClosedHandler, EditorActiveFileChangedHandler
{

   private boolean isJavaProject = false;

   private boolean isJavaFile = false;

   private HashSet<String> supportedProjectType = new HashSet<String>();

   /**
    * 
    */
   public CleanProjectControl()
   {
      super("Project/Clean");
      setTitle("Clean...");
      setPrompt("Clean Project");
      setImages(JdtClientBundle.INSTANCE.clean(), JdtClientBundle.INSTANCE.cleanDisabled());
      setEvent(new CleanProjectEvent());
      supportedProjectType.add(ProjectResolver.SERVLET_JSP);
      supportedProjectType.add(ProjectResolver.SPRING);
      supportedProjectType.add(ProjectResolver.APP_ENGINE_JAVA);
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      setVisible(false);
      setEnabled(false);
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent)
    */
   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      isJavaProject = false;
      updateEnabling();
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
    */
   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      isJavaProject = supportedProjectType.contains(event.getProject().getProjectType());
      updateEnabling();
   }

   /**
    * 
    */
   private void updateEnabling()
   {
      setVisible(isJavaProject);
      setEnabled(isJavaFile);
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() != null)
      {
         isJavaFile = event.getFile().getMimeType().equals(MimeType.APPLICATION_JAVA);
      }
      else
         isJavaFile = false;
      updateEnabling();
   }

}
