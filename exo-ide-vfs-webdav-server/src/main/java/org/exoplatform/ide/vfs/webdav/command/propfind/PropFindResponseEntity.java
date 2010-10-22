/*
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
 */
package org.exoplatform.ide.vfs.webdav.command.propfind;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Set;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.exoplatform.ide.vfs.webdav.resource.CollectionResource;
import org.exoplatform.services.jcr.impl.Constants;
import org.exoplatform.services.jcr.webdav.resource.IllegalResourceTypeException;
import org.exoplatform.services.jcr.webdav.resource.Resource;
import org.exoplatform.services.jcr.webdav.xml.PropertyWriteUtil;
import org.exoplatform.services.jcr.webdav.xml.PropstatGroupedRepresentation;
import org.exoplatform.services.jcr.webdav.xml.WebDavNamespaceContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * Created by The eXo Platform SARL .<br/>
 * 
 * @author Gennady Azarenkov
 * @version $Id: $
 */

public class PropFindResponseEntity implements StreamingOutput
{

   /**
    * logger.
    */
   private static Log log = ExoLogger.getLogger("exo.jcr.component.webdav.PropFindResponseEntity");

   /**
    * XML writer.
    */
   protected XMLStreamWriter xmlStreamWriter;

   /**
    * Output stream.
    */
   protected OutputStream outputStream;

   /**
    * Namespace context.
    */
   protected final WebDavNamespaceContext namespaceContext;

   /**
    * Root resource.
    */
   protected final Resource rootResource;

   /**
    * The list of properties to get.
    */
   protected Set<QName> propertyNames;

   /**
    * Request depth.
    */
   protected final int depth;

   /**
    * Boolean flag, shows if only property names a requested.
    */
   protected final boolean propertyNamesOnly;
   
   /**
    * Session.
    */
   protected final Session session;

   /**
    * Constructor.
    * 
    * @param depth reqest depth.
    * @param rootResource root resource.
    * @param propertyNames the list of properties requested
    * @param propertyNamesOnly if only property names a requested
    */
   public PropFindResponseEntity(int depth, Resource rootResource, Set<QName> propertyNames, boolean propertyNamesOnly, Session session)
   {
      this.rootResource = rootResource;
      this.namespaceContext = rootResource.getNamespaceContext();
      this.propertyNames = propertyNames;
      this.depth = depth;
      this.propertyNamesOnly = propertyNamesOnly;
      this.session = session;
   }

   public PropFindResponseEntity(int depth, Resource rootResource, Set<QName> propertyNames, boolean propertyNamesOnly)
   {
      this(depth, rootResource, propertyNames, propertyNamesOnly, null);
   }

   /**
    * {@inheritDoc}
    */
   public void write(OutputStream stream) throws IOException
   {
      this.outputStream = stream;
      try
      {
         this.xmlStreamWriter =
            XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream, Constants.DEFAULT_ENCODING);
         xmlStreamWriter.setNamespaceContext(namespaceContext);
         xmlStreamWriter.setDefaultNamespace("DAV:");

         xmlStreamWriter.writeStartDocument();
         xmlStreamWriter.writeStartElement("D", "multistatus", "DAV:");
         xmlStreamWriter.writeNamespace("D", "DAV:");

         xmlStreamWriter.writeAttribute("xmlns:b", "urn:uuid:c2f41010-65b3-11d1-a29f-00aa00c14882/");

         traverseResources(rootResource, 0);

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

   /**
    * Traverses resources and collects the vales of required properties.
    * 
    * @param resource resource to traverse
    * @param counter the depth
    * @throws XMLStreamException {@link XMLStreamException}
    * @throws RepositoryException {@link RepositoryException}
    * @throws IllegalResourceTypeException {@link IllegalResourceTypeException}
    * @throws URISyntaxException {@link URISyntaxException}
    * @throws UnsupportedEncodingException 
    */
   private void traverseResources(Resource resource, int counter) throws XMLStreamException, RepositoryException,
      IllegalResourceTypeException, URISyntaxException, UnsupportedEncodingException
   {

      xmlStreamWriter.writeStartElement("DAV:", "response");

      xmlStreamWriter.writeStartElement("DAV:", "href");
      String href = URLDecoder.decode(resource.getIdentifier().toASCIIString(), "UTF-8");
      if (resource.isCollection())
      {
         xmlStreamWriter.writeCharacters(href + "/");
      }
      else
      {
         xmlStreamWriter.writeCharacters(href);
      }
      xmlStreamWriter.writeEndElement();

      PropstatGroupedRepresentation propstat =
         new PropstatGroupedRepresentation(resource, propertyNames, propertyNamesOnly, session);

      PropertyWriteUtil.writePropStats(xmlStreamWriter, propstat.getPropStats());

      xmlStreamWriter.writeEndElement();

      int d = depth;

      if (resource.isCollection())
      {
         if (counter < d)
         {
            CollectionResource collection = (CollectionResource)resource;
            for (Resource child : collection.getResources())
            {
               traverseResources(child, counter + 1);
            }
         }
      }

   }

}
