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
package org.exoplatform.ideall.discovery;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.exoplatform.services.jcr.impl.Constants;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class EntryPointListEntity implements StreamingOutput
{

   /**
    * logger.
    */
   private static Log log = ExoLogger.getLogger(EntryPointListEntity.class);

   private List<String> entryPoints;

   public EntryPointListEntity(List<String> entryPoints)
   {
      this.entryPoints = entryPoints;
   }

   public void write(OutputStream stream) throws IOException, WebApplicationException
   {
      try
      {
         XMLStreamWriter xmlStreamWriter =
            XMLOutputFactory.newInstance().createXMLStreamWriter(stream, Constants.DEFAULT_ENCODING);
         //xmlStreamWriter.setNamespaceContext(namespaceContext);
         //xmlStreamWriter.setDefaultNamespace("DAV:");

         xmlStreamWriter.writeStartDocument();
         xmlStreamWriter.writeStartElement("entrypoints");

         //traverseResources(rootResource, 0);
         
         for (String entryPoint : entryPoints) {
            xmlStreamWriter.writeStartElement("entrypoint");
            xmlStreamWriter.writeCharacters(entryPoint);
            xmlStreamWriter.writeEndElement();
         }

         // D:multistatus
         xmlStreamWriter.writeEndElement();
         xmlStreamWriter.writeEndDocument();

         // rootNode.accept(this);
      }
      catch (Exception exc)
      {

         log.error(exc.getMessage(), exc);
         throw new IOException(exc.getMessage());
      }

   }

}
