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
package org.exoplatform.ide.extension.groovy.client.ui;

import java.util.LinkedHashMap;

import org.exoplatform.gwtframework.commons.wadl.WadlApplication;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextAreaItem;
import org.exoplatform.ide.extension.groovy.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;

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

   private static final String ID = "ideGroovyServiceOutputPreviewForm";

   private static final String ID_FORM = "ideGroovyServiceForm";

   private static final String NAME_PATH = "ideGroovyServicePath";

   private static final String NAME_METHOD = "ideGroovyServiceMethod";

   private static final String NAME_REQUEST = "ideGroovyServiceRequest";

   private static final String NAME_RESPONSE = "ideGroovyServiceResponse";

   private static final String ID_GET_URL = "ideGroovyServiceGetURL";

   private static final String ID_SEND = "ideGroovyServiceSend";

   private static final String ID_CANCEL = "ideGroovyServiceCancel";

   private static final String ID_HEADER_TAB = "ideGroovyServiceHeaderTab";

   private static final String ID_QUERY_TAB = "ideGroovyServiceQueryTab";
   
   private static final String ID_HEADER_TABLE = "ideGroovyServiceHeaderTable";

   private static final String ID_QUERY_TABLE = "ideGroovyServiceQueryTable";

   private static final String ID_BODY_TAB = "ideGroovyServiceBodyTab";
   
   private static final String ID_TAB_SET = "ideGroovyServiceTabSet";
   
   private static final String ID_BODY_FORM = "ideGroovyServiceBodyForm";
   
   private static final String ID_BODY_FORM_TEXT = "ideGroovyServiceBodyFormText";

   private static final String TITLE = "Launch REST Service";

   private IButton showUrlButton;

   private IButton sendRequestButton;

   private IButton cancelButton;

   private GroovyServiceOutputPreviewPresenter presenter;

   private HLayout hLayout;

   private VLayout vLayout;

   private SelectItem pathField;

   private SelectItem methodField;

   private SelectItem requestMediaTypeField;

   private SelectItem responseMediaTypeField;

   private WadlParameterEntryListGrid parametersQueryGrid;

   private WadlParameterEntryListGrid parameterHeaderGrid;

   private TextAreaItem requestbody;

   private Tab bodyTab;

   private TabSet parametersTabSet;

   public GroovyServiceOutputPreviewForm(HandlerManager eventBus, WadlApplication wadlApplication, boolean undeloyOnCansel)
   {
      super(eventBus, WIDTH, HEIGHT, ID);
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

      presenter = new GroovyServiceOutputPreviewPresenter(eventBus, wadlApplication, undeloyOnCansel);
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
      parameterHeaderGrid.setID(ID_HEADER_TABLE);
      
      //parameterHeaderGrid.getFields()[0].setHidden(true);

      Tab headerTab = new Tab("Header Parameter");
      headerTab.setPane(parameterHeaderGrid);
      headerTab.setID(ID_HEADER_TAB);
      return headerTab;
   }

   private Tab createBodyTab()
   {

      bodyTab = new Tab("Body");
      bodyTab.setID(ID_BODY_TAB);

      DynamicForm form = new DynamicForm();
      form.setWidth100();
      form.setHeight100();
      form.setLayoutAlign(Alignment.CENTER);
      form.setLayoutAlign(VerticalAlignment.CENTER);
      form.setID(ID_BODY_FORM);

      requestbody = new TextAreaItem();
      requestbody.setShowTitle(false);

      requestbody.setWidth(465);
      requestbody.setHeight("100%");
      requestbody.setValue("");
      requestbody.setName(ID_BODY_FORM_TEXT);

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
      parametersQueryGrid.setID(ID_QUERY_TABLE);

      Tab queryTab = new Tab("Query Parameter");
      queryTab.setID(ID_QUERY_TAB);
      
      queryTab.setPane(parametersQueryGrid);

      return queryTab;
   }

   private TabSet createParametersTabSet()
   {
      parametersTabSet = new TabSet();
      parametersTabSet.setHeight100();
      parametersTabSet.setLayoutAlign(Alignment.CENTER);
      parametersTabSet.setWidth(480);
      parametersTabSet.setID(ID_TAB_SET);

      parametersTabSet.addTab(createParametersQueryTab());
      parametersTabSet.addTab(createParametersHeaderTab());
      parametersTabSet.addTab(createBodyTab());

      return parametersTabSet;
   }

   private void createParamsForm()
   {
      DynamicForm form = new DynamicForm();
      form.setID(ID_FORM);

      VLayout vLay = new VLayout();

      vLay.setMargin(8);
      vLay.setWidth(480);
      vLay.setAlign(Alignment.CENTER);

      StaticTextItem pathTitle = new StaticTextItem();
      pathTitle.setColSpan(2);
      pathTitle.setShowTitle(false);
      pathTitle.setDefaultValue("Path:");

      pathField = new SelectItem();
      pathField.setName(NAME_PATH);
      pathField.setShowTitle(false);
      pathField.setWidth(480);
      pathField.setColSpan(2);
      pathField.setType("comboBox");
      pathField.setHideEmptyPickList(true);

      StaticTextItem methodTitle = new StaticTextItem();
      methodTitle.setColSpan(2);
      methodTitle.setShowTitle(false);
      methodTitle.setDefaultValue("Method:");

      methodField = new SelectItem();
      methodField.setShowTitle(false);
      methodField.setWidth(480);
      methodField.setColSpan(2);
      methodField.setName(NAME_METHOD);

      StaticTextItem requestTitle = new StaticTextItem();
      requestTitle.setColSpan(2);
      requestTitle.setShowTitle(false);
      requestTitle.setDefaultValue("Request Media Type:");

      requestMediaTypeField = new SelectItem();
      requestMediaTypeField.setShowTitle(false);
      requestMediaTypeField.setColSpan(2);
      requestMediaTypeField.setWidth(480);
      requestMediaTypeField.setName(NAME_REQUEST);

      StaticTextItem responseTitle = new StaticTextItem();
      responseTitle.setColSpan(2);
      responseTitle.setShowTitle(false);
      responseTitle.setDefaultValue("Response Media Type");

      responseMediaTypeField = new SelectItem();
      responseMediaTypeField.setShowTitle(false);
      responseMediaTypeField.setColSpan(2);
      responseMediaTypeField.setWidth(480);
      responseMediaTypeField.setName(NAME_RESPONSE);

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

      showUrlButton = new IButton("Get URL");
      showUrlButton.setWidth(90);
      showUrlButton.setHeight(22);
      showUrlButton.setID(ID_GET_URL);

      showUrlButton.setIcon(Images.Buttons.URL);

      sendRequestButton = new IButton("Send");
      sendRequestButton.setWidth(90);
      sendRequestButton.setHeight(22);
      sendRequestButton.setIcon(Images.Buttons.YES);
      sendRequestButton.setDisabled(true);
      sendRequestButton.setID(ID_SEND);

      cancelButton = new IButton("Cancel");
      cancelButton.setWidth(90);
      cancelButton.setHeight(22);
      cancelButton.setIcon(Images.Buttons.NO);
      cancelButton.setID(ID_CANCEL);

      ToolbarItem tbi = new ToolbarItem();
      StatefulCanvas delimiter1 = new StatefulCanvas();
      delimiter1.setWidth(3);
      StatefulCanvas delimiter2 = new StatefulCanvas();
      delimiter2.setWidth(3);
      tbi.setButtons(showUrlButton, delimiter1, sendRequestButton, delimiter2, cancelButton);
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

   public void setMethodFieldValue(String value)
   {
      methodField.setValue(value);
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

   public void setMethods(LinkedHashMap<String, String> methods)
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

   public void setSendRequestButtonDisabled(boolean value)
   {
      sendRequestButton.setDisabled(value);
   }

   public HasClickHandlers getShowUrlButton()
   {
      return showUrlButton;
   }

   public void setShowUrlButtonDisabled(boolean value)
   {
      showUrlButton.setDisabled(value);
   }

   public void setBodyTabEnabled()
   {
      // 2 is tabIndex of body tab
      parametersTabSet.enableTab(2);
   }

   public void setBodyTabDisabled()
   {
      // 2 is tabIndex of body tab
      parametersTabSet.disableTab(2);
   }

   public void setRequestMediaType(LinkedHashMap<String, String> requestMediaType)
   {
      requestMediaTypeField.clearValue();
      requestMediaTypeField.setValueMap(requestMediaType);
   }

   public void setRequestMediaTypeFieldValue(String value)
   {
      requestMediaTypeField.setValue(value);
   }

   public void setResponseMediaType(LinkedHashMap<String, String> responseMediaType)
   {
      responseMediaTypeField.clearValue();
      responseMediaTypeField.setValueMap(responseMediaType);
   }

   public void setResponseMediaTypeFieldValue(String value)
   {
      responseMediaTypeField.setValue(value);
   }

   /**
    * @see org.exoplatform.ide.client.groovy.GroovyServiceOutputPreviewPresenter.Display#setPathFieldValue(java.lang.String)
    */
   public void setPathFieldValue(String value)
   {
      pathField.setValue(value);
   }

}