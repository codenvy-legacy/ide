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

import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 25, 2010 $
 *
 */
public class UndeployOnRunRESTServiceTest extends BaseTest
{

   private final static String PROJECT = UndeployOnRunRESTServiceTest.class.getSimpleName();

   private final static String SIMPLE_FILE_NAME = "RestServiceExample.grs";

   private final static String FILE_NAME = "kjfdshglksfdghldsfbg.grs";

   @Before
   public void beforeTest()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/restservice/";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, SIMPLE_FILE_NAME, MimeType.GROOVY_SERVICE, filePath
            + SIMPLE_FILE_NAME);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, MimeType.GROOVY_SERVICE, filePath
            + SIMPLE_FILE_NAME);
      }
      catch (IOException e)
      {
      }
   }

   @After
   public void afterTest()
   {
      try
      {
         /*       Utils.undeployService(BASE_URL, REST_CONTEXT, URL + SIMPLE_FILE_NAME);
                Utils.undeployService(BASE_URL, REST_CONTEXT, URL + FILE_NAME);*/
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
      }
   }

   @Test
   public void testUndeployOnRunRestService() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + SIMPLE_FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + SIMPLE_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + SIMPLE_FILE_NAME);

      IDE.REST_SERVICE.runRESTServiceInSanbox();

      IDE.REST_SERVICE.sendRequest();
      IDE.REST_SERVICE.waitClosed();

      IDE.OUTPUT.waitForMessageShow(4, 5);
      String text = IDE.OUTPUT.getOutputMessage(4);
      assertTrue(text.endsWith("/" + PROJECT + "/" + SIMPLE_FILE_NAME + " undeployed successfully."));

      IDE.EDITOR.closeFile(SIMPLE_FILE_NAME);
   }

   @Test
   public void testUndeloyOnCancel() throws Exception
   {
      driver.navigate().refresh();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      IDE.REST_SERVICE.runRESTServiceInSanbox();
      IDE.REST_SERVICE.closeForm();
      IDE.REST_SERVICE.waitClosed();

      IDE.OUTPUT.waitForMessageShow(3, 5);
      String text = IDE.OUTPUT.getOutputMessage(3);
      assertTrue(text.endsWith("/" + PROJECT + "/" + FILE_NAME + " undeployed successfully."));

      IDE.EDITOR.closeFile(FILE_NAME);
   }

}
