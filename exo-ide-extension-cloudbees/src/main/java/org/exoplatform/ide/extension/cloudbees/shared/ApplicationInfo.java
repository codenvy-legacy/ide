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
package org.exoplatform.ide.extension.cloudbees.shared;

/**
 * Application info.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ApplicationInfo.java Mar 15, 2012 9:25:11 AM azatsarynnyy $
 */
public interface ApplicationInfo
{

   /** @return the id */
   String getId();

   /**
    * @param id
    *    the id to set
    */
   void setId(String id);

   /** @return the title */
   String getTitle();

   /**
    * @param title
    *    the title to set
    */
   void setTitle(String title);

   /** @return the status */
   String getStatus();

   /**
    * @param status
    *    the status to set
    */
   void setStatus(String status);

   /** @return the url */
   String getUrl();

   /**
    * @param url
    *    the url to set
    */
   void setUrl(String url);

   /** @return the instances */
   String getInstances();

   /**
    * @param instances
    *    the instances to set
    */
   void setInstances(String instances);

   /** @return the securityMode */
   String getSecurityMode();

   /**
    * @param securityMode
    *    the securityMode to set
    */
   void setSecurityMode(String securityMode);

   /** @return the maxMemory */
   String getMaxMemory();

   /**
    * @param maxMemory
    *    the maxMemory to set
    */
   void setMaxMemory(String maxMemory);

   /** @return the idleTimeout */
   String getIdleTimeout();

   /**
    * @param idleTimeout
    *    the idleTimeout to set
    */
   void setIdleTimeout(String idleTimeout);

   /** @return the serverPull */
   String getServerPool();

   /**
    * @param serverPool
    *    the serverPull to set
    */
   void setServerPool(String serverPool);

   /** @return the container */
   String getContainer();

   /**
    * @param container
    *    the container to set
    */
   void setContainer(String container);

   /** @return size of the cluster */
   String getClusterSize();

   /**
    * @param clusterSize
    *    size of the cluster to set
    */
   void setClusterSize(String clusterSize);
}