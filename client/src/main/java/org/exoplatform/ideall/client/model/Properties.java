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
package org.exoplatform.ideall.client.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.exoplatform.gwt.commons.xml.QName;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class Properties
{

   public static final String DAV_NAMESPACE = "DAV:";

   public static final String JCR_NAMESPACE = "http://www.jcp.org/jcr/1.0";

   public static final String EXO_NAMESPACE = "http://www.exoplatform.com/jcr/exo/1.0";

   private static HashMap<QName, String> titles = new HashMap<QName, String>();

   private static List<QName> skipProperties = new ArrayList<QName>();

   public static interface DavProperties
   {

      public static QName DISPLAYNAME = new QName("displayname", DAV_NAMESPACE);

      public static QName CREATIONDATE = new QName("creationdate", DAV_NAMESPACE);

      public static QName GETCONTENTTYPE = new QName("getcontenttype", DAV_NAMESPACE);

      public static QName GETLASTMODIFIED = new QName("getlastmodified", DAV_NAMESPACE);

      public static QName GETCONTENTLENGTH = new QName("getcontentlength", DAV_NAMESPACE);

      public static QName RESOURCETYPE = new QName("resourcetype", DAV_NAMESPACE);

   }

   public static interface JCRProperties
   {

      public static QName JCR_CONTENT = new QName("content", JCR_NAMESPACE);

      public static QName JCR_NODETYPE = new QName("nodeType", JCR_NAMESPACE);

      public static QName JCR_PRIMARYTYPE = new QName("primaryType", JCR_NAMESPACE);
      
      public static QName JCR_ISCHECKEDOUT = new QName("isCheckedOut", JCR_NAMESPACE);

   }

   public static interface ExoProperties
   {

      public static QName EXO_AUTOLOAD = new QName("autoload", EXO_NAMESPACE);

   }

   static
   {
      // DAV
      titles.put(DavProperties.DISPLAYNAME, "Display Name");
      titles.put(DavProperties.CREATIONDATE, "Creation Date");
      titles.put(DavProperties.GETCONTENTTYPE, "Content Type");
      titles.put(DavProperties.GETLASTMODIFIED, "Last Modified");
      titles.put(DavProperties.GETCONTENTLENGTH, "Content Length");

      // JCR
      titles.put(JCRProperties.JCR_NODETYPE, "Content Node Type");
      titles.put(JCRProperties.JCR_PRIMARYTYPE, "File Node Type");
      titles.put(JCRProperties.JCR_ISCHECKEDOUT, "Is Checked Out");

      // EXO
      titles.put(ExoProperties.EXO_AUTOLOAD, "Autoload");

      skipProperties.add(DavProperties.RESOURCETYPE);

   }

   public static String getPropertyTitle(QName name)
   {
      return titles.get(name);
   }

   public static boolean isSkip(QName name)
   {
      return skipProperties.contains(name) || (titles.get(name) == null);
   }

}
