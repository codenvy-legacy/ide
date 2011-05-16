/*
 * Copyright (C) 2011 eXo Platform SAS.
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
import static org.junit.Assert.assertTrue;

/**
 * Class for operations with classpath form (for configuring classpath of project).
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Project.java May 12, 2011 12:35:39 PM vereshchaka $
 *
 */
public class ClasspathProject extends AbstractTestModule
{

   private static final String CLASSPATH_VIEW_ID = "ideConfigureBuildPathForm";

   private static final String LIST_GRID_ID = "ideClassPathEntryListGrid";

   private static final String SAVE_BUTTON_ID = "ideConfigureBuildPathFormSaveButton";

   private static final String CANCEL_BUTTON_ID = "ideConfigureBuildPathFormCancelButton";

   private static final String ADD_BUTTON_ID = "ideConfigureBuildPathFormAddButton";

   private static final String REMOVE_BUTTON_ID = "ideConfigureBuildPathFormRemoveButton";

   private static final String CHOOSE_SOURCE_VIEW_ID = "ideChooseSourcePathForm";

   private static final String CHOOSE_SOURCE_OK_BUTTON_ID = "ideChooseSourcePathFormOkButton";

   private static final String CHOOSE_SOURCE_CANCEL_BUTTON_ID = "ideChooseSourcePathFormCancelButton";

   private static final String CHOOSE_SOURCE_TREE_ID = "ideChooseSourcePathFormTreeGrid";

   /**
    * Wait, while Configure Classpath form appears.
    * 
    * @throws Exception
    */
   public void waitForClasspathDialogOpen() throws Exception
   {
      waitForElementPresent(CLASSPATH_VIEW_ID);
   }

   /**
    * Wait, while Configure Classpath view closed.
    * 
    * @throws Exception
    */
   public void waitForClasspathDialogClose() throws Exception
   {
      waitForElementNotPresent(CLASSPATH_VIEW_ID);
   }

   public void waitForChooseSourceViewOpened() throws Exception
   {
      waitForElementPresent(CHOOSE_SOURCE_VIEW_ID);
   }

   public void waitForChooseSourceViewClosed() throws Exception
   {
      waitForElementNotPresent(CHOOSE_SOURCE_VIEW_ID);
   }

   /**
    * Check, that Configure Classpath Dialog window appeared 
    * and has list grid and 4 buttons: add, remove, save, cancel.
    */
   public void checkConfigureClasspathDialog()
   {
      assertTrue(selenium().isElementPresent(CLASSPATH_VIEW_ID));
      assertTrue(selenium().isElementPresent(LIST_GRID_ID));
      assertTrue(selenium().isElementPresent(SAVE_BUTTON_ID));
      assertTrue(selenium().isElementPresent(ADD_BUTTON_ID));
      assertTrue(selenium().isElementPresent(REMOVE_BUTTON_ID));
      assertTrue(selenium().isElementPresent(CANCEL_BUTTON_ID));
   }

   public void clickCancelButton() throws Exception
   {
      selenium().click(CANCEL_BUTTON_ID);

      waitForElementNotPresent(CLASSPATH_VIEW_ID);
   }

   /**
    * Click add button.
    * @throws Exception
    */
   public void clickAddButton() throws Exception
   {
      selenium().click(ADD_BUTTON_ID);
   }

   /**
    * Click remove button.
    * @throws Exception
    */
   public void clickRemoveButton() throws Exception
   {
      selenium().click(REMOVE_BUTTON_ID);
   }

   /**
    * Click ok button.
    * @throws Exception
    */
   public void clickChooseSourceOkButton() throws Exception
   {
      selenium().click(CHOOSE_SOURCE_OK_BUTTON_ID);
   }

   /**
    * Click cancel button.
    * @throws Exception
    */
   public void clickChooseSourceCancelButton() throws Exception
   {
      selenium().click(CHOOSE_SOURCE_CANCEL_BUTTON_ID);
   }

   /**
    * Click save button.
    * @throws Exception
    */
   public void clickSaveButton() throws Exception
   {
      selenium().click(SAVE_BUTTON_ID);
   }

   /**
    * Check, that ChooseSource Dialog window appeared 
    * and has tree grid and 2 buttons: add anc cancel.
    */
   public void checkChooseSourceWindow()
   {
      assertTrue(selenium().isElementPresent(CHOOSE_SOURCE_VIEW_ID));
      assertTrue(selenium().isElementPresent(CHOOSE_SOURCE_TREE_ID));
      assertTrue(selenium().isElementPresent(CHOOSE_SOURCE_OK_BUTTON_ID));
      assertTrue(selenium().isElementPresent(CHOOSE_SOURCE_CANCEL_BUTTON_ID));
   }

   /**
    * Check, that all workspaces are present in Choose Source tree grid.
    * 
    * @param titles - workspaces titles
    */
   public void checkElementsInChooseSourceTreeGrid(String... titles)
   {
      for (String title : titles)
      {
         String locator =
            "//div[@id=\"" + CHOOSE_SOURCE_TREE_ID + "\"]//div[@class=\"ide-Tree-label\" and contains(text(), \""
               + title + "\")]";
         assertTrue(selenium().isElementPresent(locator));
      }
   }

   /**
    * Select item in tree grid from Choose Source Window.
    * 
    * @param title - the item title
    * @throws Exception
    */
   public void selectItemInChooseSourceTree(String title) throws Exception
   {
      String locator =
         "//div[@id=\"" + CHOOSE_SOURCE_TREE_ID + "\"]//div[@class=\"ide-Tree-label\" and contains(text(), \"" + title
            + "\")]";
      selenium().clickAt(locator, "0");
   }

   /**
    * Click on open img of folder in Choose Source list grid.
    * 
    * @param title - folder name
    * 
    * @throws Exception
    */
   public void openFolderInChooseSourceTree(String title) throws Exception
   {
      String locator =
         "//div[@id=\"" + CHOOSE_SOURCE_TREE_ID + "\"]//div[@class=\"ide-Tree-label\" and contains(text(), \"" + title
            + "\")]";
      selenium().doubleClickAt(locator, "0");
   }

   public void checkAddButtonEnabledState(boolean isEnabled)
   {
      String attribute = selenium().getAttribute("//div[@id='" + ADD_BUTTON_ID + "']/@button-enabled");
      boolean buttonEnabled = Boolean.parseBoolean(attribute);
      assertEquals(isEnabled, buttonEnabled);
   }

   public void checkSaveButtonEnabledState(boolean isEnabled)
   {
      String attribute = selenium().getAttribute("//div[@id='" + SAVE_BUTTON_ID + "']/@button-enabled");
      boolean buttonEnabled = Boolean.parseBoolean(attribute);
      assertEquals(isEnabled, buttonEnabled);
   }

   public void checkRemoveButtonEnabledState(boolean isEnabled)
   {
      String attribute = selenium().getAttribute("//div[@id='" + REMOVE_BUTTON_ID + "']/@button-enabled");
      boolean buttonEnabled = Boolean.parseBoolean(attribute);
      assertEquals(isEnabled, buttonEnabled);
   }
   
   public void waitRemoveButtonEnabled(boolean enabled) throws Exception
   {
      waitForElementPresent("//div[@id='" + REMOVE_BUTTON_ID + "' and @button-enabled=\""+enabled+"\"]");
   }

   public void checkCancelButtonEnabledState(boolean isEnabled)
   {
      String attribute = selenium().getAttribute("//div[@id='" + CANCEL_BUTTON_ID + "']/@button-enabled");
      boolean buttonEnabled = Boolean.parseBoolean(attribute);
      assertEquals(isEnabled, buttonEnabled);
   }

   /**
    * Check is button in "Configure Build Classpath" dialog enabled or disabled.
    * 
    * @param buttonTitle - the button title
    * @param enabled - is enabled
    */
   public void checkChooseSourceOkButtonEnabledState(boolean enabled)
   {
      String attribute = selenium().getAttribute("//div[@id='" + CHOOSE_SOURCE_OK_BUTTON_ID + "']/@button-enabled");
      boolean buttonEnabled = Boolean.parseBoolean(attribute);
      assertEquals(enabled, buttonEnabled);
   }

   /**
    * Check is button in "Configure Build Classpath" dialog enabled or disabled.
    * 
    * @param buttonTitle - the button title
    * @param enabled - is enabled
    */
   public void checkChooseSourceCancelButtonEnabledState(boolean enabled)
   {
      String attribute = selenium().getAttribute("//div[@id='" + CHOOSE_SOURCE_CANCEL_BUTTON_ID + "']/@button-enabled");
      boolean buttonEnabled = Boolean.parseBoolean(attribute);
      assertEquals(enabled, buttonEnabled);
   }

   public void checkItemsCountInClasspathGrid(int size)
   {
      int rows = selenium().getXpathCount("//table[@id=\"" + LIST_GRID_ID + "\"]/tbody[1]/tr").intValue();
      assertEquals(size, rows);
   }

   public String getPathByIndex(int index)
   {
      String locator = "//table[@id=\"" + LIST_GRID_ID + "\"]/tbody[1]/tr[" + index + "]";
      return selenium().getText(locator);
   }

   public void selectRowInListGrid(int index)
   {
      String locator = "//table[@id=\"" + LIST_GRID_ID + "\"]/tbody[1]/tr[" + index + "]//span";
      selenium().clickAt(locator, "0");
   }
}
