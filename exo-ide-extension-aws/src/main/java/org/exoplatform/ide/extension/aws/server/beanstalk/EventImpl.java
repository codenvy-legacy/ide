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
package org.exoplatform.ide.extension.aws.server.beanstalk;

import org.exoplatform.ide.extension.aws.shared.beanstalk.Event;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EventsSeverity;

import java.util.Date;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class EventImpl implements Event
{
   private long eventDate;
   private String message;
   private String applicationName;
   private String versionLabel;
   private String templateName;
   private String environmentName;
   private EventsSeverity severity;

   public static class Builder
   {
      private long eventDate;
      private String message;
      private String applicationName;
      private String versionLabel;
      private String templateName;
      private String environmentName;
      private EventsSeverity severity;

      public Builder eventDate(Date eventDate)
      {
         if (eventDate == null)
         {
            this.eventDate = -1;
            return this;
         }
         this.eventDate = eventDate.getTime();
         return this;
      }

      public Builder message(String message)
      {
         this.message = message;
         return this;
      }

      public Builder applicationName(String applicationName)
      {
         this.applicationName = applicationName;
         return this;
      }

      public Builder versionLabel(String versionLabel)
      {
         this.versionLabel = versionLabel;
         return this;
      }

      public Builder templateName(String templateName)
      {
         this.templateName = templateName;
         return this;
      }

      public Builder environmentName(String environmentName)
      {
         this.environmentName = environmentName;
         return this;
      }

      public Builder severity(String severity)
      {
         this.severity = EventsSeverity.fromValue(severity);
         return this;
      }

      public Event build()
      {
         return new EventImpl(this);
      }
   }

   private EventImpl(Builder builder)
   {
      this.eventDate = builder.eventDate;
      this.message = builder.message;
      this.applicationName = builder.applicationName;
      this.versionLabel = builder.versionLabel;
      this.templateName = builder.templateName;
      this.environmentName = builder.environmentName;
      this.severity = builder.severity;
   }

   public EventImpl()
   {
   }

   @Override
   public long getEventDate()
   {
      return eventDate;
   }

   @Override
   public void setEventDate(long eventDate)
   {
      this.eventDate = eventDate;
   }

   @Override
   public String getMessage()
   {
      return message;
   }

   @Override
   public void setMessage(String message)
   {
      this.message = message;
   }

   @Override
   public String getApplicationName()
   {
      return applicationName;
   }

   @Override
   public void setApplicationName(String applicationName)
   {
      this.applicationName = applicationName;
   }

   @Override
   public String getVersionLabel()
   {
      return versionLabel;
   }

   @Override
   public void setVersionLabel(String versionLabel)
   {
      this.versionLabel = versionLabel;
   }

   @Override
   public String getTemplateName()
   {
      return templateName;
   }

   @Override
   public void setTemplateName(String templateName)
   {
      this.templateName = templateName;
   }

   @Override
   public String getEnvironmentName()
   {
      return environmentName;
   }

   @Override
   public void setEnvironmentName(String environmentName)
   {
      this.environmentName = environmentName;
   }

   @Override
   public EventsSeverity getSeverity()
   {
      return severity;
   }

   @Override
   public void setSeverity(EventsSeverity severity)
   {
      this.severity = severity;
   }

   @Override
   public String toString()
   {
      return "EventImpl{" +
         "eventDate=" + eventDate +
         ", message='" + message + '\'' +
         ", applicationName='" + applicationName + '\'' +
         ", versionLabel='" + versionLabel + '\'' +
         ", templateName='" + templateName + '\'' +
         ", environmentName='" + environmentName + '\'' +
         ", severity=" + severity +
         '}';
   }
}
