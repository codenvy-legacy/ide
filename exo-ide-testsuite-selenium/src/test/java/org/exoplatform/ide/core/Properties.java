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

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.Locators;
import org.exoplatform.ide.ToolbarCommands;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Properties May 12, 2011 12:15:53 PM evgen $
 *
 */
public class Properties extends AbstractTestModule
{

   /**
    * Get Autoload property value
    * @return String value
    */
   public String getAutoloadProperty()
   {
      return selenium().getText("Autoload");
   }

   /**
    * Get Content Node Type property value 
    * @return String value
    */
   public String getContentNodeType()
   {
      return selenium().getText("Content-Node-Type");
   }

   /**
    * Get Content Type property value
    * @return String value
    */
   public String getContentType()
   {
      return selenium().getText("Content-Type");
   }

   /**
    * Get Display Name property value
    * @return String value
    */
   public String getDisplayName()
   {
      return selenium().getText("Display-Name");
   }

   /**
    * Get File Node Type property value
    * @return String value
    */
   public String getFileNodeType()
   {
      return selenium().getText("File-Node-Type");
   }

   /**
    * Close Properties View
    */
   public void closeProperties() throws Exception
   {
      selenium().click("//div[@button-name='close-tab' and @tab-title='Properties']");
      waitForElementNotPresent(Locators.OperationForm.PROPERTIES_FORM_LOCATOR);
   }

   public void openProperties() throws Exception
   {
      IDE().TOOLBAR.runCommand(ToolbarCommands.View.SHOW_PROPERTIES);
      waitForElementPresent(Locators.OperationForm.PROPERTIES_FORM_LOCATOR);
   }

}
