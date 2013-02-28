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

import java.util.HashMap;
import java.util.Map;

/**
 * <p>Defines a component that holds variables of type {@link ThreadLocal}
 * whose value is required by the component to work normally and cannot be recovered. 
 * This component is mainly used when we want to do a task asynchronously, in that case
 * to ensure that the task will be executed in the same conditions as if it would be 
 * executed synchronously we need to transfer the thread context from the original
 * thread to the executor thread.</p>
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: EnvironmentContext.java Feb 26, 2013 vetal $
 *
 */
public class EnvironmentContext
{

   public final static String WORKSPACE = "workspace";

   private Map<String, Object> environment;

   /**
    * ThreadLocal keeper for EnvironmentContext.
    */
   private static ThreadLocal<EnvironmentContext> current = new ThreadLocal<EnvironmentContext>();

   public EnvironmentContext()
   {
      environment = new HashMap<String, Object>();
   }

   public static EnvironmentContext getCurrentEnvironment()
   {
      return current.get();
   }

   public static void setCurrentEnvironment(EnvironmentContext environment)
   {
      current.set(environment);
   }

   public void setEnvironmentVariable(String name, String value)
   {
      environment.put(name, value);
   }

   public Object getEnvironmentVariable(String name)
   {
      return environment.get(name);
   }

}
