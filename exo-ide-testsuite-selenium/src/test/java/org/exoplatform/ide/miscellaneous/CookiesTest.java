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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 14, 2010 $
 *
 */
public class CookiesTest extends BaseTest
{

   private final static String FILE_NAME = "zxcvjnklzxbvlczkxbvlkbnlsf";

   private final static String TEST_FOLDER = CookiesTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + TEST_FOLDER + "/";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(
            "src/test/resources/org/exoplatform/ide/operation/restservice/RESTServiceGetURL.groovy",
            MimeType.GROOVY_SERVICE, URL + FILE_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testCookies() throws Exception
   {
      //wait
      IDE.WORKSPACE.waitForRootItem();
      //select
      IDE.WORKSPACE.selectRootItem();
      //refresh
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(TEST_FOLDER);
      //select and open file
      IDE.WORKSPACE.selectItem(TEST_FOLDER);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(TEST_FOLDER + "/" + FILE_NAME);
      IDE.WORKSPACE.doubleClickOnFile(TEST_FOLDER + "/" + FILE_NAME);

      //Chek cookies
      String[] cookies = selenium().getCookie().split("; ");

      assertTrue(cookies.length > 0);

      List<Boolean> listUserCookies = new ArrayList<Boolean>();
      for (int i = 0; i < cookies.length; i++)
      {
         if (cookies[i].startsWith("eXo-IDE-" + USER_NAME))
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
         VirtualFileSystemUtils.delete(URL);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
}
