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
package org.exoplatform.ide.miscellaneous;

import static org.junit.Assert.assertEquals;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class AvailableDependenciesTest extends BaseTest
{

   @Test
   public void testAvailableDependencies() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();

      IDE.MENU.runCommand(MenuCommands.Help.HELP, MenuCommands.Help.AVAILABLE_DEPENDENCIES);
      IDE.AVAILABLE_DEPENDENCIES.waitOpened();
      IDE.AVAILABLE_DEPENDENCIES.waitForDependencies();
      assertEquals(0, IDE.AVAILABLE_DEPENDENCIES.getAttributeCount());

      //This is because of scroll problem - works only in Chrome:
      if (driver instanceof ChromeDriver)
      {
         IDE.AVAILABLE_DEPENDENCIES.selectDependency("exo-ide-framework-client");
         assertEquals("Apache Maven", IDE.AVAILABLE_DEPENDENCIES.getAttributeValue("Created-By"));
         assertEquals("eXo IDE :: Framework : Client",
            IDE.AVAILABLE_DEPENDENCIES.getAttributeValue("Implementation-Title"));
         assertEquals("eXo Platform SAS", IDE.AVAILABLE_DEPENDENCIES.getAttributeValue("Implementation-Vendor"));
         assertEquals("org.exoplatform.ide", IDE.AVAILABLE_DEPENDENCIES.getAttributeValue("Implementation-Vendor-Id"));

         IDE.AVAILABLE_DEPENDENCIES.selectDependency("exo-gwtframework-ui");
         assertEquals("Apache Maven", IDE.AVAILABLE_DEPENDENCIES.getAttributeValue("Created-By"));
         assertEquals("eXo GWT Framework :: UI", IDE.AVAILABLE_DEPENDENCIES.getAttributeValue("Implementation-Title"));
         assertEquals("eXo Platform SAS", IDE.AVAILABLE_DEPENDENCIES.getAttributeValue("Implementation-Vendor"));
         assertEquals("org.exoplatform.gwt", IDE.AVAILABLE_DEPENDENCIES.getAttributeValue("Implementation-Vendor-Id"));
      }
      else
      {
         IDE.AVAILABLE_DEPENDENCIES.selectDependency("activation");
         assertEquals("javax.activation", IDE.AVAILABLE_DEPENDENCIES.getAttributeValue("Extension-Name"));
         assertEquals("Sun Microsystems, Inc.", IDE.AVAILABLE_DEPENDENCIES.getAttributeValue("Implementation-Vendor"));
         assertEquals("com.sun", IDE.AVAILABLE_DEPENDENCIES.getAttributeValue("Implementation-Vendor-Id"));

         IDE.AVAILABLE_DEPENDENCIES.selectDependency("annotations-api");
         assertEquals("1.5.0_15-b04 (Sun Microsystems Inc.)",
            IDE.AVAILABLE_DEPENDENCIES.getAttributeValue("Created-By"));
         assertEquals("Apache Ant 1.7.0", IDE.AVAILABLE_DEPENDENCIES.getAttributeValue("Ant-Version"));
      }

      IDE.AVAILABLE_DEPENDENCIES.clickOkButton();
      IDE.AVAILABLE_DEPENDENCIES.waitClosed();
   }

}
