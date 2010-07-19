/*
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.exoplatform.ideall.client.search.file;

import org.exoplatform.gwtframework.ui.client.smartgwt.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextField;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.framework.ui.DialogWindow;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   $
 *
 */
public class SearchForm extends DialogWindow implements SearchPresenter.Display
{
   private static final int WIDTH = 450;

   private static final int HEIGHT = 190;
   
   private static final String ID = "ideallSearchForm";

   private final int BUTTON_WIDTH = 90;

   private final int BUTTON_HEIGHT = 22;

   private static int FIELD_WIDTH = 300;

   private static int FIELD_HEIGHT = 20;

   private final int FORM_WIDTH = 430;

   private TextField contentField;

   private TextField pathField;

   private IButton searchButton;

   private IButton cancelButton;

   private ComboBoxField mimeTypesField;

   /**
    * {@inheritDoc}
    */
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   /**
    * {@inheritDoc}
    */
   public HasValue<String> getSearchContent()
   {
      return contentField;
   }

   /**
    * {@inheritDoc}
    */
   public HasClickHandlers getSearchButton()
   {
      return searchButton;
   }

   public SearchForm(HandlerManager eventBus, ApplicationContext context)
   {
      super(eventBus, WIDTH, HEIGHT, ID);
      setTitle("Search");

      VLayout mainLayout = new VLayout();
      mainLayout.setHeight100();
      mainLayout.setWidth100();
      mainLayout.setMargin(5);
      mainLayout.setPadding(1);
      mainLayout.setMembersMargin(10);

      mainLayout.addMember(createSearchForm());
      mainLayout.addMember(createButtonsLayout());

      addItem(mainLayout);

      show();
      
      UIHelper.setAsReadOnly(pathField.getName());
      
      SearchPresenter advancedSearchPresenter = new SearchPresenter(eventBus, context);
      advancedSearchPresenter.bindDisplay(this);
   }

   private DynamicForm createSearchForm()
   {
      DynamicForm paramForm = new DynamicForm();
      paramForm.setLayoutAlign(Alignment.CENTER);
      paramForm.setWidth100();
      paramForm.setPadding(5);

      paramForm.setWidth(FORM_WIDTH);
      paramForm.setCellSpacing(5);
      paramForm.setLayoutAlign(Alignment.CENTER);

      pathField = createValueField("Path");

      contentField = createValueField("Containing text");

      mimeTypesField = createSelectField("Mime type");

      paramForm.setItems(pathField, contentField, mimeTypesField);

      return paramForm;
   }

   private TextField createValueField(String title)
   {
      TextField textField = new TextField();
      textField.setTitle("<NOBR>" + title + "</NOBR>");
      textField.setHeight(FIELD_HEIGHT);
      textField.setWidth(FIELD_WIDTH);
      textField.setSelectOnFocus(true);
      return textField;
   }

   private ComboBoxField createSelectField(String title)
   {
      ComboBoxField comboboxField = new ComboBoxField();
      comboboxField.setTitle("<NOBR>" + title + "</NOBR>");
      comboboxField.setWidth(FIELD_WIDTH);
      comboboxField.setHeight(FIELD_HEIGHT);
      comboboxField.setColSpan(2);
      return comboboxField;
   }

   private HLayout createButtonsLayout()
   {
      HLayout buttonsLayout = new HLayout();
      buttonsLayout.setHeight(BUTTON_HEIGHT);
      buttonsLayout.setLayoutAlign(Alignment.CENTER);
      buttonsLayout.setAutoWidth();
      buttonsLayout.setMembersMargin(10);

      searchButton = createButton("Search", Images.Buttons.SEARCH);
      cancelButton = createButton("Cancel", Images.Buttons.CANCEL);

      buttonsLayout.addMember(searchButton);
      buttonsLayout.addMember(cancelButton);

      return buttonsLayout;
   }

   private IButton createButton(String title, String icon)
   {
      IButton button = new IButton(title);
      button.setIcon(icon);
      button.setWidth(BUTTON_WIDTH);
      button.setHeight(BUTTON_HEIGHT);
      return button;
   }

   public void closeForm()
   {
      destroy();
   }

   /**
    * @see org.exoplatform.ideall.client.search.file.SearchPresenter.Display#getPathItem()
    */
   public HasValue<String> getPathItem()
   {
      return pathField;
   }

   /**
    * @see org.exoplatform.ideall.client.search.file.SearchPresenter.Display#getSearchContentItem()
    */
   public HasValue<String> getSearchContentItem()
   {
      return contentField;
   }

   /**
    * @see org.exoplatform.ideall.client.search.file.SearchPresenter.Display#getMimeTypeItem()
    */
   public HasValue<String> getMimeTypeItem()
   {
      return mimeTypesField;
   }

   /**
    * @see org.exoplatform.ideall.client.search.file.SearchPresenter.Display#setMimeTypeValues(java.lang.String[])
    */
   public void setMimeTypeValues(String[] mimeTypes)
   {
      mimeTypesField.setValueMap(mimeTypes);
   }

}
