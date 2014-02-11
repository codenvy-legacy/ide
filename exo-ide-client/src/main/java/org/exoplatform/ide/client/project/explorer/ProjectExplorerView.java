/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.client.project.explorer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.client.component.IconButton;
import org.exoplatform.gwtframework.ui.client.component.TreeIconPosition;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay;
import org.exoplatform.ide.client.framework.project.api.IDEProject;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.project.explorer.ui.ProjectExplorerItemTree;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.List;
import java.util.Map;

/**
 * @author Vitaliy Gulyy
 */

public class ProjectExplorerView extends ViewImpl implements ProjectExplorerDisplay {

    public static final String ID = "ideTinyProjectExplorerView";

    /** Initial width of this view */
    private static final int WIDTH = 250;

    /** Initial height of this view */
    private static final int HEIGHT = 450;

    private static ProjectExplorerViewUiBinder uiBinder = GWT.create(ProjectExplorerViewUiBinder.class);

    interface ProjectExplorerViewUiBinder extends UiBinder<Widget, ProjectExplorerView> {
    }

    @UiField
    ProjectExplorerItemTree treeGrid;

    @UiField
    ProjectsListGrid projectsListGrid;

    @UiField
    IconButton linkWithEditorButton;

    private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.projectExplorerViewTitle();

    public ProjectExplorerView() {
        super(ID, "navigation", TITLE, new Image(IDEImageBundle.INSTANCE.projectExplorer()), WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));
        setCanShowContextMenu(true);
    }

    @Override
    public void activate() {
        super.activate();

        if (treeGrid.isVisible()) {
            treeGrid.getElement().focus();
            treeGrid.updateHighlighter();
        } else {
            projectsListGrid.getElement().focus();
        }
    }

    @Override
    public TreeGridItem<Item> getProjectTree() {
        return treeGrid;
    }

    @Override
    public Item getSelectedItem() {
        return treeGrid.getSelectedItem();
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay#getLinkWithEditorButton() */
    @Override
    public HasClickHandlers getLinkWithEditorButton() {
        return linkWithEditorButton;
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay#setLinkWithEditorButtonEnabled(boolean) */
    @Override
    public void setLinkWithEditorButtonEnabled(boolean enabled) {
        linkWithEditorButton.setEnabled(enabled);
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay#setLinkWithEditorButtonSelected(boolean) */
    @Override
    public void setLinkWithEditorButtonSelected(boolean selected) {
        linkWithEditorButton.setSelected(selected);
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay#getProjectsListGrid() */
    @Override
    public ProjectsListGrid getProjectsListGrid() {
        return projectsListGrid;
    }

    /** @see org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay#getSelectedProjects() */
    @Override
    public List<ProjectModel> getSelectedProjects() {
        return projectsListGrid.getSelectedItems();
    }

    @Override
    public void setProject(ProjectModel project) {
        if (project != null && project instanceof IDEProject) {
            projectsListGrid.setVisible(false);
            treeGrid.setVisible(true);
            treeGrid.setProject((IDEProject)project);
        } else {
            treeGrid.setProject(null);
            treeGrid.setVisible(false);
            projectsListGrid.setVisible(true);
        }
    }

    @Override
    public boolean selectItem(Item item) {
        return treeGrid.selectItem(item);
    }

    @Override
    public List<Item> getVisibleItems() {
        return treeGrid.getVisibleItems();
    }

    @Override
    public void refreshTree() {
        treeGrid.refresh();
    }

    @Override
    public void addItemsIcons(Map<Item, Map<TreeIconPosition, ImageResource>> itemsIcons) {
        treeGrid.addItemsIcons(itemsIcons);
    }

    @Override
    public void removeItemIcons(Map<Item, TreeIconPosition> itemsIcons) {
        treeGrid.removeItemIcons(itemsIcons);
    }

}
