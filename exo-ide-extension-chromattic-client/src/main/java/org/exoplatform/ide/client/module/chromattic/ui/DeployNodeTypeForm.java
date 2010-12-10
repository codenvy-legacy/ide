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
package org.exoplatform.ide.client.module.chromattic.ui;

import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import com.smartgwt.client.types.TitleOrientation;

import com.smartgwt.client.types.Alignment;

import com.smartgwt.client.widgets.layout.HLayout;

import com.smartgwt.client.widgets.layout.VLayout;

import com.smartgwt.client.widgets.form.DynamicForm;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextField;
import org.exoplatform.ide.client.framework.ui.DialogWindow;

import java.util.LinkedHashMap;

/**
 * View for deploy node type operation.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 6, 2010 $
 *
 */
public class DeployNodeTypeForm extends DialogWindow implements DeployNodeTypePresenter.Display
{
   public static final int WIDTH = 650;

   public static final int HEIGHT = 320;

   private final int BUTTON_WIDTH = 90;

   private final int BUTTON_HEIGHT = 22;

   private static final String ID = "ideDeployNodeTypeForm";

   private final String TITLE = "Deploy Node Type";

   //IDs for Selenium tests:

   private final String ID_CANCEL_BUTTON = "ideDeployNodeTypeFormCancelButton";

   private final String ID_GENERATE_BUTTON = "ideDeployNodeTypeFormGenerateButton";

   private final String ID_DEPLOY_BUTTON = "ideDeployNodeTypeFormDeployButton";

   private final String ID_DEPENDENCY_FORM = "ideDeployNodeTypeFormDependendyDynamicForm";

   private final String ID_RESULT_FORM = "ideDeployNodeTypeFormResultDynamicForm";

   private final String ID_CHOICE_FORM = "ideDeployNodeTypeFormChoiceDynamicForm";

   private final String ID_LOCATION_FIELD = "ideDeployNodeTypeFormLocationField";

   private final String ID_GENERATION_RESULT_FIELD = "ideDeployNodeTypeFormGenerationResultField";

   private final String ID_DEPENDENCY_FIELD = "ideDeployNodeTypeFormDependencyField";

   private final String ID_FORMAT_FIELD = "ideDeployNodeTypeFormFormatField";

   private final String ID_ALREADY_EXIST_BEHAVIOR_FIELD = "ideDeployNodeTypeFormAlreadyExistBehaviorField";

   /**
    * Location text field.
    */
   private TextField locationField;

   /**
    * Dependency location text field.
    */
   private TextField dependencyField;

   /**
    * Generation result text field.
    */
   private TextField generationResultField;

   /**
    * Node type format select field.
    */
   private SelectItem formatField;

   /**
    * Already exist behavior select field.
    */
   private SelectItem alreadyExistBehaviorField;

   /**
    * Button to generate operation.
    */
   private IButton generateButton;

   /**
    * Button for deploy operation.
    */
   private IButton deployButton;

   /**
    * Button to cancel and close deploy window.
    */
   private IButton cancelButton;

   /**
    * @param eventBus
    */
   public DeployNodeTypeForm(HandlerManager eventBus)
   {
      super(eventBus, WIDTH, HEIGHT, ID);
      setTitle(TITLE);
      setCanDragResize(true);

      VLayout mainLayout = new VLayout();
      mainLayout.setWidth100();
      mainLayout.setHeight100();
      mainLayout.setPadding(20);
      mainLayout.setMembersMargin(10);

      mainLayout.addMember(createDependencyForm());

      HLayout centerLayout = new HLayout();
      centerLayout.setWidth100();
      centerLayout.setHeight100();
      centerLayout.setMembersMargin(5);
      centerLayout.addMember(createLeftLayout());
      centerLayout.addMember(createResultForm());

      mainLayout.addMember(centerLayout);

      mainLayout.addMember(createButtonLayout());

      addCloseClickHandler(new CloseClickHandler()
      {

         @Override
         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });

      addItem(mainLayout);
      show();
   }

   /**
    * Create form with location fields.
    * 
    * @return {@link DynamicForm}
    */
   private DynamicForm createDependencyForm()
   {
      DynamicForm form = new DynamicForm();
      form.setID(ID_DEPENDENCY_FORM);
      form.setWrapItemTitles(true);
      form.setAutoWidth();
      form.setNumCols(1);
      form.setLayoutAlign(Alignment.CENTER);

      locationField = new TextField(ID_LOCATION_FIELD, "<nobr>" + "Location" + "</nobr>");
      locationField.setTitleAlign(Alignment.LEFT);
      locationField.setWidth(500);
      locationField.setShowDisabled(false);
      locationField.setDisabled(true);

      dependencyField = new TextField(ID_DEPENDENCY_FIELD, "<nobr>" + "Dependency URL" + "</nobr>");
      dependencyField.setTitleAlign(Alignment.LEFT);
      dependencyField.setWidth(500);

      form.setItems(locationField, dependencyField);
      return form;
   }

   /**
    * Create form for node type and already exist behavior select fields.
    * 
    * @return {@link DynamicForm}
    */
   private DynamicForm createChoiceForm()
   {
      DynamicForm form = new DynamicForm();
      form.setID(ID_CHOICE_FORM);
      form.setAutoWidth();
      form.setNumCols(1);
      form.setLayoutAlign(Alignment.LEFT);

      formatField = createSelectItem(ID_FORMAT_FIELD, "Node type format", 200);
      alreadyExistBehaviorField = createSelectItem(ID_ALREADY_EXIST_BEHAVIOR_FIELD, "What to do if node exists?", 200);

      form.setItems(formatField, alreadyExistBehaviorField);

      return form;
   }

   /**
    * Create form for result field.
    * 
    * @return {@link DynamicForm}
    */
   private DynamicForm createResultForm()
   {
      DynamicForm form = new DynamicForm();
      form.setID(ID_RESULT_FORM);
      form.setAutoWidth();
      form.setNumCols(1);
      form.setLayoutAlign(Alignment.RIGHT);

      generationResultField = new TextField(ID_GENERATION_RESULT_FIELD, "Node type");
      generationResultField.setTitleOrientation(TitleOrientation.TOP);
      generationResultField.setWidth(390);
      generationResultField.setHeight(130);

      form.setItems(generationResultField);

      return form;
   }

   /**
    * @return {@link VLayout}
    */
   private VLayout createLeftLayout()
   {
      VLayout vLayout = new VLayout();
      vLayout.setAutoWidth();
      vLayout.setHeight100();
      vLayout.setMembersMargin(20);

      vLayout.addMember(createChoiceForm());

      HLayout hLayout = new HLayout();
      hLayout.setAutoWidth();
      hLayout.setHeight(BUTTON_HEIGHT);
      hLayout.setLayoutAlign(Alignment.CENTER);

      generateButton = createButton(ID_GENERATE_BUTTON, "Generate", "");
      hLayout.addMember(generateButton);

      vLayout.addMember(hLayout);
      return vLayout;
   }

   /**
    * Create select field.
    * 
    * @param id select field id
    * @param title select field title
    * @param width select field width
    * @return {@link SelectItem} created select field
    */
   private SelectItem createSelectItem(String id, String title, int width)
   {
      SelectItem selectItem = new SelectItem();
      selectItem.setTitleOrientation(TitleOrientation.TOP);
      selectItem.setName(id);
      selectItem.setTitle(title);
      selectItem.setWidth(width);
      return selectItem;
   }

   /**
    * @return {@link HLayout}
    */
   private HLayout createButtonLayout()
   {
      HLayout hLayout = new HLayout();
      hLayout.setMembersMargin(10);
      hLayout.setAutoWidth();
      hLayout.setHeight(BUTTON_HEIGHT);
      hLayout.setLayoutAlign(Alignment.CENTER);

      deployButton = createButton(ID_DEPLOY_BUTTON, "Deploy", "");
      cancelButton = createButton(ID_CANCEL_BUTTON, "Cancel", "");

      hLayout.addMember(deployButton);
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
    * @see org.exoplatform.ide.client.module.chromattic.ui.DeployNodeTypePresenter.Display#getDeployButton()
    */
   @Override
   public HasClickHandlers getDeployButton()
   {
      return deployButton;
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.DeployNodeTypePresenter.Display#getCancelButton()
    */
   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.DeployNodeTypePresenter.Display#getGenerateButton()
    */
   @Override
   public HasClickHandlers getGenerateButton()
   {
      return generateButton;
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.DeployNodeTypePresenter.Display#getDependencyLocation()
    */
   @Override
   public TextFieldItem getDependencyLocation()
   {
      return dependencyField;
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.DeployNodeTypePresenter.Display#getLocation()
    */
   @Override
   public TextFieldItem getLocation()
   {
      return locationField;
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.DeployNodeTypePresenter.Display#getNodeTypeFormat()
    */
   @Override
   public HasValue<String> getNodeTypeFormat()
   {
      return formatField;
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.DeployNodeTypePresenter.Display#getActionIfExist()
    */
   @Override
   public HasValue<String> getActionIfExist()
   {
      return alreadyExistBehaviorField;
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.DeployNodeTypePresenter.Display#getGenerationResult()
    */
   @Override
   public HasValue<String> getGenerationResult()
   {
      return generationResultField;
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.DeployNodeTypePresenter.Display#closeView()
    */
   @Override
   public void closeView()
   {
      destroy();
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.DeployNodeTypePresenter.Display#updateDeployButtonState(boolean)
    */
   @Override
   public void updateDeployButtonState(boolean isEnabled)
   {
      if (isEnabled)
      {
         deployButton.enable();
      }
      else
      {
         deployButton.disable();
      }
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.DeployNodeTypePresenter.Display#setNodeTypeFormatValues(java.lang.String[])
    */
   @Override
   public void setNodeTypeFormatValues(String[] values)
   {
      formatField.setValueMap(values);
      if (values.length > 0)
      {
         formatField.setValue(values[0]);
      }
   }

   /**
    * @see org.exoplatform.ide.client.module.chromattic.ui.DeployNodeTypePresenter.Display#setActionIfExistValues(java.lang.String[])
    */
   @Override
   public void setActionIfExistValues(LinkedHashMap<String, String> values)
   {
      alreadyExistBehaviorField.setValueMap(values);
      if (values.keySet().iterator().hasNext())
      {
         alreadyExistBehaviorField.setValue(values.keySet().iterator().next());
      }
   }
}
