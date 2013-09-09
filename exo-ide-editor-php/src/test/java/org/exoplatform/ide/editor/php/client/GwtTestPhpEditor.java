/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.editor.php.client;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * GWT JUnit <b>integration</b> tests must extend GWTTestCase. Using <code>"GwtTest*"</code> naming pattern exclude them from
 * running with surefire during the test phase.
 */
public class GwtTestPhpEditor extends GWTTestCase {

    String content = "<? echo \"Hello\" ?>";

    /** Must refer to a valid module that sources this class. */
    public String getModuleName() {
        return "org.exoplatform.ide.editor.php.client.PhpEditorJUnit";
    }

    public void testdefaultContent() {
        String content = PhpEditorExtension.DEFAULT_CONTENT.getSource().getText();
        assertNotNull(content);
        assertEquals(this.content, content);
    }

}
