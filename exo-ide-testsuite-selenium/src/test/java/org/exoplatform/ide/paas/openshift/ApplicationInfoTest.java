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

import static org.junit.Assert.assertEquals;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ApplicationInfoTest extends BaseTest
{

   private static final String TEST_FOLDER = ApplicationInfoTest.class.getSimpleName();

   @Before
   public void setUp() throws Exception
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + TEST_FOLDER);
      }
      catch (Exception e)
      {
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
      }
   }

   /**
    * Test added to Ignore, because at the moment he is in progress of develop.
    * 
    * @throws Exception
    */
   @Ignore
   @Test
   public void testCreateApplicationAndGetItInfo() throws Exception
   {
      /*
       * Wait while IDE has been successfully initialized.
       */
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      Thread.sleep(1000);

      /*
       * Select TEST FOLDER
       */
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");

      /*
       * Clear Output
       */
      if (IDE.OUTPUT.isOpened())
      {
         IDE.OUTPUT.clickClearButton();
      }

      /*
       * Create test domain "domain1"
       */
      IDE.OPENSHIFT.createDomain("domain1");

      /*
       * Create application "appinfotest"
       */
      IDE.OPENSHIFT.createApplication("appinfotest");

      /*
       * Open Application Info Window
       */
      IDE.OPENSHIFT.APPLICATION_INFO.openApplicationInfoWindow();

      assertEquals("Name", IDE.OPENSHIFT.APPLICATION_INFO.getTextFromAppInfoTable(1, 1));
      assertEquals("appinfotest", IDE.OPENSHIFT.APPLICATION_INFO.getTextFromAppInfoTable(1, 2));

      assertEquals("Type", IDE.OPENSHIFT.APPLICATION_INFO.getTextFromAppInfoTable(2, 1));
      assertEquals("rack-1.1", IDE.OPENSHIFT.APPLICATION_INFO.getTextFromAppInfoTable(2, 2));

      assertEquals("Public URL", IDE.OPENSHIFT.APPLICATION_INFO.getTextFromAppInfoTable(3, 1));
      assertEquals("http://appinfotest-domain1.rhcloud.com/",
         IDE.OPENSHIFT.APPLICATION_INFO.getTextFromAppInfoTable(3, 2));

      assertEquals("Git URL", IDE.OPENSHIFT.APPLICATION_INFO.getTextFromAppInfoTable(4, 1));
      assertEquals("ssh://04fc0584021b4a9da8d95cd6383b12e4@appinfotest-domain1.rhcloud.com/~/git/appinfotest.git/",
         IDE.OPENSHIFT.APPLICATION_INFO.getTextFromAppInfoTable(4, 2));

      assertEquals("Creation time", IDE.OPENSHIFT.APPLICATION_INFO.getTextFromAppInfoTable(5, 1));
      assertEquals("1970 Jan 1 03:00:00", IDE.OPENSHIFT.APPLICATION_INFO.getTextFromAppInfoTable(5, 2));
   }

}
