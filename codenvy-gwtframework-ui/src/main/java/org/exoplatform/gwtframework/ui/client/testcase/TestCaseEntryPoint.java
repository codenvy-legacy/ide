/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.gwtframework.ui.client.testcase;

import org.exoplatform.gwtframework.ui.client.testcase.cases.ResizeableWindowsTestCase;
import org.exoplatform.gwtframework.ui.client.testcase.cases.WindowRootPanelTestCase;
import org.exoplatform.gwtframework.ui.client.testcase.cases.WindowsTestCase;
import org.exoplatform.gwtframework.ui.client.testcase.cases.FormItemsTestCase;
import org.exoplatform.gwtframework.ui.client.testcase.cases.GWTDialogsTestCase;
import org.exoplatform.gwtframework.ui.client.testcase.cases.IconButtonTestCase;
import org.exoplatform.gwtframework.ui.client.testcase.cases.ListGridTestCase;
import org.exoplatform.gwtframework.ui.client.testcase.cases.MenuTestCase;
import org.exoplatform.gwtframework.ui.client.testcase.cases.PopupMenuButtonTestCase;
import org.exoplatform.gwtframework.ui.client.testcase.cases.SelectItemsTestCase;
import org.exoplatform.gwtframework.ui.client.testcase.cases.SplitPanelsTestCase;
import org.exoplatform.gwtframework.ui.client.testcase.cases.TabPanelTestCase;
import org.exoplatform.gwtframework.ui.client.testcase.cases.TextButtonTestCase;
import org.exoplatform.gwtframework.ui.client.testcase.cases.ToolbarAndStatusbarTestCase;
import org.exoplatform.gwtframework.ui.client.testcase.cases.ToolbarTestCase;
import org.exoplatform.gwtframework.ui.client.tree.Tree;
import org.exoplatform.gwtframework.ui.client.tree.TreeNode;
import org.exoplatform.gwtframework.ui.client.tree.TreeRecord;
import org.exoplatform.gwtframework.ui.client.util.ExoStyle;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TestCaseEntryPoint implements EntryPoint
{

   public interface Images
   {

      public interface Cases
      {

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

   public void onModuleLoad()
   {
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

   public void showCase(Widget widget)
   {
      grid.clearCell(0, 1);
      grid.setWidget(0, 1, widget);
   }

   private class TestCaseTree extends Tree
   {
      @Override
      public void onClick(TreeRecord treerecord)
      {
         switchTestCase();
      }
   }

   private class TestCaseTreeNode extends TreeNode
   {

      private TestCase testCase;

      public TestCaseTreeNode(String name, String icon, TestCase testCase)
      {
         super(name, icon);
         this.testCase = testCase;
         setIsFolder(false);
      }

      public TestCase getTestCase()
      {
         return testCase;
      }

   }

   protected void switchTestCase()
   {
      if (testCaseTree.getSelectedRecord() == null)
      {
         return;
      }

      TreeNode node = testCaseTree.getSelectedRecord().getNode();
      if (node instanceof TestCaseTreeNode)
      {
         TestCaseTreeNode testCaseTreeNode = (TestCaseTreeNode)node;
         if (testCaseTreeNode.getTestCase() != null)
         {
            showCase(testCaseTreeNode.getTestCase());
         }
      }
   }

   private TreeNode createDialogsCases()
   {
      TreeNode node = new TreeNode("Windows", Images.Cases.CASE);

      node.getChildren().add(new TestCaseTreeNode("Dialogs", Images.Cases.CASE, new GWTDialogsTestCase()));
      node.getChildren().add(new TestCaseTreeNode("Windows", Images.Cases.CASE, new WindowsTestCase()));
      node.getChildren().add(new TestCaseTreeNode("Resizeable Windows", Images.Cases.CASE, new ResizeableWindowsTestCase()));
      node.getChildren().add(new TestCaseTreeNode("Open Window in region", Images.Cases.CASE, new WindowRootPanelTestCase()));

      return node;
   }

   private TreeNode createButtonsCases()
   {
      TreeNode node = new TreeNode("Buttons", Images.Cases.BUTTONS);
      node.getChildren().add(new TestCaseTreeNode("Icon Button", Images.Cases.CASE, new IconButtonTestCase()));
      node.getChildren().add(new TestCaseTreeNode("Text Button", Images.Cases.CASE, new TextButtonTestCase()));
      return node;
   }

   private TreeNode createMenuCases()
   {
      TreeNode node = new TreeNode("Menu", Images.Cases.MENUS);
      node.getChildren().add(new TestCaseTreeNode("Menu", Images.Cases.CASE, new MenuTestCase()));
      node.getChildren().add(
         new TestCaseTreeNode("Popup Menu Button", Images.Cases.CASE, new PopupMenuButtonTestCase()));
      return node;
   }

  private TreeNode createToolbarCases()
   {
      TreeNode node = new TreeNode("Toolbar", Images.Cases.MENUS);
      node.getChildren().add(new TestCaseTreeNode("Toolbar", Images.Cases.CASE, new ToolbarTestCase()));
      node.getChildren().add(
         new TestCaseTreeNode("Toolbar and Statusbar", Images.Cases.CASE, new ToolbarAndStatusbarTestCase()));
      return node;
   }

  
   private TreeNode createGWTStyledComponents()
   {
      TreeNode node = new TreeNode("Restyled GWT Components", Images.Cases.CASE);
      node.getChildren().add(new TestCaseTreeNode("Split Layout Panels", Images.Cases.CASE, new SplitPanelsTestCase()));
      node.getChildren().add(new TestCaseTreeNode("TabPanel", Images.Cases.CASE, new TabPanelTestCase()));
      return node;
   }

   private TreeNode createSelectCases()
   {
      TreeNode node = new TreeNode("Select items", Images.Cases.CASE);
      node.getChildren().add(new TestCaseTreeNode("Select item", Images.Cases.CASE, new SelectItemsTestCase()));
      return node;
   }

   private TreeNode createListGridCases()
   {
      TreeNode node = new TreeNode("List Grid", Images.Cases.CASE);
      node.getChildren().add(new TestCaseTreeNode("List Grid", Images.Cases.CASE, new ListGridTestCase()));
      return node;
   }

   private TreeNode createFormItemsCases()
   {
      TreeNode node = new TreeNode("Form", Images.Cases.CASE);
      node.getChildren().add(new TestCaseTreeNode("Text Input", Images.Cases.CASE, new FormItemsTestCase()));
      return node;
   }

}
