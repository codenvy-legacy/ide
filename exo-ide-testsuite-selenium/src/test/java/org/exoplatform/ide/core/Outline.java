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
      String TREE_ID = "ideOutlineTreeGrid";

      String TREE = "//div[@id='" + TREE_ID + "']/";
   }

   /**
    * Get title of outline node.
    * 
    * @param row - row number (from 0)
    * @param col - column number (from 0)
    * @return title in (row, col) position in outline tree
    * @throws InterruptedException 
    */
   public String getTitle(int row, int col) throws InterruptedException
   {
      String subDiv = "";
      if (col > 0)
      {
         return selenium().getText(
            "//div[@id='ideOutlineTreeGrid']/div[" + String.valueOf(row + 2) + "]" + "/div/div["
               + String.valueOf(col) + "]");

      }

      else
         return selenium().getText("//div[@id='ideOutlineTreeGrid']/div[" + String.valueOf(row + 2) + "]");

   }

   /**
    * Click on open icon of outline node.
    * 
    * Can open or close node.
    * 
    * @param row - row number (from 0)
    * @param col - subImg number (from 0)
    * @throws Exception
    */
   public void clickOpenImg(int row, int subImg) throws Exception
   {
      String subDiv = "";
      if (subImg > 0)
      {
         for (int i = 0; i < subImg; i++)
         {
            subDiv = "/" + "div";
         }

         selenium().mouseDown("//div[@id='ideOutlineTreeGrid']/div[" + String.valueOf(row + 2) + "]//td/img");
         selenium().mouseUp("//div[@id='ideOutlineTreeGrid']/div[" + String.valueOf(row + 2) + "]//td/img");
         Thread.sleep(TestConstants.REDRAW_PERIOD);
         selenium().mouseDown(
            "//div[@id='ideOutlineTreeGrid']/div[" + String.valueOf(row + 2) + "]" + subDiv + "//td/img");
         selenium().mouseUp("//div[@id='ideOutlineTreeGrid']/div[" + String.valueOf(row + 2) + "]//td/img");
      }
      else

         selenium().mouseDown("//div[@id='ideOutlineTreeGrid']/div[" + String.valueOf(row + 2) + "]//td/img");
      selenium().mouseUp("//div[@id='ideOutlineTreeGrid']/div[" + String.valueOf(row + 2) + "]//td/img");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

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
    * Check is Outline Panel visible.
    * Note: you can't use this method, to check is Outline Panel visible,
    * when it appears in first time.
    * It is because to check, is Outline visible this method used
    * visibility attribute in style attribute.
    * But this attribute appears after that, when you hide outline.
    * 
    * @param isVisible
    */
   public void checkOutlineVisibility(boolean isVisible)
   {
      if (isVisible)
      {
         assertTrue(selenium().isElementPresent(
            "//td[contains(@class, 'gwt-TabBarItem-wrapper')]//td[text()='Outline']"));
         assertFalse(selenium().isElementPresent(
            "//td[contains(@class, 'gwt-TabBarItem-wrapper')]//td[text()='Outline']"));
      }
      else
      {
         assertFalse(selenium().isElementPresent(
            "//td[contains(@class, 'gwt-TabBarItem-wrapper')]//td[text()='Outline']"));
         assertTrue(selenium().isElementPresent(
            "//td[contains(@class, 'gwt-TabBarItem-wrapper')]//td[text()='Outline']"));
      }
   }

   /**
    * Check is node in Outline tree is selected
    * 
    * @param rowNumber number of item in treegrid starting from 0
    * @param name name of item
    * @param isSelected is node selected
    */
   public void checkOutlineTreeNodeSelected(int rowNumber, String name, boolean isSelected)
   {
      String divIndex = String.valueOf(rowNumber + 1);
      if (isSelected)
      {
         assertTrue(selenium().isElementPresent(
            "//div[@eventproxy='ideOutlineTreeGrid']//table[@class='listTable']/tbody/tr[" + divIndex
               + "]/td[@class='treeCellSelected']//nobr[text()='" + name + "']"));
      }
      else
      {
         assertTrue(selenium().isElementPresent(
            "//div[@eventproxy='ideOutlineTreeGrid']//table[@class='listTable']/tbody/tr[" + divIndex
               + "]/td[@class='treeCell']//nobr[text()='" + name + "']"));
      }
   }

   /**
    * Check item is shown in outline tree.
    * 
    * @param name
    * @throws Exception
    */
   public void assertElementPresentOutlineTree(String name) throws Exception
   {
      assertTrue(selenium().isElementPresent(
         "//div[@id=\"ideOutlineTreeGrid\"]//div[@class=\"gwt-Label\"and text()=" + "'" + name + "'" + "]"));
   }

   /**
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
    * Check is outline tree present in DOM 
    */
   public void assertOutlineTreePresent()
   {
      assertTrue(selenium().isElementPresent(Locators.TREE_ID));
   }

   /**
    * Check is element present in outline tree
    * @param id of row
    */
   public void assertElmentPresentById(String id)
   {
      assertTrue(selenium().isElementPresent(Locators.TREE + "/div[@id='" + id + "']"));
   }
}
