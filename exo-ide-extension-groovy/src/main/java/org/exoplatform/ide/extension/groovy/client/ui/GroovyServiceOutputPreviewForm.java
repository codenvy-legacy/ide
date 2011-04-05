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

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.commons.wadl.WadlApplication;
import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.gwtframework.ui.client.component.SelectItem;
import org.exoplatform.gwtframework.ui.client.component.TextAreaItem;
import org.exoplatform.gwtframework.ui.client.component.TitleOrientation;
import org.exoplatform.gwtframework.ui.client.tab.TabPanel;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.extension.groovy.client.Images;

import java.util.LinkedHashMap;

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

   private VerticalPanel vLayout;

   private ComboBoxField pathField;

   private SelectItem methodField;

   private SelectItem requestMediaTypeField;

   private SelectItem responseMediaTypeField;

   private WadlParameterEntryListGrid parametersQueryGrid;

   private WadlParameterEntryListGrid parameterHeaderGrid;

   private TextAreaItem requestbody;

   private TabPanel parametersTabSet;

   public GroovyServiceOutputPreviewForm(HandlerManager eventBus, WadlApplication wadlApplication,
      boolean undeloyOnCansel)
   {
      super(WIDTH, HEIGHT, ID);
      setTitle(TITLE);

      vLayout = new VerticalPanel();
      vLayout.setWidth("100%");
      vLayout.setHeight("100%");
      vLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

      setWidget(vLayout);

      createParamsForm();
      createButtonsForm();

      show();

      presenter = new GroovyServiceOutputPreviewPresenter(eventBus, wadlApplication, undeloyOnCansel);
      presenter.bindDisplay(this);
      parametersTabSet.selectTab(0);
   }

   private Widget createParametersHeaderTab()
   {
      parameterHeaderGrid = new WadlParameterEntryListGrid();
      parameterHeaderGrid.setID(ID_HEADER_TABLE);
      parameterHeaderGrid.setHeight("100%");
      parameterHeaderGrid.setWidth("100%");
      return parameterHeaderGrid;
   }

   private Widget createBodyTab()
   {
      VerticalPanel form = new VerticalPanel();
      form.getElement().setId(ID_BODY_FORM);
      requestbody = new TextAreaItem();
      requestbody.setShowTitle(false);
      requestbody.setWidth(465);
      requestbody.setHeight(110);
      requestbody.setValue("");
      requestbody.setName(ID_BODY_FORM_TEXT);
      form.add(requestbody);
      return form;
   }

   private Widget createParametersQueryTab()
   {
      parametersQueryGrid = new WadlParameterEntryListGrid();
      parametersQueryGrid.setID(ID_QUERY_TABLE);
      parametersQueryGrid.setHeight("100%");
      parametersQueryGrid.setWidth("100%");
      return parametersQueryGrid;
   }

   private TabPanel createParametersTabSet()
   {
      parametersTabSet = new TabPanel();
      parametersTabSet.setWidth(480 + "px");
      parametersTabSet.setHeight(155 + "px");
      parametersTabSet.getElement().setId(ID_TAB_SET);

      parametersTabSet.addTab(ID_QUERY_TAB, null, "Query Parameter", createParametersQueryTab(), false);
      parametersTabSet.addTab(ID_HEADER_TAB, null, "Header Parameter", createParametersHeaderTab(), false);
      parametersTabSet.addTab(ID_BODY_TAB, null, "Body", createBodyTab(), false);

      return parametersTabSet;
   }

   private void createParamsForm()
   {
      VerticalPanel form = new VerticalPanel();
      form.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
      form.setSpacing(3);
      form.getElement().setId(ID_FORM);

      pathField = new ComboBoxField();
      pathField.setTitleOrientation(TitleOrientation.TOP);
      pathField.setShowTitle(true);
      pathField.setTitle("Path:");
      pathField.setName(NAME_PATH);
      pathField.setWidth(483);
      pathField.setPickListHeight(100);
      

      methodField = new SelectItem(NAME_METHOD, "Method:");
      methodField.setWidth(480);
      methodField.setTitleOrientation(TitleOrientation.TOP);

      requestMediaTypeField = new SelectItem(NAME_REQUEST, "Request Media Type:");
      requestMediaTypeField.setWidth(480);
      requestMediaTypeField.setTitleOrientation(TitleOrientation.TOP);

      responseMediaTypeField = new SelectItem(NAME_RESPONSE, "Response Media Type");
      responseMediaTypeField.setWidth(480);
      responseMediaTypeField.setTitleOrientation(TitleOrientation.TOP);

      form.add(pathField);
      form.add(methodField);
      form.add(requestMediaTypeField);
      form.add(responseMediaTypeField);

      form.add(createParametersTabSet());

      vLayout.add(form);
   }

   private void createButtonsForm()
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setHeight(22 + 20 + "px");
      buttonsLayout.setSpacing(5);

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

      buttonsLayout.add(showUrlButton);
      buttonsLayout.add(sendRequestButton);
      buttonsLayout.add(cancelButton);
      vLayout.add(buttonsLayout);
   }

   public void closeForm()
   {
      destroy();
   }

   /**
    * @see org.exoplatform.gwtframework.ui.client.window.Window#destroy()
    */
   @Override
   public void destroy()
   {
      presenter.destroy();
      super.destroy();
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
      //TODO not implemented in tab panel parametersTabSet.enableTab(2);
   }

   public void setBodyTabDisabled()
   {
      // 2 is tabIndex of body tab
      //TODO not implemented in tab panel parametersTabSet.disableTab(2);
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