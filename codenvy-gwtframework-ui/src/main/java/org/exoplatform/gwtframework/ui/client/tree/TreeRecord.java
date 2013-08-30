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
package org.exoplatform.gwtframework.ui.client.tree;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.gwtframework.ui.client.util.ExoStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TreeRecord extends Composite {

    public static interface Style {

        static final String TREE_ROW_OVERED = "exo-tree-row-overed";

        static final String TREE_ROW_SELECTED = "exo-tree-row-selected";

        static final String TREE_ROW_SELECTED_OVERED = "exo-tree-row-selected-overed";

        static final String TREE_ROW = "exo-tree-row";

        static final String ROW_OVER = "exo-tree-row-over";

        static final String ROW_SELECTED = "exo-tree-row-selected";

        static final String ROW_SELECTED_OVER = "exo-tree-row-selected-over";

        static final String CONTROL_IMAGE_PANEL = "exo-tree-control-image-panel";

        static final String CONTROL_IMAGE = "exo-tree-control-image";

        static final String ICON_PANEL = "exo-tree-control-icon-panel";

        static final String ICON_IMAGE = "exo-tree-control-icon-image";

        static final String TITLE_PANEL = "exo-tree-control-title-panel";

        static final String TITLE_TABLE = "exo-tree-control-title-table";

        static final String TITLE_TABLE_TR = "exo-tree-control-title-table-tr";

        static final String TITLE_TABLE_TD = "exo-tree-control-title-table-td";

    }

    public static interface Images {

        static final String CONTROL_OPEN = ExoStyle.getEXoStyleURL() + "tree/open.png";

        static final String CONTROL_CLOSE = ExoStyle.getEXoStyleURL() + "tree/close.png";

        static final String FOLDER_CLOSED = ExoStyle.getEXoStyleURL() + "tree/folder-closed.png";

        static final String FILE = ExoStyle.getEXoStyleURL() + "tree/file.png";

    }

    private FlowPanel panel = new FlowPanel();

    private RowPanel rowPanel = new RowPanel();

    private FlowPanel subtreePanel = new FlowPanel();

    private Tree tree;

    private TreeNode node;

    private int depth;

    private boolean expanded;

    private Image controlImage;

    private boolean selected;

    int pos = 0;

    public TreeRecord(Tree tree, TreeNode node, int depth) {
        this.tree = tree;
        this.node = node;
        this.depth = depth;

        initWidget(panel);

        rowPanel.setStyleName(Style.TREE_ROW);
        panel.add(rowPanel);

        panel.add(subtreePanel);

        createRowElement();

        if (depth > 0) {
            createDepthDivision();
        }

        if (node.isFolder()) {
            createControlButtonPanel();
        }

        createIconPanel();
        createTitlePanel();
    }

    private Grid rowGrid;

    private void createRowElement() {
        rowGrid = new Grid(1, 4);
        rowGrid.setBorderWidth(0);
        rowGrid.setCellPadding(0);
        rowGrid.setCellSpacing(0);
        DOM.setStyleAttribute(rowGrid.getElement(), "borderCollapse", "collapse");

        rowGrid.getRowFormatter().setStyleName(0, Style.TITLE_TABLE_TR);
        rowPanel.add(rowGrid);
    }

    private void createDepthDivision() {
        Image divisionImage = new Image();
        divisionImage.setUrl(ExoStyle.getEXoStyleURL() + "blank.png");

        if (node.isFolder()) {
            DOM.setStyleAttribute(divisionImage.getElement(), "width", "" + (depth * 20) + "px");
        } else {
            DOM.setStyleAttribute(divisionImage.getElement(), "width", "" + (depth * 20 + 20) + "px");
        }

        DOM.setStyleAttribute(divisionImage.getElement(), "height", "20px");

        rowGrid.setWidget(0, pos, divisionImage);

        pos++;
    }

    public void refreshWidth(int width) {
        DOM.setStyleAttribute(rowPanel.getElement(), "width", "" + width + "px");
    }

    private void createControlButtonPanel() {
        controlImage = new Image();
        controlImage.setStyleName(Style.CONTROL_IMAGE);
        controlImage.setUrl(Images.CONTROL_OPEN);
        controlImage.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                controlButtonClicked();
            }
        });

        rowGrid.setWidget(0, pos, controlImage);

        pos++;
    }

    private void createIconPanel() {
        Image iconImage = new Image();
        iconImage.setStyleName(Style.ICON_IMAGE);

        if (node.getIcon() == null) {
            if (node.isFolder()) {
                iconImage.setUrl(Images.FOLDER_CLOSED);
            } else {
                iconImage.setUrl(Images.FILE);
            }
        } else {
            iconImage.setUrl(node.getIcon());
        }

        rowGrid.setWidget(0, pos, iconImage);

        pos++;
    }

    private void createTitlePanel() {
        rowGrid.getCellFormatter().setStyleName(0, pos, Style.TITLE_TABLE_TD);
        rowGrid.setHTML(0, pos, "<nobr>" + node.getName() + "</nobr>");
    }

    public int getTitlePanelWidth() {
        return rowGrid.getOffsetWidth();
    }

    public void expand(boolean updateChildren) {
        tree.onExpand(this);

        DOM.setStyleAttribute(subtreePanel.getElement(), "display", "block");
        controlImage.setUrl(Images.CONTROL_CLOSE);
        expanded = true;

        if (updateChildren || subtreePanel.getWidgetCount() == 0) {
            refreshSubtree();
        }

        tree.updateRowsWidth();
    }

    public void expand() {
        expand(false);
    }

    public void refreshSubtree() {
        subtreePanel.clear();

        for (TreeNode n : node.getChildren()) {
            subtreePanel.add(new TreeRecord(tree, n, depth + 1));
        }
    }

    public void collapse() {
        DOM.setStyleAttribute(subtreePanel.getElement(), "display", "none");
        controlImage.setUrl(Images.CONTROL_OPEN);
        expanded = false;

        tree.updateRowsWidth();
        tree.onCollapse(this);
    }

    public boolean isSubtreePanelVisible() {
        if ("none".equals(DOM.getStyleAttribute(subtreePanel.getElement(), "display"))) {
            return false;
        }

        return true;
    }

    public List<TreeRecord> getChildren() {
        ArrayList<TreeRecord> records = new ArrayList<TreeRecord>();

        for (int i = 0; i < subtreePanel.getWidgetCount(); i++) {
            records.add((TreeRecord)subtreePanel.getWidget(i));
        }

        return records;
    }

    public TreeNode getNode() {
        return node;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public int getDepth() {
        return depth;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setStyleOver() {
        if (selected) {
            rowPanel.setStyleName(Style.TREE_ROW_SELECTED_OVERED);
        } else {
            rowPanel.setStyleName(Style.TREE_ROW_OVERED);
        }
    }

    public void setStyleNormal() {
        if (selected) {
            rowPanel.setStyleName(Style.TREE_ROW_SELECTED);
        } else {
            rowPanel.setStyleName(Style.TREE_ROW);
        }
    }

    private void onMouseOver() {
        tree.recordMouseOver(this);
    }

    private void onMouseOut() {
        tree.recordMouseOut(this);
    }

    private class RowPanel extends FlowPanel {
        public RowPanel() {
            sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONMOUSEDOWN | Event.ONCLICK | Event.ONDBLCLICK);
        }

        @Override
        public void onBrowserEvent(Event event) {
            switch (DOM.eventGetType(event)) {
                case Event.ONMOUSEOVER:
                    onMouseOver();
                    break;

                case Event.ONMOUSEOUT:
                    onMouseOut();
                    break;

                case Event.ONCLICK:
                    mouseClick();
                    break;

                case Event.ONMOUSEDOWN:
                    onMouseDown(event.getCtrlKey(), event.getShiftKey());
                    skipClicking = false;
                    break;

                case Event.ONDBLCLICK:
                    onMouseDoubleClick();
                    break;
            }
        }
    }

    private void onMouseDown(boolean ctrlKey, boolean shiftKey) {
        tree.recordMouseDown(this, ctrlKey, shiftKey);
    }

    private void onMouseDoubleClick() {
        if (skipDClicking) {
            skipDClicking = false;
            return;
        }

        tree.onDoubleClick(this);
    }

    private void mouseClick() {
        if (skipClicking) {
            skipClicking = false;
            return;
        }

        tree.onClick(this);
    }

    private boolean skipClicking = false;

    private boolean skipDClicking = false;

    protected void controlButtonClicked() {
        skipClicking = true;
        skipDClicking = true;

        if (expanded) {
            collapse();
        } else {
            expand();
        }
    }

    public TreeRecord getParentRecord() {
        return (TreeRecord)getParent().getParent().getParent();
    }

}
