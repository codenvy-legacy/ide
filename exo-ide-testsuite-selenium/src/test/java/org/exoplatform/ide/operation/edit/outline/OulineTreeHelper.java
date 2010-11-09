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
package org.exoplatform.ide.operation.edit.outline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;

/**
 * This class helps to verify code outline tree grid.
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 */
public class OulineTreeHelper extends BaseTest
{
   private Map<Integer, OutlineItemInfo> outlineTreeInfo = new HashMap<Integer, OutlineItemInfo>();
 
   /**
    * Add item to into the outlineTreeInfo list
    * @param itemRowNumber
    * @param itemName
    * @param fileLineNumber number of line with this item in the code
    */
   protected void addOutlineItem(int itemRowNumber, String itemName, int fileLineNumber)
   {
      addOutlineItem(itemRowNumber, itemName, fileLineNumber, false);
   }
   
   /**
    * 
    * @param itemRowNumber starting from 0
    * @param itemName
    * @param fileLineNumber starting from 1
    * @param checkFromFile = <b>true</b> outline item be checked from outline panel and then from file, <b>false</b> - only from outline panel
    */
   protected void addOutlineItem(int itemRowNumber, String itemName, int fileLineNumber, boolean checkFromFile)
   {
      this.outlineTreeInfo.put(itemRowNumber, new OutlineItemInfo(itemName, fileLineNumber, checkFromFile));
   }
   
   /**
    * empty outlineTreeInfo list
    */
   protected void clearOutlineTreeInfo()
   {
      this.outlineTreeInfo.clear();
   }
   
   /** 
    * Check outline item from outline panel and then from file based on each outline item info from outlineTreeInfo
    * @throws InterruptedException
    * @throws Exception
    */
   protected void checkOutlineTree() throws InterruptedException, Exception
   {
      Set<Entry<Integer, OutlineItemInfo>> items = outlineTreeInfo.entrySet();
      
      // test outline item from outline panel
      Iterator<Entry<Integer, OutlineItemInfo>> iterator = items.iterator();
      while (iterator.hasNext()) {
         Entry<Integer, OutlineItemInfo> item = iterator.next();
         OutlineItemInfo outlineItem = item.getValue();
         
         System.out.println(">>> test outline item from outline panel: item row = " + item.getKey() + ", item name = '" + outlineItem.getName() + "', file line number = " + outlineItem.getFileLineNumber());
         
         checkOutlineItemFromOutlinePanel(item.getKey(), outlineItem.getName(), outlineItem.getFileLineNumber());
      }

      // test outline item from file
      iterator = items.iterator();
      while (iterator.hasNext()) {
         Entry<Integer, OutlineItemInfo> item = iterator.next();
         OutlineItemInfo outlineItem = item.getValue();
         
         System.out.println(">>> test outline item from file: item row = " + item.getKey() + ", item name = '" + outlineItem.getName() + "', file line number = " + outlineItem.getFileLineNumber());

         if (outlineItem.isCheckItemFromFile())
         {
            checkOutlineItemFromFile(item.getKey(), outlineItem.getName(), outlineItem.getFileLineNumber());  
         }
      }      
   } 
   
   /**
    * Expand outline tree by goto from the first line to the last in the file tab. Then cursor will be returned to the line 1.
    * @throws Exception 
    */
   protected void expandOutlineTree() throws Exception
   {
      // recognize file's line numbers
      String fileContent = getTextFromCodeEditor(0);
      System.out.println("fileContent=" + fileContent);
      int fileLineNumbers = fileContent.split("\\r?\\n").length;   
      
      goToLine(1);
           
      // go to the end of file
      do
      {
         // Press down key on keyboard.         
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
         Thread.sleep(TestConstants.SLEEP);
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      } 
      while ((fileLineNumbers -= 2) > 0);
      
      goToLine(1);

   }
   
   /**
    * Check outline item by clicking in outline panel on row with <b>itemRowNumber</b> and verifying <b>fileLineNumber</b> in the Status Bar   
    * @param itemRowNumber starting from 0
    * @param fileLineNumber starting from 1
    * @throws InterruptedException 
    * @throws Exception 
    */
   private void checkOutlineItemFromOutlinePanel(Integer itemRowNumber, String itemName, int fileLineNumber) throws InterruptedException
   {
      assertEquals(itemName, selenium.getText("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[" + itemRowNumber + "]/col[0]"));
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[" + itemRowNumber + "]/col[1]");
      Thread.sleep(TestConstants.SLEEP);
      assertEquals(fileLineNumber + " : 1", getCursorPositionUsingStatusBar());      
   }
   
   /**
    * Check outline item by goto <b>fileLineNumber</b> line in the file and verifying is <b>itemRowNumber</b> is selected in the Outline Panel   
    * @param itemRowNumber starting from 0
    * @param itemName
    * @param fileLineNumber starting from 1
    * @throws Exception 
    */
   private void checkOutlineItemFromFile(int itemRowNumber, String itemName, int fileLineNumber) throws Exception
   {
      goToLine(fileLineNumber);
      Thread.sleep(TestConstants.SLEEP);
      checkOutlineTreeNodeSelected(itemRowNumber, itemName, true);
   }
   
   /**
    * check icon near outline item
    * @param rowNumber starting from 0
    * @param iconText
    * @param isSelected row
    */
   protected static void checkIconNearToken(int rowNumber, String iconText, boolean isSelected)
   {
      String divIndex = String.valueOf(rowNumber + 1);
      if (isSelected)
      {
         assertTrue(selenium.isElementPresent("//div[@eventproxy='ideOutlineTreeGrid_body']//table[@class='listTable']/tbody/tr[" + divIndex 
            + "]//table[@class='treeCellSelected']/tbody/tr/td[2]/img[2 and contains(@src, '" + iconText + "')]"));
      }
      else
      {
         assertTrue(selenium.isElementPresent("//div[@eventproxy='ideOutlineTreeGrid_body']//table[@class='listTable']/tbody/tr[" + divIndex 
            + "]//table[@class='treeCell']/tbody/tr/td[2]/img[2 and contains(@src, '" + iconText + "')]"));
      }
   }

   /**
    * click the outline item node
    * @param rowNumber startign from 0
    * @throws Exception
    */
   protected static void clickNode(int rowNumber) throws Exception
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[" + String.valueOf(rowNumber)
         + "]/col[1]");
      Thread.sleep(TestConstants.SLEEP);
   }
   
}

/**
 * Info about item within the code outline tree grid
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id
 */
class OutlineItemInfo {
   private String name;
   
   private int fileLineNumber;
   
   /**
    * <b>true</b> outline item be checked from outline panel and then from file, <b>false</b> - only from outline panel 
    */
   private boolean checkItemFromFile = true;
   
   public OutlineItemInfo(String name, int fileLineNumber, boolean checkItemFromFile)
   {
      this.name = name;
      this.fileLineNumber = fileLineNumber;
      this.checkItemFromFile = checkItemFromFile;
   }
   
   public String getName()
   {
      return name;
   }
   
   public int getFileLineNumber()
   {
      return fileLineNumber;
   }
   
   public boolean isCheckItemFromFile()
   {
      return checkItemFromFile;
   }
}
