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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.eclipse.jdt.client.JdtClientBundle;
import org.eclipse.jdt.client.packaging.model.JavaProject;
import org.eclipse.jdt.client.packaging.ui.PackageExplorerItemTree;
import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.client.component.IconButton;
import org.exoplatform.gwtframework.ui.client.component.TreeIconPosition;
import org.exoplatform.ide.client.framework.project.PackageExplorerDisplay;
import org.exoplatform.ide.client.framework.project.api.IDEProject;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class PackageExplorerView extends ViewImpl implements PackageExplorerDisplay {

    private static final String                ID       = "idePackageExplorerView";

    private static PackageExplorerViewUiBinder uiBinder = GWT.create(PackageExplorerViewUiBinder.class);

    interface PackageExplorerViewUiBinder extends UiBinder<Widget, PackageExplorerView> {
    }

    private static final String TITLE = "Package Explorer";

    @UiField
    IconButton                  linkWithEditorButton;

    @UiField
    PackageExplorerItemTree     treeGrid;

    @UiField
    HTMLPanel                   projectNotOpenedPanel;

    /**
     * Creates new instance of {@link PackageExplorerView}
     */
    public PackageExplorerView() {
        super(ID, ViewType.NAVIGATION, TITLE, new Image(JdtClientBundle.INSTANCE.packageExplorer()));
        add(uiBinder.createAndBindUi(this));
        setCanShowContextMenu(true);
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.PackageExplorerDisplay#setProject(org.exoplatform.ide.client.framework.project.api.IDEProject)
     */
    @Override
    public void setProject(IDEProject project) {
        if (project != null) {
            projectNotOpenedPanel.setVisible(false);
            treeGrid.setVisible(true);
            treeGrid.setProject((JavaProject)project);
        } else {
            projectNotOpenedPanel.setVisible(true);
            treeGrid.setVisible(false);
            treeGrid.setProject(null);
        }
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.PackageExplorerDisplay#getBrowserTree()
     */
    @Override
    public TreeGridItem<Item> getBrowserTree()
    {
        return treeGrid;
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.PackageExplorerDisplay#getSelectedItem()
     */
    @Override
    public Item getSelectedItem() {
        return treeGrid.getSelectedItem();
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.PackageExplorerDisplay#selectItem(org.exoplatform.ide.vfs.shared.Item)
     */
    @Override
    public boolean selectItem(Item item) {
        return treeGrid.selectItem(item);
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.PackageExplorerDisplay#getLinkWithEditorButton()
     */
    @Override
    public HasClickHandlers getLinkWithEditorButton() {
        return linkWithEditorButton;
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.PackageExplorerDisplay#setLinkWithEditorButtonEnabled(boolean)
     */
    @Override
    public void setLinkWithEditorButtonEnabled(boolean enabled) {
        linkWithEditorButton.setEnabled(enabled);
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.PackageExplorerDisplay#setLinkWithEditorButtonSelected(boolean)
     */
    @Override
    public void setLinkWithEditorButtonSelected(boolean selected) {
        linkWithEditorButton.setSelected(selected);
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.PackageExplorerDisplay#addItemsIcons(java.util.Map)
     */
    @Override
    public void addItemsIcons(Map<Item, Map<TreeIconPosition, ImageResource>> itemsIcons) {
        treeGrid.addItemsIcons(itemsIcons);
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.PackageExplorerDisplay#removeItemIcons(java.util.Map)
     */
    @Override
    public void removeItemIcons(Map<Item, TreeIconPosition> itemsIcons) {
        treeGrid.removeItemIcons(itemsIcons);
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.PackageExplorerDisplay#getTreeChildren(org.exoplatform.ide.vfs.client.model.FolderModel)
     */
    @Override
    public List<Item> getTreeChildren(FolderModel folder) {
        return treeGrid.getTreeChildren(folder);
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.PackageExplorerDisplay#getVisibleItems()
     */
    @Override
    public List<Item> getVisibleItems() {
        return treeGrid.getVisibleItems();
    }

    /**
     * @see org.exoplatform.ide.client.framework.project.PackageExplorerDisplay#refreshTree()
     */
    @Override
    public void refreshTree() {
        treeGrid.refresh();
    }

}
