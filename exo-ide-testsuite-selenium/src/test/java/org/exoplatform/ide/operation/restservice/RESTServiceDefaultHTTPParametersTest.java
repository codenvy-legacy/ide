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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RESTServiceDefaultHTTPParametersTest extends BaseTest
{

   private final static String FILE_NAME = RESTServiceDefaultHTTPParametersTest.class.getSimpleName()+".grs";

   private final static String PROJECT = "DefaultHTTPParameters";

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/DefaultHTTPParameters.groovy";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, MimeType.GROOVY_SERVICE, filePath);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testDefaultHTTPParameters() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      IDE.REST_SERVICE.deploy(PROJECT + "/" + FILE_NAME, 1);

      IDE.REST_SERVICE.launchRestService();
      checkParam();
      IDE.REST_SERVICE.setMethodFieldValue("GET");
      checkParam();

      IDE.REST_SERVICE.closeForm();
      IDE.REST_SERVICE.waitClosed();
   }

   /**
    *  Check Request parameters
    */
   private void checkParam()
   {
      assertEquals("TestQueryParam 1", IDE.REST_SERVICE.getQueryParameterName(1));
      assertEquals("boolean", IDE.REST_SERVICE.getQueryParameterType(1));
      assertEquals("true", IDE.REST_SERVICE.getQueryParameterDefaultValue(1));
      assertEquals("", IDE.REST_SERVICE.getQueryParameterValue(1));

      IDE.REST_SERVICE.selectHeaderParametersTab();

      assertEquals("Test-Header", IDE.REST_SERVICE.getHeaderParameterName(1));
      assertEquals("integer", IDE.REST_SERVICE.getHeaderParameterType(1));
      assertEquals("3", IDE.REST_SERVICE.getHeaderParameterDefaultValue(1));
      assertEquals("", IDE.REST_SERVICE.getHeaderParameterValue(1));

      IDE.REST_SERVICE.selectQueryParametersTab();
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

}
