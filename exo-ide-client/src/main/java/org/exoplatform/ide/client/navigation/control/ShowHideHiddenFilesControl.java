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
package org.exoplatform.ide.client.navigation.control;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.navigation.event.ShowHideHiddenFilesEvent;
import org.exoplatform.ide.client.framework.navigation.event.ShowHideHiddenFilesHandler;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;

/**
 * Control for show or hide hidden files.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ShowHiddenFilesControl.java Mar 30, 2012 11:50:01 AM azatsarynnyy $
 *
 */
@RolesAllowed({"developer"})
public class ShowHideHiddenFilesControl extends SimpleControl implements IDEControl, ShowHideHiddenFilesHandler,
   ProjectOpenedHandler, ProjectClosedHandler
{

   /**
    * ID of this control.
    */
   public static final String ID = "View/Show \\ Hide Hidden Files";

   /**
    * Title of this control.
    */
   public static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.showHiddenFilesControlTitle();

   /**
    * State of hidden files visibility.
    */
   private boolean filesAreShown = false;

   private boolean isProjectOpened = false;

   /**
    * Default constructor.
    */
   public ShowHideHiddenFilesControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(TITLE);
      setImages(IDEImageBundle.INSTANCE.showHiddenFiles(), IDEImageBundle.INSTANCE.showHiddenFiles());
      setEvent(new ShowHideHiddenFilesEvent(true));
      setDelimiterBefore(true);
      setCanBeSelected(true);
      setVisible(true);
      setEnabled(true);
   }

   /**
    * Initializes control.
    * 
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(ShowHideHiddenFilesEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);

      updateState();
   }

   /**
    * Update title, prompt and event.
    */
   private void updateState()
   {
      if (!isProjectOpened)
      {
         setEnabled(false);
         return;
      }
      else
      {
         setEnabled(true);
      }

      if (filesAreShown)
      {
         setEvent(new ShowHideHiddenFilesEvent(false));
      }
      else
      {
         setEvent(new ShowHideHiddenFilesEvent(true));
      }

      setSelected(filesAreShown);
   }

   /**
    * @see org.exoplatform.ide.client.navigation.handler.ShowHideHiddenFilesHandler#onShowHideHiddenFiles(org.exoplatform.ide.client.navigation.event.ShowHideHiddenFilesEvent)
    */
   @Override
   public void onShowHideHiddenFiles(ShowHideHiddenFilesEvent event)
   {
      filesAreShown = event.isFilesShown();
      updateState();
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent)
    */
   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      isProjectOpened = false;
      updateState();
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
    */
   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      isProjectOpened = true;
      updateState();
   }

}
