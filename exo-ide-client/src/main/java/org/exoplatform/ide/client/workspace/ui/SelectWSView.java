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
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.discovery.EntryPoint;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
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

   private static final int DEFAULT_WIDTH = 400;

   private static final int DEFAULT_HEIGHT = 250;

   private static SelectWSViewUiBinder uiBinder = GWT.create(SelectWSViewUiBinder.class);

   interface SelectWSViewUiBinder extends UiBinder<Widget, SelectWSView>
   {
   }

   public SelectWSView()
   {
      super(ID, "popup", "Workspace", new Image(IDEImageBundle.INSTANCE.restServicesDiscovery()), DEFAULT_WIDTH,
         DEFAULT_HEIGHT, true);
      add(uiBinder.createAndBindUi(this));
   }

   @Override
   public ListGridItem<EntryPoint> getWorkspaceListGrid()
   {
      return null;
   }

   @Override
   public HasClickHandlers getOkButton()
   {
      return null;
   }

   @Override
   public HasClickHandlers getCancelButton()
   {
      return null;
   }

   @Override
   public void enableOkButton()
   {
   }

   @Override
   public void disableOkButton()
   {
   }

   @Override
   public void setSelectedItem(EntryPoint item)
   {
   }

}
