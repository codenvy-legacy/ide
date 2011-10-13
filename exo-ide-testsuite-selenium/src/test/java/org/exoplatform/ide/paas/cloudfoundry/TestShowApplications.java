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

package org.exoplatform.ide.paas.cloudfoundry;

import junit.framework.Assert;

import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.paas.cloudfoundry.core.CloudFoundry;
import org.junit.Test;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TestShowApplications extends CloudFoundryTest
{
   
   @Test
   public void testShowApplications() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      
      CloudFoundry.APPLICATIONS.openFromMenu();
      CloudFoundry.APPLICATIONS.waitForOpened();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      int appCount = CloudFoundry.APPLICATIONS.getApplicationsCount();
      Assert.assertEquals(2, appCount);
      
      Assert.assertEquals("test-app1", CloudFoundry.APPLICATIONS.getApplicationName(1));
      Assert.assertEquals("test-app2", CloudFoundry.APPLICATIONS.getApplicationName(2));
      
      Assert.assertEquals("1", CloudFoundry.APPLICATIONS.getAppplicationInstances(1));
      Assert.assertEquals("1", CloudFoundry.APPLICATIONS.getAppplicationInstances(2));
      
      Assert.assertEquals("STOPPED", CloudFoundry.APPLICATIONS.getApplicationState(1));
      Assert.assertEquals("STOPPED", CloudFoundry.APPLICATIONS.getApplicationState(2));
      
      Assert.assertEquals("test-app1.cloudfoundry.com", CloudFoundry.APPLICATIONS.getApplicationURL(1));
      Assert.assertEquals("test-app2.cloudfoundry.com", CloudFoundry.APPLICATIONS.getApplicationURL(2));
      
      CloudFoundry.APPLICATIONS.clickCloseButton();
   }

}
