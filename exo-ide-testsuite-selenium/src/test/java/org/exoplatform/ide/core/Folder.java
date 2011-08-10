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

import org.exoplatform.ide.TestConstants;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Folder.java May 13, 2011 4:52:00 PM vereshchaka $
 *
 */
public class Folder extends AbstractTestModule
{
   
   private static final String FOLDER_CREATE_FORM_ID = "ideCreateFolderForm";
   
   private static final String INPUT_FIELD_NAME = "ideCreateFolderFormNameField";
   
   private static final String CREATE_BUTTON_ID = "ideCreateFolderFormCreateButton";
   
   public void waitForDialog() throws Exception
   {
      waitForElementPresent("//div[@view-id='" + FOLDER_CREATE_FORM_ID + "']");
   }
   
   public void typeFolderName(String name) throws InterruptedException
   {
      selenium().type(INPUT_FIELD_NAME, name);
      Thread.sleep(TestConstants.SLEEP_SHORT);
   }
   
   public void clickCreateButton() throws Exception
   {
      selenium().click(CREATE_BUTTON_ID);

      waitForElementNotPresent(FOLDER_CREATE_FORM_ID);
   }

}
