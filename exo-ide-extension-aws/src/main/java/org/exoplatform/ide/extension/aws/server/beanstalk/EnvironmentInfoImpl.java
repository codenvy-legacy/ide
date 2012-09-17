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

import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentHealth;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentStatus;

import java.util.Date;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class EnvironmentInfoImpl implements EnvironmentInfo
{
   private String name;
   private String id;
   private String applicationName;
   private String versionLabel;
   private String solutionStackName;
   private String templateName;
   private String description;
   private String endpointUrl;
   private String cNAME;
   private long created;
   private long updated;
   private EnvironmentStatus status;
   private EnvironmentHealth health;

   public static class Builder
   {
      private String name;
      private String id;
      private String applicationName;
      private String versionLabel;
      private String solutionStackName;
      private String templateName;
      private String description;
      private String endpointUrl;
      private String cNAME;
      private long created;
      private long updated;
      private EnvironmentStatus status;
      private EnvironmentHealth health;

      public Builder name(String name)
      {
         this.name = name;
         return this;
      }

      public Builder id(String id)
      {
         this.id = id;
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

      public Builder solutionStackName(String solutionStackName)
      {
         this.solutionStackName = solutionStackName;
         return this;
      }

      public Builder templateName(String templateName)
      {
         this.templateName = templateName;
         return this;
      }

      public Builder description(String description)
      {
         this.description = description;
         return this;
      }

      public Builder endpointUrl(String endpointUrl)
      {
         this.endpointUrl = endpointUrl;
         return this;
      }

      public Builder cNAME(String cNAME)
      {
         this.cNAME = cNAME;
         return this;
      }

      public Builder created(Date created)
      {
         if (created != null)
         {
            this.created = created.getTime();
         }
         return this;
      }

      public Builder updated(Date updated)
      {
         if (updated != null)
         {
            this.updated = updated.getTime();
         }
         return this;
      }

      public Builder status(String status)
      {
         this.status = EnvironmentStatus.fromValue(status);
         return this;
      }

      public Builder health(String health)
      {
         this.health = EnvironmentHealth.fromValue(health);
         return this;
      }

      public EnvironmentInfo build()
      {
         return new EnvironmentInfoImpl(this);
      }
   }

   private EnvironmentInfoImpl(Builder builder)
   {
      this.name = builder.name;
      this.id = builder.id;
      this.applicationName = builder.applicationName;
      this.versionLabel = builder.versionLabel;
      this.solutionStackName = builder.solutionStackName;
      this.templateName = builder.templateName;
      this.description = builder.description;
      this.endpointUrl = builder.endpointUrl;
      this.cNAME = builder.cNAME;
      this.created = builder.created;
      this.updated = builder.updated;
      this.status = builder.status;
      this.health = builder.health;
   }

   public EnvironmentInfoImpl()
   {
   }

   @Override
   public String getName()
   {
      return name;
   }

   @Override
   public void setName(String name)
   {
      this.name = name;
   }

   @Override
   public String getId()
   {
      return id;
   }

   @Override
   public void setId(String id)
   {
      this.id = id;
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
   public String getSolutionStackName()
   {
      return solutionStackName;
   }

   @Override
   public void setSolutionStackName(String solutionStackName)
   {
      this.solutionStackName = solutionStackName;
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
   public String getDescription()
   {
      return description;
   }

   @Override
   public void setDescription(String description)
   {
      this.description = description;
   }

   @Override
   public String getEndpointUrl()
   {
      return endpointUrl;
   }

   @Override
   public void setEndpointUrl(String endpointUrl)
   {
      this.endpointUrl = endpointUrl;
   }

   @Override
   public String getCNAME()
   {
      return cNAME;
   }

   @Override
   public void setCNAME(String cNAME)
   {
      this.cNAME = cNAME;
   }

   @Override
   public long getCreated()
   {
      return created;
   }

   @Override
   public void setCreated(long created)
   {
      this.created = created;
   }

   @Override
   public long getUpdated()
   {
      return updated;
   }

   @Override
   public void setUpdated(long updated)
   {
      this.updated = updated;
   }

   @Override
   public EnvironmentStatus getStatus()
   {
      return status;
   }

   @Override
   public void setStatus(EnvironmentStatus status)
   {
      this.status = status;
   }

   @Override
   public EnvironmentHealth getHealth()
   {
      return health;
   }

   @Override
   public void setHealth(EnvironmentHealth health)
   {
      this.health = health;
   }

   @Override
   public String toString()
   {
      return "EnvironmentInfoImpl{" +
         "name='" + name + '\'' +
         ", id='" + id + '\'' +
         ", applicationName='" + applicationName + '\'' +
         ", versionLabel='" + versionLabel + '\'' +
         ", solutionStackName='" + solutionStackName + '\'' +
         ", templateName='" + templateName + '\'' +
         ", description='" + description + '\'' +
         ", endpointUrl='" + endpointUrl + '\'' +
         ", cNAME='" + cNAME + '\'' +
         ", created=" + created +
         ", updated=" + updated +
         ", status=" + status +
         ", health=" + health +
         '}';
   }
}
