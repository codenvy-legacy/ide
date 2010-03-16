/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.groovy;

import org.exoplatform.gwtframework.commons.wadl.WadlApplication;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextAreaItem;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextField;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.component.DialogWindow;
import org.exoplatform.ideall.client.component.WadlParameterEntryListGrid;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class GroovyServiceOutputPreviewForm extends DialogWindow implements GroovyServiceOutputPreviewPresenter.Display
{
   private static final int WIDTH = 530;

   private static final int HEIGHT = 400;

   private static final String TITLE = "REST Service output preview";

   private IButton sendRequestButton;

   private IButton cancelButton;

   private GroovyServiceOutputPreviewPresenter presenter;

   private HLayout hLayout;

   private VLayout vLayout;

   private SelectItem pathField;

   private SelectItem methodField;

   private TextField requestMediaTypeField;

   private TextField responseMediaTypeField;

   private WadlParameterEntryListGrid parametersQueryGrid;

   private WadlParameterEntryListGrid parameterHeaderGrid;

   private TextAreaItem requestbody;

   public GroovyServiceOutputPreviewForm(HandlerManager eventBus, ApplicationContext context, WadlApplication wadlApplication)
   {
      super(eventBus, WIDTH, HEIGHT);
      setTitle(TITLE);

      vLayout = new VLayout();
      vLayout.setMargin(2);

      hLayout = new HLayout();
      hLayout.setMargin(8);
      hLayout.setHeight("*");

      vLayout.addMember(hLayout);

      addItem(vLayout);

      createParamsForm();
      createButtonsForm();
      
      show();

      presenter = new GroovyServiceOutputPreviewPresenter(eventBus, context, wadlApplication);
      presenter.bindDisplay(this);

      addCloseClickHandler(new CloseClickHandler()
      {

         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });

   }
   
   private Tab createParametersHeaderTab()
   {
      parameterHeaderGrid = new WadlParameterEntryListGrid();
      parameterHeaderGrid.setHeight100();
      parameterHeaderGrid.setCanEdit(true);

      Tab headerTab = new Tab("Header Parameter");
      headerTab.setPane(parameterHeaderGrid);

      return headerTab;
   }

   private Tab createBodyTab()
   {

      Tab bodyTab = new Tab("Body");

      DynamicForm form = new DynamicForm();
      form.setWidth100();
      form.setHeight100();
      form.setLayoutAlign(Alignment.CENTER);
      form.setLayoutAlign(VerticalAlignment.CENTER);
      
      requestbody = new TextAreaItem();
      requestbody.setShowTitle(false);

      requestbody.setWidth(465);
      requestbody.setHeight("100%");
      requestbody.setValue("");

      form.setLayoutAlign(Alignment.CENTER);
      form.setLayoutAlign(VerticalAlignment.CENTER);

      form.setFields(requestbody);

      bodyTab.setPane(form);

      return bodyTab;
   }

   private Tab createParametersQueryTab()
   {
      parametersQueryGrid = new WadlParameterEntryListGrid();
      parametersQueryGrid.setHeight100();
      parametersQueryGrid.setCanEdit(true);

      Tab queryTab = new Tab("Query Parameter");
      queryTab.setPane(parametersQueryGrid);

      return queryTab;
   }

   private TabSet createParametersTabSet()
   {
      TabSet parametersTabSet = new TabSet();
      parametersTabSet.setHeight100();
      parametersTabSet.setLayoutAlign(Alignment.CENTER);
      parametersTabSet.setWidth(480);

      parametersTabSet.addTab(createParametersQueryTab());
      parametersTabSet.addTab(createParametersHeaderTab());
      parametersTabSet.addTab(createBodyTab());

      return parametersTabSet;
   }
   
   private void createParamsForm()
   {
      DynamicForm form = new DynamicForm();

      VLayout vLay = new VLayout();
      
      vLay.setMargin(8);
      vLay.setWidth(480);
      vLay.setAlign(Alignment.CENTER);

      StaticTextItem pathTitle = new StaticTextItem();
      pathTitle.setColSpan(2);
      pathTitle.setShowTitle(false);
      pathTitle.setDefaultValue("Path:");

      pathField = new SelectItem();
      pathField.setShowTitle(false);
      pathField.setWidth(480);
      pathField.setColSpan(2);
      pathField.setType("comboBox");

      StaticTextItem methodTitle = new StaticTextItem();
      methodTitle.setColSpan(2);
      methodTitle.setShowTitle(false);
      methodTitle.setDefaultValue("Method:");

      methodField = new SelectItem();
      methodField.setShowTitle(false);
      methodField.setWidth(480);
      methodField.setColSpan(2);

      StaticTextItem requestTitle = new StaticTextItem();
      requestTitle.setColSpan(2);
      requestTitle.setShowTitle(false);
      requestTitle.setDefaultValue("Request Media Type:");

      requestMediaTypeField = new TextField();
      requestMediaTypeField.setShowTitle(false);
      requestMediaTypeField.setColSpan(2);
      requestMediaTypeField.setWidth(480);
      requestMediaTypeField.setDisabled(true);

      StaticTextItem responseTitle = new StaticTextItem();
      responseTitle.setColSpan(2);
      responseTitle.setShowTitle(false);
      responseTitle.setDefaultValue("Response Media Type");

      responseMediaTypeField = new TextField();
      responseMediaTypeField.setShowTitle(false);
      responseMediaTypeField.setColSpan(2);
      responseMediaTypeField.setWidth(480);
      responseMediaTypeField.setDisabled(true);
      
      SpacerItem spacer = new SpacerItem();
      spacer.setHeight(5);

      form.setItems(pathTitle, pathField, methodTitle, methodField, requestTitle, requestMediaTypeField, responseTitle,
         responseMediaTypeField, spacer);

      vLay.addMember(form);

      vLay.addMember(createParametersTabSet());

      hLayout.addMember(vLay);
   }
   
   private void createButtonsForm()
   {
      DynamicForm buttonsForm = new DynamicForm();
      buttonsForm.setMargin(3);
      buttonsForm.setPadding(3);
      buttonsForm.setHeight(24);
      buttonsForm.setLayoutAlign(Alignment.CENTER);

      sendRequestButton = new IButton("Send");
      sendRequestButton.setWidth(90);
      sendRequestButton.setHeight(22);
      sendRequestButton.setIcon(Images.Buttons.YES);

      cancelButton = new IButton("Cancel");
      cancelButton.setWidth(90);
      cancelButton.setHeight(22);
      cancelButton.setIcon(Images.Buttons.NO);

      ToolbarItem tbi = new ToolbarItem();
      StatefulCanvas delimiter1 = new StatefulCanvas();
      delimiter1.setWidth(3);
      tbi.setButtons(sendRequestButton, delimiter1, cancelButton);
      buttonsForm.setFields(tbi);

      buttonsForm.setAutoWidth();
      vLayout.addMember(buttonsForm);
   }
   
   public void closeForm()
   {
      destroy();
   }

   /**
    * Destroy the window
    * 
    * @see com.smartgwt.client.widgets.BaseWidget#onDestroy()
    */
   @Override
   protected void onDestroy()
   {
      presenter.destroy();
      super.onDestroy();
   }

   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   public HasClickHandlers getSendRequestButton()
   {
      return sendRequestButton;
   }

   public HasValue<String> getMethodField()
   {
      return methodField;
   }

   public HasValue<String> getPathField()
   {
      return pathField;
   }
   
   public void setPaths(String[] paths)
   {
      pathField.clearValue();
      pathField.setValueMap(paths);
   }
   
   public void setMethods(String[] methods)
   {
      methodField.clearValue();
      methodField.setValueMap(methods);
   }
   
   public HasValue<String> getRequestMediaTypeField()
   {
      return requestMediaTypeField;
   }

   public HasValue<String> getResponseMediaTypeField()
   {
      return responseMediaTypeField;
   }

   public WadlParameterEntryListGrid getParametersHeaderListGrid()
   {
      return parameterHeaderGrid;
   }

   public WadlParameterEntryListGrid getParametersQueryListGrid()
   {
      return parametersQueryGrid;
   }

   public HasValue<String> getRequestBody()
   {
      return requestbody;
   }
   
   public void setBodyDisabled(boolean value)
   {
      requestbody.setDisabled(value);
   }

}