package org.exoplatform.ide.preferences;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.junit.Test;

public class SshKeysTest extends BaseTest
{
   final String HOST = "github.com";

   @Test
   public void sshAddTest() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.MENU.runCommand(MenuCommands.Window.WINDOW, MenuCommands.Window.PREFERNCESS);
      IDE.PREFERENCES.waitPreferencesOpen();
      IDE.PREFERENCES.selectCustomizeMenu(MenuCommands.Preferences.SSH_KEY);
      IDE.SSH.waitSshView();
      IDE.SSH.clickGenerateBtn();
      IDE.SSH.waitSshAskForm();
      IDE.SSH.typeHostToSshAsk(HOST);
      IDE.SSH.cliclOkBtnSshAsk();
      IDE.SSH.waitSshAskFormClose();
      IDE.SSH.waitAppearContentInSshListGrig();
      assertThat(IDE.SSH.getAllKeysList().split("\n")).contains("github.com", "View", "Delete");
   }

   @Test
   public void sshPreviewKeyTest() throws Exception
   {
      IDE.SSH.clickViewKeyInGridPosition(1);
      IDE.SSH.waitAppearSshKeyManadger();
      //need for redraw in google chrom
      Thread.sleep(500);
      assertTrue(IDE.SSH.getSshKeyHash().startsWith("ssh-rsa"));
      assertTrue(IDE.SSH.getSshKeyHash().length() > 378);
      IDE.SSH.clickOnCloseSsshKeyManager();
      IDE.SSH.waitCloseSshKeyManadger();
   }

   @Test
   public void chekVisibleKeysAfterSwitch() throws Exception
   {
      IDE.PREFERENCES.selectCustomizeMenu("Customize Toolbar");
      IDE.CUSTOMIZE_TOOLBAR.waitOpened();
      IDE.PREFERENCES.selectCustomizeMenu(MenuCommands.Preferences.SSH_KEY);
      IDE.SSH.waitSshView();
      IDE.SSH.waitAppearContentInSshListGrig();
      assertThat(IDE.SSH.getAllKeysList().split("\n")).contains("github.com", "View", "Delete");
   }

   @Test
   public void deleteCreatedKey() throws Exception
   {
      IDE.SSH.clickDeleteKeyInGridPosition(1);
      IDE.ASK_DIALOG.waitOpened();
      IDE.ASK_DIALOG.clickYes();
      IDE.ASK_DIALOG.waitClosed();
      IDE.SSH.waitDisAppearContentInSshListGrig();
   }

}
