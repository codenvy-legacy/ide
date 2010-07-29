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
package org.exoplatform.ide.server.servlet.validate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.exoplatform.gwtframework.commons.rest.HTTPMethod;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.ide.groovy.ServletMapping;
import org.exoplatform.ideall.testframework.server.CanHandleRequest;
import org.exoplatform.ideall.testframework.server.MockRequestHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@CanHandleRequest(ServletMapping.VALIDATE_SUCCESSFULL)
public class ValidateSuccessfull implements MockRequestHandler
{

   public void handleRequest(String path, HttpServletRequest request, HttpServletResponse response)
   {
      if (request.getMethod().equals(HTTPMethod.POST)) {
         response.setStatus(HTTPStatus.OK);
      } else {
         response.setStatus(HTTPStatus.INTERNAL_ERROR);
      }
   }

}
