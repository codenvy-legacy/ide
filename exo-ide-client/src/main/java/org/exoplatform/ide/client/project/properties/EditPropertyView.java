/*
 * Copyright (C) 2011 eXo Platform SAS.
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

package org.exoplatform.ide.client.project.properties;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import com.google.gwt.user.client.ui.TextBox;

import com.google.gwt.user.client.ui.Image;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

import com.google.gwt.user.client.ui.Composite;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class EditPropertyView extends ViewImpl implements
   org.exoplatform.ide.client.project.properties.EditPropertyPresenter.Display
{

   public static final String ID = "ideEditProjectPropertyView";

   public static final String TITLE = "Edit Property";

   /**
    * Initial width of this view
    */
   private static final int WIDTH = 420;

   /**
    * Initial height of this view
    */
   private static final int HEIGHT = 200;

   private static EditPropertyViewUiBinder uiBinder = GWT.create(EditPropertyViewUiBinder.class);

   interface EditPropertyViewUiBinder extends UiBinder<Widget, EditPropertyView>
   {
   }

   @UiField
   TextBox nameField;

   @UiField
   TextBox valueField;

   @UiField
   ImageButton okButton;

   @UiField
   ImageButton cancelButton;

   public EditPropertyView()
   {
      super(ID, "modal", TITLE, new Image(IDEImageBundle.INSTANCE.projectProperties()), WIDTH, HEIGHT);
      setCloseOnEscape(true);
      add(uiBinder.createAndBindUi(this));

      nameField.setReadOnly(true);
   }

   @Override
   public HasValue<String> getNameField()
   {
      return nameField;
   }

   @Override
   public HasValue<String> getValueField()
   {
      return valueField;
   }

   @Override
   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

   @Override
   public void setOkButtonText(String text)
   {
      okButton.setText(text);
   }

   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

}
