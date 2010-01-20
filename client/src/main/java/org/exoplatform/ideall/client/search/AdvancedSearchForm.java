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
package org.exoplatform.ideall.client.search;

import org.exoplatform.gwt.commons.smartgwt.component.ComboBoxField;
import org.exoplatform.gwt.commons.smartgwt.component.IButton;
import org.exoplatform.gwt.commons.smartgwt.component.TextField;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.component.DialogWindow;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   $
 *
 */
public class AdvancedSearchForm extends DialogWindow implements AdvancedSearchPresenter.Display
{
   private static final int WIDTH = 320;

   private static final int HEIGHT = 350;

   private final int BUTTON_WIDTH = 90;

   private final int BUTTON_HEIGHT = 22;

   private static int FIELD_WIDTH = 250;

   private static int FIELD_HEIGHT = 20;

   private TextField contentField;

   private TextField pathField;

   private TextField fileNameField;

   private IButton searchButton;

   private IButton cancelButton;

   private ComboBoxField mimeTypesField;

   private FormItem[] paramFormItemArray;

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

   public AdvancedSearchForm(HandlerManager eventBus, ApplicationContext context)
   {
      super(eventBus, WIDTH, HEIGHT);
      setTitle("Advanced Search");

      VLayout mainLayout = new VLayout();
      mainLayout.setWidth100();
      mainLayout.setHeight100();
      mainLayout.setPadding(10);
      mainLayout.setMembersMargin(15);

      mainLayout.addMember(createSearchForm());
      mainLayout.addMember(createButtonsLayout());

      addItem(mainLayout);

      show();
      AdvancedSearchPresenter advancedSearchPresenter = new AdvancedSearchPresenter(eventBus, context);
      advancedSearchPresenter.bindDisplay(this);
   }

   private DynamicForm createSearchForm()
   {
      DynamicForm paramForm = new DynamicForm();
      paramForm.setLayoutAlign(Alignment.CENTER);
      paramForm.setPadding(10);
      paramForm.setWidth(FIELD_WIDTH + 20);
      paramForm.setIsGroup(true);
      paramForm.setGroupTitle("Search parameters");

      paramFormItemArray = new FormItem[12];

      contentField = new TextField();
      createValueField("Text:", contentField, 0);

      pathField = new TextField();
      createValueField("Path:", pathField, 3);

      fileNameField = new TextField();
      createValueField("File name:", fileNameField, 6);

      createSelectField(9);

      paramForm.setItems(paramFormItemArray);
      paramForm.setAutoFocus(true);

      return paramForm;
   }

   private void createValueField(String title, TextField textField, int position)
   {
      StaticTextItem fieldTitle = new StaticTextItem();
      fieldTitle.setShowTitle(false);
      fieldTitle.setValue(title);
      fieldTitle.setColSpan(2);
      fieldTitle.setWrap(false);

      SpacerItem fieldSpacerItem = new SpacerItem();
      fieldSpacerItem.setHeight(10);

      textField.setShowTitle(false);
      textField.setColSpan(2);
      textField.setHeight(FIELD_HEIGHT);
      textField.setWidth(FIELD_WIDTH);
      textField.setSelectOnFocus(true);

      paramFormItemArray[position] = fieldTitle;
      paramFormItemArray[position + 1] = textField;
      paramFormItemArray[position + 2] = fieldSpacerItem;
   }

   private void createSelectField(int position)
   {
      StaticTextItem title = new StaticTextItem();
      title.setValue("Mime Type:");
      title.setShowTitle(false);
      title.setColSpan(2);

      mimeTypesField = new ComboBoxField();
      mimeTypesField.setWidth(FIELD_WIDTH);
      mimeTypesField.setHeight(FIELD_HEIGHT);
      mimeTypesField.setShowTitle(false);
      mimeTypesField.setColSpan(2);

      SpacerItem fieldSpacerItem = new SpacerItem();
      fieldSpacerItem.setHeight(10);

      paramFormItemArray[position] = title;
      paramFormItemArray[position + 1] = mimeTypesField;
      paramFormItemArray[position + 2] = fieldSpacerItem;

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
    * @see org.exoplatform.ideall.client.search.AdvancedSearchPresenter.Display#getPathItem()
    */
   public HasValue<String> getPathItem()
   {
      return pathField;
   }

   /**
    * @see org.exoplatform.ideall.client.search.AdvancedSearchPresenter.Display#getSearchContentItem()
    */
   public HasValue<String> getSearchContentItem()
   {
      return contentField;
   }

   /**
    * @see org.exoplatform.ideall.client.search.AdvancedSearchPresenter.Display#getFileNameItem()
    */
   public HasValue<String> getFileNameItem()
   {
      return fileNameField;
   }

   /**
    * @see org.exoplatform.ideall.client.search.AdvancedSearchPresenter.Display#getMimeTypeItem()
    */
   public HasValue<String> getMimeTypeItem()
   {
      return mimeTypesField;
   }

   /**
    * @see org.exoplatform.ideall.client.search.AdvancedSearchPresenter.Display#setMimeTypeValues(java.lang.String[])
    */
   public void setMimeTypeValues(String[] mimeTypes)
   {
      mimeTypesField.setValueMap(mimeTypes);
   }

   /**
    * @see org.exoplatform.ideall.client.search.AdvancedSearchPresenter.Display#disablePathItem()
    */
   public void disablePathItem()
   {
      pathField.disable();
   }
}
