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
package org.exoplatform.ide.client.remote.service;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.Unmarshallable;
import org.exoplatform.ide.client.framework.vfs.File;

import com.google.gwt.http.client.Response;

/**
 * Unmarshaller for Get remote file content response.
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class RemoteFileContentUnmarshaller implements Unmarshallable
{

   /**
    * File
    */
   private File file;

   /**
    * Crates new instance of this unmarshaller.
    * 
    * @param file
    */
   public RemoteFileContentUnmarshaller(File file)
   {
      this.file = file;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      String contentType = response.getHeader(HTTPHeader.CONTENT_TYPE);
      if (contentType.indexOf("charset=") >= 0)
      {
         contentType = contentType.substring(0, contentType.indexOf(";"));
      }
      file.setContentType(contentType);

      file.setContent(response.getText());
   }

}
