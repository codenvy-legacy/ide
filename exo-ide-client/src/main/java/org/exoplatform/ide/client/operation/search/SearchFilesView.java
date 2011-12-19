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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   $
 *
 */
public class SearchFilesView extends ViewImpl implements
   org.exoplatform.ide.client.operation.search.SearchFilesPresenter.Display
{

   private static final String ID = "ideSearchView";

   private static final int WIDTH = 470;

   private static final int HEIGHT = 210;

   private static final String ID_SEARCH_BUTTON = "ideSearchFormSearchButton";

   private static final String ID_CANCEL_BUTTON = "ideSearchFormCancelButton";

   private static final String CONTENT_FIELD = "ideSearchFormContentField";

   private static final String MIME_TYPE_FIELD = "ideSearchFormMimeTypeField";

   private static final String PATH_FIELD = "ideSearchFormPathField";

   @UiField
   TextInput contentField;

   @UiField
   TextInput pathField;

   @UiField
   ImageButton searchButton;

   @UiField
   ImageButton cancelButton;

   @UiField
   ComboBoxField mimeTypesField;

   private static final String TITLE = IDE.NAVIGATION_CONSTANT.searchFilesTitle();

   private static SearchFilesViewUiBinder uiBinder = GWT.create(SearchFilesViewUiBinder.class);

   interface SearchFilesViewUiBinder extends UiBinder<Widget, SearchFilesView>
   {
   }

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
      setCloseOnEscape(true);

      add(uiBinder.createAndBindUi(this));

      pathField.setName(PATH_FIELD);
      contentField.setName(CONTENT_FIELD);
      mimeTypesField.setName(MIME_TYPE_FIELD);
      searchButton.setId(ID_SEARCH_BUTTON);
      cancelButton.setId(ID_CANCEL_BUTTON);
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
