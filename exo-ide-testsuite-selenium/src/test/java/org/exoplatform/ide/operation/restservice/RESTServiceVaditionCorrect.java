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

import static org.junit.Assert.*;

import org.exoplatform.ide.BaseTest;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RESTServiceVaditionCorrect extends BaseTest
{
   @Test
   public void testValidaton() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(1000);
      openNewFileFromToolbar("REST Service");
      Thread.sleep(1000);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_PAGE_DOWN);
      for (int i = selenium.getText("//body[@class='editbox']").length() - 1; i >= 0; i--)
      {
         selenium.keyPress("//body[@class='editbox']", "\\8");
      }
      
      typeText("// simple groovy script\n" + 
         "import javax.ws.rs.Path\n" + 
         "import javax.ws.rs.GET\n" +
         "import javax.ws.rs.PathParam\n \n" +
         "@Path(\"/\")\n" + 
         "public class HelloWorld {\n" + 
         "@GET\n" + 
         "@Path(\"helloworld/{name}\")\n" +
         "public String hello(@PathParam(\"name\") String name) {\n" + 
         "return \"Hello \" + name\n" + 
         "}\n" + 
         "}\n");
      
      selenium.mouseDownAt("//div[@title='Validate REST Service']//img", "");
      selenium.mouseUpAt("//div[@title='Validate REST Service']//img", "");
      
      Thread.sleep(500);
      
      assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"ideOperationFormTabSet\"]/tab[ID=Output]/"));
      assertEquals("[INFO] Untitled file.groovy validated successfully.", selenium.getText("//font[@color='#007700']"));
      
      closeTab("0");
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton");
   }
}
