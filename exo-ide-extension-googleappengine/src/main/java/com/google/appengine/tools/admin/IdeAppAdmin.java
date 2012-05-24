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
package com.google.appengine.tools.admin;

import org.exoplatform.ide.extension.googleappengine.server.AppEngineCookieStore;

import java.io.PrintWriter;

import static com.google.appengine.tools.admin.AppAdminFactory.ApplicationProcessingOptions;
import static com.google.appengine.tools.admin.AppAdminFactory.ConnectOptions;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class IdeAppAdmin extends AppAdminImpl
{
   private final GenericApplication app;
   private final AppEngineCookieStore cookieStore;

   public IdeAppAdmin(ConnectOptions options,
                      GenericApplication app,
                      PrintWriter errorWriter,
                      ApplicationProcessingOptions appOptions,
                      Class<? extends AppVersionUpload> appVersionUploadClass,
                      AppEngineCookieStore cookieStore)
   {
      super(options, app, errorWriter, appOptions, appVersionUploadClass);
      this.app = app;
      this.cookieStore = cookieStore;
   }

   @Override
   protected ServerConnection getServerConnection(ConnectOptions options)
   {
      return new IdeClientLoginServerConnection(options, cookieStore);
   }

   public GenericApplication getApplication()
   {
      return app;
   }
}
