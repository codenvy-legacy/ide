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
package org.exoplatform.gwtframework.ui.client.testcase;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.testcase.cases.*;
import org.exoplatform.gwtframework.ui.client.tree.Tree;
import org.exoplatform.gwtframework.ui.client.tree.TreeNode;
import org.exoplatform.gwtframework.ui.client.tree.TreeRecord;
import org.exoplatform.gwtframework.ui.client.util.ExoStyle;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TestCaseEntryPoint implements EntryPoint {

    public interface Images {

        public interface Cases {

            static final String IMAGES_URL = ExoStyle.getEXoStyleURL() + "../../../../showcase/images/cases/";

            static final String BUTTONS = IMAGES_URL + "buttons.png";

            static final String CASE = IMAGES_URL + "case.png";

            static final String GRIDS = IMAGES_URL + "grids.png";

            static final String HOME = IMAGES_URL + "house.png";

            static final String MENUS = IMAGES_URL + "menus.png";

            static final String OK = IMAGES_URL + "ok.png";

            static final String TREE = IMAGES_URL + "tree.png";

        }

        public static final String IMAGE_URL = UIHelper.getGadgetImagesURL() + "../showcase/images/";

        public static final String ADD = IMAGE_URL + "bundled/add.png";

        public static final String REMOVE = IMAGE_URL + "bundled/remove.png";

        public static final String CANCEL = IMAGE_URL + "bundled/cancel.png";

        public static final String OK = IMAGE_URL + "bundled/ok.png";

        public static final String XML = IMAGE_URL + "bundled/xml.png";

        public static final String HTML = IMAGE_URL + "bundled/html.png";

        public static final String GROOVY = IMAGE_URL + "bundled/groovy.png";

        public static final String SEARCH = IMAGE_URL + "bundled/search.png";

    }

    private Grid grid;

    private TestCaseTree testCaseTree;

    public void onModuleLoad() {
        grid = new Grid(1, 2);
        DOM.setStyleAttribute(grid.getElement(), "width", "100%");
        DOM.setStyleAttribute(grid.getElement(), "height", "100%");
        DOM.setStyleAttribute(grid.getElement(), "background", "#ffffff");
        grid.setBorderWidth(0);

        DOM.setStyleAttribute(grid.getCellFormatter().getElement(0, 0), "width", "200px");
        DOM.setStyleAttribute(grid.getCellFormatter().getElement(0, 0), "height", "100%");
        DOM.setStyleAttribute(grid.getCellFormatter().getElement(0, 1), "verticalAlign", "top");
        DOM.setStyleAttribute(grid.getCellFormatter().getElement(0, 1), "height", "100%");

        FlowPanel testCaseTreePanel = new FlowPanel();
        DOM.setStyleAttribute(testCaseTreePanel.getElement(), "width", "250px");
        DOM.setStyleAttribute(testCaseTreePanel.getElement(), "height", "100%");
        DOM.setStyleAttribute(testCaseTreePanel.getElement(), "background", "#EE8899");
        grid.setWidget(0, 0, testCaseTreePanel);

        grid.setHTML(0, 1, "&nbsp;");
        RootPanel.get().add(grid);

        testCaseTree = new TestCaseTree();
        testCaseTreePanel.add(testCaseTree);
        testCaseTree.setWidth("100%");
        testCaseTree.setHeight("100%");

        TreeNode showCase = new TreeNode("ShowCase", Images.Cases.HOME);

        showCase.getChildren().add(createDialogsCases());

        showCase.getChildren().add(createButtonsCases());

        showCase.getChildren().add(createMenuCases());

        showCase.getChildren().add(createSelectCases());

        showCase.getChildren().add(createListGridCases());

        showCase.getChildren().add(createToolbarCases());

        showCase.getChildren().add(createGWTStyledComponents());

        showCase.getChildren().add(createFormItemsCases());

        testCaseTree.setRoot(showCase);

    }

    public void showCase(Widget widget) {
        grid.clearCell(0, 1);
        grid.setWidget(0, 1, widget);
    }

    private class TestCaseTree extends Tree {
        @Override
        public void onClick(TreeRecord treerecord) {
            switchTestCase();
        }
    }

    private class TestCaseTreeNode extends TreeNode {

        private TestCase testCase;

        public TestCaseTreeNode(String name, String icon, TestCase testCase) {
            super(name, icon);
            this.testCase = testCase;
            setIsFolder(false);
        }

        public TestCase getTestCase() {
            return testCase;
        }

    }

    protected void switchTestCase() {
        if (testCaseTree.getSelectedRecord() == null) {
            return;
        }

        TreeNode node = testCaseTree.getSelectedRecord().getNode();
        if (node instanceof TestCaseTreeNode) {
            TestCaseTreeNode testCaseTreeNode = (TestCaseTreeNode)node;
            if (testCaseTreeNode.getTestCase() != null) {
                showCase(testCaseTreeNode.getTestCase());
            }
        }
    }

    private TreeNode createDialogsCases() {
        TreeNode node = new TreeNode("Windows", Images.Cases.CASE);

        node.getChildren().add(new TestCaseTreeNode("Dialogs", Images.Cases.CASE, new GWTDialogsTestCase()));
        node.getChildren().add(new TestCaseTreeNode("Windows", Images.Cases.CASE, new WindowsTestCase()));
        node.getChildren().add(new TestCaseTreeNode("Resizeable Windows", Images.Cases.CASE, new ResizeableWindowsTestCase()));
        node.getChildren().add(new TestCaseTreeNode("Open Window in region", Images.Cases.CASE, new WindowRootPanelTestCase()));

        return node;
    }

    private TreeNode createButtonsCases() {
        TreeNode node = new TreeNode("Buttons", Images.Cases.BUTTONS);
        node.getChildren().add(new TestCaseTreeNode("Icon Button", Images.Cases.CASE, new IconButtonTestCase()));
        node.getChildren().add(new TestCaseTreeNode("Text Button", Images.Cases.CASE, new TextButtonTestCase()));
        return node;
    }

    private TreeNode createMenuCases() {
        TreeNode node = new TreeNode("Menu", Images.Cases.MENUS);
        node.getChildren().add(new TestCaseTreeNode("Menu", Images.Cases.CASE, new MenuTestCase()));
        node.getChildren().add(
                new TestCaseTreeNode("Popup Menu Button", Images.Cases.CASE, new PopupMenuButtonTestCase()));
        return node;
    }

    private TreeNode createToolbarCases() {
        TreeNode node = new TreeNode("Toolbar", Images.Cases.MENUS);
        node.getChildren().add(new TestCaseTreeNode("Toolbar", Images.Cases.CASE, new ToolbarTestCase()));
        node.getChildren().add(
                new TestCaseTreeNode("Toolbar and Statusbar", Images.Cases.CASE, new ToolbarAndStatusbarTestCase()));
        return node;
    }


    private TreeNode createGWTStyledComponents() {
        TreeNode node = new TreeNode("Restyled GWT Components", Images.Cases.CASE);
        node.getChildren().add(new TestCaseTreeNode("Split Layout Panels", Images.Cases.CASE, new SplitPanelsTestCase()));
        node.getChildren().add(new TestCaseTreeNode("TabPanel", Images.Cases.CASE, new TabPanelTestCase()));
        return node;
    }

    private TreeNode createSelectCases() {
        TreeNode node = new TreeNode("Select items", Images.Cases.CASE);
        node.getChildren().add(new TestCaseTreeNode("Select item", Images.Cases.CASE, new SelectItemsTestCase()));
        return node;
    }

    private TreeNode createListGridCases() {
        TreeNode node = new TreeNode("List Grid", Images.Cases.CASE);
        node.getChildren().add(new TestCaseTreeNode("List Grid", Images.Cases.CASE, new ListGridTestCase()));
        return node;
    }

    private TreeNode createFormItemsCases() {
        TreeNode node = new TreeNode("Form", Images.Cases.CASE);
        node.getChildren().add(new TestCaseTreeNode("Text Input", Images.Cases.CASE, new FormItemsTestCase()));
        return node;
    }

}
