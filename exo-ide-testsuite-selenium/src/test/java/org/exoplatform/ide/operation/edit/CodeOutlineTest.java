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
package org.exoplatform.ide.operation.edit;

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id:
 *
 */

public class CodeOutlineTest extends BaseTest
{

   @Test
   public void testXmlCodeOutline() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      
      Thread.sleep(1000);
      
      openNewFileFromToolbar("XML File");
      
      Thread.sleep(1000);

      assertTrue(getTextFromCodeEditor(0).startsWith("<?xml version='1.0' encoding='UTF-8'?>"));
      
      // go to the end of first line of text and paste empty line
      selenium.mouseDownAt("//body[@class='editbox']//span[2]", "");
      selenium.mouseUpAt("//body[@class='editbox']//span[2]", "");
      selenium.keyDown("//body[@class='editbox']", "\\35");
      selenium.keyDown("//body[@class='editbox']", "\\13");
      
      // clear document      
      String xml = "<web-app atr='1'>\n" +
      		"  <display-name atr='2'>\n" +
      		"    IDEall\n" +
      		"  </display-name>\n" +
      		"\n" +
      		"  <context-param>\n" +
      		"    <param-name>\n" +
      		"      portalContainerName\n" +
      		"    </param-name>\n" +
      		"\n" +
      		"    <param-value>\n" +
      		"      portal\n" +
      		"    </param-value>\n" +
      		"  </context-param>\n" +
      		"  \n" +
      		"  <!-- comment -->\n" +
      		"  \n" +
      		"  <context-param>\n" +
      		"    <param-name>portalContainerName</param-name>\n" +
      		"    <param-value>portal</param-value>\n" +
      		"  </context-param>\n" +
      		"\n" +
      		"  <![CDATA[ Hello, world!\n" +
      		"  ]]>\n" +
      		"\n" +
      		"  <filter>\n" +
      		"    <filter-name />\n" +
      		"    <filter-class>org.exoplatform.portal.application.ResourceRequestFilter</filter-class>\n" +
      		"  </filter>\n" +
      		"\n" +
      		"</web-app>";

      typeTextIntoEditor(0, xml);      

      Thread.sleep(500);
      
      clickOnToolbarButton("Show Outline");
      
      assertTrue(selenium.isElementPresent("scLocator=//TabSet[ID=\"isc_TabSet_2\"]/tab[ID=isc_OutlineForm_0]/"));
      
      // trying to walk thought the Outline Panel
      selenium.click("scLocator=//TreeGrid[ID=\"isc_OutlineTreeGrid_0\"]/body/row[name=display-name||1]/col[fieldName=name||0]");
      selenium.click("scLocator=//TreeGrid[ID=\"isc_OutlineTreeGrid_0\"]/body/row[name=context-param||2]/col[fieldName=name||0]");
      selenium.click("scLocator=//TreeGrid[ID=\"isc_OutlineTreeGrid_0\"]/body/row[name=CDATA||4]/col[fieldName=name||0]");
      selenium.click("scLocator=//TreeGrid[ID=\"isc_OutlineTreeGrid_0\"]/body/row[name=filter||5]/col[fieldName=name||0]");
      selenium.click("scLocator=//TreeGrid[ID=\"isc_OutlineTreeGrid_0\"]/body/row[name=context-param||2]/col[fieldName=name||0]/open");
      selenium.click("scLocator=//TreeGrid[ID=\"isc_OutlineTreeGrid_0\"]/body/row[name=filter||9]/col[fieldName=name||0]/open");
      selenium.click("scLocator=//TreeGrid[ID=\"isc_OutlineTreeGrid_0\"]/body/row[name=filter-name||10]/col[fieldName=name||0]");
      selenium.click("scLocator=//TreeGrid[ID=\"isc_OutlineTreeGrid_0\"]/body/row[name=filter-class||11]/col[fieldName=name||0]");      
            
      closeTab("0");      
   }
   
}
