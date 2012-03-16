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
 *
 */
public interface ApplicationInfo
{

   /**
    * @return the id
    */
   public String getId();

   /**
    * @param id the id to set
    */
   public void setId(String id);

   /**
    * @return the title
    */
   public String getTitle();

   /**
    * @param title the title to set
    */
   public void setTitle(String title);

   /**
    * @return the status
    */
   public String getStatus();

   /**
    * @param status the status to set
    */
   public void setStatus(String status);

   /**
    * @return the url
    */
   public String getUrl();

   /**
    * @param url the url to set
    */
   public void setUrl(String url);

   /**
    * @return the instances
    */
   public String getInstances();

   /**
    * @param instances the instances to set
    */
   public void setInstances(String instances);

   /**
    * @return the securityMode
    */
   public String getSecurityMode();

   /**
    * @param securityMode the securityMode to set
    */
   public void setSecurityMode(String securityMode);

   /**
    * @return the maxMemory
    */
   public String getMaxMemory();

   /**
    * @param maxMemory the maxMemory to set
    */
   public void setMaxMemory(String maxMemory);

   /**
    * @return the idleTimeout
    */
   public String getIdleTimeout();

   /**
    * @param idleTimeout the idleTimeout to set
    */
   public void setIdleTimeout(String idleTimeout);

   /**
    * @return the serverPull
    */
   public String getServerPool();

   /**
    * @param serverPool the serverPull to set
    */
   public void setServerPool(String serverPool);

   /**
    * @return the container
    */
   public String getContainer();

   /**
    * @param container the container to set
    */
   public void setContainer(String container);
   
   /**
    * @return size of the cluster
    */
   public String getClusterSize();

   /**
    * @param clusterSize size of the cluster to set
    */
   public void setClusterSize(String clusterSize);
}