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
package org.exoplatform.ide.vfs.shared;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class Link
{
   // Folder
   public static String REL_CHILDREN = "children";

   public static String REL_CREATE_PROJECT = "create-project";

   public static String REL_CREATE_FOLDER = "create-folder";

   public static String REL_CREATE_FILE = "create-file";

   public static String REL_UPLOAD_FILE = "upload-file";

   public static String REL_EXPORT = "export";

   public static String REL_IMPORT = "import";

   public static String REL_DOWNLOAD_ZIP = "download-zip";

   public static String REL_UPLOAD_ZIP = "upload-zip";

   // File
   public static String REL_CURRENT_VERSION = "current-version";

   public static String REL_VERSION_HISTORY = "version-history";

   public static String REL_CONTENT = "content";

   public static String REL_DOWNLOAD_FILE = "download-file";

   public static String REL_CONTENT_BY_PATH = "content-by-path";

   public static String REL_UNLOCK = "unlock";

   public static String REL_LOCK = "lock";

   // Common
   public static String REL_PARENT = "parent";

   public static String REL_DELETE = "delete";

   public static String REL_MOVE = "move";

   public static String REL_COPY = "copy";

   public static String REL_SELF = "self";

   public static String REL_ITEM = "item";

   public static String REL_ITEM_BY_PATH = "item-by-path";

   public static String REL_ACL = "acl";

   public static String REL_RENAME = "rename";

   public static String REL_SEARCH = "search";

   public static String REL_SEARCH_FORM = "search-form";

   /** URL of resource. */
   private String href;

   /** Produced media type of resource described by this link. */
   private String type;

   /** Relation attribute of link. Client may use it for choice links to retrieve specific info about resource. */
   private String rel;

   public Link(String href, String rel, String type)
   {
      this.href = href;
      this.rel = rel;
      this.type = type;
   }

   public Link()
   {
   }

   public String getHref()
   {
      return href;
   }

   public void setHref(String href)
   {
      this.href = href;
   }

   public String getRel()
   {
      return rel;
   }

   public void setRel(String rel)
   {
      this.rel = rel;
   }

   public String getType()
   {
      return type;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   /**
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString()
   {
      return "Link [href=" + href + ", type=" + type + ", rel=" + rel + "]";
   }
}
