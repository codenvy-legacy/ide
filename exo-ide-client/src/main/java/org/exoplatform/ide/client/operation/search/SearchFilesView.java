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
package org.exoplatform.ide.client.operation.search;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   $
 *
 */
public class SearchFilesView extends ViewImpl implements org.exoplatform.ide.client.operation.search.SearchFilesPresenter.Display
{
   
   private static final String ID = "ideSearchView";

   private static final int WIDTH = 470;

   private static final int HEIGHT = 220;

   private static final String ID_SEARCH_BUTTON = "ideSearchFormSearchButton";

   private static final String ID_CANCEL_BUTTON = "ideSearchFormCancelButton";

   private static final String CONTENT_FIELD = "ideSearchFormContentField";

   private static final String MIME_TYPE_FIELD = "ideSearchFormMimeTypeField";

   private static final String PATH_FIELD = "ideSearchFormPathField";

   private static final String ID_DYNAMIC_FORM = "ideSearchFormDynamicForm";

   private final int BUTTON_HEIGHT = 22;

   private static int FIELD_WIDTH = 300;

   private static int FIELD_HEIGHT = 18;

   private TextField contentField;

   private TextField pathField;

   private ImageButton searchButton;

   private ImageButton cancelButton;

   private ComboBoxField mimeTypesField;
   
   private static final String TITLE = IDE.NAVIGATION_CONSTANT.searchFilesTitle();
   
   private static final String PATH = IDE.NAVIGATION_CONSTANT.searchFilesPath();
   
   private static final String CONTAINING_TEXT = IDE.NAVIGATION_CONSTANT.searchFilesContainingText();
   
   private static final String MIME_TYPE = IDE.NAVIGATION_CONSTANT.searchFilesMimeType();

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

   public SearchFilesView()
   {
      super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.search()), WIDTH, HEIGHT);

      VerticalPanel mainLayout = new VerticalPanel();
      mainLayout.setHeight("100%");
      mainLayout.setWidth("100%");
      mainLayout.setSpacing(20);
      mainLayout.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

      mainLayout.add(createSearchForm());
      mainLayout.add(createButtonsLayout());

      add(mainLayout);

      UIHelper.setAsReadOnly(pathField.getName());
   }

   private VerticalPanel createSearchForm()
   {
      VerticalPanel paramForm = new VerticalPanel();
      paramForm.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
      paramForm.getElement().setId(ID_DYNAMIC_FORM);
      paramForm.setSpacing(3);

      pathField = createValueField(PATH, PATH_FIELD);

      contentField = createValueField(CONTAINING_TEXT, CONTENT_FIELD);

      HorizontalPanel mimeTypeHorizotalPanel = new HorizontalPanel();
      Label mimeTypeLabel = new Label(MIME_TYPE);
      mimeTypeHorizotalPanel.add(mimeTypeLabel);
      mimeTypesField = createSelectField(MIME_TYPE_FIELD);
      mimeTypeHorizotalPanel.add(mimeTypesField);

      paramForm.add(pathField);
      paramForm.add(contentField);
      paramForm.add(mimeTypeHorizotalPanel);

      return paramForm;
   }

   private TextField createValueField(String title, String id)
   {
      TextField textField = new TextField();
      textField.setName(id);
      textField.setTitle(title);
      textField.setHeight(FIELD_HEIGHT);
      textField.setWidth(FIELD_WIDTH);
      return textField;
   }

   private ComboBoxField createSelectField(String id)
   {
      ComboBoxField comboboxField = new ComboBoxField();
      comboboxField.setName(id);
      comboboxField.setWidth(FIELD_WIDTH);
      comboboxField.setHeight(22);
      return comboboxField;
   }

   private HorizontalPanel createButtonsLayout()
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setHeight(BUTTON_HEIGHT + "px");
      buttonsLayout.setSpacing(5);

      searchButton = new ImageButton(IDE.IDE_LOCALIZATION_CONSTANT.searchButton(), "search");
      searchButton.setId(ID_SEARCH_BUTTON);
      
      cancelButton = new ImageButton(IDE.IDE_LOCALIZATION_CONSTANT.cancelButton(), "cancel");
      cancelButton.setId(ID_CANCEL_BUTTON);

      buttonsLayout.add(searchButton);
      buttonsLayout.add(cancelButton);

      return buttonsLayout;
   }

   /**
    * @see org.exoplatform.ide.client.search.file.AskForNamePresenter.Display#getPathItem()
    */
   public HasValue<String> getPathItem()
   {
      return pathField;
   }

   /**
    * @see org.exoplatform.ide.client.search.file.AskForNamePresenter.Display#getSearchContentItem()
    */
   public HasValue<String> getSearchContentItem()
   {
      return contentField;
   }

   /**
    * @see org.exoplatform.ide.client.search.file.AskForNamePresenter.Display#getMimeTypeItem()
    */
   public HasValue<String> getMimeTypeItem()
   {
      return mimeTypesField;
   }

   /**
    * @see org.exoplatform.ide.client.search.file.AskForNamePresenter.Display#setMimeTypeValues(java.lang.String[])
    */
   public void setMimeTypeValues(String[] mimeTypes)
   {
      mimeTypesField.setValueMap(mimeTypes);
   }

}
