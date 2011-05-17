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

import org.exoplatform.ide.ToolbarCommands;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Properties May 12, 2011 12:15:53 PM evgen $
 *
 */
public class Properties extends AbstractTestModule
{

   private final String PROPERTY_LOCATOR = "//div[@view-id='ideFilePropertiesView']//td[@propertyName='%1s']";
   
   public static final String PROPERTIES_FORM_LOCATOR = "//div[@view-id=\"ideFilePropertiesView\"]";
   
   /**
    * Get Autoload property value
    * @return String value
    */
   public String getAutoloadProperty()
   {
      String locator = String.format(PROPERTY_LOCATOR, "Autoload");
      return selenium().getText(locator);
   }

   /**
    * Get Content Node Type property value 
    * @return String value
    */
   public String getContentNodeType()
   {
      String locator = String.format(PROPERTY_LOCATOR, "Content Node Type");
      return selenium().getText(locator);
   }

   /**
    * Get Content Length property value.
    * @return String value
    */
   public String getContentLength()
   {
      String locator = String.format(PROPERTY_LOCATOR, "Content Length");
      return selenium().getText(locator);
   }
   
   /**
    * Get Content Type property value
    * @return String value
    */
   public String getContentType()
   {
      String locator = String.format(PROPERTY_LOCATOR, "Content Type");
      return selenium().getText(locator);
   }

   /**
    * Get Display Name property value
    * @return String value
    */
   public String getDisplayName()
   {
      String locator = String.format(PROPERTY_LOCATOR, "Display Name");
      return selenium().getText(locator);
   }

   /**
    * Get File Node Type property value
    * @return String value
    */
   public String getFileNodeType()
   {
      String locator = String.format(PROPERTY_LOCATOR, "File Node Type");
      return selenium().getText(locator);
   }

   /**
    * Close Properties View
    */
   public void closeProperties() throws Exception
   {
      selenium().click("//div[@button-name='close-tab' and @tab-title='Properties']");
      waitForElementNotPresent(PROPERTIES_FORM_LOCATOR);
   }

   public void openProperties() throws Exception
   {
      IDE().TOOLBAR.runCommand(ToolbarCommands.View.SHOW_PROPERTIES);
      waitForElementPresent(PROPERTIES_FORM_LOCATOR);
   }
   
   /**
    * Wait for properties view to be opened.
    * 
    * @throws Exception
    */
   public void waitForPropertiesViewOpened() throws Exception
   {
      waitForElementPresent(PROPERTIES_FORM_LOCATOR);
   }
}
