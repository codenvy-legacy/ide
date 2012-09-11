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
      Thread.sleep(5000);
   }
}
