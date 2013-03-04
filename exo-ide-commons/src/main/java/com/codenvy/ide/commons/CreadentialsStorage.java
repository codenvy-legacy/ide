/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.commons;

import org.exoplatform.ide.commons.ReadCredentialsException;
import org.exoplatform.ide.commons.WriteCredentialsException;

/**
 * @author <a href="mailto:vparfonov@codenvy.com">Vitaly Parfonov</a>
 * @version $Id: CreadentialsStorage.java Mar 1, 2013 vetal $
 *
 */
public interface CreadentialsStorage
{
   /**
    * @param user
    * @param target
    * @param credentials
    */
   void writeCredetials(String user, String target, Credentials credentials) throws WriteCredentialsException;

   /**
    * @param user
    * @param target
    * @return
    */
   Credentials readCredetials(String user, String target) throws ReadCredentialsException;
}