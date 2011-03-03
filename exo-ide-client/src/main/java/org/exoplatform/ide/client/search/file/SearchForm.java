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
package org.exoplatform.ide.client.search.file;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;

import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.gwtframework.ui.client.component.TextField;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.framework.vfs.Item;

import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   $
 *
 */
public class SearchForm extends DialogWindow implements SearchPresenter.Display
{
   private static final int WIDTH = 450;

   private static final int HEIGHT = 190;

   private static final String ID = "ideSearchForm";

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

   private final int FORM_WIDTH = 430;

   private TextField contentField;

   private TextField pathField;

   private IButton searchButton;

   private IButton cancelButton;

   private ComboBoxField mimeTypesField;

   private SearchPresenter advancedSearchPresenter;

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

   public SearchForm(HandlerManager eventBus, List<Item> selectedItems, String entryPoint)
   {
      super(eventBus, WIDTH, HEIGHT, ID);
      setTitle("Search");

      VerticalPanel mainLayout = new VerticalPanel();
      mainLayout.setHeight("100%");
      mainLayout.setWidth("100%");
      mainLayout.setSpacing(10);
      mainLayout.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);

      mainLayout.add(createSearchForm());
      mainLayout.add(createButtonsLayout());

      addItem(mainLayout);

      show();

      UIHelper.setAsReadOnly(pathField.getName());

      addCloseClickHandler(new CloseClickHandler()
      {
         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });

      advancedSearchPresenter = new SearchPresenter(eventBus, selectedItems, entryPoint);
      advancedSearchPresenter.bindDisplay(this);
   }

   private VerticalPanel createSearchForm()
   {
      VerticalPanel paramForm = new VerticalPanel();
      paramForm.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
      paramForm.getElement().setId(ID_DYNAMIC_FORM);
      paramForm.setSpacing(3);
     // paramForm.setWidth(FORM_WIDTH + "px");

      pathField = createValueField("Path", PATH_FIELD);

      contentField = createValueField("Containing text", CONTENT_FIELD);

      mimeTypesField = createSelectField("Mime type", MIME_TYPE_FIELD);

      paramForm.add(pathField);
      paramForm.add(contentField);
      //TODO combobox when ready:
      //      paramForm.add(mimeTypesField);

      return paramForm;
   }

   private TextField createValueField(String title, String id)
   {
      TextField textField = new TextField();
      textField.setName(id);
      textField.setTitle("<NOBR>" + title + "</NOBR>");
      textField.setHeight(FIELD_HEIGHT);
      textField.setWidth(FIELD_WIDTH);
      // textField.setSelectOnFocus(true);
      return textField;
   }

   private ComboBoxField createSelectField(String title, String id)
   {
      ComboBoxField comboboxField = new ComboBoxField();
      comboboxField.setName(id);
      comboboxField.setTitle("<NOBR>" + title + "</NOBR>");
      comboboxField.setWidth(FIELD_WIDTH);
      comboboxField.setHeight(FIELD_HEIGHT);
//      comboboxField.setColSpan(2);
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

   public void closeForm()
   {
      destroy();
   }

   /**
    * @see org.exoplatform.ide.client.search.file.SearchPresenter.Display#getPathItem()
    */
   public HasValue<String> getPathItem()
   {
      return pathField;
   }

   /**
    * @see org.exoplatform.ide.client.search.file.SearchPresenter.Display#getSearchContentItem()
    */
   public HasValue<String> getSearchContentItem()
   {
      return contentField;
   }

   /**
    * @see org.exoplatform.ide.client.search.file.SearchPresenter.Display#getMimeTypeItem()
    */
   public HasValue<String> getMimeTypeItem()
   {
      return mimeTypesField;
   }

   /**
    * @see org.exoplatform.ide.client.search.file.SearchPresenter.Display#setMimeTypeValues(java.lang.String[])
    */
   public void setMimeTypeValues(String[] mimeTypes)
   {
      mimeTypesField.setValueMap(mimeTypes);
   }

}
