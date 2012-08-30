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
package org.exoplatform.ide.extension.samples.client.githubimport;

import com.google.gwt.user.client.ui.CheckBox;

import com.google.gwt.user.client.ui.DockLayoutPanel;

import com.google.gwt.user.client.ui.FlowPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.PasswordTextInput;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.samples.client.SamplesExtension;
import org.exoplatform.ide.extension.samples.client.github.load.ProjectData;

/**
 * View for importing projects from GitHub.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ImportFromGithubView.java Dec 7, 2011 3:37:28 PM vereshchaka $
 */
public class ImportFromGithubView extends ViewImpl implements ImportFromGithubPresenter.Display
{
   private static final String ID = "ideImportFromGithubView";

   private static final String TITLE = SamplesExtension.LOCALIZATION_CONSTANT.importLoadDialogTitle();

   private static final int HEIGHT = 365;

   private static final int WIDTH = 500;

   private static final String LOGIN_RESULT_ID = "ideImportFromGithubViewLoginResult";

   private static final String LOGIN_FIELD_ID = "ideImportFromGithubViewLoginField";

   private static final String PASSWORD_FIELD_ID = "ideImportFromGithubViewPasswordField";

   private static final String READONLY_MODE_FIELD_ID = "ideImportFromGithubViewReadOnlyModeField";

   private static final String NAME_FIELD_ID = "ideImportFromGithubViewNameField";

   private static final String TYPE_FIELD_ID = "ideImportFromGithubViewTypeField";

   private static final String NEXT_BUTTON_ID = "ideImportFromGithubViewNextButton";

   private static final String BACK_BUTTON_ID = "ideImportFromGithubViewBackButton";

   private static final String CANCEL_BUTTON_ID = "ideImportFromGithubViewCancelButton";

   interface ImportFromGithubViewUiBinder extends UiBinder<Widget, ImportFromGithubView>
   {
   }

   private static ImportFromGithubViewUiBinder uiBinder = GWT.create(ImportFromGithubViewUiBinder.class);

   /**
    * Login field.
    */
   @UiField
   TextInput loginField;

   /**
    * Password field.
    */
   @UiField
   PasswordTextInput passwordField;

   /**
    * Login result label.
    */
   @UiField
   Label loginResult;

   /**
    * Select project's type field.
    */
   @UiField
   SelectItem projectTypeField;

   /**
    * Cancel button.
    */
   @UiField
   ImageButton cancelButton;

   /**
    * Next button.
    */
   @UiField
   ImageButton nextButton;

   /**
    * Back button.
    */
   @UiField
   ImageButton backButton;

   /**
    * GitHub repositories grid.
    */
   @UiField
   GitHubProjectsListGrid repositoriesGrid;

   /**
    * Project's name field.
    */
   @UiField
   TextInput projectNameField;

   /**
    * Login step panel.
    */
   @UiField
   FlowPanel loginStep;

   /**
    * Import step panel.
    */
   @UiField
   DockLayoutPanel importStep;

   /**
    * Read-only mode field.
    */
   @UiField
   CheckBox readOnlyModeField;

   public ImportFromGithubView()
   {
      super(ID, ViewType.POPUP, TITLE, null, WIDTH, HEIGHT, false);
      add(uiBinder.createAndBindUi(this));

      loginField.setName(LOGIN_FIELD_ID);
      passwordField.setName(PASSWORD_FIELD_ID);
      loginResult.setID(LOGIN_RESULT_ID);

      nextButton.setButtonId(NEXT_BUTTON_ID);
      backButton.setButtonId(BACK_BUTTON_ID);
      cancelButton.setButtonId(CANCEL_BUTTON_ID);

      projectNameField.setName(NAME_FIELD_ID);
      projectTypeField.setName(TYPE_FIELD_ID);
      readOnlyModeField.setName(READONLY_MODE_FIELD_ID);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#getProjectTypeField()
    */
   @Override
   public HasValue<String> getProjectTypeField()
   {
      return projectTypeField;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#setProjectTypeValues(java.lang.String[])
    */
   @Override
   public void setProjectTypeValues(String[] values)
   {
      projectTypeField.setValueMap(values);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#getNextButton()
    */
   @Override
   public HasClickHandlers getNextButton()
   {
      return nextButton;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#getRepositoriesGrid()
    */
   @Override
   public ListGridItem<ProjectData> getRepositoriesGrid()
   {
      return repositoriesGrid;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#getProjectNameField()
    */
   @Override
   public HasValue<String> getProjectNameField()
   {
      return projectNameField;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#setNextButtonEnabled(boolean)
    */
   @Override
   public void setNextButtonEnabled(boolean enabled)
   {
      nextButton.setEnabled(enabled);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#getLoginField()
    */
   @Override
   public HasValue<String> getLoginField()
   {
      return loginField;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#getPasswordField()
    */
   @Override
   public HasValue<String> getPasswordField()
   {
      return passwordField;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#getLoginResult()
    */
   @Override
   public HasValue<String> getLoginResult()
   {
      return loginResult;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#getBackButton()
    */
   @Override
   public HasClickHandlers getBackButton()
   {
      return backButton;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#focusInLoginField()
    */
   @Override
   public void focusInLoginField()
   {
      loginField.setFocus(true);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#showLoginStep(boolean)
    */
   @Override
   public void showLoginStep(boolean show)
   {
      loginStep.setVisible(show);
      backButton.setVisible(!show);
      importStep.setVisible(!show);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#showImportStep(boolean)
    */
   @Override
   public void showImportStep(boolean show)
   {
      importStep.setVisible(show);
      backButton.setVisible(show);
      loginStep.setVisible(!show);
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#getReadOnlyModeField()
    */
   @Override
   public HasValue<Boolean> getReadOnlyModeField()
   {
      return readOnlyModeField;
   }

   /**
    * @see org.exoplatform.ide.extension.samples.client.githubimport.ImportFromGithubPresenter.Display#setBackButtonVisible(boolean)
    */
   @Override
   public void setBackButtonVisible(boolean visible)
   {
      backButton.setVisible(visible);
   }
}
