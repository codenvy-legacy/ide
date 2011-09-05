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
package org.exoplatform.ide.extension.samples.shared;

import java.util.Date;

/**
 * Stores data about github repository.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Repository.java Aug 29, 2011 10:52:37 AM vereshchaka $
 */
public class Repository
{
   
   private String name;
   
   private String description;
   
   private boolean isPrivate;
   
   private String url;
   
   private String owner;
   
   private String homepage;
   
   private boolean hasWiki;
   
   private int openIssues;
   
   private boolean hasIssues;
   
   private Date pushed;
   
   private Date created;
   
   private int watchers;
   
   private int forks;
   
   private boolean fork;
   
   private int size;
   
   private boolean hasDownloads;
   
   public Repository()
   {
   }
   
   public Repository(String name, String description, String url)
   {
      this.name = name;
      this.description = description;
      this.url = url;
   }

   /**
    * @return the description
    */
   public String getDescription()
   {
      return description;
   }

   /**
    * @param description the description to set
    */
   public void setDescription(String description)
   {
      this.description = description;
   }

   /**
    * @return the isPrivate
    */
   public boolean isPrivate()
   {
      return isPrivate;
   }

   /**
    * @param isPrivate the isPrivate to set
    */
   public void setPrivate(boolean isPrivate)
   {
      this.isPrivate = isPrivate;
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
    * @return the owner
    */
   public String getOwner()
   {
      return owner;
   }

   /**
    * @param owner the owner to set
    */
   public void setOwner(String owner)
   {
      this.owner = owner;
   }

   /**
    * @return the homepage
    */
   public String getHomepage()
   {
      return homepage;
   }

   /**
    * @param homepage the homepage to set
    */
   public void setHomepage(String homepage)
   {
      this.homepage = homepage;
   }

   /**
    * @return the hasWiki
    */
   public boolean isHasWiki()
   {
      return hasWiki;
   }

   /**
    * @param hasWiki the hasWiki to set
    */
   public void setHasWiki(boolean hasWiki)
   {
      this.hasWiki = hasWiki;
   }

   /**
    * @return the openIssues
    */
   public int getOpenIssues()
   {
      return openIssues;
   }

   /**
    * @param openIssues the openIssues to set
    */
   public void setOpenIssues(int openIssues)
   {
      this.openIssues = openIssues;
   }

   /**
    * @return the hasIssues
    */
   public boolean isHasIssues()
   {
      return hasIssues;
   }

   /**
    * @param hasIssues the hasIssues to set
    */
   public void setHasIssues(boolean hasIssues)
   {
      this.hasIssues = hasIssues;
   }

   /**
    * @return the pushed
    */
   public Date getPushed()
   {
      return pushed;
   }

   /**
    * @param pushed the pushed to set
    */
   public void setPushed(Date pushed)
   {
      this.pushed = pushed;
   }

   /**
    * @return the created
    */
   public Date getCreated()
   {
      return created;
   }

   /**
    * @param created the created to set
    */
   public void setCreated(Date created)
   {
      this.created = created;
   }

   /**
    * @return the watchers
    */
   public int getWatchers()
   {
      return watchers;
   }

   /**
    * @param watchers the watchers to set
    */
   public void setWatchers(int watchers)
   {
      this.watchers = watchers;
   }

   /**
    * @return the forks
    */
   public int getForks()
   {
      return forks;
   }

   /**
    * @param forks the forks to set
    */
   public void setForks(int forks)
   {
      this.forks = forks;
   }

   /**
    * @return the fork
    */
   public boolean isFork()
   {
      return fork;
   }

   /**
    * @param fork the fork to set
    */
   public void setFork(boolean fork)
   {
      this.fork = fork;
   }

   /**
    * @return the size
    */
   public int getSize()
   {
      return size;
   }

   /**
    * @param size the size to set
    */
   public void setSize(int size)
   {
      this.size = size;
   }

   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return the hasDownloads
    */
   public boolean isHasDownloads()
   {
      return hasDownloads;
   }

   /**
    * @param hasDownloads the hasDownloads to set
    */
   public void setHasDownloads(boolean hasDownloads)
   {
      this.hasDownloads = hasDownloads;
   }

}
