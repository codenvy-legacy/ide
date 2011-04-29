package org.exoplatform.ide.editor.extension.php.client;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * GWT JUnit <b>integration</b> tests must extend GWTTestCase.
 * Using <code>"GwtTest*"</code> naming pattern exclude them from running with
 * surefire during the test phase.
 */
public class GwtTestPhpEditor extends GWTTestCase {
   
   String content = "";

  /**
   * Must refer to a valid module that sources this class.
   */
  public String getModuleName() {
    return "org.exoplatform.ide.editor.extension.php.client.PhpEditorJUnit";
  }
  
  public void testdefaultContent()
  {
     String content = PhpEditorExtension.DEFAULT_CONTENT.getSource().getText();
     assertNotNull(content);
     assertEquals(this.content, content);
  }

  


}
