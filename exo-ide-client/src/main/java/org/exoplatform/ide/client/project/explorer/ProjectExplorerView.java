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

package org.exoplatform.ide.client.project.explorer;

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent.HasSelectionChangedHandlers;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ProjectExplorerView extends ViewImpl implements org.exoplatform.ide.client.project.explorer.ProjectExplorerPresenter.Display
{

   private static ProjectExplorerViewUiBinder uiBinder = GWT.create(ProjectExplorerViewUiBinder.class);

   interface ProjectExplorerViewUiBinder extends UiBinder<Widget, ProjectExplorerView>
   {
   }

   public static final String ID = "projectExplorerView";

   private static final String TITLE = "Project Explorer";
   
   @UiField
   ScrollPanel scrollPanel;

   public ProjectExplorerView()
   {
      super(ID, "information", TITLE, new Image(IDEImageBundle.INSTANCE.projectExplorer()));
      add(uiBinder.createAndBindUi(this));
   }

   @Override
   public HasSelectionChangedHandlers getSelectionModel()
   {
      return null;
   }

   @Override
   public void initialize(ProjectModel project)
   {
      ItemsCellTree cellTree = new ItemsCellTree(project);
      scrollPanel.add(cellTree);
   }

}
