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
package org.exoplatform.ide.client.statusbar;

import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.gwtframework.ui.client.command.StatusTextControl;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.NavigatorDisplay;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"administrators", "developers"})
public class NavigatorStatusControl extends StatusTextControl implements IDEControl, ItemsSelectedHandler,
   VfsChangedHandler, ProjectOpenedHandler, ProjectClosedHandler, ViewActivatedHandler, ViewOpenedHandler,
   ViewClosedHandler
{

   private class StatusMessage
   {

      private String html;

      public StatusMessage(Folder root, Item item)
      {
         ImageResource imageResource =
            item.getId().equals(root.getId()) ? IDEImageBundle.INSTANCE.workspace() : IDEImageBundle.INSTANCE.folder();
         String path = vfsInfo.getId() + ("/".equals(item.getPath()) ? "" : item.getPath());
         html = tuneMessage(getPathReadable(path), ImageHelper.getImageHTML(imageResource));
      }

      public StatusMessage(ProjectModel project, Item item)
      {
         String path = item.getPath();
         if (item instanceof FileModel)
         {
            path = path.substring(0, path.lastIndexOf("/"));
         }

         path = path.substring(project.getPath().length());

         ImageResource projectImage = ProjectResolver.getImageForProject(project.getProjectType());
         path = project.getName() + (path.isEmpty() ? "" : path);
         html = tuneMessage(getPathReadable(path), ImageHelper.getImageHTML(projectImage));
      }

      private String getPathReadable(String path)
      {
         if (path == null || path.isEmpty())
         {
            return "";
         }

         String[] parts = path.split("/");
         path = "";
         for (String part : parts)
         {
            path += (path.isEmpty() ? part : " / " + part);
         }

         return path;
      }

      /**
       * @param originalStatusMessage
       * @param icon
       * @return
       */
      private String tuneMessage(String originalStatusMessage, String iconHTML)
      {
         String table =
            "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"height:20px; border-collapse: collapse;\">"
               + "<tr>"
               + "<td style=\"width:3px; \"><img src=\""
               + Images.BLANK
               + "\" style=\"width:1px; height:1px;\"></td>"
               + "<td style=\"width:16px; height:20px; vertical-align:middle; \">"
               + "<div style=\"width:16px; height:16px; margin-top:2px;\">"
               + iconHTML
               + "</div>"
               + "</td>"
               + "<td style=\"width:3px;\"><img src=\""
               + Images.BLANK
               + "\" style=\"width:1px; height:1px;\"></td>"
               + "<td style=\"border: none; font-family:Verdana,Bitstream Vera Sans,sans-serif; font-size:11px; font-style:normal; line-height:20px; \"><nobr>"
               + "&nbsp;" + originalStatusMessage + "</nobr></td>" + "</tr>" + "</table>";
         return table;
      }

      public String getHtml()
      {
         return html;
      }

   }

   public static final String ID = "__navigator_status";

   private VirtualFileSystemInfo vfsInfo;

   private ProjectModel currentOpenedProject;

   private View currentActiveView;

   private List<String> openedViews = new ArrayList<String>();

   /**
    * 
    */
   public NavigatorStatusControl()
   {
      super(ID);
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);
      IDE.addHandler(ViewActivatedEvent.TYPE, this);

      IDE.addHandler(ViewOpenedEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);

      setEnabled(true);
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      vfsInfo = event.getVfsInfo();
   }

   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      currentOpenedProject = null;
   }

   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      currentOpenedProject = event.getProject();
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (!openedViews.contains(event.getView().getId()))
      {
         return;
      }

      if (vfsInfo == null || currentActiveView == null || event.getSelectedItems().size() == 0)
      {
         setText("");
         setVisible(false);
         return;
      }

      if (event.getView() instanceof ProjectExplorerDisplay)
      {
         setText(new StatusMessage(currentOpenedProject, event.getSelectedItems().get(0)).getHtml());
         setVisible(true);
         return;
      }

      if (event.getView() instanceof NavigatorDisplay)
      {
         setText(new StatusMessage(vfsInfo.getRoot(), event.getSelectedItems().get(0)).getHtml());
         setVisible(true);
         return;
      }

      setVisible(false);
   }

   @Override
   public void onViewActivated(ViewActivatedEvent event)
   {
      currentActiveView = event.getView();
   }

   @Override
   public void onViewOpened(ViewOpenedEvent event)
   {
      if (!openedViews.contains(event.getView().getId()))
      {
         openedViews.add(event.getView().getId());
      }
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (openedViews.contains(event.getView().getId()))
      {
         openedViews.remove(event.getView().getId());
      }
   }

}
