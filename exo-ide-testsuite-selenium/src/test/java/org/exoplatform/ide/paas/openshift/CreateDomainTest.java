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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CreateDomainTest extends BaseTest {

	/**
	 * Test added to Ignore, because at the moment he is in progress of develop.
	 * 
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testCreateDomain() throws Exception {
		/*
		 * Wait while IDE has been successfully initialized.
		 */
		IDE.WORKSPACE.waitForRootItem();
		Thread.sleep(1000);

		/*
		 * Clear Output
		 */
		if (IDE.OUTPUT.isOpened())
      {
         IDE.OUTPUT.clickClearButton();
      }
		
		/*
		 * Open PaaS > OpenShift > Create Domain...
		 */
		IDE.OPENSHIFT.CREATE_DOMAIN.openCreateDomainWindow();

		/*
		 * Create button must be disabled
		 */
		assertFalse(IDE.OPENSHIFT.CREATE_DOMAIN.isCreateButtonEnabled());

		/*
		 * Type new domain name
		 */
		IDE.OPENSHIFT.CREATE_DOMAIN.typeDomainName("mydomain");

		/*
		 * Create button must be enabled
		 */
		assertTrue(IDE.OPENSHIFT.CREATE_DOMAIN.isCreateButtonEnabled());

		/*
		 * Click Create button
		 */
		IDE.OPENSHIFT.CREATE_DOMAIN.clickCreateButton();
		IDE.OPENSHIFT.CREATE_DOMAIN.waitForCreateDomainWindowNotPresent();

		/*
		 * New domain must be created and result must be displayed in Output.
		 */
		String expectedDomainCreatedOutputMessage = "[INFO] Domain mydomain is successfully created.";
		assertEquals(expectedDomainCreatedOutputMessage, IDE.OUTPUT
				.getOutputMessage(1));
	}

}
