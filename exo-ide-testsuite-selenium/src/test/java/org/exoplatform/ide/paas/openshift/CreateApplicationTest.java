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

public class CreateApplicationTest extends BaseTest {

	private static final String TEST_FOLDER = CreateApplicationTest.class
			.getSimpleName();

	@Before
	public void setUp() throws Exception {
		try {
			VirtualFileSystemUtils.mkcol(WS_URL + TEST_FOLDER);
		} catch (Exception e) {
		}
	}

	@After
	public void tearDown() {
		try {
			VirtualFileSystemUtils.delete(WS_URL + TEST_FOLDER);
		} catch (Exception e) {
		}
	}

	/*
	 * - create project folder - OpenShift > create new domain - OpenShift >
	 * create application - creation result must be displayed in Output ( check
	 * name, type, public url )
	 */
	/**
	 * Test added to Ignore, because at the moment he is in progress of develop.
	 * 
	 * @throws Exception
	 */
	@Ignore
	@Test
	public void testCreateApplication() throws Exception {
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
		 * Open Create application form
		 */
		IDE.OPENSHIFT.CREATE_APPLICATION.openCreateApplicationWindow();

		/*
		 * Create button must be disabled
		 */
		assertFalse(IDE.OPENSHIFT.CREATE_APPLICATION.isCreateButtonEnabled());

		/*
		 * Type "app1" to Application Name field
		 */
		IDE.OPENSHIFT.CREATE_APPLICATION.typeApplicationName("app1");

		/*
		 * Create button must be enabled
		 */
		assertTrue(IDE.OPENSHIFT.CREATE_APPLICATION.isCreateButtonEnabled());

		/*
		 * Click Create button
		 */
		IDE.OPENSHIFT.CREATE_APPLICATION.clickCreateButton();
		IDE.OPENSHIFT.CREATE_APPLICATION
				.waitForCreateApplicationWindowNotPresent();

		/*
		 * New Application must be created and result must be displayed in
		 * Output.
		 */
		String expectedAppCreatedOutputMessage = "[INFO] Domain domain1 is successfully created.";
		assertEquals(expectedAppCreatedOutputMessage, IDE.OUTPUT
				.getOutputMessage(1));
	}

}
