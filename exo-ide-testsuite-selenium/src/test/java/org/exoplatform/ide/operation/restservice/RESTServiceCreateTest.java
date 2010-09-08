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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RESTServiceCreateTest extends BaseTest
{

   private static final String FIRST_NAME = System.currentTimeMillis() + "test.groovy";

   private static final String SECOND_NAME = System.currentTimeMillis() + "новий.groovy";
   
   private final static String URL = BASE_URL + REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/";

   @Test
   public void testCreatingRESTService() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      createFileFromToolbar("REST Service");
      Thread.sleep(TestConstants.SLEEP);

      saveAsUsingToolbarButton(FIRST_NAME);

      Thread.sleep(TestConstants.SLEEP_SHORT);

      selenium.mouseDownAt("//div[@title='Show Properties']//img", "");
      selenium.mouseUpAt("//div[@title='Show Properties']//img", "");

      assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/"));

      assertEquals(
         "false",
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextAutoload||title=%3Cb%3EAutoload%3C%24fs%24b%3E||value=false||index=0||Class=StaticTextItem]/textbox"));
      assertEquals(
         TestConstants.NodeTypes.EXO_GROOVY_RESOURCE_CONTAINER,
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentNodeType||title=%3Cb%3EContent%20Node%20Type%3C%24fs%24b%3E||value=exo%3AgroovyResourceContainer||index=2||Class=StaticTextItem]/textbox"));
      assertEquals(MimeType.GROOVY_SERVICE,
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentType||title=%3Cb%3EContent%20Type%3C%24fs%24b%3E||value=application%24fs%24x-jaxrs-groovy||index=3||Class=StaticTextItem]/textbox"));
      assertEquals(
         FIRST_NAME,
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextDisplayName||title=%3Cb%3EDisplay%20Name%3C%24fs%24b%3E||value=новий.groove||index=5||Class=StaticTextItem]/textbox"));
      assertEquals(
         TestConstants.NodeTypes.NT_FILE,
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextFileNodeType||title=%3Cb%3EFile%20Node%20Type%3C%24fs%24b%3E||value=nt%3Afile||index=6||Class=StaticTextItem]/textbox"));

      selenium.click("scLocator=//TabSet[ID=\"ideOperationFormTabSet\"]/tab[ID=Properties]/icon");
      Thread.sleep(TestConstants.SLEEP_SHORT);

      assertFalse(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/"));

      selenium.mouseDownAt("//div[@title='Show Properties']//img", "");
      selenium.mouseUpAt("//div[@title='Show Properties']//img", "");
      Thread.sleep(TestConstants.SLEEP_SHORT);

      assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/"));

      saveAsUsingToolbarButton(SECOND_NAME);
      Thread.sleep(TestConstants.SLEEP);

      assertEquals(
         "false",
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextAutoload||title=%3Cb%3EAutoload%3C%24fs%24b%3E||value=false||index=0||Class=StaticTextItem]/textbox"));
      assertEquals(
         TestConstants.NodeTypes.EXO_GROOVY_RESOURCE_CONTAINER,
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentNodeType||title=%3Cb%3EContent%20Node%20Type%3C%24fs%24b%3E||value=exo%3AgroovyResourceContainer||index=2||Class=StaticTextItem]/textbox"));
      assertEquals(
         MimeType.GROOVY_SERVICE,
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentType||title=%3Cb%3EContent%20Type%3C%24fs%24b%3E||value=application%24fs%24x-jaxrs-groovy||index=3||Class=StaticTextItem]/textbox"));
      assertEquals(
         SECOND_NAME,
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextDisplayName||title=%3Cb%3EDisplay%20Name%3C%24fs%24b%3E||value=новий.groove||index=5||Class=StaticTextItem]/textbox"));
      assertEquals(
         TestConstants.NodeTypes.NT_FILE,
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextFileNodeType||title=%3Cb%3EFile%20Node%20Type%3C%24fs%24b%3E||value=nt%3Afile||index=6||Class=StaticTextItem]/textbox"));
   }
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL + FIRST_NAME);
         VirtualFileSystemUtils.delete(URL + URLEncoder.encode(SECOND_NAME, "UTF-8"));
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }
}
