package org.exoplatform.ide.operation.paas;

import static org.junit.Assert.*;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.operation.autocompletion.jsp.JspTagsTest;
import org.junit.Test;

import java.util.ResourceBundle;

public class CloudBeesZipWithWizard extends BaseTest
{
   private static final String PROJECT = JspTagsTest.class.getSimpleName();

   private static final String FOLDER_NAME = JspTagsTest.class.getSimpleName();

   private final ResourceBundle CLOUD_BEES_CREDENTIONALS = ResourceBundle.getBundle("conf/ide-selenium");

   private final String CLOUD_BEES_MAIL = CLOUD_BEES_CREDENTIONALS.getString("ide.cloudbees.mail");

   private final String CLOUD_BEES_PASS = CLOUD_BEES_CREDENTIONALS.getString("ide.cloudbees.password");

   @Test
   public void deployToCloudBess() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.MENU.runCommand(MenuCommands.Project.PROJECT, MenuCommands.Project.NEW, MenuCommands.Project.CREATE_PROJECT);
      IDE.PROJECTMENU.waitOpened();
      IDE.PROJECTMENU.waitForProjectAppear("Java Web Project");
      IDE.PROJECTMENU.selectPoject("Java Web Project");
      IDE.PROJECTMENU.nextBtnClick();
      IDE.DEPLOY.waitOpened();
      IDE.DEPLOY.waitPaasListOpened("CloudBees");
      IDE.DEPLOY.selectAndClickOnPaasInList("CloudBees");
            if (IDE.PAASAUTORIZATION.checkAppearAutorizationForm(6))
            {
               IDE.PAASAUTORIZATION.waitOpened();
               IDE.PAASAUTORIZATION.typeEmailField(CLOUD_BEES_MAIL);
               IDE.PAASAUTORIZATION.typePasswordField(CLOUD_BEES_PASS);
               IDE.PAASAUTORIZATION.waitLoginBtnIsActive();
               IDE.PAASAUTORIZATION.clickLoginBtn();
               IDE.OUTPUT.waitForMessageShow(1);
               assertEquals("[INFO] Logged in CloudBees successfully.", IDE.OUTPUT.getOutputMessage(1));
            }
              IDE.DEPLOY.waitOpened();
              System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<:"+IDE.DEPLOY.paasNameIsSelected("CloudBees"));
         
      Thread.sleep(3000);

   }

}
