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
package org.exoplatform.ide.extension.heroku.shared;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class HerokuApplicationInfo
{
   private String name;
   private String webUrl;
   private String domainName;
   private String gitUrl;
   private int dynos;
   private int workers;
   private Integer repoSize;
   private Integer slugSize;
   private String stack;
   private String owner;
   private String databaseSize;

   public HerokuApplicationInfo()
   {
   }

   public HerokuApplicationInfo(String name, String webUrl, String domainName, String gitUrl, int dynos, int workers,
      Integer repoSize, Integer slugSize, String stack, String owner, String databaseSize)
   {
      this.name = name;
      this.webUrl = webUrl;
      this.domainName = domainName;
      this.gitUrl = gitUrl;
      this.dynos = dynos;
      this.workers = workers;
      this.repoSize = repoSize;
      this.slugSize = slugSize;
      this.stack = stack;
      this.owner = owner;
      this.databaseSize = databaseSize;
   }

   public String getDatabaseSize()
   {
      return databaseSize;
   }

   public String getDomainName()
   {
      return domainName;
   }

   public int getDynos()
   {
      return dynos;
   }

   public String getGitUrl()
   {
      return gitUrl;
   }

   public String getName()
   {
      return name;
   }

   public String getOwner()
   {
      return owner;
   }

   public Integer getRepoSize()
   {
      return repoSize;
   }

   public Integer getSlugSize()
   {
      return slugSize;
   }

   public String getStack()
   {
      return stack;
   }

   public String getWebUrl()
   {
      return webUrl;
   }

   public int getWorkers()
   {
      return workers;
   }

   public void setDatabaseSize(String databaseSize)
   {
      this.databaseSize = databaseSize;
   }

   public void setDomainName(String domainName)
   {
      this.domainName = domainName;
   }

   public void setDynos(int dynos)
   {
      this.dynos = dynos;
   }

   public void setGitUrl(String gitUrl)
   {
      this.gitUrl = gitUrl;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public void setOwner(String owner)
   {
      this.owner = owner;
   }

   public void setRepoSize(Integer repoSize)
   {
      this.repoSize = repoSize;
   }

   public void setSlugSize(Integer slugSize)
   {
      this.slugSize = slugSize;
   }

   public void setStack(String stack)
   {
      this.stack = stack;
   }

   public void setWebUrl(String webUrl)
   {
      this.webUrl = webUrl;
   }

   public void setWorkers(int workers)
   {
      this.workers = workers;
   }

   @Override
   public String toString()
   {
      return "HerokuApplicationInfo [name=" + name + ", webUrl=" + webUrl + ", domainName=" + domainName + ", gitUrl="
         + gitUrl + ", dynos=" + dynos + ", workers=" + workers + ", repoSize=" + repoSize + ", slugSize=" + slugSize
         + ", stack=" + stack + ", owner=" + owner + ", databaseSize=" + databaseSize + "]";
   }
}
