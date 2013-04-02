/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.commons;

import org.everrest.core.impl.provider.json.JsonHandler;
import org.everrest.core.impl.provider.json.JsonParser;

/**
 * JSON parser that support transformation of names in JSON document.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 * @see JsonNameConvention
 * @see JsonNameConventions
 */
public class NameConventionJsonParser extends JsonParser
{
   public NameConventionJsonParser(JsonNameConvention nameConvention)
   {
      super(new NameConventionJsonHandler(nameConvention));
   }

   private static class NameConventionJsonHandler extends JsonHandler
   {
      private final JsonNameConvention nameConvention;

      private NameConventionJsonHandler(JsonNameConvention nameConvention)
      {
         this.nameConvention = nameConvention;
      }

      @Override
      public void key(String key)
      {
         super.key(nameConvention.toJavaName(key));
      }
   }
}
