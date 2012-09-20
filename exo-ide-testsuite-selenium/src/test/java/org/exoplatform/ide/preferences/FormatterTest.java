package org.exoplatform.ide.preferences;

import static org.junit.Assert.assertEquals;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.junit.Test;

public class FormatterTest extends BaseTest
{
   private final String VISIBLE_EXO_TEXT = "/**" + "\n" + " * A sample source file for the code formatter preview\n"
      + " */" + "\n" + "package mypackage;\n" + "import java.util.LinkedList;\n" + "public class MyIntStack\n" + "{\n"
      + "   private final LinkedList fStack;\n" + "   public MyIntStack()\n" + "   {\n"
      + "      fStack = new LinkedList();\n" + "   }\n" + "   public int pop()\n" + "   {";

   private final String VISIBLE_ECLIPSE_TEXT = "/**\n" + " * A sample source file for the code formatter preview\n"
      + " */\n" + "package mypackage;\n" + "import java.util.LinkedList;\n" + "public class MyIntStack {\n"
      + "    private final LinkedList fStack;\n" + "    public MyIntStack() {\n"
      + "        fStack = new LinkedList();\n" + "    }\n" + "    public int pop() {\n"
      + "        return ((Integer) fStack.removeFirst()).intValue();\n" + "    }";

   @Test
   public void eXoFormatterTest() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.PREFERNCESS);
      IDE.PREFERENCES.waitPreferencesOpen();
      IDE.PREFERENCES.selectCustomizeMenu(MenuCommands.Preferences.FORMATTER);
      IDE.FORMATTER.waitOpened();
      IDE.FORMATTER.selectExoFormatter();
      IDE.FORMATTER.waitRedrawSampleText();
      //need for full reparce of the format text in editor
      assertEquals(IDE.FORMATTER.getFormatterText(), VISIBLE_EXO_TEXT);
      IDE.FORMATTER.selectEclipseFormatter();
      IDE.FORMATTER.waitRedrawSampleText();
      //need for full reparce of the format text in editor
      assertEquals(IDE.FORMATTER.getFormatterText(), VISIBLE_ECLIPSE_TEXT);

   }
}
