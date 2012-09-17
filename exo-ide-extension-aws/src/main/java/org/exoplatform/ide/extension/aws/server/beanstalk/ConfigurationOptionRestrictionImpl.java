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

import org.exoplatform.ide.extension.aws.shared.beanstalk.ConfigurationOptionRestriction;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ConfigurationOptionRestrictionImpl implements ConfigurationOptionRestriction
{
   private String label;
   private String pattern;

   public ConfigurationOptionRestrictionImpl(String label, String pattern)
   {
      this.label = label;
      this.pattern = pattern;
   }

   public ConfigurationOptionRestrictionImpl()
   {
   }

   @Override
   public String getLabel()
   {
      return label;
   }

   @Override
   public void setLabel(String label)
   {
      this.label = label;
   }

   @Override
   public String getPattern()
   {
      return pattern;
   }

   @Override
   public void setPattern(String pattern)
   {
      this.pattern = pattern;
   }
}
