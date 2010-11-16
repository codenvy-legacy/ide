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

import javax.ws.rs.core.Response;


/**
 * Service provide save meta information about classes in storage.
 * Information save according to hierarchy  in packet.
 * For exapmle: 
 * for class org.exoplatform.ide.groovy.codeassistant.ClassInfoStorage
 * it will be
 * /org
 *  /org.exoplatform
 *   /org.exoplatform.ide
 *    /org.exoplatform.ide.groovy
 *     /org.exoplatform.ide.groovy.codeassistant
 *      /org.exoplatform.ide.groovy.codeassistant.ClassInfoStorage
 * 
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public interface ClassInfoStorage
{

   /**
    * Save information about classes in jar. Can be filtering by package name 
    * 
    * @param jarPath the path to jar
    * @param packageName the package name for filtering classes if set to null save
    *        info about all classes in jar 
    * @return true if save info successfully
    * @throws SaveClassInfoException
    */
   void addClassesFormJar(String jarPath,  String packageName) throws SaveClassInfoException;

   /**
    * Save information about class. 
    * 
    * @param fqn the Canonical Name of classes
    * @return
    * @throws SaveClassInfoException
    */
   void addClass(String fqn) throws SaveClassInfoException;

   /**
    * Save information about classes in source. Can be filtering by package name 
    * Can be used for save information about classes from jdk source (src.zip) 
    * 
    * @param javaSrcPath the path to jar
    * @param packageName the package name for filtering classes if set to null save
    *        info about all classes in jar 
    * @return true if save info successfully
    * @throws SaveClassInfoException
    */
   void addClassesFromJavaSource(String javaSrcPath, String packageName) throws SaveClassInfoException;

}