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
package org.exoplatform.ide.vfs.webdav.resource;

import java.net.URI;

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.xml.namespace.QName;

import org.exoplatform.common.util.HierarchicalProperty;
import org.exoplatform.services.jcr.webdav.resource.IllegalResourceTypeException;
import org.exoplatform.services.jcr.webdav.resource.VersionHistoryResource;
import org.exoplatform.services.jcr.webdav.resource.VersionedResource;
import org.exoplatform.services.jcr.webdav.xml.WebDavNamespaceContext;

/**
 * Created by The eXo Platform SARL .<br/>
 * Versioned file resource (nt:file+mix:versionable/nt:resource )
 * 
 * @author Gennady Azarenkov
 * @version $Id: $
 */

public class VersionedFileResource extends FileResource implements VersionedResource
{

   static
   {
      FILE_SKIP.add("jcr:predecessors");
      FILE_SKIP.add("jcr:versionHistory");
      FILE_SKIP.add("jcr:baseVersion");
      FILE_SKIP.add("jcr:uuid");
   }

   /**
    * 
    * @param identifier resource identifier
    * @param node node
    * @param namespaceContext namespace context
    * @throws IllegalResourceTypeException {@link IllegalResourceTypeException}
    * @throws RepositoryException {@link RepositoryException}}
    */
   public VersionedFileResource(URI identifier, Node node, WebDavNamespaceContext namespaceContext)
      throws IllegalResourceTypeException, RepositoryException
   {
      super(VERSIONED_FILE, identifier, node, namespaceContext);
   }

   /**
    * {@inheritDoc}
    */
   public VersionHistoryResource getVersionHistory() throws RepositoryException, IllegalResourceTypeException
   {
      return new VersionHistoryResource(versionHistoryURI(), node.getVersionHistory(), this, namespaceContext);
   }

   /**
    * Resturns version history URI.
    * 
    * @return version history URI
    */
   protected final URI versionHistoryURI()
   {
      return URI.create(identifier.toASCIIString() + "?vh");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public HierarchicalProperty getProperty(QName name) throws PathNotFoundException, AccessDeniedException,
      RepositoryException
   {
      if (name.equals(ISVERSIONED))
      {
         return new HierarchicalProperty(name, "1");
      }
      else if (name.equals(CHECKEDIN))
      {
         if (node.isCheckedOut())
         {
            throw new PathNotFoundException();
         }

         String checkedInHref = identifier.toASCIIString() + "?version=" + node.getBaseVersion().getName();
         HierarchicalProperty checkedIn = new HierarchicalProperty(name);
         checkedIn.addChild(new HierarchicalProperty(new QName("DAV:", "href"), checkedInHref));
         return checkedIn;

      }
      else if (name.equals(CHECKEDOUT))
      {
         if (!node.isCheckedOut())
         {
            throw new PathNotFoundException();
         }
         return new HierarchicalProperty(name);
      }
      else if (name.equals(VERSIONNAME))
      {
         return new HierarchicalProperty(name, node.getBaseVersion().getName());
      }

      return super.getProperty(name);
   }

}
