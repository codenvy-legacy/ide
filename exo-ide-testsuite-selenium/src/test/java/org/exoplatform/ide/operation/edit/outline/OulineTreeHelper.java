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
import org.exoplatform.ide.core.Outline;
import org.exoplatform.ide.core.Outline.TokenType;

/**
 * This class helps to verify code outline tree grid.
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 */
public class OulineTreeHelper extends BaseTest
{   
   private Map<Integer, OutlineItemInfo> outlineTreeInfo = new HashMap<Integer, OutlineItemInfo>();
   
   static int outlineTreeInfoIndex = 0;

   /**
    * Set outlineTreeInfoIndex = 0 
    */
   public static void init()
   {
      outlineTreeInfoIndex = 0;
   }
   
   /**
    * Add item to into the outlineTreeInfo list
    * @param itemRowNumber starting from 1
    * @param itemLabel
    * @param fileLineNumber number of line with this item in the code starting from 1
    */
   protected void addOutlineItem(int itemRowNumber, String itemLabel, int fileLineNumber)
   {
      addOutlineItem(itemRowNumber, itemLabel, fileLineNumber, true);
   }
   
   /**
    * Add item to into the outlineTreeInfo list with index starting from 1
    * @param itemLabel
    * @param fileLineNumber number of line with this item in the code starting from 1
    * @param TokenType type of token displayed as item 
    */
   protected void addOutlineItem(String itemLabel, int fileLineNumber, TokenType tokenType)
   {
      addOutlineItem(0, itemLabel, fileLineNumber, true, tokenType, null);
   }
   
   /**
    * Add item to into the outlineTreeInfo list with index starting from 1
    * @param itemLabel
    * @param fileLineNumber number of line with this item in the code starting from 1
    */
   protected void addOutlineItem(String itemLabel, int fileLineNumber)
   {
      addOutlineItem(0, itemLabel, fileLineNumber, true, null, null);
   }
   
   /**
    * Add item to into the outlineTreeInfo list
    * @param itemRowNumber starting from 1
    * @param itemLabel
    * @param fileLineNumber number of line with this item in the code starting from 1
    * @param TokenType type of token displayed as item
    */
   protected void addOutlineItem(int itemRowNumber, String itemLabel, int fileLineNumber, TokenType tokenType)
   {
      addOutlineItem(itemRowNumber, itemLabel, fileLineNumber, true, tokenType, null);
   }

   /**
    * Add item to into the outlineTreeInfo list
    * @param itemLabel
    * @param fileLineNumber number of line with this item in the code starting from 1
    * @param TokenType type of token displayed as item
    * @param itemName represent token's name 
    */
   protected void addOutlineItem(String itemLabel, int fileLineNumber, TokenType tokenType, String itemName)
   {
      addOutlineItem(0, itemLabel, fileLineNumber, true, tokenType, itemName);
   }   

   /**
    * Add item to into the outlineTreeInfo list
    * @param itemLabel
    * @param fileLineNumber number of line with this item in the code starting from 1
    * @param checkFromFile = default <b>true</b> outline item be checked from outline panel and then from file, <b>false</b> - only from outline panel 
    * @param TokenType type of token displayed as item
    * @param itemName represent token's name
    */
   protected void addOutlineItem(String itemLabel, int fileLineNumber, boolean checkFromFile, TokenType tokenType, String itemName)
   {
      addOutlineItem(0, itemLabel, fileLineNumber, checkFromFile, tokenType, itemName);
   }   
   
   
   /**
    * Add item to into the outlineTreeInfo list with index starting from 1
    * @param itemLabel
    * @param fileLineNumber number of line with this item in the code starting from 1
    * @param checkFromFile = default <b>true</b> outline item be checked from outline panel and then from file, <b>false</b> - only from outline panel
    */
   protected void addOutlineItem(String itemLabel, int fileLineNumber, boolean checkFromFile)
   {
      addOutlineItem(0, itemLabel, fileLineNumber, checkFromFile, null, null);
   }
   
   /**
    * Add item to into the outlineTreeInfo list with index starting from 1
    * @param itemLabel
    * @param fileLineNumber number of line with this item in the code starting from 1
    * @param checkFromFile = default <b>true</b> outline item be checked from outline panel and then from file, <b>false</b> - only from outline panel
    * @param TokenType type of token displayed as item 
    */
   protected void addOutlineItem(String itemLabel, int fileLineNumber, boolean checkFromFile, TokenType tokenType)
   {
      addOutlineItem(0, itemLabel, fileLineNumber, checkFromFile, tokenType, null);
   }
   
   /**
    * 
    * @param itemRowNumber starting from 0
    * @param itemLabel
    * @param fileLineNumber starting from 1
    * @param checkFromFile = default <b>true</b> outline item be checked from outline panel and then from file, <b>false</b> - only from outline panel
    */
   protected void addOutlineItem(int itemRowNumber, String itemLabel, int fileLineNumber, boolean checkFromFile)
   {
      addOutlineItem(itemRowNumber, itemLabel, fileLineNumber, checkFromFile, null, null);
   }

   /**
    * 
    * @param itemRowNumber starting from 1. If itemRowNumber=0 then outlineTreeInfoIndex value will be used
    * @param itemLabel
    * @param fileLineNumber starting from 1
    * @param checkFromFile = default <b>true</b> outline item be checked from outline panel and then from file, <b>false</b> - only from outline panel
    * @param TokenType type of token displayed as item 
    */
   protected void addOutlineItem(int itemRowNumber, String itemLabel, int fileLineNumber, boolean checkFromFile, TokenType tokenType, String itemName)
   {
      if (itemRowNumber == 0)
         itemRowNumber = ++outlineTreeInfoIndex;
      else
         outlineTreeInfoIndex = itemRowNumber;   

      this.outlineTreeInfo.put(itemRowNumber, new OutlineItemInfo(itemLabel, fileLineNumber, checkFromFile, tokenType, itemName));
      
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
         
         System.out.println(">>> test outline item from outline panel: item row = " + item.getKey() + ", item label = '" + outlineItem.getLabel() + "', file line number = " + outlineItem.getFileLineNumber());
         
         checkItemPresent(outlineItem.getId());
         checkOutlineItemFromOutlinePanel(item.getKey(), outlineItem.getLabel(), outlineItem.getFileLineNumber());
      }

      // test outline item from file
      iterator = items.iterator();
      while (iterator.hasNext()) {
         Entry<Integer, OutlineItemInfo> item = iterator.next();
         OutlineItemInfo outlineItem = item.getValue();
         
         System.out.println(">>> test outline item from file: item row = " + item.getKey() + ", item label = '" + outlineItem.getLabel() + "', file line number = " + outlineItem.getFileLineNumber());

         if (outlineItem.isCheckItemFromFile())
         {
            checkOutlineItemFromFile(item.getKey(), outlineItem.getLabel(), outlineItem.getFileLineNumber());  
         }
      }      
   } 
   
   /**
    * Check item present by checking its id like "a:PROPERTY:23"
    * @param itemId item's id
    */
   private void checkItemPresent(String itemId)
   {
      if (itemId != null)    
         IDE.OUTLINE.assertElmentPresentById(itemId);
   }

   /**
    * Expand outline tree by goto from the first line to the last in the file tab. Then cursor will be returned to the line 1.
    * @throws Exception 
    */
   protected void expandOutlineTree() throws Exception
   {
      // recognize file's line numbers
      String fileContent =IDE.EDITOR.getTextFromCodeEditor(0);
      System.out.println("fileContent=" + fileContent);
      int fileLineNumbers = fileContent.split("\\r?\\n").length;   
      
      goToLine(1);
      IDE.EDITOR.clickOnEditor(0);
           
      // go to the end of file
      do
      {
         // Press down key on keyboard.         
         selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
         Thread.sleep(Outline.Locators.SELECT_OUTLINE_DELAY * 2);
      } 
      while ((fileLineNumbers -= 1) > 0);
      
      goToLine(1);

   }
   
   // Updated
   /**
    * Check outline item by clicking in outline panel on row with <b>itemRowNumber</b> and verifying <b>fileLineNumber</b> in the Status Bar   
    * @param itemRowNumber starting from 0
    * @param fileLineNumber starting from 1
    * @throws Exception 
    * @throws Exception 
    */
   private void checkOutlineItemFromOutlinePanel(Integer itemRowNumber, String itemLabel, int fileLineNumber) throws Exception
   {
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      assertEquals(itemLabel, IDE.OUTLINE.getItemLabel(itemRowNumber));
      IDE.OUTLINE.selectRow(itemRowNumber);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      assertEquals(fileLineNumber + " : 1", IDE.STATUSBAR.getCursorPosition());      
   }
   
   /**
    * Check outline item by goto <b>fileLineNumber</b> line in the file and verifying is <b>itemRowNumber</b> is selected in the Outline Panel   
    * @param itemRowNumber starting from 0
    * @param itemLabel
    * @param fileLineNumber starting from 1
    * @throws Exception 
    */
   private void checkOutlineItemFromFile(int itemRowNumber, String itemLabel, int fileLineNumber) throws Exception
   {
      goToLine(fileLineNumber);
      Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      IDE.OUTLINE.checkOutlineTreeNodeSelected(itemRowNumber, itemLabel, true);
   }
   
   /**
    * TODO must be checked
    * check icon near outline item
    * @param rowNumber starting from 0
    * @param iconText
    * @param isSelected row
    */
   protected void checkIconNearToken(int rowNumber, String iconText, boolean isSelected)
   {
      String divIndex = String.valueOf(rowNumber + 1);
      if (isSelected)
      {
         assertTrue(selenium().isElementPresent("//div[@eventproxy='ideOutlineTreeGrid_body']//table[@class='listTable']/tbody/tr[" + divIndex 
            + "]//table[@class='treeCellSelected']/tbody/tr/td[2]/img[2 and contains(@src, '" + iconText + "')]"));
      }
      else
      {
         assertTrue(selenium().isElementPresent("//div[@eventproxy='ideOutlineTreeGrid_body']//table[@class='listTable']/tbody/tr[" + divIndex 
            + "]//table[@class='treeCell']/tbody/tr/td[2]/img[2 and contains(@src, '" + iconText + "')]"));
      }
   }

   /**
    * TODO must be checked
    * click the outline item node
    * @param rowNumber startign from 0
    * @throws Exception
    */
   protected void clickNode(int rowNumber) throws Exception
   {
      selenium().click("scLocator=//TreeGrid[ID=\"ideOutlineTreeGrid\"]/body/row[" + String.valueOf(rowNumber)
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
   /**
    * Full label including parameters and element type 
    */
   private String label;
   
   /**
    * Name of token presented by item, without parameters and element type
    */
   private String name;
   
   private int fileLineNumber;
   
   /**
    * Type of token displayed as item
    */
   private TokenType tokenType;
   
   /**
    * <b>true</b> outline item be checked from outline panel and then from file, <b>false</b> - only from outline panel 
    */
   private boolean checkItemFromFile = true;
   
   public OutlineItemInfo(String label, int fileLineNumber, boolean checkItemFromFile, TokenType tokenType, String name)
   {
      this.label = label;
      this.fileLineNumber = fileLineNumber;
      this.checkItemFromFile = checkItemFromFile;
      this.tokenType = tokenType;
      this.name = name;
   }
   
   /**
    * Get item's id in pattern like "a:METHOD:26"
    * @return item's id or null, if (name == null or tokenType == null)
    */
   public String getId()
   {
      if (name != null && tokenType != null)
         return name + ":" + tokenType + ":" + fileLineNumber;
      
      else
         return null;
   }

   public String getLabel()
   {
      return label;
   }
   
   public int getFileLineNumber()
   {
      return fileLineNumber;
   }
   
   public boolean isCheckItemFromFile()
   {
      return checkItemFromFile;
   }
   
   public TokenType getTokenType()
   {
      return tokenType;
   }
}
