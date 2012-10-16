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
package org.exoplatform.ide.extension.aws.shared.ec2;

/**
 * Information about user's key pairs
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface KeyPairInfo
{
   /**
    * Get name of the key pair
    *
    * @return
    *    name of the key pair
    */
   String getName();

   /**
    * Set name of the key pair
    *
    * @param name
    *    name of the key pair
    */
   void setName(String name);

   /**
    * Get SHA-1 digest of the DER encoded private key.
    *
    * @return
    *    String containing SHA-1 digest of the DER encoded private key
    */
   String getFingerprint();

   /**
    * Set SHA-1 digest of the DER encoded private key.
    *
    * @param fingerprint
    *    String containing SHA-1 digest of the DER encoded private key
    */
   void setFingerprint(String fingerprint);
}
