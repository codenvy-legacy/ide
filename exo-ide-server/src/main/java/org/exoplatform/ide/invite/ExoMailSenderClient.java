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

import org.codenvy.mail.MailSenderClient;
import org.exoplatform.container.configuration.ConfigurationException;

import java.io.IOException;
import java.util.Map;

import javax.mail.MessagingException;

public class ExoMailSenderClient
{
   private final TemplateResolver templateResolver;

   private final MailSenderClient mailSender;

   public ExoMailSenderClient(MailSenderClient mailSender, TemplateResolver templateResolver)
   {
      this.templateResolver = templateResolver;
      this.mailSender = mailSender;
   }

   public void sendMail(String from, String to, String replyTo, String subject, String mimeType, String templateName,
      Map<String, Object> templateProperties) throws SendingIdeMailException
   {
      try
      {
         mailSender.sendMail(from, to, replyTo, subject, mimeType,
            templateResolver.resolveTemplate(templateName, templateProperties));
      }
      catch (IOException e)
      {
         throw new SendingIdeMailException(e.getLocalizedMessage(), e);
      }
      catch (MessagingException e)
      {
         throw new SendingIdeMailException(e.getLocalizedMessage(), e);
      }
      catch (ConfigurationException e)
      {
         throw new SendingIdeMailException(e.getLocalizedMessage(), e);
      }
   }

}
