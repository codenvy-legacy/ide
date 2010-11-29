/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.groovy.codeassistant;

/**
 *
 * Service provide save javadoc groovydoc in storage.
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public interface DocStorage
{
   
   /**
    * Save docs from jar-source. Can be filtering by package name 
    * 
    * @param jarPath the path to jar
    * @param packageName the package name for filtering classes if set to null save
    *        info about all classes in jar 
    * @throws SaveClassInfoException
    */
   void addDocs(String jarPath,  String packageName) throws SaveDocException;
   
   /**
    * Save docs from source file. 
    * 
    * @param pathToFile the path to source file
    * @throws SaveClassInfoException
    */
   void addDocsFromSource(String pathToFile) throws SaveDocException;

}
