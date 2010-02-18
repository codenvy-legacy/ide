/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.groovy;

import java.util.List;

import org.exoplatform.gwtframework.commons.rest.HTTPMethod;
import org.exoplatform.gwtframework.ui.smartgwt.component.IButton;
import org.exoplatform.gwtframework.ui.smartgwt.component.SelectItem;
import org.exoplatform.gwtframework.ui.smartgwt.component.TextAreaItem;
import org.exoplatform.gwtframework.ui.smartgwt.component.TextField;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.component.DialogWindow;
import org.exoplatform.ideall.client.component.SimpleParameterEntryListGrid;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.SimpleParameterEntry;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
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
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class RestServiceOutputPreviewForm extends DialogWindow implements RestServiceOutputPreviewPresenter.Display
{

   private static final int WIDTH = 500;

   private static final int HEIGHT = 370;

   private static final String TITLE = "REST Service output preview";

   private TextField groovyScriptURLField;

   private SelectItem httpMethod;

   private TextAreaItem requestbody;

   private IButton sendRequestButton;

   private IButton cancelButton;

   private RestServiceOutputPreviewPresenter presenter;

   private SimpleParameterEntryListGrid headers;

   private SimpleParameterEntryListGrid queryParams;

   private IButton addHeaderButton;

   private IButton deleteHeaderButton;

   private IButton addQueryParamButton;

   private IButton deleteQueryParamButton;

   public RestServiceOutputPreviewForm(HandlerManager eventBus, ApplicationContext context)
   {
      super(eventBus, WIDTH, HEIGHT);
      setTitle(TITLE);

      headers = new SimpleParameterEntryListGrid();
      queryParams = new SimpleParameterEntryListGrid();
      requestbody = new TextAreaItem();

      createHttpMethodForm();
      createURLForm();
      createAdvanceRequestForm();
      createButtonsForm();

      show();

      presenter = new RestServiceOutputPreviewPresenter(eventBus, context);
      presenter.bindDisplay(this);

      addCloseClickHandler(new CloseClickHandler()
      {
         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });
   }

   private void createAdvanceRequestForm()
   {
      TabSet tabSet = new TabSet();
      tabSet.setLayoutAlign(Alignment.CENTER);
      tabSet.setWidth(446);

      Tab headersTab = createHttpHeadersTab();
      Tab queryParamsTab = createQueryParamsTab();
      Tab bodyTab = createBodyTab();
      tabSet.setTabs(headersTab, queryParamsTab, bodyTab);
      addItem(tabSet);

   }

   private Tab createHttpHeadersTab()
   {
      Tab headersTab = new Tab("Headers");
      VLayout headerLayout = new VLayout();
      headerLayout.addMember(headers);
      DynamicForm buttonsForm = new DynamicForm();
      buttonsForm.setPadding(5);
      buttonsForm.setHeight(24);
      buttonsForm.setLayoutAlign(Alignment.CENTER);
      addHeaderButton = new IButton("Add");
      addHeaderButton.setIcon(Images.Buttons.ADD);
      addHeaderButton.setWidth(90);
      deleteHeaderButton = new IButton("Delete");
      deleteHeaderButton.setIcon(Images.Buttons.DELETE);
      deleteHeaderButton.setWidth(90);
      ToolbarItem tbi = new ToolbarItem();
      StatefulCanvas delimiter1 = new StatefulCanvas();
      delimiter1.setWidth(3);
      tbi.setButtons(addHeaderButton, delimiter1, deleteHeaderButton);
      buttonsForm.setFields(tbi);
      buttonsForm.setAutoWidth();
      headerLayout.addMember(buttonsForm);
      headersTab.setPane(headerLayout);
      return headersTab;
   }

   private Tab createQueryParamsTab()
   {
      Tab headersTab = new Tab("Query Params");
      VLayout paramsLayout = new VLayout();
      paramsLayout.addMember(queryParams);
      DynamicForm buttonsForm = new DynamicForm();
      buttonsForm.setPadding(5);
      buttonsForm.setHeight(24);
      buttonsForm.setLayoutAlign(Alignment.CENTER);
      addQueryParamButton = new IButton("Add");
      addQueryParamButton.setIcon(Images.Buttons.ADD);
      addQueryParamButton.setWidth(90);
      deleteQueryParamButton = new IButton("Delete");
      deleteQueryParamButton.setIcon(Images.Buttons.DELETE);
      deleteQueryParamButton.setWidth(90);
      ToolbarItem tbi = new ToolbarItem();
      StatefulCanvas delimiter1 = new StatefulCanvas();
      delimiter1.setWidth(3);
      tbi.setButtons(addQueryParamButton, delimiter1, deleteQueryParamButton);
      buttonsForm.setFields(tbi);
      buttonsForm.setAutoWidth();
      paramsLayout.addMember(buttonsForm);
      headersTab.setPane(paramsLayout);
      return headersTab;
   }

   private Tab createBodyTab()
   {
      requestbody.setShowTitle(false);
      //      requestbody.setWidth(430);
      //      requestbody.setHeight(110);
      requestbody.setWidth(431);
      requestbody.setHeight("100%");
      requestbody.setValue("");
      Tab bodyTab = new Tab("Body");

      VLayout bodyLayout = new VLayout();
      bodyLayout.setWidth100();

      DynamicForm form = new DynamicForm();
      //form.setPadding(5);
      //form.setHeight(24);
      form.setLayoutAlign(Alignment.CENTER);
      form.setLayoutAlign(VerticalAlignment.CENTER);
      form.setFields(requestbody);
      form.setWidth100();
      form.setHeight100();
      bodyLayout.addMember(form);
      bodyTab.setPane(bodyLayout);
      //bodyTab.setPane(form);

      return bodyTab;
   }

   private void createHttpMethodForm()
   {
      DynamicForm form = new DynamicForm();
      form.setLayoutAlign(Alignment.CENTER);
      form.setMargin(5);
      form.setWidth(450);

      StaticTextItem title = new StaticTextItem();
      title.setColSpan(2);
      title.setShowTitle(false);
      title.setDefaultValue("Http Method:");

      httpMethod = new SelectItem();
      httpMethod.setShowTitle(false);
      httpMethod.setWidth(450);
      httpMethod.setColSpan(2);
      httpMethod.setDefaultValue(HTTPMethod.GET);

      //      SpacerItem spacer = new SpacerItem();
      //      spacer.setHeight(1);      

      form.setItems(title, httpMethod);
      addItem(form);
   }

   /**
    * Create title and field for entering deployed Groovy script URl
    */
   private void createURLForm()
   {
      DynamicForm form = new DynamicForm();
      form.setMargin(5);
      form.setLayoutAlign(Alignment.CENTER);
      form.setWidth(450);

      StaticTextItem urlTitle = new StaticTextItem();
      urlTitle.setColSpan(2);
      urlTitle.setShowTitle(false);
      urlTitle.setDefaultValue("Groovy script URL:");

      SpacerItem spacer = new SpacerItem();
      spacer.setHeight(5);

      groovyScriptURLField = new TextField();
      groovyScriptURLField.setShowTitle(false);
      groovyScriptURLField.setColSpan(2);
      groovyScriptURLField.setWidth(450);

      form.setItems(urlTitle, groovyScriptURLField, spacer);

      addItem(form);
   }

   /**
    * Create Send and Cancel buttons
    */
   private void createButtonsForm()
   {
      DynamicForm buttonsForm = new DynamicForm();
      buttonsForm.setPadding(5);
      buttonsForm.setHeight(24);
      buttonsForm.setMargin(10);
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
      addItem(buttonsForm);
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

   /**
    * Close form
    * 
    * @see org.exoplatform.ideall.client.groovy.RestServiceOutputPreviewPresenter.Display#closeForm()
    */
   public void closeForm()
   {
      destroy();
   }

   /**
    * Return Groovy script URl field
    * 
    * @see org.exoplatform.ideall.client.groovy.RestServiceOutputPreviewPresenter.Display#getGroovyScriptURLField()
    */
   public HasValue<String> getGroovyScriptURLField()
   {
      return groovyScriptURLField;
   }

   /**
    * Return Send request button
    * 
    * @see org.exoplatform.ideall.client.groovy.RestServiceOutputPreviewPresenter.Display#getSendRequestButton()
    */
   public HasClickHandlers getSendRequestButton()
   {
      return sendRequestButton;
   }

   /**
    * Return Cancel button
    * 
    * @see org.exoplatform.ideall.client.groovy.RestServiceOutputPreviewPresenter.Display#getCancelButton()
    */
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * {@inheritDoc}
    */
   public HasValue<List<SimpleParameterEntry>> getHttpHeaders()
   {
      return headers;
   }

   /**
    * {@inheritDoc}
    */
   public HasValue<List<SimpleParameterEntry>> getQueryParams()
   {
      return queryParams;
   }

   /**
    * {@inheritDoc}
    */
   public HasValue<String> getHttpMethod()
   {
      return httpMethod;
   }

   /**
    * {@inheritDoc}
    */
   public HasValue<String> getRequestBody()
   {
      return requestbody;
   }

   public HasClickHandlers getAddHeaderButton()
   {
      return addHeaderButton;
   }

   public HasClickHandlers getAddQueryParamButton()
   {
      return addQueryParamButton;
   }

   public HasClickHandlers getDeleteHeaderButton()
   {
      return deleteHeaderButton;
   }

   public HasClickHandlers getDeleteQueryParamButton()
   {
      return deleteQueryParamButton;
   }

   public void addNewHeader()
   {
      headers.startEditingNew();
   }

   public void addNewQueryParam()
   {
      queryParams.startEditingNew();
   }

   public void deleteSelectedHeader()
   {
      headers.removeSelectedData();
   }

   public void deleteSelectedQueryParam()
   {
      queryParams.removeSelectedData();
   }

   public void setHttpMethods(String[] methods)
   {
      httpMethod.clearValue();
      httpMethod.setValueMap(methods);

   }

   public void setHttpHeaders(List<SimpleParameterEntry> headers)
   {

      this.headers.setValue(headers);
   }

   public void setQueryParams(List<SimpleParameterEntry> params)
   {
      this.queryParams.setValue(params);
   }

   public HasSelectionHandlers<SimpleParameterEntry> getHttHeadersListGridSelectable()
   {
      return headers;
   }

   public void enableDeleteHeaderButton()
   {
      deleteHeaderButton.setDisabled(false);
   }

   public void disableDeleteHeaderButton()
   {
      deleteHeaderButton.setDisabled(true);
   }

   public void disableDeleteQueryParameterButton()
   {
      deleteQueryParamButton.setDisabled(true);
   }

   public void enableDeleteQueryParameterButton()
   {
      deleteQueryParamButton.setDisabled(false);
   }

   public HasSelectionHandlers<SimpleParameterEntry> getQuereParameterListGridSelectable()
   {
      return queryParams;
   }

}
