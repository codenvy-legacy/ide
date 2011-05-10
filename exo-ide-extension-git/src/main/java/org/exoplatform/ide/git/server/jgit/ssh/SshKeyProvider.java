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
package org.exoplatform.ide.git.server.jgit.ssh;

import java.io.IOException;

/**
 * SSH key manager.
 * 
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface SshKeyProvider
{
   /**
    * Get SSH private key for <code>host</code>.
    * 
    * @param host host name
    * @return private key
    */
   KeyFile getPrivateKey(String host);

   /**
    * Get SSH public key for <code>host</code>.
    * 
    * @param host host name
    * @return public key
    */
   KeyFile getPublicKey(String host) throws IOException;

   /**
    * Generate SSH key files.
    * 
    * @param host host name
    * @param comment comment to add in public key
    * @param passphrase optional passphrase to protect private key
    * @throws IOException if any i/o error occurs
    */
   void genKeyFiles(String host, String comment, String passphrase) throws IOException;
}
