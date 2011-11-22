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
package org.exoplatform.ide.operation.browse.highlight;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 15, 2010 $
 * 
 */
public class HighlightCKEditorTest extends BaseTest {
	private final static String URL = BASE_URL + REST_CONTEXT + "/"
			+ WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";

	private static String FOLDER_NAME = HighlightCKEditorTest.class
			.getSimpleName();

	private static String FILE_NAME = HighlightCKEditorTest.class
			.getSimpleName()
			+ "File";

	@Before
	public void setUp() {
		try {
			VirtualFileSystemUtils.mkcol(URL + FOLDER_NAME);
			VirtualFileSystemUtils
					.put(
							"src/test/resources/org/exoplatform/ide/operation/edit/outline/HtmlCodeOutline.html",
							MimeType.TEXT_HTML, URL + FOLDER_NAME + "/"
									+ FILE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// This test will fail until IDE-424
	@Test
	public void testHighlightCKEdditor() throws Exception {
		IDE.WORKSPACE.waitForRootItem();

		assertTrue(IDE.PROJECT.EXPLORER.isActive());

		IDE.WORKSPACE.waitForItem(WS_URL + FOLDER_NAME + "/");
		IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME + "/");

		IDE.WORKSPACE.waitForItem(URL + FOLDER_NAME + "/" + FILE_NAME);
		IDE.WORKSPACE.selectItem(URL + FOLDER_NAME + "/" + FILE_NAME);
		IDE.WORKSPACE.doubleClickOnFile(URL + FOLDER_NAME + "/" + FILE_NAME);
      IDE.EDITOR.clickDesignButton();
		waitForElementPresent("//div[@panel-id='editor']");

		IDE.MENU
				.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_PREVIEW);
		Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);
		assertTrue(IDE.PROJECT.EXPLORER.isActive());
		assertFalse(IDE.EDITOR.isActive(0));
		
		IDE.EDITOR.clickOnEditor(0);

		// TODO should be compled should be completed after fix problem|
		// highlighting in codeeditor after setting cursor in text
		// IDE.PERSPECTIVE.checkViewIsActive("editor-0");
		// IDE.PERSPECTIVE.checkViewIsNotActive("idePreviewHTMLView");
		// ------------------------------------------------------------

		// TODO should be compled should be completed after fix problem issue IDE-804
		//IDE.EDITOR.closeFile(0);
	}

	@After
	public void tearDown() {
		deleteCookies();
		try {
			VirtualFileSystemUtils.delete(URL + FOLDER_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
