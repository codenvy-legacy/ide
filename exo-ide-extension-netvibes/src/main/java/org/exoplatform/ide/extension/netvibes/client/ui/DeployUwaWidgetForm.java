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
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.FormErrorOrientation;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.DynamicForm;
import org.exoplatform.gwtframework.ui.client.component.IButton;
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

   private final String ID_DESCRIBTION_FIELD = "ideDeployUwaWidgetFormDescriptionField";

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

   private IButton deployWidgetButton;

   private IButton cancelButton;

   private IButton nextStepButton;

   private IButton prevStepButton;

   //Main :

   private TextField urlField;

   // Details :

   private DynamicForm detailsDynamicForm;

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

   private Layout mainInfoLayout;

   private Layout detailsLayout;

   private Layout privacyLayout;

   
   private LinkedHashMap<String, String> categories = new LinkedHashMap<String, String>();

   
   /**
    * @param eventBus 
    */
   public DeployUwaWidgetForm(HandlerManager eventBus)
   {
      super(eventBus, WIDTH, HEIGHT, ID);
      setCanDragResize(true);

      setTitle(TITLE);
      
      //Main layout of the dialog window:
      VLayout mainLayout = new VLayout();
      mainLayout.setWidth100();
      mainLayout.setHeight100();
      mainLayout.setPadding(20);
      mainLayout.setMembersMargin(15);

      //Create layout for main information
      mainInfoLayout = createMainInfoLayout();
      mainLayout.addMember(mainInfoLayout);
      
     //Create layout for detailed information
      detailsLayout = createDetailsLayout();
      mainLayout.addMember(detailsLayout);
      
     //Create layout for private information
      privacyLayout = createPrivacyLayout();
      mainLayout.addMember(privacyLayout);
      
      //Create and layout with buttons
      mainLayout.addMember(createButtonsLayout());

      addCloseClickHandler(new CloseClickHandler()
      {

         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });
      
      addItem(mainLayout);
      show();
   }

   /**
    * Create layout to get main information about widget.
    * 
    * @return {@link VLayout}
    */
   private Layout createMainInfoLayout()
   {
      Layout vLayout = new Layout();
      vLayout.setWidth100();
      vLayout.setHeight100();

      DynamicForm form = new DynamicForm();
      form.setPadding(8);
      form.setID(ID_MAIN_DYNAMIC_FORM);
      form.setWidth("100%");
   /* TODO  form.setIsGroup(true);
      form.setGroupTitle("<b>Step 1.</b> Widget Content");
      form.setAutoHeight();
      
      form.setLayoutAlign(Alignment.CENTER);
      form.setLayoutAlign(VerticalAlignment.CENTER);*/

      urlField = createTextField(ID_URL_FIELD, "Public widget URL", "http://", 390);
      form.add(urlField);

      vLayout.addMember(form);
      vLayout.hide();
      return vLayout;
   }

   /**
    * Create layout to private information about widget's author.
    * 
    * @return {@link VLayout}
    */
   private Layout createPrivacyLayout()
   {
      Layout vLayout = new Layout();
      vLayout.setWidth100();
      vLayout.setHeight100();

      DynamicForm form = new DynamicForm();
      form.setPadding(8);
      form.setID(ID_PRIVACY_DYNAMIC_FORM);
      form.setWidth("100%");
    /* TODO :( form.setIsGroup(true);
      form.setGroupTitle("<b>Step 3.</b> Private Information");
      form.setAutoHeight();
      form.setWidth100();
      form.setLayoutAlign(Alignment.CENTER);
      form.setLayoutAlign(VerticalAlignment.CENTER);*/

      apiKeyField = createPasswordField(ID_API_KEY_FIELD, "Enter API key", 390);
    //  apiKeyField.setColSpan(2);
      secretKeyField = createPasswordField(ID_SECRET_KEY_FIELD, "Enter secrete key", 390);
     // secretKeyField.setColSpan(2);
      loginField = createTextField(ID_LOGIN_FIELD, "Login", "", 195);
      passwordField = createPasswordField(ID_PASSWORD_FIELD, "Password", 195);

      form.add(loginField);
      form.add(passwordField);
      form.add(apiKeyField);
      form.add(secretKeyField);

      vLayout.addMember(form);
      vLayout.hide();
      return vLayout;
   }

   /**
    * Create layout to get detailed information about widget.
    * 
    * @return {@link VLayout}
    */
   private Layout createDetailsLayout()
   {
      Layout vLayout = new Layout();
      vLayout.setWidth100();
      vLayout.setHeight100();

      detailsDynamicForm = new DynamicForm();
      detailsDynamicForm.setID(ID_DETAILS_DYNAMIC_FORM);
      detailsDynamicForm.setPadding(8);
      detailsDynamicForm.setWidth("100%");
    /*  detailsDynamicForm.setIsGroup(true);
      detailsDynamicForm.setGroupTitle("<b>Step 2.</b> Detailed Information");
      detailsDynamicForm.setAutoHeight();
      detailsDynamicForm.setWidth100();
      detailsDynamicForm.setLayoutAlign(Alignment.CENTER);
      detailsDynamicForm.setLayoutAlign(VerticalAlignment.CENTER);*/

      titleField = createTextField(ID_TITLE_FIELD, "Title&#42;", "", 300);
    //TODO  titleField.setColSpan(2);
      descriptionField = createTextField(ID_DESCRIBTION_FIELD, "Description&#42;", "", 300);
      descriptionField.setHeight(50);
     //TODO descriptionField.setColSpan(2);
      versionField = createTextField(ID_VERSION_FIELD, "Widget version", "", 150);
      keywordsField = createTextField(ID_KEYWORDS_FIELD, "Descriptive keywords (max 6, space separated)", "", 300);
      //TODO keywordsField.setColSpan(2);
      thumbnailField = createTextField(ID_TUMBNAIL_FIELD, "Thumbnail URL", "", 300);
      ///TODO thumbnailField.setColSpan(2);
      languageField = createCombobox(ID_LANGUAGE_FIELD, "Main language&#42;", 150);
      languageField.setValidators(new LanguageFieldValidator());
      categoryField = createCombobox(ID_CATEGORY_FIELD, "Most appropriate category&#42;", 150);
      categoryField.setValidators(new CategoryFieldValidator(categories));
      regionField = createCombobox(ID_REGION_FIELD, "Most appropriate region&#42;", 150);
      regionField.setValidators(new RegionFieldValidator());

      detailsDynamicForm.add(titleField);
      detailsDynamicForm.add(descriptionField);
      detailsDynamicForm.add(versionField);
    //TODO  when combobox is ready detailsDynamicForm.add(languageField);
      detailsDynamicForm.add(keywordsField);
      detailsDynamicForm.add(thumbnailField);
    //TODO  when combobox is ready detailsDynamicForm.add(regionField);
    //TODO  when combobox is ready detailsDynamicForm.add(categoryField);

      vLayout.addMember(detailsDynamicForm);
      vLayout.hide();
      return vLayout;
   }

   /**
    * Create centered layout with buttons.
    * 
    * @return {@link HLayout}
    */
   private HLayout createButtonsLayout()
   {
      HLayout hLayout = new HLayout();
      hLayout.setMembersMargin(10);
      hLayout.setAutoWidth();
      hLayout.setHeight(BUTTON_HEIGHT);
      hLayout.setLayoutAlign(Alignment.CENTER);

      deployWidgetButton = createButton(ID_DEPLOY_WIDGET_BUTTON, "Deploy", Images.Buttons.OK);
      cancelButton = createButton(ID_CANCEL_BUTTON, "Cancel", Images.Buttons.CANCEL);
      nextStepButton = createButton(ID_NEXT_STEP_BUTTON, "Next", Images.Buttons.NEXT_STEP);
      prevStepButton = createButton(ID_PREV_STEP_BUTTON, "Back", Images.Buttons.PREV_STEP);

      hLayout.addMember(prevStepButton);
      hLayout.addMember(deployWidgetButton);
      hLayout.addMember(nextStepButton);
      hLayout.addMember(cancelButton);
      return hLayout;
   }

   /**
    * Created button.
    * 
    * @param id button's id
    * @param title button's display title
    * @param icon button's icon
    * @return {@link IButton} created button
    */
   private IButton createButton(String id, String title, String icon)
   {
      IButton button = new IButton(title);
      button.setID(id);
      button.setWidth(BUTTON_WIDTH);
      button.setHeight(BUTTON_HEIGHT);
      button.setIcon(icon);
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
      combobox.setDefaultToFirstOption(true);
      combobox.setTitle(title);
     //TODO combobox.setTitleOrientation(TitleOrientation.TOP);
      combobox.setWidth(width);
      combobox.setShowErrorStyle(false);
      combobox.setErrorOrientation(FormErrorOrientation.RIGHT);
      return combobox;
   }

   /**
    * @see com.smartgwt.client.widgets.BaseWidget#onDestroy()
    */
   @Override
   protected void onDestroy()
   {
      super.onDestroy();
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
         mainInfoLayout.show();
         centerInPage();
      }
      else
      {
         mainInfoLayout.hide();
      }
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
         detailsLayout.show();
      }
      else
      {
         detailsLayout.hide();
      }
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
         privacyLayout.show();
      }
      else
      {
         privacyLayout.hide();
      }
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#updateNextButtonState(boolean, boolean)
    */
   public void updateNextButtonState(boolean isVisible, boolean isEnabled)
   {
      nextStepButton.setVisible(isVisible);
      if (isEnabled)
      {
         nextStepButton.enable();
      }
      else
      {
         nextStepButton.disable();
      }
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#updatePrevButtonState(boolean, boolean)
    */
   public void updatePrevButtonState(boolean isVisible, boolean isEnabled)
   {
      prevStepButton.setVisible(isVisible);
      if (isEnabled)
      {
         prevStepButton.enable();
      }
      else
      {
         prevStepButton.disable();
      }
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#updateDeployButtonState(boolean, boolean)
    */
   public void updateDeployButtonState(boolean isVisible, boolean isEnabled)
   {
      deployWidgetButton.setVisible(isVisible);
      if (isEnabled)
      {
         deployWidgetButton.enable();
      }
      else
      {
         deployWidgetButton.disable();
      }
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
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#setCategoryValueMap(java.util.LinkedHashMap)
    */
   public void setCategoryValueMap(LinkedHashMap<String, String> values)
   {
      categories.clear();
      categories.putAll(values);
      categoryField.setValueMap(values);
      if (values != null && values.keySet().iterator().hasNext())
      {
         categoryField.setValue(values.keySet().iterator().next());
      }
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#setRegionValueMap(java.util.LinkedHashMap)
    */
   public void setRegionValueMap(LinkedHashMap<String, String> values)
   {
      regionField.setValueMap(values);
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#setLanguageValueMap(java.util.LinkedHashMap)
    */
   public void setLanguageValueMap(LinkedHashMap<String, String> values)
   {
      languageField.setValueMap(values);
   }

   /**
    * @see org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter.Display#isValidDetailsFields()
    */
   public boolean isValidDetailsFields()
   {
      //TODO !!!  detailsDynamicForm.validate(false);
      return false;
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
}
