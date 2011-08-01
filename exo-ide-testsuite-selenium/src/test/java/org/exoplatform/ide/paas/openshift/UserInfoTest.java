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
package org.exoplatform.ide.paas.openshift;

import java.net.URL;

import org.exoplatform.common.http.client.HTTPConnection;
import org.exoplatform.common.http.client.HTTPResponse;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.Utils;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class UserInfoTest extends BaseTest
{
   
   private static final String TEST_FOLDER = UserInfoTest.class.getSimpleName();
   
   @Before
   public void setUp() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + TEST_FOLDER);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + TEST_FOLDER);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @After
   public void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + TEST_FOLDER);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
   private void resetServices() throws Exception {
      String resetGitServiceURL = BASE_URL + REST_CONTEXT + "/ide/git-repo/reset";
      System.out.println("reset URL1 > [" + resetGitServiceURL + "]");
      
      URL url1 = new URL(resetGitServiceURL);
      HTTPConnection connection1 = Utils.getConnection(url1);
      HTTPResponse response1 = connection1.Post(url1.getFile());
      System.out.println("STATUS1 > " + response1.getStatusCode());
      
      String resetExpressServiceURL = BASE_URL + REST_CONTEXT + "/ide/openshift/express/reset";
      System.out.println("reset URL2 > [" + resetExpressServiceURL + "]");
      
      URL url2 = new URL(resetExpressServiceURL);
      HTTPConnection connection2 = Utils.getConnection(url2);
      HTTPResponse response2 = connection2.Post(url2.getFile());
      System.out.println("STATUS2 > " + response2.getStatusCode());
      
   }
   
   @Test
   public void testShowUserInfo() throws Exception {
      resetServices();
      
      /*
       * Wait while IDE has been successfully initialized.
       */
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      Thread.sleep(1000);

      /*
       * Clear Output
       */
      IDE.OUTPUT.clearOutputIfIsOpened();

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.NAVIGATION.createFolder("application-1");

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.NAVIGATION.createFolder("application-2");
      
      /*
       *  Create test domain "dd1"
       */
      IDE.OPENSHIFT.createDomain("dd1");
      
      /*
       * Create application "app1"
       */
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/application-1/");
      IDE.OPENSHIFT.createApplication("app1");
      
      /*
       * Create application "app2"
       */
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/application-2/");
      IDE.OPENSHIFT.createApplication("app2");

      IDE.OPENSHIFT.USER_INFO.openUserInformationWindow();
      
      assertEquals("an4ous@bigmir.net", IDE.OPENSHIFT.USER_INFO.getLogin());
      assertEquals("dd1", IDE.OPENSHIFT.USER_INFO.getDomain());
      
      IDE.OPENSHIFT.USER_INFO.selectApplication("app1");
      
      assertEquals("Name", IDE.OPENSHIFT.USER_INFO.getTextFromAppInfoTble(1, 1));
      assertEquals("app1", IDE.OPENSHIFT.USER_INFO.getTextFromAppInfoTble(1, 2));
      
      assertEquals("Type", IDE.OPENSHIFT.USER_INFO.getTextFromAppInfoTble(2, 1));
      assertEquals("rack-1.1", IDE.OPENSHIFT.USER_INFO.getTextFromAppInfoTble(2, 2));

      assertEquals("Public URL", IDE.OPENSHIFT.USER_INFO.getTextFromAppInfoTble(3, 1));
      assertEquals("http://app1-dd1.rhcloud.com/", IDE.OPENSHIFT.USER_INFO.getTextFromAppInfoTble(3, 2));

      assertEquals("Git URL", IDE.OPENSHIFT.USER_INFO.getTextFromAppInfoTble(4, 1));
      assertEquals("ssh://04fc0584021b4a9da8d95cd6383b12e4@app1-dd1.rhcloud.com/~/git/app1.git/", IDE.OPENSHIFT.USER_INFO.getTextFromAppInfoTble(4, 2));
      
      assertEquals("Creation time", IDE.OPENSHIFT.USER_INFO.getTextFromAppInfoTble(5, 1));
      assertEquals("1970 Jan 1 03:00:00", IDE.OPENSHIFT.USER_INFO.getTextFromAppInfoTble(5, 2));

      IDE.OPENSHIFT.USER_INFO.selectApplication("app2");
      
      assertEquals("Name", IDE.OPENSHIFT.USER_INFO.getTextFromAppInfoTble(1, 1));
      assertEquals("app2", IDE.OPENSHIFT.USER_INFO.getTextFromAppInfoTble(1, 2));
      
      assertEquals("Type", IDE.OPENSHIFT.USER_INFO.getTextFromAppInfoTble(2, 1));
      assertEquals("rack-1.1", IDE.OPENSHIFT.USER_INFO.getTextFromAppInfoTble(2, 2));

      assertEquals("Public URL", IDE.OPENSHIFT.USER_INFO.getTextFromAppInfoTble(3, 1));
      assertEquals("http://app2-dd1.rhcloud.com/", IDE.OPENSHIFT.USER_INFO.getTextFromAppInfoTble(3, 2));

      assertEquals("Git URL", IDE.OPENSHIFT.USER_INFO.getTextFromAppInfoTble(4, 1));
      assertEquals("ssh://04fc0584021b4a9da8d95cd6383b12e4@app2-dd1.rhcloud.com/~/git/app2.git/", IDE.OPENSHIFT.USER_INFO.getTextFromAppInfoTble(4, 2));
      
      assertEquals("Creation time", IDE.OPENSHIFT.USER_INFO.getTextFromAppInfoTble(5, 1));
      assertEquals("1970 Jan 1 03:00:00", IDE.OPENSHIFT.USER_INFO.getTextFromAppInfoTble(5, 2));
   }

}
