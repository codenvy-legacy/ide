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
package org.exoplatform.ide;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public interface MenuCommands
{
   public interface New
   {
      public static final String XML_FILE = "XML File";
      public static final String HTML_FILE = "HTML File";
      public static final String TEXT_FILE = "Text File";
      public static final String JAVASCRIPT_FILE = "JavaScript File";
      public static final String CSS_FILE = "CSS File";
      public static final String GOOGLE_GADGET_FILE = "Google Gadget";
      public static final String REST_SERVICE_FILE = "REST Service";
      public static final String GROOVY_SCRIPT_FILE = "Groovy Script";
      public static final String GROOVY_TEMPLATE_FILE = "Groovy Template";
      public static final String NETVIBES_WIDGET_FILE = "Netvibes Widget";
      public static final String FROM_TEMPLATE = "From Template...";
      public static final String FOLDER = "Folder...";
   }

   public interface View
   {
      public static final String VIEW = "View";
      
      public static final String GO_TO_FOLDER = "Go to Folder";
   }
}
