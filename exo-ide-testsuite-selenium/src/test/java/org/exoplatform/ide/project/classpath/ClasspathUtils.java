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
package org.exoplatform.ide.project.classpath;

import static org.junit.Assert.*;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;

import com.thoughtworks.selenium.Selenium;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Jan 13, 2011 $
 *
 */
public class ClasspathUtils
{
   private static final Selenium selenium;
   
   public interface Locators
   {
      public static final String CONFIGURE_CLASSPATH_DIALOG = "//div[@eventproxy='ideConfigureBuildPathForm']";
      
      public static final String CHOOSE_SOURCE_WINDOW = "//div[@eventproxy='ideChooseSourcePathForm']";
      
      public static final String SC_CONFIGURE_CLASSPATH_DIALOG = "scLocator=//Window[ID=\"ideConfigureBuildPathForm\"]";
      
      public static final String SC_CONFIGURE_CLASSPATH_DIALOG_HEADER = SC_CONFIGURE_CLASSPATH_DIALOG + "/header";
      
      public static final String SC_ADD_BTN = "scLocator=//IButton[ID=\"ideConfigureBuildPathFormAddButton\"]";
      
      public static final String SC_REMOVE_BTN = "scLocator=//IButton[ID=\"ideConfigureBuildPathFormRemoveButton\"]";
      
      public static final String SC_SAVE_BTN = "scLocator=//IButton[ID=\"ideConfigureBuildPathFormSaveButton\"]";
      
      public static final String SC_CANCEL_BTN = "scLocator=//IButton[ID=\"ideConfigureBuildPathFormCancelButton\"]";
      
      public static final String SC_CLASSPATH_LISTGRID = "scLocator=//ListGrid[ID=\"ideClassPathEntryListGrid\"]";
      
      public static final String SC_CHOOSE_SOURCE_WINDOW = "scLocator=//Window[ID=\"ideChooseSourcePathForm\"]";
      
      public static final String SC_CHOOSE_SOURCE_WINDOW_HEADER = SC_CHOOSE_SOURCE_WINDOW + "/headerLabel/";
      
      public static final String SC_CHOOSE_SOURCE_TREEGRID = "scLocator=//TreeGrid[ID=\"ideChooseSourcePathFormTreeGrid\"]";
      
      public static final String SC_CHOOSE_SOURCE_OK_BTN = "scLocator=//IButton[ID=\"ideChooseSourcePathFormOkButton\"]";
      
      public static final String SC_CHOOSE_SOURCE_CANCEL_BTN = "scLocator=//IButton[ID=\"ideChooseSourcePathFormCancelButton\"]";
   }
   
   public interface TITLES
   {
      public static final String ADD = "Add...";
      
      public static final String REMOVE = "Remove";
      
      public static final String SAVE = "Save";
      
      public static final String CANCEL = "Cancel";
      
      public static final String OK = "Ok";
      
      public static final String CLASSPATH_DIALOG_TITLE = "Configure Classpath";
      
      public static final String CHOOSE_SOURCE_WINDOW_TITLE = "Choose source path";
   }
   
   static
   {
      selenium = BaseTest.selenium;
   }
   
   /*
    * ============== Configure Classpath Dialog actions ==============
    */
   
   /**
    * Check, that Configure Classpath Dialog window appeared 
    * and has list grid and 4 buttons: add, remove, save, cancel.
    */
   public static void checkConfigureClasspathDialog()
   {
      assertTrue(selenium.isElementPresent(Locators.SC_CONFIGURE_CLASSPATH_DIALOG));
      assertTrue(selenium.isElementPresent(Locators.SC_CLASSPATH_LISTGRID));
      assertTrue(selenium.isElementPresent(Locators.SC_ADD_BTN));
      assertTrue(selenium.isElementPresent(Locators.SC_REMOVE_BTN));
      assertTrue(selenium.isElementPresent(Locators.SC_SAVE_BTN));
      assertTrue(selenium.isElementPresent(Locators.SC_CANCEL_BTN));
   }
   
   /**
    * Check is button in "Configure Build Classpath" dialog enabled or disabled.
    * 
    * @param buttonTitle - the button title
    * @param enabled - is enabled
    */
   static void checkConfigureClasspathButtonEnabled(String buttonTitle, boolean enabled)
   {
      if (enabled)
      {
         //check that not only td with class 'buttonTitle' present,
         //but 
         assertTrue(selenium.isElementPresent(Locators.CONFIGURE_CLASSPATH_DIALOG 
            + "//td[@class='buttonTitle' and text()='" + buttonTitle + "']")
            || selenium.isElementPresent(Locators.CONFIGURE_CLASSPATH_DIALOG 
               + "//td[@class='buttonTitleOver' and text()='" + buttonTitle + "']"));
         assertFalse(selenium.isElementPresent(Locators.CONFIGURE_CLASSPATH_DIALOG 
            + "//td[@class='buttonTitleDisabled' and text()='" + buttonTitle + "']")
            && selenium.isElementPresent(Locators.CONFIGURE_CLASSPATH_DIALOG 
               + "//td[@class='buttonTitleDisabledOver' and text()='" + buttonTitle + "']"));
      }
      else
      {
         assertTrue(selenium.isElementPresent(Locators.CONFIGURE_CLASSPATH_DIALOG 
            + "//td[@class='buttonTitleDisabled' and text()='" + buttonTitle + "']")
            || selenium.isElementPresent(Locators.CONFIGURE_CLASSPATH_DIALOG 
               + "//td[@class='buttonTitleDisabledOver' and text()='" + buttonTitle + "']"));
         assertFalse(selenium.isElementPresent(Locators.CONFIGURE_CLASSPATH_DIALOG 
            + "//td[@class='buttonTitle' and text()='" + buttonTitle + "']")
            && selenium.isElementPresent(Locators.CONFIGURE_CLASSPATH_DIALOG 
               + "//td[@class='buttonTitleOver' and text()='" + buttonTitle + "']"));
      }
   }
   
   static String getScListGridEntryLocator(int row, int col)
   {
      return Locators.SC_CLASSPATH_LISTGRID + "/body/row[" + row + "]/col[" + col + "]";
   }
   
   /*
    * ============== Choose Source Window actions ==============
    */
   
   /**
    * Check, that ChooseSource Dialog window appeared 
    * and has tree grid and 2 buttons: add anc cancel.
    */
   static void checkChooseSourceWindow()
   {
      assertTrue(selenium.isElementPresent(Locators.SC_CHOOSE_SOURCE_WINDOW));
      assertEquals(TITLES.CHOOSE_SOURCE_WINDOW_TITLE, selenium.getText(Locators.SC_CHOOSE_SOURCE_WINDOW_HEADER));
      assertTrue(selenium.isElementPresent(Locators.SC_CHOOSE_SOURCE_TREEGRID));
      assertTrue(selenium.isElementPresent(Locators.SC_CHOOSE_SOURCE_OK_BTN));
      assertTrue(selenium.isElementPresent(Locators.SC_CHOOSE_SOURCE_CANCEL_BTN));
   }
   
   /**
    * Check is button in "Configure Build Classpath" dialog enabled or disabled.
    * 
    * @param buttonTitle - the button title
    * @param enabled - is enabled
    */
   static void checkChooseSourceButtonEnabled(String buttonTitle, boolean enabled)
   {
      if (enabled)
      {
         //check that not only td with class 'buttonTitle' present,
         //but 
         assertTrue(selenium.isElementPresent(Locators.CHOOSE_SOURCE_WINDOW 
            + "//td[@class='buttonTitle' and text()='" + buttonTitle + "']")
            || selenium.isElementPresent(Locators.CHOOSE_SOURCE_WINDOW 
               + "//td[@class='buttonTitleOver' and text()='" + buttonTitle + "']"));
         assertFalse(selenium.isElementPresent(Locators.CHOOSE_SOURCE_WINDOW 
            + "//td[@class='buttonTitleDisabled' and text()='" + buttonTitle + "']")
            && selenium.isElementPresent(Locators.CHOOSE_SOURCE_WINDOW 
               + "//td[@class='buttonTitleDisabledOver' and text()='" + buttonTitle + "']"));
      }
      else
      {
         assertTrue(selenium.isElementPresent(Locators.CHOOSE_SOURCE_WINDOW 
            + "//td[@class='buttonTitleDisabled' and text()='" + buttonTitle + "']")
            || selenium.isElementPresent(Locators.CHOOSE_SOURCE_WINDOW 
               + "//td[@class='buttonTitleDisabledOver' and text()='" + buttonTitle + "']"));
         assertFalse(selenium.isElementPresent(Locators.CHOOSE_SOURCE_WINDOW 
            + "//td[@class='buttonTitle' and text()='" + buttonTitle + "']")
            && selenium.isElementPresent(Locators.CHOOSE_SOURCE_WINDOW 
               + "//td[@class='buttonTitleOver' and text()='" + buttonTitle + "']"));
      }
   }
   
   /**
    * Check, that all workspaces are present in Choose Source tree grid.
    * 
    * @param titles - workspaces titles
    */
   static void checkElementsInChooseSourceTreeGrid(String... titles)
   {
      for (String title : titles)
      {
         assertTrue(selenium.isElementPresent(getScChooseSourceTreegridLocator(title, 0)));
      }
   }
   
   /**
    * Get locator for element in Choose Source tree grid.
    * 
    * @param rowTitle - title of element
    * @param col - column number (0 - the title, 1 - for selection).
    * @return {@link String}
    */
   static String getScChooseSourceTreegridLocator(String rowTitle, int col)
   {
      return Locators.SC_CHOOSE_SOURCE_TREEGRID + "/body/row[name=" + rowTitle + "]/col[" + col + "]";
   }
   
   /**
    * Select item in tree grid from Choose Source Window.
    * 
    * @param title - the item title
    * @throws Exception
    */
   static void selectItemInChooseSourceTreegrid(String title) throws Exception
   {
      selenium.click(getScChooseSourceTreegridLocator(title, 1));
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }
   
   /**
    * Click on open img of folder in Choose Source list grid.
    * 
    * @param title - folder name
    * 
    * @throws Exception
    */
   static void openFolderInChooseSourceTreegrid(String title) throws Exception
   {
      selenium.click(getScChooseSourceTreegridLocator(title, 0) + "/open");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   /*
    * ============== Buttons actions ==============
    */
   
   /**
    * Click save button.
    * @throws Exception
    */
   static void clickSave() throws Exception
   {
      selenium.click(Locators.SC_SAVE_BTN);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   /**
    * Click cancel button.
    * @throws Exception
    */
   public static void clickCancel() throws Exception
   {
      selenium.click(Locators.SC_CANCEL_BTN);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   /**
    * Click add button.
    * @throws Exception
    */
   static void clickAdd() throws Exception
   {
      selenium.click(Locators.SC_ADD_BTN);
      Thread.sleep(TestConstants.SLEEP);
   }
   
   /**
    * Click remove button.
    * @throws Exception
    */
   static void clickRemove() throws Exception
   {
      selenium.click(Locators.SC_REMOVE_BTN);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   /**
    * Click ok button.
    * @throws Exception
    */
   static void clickOk() throws Exception
   {
      selenium.click(ClasspathUtils.Locators.SC_CHOOSE_SOURCE_OK_BTN);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }

}
