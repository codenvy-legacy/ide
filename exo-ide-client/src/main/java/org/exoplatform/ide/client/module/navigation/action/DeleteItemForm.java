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

import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

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

   private HLayout hLayout;

   private String prompt;

   private IButton deleteButton;

   private IButton cancelButton;

   private DeleteItemPresenter presenter;

   public DeleteItemForm(HandlerManager eventBus, List<Item> selectedItems, Map<String, File> openedFiles, Map<String, String> lockTokens)
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

      hLayout = new HLayout();
      addItem(hLayout);
      //hLayout.setBackgroundColor("#FFEEAA");

      createImageLayout();
      createPromptLayout();

      show();

      deleteButton.focus();
      
      addCloseClickHandler(new CloseClickHandler()
      {
         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });

      presenter = new DeleteItemPresenter(eventBus, selectedItems, openedFiles, lockTokens);
      presenter.bindDisplay(this);
   }

   private void createImageLayout()
   {
      Layout imageLayot = new Layout();
      imageLayot.setWidth(64);
      hLayout.addMember(imageLayot);

      Canvas imageCanvas = new Canvas();
      imageCanvas.setLayoutAlign(Alignment.CENTER);
      imageCanvas.setLayoutAlign(VerticalAlignment.TOP);
      imageCanvas.setContents("<img src=\"" + Images.Dialogs.ASK + "\" />");
      imageCanvas.setWidth(32);
      imageCanvas.setHeight(32);
      imageCanvas.setLeft(20);
      imageCanvas.setTop(22);
      imageLayot.addChild(imageCanvas);
   }

   private void createPromptLayout()
   {
      VLayout vLayout = new VLayout();
      hLayout.addMember(vLayout);
      vLayout.setMargin(10);

      Layout promptLayout = new Layout();
      promptLayout.setLayoutAlign(VerticalAlignment.CENTER);
      promptLayout.setContents(prompt);
      vLayout.addMember(promptLayout);

      DynamicForm buttonsForm = new DynamicForm();
      buttonsForm.setPadding(5);
      buttonsForm.setHeight(24);
      buttonsForm.setLayoutAlign(Alignment.CENTER);

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

      ToolbarItem tbi = new ToolbarItem();
      StatefulCanvas delimiter1 = new StatefulCanvas();
      delimiter1.setWidth(3);
      tbi.setButtons(deleteButton, delimiter1, cancelButton);
      buttonsForm.setFields(tbi);

      buttonsForm.setAutoWidth();
      vLayout.addMember(buttonsForm);
   }

   @Override
   protected void onDestroy()
   {
      presenter.destroy();
      super.onDestroy();
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
