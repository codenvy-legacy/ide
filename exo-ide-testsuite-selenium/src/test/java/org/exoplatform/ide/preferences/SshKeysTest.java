package org.exoplatform.ide.preferences;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.junit.Test;

public class SshKeysTest extends BaseTest
{

   @Test
   public void sshAddTest() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.PREFERNCESS);
      IDE.PREFERENCES.waitPreferencesView();
      IDE.PREFERENCES.selectCustomizeMenu("Ssh Keys");
      IDE.SSH.waitSSHView();
      IDE.SSH.clickGenerateBtn();
      IDE.ASK_FOR_VALUE_DIALOG.waitOpened();
      IDE.ASK_FOR_VALUE_DIALOG.setValue("github.com");
      IDE.ASK_FOR_VALUE_DIALOG.clickOkButton();
      IDE.ASK_FOR_VALUE_DIALOG.waitClosed();
      Thread.sleep(5000);
   }
}
