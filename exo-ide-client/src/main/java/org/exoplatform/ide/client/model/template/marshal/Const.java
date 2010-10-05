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
package org.exoplatform.ide.client.model.template.marshal;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public interface Const
{
   
   public interface TemplateType
   {
      static final String FILE = "file";
      
      static final String PROJECT = "project";
   }
   
   public static final String TEMPLATE = "template";
   
   public static final String TEMPLATES = "templates";

   //common nodes
   public static final String NAME = "name";
   
   public static final String DESCRIPTION = "description";
   
   public static final String TEMPLATE_TYPE = "template-type";
   
   //file template nodes
   public static final String MIME_TYPE = "mime-type";
   
   public static final String CONTENT = "content";
   
   //project template nodes
   public static final String FILE = "file";
   
   public static final String FOLDER = "folder";
   
   public static final String ITEMS = "items";
   
   public static final String TEMPLATE_FILE_NAME = "template-file-name";
   
   public static final String FILE_NAME = "file-name";

}
