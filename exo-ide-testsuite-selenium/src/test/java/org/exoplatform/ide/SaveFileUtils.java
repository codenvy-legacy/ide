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
package org.exoplatform.ide;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class SaveFileUtils
{

   /**
    * Check is dialog window Save as file appeared
    * and do all elements present.
    * 
    * Enter name to text field and click Ok button.
    * 
    * If name is null, will created with proposed default name.
    * 
    * Note: this method check only Yes and Cancel buttons, because it cat use
    * for saved and new files.
    * 
    * If you need to check extended window dialog (with Yes, Discard, Cancel buttons),
    * put noButtonPresent parameter to true
    * 
    * @param name file name
    * @param noButtonPresent is no button present in dialog window
    * @throws Exception
    */
   public static void checkSaveAsDialogAndSave(String name, boolean noButtonPresent) throws Exception
   {
      checkSaveAsDialog(noButtonPresent);
   
      //clearFocus();
      if (name != null)
      {
         IDE.getInstance().ASK_FOR_VALUE_DIALOG.setValue(name);
         //AbstractTextUtil.getInstance().typeToInput(ASK_FOR_VALUE_TEXT_FIELD_LOCATOR, name, true);
      }
   
      IDE.getInstance().ASK_FOR_VALUE_DIALOG.clickOkButton();
//      selenium.click(ASK_FOR_VALUE_OK_BUTTON_LOCATOR);
      
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);

      assertFalse(IDE.getInstance().ASK_FOR_VALUE_DIALOG.isOpened());
      //assertFalse(selenium.isElementPresent(ASK_FOR_VALUE_DIALOG_LOCATOR));
   }
   
   /**
    * Check, that all elements of Save As dialog window are present.
    * 
    * @param noButtonPresent - is No button must be present in Save As dialog
    * @throws Exception
    */
   public static void checkSaveAsDialog(boolean noButtonPresent) throws Exception
   {
      assertTrue(IDE.getInstance().ASK_FOR_VALUE_DIALOG.isOpened());
      assertEquals(noButtonPresent, IDE.getInstance().ASK_FOR_VALUE_DIALOG.isNoButtonPresent());
      
//      assertTrue(selenium.isElementPresent(ASK_FOR_VALUE_DIALOG_LOCATOR));
//      assertTrue(selenium.isTextPresent("Save file as"));
//      assertTrue(selenium.isElementPresent(ASK_FOR_VALUE_TEXT_FIELD_LOCATOR));
//      assertTrue(selenium.isElementPresent(ASK_FOR_VALUE_OK_BUTTON_LOCATOR));
//      assertTrue(selenium.isElementPresent(ASK_FOR_VALUE_CANCEL_BUTTON_LOCATOR));
//      if (noButtonPresent)
//      {
//         assertTrue(selenium.isElementPresent(ASK_FOR_VALUE_NO_BUTTON_LOCATOR));
//      }
   }
   
}
