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
package org.exoplatform.ide.authentication.openid;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class OpenIDProvider
{
   private final String discoveryUrl;
   private final String image;
   private final String message;

   public OpenIDProvider(String discoveryUrl, String image, String message)
   {
      this.discoveryUrl = discoveryUrl;
      this.image = image;
      this.message = message;
   }

   public String getDiscoveryUrl()
   {
      return discoveryUrl;
   }

   public String getImage()
   {
      return image;
   }

   public String getMessage()
   {
      return message;
   }
}
