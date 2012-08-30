/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
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
package org.exoplatform.ide.shared.core.resources;

/**
 * Manages a collection of path variables and resolves paths containing a
 * variable reference.
 * <p>
 * A path variable is a pair of non-null elements (name,value) where name is 
 * a case-sensitive string (containing only letters, digits and the underscore
 * character, and not starting with a digit), and value is an absolute
 * <code>IPath</code> object.
 * </p>
 * <p>
 * Path variables allow for the creation of relative paths whose exact
 * location in the file system depends on the value of a variable. A variable
 * reference may only appear as the first segment of a relative path.
 * </p>
 * 
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 15, 2012  
 */
public interface PathVariableManager
{

}
