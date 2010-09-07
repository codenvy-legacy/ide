/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ide.testframework.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.gwtframework.commons.rest.HTTPStatus;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@SuppressWarnings("serial")
public abstract class AbstractTestServlet extends HttpServlet
{

   private HashMap<String, MockRequestHandler> handlers = new HashMap<String, MockRequestHandler>();

   @SuppressWarnings("unchecked")
   public AbstractTestServlet()
   {
      List<String> classes = new ClassScanner().getClasses();
      List<String> handlerNames = new ArrayList<String>();

      for (String className : classes)
      {
         try
         {
            if (handlerNames.contains(className))
            {
               continue;
            }

            Class c = Class.forName(className);
            if (c.getAnnotation(CanHandleRequest.class) != null)
            {
               MockRequestHandler handler = (MockRequestHandler)c.newInstance();
               String mapping = handler.getClass().getAnnotation(CanHandleRequest.class).value();
               handlers.put(mapping, handler);
               handlerNames.add(className);
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }

      }
   }

   @Override
   protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
      IOException
   {
      String pathInfo = request.getPathInfo();

      if (pathInfo.startsWith("/"))
      {
         pathInfo = pathInfo.substring(1);
      }

      String mapping = pathInfo.substring(0, pathInfo.indexOf("/"));
      pathInfo = pathInfo.substring(pathInfo.indexOf("/"));

      MockRequestHandler handler = handlers.get(mapping);
      if (handler == null)
      {
         response.setStatus(HTTPStatus.NOT_IMPLEMENTED);
         return;
      }

      handler.handleRequest(pathInfo, request, response);
   }

}
