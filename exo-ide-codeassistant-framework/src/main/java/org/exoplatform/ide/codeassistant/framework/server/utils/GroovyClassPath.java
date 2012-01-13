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
package org.exoplatform.ide.codeassistant.framework.server.utils;

/**
 * Groovy class path data. This bean has the JSON representation, like in the following example:
 * 
 * { "entries" : [ { "kind' : "dir", "path" : "jcr://repository/workspace#/groovy-src/" }, { "kind" : "file", "path" :
 * "jcr://repository/workspace#/groovy-src/org/exo/Test.groovy" } ], "extensions" : [] }
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 24, 2010 $
 * 
 */
public class GroovyClassPath
{
   /**
    * Entries of dependencies.
    */
   private GroovyClassPathEntry[] entries;

   /**
    * Extensions.
    */
   private String[] extensions;

   /**
    * @return the entries
    */
   public GroovyClassPathEntry[] getEntries()
   {
      return entries;
   }

   /**
    * @return the extensions
    */
   public String[] getExtensions()
   {
      return extensions;
   }

   /**
    * @param entries the entries to set
    */
   public void setEntries(GroovyClassPathEntry[] entries)
   {
      this.entries = entries;
   }

   /**
    * @param extensions the extensions to set
    */
   public void setExtensions(String[] extensions)
   {
      this.extensions = extensions;
   }
}
