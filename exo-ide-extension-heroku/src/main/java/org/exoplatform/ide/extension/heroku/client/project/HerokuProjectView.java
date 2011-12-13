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
package org.exoplatform.ide.extension.heroku.client.project;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.heroku.client.HerokuClientBundle;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;

/**
 * View for managing project, deployed on Heroku.
 * View must be pointed in <b>Views.gwt.xml</b>.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id:  Dec 8, 2011 10:30:18 AM anya $
 *
 */
public class HerokuProjectView extends ViewImpl implements HerokuProjectPresenter.Display
{
   private static final int WIDTH = 580;

   private static final int HEIGHT = 245;

   private static final String ID = "ideHerokuProjectView";

   private static final String DELETE_BUTTON_ID = "ideHerokuProjectViewDeleteButton";

   private static final String RENAME_BUTTON_ID = "ideHerokuProjectViewRenameButton";

   private static final String INFO_BUTTON_ID = "ideHerokuProjectViewInfoButton";

   private static final String CLOSE_BUTTON_ID = "ideHerokuProjectViewCloseButton";

   private static final String LOGS_BUTTON_ID = "ideHerokuProjectViewLogsButton";

   private static final String RAKE_BUTTON_ID = "ideHerokuProjectViewRakeButton";

   private static final String EDIT_STACK_BUTTON_ID = "ideHerokuProjectViewEditStackButton";

   private static final String NAME_FIELD_ID = "ideHerokuProjectViewNameField";

   private static final String URL_FIELD_ID = "ideHerokuProjectViewUrlField";

   private static final String STACK_FIELD_ID = "ideHerokuProjectViewSatckField";

   private static HerokuProjectViewUiBinder uiBinder = GWT.create(HerokuProjectViewUiBinder.class);

   interface HerokuProjectViewUiBinder extends UiBinder<Widget, HerokuProjectView>
   {
   }

   @UiField
   Button deleteButton;

   @UiField
   Button rakeButton;

   @UiField
   Button logsButton;

   @UiField
   ImageButton renameButton;

   @UiField
   ImageButton infoButton;

   @UiField
   ImageButton closeButton;

   @UiField
   ImageButton editStackButton;

   @UiField
   TextInput nameField;

   @UiField
   Anchor urlField;

   @UiField
   TextInput stackField;

   public HerokuProjectView()
   {
      super(ID, ViewType.MODAL, HerokuExtension.LOCALIZATION_CONSTANT.manageProjectViewTitle(), new Image(
         HerokuClientBundle.INSTANCE.heroku()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
      
      deleteButton.getElement().setId(DELETE_BUTTON_ID);
      renameButton.getElement().setId(RENAME_BUTTON_ID);
      closeButton.setButtonId(CLOSE_BUTTON_ID);
      infoButton.setButtonId(INFO_BUTTON_ID);
      logsButton.getElement().setId(LOGS_BUTTON_ID);
      rakeButton.getElement().setId(RAKE_BUTTON_ID);
      editStackButton.setButtonId(EDIT_STACK_BUTTON_ID);

      nameField.setName(NAME_FIELD_ID);
      urlField.setName(URL_FIELD_ID);
      stackField.setName(STACK_FIELD_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.project.HerokuProjectPresenter.Display#getCloseButton()
    */
   @Override
   public HasClickHandlers getCloseButton()
   {
      return closeButton;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.project.HerokuProjectPresenter.Display#getRenameButton()
    */
   @Override
   public HasClickHandlers getRenameButton()
   {
      return renameButton;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.project.HerokuProjectPresenter.Display#getDeleteButton()
    */
   @Override
   public HasClickHandlers getDeleteButton()
   {
      return deleteButton;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.project.HerokuProjectPresenter.Display#getInfoButton()
    */
   @Override
   public HasClickHandlers getInfoButton()
   {
      return infoButton;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.project.HerokuProjectPresenter.Display#getRakeButton()
    */
   @Override
   public HasClickHandlers getRakeButton()
   {
      return rakeButton;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.project.HerokuProjectPresenter.Display#getLogsButton()
    */
   @Override
   public HasClickHandlers getLogsButton()
   {
      return logsButton;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.project.HerokuProjectPresenter.Display#getEditStackButton()
    */
   @Override
   public HasClickHandlers getEditStackButton()
   {
      return editStackButton;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.project.HerokuProjectPresenter.Display#getApplicationName()
    */
   @Override
   public HasValue<String> getApplicationName()
   {
      return nameField;
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.project.HerokuProjectPresenter.Display#setApplicationURL(java.lang.String)
    */
   @Override
   public void setApplicationURL(String URL)
   {
      urlField.setHref(URL);
      urlField.setText(URL);
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.project.HerokuProjectPresenter.Display#getApplicationStack()
    */
   @Override
   public HasValue<String> getApplicationStack()
   {
      return stackField;
   }
}
