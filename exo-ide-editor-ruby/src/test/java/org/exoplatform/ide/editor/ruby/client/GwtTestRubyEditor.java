package org.exoplatform.ide.editor.ruby.client;

import org.exoplatform.ide.editor.ruby.client.RubyEditorExtension;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * GWT JUnit <b>integration</b> tests must extend GWTTestCase. Using <code>"GwtTest*"</code> naming pattern exclude them from
 * running with surefire during the test phase.
 */
public class GwtTestRubyEditor extends GWTTestCase
{

   String content = "# Ruby Sample program\n\n" + "class HelloClass\n" + "  def sayHello\n"
      + "    puts( \"Hello, wolrd!\" )\n" + "  end\n" + "end\n\n" + "ob = HelloClass.new\n" + "ob.sayHello";

   /**
    * Must refer to a valid module that sources this class.
    */
   public String getModuleName()
   {
      return "org.exoplatform.ide.editor.ruby.client.RubyEditorJUnit";
   }

   public void testdefaultContent()
   {
      String content = RubyEditorExtension.DEFAULT_CONTENT.getSource().getText();
      assertNotNull(content);
      assertEquals(this.content, content);
   }

}
