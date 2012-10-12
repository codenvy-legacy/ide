/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.client.project.packaging;

import java.util.List;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class PackageExplorerView extends ViewImpl implements
   org.exoplatform.ide.client.project.packaging.PackageExplorerPresenter.Display
{

   private static final String ID = "idePackageExplorerView";

   private static PackageExplorerViewUiBinder uiBinder = GWT.create(PackageExplorerViewUiBinder.class);

   interface PackageExplorerViewUiBinder extends UiBinder<Widget, PackageExplorerView>
   {
   }

   private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.packageExplorerViewTitle();

//   @UiField
//   IconButton linkWithEditorButton;

   @UiField
   //ItemTree treeGrid;
   //PackageExplorerItemTree treeGrid;
   PEItemTree treeGrid;

   @UiField
   HTMLPanel projectNotOpenedPanel;

   public PackageExplorerView()
   {
      super(ID, ViewType.NAVIGATION, TITLE, new Image(IDEImageBundle.INSTANCE.packageExplorer()));
      add(uiBinder.createAndBindUi(this));
   }

   @Override
   public void setPackageExplorerTreeVisible(boolean visible)
   {
      projectNotOpenedPanel.setVisible(!visible);
      treeGrid.setVisible(visible);
   }

   @Override
   public TreeGridItem<Object> getBrowserTree()
   {
      return treeGrid;
   }

//   @Override
//   public void selectItem(Object item)
//   {
//   }

   @Override
   public Object getSelectedObject()
   {
      return treeGrid.getSelectedObject();
   }

   @Override
   public void goToItem(List<Object> itemList)
   {
      treeGrid.goToItem(itemList);
   }

}
