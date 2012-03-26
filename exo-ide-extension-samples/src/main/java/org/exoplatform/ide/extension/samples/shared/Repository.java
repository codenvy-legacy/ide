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
package org.exoplatform.ide.extension.samples.shared;

import java.util.Date;

/**
 * Interface describe GitHub repository.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: Repository.java Mar 25, 2012 11:25:43 PM azatsarynnyy $
 *
 */
public interface Repository
{

   /**
    * @return the description
    */
   public String getDescription();

   /**
    * @param description the description to set
    */
   public void setDescription(String description);

   /**
    * @return the isPrivate
    */
   public boolean isPrivate();

   /**
    * @param isPrivate the isPrivate to set
    */
   public void setPrivate(boolean isPrivate);

   /**
    * @return the url
    */
   public String getUrl();

   /**
    * @param url the url to set
    */
   public void setUrl(String url);

   /**
    * @return the owner
    */
   public String getOwner();

   /**
    * @param owner the owner to set
    */
   public void setOwner(String owner);

   /**
    * @return the homepage
    */
   public String getHomepage();

   /**
    * @param homepage the homepage to set
    */
   public void setHomepage(String homepage);

   /**
    * @return the hasWiki
    */
   public boolean isHasWiki();

   /**
    * @param hasWiki the hasWiki to set
    */
   public void setHasWiki(boolean hasWiki);

   /**
    * @return the openIssues
    */
   public int getOpenIssues();

   /**
    * @param openIssues the openIssues to set
    */
   public void setOpenIssues(int openIssues);

   /**
    * @return the hasIssues
    */
   public boolean isHasIssues();

   /**
    * @param hasIssues the hasIssues to set
    */
   public void setHasIssues(boolean hasIssues);

   /**
    * @return the pushed
    */
   public Date getPushed();

   /**
    * @param pushed the pushed to set
    */
   public void setPushed(Date pushed);

   /**
    * @return the created
    */
   public Date getCreated();

   /**
    * @param created the created to set
    */
   public void setCreated(Date created);

   /**
    * @return the watchers
    */
   public int getWatchers();

   /**
    * @param watchers the watchers to set
    */
   public void setWatchers(int watchers);

   /**
    * @return the forks
    */
   public int getForks();

   /**
    * @param forks the forks to set
    */
   public void setForks(int forks);

   /**
    * @return the fork
    */
   public boolean isFork();

   /**
    * @param fork the fork to set
    */
   public void setFork(boolean fork);

   /**
    * @return the size
    */
   public int getSize();

   /**
    * @param size the size to set
    */
   public void setSize(int size);

   /**
    * @return the name
    */
   public String getName();

   /**
    * @param name the name to set
    */
   public void setName(String name);

   /**
    * @return the hasDownloads
    */
   public boolean isHasDownloads();

   /**
    * @param hasDownloads the hasDownloads to set
    */
   public void setHasDownloads(boolean hasDownloads);

}