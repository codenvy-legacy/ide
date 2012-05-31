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
package org.exoplatform.ide.extension.java.server;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class CodeAssistantStorageClient
{
   private static final Log LOG = ExoLogger.getLogger(CodeAssistantStorageClient.class);

   public static final String STORAGE_BASE_URL = "exo.ide.codeassistan.storage-base-url";

   private final String baseURL;

   /**
    * 
    */
   public CodeAssistantStorageClient(InitParams initParams)
   {
      this(readValueParam(initParams, "codeassistant-storage-base-url", System.getProperty(STORAGE_BASE_URL)));
   }

   /**
    * @param baseURL
    */
   protected CodeAssistantStorageClient(String baseURL)
   {
      super();
      this.baseURL = baseURL;
   }

   private static String readValueParam(InitParams initParams, String paramName, String defaultValue)
   {
      if (initParams != null)
      {
         ValueParam vp = initParams.getValueParam(paramName);
         if (vp != null)
         {
            return vp.getValue();
         }
      }
      return defaultValue;
   }

   public void updateIndex(String dependencyList, String zipUrl)
   {
      try
      {
         URL url = new URL(baseURL + "/storage/update");
         HttpURLConnection http = null;

         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("POST");
         http.setRequestProperty("content-type", "application/json");
         http.setDoOutput(true);
         OutputStreamWriter w = new OutputStreamWriter(http.getOutputStream());
         w.write("{\"dependencies\":");
         w.write(dependencyList);
         w.write(",\"zipUrl\":");
         w.write("\"" + zipUrl + "\"");
         w.write("}");
         w.flush();
         w.close();
         http.getResponseCode();
      }
      catch (MalformedURLException e)
      {
         if (LOG.isDebugEnabled())
            LOG.debug(e.getMessage(), e);
      }
      catch (IOException e)
      {
         if (LOG.isDebugEnabled())
            LOG.debug(e.getMessage(), e);
      }
   }

}
