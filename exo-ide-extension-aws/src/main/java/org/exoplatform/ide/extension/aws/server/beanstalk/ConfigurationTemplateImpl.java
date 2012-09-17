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

import org.exoplatform.ide.extension.aws.shared.beanstalk.ConfigurationOption;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ConfigurationTemplateInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ConfigurationTemplateDeploymentStatus;

import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ConfigurationTemplateImpl implements ConfigurationTemplateInfo
{
   private String solutionStackName;
   private String applicationName;
   private String templateName;
   private String description;
   private String environmentName;
   private ConfigurationTemplateDeploymentStatus deploymentStatus;
   private long created;
   private long updated;
   private List<ConfigurationOption> options;

   public static class Builder
   {
      private String solutionStackName;
      private String applicationName;
      private String templateName;
      private String description;
      private String environmentName;
      private ConfigurationTemplateDeploymentStatus deploymentStatus;
      private long created;
      private long updated;
      private List<ConfigurationOption> options;

      public Builder solutionStackName(String solutionStackName)
      {
         this.solutionStackName = solutionStackName;
         return this;
      }

      public Builder applicationName(String applicationName)
      {
         this.applicationName = applicationName;
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

      public Builder environmentName(String environmentName)
      {
         this.environmentName = environmentName;
         return this;
      }

      public Builder deploymentStatus(String deploymentStatus)
      {
         this.deploymentStatus = ConfigurationTemplateDeploymentStatus.fromValue(deploymentStatus);
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

      public Builder options(List<ConfigurationOption> options)
      {
         this.options = options;
         return this;
      }

      public ConfigurationTemplateInfo build()
      {
         return new ConfigurationTemplateImpl(this);
      }
   }


   private ConfigurationTemplateImpl(Builder builder)
   {
      this.solutionStackName = builder.solutionStackName;
      this.applicationName = builder.applicationName;
      this.templateName = builder.templateName;
      this.description = builder.description;
      this.environmentName = builder.environmentName;
      this.deploymentStatus = builder.deploymentStatus;
      this.created = builder.created;
      this.updated = builder.updated;
      this.options = builder.options;
   }

   public ConfigurationTemplateImpl()
   {
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
   public ConfigurationTemplateDeploymentStatus getDeploymentStatus()
   {
      return deploymentStatus;
   }

   @Override
   public void setDeploymentStatus(ConfigurationTemplateDeploymentStatus deploymentStatus)
   {
      this.deploymentStatus = deploymentStatus;
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
   public List<ConfigurationOption> getOptions()
   {
      return options;
   }

   @Override
   public void setOptions(List<ConfigurationOption> options)
   {
      this.options = options;
   }

   @Override
   public String toString()
   {
      return "ConfigurationTemplateImpl{" +
         "solutionStackName='" + solutionStackName + '\'' +
         ", applicationName='" + applicationName + '\'' +
         ", templateName='" + templateName + '\'' +
         ", description='" + description + '\'' +
         ", environmentName='" + environmentName + '\'' +
         ", deploymentStatus=" + deploymentStatus +
         ", created=" + created +
         ", updated=" + updated +
         ", options=" + options +
         '}';
   }
}
