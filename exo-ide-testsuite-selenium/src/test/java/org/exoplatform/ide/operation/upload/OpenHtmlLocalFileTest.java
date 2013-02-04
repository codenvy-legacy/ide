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
package org.exoplatform.ide.operation.upload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 * 
 */
public class OpenHtmlLocalFileTest extends BaseTest {
	private static String HTML_NAME = "Example.html";

	private static String PROJECT = OpenHtmlLocalFileTest.class.getSimpleName();

	private static final String FILE_PATH = "src/test/resources/org/exoplatform/ide/operation/file/upload/Example.html";

	@BeforeClass
	public static void setUp() {
		try {
			VirtualFileSystemUtils.createDefaultProject(PROJECT);
		} catch (IOException e) {
		}
	}

	@Test
	public void testOpenHtml() throws Exception {
		IDE.PROJECT.EXPLORER.waitOpened();
		IDE.LOADER.waitClosed();
		IDE.PROJECT.OPEN.openProject(PROJECT);
		IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
		IDE.LOADER.waitClosed();

		IDE.UPLOAD.open(MenuCommands.File.OPEN_LOCAL_FILE, FILE_PATH,
				MimeType.TEXT_HTML);
		IDE.EDITOR.waitActiveFile();

		String text = IDE.EDITOR.getTextFromCodeEditor();

		assertTrue(text.length() > 0);

		String fileContent = getFileContent(FILE_PATH);
		assertEquals(fileContent.split("\n").length, text.split("\n").length);

		IDE.EDITOR.saveAs(1, HTML_NAME);
		IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + HTML_NAME);

		IDE.PROPERTIES.openProperties();

		assertEquals(MimeType.TEXT_HTML, IDE.PROPERTIES.getContentType());
	}

	@AfterClass
	public static void tearDown() {
		try {
			VirtualFileSystemUtils.delete(WS_URL + PROJECT);
		} catch (IOException e) {
		}
	}

}
