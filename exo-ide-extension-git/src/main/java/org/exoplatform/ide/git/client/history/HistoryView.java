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
package org.exoplatform.ide.git.client.history;

import com.google.gwt.user.client.ui.Image;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.button.IconButton;
import org.exoplatform.gwtframework.ui.client.component.TextAreaItem;
import org.exoplatform.gwtframework.ui.client.toolbar.Toolbar;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.git.client.GitClientBundle;
import org.exoplatform.ide.git.client.commit.RevisionGrid;
import org.exoplatform.ide.git.shared.Revision;

/**
 * View for displaying the history of commits and it's diff.
 * Must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 29, 2011 3:00:37 PM anya $
 *
 */
public class HistoryView extends ViewImpl implements HistoryPresenter.Display
{
   public static final String ID = "ideHistoryView";

   public static final String TYPE = "information";

   public static final String TITLE = "History";

   public static final String PROJECT_CHANGES_BUTTON_TITLE = "Show changes in project";

   public static final String RESOURCE_CHANGES_BUTTON_TITLE = "Show changes of selected resource";

   /**
    * Grid for displaying revisions.
    */
   @UiField
   RevisionGrid revisionGrid;

   /**
    * View's toolbar.
    */
   @UiField
   Toolbar toolbar;

   /**
    * Field for displaying diff text.
    */
   @UiField
   TextAreaItem diffTextField;

   /**
    * Refresh button.
    */
   private IconButton refreshButton;

   /**
    * Show changes in whole project button.
    */
   private IconButton projectChangesButton;

   /**
    * Show changes of the resource button.
    */
   private IconButton resourceChangesButton;

   /**
    * UI binder for this view.
    */
   private static HistoryViewUiBinder uiBinder = GWT.create(HistoryViewUiBinder.class);

   interface HistoryViewUiBinder extends UiBinder<Widget, HistoryView>
   {
   }

   public HistoryView()
   {
      super(ID, TYPE, TITLE, new Image(GitClientBundle.INSTANCE.history()), 400, 250, true);
      add(uiBinder.createAndBindUi(this));

      refreshButton =
         new IconButton(ImageHelper.getImageHTML(GitClientBundle.INSTANCE.refresh()),
            ImageHelper.getImageHTML(GitClientBundle.INSTANCE.refreshDisabled()));
      toolbar.addItem(refreshButton, true);

      projectChangesButton =
         new IconButton(ImageHelper.getImageHTML(GitClientBundle.INSTANCE.projectLevel()),
            ImageHelper.getImageHTML(GitClientBundle.INSTANCE.projectLevelDisabled()));
      projectChangesButton.setTitle(PROJECT_CHANGES_BUTTON_TITLE);
      toolbar.addItem(projectChangesButton, true);

      resourceChangesButton =
         new IconButton(ImageHelper.getImageHTML(GitClientBundle.INSTANCE.resourceLevel()),
            ImageHelper.getImageHTML(GitClientBundle.INSTANCE.resourceLevelDisabled()));
      resourceChangesButton.setTitle(RESOURCE_CHANGES_BUTTON_TITLE);
      toolbar.addItem(resourceChangesButton, true);
   }

   /**
    * @see org.exoplatform.ide.git.client.history.HistoryPresenter.Display#getRevisionGrid()
    */
   @Override
   public ListGridItem<Revision> getRevisionGrid()
   {
      return revisionGrid;
   }

   /**
    * @see org.exoplatform.ide.git.client.history.HistoryPresenter.Display#getDiffText()
    */
   @Override
   public HasValue<String> getDiffText()
   {
      return diffTextField;
   }

   /**
    * @see org.exoplatform.ide.git.client.history.HistoryPresenter.Display#getRefreshButton()
    */
   @Override
   public HasClickHandlers getRefreshButton()
   {
      return refreshButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.history.HistoryPresenter.Display#getProjectChangesButton()
    */
   @Override
   public HasClickHandlers getProjectChangesButton()
   {
      return projectChangesButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.history.HistoryPresenter.Display#getResourceChangesButton()
    */
   @Override
   public HasClickHandlers getResourceChangesButton()
   {
      return resourceChangesButton;
   }

   /**
    * @see org.exoplatform.ide.git.client.history.HistoryPresenter.Display#selectProjectChangesButton(boolean)
    */
   @Override
   public void selectProjectChangesButton(boolean selected)
   {
      projectChangesButton.setSelected(selected);
   }

   /**
    * @see org.exoplatform.ide.git.client.history.HistoryPresenter.Display#selectResourceChangesButton(boolean)
    */
   @Override
   public void selectResourceChangesButton(boolean selected)
   {
      resourceChangesButton.setSelected(selected);
   }
}
