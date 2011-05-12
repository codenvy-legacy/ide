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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.project.classpath.ClasspathUtils.Locators;

/**
 * Class for operations with classpath form (for configuring classpath of project).
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Project.java May 12, 2011 12:35:39 PM vereshchaka $
 *
 */
public class ClasspathProject extends AbstractTestModule
{

   private static final String CLASSPATH_FORM_ID = "ideConfigureBuildPathForm";
   
   private static final String LIST_GRID_ID = "ideClassPathEntryListGrid";
   
   private static final String SAVE_BUTTON_ID = "ideConfigureBuildPathFormSaveButton";
   
   private static final String CANCEL_BUTTON_ID = "ideConfigureBuildPathFormCancelButton";
   
   private static final String ADD_BUTTON_ID = "ideConfigureBuildPathFormAddButton";
   
   private static final String REMOVE_BUTTON_ID = "ideConfigureBuildPathFormRemoveButton";
   
   /**
    * Wait, while Configure Classpath form appears.
    * 
    * @throws Exception
    */
   public void waitForClasspathDialog() throws Exception
   {
      waitForElementPresent(CLASSPATH_FORM_ID);
   }
   
   /**
    * Check, that Configure Classpath Dialog window appeared 
    * and has list grid and 4 buttons: add, remove, save, cancel.
    */
   public void checkConfigureClasspathDialog()
   {
      assertTrue(selenium().isElementPresent(CLASSPATH_FORM_ID));
      assertTrue(selenium().isElementPresent(LIST_GRID_ID));
      assertTrue(selenium().isElementPresent(SAVE_BUTTON_ID));
      assertTrue(selenium().isElementPresent(ADD_BUTTON_ID));
      assertTrue(selenium().isElementPresent(REMOVE_BUTTON_ID));
      assertTrue(selenium().isElementPresent(CANCEL_BUTTON_ID));
   }
   
   public void clickCancelButton() throws Exception
   {
      selenium().click(CANCEL_BUTTON_ID);

      waitForElementNotPresent(CLASSPATH_FORM_ID);
   }
}
