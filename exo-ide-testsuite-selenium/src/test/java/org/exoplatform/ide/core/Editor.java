/**
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
 *
 */

package org.exoplatform.ide.core;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.exoplatform.ide.Locators;
import org.exoplatform.ide.SaveFileUtils;
import org.exoplatform.ide.TestConstants;

import com.thoughtworks.selenium.Selenium;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Editor
{
   
   private Selenium selenium;
   
   public Editor(Selenium selenium) {
      this.selenium = selenium;
   }
   
   /**
    * Returns the title of the tab with the pointed index.
    * 
    * @param index tab index
    * @return {@link String} tab's title
    * @throws Exception
    */
   public String getTabTitle(int index) throws Exception
   {
      return selenium.getText("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=" + index + "]/title");
   }   
   
   /**
    * Click on editor tab to make it active.
    * 
    * Numbering of tabs starts with 0.
    * 
    * @param tabIndex index of tab
    * @throws Exception
    */
    public void selectTab(int tabIndex) throws Exception
    {
       selenium.click("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=" + String.valueOf(tabIndex) + "]/");
       Thread.sleep(TestConstants.REDRAW_PERIOD);
    }

    /**
     * Close tab by it's index.
     * 
     * @param index numeration starts with 0 index
     */
    public void closeTab(int index) throws Exception
    {
       selenium.mouseOver(Locators.getTabCloseButtonLocator(index));
       Thread.sleep(TestConstants.REDRAW_PERIOD);

       selenium.click(Locators.getTabCloseButtonLocator(index));
       Thread.sleep(TestConstants.REDRAW_PERIOD);
    }    
    
    /**
     * In case tab with index as tabIndex is exist in editor, close it without saving. 
     * 
    * @param tabIndex
    */
   public void tryCloseTabWithNonSaving(int tabIndex) throws Exception {
      //if file is opened, close it
      if (selenium.isElementPresent("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=" + tabIndex + "]/icon"))
      {
         closeTabWithNonSaving(tabIndex);
      }       
    }

    /**
     * Close tab in editor. Close ask window in case it appear while closing.
     * 
    * @param tabIndex
    * @throws Exception
    */
   public void closeTabWithNonSaving(int tabIndex) throws Exception
    {
       closeTab(tabIndex);

       //check is warning dialog appears
       if (selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header[contains(text(), 'Close file')]"))
       {
          //click No button
          selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
          Thread.sleep(TestConstants.SLEEP);
       }
    }
    
    /**
     * Close unsaved file without saving it.
     * 
     * Close tab with tabIndex. Check is warning dialog appears.
     * Click No (Discard) button if file is new.
     * 
     * @param tabIndex index of tab to close
     * @throws Exception
     */
    public void closeUnsavedFileAndDoNotSave(int tabIndex) throws Exception
    {
       //check, that file is unsaved
       final String tabName = getTabTitle(Integer.valueOf(tabIndex));
       assertTrue(tabName.endsWith("*"));
       closeTab(tabIndex);

       /*
        * close existed file
        * SmartGWT not destroy warning dialog(only hide, maybe set smoller z-index property ),
        * so need check is warning dialogs is visible
        */
       if (selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header[contains(text(), 'Close file')]")
          && selenium.isVisible("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header[contains(text(), 'Close file')]"))
       {
          //check is warning dialog appears
          assertTrue(selenium
             .isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header[contains(text(), 'Close file')]"));

          assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
          assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/"));

          //click No button
          selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/");
       }
       //close new file
       else if (selenium.isElementPresent(Locators.AskForValue.ASK_FOR_VALUE_DIALOG_LOCATOR)
                && selenium.isVisible(Locators.AskForValue.ASK_FOR_VALUE_DIALOG_LOCATOR))
       {
          selenium.click(Locators.AskForValue.ASK_FOR_VALUE_NO_BUTTON_LOCATOR);
       }
       else
       {
          fail("Unknown warning dialog!");
       }
       Thread.sleep(TestConstants.SLEEP);
    }
    
    /**
     * Close file tab, and don't see into file has changes or no.
     * 
     * If tab's title doesn't ends with *, simple close tab.
     * Otherwise, waits for warning dialog window and click No button.
     * 
     * @param tabIndex - index of tab with file to close
     * @throws Exception
     */
    public void closeFileTabIgnoreChanges(int tabIndex) throws Exception
    {
       //check, is file was changed
       final String tabName = getTabTitle(Integer.valueOf(tabIndex));
       
       if (tabName.endsWith("*"))
       {
          closeUnsavedFileAndDoNotSave(tabIndex);
       }
       else
       {
          closeTab(tabIndex);
       }
    }
    
    /**
     * Close new file. 
     * If saveFile true - save file.
     * If fileName is null - save with default name, else
     * save with fileName name.
     * 
     * @param tabIndex - index of tab in editor panel
     * @param saveFile - is save file before closing
     * @param fileName - name of new file
     * @throws Exception
     */
    public void closeNewFile(int tabIndex, boolean saveFile, String fileName) throws Exception
    {
       closeTab(tabIndex);

       if (saveFile)
       {
          SaveFileUtils.checkSaveAsDialogAndSave(fileName, true);
//          if(fileName != null)
//          {
//             AbstractTextUtil.getInstance().typeToInput(ASK_FOR_VALUE_TEXT_FIELD_LOCATOR, fileName, true);
//          }
//          selenium.click(ASK_FOR_VALUE_OK_BUTTON_LOCATOR);
       }
       else
       {
          SaveFileUtils.checkSaveAsDialog(true);
          selenium.click(Locators.AskForValue.ASK_FOR_VALUE_NO_BUTTON_LOCATOR);
       }
       Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);

    }    

}
