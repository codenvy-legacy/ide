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
package org.exoplatform.ide.client.workspace.ui;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.IButton;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.discovery.EntryPoint;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SelectWSView extends ViewImpl implements
   org.exoplatform.ide.client.workspace.SelectWorkspacePresenter.Display
{

   private static final int WIDTH = 450;

   private static final int HEIGHT = 200;

   private static SelectWSViewUiBinder uiBinder = GWT.create(SelectWSViewUiBinder.class);

   interface SelectWSViewUiBinder extends UiBinder<Widget, SelectWSView>
   {
   }

   @UiField
   EntryPointListGrid entryPointListGrid;

   @UiField
   IButton okButton;

   @UiField
   IButton cancelButton;

   public SelectWSView()
   {
      super(ID, "popup", "Workspace", new Image(IDEImageBundle.INSTANCE.restServicesDiscovery()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
   }

   @Override
   public ListGridItem<EntryPoint> getWorkspaceListGrid()
   {
      return entryPointListGrid;
   }

   @Override
   public HasClickHandlers getOkButton()
   {
      return okButton;
   }

   @Override
   public HasClickHandlers getCancelButton()
   {
      return cancelButton;
   }

   @Override
   public void setSelectedItem(EntryPoint item)
   {
      entryPointListGrid.selectItem(item);
   }

   @Override
   public void setOkButtonEnabled(boolean enabled)
   {
      okButton.setEnabled(enabled);
   }

}
