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
package org.exoplatform.ide.extension.cloudbees.client.info;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.json.client.JSONObject;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Sep 21, 2011 evgen $
 *
 */
public class ApplicationInfo
{
   private String id;

   private String title;

   private String status;

   private String url;

   private String instances;

   private String securityMode;

   private String maxMemory;

   private String idleTimeout;

   private String serverPool;

   private JSONObject init;

   /**
    * 
    */
   public ApplicationInfo()
   {
   }

   public void init(JSONObject init)
   {
      this.init = init;
      setId(init.get("id").isString().stringValue());
      setTitle(init.get("title").isString().stringValue());
      setServerPool(init.get("serverPool").isString().stringValue());
      setStatus(init.get("status").isString().stringValue());
      setIdleTimeout(init.get("idleTimeout").isString().stringValue());
      setMaxMemory(init.get("maxMemory").isString().stringValue());
      setSecurityMode(init.get("securityMode").isString().stringValue());
      setInstances(init.get("clusterSize").isString().stringValue());
      setUrl(init.get("url").isString().stringValue());
   }

   /**
    * @return the id
    */
   public String getId()
   {
      return id;
   }

   /**
    * @param id the id to set
    */
   public void setId(String id)
   {
      this.id = id;
   }

   /**
    * @return the title
    */
   public String getTitle()
   {
      return title;
   }

   /**
    * @param title the title to set
    */
   public void setTitle(String title)
   {
      this.title = title;
   }

   /**
    * @return the status
    */
   public String getStatus()
   {
      return status;
   }

   /**
    * @param status the status to set
    */
   public void setStatus(String status)
   {
      this.status = status;
   }

   /**
    * @return the url
    */
   public String getUrl()
   {
      return url;
   }

   /**
    * @param url the url to set
    */
   public void setUrl(String url)
   {
      this.url = url;
   }

   /**
    * @return the instances
    */
   public String getInstances()
   {
      return instances;
   }

   /**
    * @param instances the instances to set
    */
   public void setInstances(String instances)
   {
      this.instances = instances;
   }

   /**
    * @return the securityMode
    */
   public String getSecurityMode()
   {
      return securityMode;
   }

   /**
    * @param securityMode the securityMode to set
    */
   public void setSecurityMode(String securityMode)
   {
      this.securityMode = securityMode;
   }

   /**
    * @return the maxMemory
    */
   public String getMaxMemory()
   {
      return maxMemory;
   }

   /**
    * @param maxMemory the maxMemory to set
    */
   public void setMaxMemory(String maxMemory)
   {
      this.maxMemory = maxMemory;
   }

   /**
    * @return the idleTimeout
    */
   public String getIdleTimeout()
   {
      return idleTimeout;
   }

   /**
    * @param idleTimeout the idleTimeout to set
    */
   public void setIdleTimeout(String idleTimeout)
   {
      this.idleTimeout = idleTimeout;
   }

   /**
    * @return the serverPull
    */
   public String getServerPool()
   {
      return serverPool;
   }

   /**
    * @param serverPool the serverPull to set
    */
   public void setServerPool(String serverPool)
   {
      this.serverPool = serverPool;
   }

   public Map<String, String> toMap()
   {
      Map<String, String> map = new HashMap<String, String>(init.size());

      for (String key : init.keySet())
      {
         //all fields has id JSONString values 
         map.put(key, init.get(key).isString().stringValue());
      }

      return map;
   }

}
