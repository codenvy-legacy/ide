/*
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
package org.exoplatform.ide.extension.groovy.client.service;

import com.google.gwt.http.client.Header;
import com.google.gwt.http.client.Response;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class RestServiceOutput
{

   private String url;

   private String method;

   private Response response;

   public RestServiceOutput(String url, String method)
   {
      this.url = url;
      this.method = method;
   }

   public void setResponse(Response response)
   {
      this.response = response;
   }

   public Response getResponse()
   {
      return response;
   }

   public String getUrl()
   {
      return url;
   }

   public String getMethod()
   {
      return method;
   }

   public String getResponseAsHtmlString()
   {
      String headers = new String();
      for (Header header : response.getHeaders())
      {
         // Add temporary for fix strange bug in IE http://jira.exoplatform.org/browse/IDE-250
         // TODO: try found problem
         try
         {
            headers += "<b>" + header.getName() + "</b>" + "&nbsp;:&nbsp;" + header.getValue() + "<br/>";
         }
         catch (Exception e)
         {
            // TODO: handle exception
         }

      }
      //
      String result =
         "- -Status - - - - - - - -<br/>" + response.getStatusCode() + "&nbsp;" + response.getStatusText() + "<br/>"
            + "- -Headers- - - - - - - -<br/>" + headers + "- -Text - - - - - - - - -<br/>"
            + response.getText().replace("<", "&lt;").replace(">", "&gt;");
      return result;
   }

}
