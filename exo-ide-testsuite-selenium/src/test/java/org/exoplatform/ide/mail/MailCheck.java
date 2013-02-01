/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.mail;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import javax.mail.MessagingException;

import org.exoplatform.ide.BaseTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class checks links in email after registration, creating REPO_NAME
 */
public class MailCheck extends BaseTest
{
   private static final Logger LOG = LoggerFactory.getLogger(MailCheck.class);

   private static String LINK;

   private static String PASSWORD_RECOVER_LINK;

   private static String INVITE_LINK;

   private static String PROTOCOL = "https://"; //http:// or https://;

   /**
    * Waits for email to conform email 
    */
   public void waitForConfirmEmailInviteMailBox(final String user, final String password)
   {
      try
      {
         MailReceiver.receiveInviteEmail(user, password);
      }
      catch (TimeoutException e1)
      {
         e1.printStackTrace();
      }
      catch (MessagingException e1)
      {
         e1.printStackTrace();
      }

      new WebDriverWait(driver, 180).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver elem)
         {
            try
            {
               LOG.info("Find link collision ....." + "/create?REPO_NAMEName=" + REPO_NAME + "&user-mail=" + user);
               LINK =
                  MailReceiver.getLink("Your Cloud IDE Domain", "/create?REPO_NAMEName=" + REPO_NAME + "&user-mail="
                     + user);
               if (LINK != null)
               {
                  LINK = REPO_NAME + "." + IDE_HOST + LINK;
                  LOG.info(LINK);
                  return true;
               }
               LOG.error("Link Not found..." + "/create?REPO_NAMEName=" + REPO_NAME + "&user-mail=" + user);
               return false;

            }
            catch (MessagingException e)
            {
               return false;
            }
            catch (IOException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * Waits for email to conform email 
    */
   public void waitForConfirmEmail(final String user, final String password)
   {
      try
      {
         MailReceiver.receiveEmail(user, password);
      }
      catch (TimeoutException e1)
      {
         e1.printStackTrace();
      }
      catch (MessagingException e1)
      {
         e1.printStackTrace();
      }

      new WebDriverWait(driver, 300).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver elem)
         {
            try
            {
               LINK =
                  MailReceiver.getLink("Your Cloud IDE Domain", BASE_URL + "create?REPO_NAMEName=" + REPO_NAME
                     + "&user-mail=" + user);
               if (LINK != null)
               {
                  getProtocol();
                  LINK = PROTOCOL + LINK;
                  LOG.info("Found confirmation email with link to -> " + LINK);
                  return true;
               }
               return false;

            }
            catch (MessagingException e)
            {
               return false;
            }
            catch (IOException e)
            {
               return false;
            }
         }
      });
   }

   /**
    * The method set link to created REPO_NAME
    * 
    */
   //   public void setLink()
   //   {
   //      if (LINK == null)
   //      {
   //         REPO_NAME = REPO_NAME_LINK.substring(REPO_NAME_LINK.indexOf(":") + 3, REPO_NAME_LINK.indexOf("."));
   //         LINK = REPO_NAME_LINK;
   //         LOG.info(REPO_NAME + " at link..............>" + LINK + "<");
   //      }
   //   }

   /**
    * The method returns link to created tenant
    * 
    */
   public String getLink()
   {
      return LINK;
   }

   /**
    * The method returns invite link from email
    * 
    */
   public String getInviteLink()
   {
      return INVITE_LINK;
   }

   /**
    * The method goto page to conform invite
    * 
    */
   public void gotoConfirmInvitePage()
   {
      LOG.info("Invite link ............ " + INVITE_LINK);
      driver.get(PROTOCOL + INVITE_LINK);
   }

   /**
    * The method goto page to setup new password
    * 
    */
   public void gotoSetupNewPasswordPage()
   {
      driver.get(PROTOCOL + PASSWORD_RECOVER_LINK);
   }

   /**
    * Waits for email with credentials
    */
   public void waitForContactUsEmail(final String user, final String password)
   {
      try
      {
         MailReceiver.receiveEmail(user, password);
      }
      catch (TimeoutException e1)
      {
         e1.printStackTrace();
      }
      catch (MessagingException e1)
      {
         e1.printStackTrace();
      }

      new WebDriverWait(driver, 120).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver elem)
         {
            try
            {
               LOG.info("Searching link ........ " + REPO_NAME + "." + IDE_HOST + "/cloud/?username=" + user
                  + "&password=");

               if (MailReceiver.getLink("Selenium test", REPO_NAME + " Selenium Test Message") != null)
               {
                  LOG.info("Found ....... contact email.");
                  return true;
               }
               return false;

            }
            catch (MessagingException e)
            {
               return false;
            }
            catch (IOException e)
            {
               return false;
            }
         }
      });

   }

   /**
    * Waits for email with credentials
    */
   public void waitForCredentialEmail(final String user, final String password)
   {
      try
      {
         MailReceiver.receiveEmail(user, password);
      }
      catch (TimeoutException e1)
      {
         e1.printStackTrace();
      }
      catch (MessagingException e1)
      {
         e1.printStackTrace();
      }

      new WebDriverWait(driver, 300).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver elem)
         {
            try
            {
               LINK =
                  MailReceiver.getLink("Your Cloud IDE Domain", REPO_NAME + "." + IDE_HOST + "/cloud/?username=" + user
                     + "&password=");
               if (LINK != null)
               {
                  getProtocol();
                  LINK = PROTOCOL + LINK;
                  LOG.info("Found credential email with link to -> " + LINK);
                  return true;
               }
               return false;

            }
            catch (MessagingException e)
            {
               return false;
            }
            catch (IOException e)
            {
               return false;
            }
         }
      });

   }

   /**
    * Waits for email with credentials
    * @throws MessagingException 
    * @throws TimeoutException 
    */
   public void waitAndGetInviteLink(final String user, final String password)
   {

      new WebDriverWait(driver, 180).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver elem)
         {
            try
            {

               Thread.sleep(20000);
               MailReceiver.receiveInviteEmail(user, password);
               INVITE_LINK =
                  MailReceiver.getLink("You've been invited to use Codenvy", REPO_NAME + "." + IDE_HOST
                     + "/invites/accept");
               System.out.println(INVITE_LINK);

               if (INVITE_LINK != null)
               {
                  return true;
               }
               return false;

            }
            catch (MessagingException e)
            {
               return false;
            }
            catch (IOException e)
            {
               return false;
            }
            catch (TimeoutException e1)
            {
               return false;
            }
            catch (InterruptedException e)
            {
               return false;
            }
         }
      });

   }

   /**
    * Waits for email with password recover link
    */
   public void waitForPasswordRecoverEmail(final String user, final String password)
   {
      try
      {
         MailReceiver.receiveEmail(user, password);
      }
      catch (TimeoutException e1)
      {
         e1.printStackTrace();
      }
      catch (MessagingException e1)
      {
         e1.printStackTrace();
      }

      new WebDriverWait(driver, 300).until(new ExpectedCondition<Boolean>()
      {
         @Override
         public Boolean apply(WebDriver elem)
         {
            try
            {
               PASSWORD_RECOVER_LINK =
                  MailReceiver
                     .getLink("Cloud-IDE password recover", REPO_NAME + "." + IDE_HOST + "/setup-password.jsp");
               if (PASSWORD_RECOVER_LINK != null)
               {
                  LOG.info("Found password recover email with link to -> " + PASSWORD_RECOVER_LINK);
                  return true;
               }
               return false;

            }
            catch (MessagingException e)
            {
               return false;
            }
            catch (IOException e)
            {
               return false;
            }
         }
      });

   }

   public void setLinkNewPassword(String newpassword)
   {
      LINK = LINK.replaceFirst("password=.+", "password=" + newpassword);
   }

   public String getLinkPassword()
   {
      return LINK.substring(LINK.indexOf("&password") + 10);
   }

   public void cleanMailBox(String user, String password) throws TimeoutException, MessagingException
   {
      MailReceiver.cleanMailBox(user, password);
   }

   public void gotoInviteLink()
   {
      driver.get(PROTOCOL + REPO_NAME + "." + IDE_HOST + "/cloud/invite.jsp" + LINK.substring(LINK.indexOf("?")));
   }

   public void getProtocol()
   {
      PROTOCOL = driver.getCurrentUrl().split(":")[0] + "://";
      return;
   }
}
