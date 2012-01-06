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
package org.exoplatform.ide.miscellaneous;

import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.folder.DeleteSeveralFoldersSimultaneously;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.internal.seleniumemulation.GetCookie;

import java.io.IOException;
import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 14, 2010 $
 *
 */
public class CookiesTest extends BaseTest
{

   private final static String PROJECT = CookiesTest.class.getSimpleName();

   private final static String FILE_NAME = "zxcvjnklzxbvlczkxbvlkbnlsf";

   private final static String TEST_FOLDER = "Test";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + TEST_FOLDER + "/";

   @BeforeClass
   public static void setUp()
   {
      try
      {

         VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + TEST_FOLDER);

         VirtualFileSystemUtils.put(
            "src/test/resources/org/exoplatform/ide/operation/restservice/RESTServiceGetURL.groovy",
            MimeType.GROOVY_SERVICE, WS_URL + PROJECT + "/" + TEST_FOLDER + "/" + FILE_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testCookies() throws Exception
   {

      //goto ide, open project, folder in project and test-file
      IDE.PROJECT.EXPLORER.waitOpened();

      IDE.PROJECT.OPEN.openProject(PROJECT);

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FOLDER);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + TEST_FOLDER);

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FOLDER + "/" + FILE_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + TEST_FOLDER + "/" + FILE_NAME);

      //get all cookies and split string. Add substrins in string array
      String[] cookies = driver.manage().getCookies().toString().split("; ");

      //check cookies is not empty
      assertTrue(cookies.length > 0);

      //Chek value cookies
      List<Boolean> listUserCookies = new ArrayList<Boolean>();
      for (int i = 0; i < cookies.length; i++)
      {
         if (cookies[i].startsWith("domain=" + SELENIUM_HOST + ", eXo-IDE-" + USER_NAME))
         {
            listUserCookies.add(true);
         }
      }

      assertTrue(listUserCookies.size() >= 4);
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
