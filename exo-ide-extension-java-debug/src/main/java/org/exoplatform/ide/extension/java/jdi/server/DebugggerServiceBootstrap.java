/**
 * Copyright (C) 2010 eXo Platform SAS.
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

package org.exoplatform.ide.extension.java.jdi.server;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: BookServiceBootstrap.java 124 2010-11-23 14:17:20Z andrew00x $
 */
public class DebugggerServiceBootstrap implements ServletContextListener
{

   public void contextDestroyed(ServletContextEvent sce)
   {
      ServletContext sctx = sce.getServletContext();
//      sctx.removeAttribute(DEBUGGER_REGISTRY);
   }

   public void contextInitialized(ServletContextEvent sce)
   {
      ServletContext sctx = sce.getServletContext();
//      sctx.setAttribute(DEBUGGER_REGISTRY, new DebuggerRegistry());
   }
}
