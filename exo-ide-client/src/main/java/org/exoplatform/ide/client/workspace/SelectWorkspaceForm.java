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
package org.exoplatform.ide.client.workspace;

import com.smartgwt.client.widgets.layout.HLayout;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.discovery.EntryPoint;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.framework.vfs.File;

import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class SelectWorkspaceForm extends DialogWindow implements SelectWorkspacePresenter.Display
{

   private static final int WIDTH = 500;

   private static final int HEIGHT = 200;

   private static final String ID = "ideSelectWorkspaceForm";
   
   private final String ID_OK_BUTTON = "ideSelectWorkspaceFormOkButton";
   
   private final String ID_CANCEL_BUTTON = "ideSelectWorkspaceFormCancelButton";

   private static final String TITLE = "Workspace";

   private SelectWorkspacePresenter presenter;

   private VLayout vLayout;

   private IButton okButton;

   private IButton cancelButton;

   private EntryPointListGrid entryPointListGrid;

   public SelectWorkspaceForm(HandlerManager eventBus, ApplicationSettings applicationSettings, List<EntryPoint> entryPointList,
      Map<String, File> openedFiles, Map<String, String> lockTokens)
   {
      super(eventBus, WIDTH, HEIGHT, ID);

      setTitle(TITLE);

      this.eventBus = eventBus;

      vLayout = new VLayout();
      vLayout.setMargin(10);
      addItem(vLayout);

      createSelectWorkspaceForm();

      createButtonsForm();

      addCloseClickHandler(new CloseClickHandler()
      {
         public void onCloseClick(CloseClientEvent event)
         {
            destroy();
         }
      });
      
      show();

      presenter = new SelectWorkspacePresenter(eventBus, applicationSettings, entryPointList, openedFiles, lockTokens);
      presenter.bindDisplay(this);
   }

   private void createSelectWorkspaceForm()
   {

      entryPointListGrid = new EntryPointListGrid();
      vLayout.addMember(entryPointListGrid);

      Layout l = new Layout();
      l.setHeight(5);
      vLayout.addMember(l);
   }

   private void createButtonsForm()
   {
      HLayout buttonsLayout = new HLayout();
      buttonsLayout.setAutoWidth();
      buttonsLayout.setHeight(22);
      buttonsLayout.setLayoutAlign(Alignment.CENTER);
      buttonsLayout.setMembersMargin(5);

      okButton = new IButton("OK");
      okButton.setID(ID_OK_BUTTON);
      okButton.setWidth(90);
      okButton.setHeight(22);
      okButton.setIcon(Images.Buttons.YES);

      cancelButton = new IButton("Cancel");
      cancelButton.setID(ID_CANCEL_BUTTON);
      cancelButton.setWidth(90);
      cancelButton.setHeight(22);
      cancelButton.setIcon(Images.Buttons.CANCEL);

      buttonsLayout.addMember(okButton);
      buttonsLayout.addMember(cancelButton);

      vLayout.addMember(buttonsLayout);
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

   public ListGridItem<EntryPoint> getEntryPoints()
   {
      return entryPointListGrid;
   }

   public HasClickHandlers getOkButton()
   {
      return okButton;
   }
}
