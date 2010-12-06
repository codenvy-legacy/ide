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
package org.exoplatform.ide.vfs;

import java.util.Collection;
import java.util.Map;

/**
 * Item data from persistent storage of virtual file system. Basically it should
 * not give access to content if ItemData represents Document since content may
 * be stored separately. However it is implementation specific. ItemData is
 * immutable updating of meta information or(and) content should be done via
 * corresponded methods of {@link VirtualFileSystem}. Some properties may be
 * updated automatically, e.g. if content of document is updated then
 * 'content-type' properties and so on may be updated also.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface ItemData
{
   /**
    * Unified opaque identifier of item, e.g. path of item in hierarchical
    * systems.
    * 
    * @return identifier
    */
   ObjectId getId();

   /**
    * To get the object's properties.
    * 
    * @return the set of properties
    */
   Map<String, Property<?>> getProperties();

   /**
    * Get subset of properties with specified names.
    * 
    * @param filter property filter
    * @return subset of properties
    */
   Map<String, Property<?>> getProperties(Collection<String> filter);

   /**
    * Get property with specified name.
    * 
    * @param name property name
    * @return property with specified name or <code>null</code>
    */
   Property<?> getProperty(String name);
}
