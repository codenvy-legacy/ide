/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.model.vfs.webdav.marshal;

import org.exoplatform.gwtframework.commons.rest.Marshallable;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class SearchRequestMarshaller implements Marshallable
{

   private String query;

   //andFlag shows whether this is the first condition or not
   //if andFlag has true value, than "AND" must be put before condition
   private boolean andFlag = false;

   public SearchRequestMarshaller(String content, String name, String contentType, String path)
   {
      content = escapeRegisteredSymbols(content);

      String statement = "SELECT * FROM nt:base";

      if (content != null && content.length() > 0)
      {
         statement += " WHERE CONTAINS(*, '" + content + "')";
         andFlag = true;
      }

      if ((name != null) && (name.length() > 0))
      {
         if (andFlag)
         {
            statement += " AND (fn:name() = '" + name + "')";
         }
         else
         {
            statement += " WHERE (fn:name() = '" + name + "')";
            andFlag = true;
         }
      }

      if ((contentType != null) && (contentType.length() > 0))
      {
         if (andFlag)
         {
            statement += " AND (jcr:mimeType = '" + contentType + "')";
         }
         else
         {
            statement += " WHERE (jcr:mimeType = '" + contentType + "')";
            andFlag = true;
         }
      }

      if (path != null && (path.length() > 0))
      {
         if (andFlag)
         {
            statement += " AND jcr:path LIKE '" + path + "/" + "%'";
         }
         else
         {
            // This is made with purpose to get only files (not with folders)
            statement = "SELECT * FROM nt:file WHERE jcr:path LIKE '" + path + "/" + "%'";
            andFlag = true;
         }
      }

      if (!andFlag)
      {
         statement = "SELECT * FROM nt:file";
      }

      query =
         "<?xml version='1.0' encoding='UTF-8' ?>\n" + "<D:searchrequest xmlns:D='DAV:'>\n" + "    <D:sql>\n"
            + statement + "\n" + " </D:sql>\n" + "</D:searchrequest>\n";
   }

   public SearchRequestMarshaller(String content)
   {
      content = escapeRegisteredSymbols(content);
      String statement = "SELECT * FROM nt:base WHERE CONTAINS(*, '" + content + "')";
      query =
         "<?xml version='1.0' encoding='UTF-8' ?>\n" + "<D:searchrequest xmlns:D='DAV:'>\n" + "    <D:sql>\n"
            + statement + "\n" + " </D:sql>\n" + "</D:searchrequest>\n";
   }

   private String escapeRegisteredSymbols(String request)
   {
      String escapedRequest = request;
      escapedRequest = escapedRequest.replace("\\", "\\\\"); // to replace \ on \\
      escapedRequest = escapedRequest.replace("-", "\\-"); // to replace - on \-
      escapedRequest = escapedRequest.replace("\"", "\\\""); // to replace " on \"
      escapedRequest = escapedRequest.replace("'", "\\'"); // to replace ' on \'
      return escapedRequest;
   }

   public String marshal()
   {    
      return query;
   }

}
