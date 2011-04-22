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
package org.exoplatform.ide.git.server.jgit;

import org.eclipse.jgit.lib.Repository;

/**
 * Listener restore mime-types on JCR nt:file nodes (). Mime
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class RestoreMimeTypeListener implements GitCommandListener
{

   /**
    * @see org.exoplatform.ide.git.server.jgit.GitCommandListener#beforeCommand(java.lang.String,
    *      org.eclipse.jgit.lib.Repository)
    */
   @Override
   public void beforeCommand(String gitCommand, Repository repository)
   {
   }

   /**
    * @see org.exoplatform.ide.git.server.jgit.GitCommandListener#afterCommand(java.lang.String,
    *      org.eclipse.jgit.lib.Repository)
    */
   @Override
   public void afterCommand(String gitCommand, Repository repository)
   {
      if (gitCommand.equals("clone") || gitCommand.equals("fetch"))
         System.out.println(">>>>> Restore jrc:mimeType after " + gitCommand + " operation. ");
      // TODO : Get JCR repository, traverse and restore jrc:mimeType property on nt:file/jcr:content.
   }
}
