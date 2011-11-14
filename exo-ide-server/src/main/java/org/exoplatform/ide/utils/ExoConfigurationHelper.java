/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.utils;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.container.xml.ValuesParam;

import java.util.Collections;
import java.util.List;

/**
 * Utilities to simplify regular operations with configuration of ExoContainer components.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ExoConfigurationHelper
{
   /**
    * Get 'value-param' with <code>name</code> from InitParams instance. If <code>initParams == null</code> or does not
    * contains requested 'value-param' this method return <code>null</code>.
    * 
    * @param initParams the InitParams
    * @param name the name of 'value-param'
    * @return the value of requested 'value-param' or <code>null</code>
    */
   public static String readValueParam(InitParams initParams, String name)
   {
      return readValueParam(initParams, name, null);
   }

   /**
    * Get 'value-param' with <code>name</code> from InitParams instance. If <code>initParams == null</code> or does not
    * contains requested 'value-param' this method return <code>defaultValue</code>.
    * 
    * @param initParams the InitParams
    * @param name the name of 'value-param'
    * @param defaultValue the default value
    * @return the value of requested 'value-param' or <code>defaultValue</code>
    */
   public static String readValueParam(InitParams initParams, String name, String defaultValue)
   {
      if (initParams != null)
      {
         ValueParam vp = initParams.getValueParam(name);
         if (vp != null)
         {
            return vp.getValue();
         }
      }
      return defaultValue;
   }

   /**
    * Get 'values-param' with <code>name</code> from InitParams instance. If <code>initParams == null</code> or does not
    * contains requested 'values-param' this method return empty List never <code>null</code>. The returned List is
    * unmodifiable.
    * <p>
    * If part of configuration looks like:
    * 
    * <pre>
    * ...
    * &lt;init-params&gt;
    *    &lt;values-param&gt;
    *       &lt;name&gt;my-parameters&lt;/name&gt;
    *       &lt;value&gt;foo&lt;/value&gt;
    *       &lt;value&gt;bar&lt;/value&gt;
    *    &lt;/values-param&gt;
    * &lt;/init-params&gt;
    * ...
    * </pre>
    * 
    * It becomes to List: <code>["foo", "bar"]</code>
    * 
    * @param initParams the InitParams
    * @param name name of 'values-param'
    * @return unmodifiable List of requested 'values-param' or empty List if requested parameter not found
    */
   @SuppressWarnings("unchecked")
   public static List<String> readValuesParam(InitParams initParams, String name)
   {
      if (initParams != null)
      {
         ValuesParam vp = initParams.getValuesParam(name);
         if (vp != null)
         {
            return Collections.unmodifiableList(vp.getValues());
         }
      }
      return Collections.emptyList();
   }

   private ExoConfigurationHelper()
   {
   }
}
