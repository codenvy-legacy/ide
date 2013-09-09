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
package org.exoplatform.ide.client.framework.ui;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.*;

import org.exoplatform.gwtframework.ui.client.component.TreeIcon;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.framework.util.ImageUtil;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.Map;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class ProjectTreeItem extends TreeItem {

    private Item item;

    private String prefixId;

    private Map<String, String> locktokens;

    public ProjectTreeItem(Item item, String prefixId, Map<String, String> locktokens) {
        this.item = item;
        this.prefixId = prefixId;
        this.locktokens = locktokens;
        setUserObject(item);
        render();
    }

    public void render() {
        ImageResource icon = getItemIcon(item);
        String title = getTitle(item);
        Widget widget = createItemWidget(icon, title);
        setWidget(widget);

        if (item instanceof FolderModel) {
            boolean opened = getState();
            if (!((FolderModel)item).getChildren().getItems().isEmpty() && !opened) {
                addItem("");
            }
        }

        getElement().setId(prefixId + Utils.md5(item.getPath()));
    }

    public void setItem(Item item) {
        this.item = item;
        setUserObject(item);
        render();
    }

    /**
     * Creates widget for tree node
     *
     * @param icon
     * @param text
     * @return
     */
    private Widget createItemWidget(ImageResource icon, String text) {
        Grid grid = new Grid(1, 2);
        grid.setWidth("100%");

        // Image i = new Image(icon);
        TreeIcon i = new TreeIcon(icon);
        i.setHeight("16px");
        grid.setWidget(0, 0, i);
        // Label l = new Label(text, false);
        HTMLPanel l = new HTMLPanel("div", text);
        l.setStyleName("ide-Tree-label");
        grid.setWidget(0, 1, l);

        grid.getCellFormatter().setWidth(0, 0, "16px");
        grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setWidth(0, 1, "100%");
        // grid.getCellFormatter().addStyleName(0, 1, "ide-Tree-label");
        DOM.setStyleAttribute(grid.getElement(), "display", "block");
        return grid;
    }

    /**
     * Select icon for item
     *
     * @param item
     * @return {@link ImageResource} of item icon
     */
    public ImageResource getItemIcon(Item item) {
        if (item instanceof ProjectModel) {
            return ProjectResolver.getImageForProject(((ProjectModel)item).getProjectType());
        } else
            return ImageUtil.getIcon(item.getMimeType());
    }


    private String getTitle(Item item) {
        if (locktokens == null) {
            return (item.getName() == null || item.getName().isEmpty()) ? "/" : item.getName();
        }

        String title = "";
        if (item instanceof FileModel && ((FileModel)item).isLocked()) {
            if (!locktokens.containsKey(item.getId())) {
                title +=
                        "<img id=\"resourceLocked\" style=\"position:absolute; margin-left:-11px; margin-top:3px;\"  border=\"0\" " +
                        "suppress=\"TRUE\" src=\""
                        + UIHelper.getGadgetImagesURL() + "navigation/lock.png" + "\" />&nbsp;&nbsp;";
            }
        }
        title += item.getName().isEmpty() ? "/" : item.getName();

        return title;
    }

    public ProjectTreeItem getChildByItemId(String itemId) {
        for (int i = 0; i < getChildCount(); i++) {
            ProjectTreeItem childItem = (ProjectTreeItem)getChild(i);
            if (childItem.getUserObject() != null && ((Item)childItem.getUserObject()).getId().equals(itemId)) {
                return childItem;
            }
        }

        return null;
    }

}
