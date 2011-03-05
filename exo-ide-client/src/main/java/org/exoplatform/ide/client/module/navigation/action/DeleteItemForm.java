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
package org.exoplatform.ide.client.module.navigation.action;

import com.google.gwt.user.client.ui.HasVerticalAlignment;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;

import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class DeleteItemForm extends DialogWindow implements DeleteItemPresenter.Display
{

   public static final int WIDTH = 500;

   public static final int HEIGHT = 130;

   public static final String ID = "ideDeleteItemForm";

   public static final String ID_OK_BUTTON = "ideDeleteItemFormOkButton";

   public static final String ID_CANCEL_BUTTON = "ideDeleteItemFormCancelButton";

   private VerticalPanel mainLayout;
   
   private HorizontalPanel infoLayout;

   private String prompt;

   private IButton deleteButton;

   private IButton cancelButton;

   private DeleteItemPresenter presenter;

   public DeleteItemForm(HandlerManager eventBus, List<Item> selectedItems, Map<String, File> openedFiles,
      Map<String, String> lockTokens)
   {
      super(eventBus, WIDTH, HEIGHT, ID);

      if (selectedItems.size() == 1)
      {
         prompt = "<br>Do you want to delete  <b>" + selectedItems.get(0).getName() + "</b> ?";
      }
      else
      {
         prompt = "<br>Do you want to delete <b>" + selectedItems.size() + "</b> items?";
      }
      setTitle("Delete Item(s)");

      mainLayout = new VerticalPanel();
      mainLayout.setWidth("100%");
      mainLayout.setHeight("100%");
      mainLayout.setSpacing(10);
      mainLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      mainLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

      setWidget(mainLayout);
      
      infoLayout = new HorizontalPanel();
      infoLayout.setWidth("100%");
      infoLayout.setHeight(32 + "px");
      infoLayout.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      createImage();
      createTextLayout(prompt);
      
      mainLayout.add(infoLayout);
      mainLayout.add(createButtonsLayout());

      show();

      deleteButton.focus();
      presenter = new DeleteItemPresenter(eventBus, selectedItems, openedFiles, lockTokens);
      presenter.bindDisplay(this);
   }

   private void createImage()
   {
      Image image = new Image(Images.Dialogs.ASK);
      image.setWidth(32 + "px");
      image.setHeight(32 + "px");
      infoLayout.add(image);
      infoLayout.setCellHorizontalAlignment(image, HasHorizontalAlignment.ALIGN_CENTER);
   }

   private void createTextLayout(String text)
   {
      Label label = new Label();
      label.getElement().setInnerHTML(text);
      infoLayout.add(label);
      infoLayout.setCellHorizontalAlignment(label, HasHorizontalAlignment.ALIGN_LEFT);
   }

   private HorizontalPanel createButtonsLayout()
   {
      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setHeight(22 + "px");
      buttonsLayout.setSpacing(5);

      deleteButton = new IButton("Yes");
      deleteButton.setID(ID_OK_BUTTON);
      deleteButton.setWidth(90);
      deleteButton.setHeight(22);
      deleteButton.setIcon(Images.Buttons.YES);

      cancelButton = new IButton("No");
      cancelButton.setID(ID_CANCEL_BUTTON);
      cancelButton.setWidth(90);
      cancelButton.setHeight(22);
      cancelButton.setIcon(Images.Buttons.NO);

      buttonsLayout.add(deleteButton);
      buttonsLayout.add(cancelButton);

      return buttonsLayout;
   }

   @Override
   public void destroy()
   {
      presenter.destroy();
      super.destroy();
   }

   public void closeDisplay()
   {
      destroy();
   }

   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   public HasClickHandlers getDeleteButton()
   {
      return deleteButton;
   }

   public void closeForm()
   {
      destroy();
   }

   public void hideForm()
   {
      hide();

   }

}
