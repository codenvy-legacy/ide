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

import com.google.gwt.event.dom.client.HasClickHandlers;

import com.google.gwt.dom.client.Style.Unit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.IconButton;
import org.exoplatform.gwtframework.ui.client.component.Toolbar;
import org.exoplatform.gwtframework.ui.client.component.Toolbar.ToolbarItem;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Folder;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ProjectExplorerView extends ViewImpl implements
   org.exoplatform.ide.client.project.explorer.ProjectExplorerPresenter.Display
{

   public static final String ID = "ideProjectExplorerView";

   /**
    * Initial width of this view
    */
   private static final int WIDTH = 250;

   /**
    * Initial height of this view
    */
   private static final int HEIGHT = 450;

   private static final String TITLE = "Project Explorer";
   
   private static final String LINK_WITH_EDITOR = IDE.IDE_LOCALIZATION_CONSTANT.projectExplorerLinkWithEditor();

   private static ProjectExplorerViewUiBinder uiBinder = GWT.create(ProjectExplorerViewUiBinder.class);

   interface ProjectExplorerViewUiBinder extends UiBinder<Widget, ProjectExplorerView>
   {
   }

   /**
    * The CellTree.
    */
   @UiField(provided = true)
   ItemsCellTree tree;
   
   @UiField
   HTMLPanel projectNotOpenedPanel;
   
   private IconButton linkWithEditorButton;
   
   @UiField
   Toolbar toolbar;   
   
   public ProjectExplorerView()
   {
      super(ID, "navigation", TITLE, new Image(IDEImageBundle.INSTANCE.projectExplorer()), WIDTH, HEIGHT);
      tree = new ItemsCellTree();
      add(uiBinder.createAndBindUi(this));
      
      Image linkWithEditorNormal = new Image(IDEImageBundle.INSTANCE.linkWithEditor());
      Image linkWithEditorDisabled = new Image(IDEImageBundle.INSTANCE.linkWithEditorDisabled());
      
      linkWithEditorButton = new IconButton(linkWithEditorNormal, linkWithEditorDisabled);
      linkWithEditorButton.setTitle(LINK_WITH_EDITOR);
      ToolbarItem toolbarItem = toolbar.addItem(linkWithEditorButton, true);
      toolbarItem.getElement().getStyle().setPaddingTop(2, Unit.PX);
      toolbarItem.getElement().getStyle().setPaddingRight(2, Unit.PX);      
      
   }

   @Override
   public void setProject(ProjectModel project)
   {
      System.out.println("ProjectExplorerView.setProject() > " + project);
      tree.setVisible(project != null);
      projectNotOpenedPanel.setVisible(project == null);
      tree.setProject(project);
   }

   @Override
   public ItemTree itemTree()
   {
      return tree;
   }

   @Override
   public HasClickHandlers getLinkWithEditorButton()
   {
      return linkWithEditorButton;
   }

   @Override
   public void setLinkWithEditorButtonEnabled(boolean enabled)
   {
      linkWithEditorButton.setEnabled(enabled);
   }

   @Override
   public void setLinkWithEditorButtonSelected(boolean selected)
   {
      linkWithEditorButton.setSelected(selected);
   }

}
