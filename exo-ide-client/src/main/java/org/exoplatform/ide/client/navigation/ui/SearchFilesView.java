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
package org.exoplatform.ide.client.navigation.ui;

import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.component.TitleOrientation;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   $
 *
 */
public class SearchFilesView extends ViewImpl implements org.exoplatform.ide.client.navigation.SearchPresenter.Display
{

   private static final int WIDTH = 450;

   private static final int HEIGHT = 190;

   private static final String ID_SEARCH_BUTTON = "ideSearchFormSearchButton";

   private static final String ID_CANCEL_BUTTON = "ideSearchFormCancelButton";

   private static final String CONTENT_FIELD = "ideSearchFormContentField";

   private static final String MIME_TYPE_FIELD = "ideSearchFormMimeTypeField";

   private static final String PATH_FIELD = "ideSearchFormPathField";

   private static final String ID_DYNAMIC_FORM = "ideSearchFormDynamicForm";

   private final int BUTTON_WIDTH = 90;

   private final int BUTTON_HEIGHT = 22;

   private static int FIELD_WIDTH = 300;

   private static int FIELD_HEIGHT = 20;

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

   public SearchFilesView()
   {
      super(ID, "popup", "Search", new Image(IDEImageBundle.INSTANCE.search()), WIDTH, HEIGHT);

      VerticalPanel mainLayout = new VerticalPanel();
      mainLayout.setHeight("100%");
      mainLayout.setWidth("100%");
      mainLayout.setSpacing(10);
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

      pathField = createValueField("Path", PATH_FIELD);

      contentField = createValueField("Containing text", CONTENT_FIELD);

      mimeTypesField = createSelectField("Mime type", MIME_TYPE_FIELD);

      paramForm.add(pathField);
      paramForm.add(contentField);
      paramForm.add(mimeTypesField);

      return paramForm;
   }

   private TextField createValueField(String title, String id)
   {
      TextField textField = new TextField();
      textField.setName(id);
      textField.setTitle("<NOBR>" + title + "</NOBR>");
      textField.setHeight(FIELD_HEIGHT);
      textField.setWidth(FIELD_WIDTH);
      return textField;
   }

   private ComboBoxField createSelectField(String title, String id)
   {
      ComboBoxField comboboxField = new ComboBoxField();
      comboboxField.setName(id);
      comboboxField.setShowTitle(true);
      comboboxField.setTitle("<NOBR>" + title + "</NOBR>");
      comboboxField.setWidth(FIELD_WIDTH);
      comboboxField.setHeight(FIELD_HEIGHT);
      comboboxField.setTitleOrientation(TitleOrientation.LEFT);
      return comboboxField;
   }

   private HorizontalPanel createButtonsLayout()
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setHeight(BUTTON_HEIGHT + "px");
      buttonsLayout.setSpacing(5);

      searchButton = createButton("Search", Images.Buttons.SEARCH, ID_SEARCH_BUTTON);
      cancelButton = createButton("Cancel", Images.Buttons.CANCEL, ID_CANCEL_BUTTON);

      buttonsLayout.add(searchButton);
      buttonsLayout.add(cancelButton);

      return buttonsLayout;
   }

   private IButton createButton(String title, String icon, String id)
   {
      IButton button = new IButton(title);
      button.setID(id);
      button.setIcon(icon);
      button.setWidth(BUTTON_WIDTH);
      button.setHeight(BUTTON_HEIGHT);
      return button;
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
