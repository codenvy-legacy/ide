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

import org.exoplatform.container.configuration.ConfigurationException;
import org.exoplatform.services.mail.MailService;

import java.io.IOException;
import java.util.Map;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

/**
 * Mail sender that use   org.exoplatform.services.mail.MailService to send mails, and TemplateResolver to
 * parse templates.
 * <p/>
 * Decoupled from MailSender for future use of MailSender in cloud-admin environment with other implementation
 * of mail sending and template resolver classes.
 */
@Deprecated
public class ExoMailSender extends MailSender
{
   private final MailService mailService;

   private final TemplateResolver templateResolver;

   public ExoMailSender(MailService mailService, TemplateResolver templateResolver)
   {
      this.mailService = mailService;
      this.templateResolver = templateResolver;
   }

   @Override
   public String getMessageBody(String templateName, Map<String, Object> templateProperties)
      throws SendingIdeMailException
   {
      try
      {
         return templateResolver.resolveTemplate(templateName, templateProperties);
      }
      catch (ConfigurationException e)
      {
         throw new SendingIdeMailException(e.getLocalizedMessage(), e);
      }
      catch (IOException e)
      {
         throw new SendingIdeMailException(e.getLocalizedMessage(), e);
      }
   }

   @Override
   public Session getMailSession()
   {
      return mailService.getMailSession();
   }

   @Override
   public void doSendMail(MimeMessage message) throws SendingIdeMailException
   {
      try
      {
         mailService.sendMessage(message);
      }
      catch (Exception e)
      {
         throw new SendingIdeMailException(e.getLocalizedMessage(), e);
      }
   }
}
