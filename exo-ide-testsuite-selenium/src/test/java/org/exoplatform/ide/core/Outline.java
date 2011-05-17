/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.TestConstants;

/**
 * Operations with or in code outline panel.
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Dec 22, 2010 $
 *
 */
public class Outline extends AbstractTestModule
{
   interface Locators
   {
      static final String TREE_ID = "ideOutlineTreeGrid";

      static final String TREE_PREFIX_ID = "outline-";
      
      static final String TREE = "//div[@id='" + TREE_ID + "']/";
      
      static final String scrollTopLocator =
         "document.getElementById('ideOutlineTreeGrid').parentNode.parentNode.parentNode.scrollTop";
   }
   
   private static final String LINE_HIGHLIGHTER_LOCATOR = Locators.TREE + "/div[@class='ide-Tree-item-selected']";

   private static final int LINE_HEIGHT = 28;

   private static final int EDITOR_TOP_OFFSET_POSITION = 94;
   
   public enum TokenType {
      CLASS, METHOD, FIELD, ANNOTATION, INTERFACE, ARRAY, ENUM, CONSTRUCTOR, KEYWORD, TEMPLATE, VARIABLE, FUNCTION, 
      /** Property type for JSON */
      PROPERTY,
      
      /**
       * HTML or XML tag.
       */
      TAG,
      
      /**
       * HTML or XML attribute; 
       */
      ATTRIBUTE,
      CDATA,

      /** Property type for JavaScript */
      BLOCK, 
      
      /** Property type for Groovy code */
      GROOVY_TAG, PACKAGE, IMPORT, PARAMETER, TYPE,
      
      /** Property type for Java code */
      JSP_TAG,
      
      /** Property type for Ruby code **/
      ROOT, MODULE, LOCAL_VARIABLE, GLOBAL_VARIABLE, CLASS_VARIABLE, INSTANCE_VARIABLE, CONSTANT;
   }
   
   @Deprecated
   /**
    * Get title of outline node.
    * 
    * @param row - row number (from 0)
    * @param col - column number (from 0)
    * @return title in (row, col) position in outline tree
    */
   public String getTitle(int row, int col)
   {
      return selenium().getText(
         "scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[" + String.valueOf(row) + "]/col["
            + String.valueOf(col) + "]");
   }
   
   /**
    * Return label of item at row
    * 1 - number of root node (workspace).  
    * @param rowNumber
    * @param labelType
    * @return
    */
   public String getItemLabel(int rowNumber, LabelType labelType)
   {      
      int size =
         selenium().getXpathCount("//div[@id='" + Locators.TREE_ID + "']//div[@class='gwt-Label']").intValue();
      if (size <= 0)
         return null;
      int index = 0;

      for (int i = 1; i <= size; i++)
      {
         if (selenium().isVisible(
            "xpath=(//div[@id='" + Locators.TREE_ID + "']//div[@class='gwt-Label'])[position()=" + i + "]"))
         {
            index++;
         }
         
         if (index == rowNumber)
         {
            if (labelType != null)
            {
               return selenium().getText("xpath=(//div[@id='" + Locators.TREE_ID + "']//span[@class='" + labelType + "'])[position()=" + i + "]");               
            }
            else
            {
               return selenium().getText("xpath=(//div[@id='" + Locators.TREE_ID + "']//div[@class='gwt-Label'])[position()=" + i + "]");
            }
         }
      }
      return null;
   }

   /**
    * Return full item label at row
    * 1 - number of root node (workspace).  
    * @param rowNumber - number of row.
    */
   public String getItemLabel(int rowNumber)
   {
      return getItemLabel(rowNumber, null);
   }
   
   @Deprecated
   /**
    * Click on open icon of outline node.
    * 
    * Can open or close node.
    * 
    * @param row - row number (from 0)
    * @param col - column number (from 0)
    * @throws Exception
    */
   public void clickOpenImg(int row, int col) throws Exception
   {
      selenium().click(
         "scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[" + String.valueOf(row) + "]/col["
            + String.valueOf(col) + "]/open");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

   /**
    * Double click the item at row
    * 
    * @param row - row number (from 0)
    * @throws Exception
    */
   public void doubleClickItem(int row) throws Exception
   {
      int size =
         selenium().getXpathCount("//div[@id='" + Locators.TREE_ID + "']//div[@class='gwt-Label']").intValue();
      if (size <= 0)
         return;
      int index = 0;

      for (int i = 1; i <= size; i++)
      {
         if (selenium().isVisible(
            "xpath=(//div[@id='" + Locators.TREE_ID + "']//div[@class='gwt-Label'])[position()=" + i + "]"))
         {
            index++;
         }
         
         if (index == row)
         {
            selenium().doubleClickAt(
               "xpath=(//div[@id='" + Locators.TREE_ID + "']//div[@class='gwt-Label'])[position()=" + i + "]", "0");
            break;
         }
      };
   }
   
   /**
    * Double click the item with label 
    * 
    * @throws Exception
    */
   public void doubleClickItem(String label) throws Exception
   {
      selenium().doubleClickAt("xpath=(//div[@id='" + Locators.TREE_ID + "']//span[text() = '" + label + "']", "0");
   }
   
   
   @Deprecated
   /**
    * Select row in outline tree.
    * 
    * Click on 1-st column of row.
    * 
    * @param row - number of row (from 0).
    * @throws Exception
    */
   public void select(int row) throws Exception
   {
      selenium().click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[" + String.valueOf(row) + "]/col[1]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   /**
    * Select row in outline tree.
    * @param row - number of row (from 1).
    * @throws Exception
    */
   public void selectRow(int rowNumber) throws Exception
   {
      int size =
         selenium().getXpathCount("//div[@id='" + Locators.TREE_ID + "']//div[@class='gwt-Label']").intValue();
      if (size <= 0)
         return;
      int index = 0;

      for (int i = 1; i <= size; i++)
      {
         if (selenium().isVisible(
            "xpath=(//div[@id='" + Locators.TREE_ID + "']//div[@class='gwt-Label'])[position()=" + i + "]"))
         {
            index++;
         }
         
         if (index == rowNumber)
         {
            selenium().clickAt(
               "xpath=(//div[@id='" + Locators.TREE_ID + "']//div[@class='gwt-Label'])[position()=" + i + "]", "0");
            break;
         }
      }
   }
   
   /**
    * Check is Outline Panel visible.
    * Note: you can't use this method, to check is Outline Panel visible,
    * when it appears in first time.
    * It is because to check, is Outline visible this method used
    * visibility attribute in style attribute.
    * But this attribute appears after that, when you hide outline.
    * 
    * @param isVisible
    */
   public void checkOutlinePanelVisibility(boolean isVisible)
   {
      if (isVisible)
      {
         assertTrue(selenium().isElementPresent(
            "//td[contains(@class, 'gwt-TabBarItem-wrapper')]//td[text()='Outline']"));
      }
      else
      {
         assertFalse(selenium().isElementPresent(
            "//td[contains(@class, 'gwt-TabBarItem-wrapper')]//td[text()='Outline']"));
      }
   }

   /**
    * Check is node in Outline tree is selected
    * @param rowNumber number of item in treegrid starting from 1
    * @param name name of item
    * @param isSelected is node selected
    */
   public void checkOutlineTreeNodeSelected(int rowNumber, String name, boolean isSelected)
   {
      Number linePositionTop = EDITOR_TOP_OFFSET_POSITION + (rowNumber - 1) * LINE_HEIGHT;
      
      // taking in mind vertical scrolling
      Integer scrollTop = getScrollTop();
      if (scrollTop != null)
      {
         linePositionTop = linePositionTop.intValue() - scrollTop;
      }

      selenium().isElementPresent(LINE_HIGHLIGHTER_LOCATOR);
      
      if (isSelected)
      {
         assertEquals("Outline row number " + rowNumber + " should be selected.", linePositionTop, selenium().getElementPositionTop(LINE_HIGHLIGHTER_LOCATOR));
      }
      else
      {
         assertFalse("Outline row number " + rowNumber + " should not be selected.", selenium().getElementPositionTop(LINE_HIGHLIGHTER_LOCATOR) == linePositionTop);
      }
   }

   /**
    * TODO this method should be verified 
    * Check item is shown in outline tree.
    * 
    * @param name
    * @throws Exception
    */
   public void assertElementPresentOutlineTree(String name) throws Exception
   {
      assertTrue(selenium().isElementPresent(
         "//div[@id=\"ideOutlineTreeGrid\"]//div[@class=\"gwt-Label\" and text()=" + "'" + name + "'" + "]"));
   }

   /**
    * TODO this method should be verified 
    * Check item is not shown in outline tree.
    * 
    * @param name
    * @throws Exception
    */
   public void assertElementNotPresentOutlineTree(String name) throws Exception
   {

      assertFalse(selenium().isElementPresent(
         "//div[@id=\"ideOutlineTreeGrid\"]//div[@class=\"gwt-Label\"and text()=" + "'" + name + "'" + "]"));
   }

   @Deprecated
   /**
    * Select the item in the outline tree. 
    * 
    * @param name item's name
    * @throws Exception
    */
   public void selectItemInOutlineTree(String name) throws Exception
   {
      selenium().click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[name=" + name + "]/col[1]");
   }
   
   /**
    * TODO this method should be fixed 
    * Select item in tree
    * @param name nome of item
    */
   public void selectItem(String name) throws Exception
   {
      selenium().clickAt("//div[@id='" + Locators.TREE_ID + "']//span[contains(text(), '" + name + "')]", "0");
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }  
   
   /**
    * Check is outline tree present in DOM 
    */
   public void assertOutlineTreePresent()
   {
      assertTrue(selenium().isElementPresent(Locators.TREE));
   }

   /**
    * Check is element present in outline tree
    * @param id of row
    */
   public void assertElmentPresentById(String id)
   {
      assertTrue(selenium().isElementPresent(Locators.TREE + "/div[@id='" + id + "']"));
   }
   
   /**
    *  Method close Outline codehelper
    */
   public void closeOutline()
   {
      selenium().click("//div[@button-name='close-tab' and @tab-title='Outline']");
   }

   /**
    * Return outline scroll top. Method return <b>null</b> in Internet Explorer, because this browser doesn't support window.document.getElementsByClassName() method still. 
    */
   private Integer getScrollTop()
   {
      Integer scrollTop = null;

      try
      {
         // trying to read the property from Firefox         
         scrollTop =
            Integer.parseInt(selenium().getEval("var win = selenium.browserbot.getCurrentWindow(); win."
               + Locators.scrollTopLocator + ";"));
      }
      catch (NumberFormatException e)
      {
         return null;
      }

      return scrollTop;
   }   
   

   public void selectRowTemp(int rowNumber)
   {
      if (rowNumber <= 0)
         return;
      selenium().mouseDown(Locators.TREE + "/" +"div[" + String.valueOf(rowNumber + 2) + "]" );
      //selenium().mouseUp(Locators.TREE + "/" +"div[" + String.valueOf(rowNumber + 2) + "]" );
  }
  
   public enum LabelType 
   {
      NAME("item-name"), 
      TYPE("item-type"), 
      PARAMETER("item-parameter"),
      MODIFIER("item-modifier");

      private String className;

      LabelType(String className)
      {
         this.className = className;
      }

      @Override
      public String toString()
      {
         return this.className;
      }
   }
}
