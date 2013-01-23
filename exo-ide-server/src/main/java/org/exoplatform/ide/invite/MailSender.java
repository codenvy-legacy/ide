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
package org.exoplatform.ide.invite;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Provide service of email sending.
 */
@Deprecated
public abstract class MailSender
{

   private static final Log LOG = ExoLogger.getLogger(MailSender.class);

   /**
    * Send mail message with body formed from specified template. All
    * information needed for message sending specifies in headers.
    * <p/>
    * If you need to send more than one copy of email, then write needed
    * receivers separated by comma in "to" parameters. "
    * 
    * @param templateName
    *           - name to mail template
    * @param templateProperties
    *           - variables map to resolve template
    */
   public void sendMail(String from, String to, String replyTo, String subject, String mimeType, String templateName,
      Map<String, Object> templateProperties) throws SendingIdeMailException
   {
      try
      {
         // check both addresses
         MimeMessage message = new MimeMessage(getMailSession());
         message.setContent(getMessageBody(templateName, templateProperties), mimeType);
         message.setSubject(subject, "UTF-8");
         message.setFrom(new InternetAddress(from, true));
         message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
         //message.setHeader("Content-Type", "text/plain; charset=UTF-8");

         if (replyTo != null)
         {
            message.setReplyTo(InternetAddress.parse(replyTo));
         }
         doSendMail(message);

         LOG.debug("Mail send from {} to {} with subject {} in template {} with variables {} , content type  {}",
            new Object[]{from, to, subject, templateName, templateProperties, mimeType});

      }
      catch (AddressException e)
      {
         throw new SendingIdeMailException(400, "Email address of the recipient is not valid ", e);
      }
      catch (MessagingException e)
      {
         throw new SendingIdeMailException("Unable to send mail. Please contact support.", e);
      }
   }

   /**
    * Provide message body according to the template name and template
    * properties.
    * 
    * @param templateName
    *           - name of the message template
    * @param templateProperties
    *           - variables, what should be replaced in template
    * @return - content of the message.
    * @throws SendingIdeMailException
    */
   public abstract String getMessageBody(String templateName, Map<String, Object> templateProperties)
      throws SendingIdeMailException;

   /**
    * @return Mail session used to create Mail message.
    */
   public abstract Session getMailSession();

   /**
    * Send MimeMessage
    * 
    * @param message
    *           - message to send.
    * @throws SendingIdeMailException
    */
   public abstract void doSendMail(MimeMessage message) throws SendingIdeMailException;

}
