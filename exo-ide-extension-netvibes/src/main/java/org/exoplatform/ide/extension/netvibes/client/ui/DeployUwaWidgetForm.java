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
package org.exoplatform.ide.extension.netvibes.client.ui;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.PasswordField;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.component.TitleOrientation;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.extension.netvibes.client.Images;

import java.util.LinkedHashMap;

/**
 * View of the deploy UWA widget operation.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 29, 2010 $
 *
 */
public class DeployUwaWidgetForm extends DialogWindow implements DeployUwaWidgetPresenter.Display
{

   public static final int WIDTH = 460;

   public static final int HEIGHT = 170;

   private final int BUTTON_WIDTH = 90;

   private final int BUTTON_HEIGHT = 22;

   private static final String ID = "ideDeployUwaWidgetForm";

   private final String TITLE = "Deploy UWA Widget to Netvibes Ecosystem";

   //IDs for Selenium tests:

   private final String ID_DEPLOY_WIDGET_BUTTON = "ideDeployUwaWidgetFormDeployWidgetButton";

   private final String ID_CANCEL_BUTTON = "ideDeployUwaWidgetFormCancelButton";

   private final String ID_NEXT_STEP_BUTTON = "ideDeployUwaWidgetFormNextStepButton";

   private final String ID_PREV_STEP_BUTTON = "ideDeployUwaWidgetFormPrevStepButton";

   private final String ID_URL_FIELD = "ideDeployUwaWidgetFormUrlField";

   private final String ID_MAIN_DYNAMIC_FORM = "ideDeployUwaWidgetFormMainDynamicForm";

   private final String ID_DETAILS_DYNAMIC_FORM = "ideDeployUwaWidgetFormDetailsDynamicForm";

   private final String ID_TITLE_FIELD = "ideDeployUwaWidgetFormTitleField";

   private final String ID_DESCRIPTION_FIELD = "ideDeployUwaWidgetFormDescriptionField";

   private final String ID_VERSION_FIELD = "ideDeployUwaWidgetFormVersionField";

   private final String ID_KEYWORDS_FIELD = "ideDeployUwaWidgetFormKeywordsField";

   private final String ID_TUMBNAIL_FIELD = "ideDeployUwaWidgetFormTumbnailField";

   private final String ID_LANGUAGE_FIELD = "ideDeployUwaWidgetFormLanguageField";

   private final String ID_CATEGORY_FIELD = "ideDeployUwaWidgetFormCategoryField";

   private final String ID_REGION_FIELD = "ideDeployUwaWidgetFormRegionField";

   private final String ID_PRIVACY_DYNAMIC_FORM = "ideDeployUwaWidgetFormPrivacyDynamicForm";

   private final String ID_API_KEY_FIELD = "ideDeployUwaWidgetFormApiKeyField";

   private final String ID_SECRET_KEY_FIELD = "ideDeployUwaWidgetFormSecretField";

   private final String ID_LOGIN_FIELD = "ideDeployUwaWidgetFormLoginField";

   private final String ID_PASSWORD_FIELD = "ideDeployUwaWidgetFormPasswordField";

   //Buttons:

   private ImageButton deployWidgetButton;

   private ImageButton cancelButton;

   private ImageButton nextStepButton;

   private ImageButton prevStepButton;

   //Main :

   private TextField urlField;

   // Details :

   private VerticalPanel detailsDynamicForm;

   private TextField titleField;

   private TextField descriptionField;

   private TextField versionField;

   private TextField keywordsField;

   private TextField thumbnailField;

   private ComboBoxField languageField;

   private ComboBoxField categoryField;

   private ComboBoxField regionField;

   //Private information

   private PasswordField secretKeyField;

   private PasswordField apiKeyField;

   private TextField loginField;

   private PasswordField passwordField;

   //Layouts

   private VerticalPanel mainInfoLayout;

   private VerticalPanel detailsLayout;

   private VerticalPanel privacyLayout;

   private LinkedHashMap<String, String> categories = new LinkedHashMap<String, String>();

   /**
    * @param eventBus 
    */
   public DeployUwaWidgetForm(HandlerManager eventBus)
   {
      super(WIDTH, HEIGHT, ID);
      //TODO setCanDragResize(true);

      setTitle(TITLE);

      //Main layout of the dialog window:
      VerticalPanel mainLayout = new VerticalPanel();
      mainLayout.setWidth("100%");
      mainLayout.setHeight("100%");
      mainLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      mainLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

      //Create layout for main information
      mainInfoLayout = createMainInfoLayout();
      mainLayout.add(mainInfoLayout);

      //Create layout for detailed information
      detailsLayout = createDetailsLayout();
      mainLayout.add(detailsLayout);

      //Create layout for private information
      privacyLayout = createPrivacyLayout();
      mainLayout.add(privacyLayout);

      //Create and layout with buttons
      mainLayout.add(createButtonsLayout());

      setWidget(mainLayout);
      show();
   }

   protected Label createTitle(String title)
   {
      Label label = new Label();
      label.getElement().setInnerHTML("<b>" + title + "</b>");
      return label;
   }

   /**
    * Create layout to get main information about widget.
    * 
    * @return {@link VerticalPanel}
    */
   private VerticalPanel createMainInfoLayout()
   {
      VerticalPanel form = new VerticalPanel();
      form.setWidth("100%");
      form.setHeight("100%");
      form.setSpacing(5);
      form.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
      form.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      form.getElement().setId(ID_MAIN_DYNAMIC_FORM);
      form.add(createTitle("Step 1. Widget Content"));

      urlField = createTextField(ID_URL_FIELD, "Public widget URL", "http://", 390);
      form.add(urlField);

      form.setVisible(false);
      return form;
   }

   /**
    * Create layout to private information about widget's author.
    * 
    * @return {@link VerticalPanel}
    */
   private VerticalPanel createPrivacyLayout()
   {
      VerticalPanel form = new VerticalPanel();
      form.setWidth("100%");
      form.setHeight("100%");
      form.setSpacing(3);
      form.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      form.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

      form.getElement().setId(ID_PRIVACY_DYNAMIC_FORM);
      form.add(createTitle("Step 3. Private Information"));

      apiKeyField = createPasswordField(ID_API_KEY_FIELD, "Enter API key", 390);
      secretKeyField = createPasswordField(ID_SECRET_KEY_FIELD, "Enter secrete key", 390);
      loginField = createTextField(ID_LOGIN_FIELD, "Login", "", 195);
      passwordField = createPasswordField(ID_PASSWORD_FIELD, "Password", 195);

      HorizontalPanel hPanel = new HorizontalPanel();
      hPanel.setWidth("390px");
      hPanel.setSpacing(1);
      hPanel.add(loginField);
      hPanel.add(passwordField);

      form.add(hPanel);
      form.add(apiKeyField);
      form.add(secretKeyField);

      form.setVisible(false);
      return form;
   }

   /**
    * Create layout to get detailed information about widget.
    * 
    * @return {@link VerticalPanel}
    */
   private VerticalPanel createDetailsLayout()
   {
      detailsDynamicForm = new VerticalPanel();
      detailsDynamicForm.setWidth("100%");
      detailsDynamicForm.setHeight("100%");
      detailsDynamicForm.setSpacing(3);
      detailsDynamicForm.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

      detailsDynamicForm.getElement().setId(ID_DETAILS_DYNAMIC_FORM);
      detailsDynamicForm.add(createTitle("Step 2. Detailed Information"));

      titleField = createTextField(ID_TITLE_FIELD, "Title&#42;", "", 300);
      descriptionField = createTextField(ID_DESCRIPTION_FIELD, "Description&#42;", "", 300);
      descriptionField.setHeight(50);
      versionField = createTextField(ID_VERSION_FIELD, "Widget version", "", 150);
      keywordsField = createTextField(ID_KEYWORDS_FIELD, "Descriptive keywords (max 6, space separated)", "", 300);
      thumbnailField = createTextField(ID_TUMBNAIL_FIELD, "Thumbnail URL", "", 300);
      languageField = createCombobox(ID_LANGUAGE_FIELD, "Main language&#42;", 150);
      categoryField = createCombobox(ID_CATEGORY_FIELD, "Most appropriate category&#42;", 150);
      regionField = createCombobox(ID_REGION_FIELD, "Most appropriate region&#42;", 150);

      detailsDynamicForm.add(titleField);
      detailsDynamicForm.add(descriptionField);
      HorizontalPanel hPanel1 = new HorizontalPanel();
      hPanel1.setWidth("300px");
      hPanel1.setSpacing(1);
      hPanel1.add(versionField);
      hPanel1.add(languageField);

      detailsDynamicForm.add(hPanel1);
      detailsDynamicForm.add(keywordsField);
      detailsDynamicForm.add(thumbnailField);

      HorizontalPanel hPanel2 = new HorizontalPanel();
      hPanel2.setWidth("300px");
      hPanel2.setSpacing(1);
      hPanel2.add(regionField);
      hPanel2.add(categoryField);

      detailsDynamicForm.add(hPanel2);
      detailsDynamicForm.setVisible(false);
      return detailsDynamicForm;
   }

   /**
    * Create centered layout with buttons.
    * 
    * @return {@link HorizontalPanel}
    */
   private HorizontalPanel createButtonsLayout()
   {
      HorizontalPanel hLayout = new HorizontalPanel();
      hLayout.setSpacing(10);
      hLayout.setHeight(BUTTON_HEIGHT + "px");
      hLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

      deployWidgetButton = createButton(ID_DEPLOY_WIDGET_BUTTON, "Deploy", Images.Buttons.OK);
      cancelButton = createButton(ID_CANCEL_BUTTON, "Cancel", Images.Buttons.CANCEL);
      nextStepButton = createButton(ID_NEXT_STEP_BUTTON, "Next", Images.Buttons.NEXT_STEP);
      prevStepButton = createButton(ID_PREV_STEP_BUTTON, "Back", Images.Buttons.PREV_STEP);

      hLayout.add(prevStepButton);
      hLayout.add(deployWidgetButton);
      hLayout.add(nextStepButton);
      hLayout.add(cancelButton);
      return hLayout;
   }

   /**
    * Created button.
    * 
    * @param id button's id
    * @param title button's display title
    * @param icon button's icon
    * @return {@link ImageButton} created button
    */
   private ImageButton createButton(String id, String title, String icon)
   {
      ImageButton button = new ImageButton(title);
      button.setButtonId(id);
      button.setWidth(BUTTON_WIDTH + "px");
      button.setHeight(BUTTON_HEIGHT + "px");
      button.setImage(new Image(icon));
      return button;
   }

   /**
    * Creates form text field item.
    * 
    * @param id id for the form item
    * @param title title to display
    * @param value default value
    * @param width width
    * @return {@link TextField} created text field
    */
   private TextField createTextField(String id, String title, String value, int width)
   {
      TextField textField = new TextField(id, title);
      textField.setWidth(width);
      textField.setHeight(22);
      textField.setTitleOrientation(TitleOrientation.TOP);
      textField.setValue(value);
      return textField;
   }

   /**
    * Creates form password field item.
    * 
    * @param id id of the item
    * @param title display title of the item
    * @param width item's width
    * @return {@link PasswordField} created password field
    */
   private PasswordField createPasswordField(String id, String title, int width)
   {
      PasswordField passwordField = new PasswordField(id, title);
      passwordField.setWidth(width);
      passwordField.setHeight(22);
      passwordField.setTitleOrientation(TitleOrientation.TOP);
      return passwordField;
   }

   /**
    * Creates form combobox field item.
    * 
    * @param id id of the item
    * @param title display title of the item
    * @param width item's width
    * @return {@link ComboBoxField} created combobox field
    */
   private ComboBoxField createCombobox(String id, String title, int width)
   {
      ComboBoxField combobox = new ComboBoxField();
      combobox.setName(id);
      combobox.setTitle(title);
      combobox.setTitleOrientation(TitleOrientation.TOP);
      combobox.setWidth(width);
      return combobox;
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#closeForm()
    */
   public void closeForm()
   {
      destroy();
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#getDeployButton()
    */
   public HasClickHandlers getDeployButton()
   {
      return deployWidgetButton;
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#getCancelButton()
    */
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#getSecretKey()
    */
   public TextFieldItem getSecretKey()
   {
      return secretKeyField;
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#getApiKey()
    */
   public TextFieldItem getApiKey()
   {
      return apiKeyField;
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#getUrlValue()
    */
   public TextFieldItem getUrlValue()
   {
      return urlField;
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#displayMainInfo(boolean)
    */
   public void displayMainInfo(boolean isShow)
   {
      if (isShow)
      {
         setWidth(460);
         setHeight(180);
         center();
      }
      mainInfoLayout.setVisible(isShow);
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#displayDetailsInfo(boolean)
    */
   public void displayDetailsInfo(boolean isShow)
   {
      if (isShow)
      {
         setHeight(410);
         setWidth(375);
      }
      detailsLayout.setVisible(isShow);
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#displayPrivacyInfo(boolean)
    */
   public void displayPrivacyInfo(boolean isShow)
   {
      if (isShow)
      {
         setWidth(460);
         setHeight(260);
      }
      privacyLayout.setVisible(isShow);
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#updateNextButtonState(boolean, boolean)
    */
   public void updateNextButtonState(boolean isVisible, boolean isEnabled)
   {
      nextStepButton.setVisible(isVisible);
      nextStepButton.setEnabled(isEnabled);
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#updatePrevButtonState(boolean, boolean)
    */
   public void updatePrevButtonState(boolean isVisible, boolean isEnabled)
   {
      prevStepButton.setVisible(isVisible);
      prevStepButton.setEnabled(isEnabled);
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#updateDeployButtonState(boolean, boolean)
    */
   public void updateDeployButtonState(boolean isVisible, boolean isEnabled)
   {
      deployWidgetButton.setVisible(isVisible);
      deployWidgetButton.setEnabled(isEnabled);
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#getNextStepButton()
    */
   public HasClickHandlers getNextStepButton()
   {
      return nextStepButton;
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#getPrevStepButton()
    */
   public HasClickHandlers getPrevStepButton()
   {
      return prevStepButton;
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#getDescription()
    */
   public TextFieldItem getDescription()
   {
      return descriptionField;
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#getVersion()
    */
   public TextFieldItem getVersion()
   {
      return versionField;
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#getKeywords()
    */
   public TextFieldItem getKeywords()
   {
      return keywordsField;
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#getThumbnail()
    */
   public TextFieldItem getThumbnail()
   {
      return thumbnailField;
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#getLanguage()
    */
   public HasValue<String> getLanguage()
   {
      return languageField;
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#getCategory()
    */
   public HasValue<String> getCategory()
   {
      return categoryField;
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#getRegion()
    */
   public HasValue<String> getRegion()
   {
      return regionField;
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#getWigdetTitle()
    */
   public TextFieldItem getWigdetTitle()
   {
      return titleField;
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#getLogin()
    */
   public TextFieldItem getLogin()
   {
      return loginField;
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#getPassword()
    */
   public TextFieldItem getPassword()
   {
      return passwordField;
   }

   /**
    * @see org.exoplatform.ide.extension.netvibes.client.ui.DeployUwaWidgetPresenter.Display#setLanguageValues(java.lang.String[])
    */
   @Override
   public void setLanguageValues(String[] values)
   {
      languageField.setValueMap(values);
   }

   /**
    * @see org.exoplatform.ide.extension.netvibes.client.ui.DeployUwaWidgetPresenter.Display#setCategoryValues(java.lang.String[])
    */
   @Override
   public void setCategoryValues(String[] values)
   {
      categoryField.setValueMap(values);
   }

   /**
    * @see org.exoplatform.ide.extension.netvibes.client.ui.DeployUwaWidgetPresenter.Display#setRegionValues(java.lang.String[])
    */
   @Override
   public void setRegionValues(String[] values)
   {
      regionField.setValueMap(values);
   }
}
