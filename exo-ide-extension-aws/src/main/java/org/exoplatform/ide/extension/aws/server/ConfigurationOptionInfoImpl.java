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
package org.exoplatform.ide.extension.aws.server;

import org.exoplatform.ide.extension.aws.shared.ConfigurationOptionChangeSeverity;
import org.exoplatform.ide.extension.aws.shared.ConfigurationOptionInfo;
import org.exoplatform.ide.extension.aws.shared.ConfigurationOptionRestriction;
import org.exoplatform.ide.extension.aws.shared.ConfigurationOptionType;

import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ConfigurationOptionInfoImpl implements ConfigurationOptionInfo
{
   private String name;
   private String namespace;
   private String defaultValue;
   private ConfigurationOptionChangeSeverity changeSeverity;
   private boolean userDefined;
   private ConfigurationOptionType valueType;
   private List<String> valueOptions;
   private Integer minValue;
   private Integer maxValue;
   private Integer maxLength;
   private ConfigurationOptionRestriction optionRestriction;

   public static class Builder
   {
      private String name;
      private String namespace;
      private String defaultValue;
      private ConfigurationOptionChangeSeverity changeSeverity;
      private boolean userDefined;
      private ConfigurationOptionType valueType;
      private List<String> valueOptions;
      private Integer minValue;
      private Integer maxValue;
      private Integer maxLength;
      private ConfigurationOptionRestriction optionRestriction;

      public Builder namespace(String namespace)
      {
         this.namespace = namespace;
         return this;
      }

      public Builder name(String name)
      {
         this.name = name;
         return this;
      }

      public Builder defaultValue(String defaultValue)
      {
         this.defaultValue = defaultValue;
         return this;
      }

      public Builder changeSeverity(String changeSeverity)
      {
         this.changeSeverity = ConfigurationOptionChangeSeverity.fromValue(changeSeverity);
         return this;
      }

      public Builder userDefined(boolean userDefined)
      {
         this.userDefined = userDefined;
         return this;
      }

      public Builder valueType(String valueType)
      {
         this.valueType = ConfigurationOptionType.fromValue(valueType);
         return this;
      }

      public Builder valueOptions(List<String> valueOptions)
      {
         this.valueOptions = valueOptions;
         return this;
      }

      public Builder minValue(Integer minValue)
      {
         this.minValue = minValue;
         return this;
      }

      public Builder maxValue(Integer maxValue)
      {
         this.maxValue = maxValue;
         return this;
      }

      public Builder maxLength(Integer maxLength)
      {
         this.maxLength = maxLength;
         return this;
      }

      public Builder optionRestriction(String label, String pattern)
      {
         this.optionRestriction = new ConfigurationOptionRestrictionImpl(label, pattern);
         return this;
      }

      public ConfigurationOptionInfo build()
      {
         return new ConfigurationOptionInfoImpl(this);
      }
   }

   private ConfigurationOptionInfoImpl(Builder builder)
   {
      this.namespace = builder.namespace;
      this.name = builder.name;
      this.defaultValue = builder.defaultValue;
      this.changeSeverity = builder.changeSeverity;
      this.userDefined = builder.userDefined;
      this.valueType = builder.valueType;
      this.valueOptions = builder.valueOptions;
      this.minValue = builder.minValue;
      this.maxValue = builder.maxValue;
      this.maxLength = builder.maxLength;
      this.optionRestriction = builder.optionRestriction;
   }

   public ConfigurationOptionInfoImpl()
   {
   }

   @Override
   public String getNamespace()
   {
      return namespace;
   }

   @Override
   public void setNamespace(String namespace)
   {
      this.namespace = namespace;
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
   public String getDefaultValue()
   {
      return defaultValue;
   }

   @Override
   public void setDefaultValue(String defaultValue)
   {
      this.defaultValue = defaultValue;
   }

   @Override
   public ConfigurationOptionChangeSeverity getChangeSeverity()
   {
      return changeSeverity;
   }

   @Override
   public void setChangeSeverity(ConfigurationOptionChangeSeverity changeSeverity)
   {
      this.changeSeverity = changeSeverity;
   }

   @Override
   public boolean isUserDefined()
   {
      return userDefined;
   }

   @Override
   public void setUserDefined(boolean userDefined)
   {
      this.userDefined = userDefined;
   }

   @Override
   public ConfigurationOptionType getValueType()
   {
      return valueType;
   }

   @Override
   public void setValueType(ConfigurationOptionType valueType)
   {
      this.valueType = valueType;
   }

   @Override
   public List<String> getValueOptions()
   {
      return valueOptions;
   }

   @Override
   public void setValueOptions(List<String> valueOptions)
   {
      this.valueOptions = valueOptions;
   }

   @Override
   public Integer getMinValue()
   {
      return minValue;
   }

   @Override
   public void setMinValue(Integer minValue)
   {
      this.minValue = minValue;
   }

   @Override
   public Integer getMaxValue()
   {
      return maxValue;
   }

   @Override
   public void setMaxValue(Integer maxValue)
   {
      this.maxValue = maxValue;
   }

   @Override
   public Integer getMaxLength()
   {
      return maxLength;
   }

   @Override
   public void setMaxLength(Integer maxLength)
   {
      this.maxLength = maxLength;
   }

   @Override
   public ConfigurationOptionRestriction getOptionRestriction()
   {
      return optionRestriction;
   }

   @Override
   public void setRegex(ConfigurationOptionRestriction optionRestriction)
   {
      this.optionRestriction = optionRestriction;
   }

   @Override
   public String toString()
   {
      return "ConfigurationOptionInfoImpl{" +
         "namespace='" + namespace + '\'' +
         ", name='" + name + '\'' +
         ", defaultValue='" + defaultValue + '\'' +
         ", changeSeverity=" + changeSeverity +
         ", userDefined=" + userDefined +
         ", valueType=" + valueType +
         ", valueOptions=" + valueOptions +
         ", minValue=" + minValue +
         ", maxValue=" + maxValue +
         ", maxLength=" + maxLength +
         ", optionRestriction=" + optionRestriction +
         '}';
   }
}
