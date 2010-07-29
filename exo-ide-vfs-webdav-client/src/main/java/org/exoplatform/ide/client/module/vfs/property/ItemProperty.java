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
package org.exoplatform.ide.client.module.vfs.property;

import org.exoplatform.gwtframework.commons.xml.QName;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ItemProperty
{

   public interface Namespace
   {

      static final String DAV = "DAV:";

      static final String JCR = "http://www.jcp.org/jcr/1.0";

      static final String EXO = "http://www.exoplatform.com/jcr/exo/1.0";

   }

   /*
    * WebDAV properties
    */

   public static QName DISPLAYNAME = new QName("displayname", Namespace.DAV);

   public static QName CREATIONDATE = new QName("creationdate", Namespace.DAV);

   public static QName GETCONTENTTYPE = new QName("getcontenttype", Namespace.DAV);

   public static QName GETLASTMODIFIED = new QName("getlastmodified", Namespace.DAV);

   public static QName GETCONTENTLENGTH = new QName("getcontentlength", Namespace.DAV);

   public static QName RESOURCETYPE = new QName("resourcetype", Namespace.DAV);

   /*
    * JCR_PROPERTIES
    */

   public static QName JCR_CONTENT = new QName("content", Namespace.JCR);

   public static QName JCR_NODETYPE = new QName("nodeType", Namespace.JCR);

   public static QName JCR_PRIMARYTYPE = new QName("primaryType", Namespace.JCR);

   public static QName JCR_ISCHECKEDOUT = new QName("isCheckedOut", Namespace.JCR);

   /*
    * EXO PROPERTIES
    */

   public static QName EXO_AUTOLOAD = new QName("autoload", Namespace.EXO);

}
