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
package org.exoplatform.ide.extension.gadget.server.shindig;

import org.apache.shindig.gadgets.http.BasicHttpFetcher;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The goal of Http Fetcher subclass is to overwrite the default timeout in BasicHttpFetcher 
 * which is quite short time to make a conversion if the server is slow.
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
@Singleton
public class ExoIdeHttpFetcher extends BasicHttpFetcher
{

   private static final int DEFAULT_CONNECT_TIMEOUT_MS = 15000;

   private static final int DEFAULT_MAX_OBJECT_SIZE = 1024 * 1024;

   @Inject
   public ExoIdeHttpFetcher()
   {
      super(DEFAULT_MAX_OBJECT_SIZE, DEFAULT_CONNECT_TIMEOUT_MS);
   }
}
