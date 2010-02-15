/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
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
package org.exoplatform.ideall.client.search;

import org.exoplatform.gwtframework.ui.component.IButton;
import org.exoplatform.gwtframework.ui.component.TextField;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.component.DialogWindow;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SpacerItem;
import com.smartgwt.client.widgets.form.fields.StaticTextItem;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class SearchForm extends DialogWindow implements SearchPresenter.Display
{

   private static final int WIDTh = 420;

   private static final int HEIGHT = 170;

   private static int FIELD_WIDTH = 350;

   private TextField contentField;

   private IButton searchButton;

   private IButton cancelButton;

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

   public SearchForm(HandlerManager eventBus, String path)
   {
      super(eventBus, WIDTh, HEIGHT);
      setTitle("Search");

      createValueFields();
      createButtonsForm();

      show();

      SearchPresenter searchPresenter = new SearchPresenter(eventBus, path);
      searchPresenter.bindDisplay(this);
   }

   private void createValueFields()
   {
      StaticTextItem contentFieldTitle = new StaticTextItem();
      contentFieldTitle.setShowTitle(false);
      contentFieldTitle.setValue("Text:");
      contentFieldTitle.setColSpan(2);
      contentFieldTitle.setWrap(false);

      SpacerItem spacerItem = new SpacerItem();
      spacerItem.setHeight(5);

      contentField = new TextField();
      contentField.setShowTitle(false);
      contentField.setColSpan(2);
      contentField.setWidth(FIELD_WIDTH);

      DynamicForm paramForm = new DynamicForm();
      //paramForm.setCellBorder(1);
      paramForm.setLayoutAlign(Alignment.CENTER);
      paramForm.setLayoutAlign(VerticalAlignment.CENTER);
      paramForm.setMargin(15);
      paramForm.setFields(contentFieldTitle, spacerItem, contentField);
      paramForm.setAutoWidth();
      addItem(paramForm);

   }

   private void createButtonsForm()
   {
      DynamicForm buttonsForm = new DynamicForm();
      buttonsForm.setPadding(5);
      buttonsForm.setHeight(24);
      buttonsForm.setLayoutAlign(Alignment.CENTER);

      searchButton = new IButton("Search");
      searchButton.setWidth(90);
      searchButton.setHeight(22);
      searchButton.setIcon(Images.Buttons.SEARCH);

      cancelButton = new IButton("Cancel");
      cancelButton.setWidth(90);
      cancelButton.setHeight(22);
      cancelButton.setIcon(Images.Buttons.NO);

      ToolbarItem tbi = new ToolbarItem();
      StatefulCanvas delimiter1 = new StatefulCanvas();
      delimiter1.setWidth(3);
      tbi.setButtons(searchButton, delimiter1, cancelButton);
      buttonsForm.setFields(tbi);

      buttonsForm.setAutoWidth();
      addItem(buttonsForm);
   }

   public void closeForm()
   {
      destroy();
   }

}
