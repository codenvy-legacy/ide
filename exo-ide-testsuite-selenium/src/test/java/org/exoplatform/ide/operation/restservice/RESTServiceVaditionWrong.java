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
package org.exoplatform.ide.operation.restservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RESTServiceVaditionWrong extends BaseTest
{

   @Test
   public void testValidaton() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(1000);
      openNewFileFromToolbar("REST Service");
      Thread.sleep(1000);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_END);

      typeText("1");
      Thread.sleep(500);

      selenium.mouseDownAt("//div[@title='Validate REST Service']//img", "");
      selenium.mouseUpAt("//div[@title='Validate REST Service']//img", "");

      Thread.sleep(500);

      assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideOperationFormTabSet\"]/tab[ID=Output]/"));
      System.out.println(selenium.getText("//font[@color='#880000']"));

      String mess = selenium.getText("//font[@color='#880000']");
      assertTrue(mess.contains("[ERROR] Untitled file.groovy validation failed. Error (400: Bad Request)"));

      assertTrue(mess.contains("Unexpected error. Error occurs when parse stream, compiler error:"));
      assertTrue(mess.contains("startup failed, Untitled file.groovy: 2: unable to resolve class javax.ws.rs.Path1"));
      assertTrue(mess
         .contains("@ line 2, column 1.Untitled file.groovy: 6: unable to resolve class Path , unable to find class for annotation"));
      assertTrue(mess
         .contains("@ line 6, column 1.Untitled file.groovy: 9: unable to resolve class Path , unable to find class for annotation"));
      assertTrue(mess.contains("@ line 9, column 3."));
      assertTrue(mess.contains("3 errors"));

      closeTab("0");
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton");
   }

}
