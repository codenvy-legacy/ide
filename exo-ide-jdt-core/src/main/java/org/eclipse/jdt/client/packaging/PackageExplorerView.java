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
package org.eclipse.jdt.client.packaging;

import java.util.List;

import org.eclipse.jdt.client.JdtClientBundle;
import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.client.component.IconButton;
import org.exoplatform.ide.client.framework.project.PackageExplorerDisplay;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
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
public class PackageExplorerView extends ViewImpl implements PackageExplorerDisplay
{

   private static final String ID = "idePackageExplorerView";

   private static PackageExplorerViewUiBinder uiBinder = GWT.create(PackageExplorerViewUiBinder.class);

   interface PackageExplorerViewUiBinder extends UiBinder<Widget, PackageExplorerView>
   {
   }

   private static final String TITLE = "Package Explorer";

   @UiField
   IconButton linkWithEditorButton;

   @UiField
   PEItemTree treeGrid;

   @UiField
   HTMLPanel projectNotOpenedPanel;

   /**
    * 
    */
   public PackageExplorerView()
   {
      super(ID, ViewType.NAVIGATION, TITLE, new Image(JdtClientBundle.INSTANCE.packageExplorer()));
      add(uiBinder.createAndBindUi(this));
      setCanShowContextMenu(true);
   }

   /**
    * @see org.eclipse.jdt.client.packaging.PackageExplorerPresenter.Display#setPackageExplorerTreeVisible(boolean)
    */
   @Override
   public void setPackageExplorerTreeVisible(boolean visible)
   {
      projectNotOpenedPanel.setVisible(!visible);
      treeGrid.setVisible(visible);
   }

   /**
    * @see org.eclipse.jdt.client.packaging.PackageExplorerPresenter.Display#getBrowserTree()
    */
   @Override
   public TreeGridItem<Object> getBrowserTree()
   {
      return treeGrid;
   }

   /**
    * @see org.eclipse.jdt.client.packaging.PackageExplorerPresenter.Display#getSelectedObject()
    */
   @Override
   public Object getSelectedObject()
   {
      return treeGrid.getSelectedObject();
   }

   /**
    * @see org.eclipse.jdt.client.packaging.PackageExplorerPresenter.Display#goToItem(java.util.List)
    */
   @Override
   public void goToItem(List<Object> itemList, boolean collapseBranches)
   {
      treeGrid.goToItem(itemList, collapseBranches);
   }

   /**
    * @see org.eclipse.jdt.client.packaging.PackageExplorerPresenter.Display#getLinkWithEditorButton()
    */
   @Override
   public HasClickHandlers getLinkWithEditorButton()
   {
      return linkWithEditorButton;
   }

   /**
    * @see org.eclipse.jdt.client.packaging.PackageExplorerPresenter.Display#setLinkWithEditorButtonEnabled(boolean)
    */
   @Override
   public void setLinkWithEditorButtonEnabled(boolean enabled)
   {
      linkWithEditorButton.setEnabled(enabled);
   }

   /**
    * @see org.eclipse.jdt.client.packaging.PackageExplorerPresenter.Display#setLinkWithEditorButtonSelected(boolean)
    */
   @Override
   public void setLinkWithEditorButtonSelected(boolean selected)
   {
      linkWithEditorButton.setSelected(selected);
   }

}
