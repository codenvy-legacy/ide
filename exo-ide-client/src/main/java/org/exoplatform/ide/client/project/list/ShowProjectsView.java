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

package org.exoplatform.ide.client.project.list;

import java.util.List;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ShowProjectsView extends ViewImpl implements org.exoplatform.ide.client.project.list.ShowProjectsPresenter.Display
{

   private static ShowProjectsViewUiBinder uiBinder = GWT.create(ShowProjectsViewUiBinder.class);

   interface ShowProjectsViewUiBinder extends UiBinder<Widget, ShowProjectsView>
   {
   }
   
   public static final String ID = "ideShowProjectsView";
   
   public static final String TITLE = "Projects";
   
   /**
    * Initial width of this view
    */
   private static int WIDTH = 500;

   /**
    * Initial height of this view
    */
   private static int HEIGHT = 280;
   
   @UiField
   ProjectsListGrid projectsListGrid;
   
   @UiField
   ImageButton openButton;
   
   @UiField
   ImageButton cancelButton;

   public ShowProjectsView()
   {
      super(ID, "modal", TITLE, new Image(IDEImageBundle.INSTANCE.projectExplorer()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
   }

   @Override
   public ListGridItem<ProjectModel> getProjectsListGrid()
   {
      return projectsListGrid;
   }

   @Override
   public HasClickHandlers getOpenButton()
   {
      return openButton;
   }

   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   @Override
   public void setOpenButtonEnabled(boolean enabled)
   {
      openButton.setEnabled(enabled);
   }

   @Override
   public List<ProjectModel> getSelectedItems()
   {
      return projectsListGrid.getSelectedItems();
   }

}
