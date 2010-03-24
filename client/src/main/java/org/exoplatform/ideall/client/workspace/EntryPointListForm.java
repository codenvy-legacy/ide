/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.ideall.client.workspace;

import java.util.List;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.component.DialogWindow;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;



/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class EntryPointListForm extends DialogWindow implements EntryPointListPresenter.Display
{
   
   private static final int WIDTH = 500;

   private static final int HEIGHT = 200;

   private static final String TITLE = "Entry Point";

   private EntryPointListPresenter presenter;

   private VLayout vLayout;

   private IButton okButton;

   private IButton cancelButton;

   private ApplicationContext context;
   
   private EntryPointListGrid entryPointListGrid;
   
   
   public EntryPointListForm(HandlerManager eventBus, ApplicationContext context, List<String> entryPoints)
   {
      super(eventBus, WIDTH, HEIGHT);
      
      setTitle(TITLE);
      
      this.eventBus = eventBus;
      this.context = context;
      
      vLayout = new VLayout();
      vLayout.setMargin(10);
      addItem(vLayout);

      createSelectWorkspaceForm();

      createButtonsForm();

      show();
      
      presenter = new EntryPointListPresenter(eventBus, context, entryPoints);
      presenter.bindDisplay(this);
      
   }

   private void createSelectWorkspaceForm()
   {

      entryPointListGrid= new EntryPointListGrid();
      vLayout.addMember(entryPointListGrid);

      Layout l = new Layout();
      l.setHeight(5);
      vLayout.addMember(l);
   }
   
   private void createButtonsForm()
   {
      DynamicForm buttonsForm = new DynamicForm();
      buttonsForm.setPadding(5);
      buttonsForm.setHeight(24);
      buttonsForm.setLayoutAlign(Alignment.CENTER);

      okButton = new IButton("OK");
      okButton.setWidth(90);
      okButton.setHeight(22);
      okButton.setIcon(Images.Buttons.YES);

      cancelButton = new IButton("Cancel");
      cancelButton.setWidth(90);
      cancelButton.setHeight(22);
      cancelButton.setIcon(Images.Buttons.CANCEL);

      ToolbarItem tbi = new ToolbarItem();
      StatefulCanvas delimiter1 = new StatefulCanvas();
      delimiter1.setWidth(3);
      tbi.setButtons(okButton, delimiter1, cancelButton);
      buttonsForm.setFields(tbi);

      buttonsForm.setAutoWidth();

      vLayout.addMember(buttonsForm);
   }
   
   public void closeForm()
   {
      destroy();      
   }

   @Override
   protected void onDestroy()
   {
      presenter.destroy();
      super.onDestroy();
   }
   
   public void disableOkButton()
   {
      okButton.disable();      
   }

   public void enableOkButton()
   {
      okButton.enable();      
   }

   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   public ListGridItem<String> getEntryPoints()
   {
      return entryPointListGrid;
   }

   public HasClickHandlers getOkButton()
   {
      return okButton;
   }
}

