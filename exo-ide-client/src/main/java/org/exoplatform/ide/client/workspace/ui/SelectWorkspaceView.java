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
package org.exoplatform.ide.client.workspace.ui;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.discovery.EntryPoint;
import org.exoplatform.ide.client.framework.ui.api.ViewType;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class SelectWorkspaceView extends ViewImpl implements
   org.exoplatform.ide.client.workspace.SelectWorkspacePresenter.Display
{

   private static final int DEFAULT_WIDTH = 500;

   private static final int DEFAULT_HEIGHT = 200;

   private final String ID_OK_BUTTON = "ideSelectWorkspaceFormOkButton";

   private final String ID_CANCEL_BUTTON = "ideSelectWorkspaceFormCancelButton";

   private IButton okButton;

   private IButton cancelButton;

   private EntryPointListGrid entryPointListGrid;

   public SelectWorkspaceView()
   {
      super(ID, ViewType.MODAL, "Workspace", new Image(IDEImageBundle.INSTANCE.workspace()), DEFAULT_WIDTH,
         DEFAULT_HEIGHT);

      VerticalPanel vLayout = new VerticalPanel();
      vLayout.setBorderWidth(0);
      vLayout.setWidth("100%");
      vLayout.setHeight("100%");
      vLayout.setSpacing(10);
      vLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      add(vLayout);

      entryPointListGrid = new EntryPointListGrid();
      entryPointListGrid.setWidth("100%");
      entryPointListGrid.setHeight("100px");
      vLayout.add(entryPointListGrid);

      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setHeight(22 + "px");
      buttonsLayout.setSpacing(5);

      okButton = new IButton("OK");
      okButton.setID(ID_OK_BUTTON);
      okButton.setWidth(90);
      okButton.setHeight(22);
      okButton.setIcon(Images.Buttons.YES, Images.Buttons.YES);

      cancelButton = new IButton("Cancel");
      cancelButton.setID(ID_CANCEL_BUTTON);
      cancelButton.setWidth(90);
      cancelButton.setHeight(22);
      cancelButton.setIcon(Images.Buttons.CANCEL, Images.Buttons.CANCEL);

      buttonsLayout.add(okButton);
      buttonsLayout.add(cancelButton);

      vLayout.add(buttonsLayout);
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

   public ListGridItem<EntryPoint> getWorkspaceListGrid()
   {
      return entryPointListGrid;
   }

   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

   public void setSelectedItem(EntryPoint item)
   {
      entryPointListGrid.getCellTable().getSelectionModel().setSelected(item, true);
   }
   
}
