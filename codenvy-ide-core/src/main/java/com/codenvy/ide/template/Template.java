/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.codenvy.ide.template;

import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id:  Jul 26, 2012 12:40:03 PM anya $
 *
 */
public interface Template
{

   /**
    * @return the isDefault
    */
   Boolean isDefault();

   /**
    * @param isDefault the isDefault to set
    */
   void setDefault(Boolean isDefault);

   /**
    * @return the name
    */
   String getName();

   /**
    * @param name the name to set
    */
   void setName(String name);

   /**
    * @return the description
    */
   String getDescription();

   /**
    * @param description the description to set
    */
   void setDescription(String description);

   /**
    * @return the nodeName
    */
   String getNodeName();

   /**
    * @param nodeName the nodeName to set
    */
   void setNodeName(String nodeName);

   ImageResource getIcon();

}