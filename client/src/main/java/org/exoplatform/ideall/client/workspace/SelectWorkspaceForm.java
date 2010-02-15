/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.workspace;

import org.exoplatform.gwtframework.ui.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.component.IButton;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.component.DialogWindow;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class SelectWorkspaceForm extends DialogWindow implements SelectWorkspacePresenter.Display
{

   private static final int WIDTH = 550;

   private static final int HEIGHT = 350;

   private static final String TITLE = "Workspace";

   private SelectWorkspacePresenter presenter;

   private VLayout vLayout;

   private IButton okButton;

   private IButton cancelButton;

   private ApplicationContext context;

   private JCRConfigurationItemTreeGrid jcrItemsTreeGrid;

   public SelectWorkspaceForm(HandlerManager eventBus, ApplicationContext context)
   {
      super(eventBus, WIDTH, HEIGHT);

      this.eventBus = eventBus;
      this.context = context;

      setTitle(TITLE);

      vLayout = new VLayout();
      vLayout.setMargin(10);
      addItem(vLayout);

      createSelectWorkspaceForm();

      createButtonsForm();

      show();

      presenter = new SelectWorkspacePresenter(eventBus, context);
      presenter.bindDisplay(this);
   }

   @Override
   protected void onDestroy()
   {
      presenter.destroy();
      super.onDestroy();
   }

   public void closeForm()
   {
      destroy();
   }

   private void createSelectWorkspaceForm()
   {

      jcrItemsTreeGrid = new JCRConfigurationItemTreeGrid();
      vLayout.addMember(jcrItemsTreeGrid);

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

   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

   public TreeGridItem<JCRConfigurationItem> getJCRItemsTreeGrid()
   {
      return jcrItemsTreeGrid;
   }

   public void disableJCRItemsTreeGrid()
   {
      jcrItemsTreeGrid.disable();
   }

   public void enableJCRItemsTreeGrid()
   {
      jcrItemsTreeGrid.enable();
   }

   public void disableOkButton()
   {
      okButton.disable();
   }

   public void enableOkButton()
   {
      okButton.enable();
   }

   public HasDoubleClickHandlers getJCRItemsTreeGridClickable()
   {
      return jcrItemsTreeGrid;
   }

}
