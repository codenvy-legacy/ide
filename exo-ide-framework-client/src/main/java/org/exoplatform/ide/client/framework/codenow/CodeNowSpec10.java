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
package org.exoplatform.ide.client.framework.codenow;

/**
 * Describe parameters for CodeNow feature.
 * Version of specification CodeNow 1.0
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: CodeNowSpec10.java Nov 30, 2012 vetal $
 *
 */
public class CodeNowSpec10
{

   /**
    * Mandatory parameter
    */
   public final static String VERSION_PARAMETER = "v";

   public final static String CURRENT_VERSION = "codenow1.0";

   /**
    * Mandatory parameter use for setting location sources in Version Control System  
    */
   public final static String VCS_URL = "vcsurl";

   /**
    * Version Control System by default "Git"; 
    */
   public final static String VCS = "vcs";

   /**
    * 
    */
   public final static String DEFAULT_VCS = "Git";

   /**
    * Optional parameter, by default will be use DEFAULT_ACTION_PARAMETER 
    */
   public final static String ACTION_PARAMETER = "action";

   public final static String DEFAULT_ACTION = "openproject";

   /**
    * Optional parameter for project name in file system, if not set we try detect it from  
    * VCS_URL param.
    * 
    * e.g. for "git@github.com:exodev/ide.git" project name will be "ide" 
    */
   public final static String PROJECT_NAME = "pn";

}
