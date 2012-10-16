/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General  License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General  License for more details.
 *
 * You should have received a copy of the GNU Lesser General 
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.extension.aws.shared.beanstalk;

import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ConfigurationOptionInfo
{
   String getNamespace();

   void setNamespace(String namespace);

   String getName();

   void setName(String name);

   String getDefaultValue();

   void setDefaultValue(String defaultValue);

   ConfigurationOptionChangeSeverity getChangeSeverity();

   void setChangeSeverity(ConfigurationOptionChangeSeverity changeSeverity);

   boolean isUserDefined();

   void setUserDefined(boolean userDefined);

   ConfigurationOptionType getValueType();

   void setValueType(ConfigurationOptionType valueType);

   List<String> getValueOptions();

   void setValueOptions(List<String> valueOptions);

   Integer getMinValue();

   void setMinValue(Integer minValue);

   Integer getMaxValue();

   void setMaxValue(Integer maxValue);

   Integer getMaxLength();

   void setMaxLength(Integer maxLength);

   ConfigurationOptionRestriction getOptionRestriction();

   void setRegex(ConfigurationOptionRestriction optionRestriction);
}
