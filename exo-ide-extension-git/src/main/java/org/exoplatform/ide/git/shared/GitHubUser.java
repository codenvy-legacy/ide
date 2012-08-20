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
package org.exoplatform.ide.git.shared;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: GitHubUser.java Aug 6, 2012
 */
public interface GitHubUser
{

   /**
    * @return the type
    */
   public abstract String getType();

   /**
    * @param type the type to set
    */
   public abstract void setType(String type);

   /**
    * @return the email
    */
   public abstract String getEmail();

   /**
    * @param email the email to set
    */
   public abstract void setEmail(String email);

   /**
    * @return the company
    */
   public abstract String getCompany();

   /**
    * @param company the company to set
    */
   public abstract void setCompany(String company);

   /**
    * @return the followers
    */
   public abstract int getFollowers();

   /**
    * @param followers the followers to set
    */
   public abstract void setFollowers(int followers);

   /**
    * @return the avatar_url
    */
   public abstract String getAvatarUrl();

   /**
    * @param avatarUrl the avatar_url to set
    */
   public abstract void setAvatarUrl(String avatarUrl);

   /**
    * @return the html_url
    */
   public abstract String getHtmlUrl();

   /**
    * @param htmlUrl the html_url to set
    */
   public abstract void setHtmlUrl(String htmlUrl);

   /**
    * @return the bio
    */
   public abstract String getBio();

   /**
    * @param bio the bio to set
    */
   public abstract void setBio(String bio);

   /**
    * @return the public_repos
    */
   public abstract int getPublicRepos();

   /**
    * @param publicRepos the public_repos to set
    */
   public abstract void setPublicRepos(int publicRepos);

   /**
    * @return the public_gists
    */
   public abstract int getPublicGists();

   /**
    * @param public_gists the public_gists to set
    */
   public abstract void setPublicGists(int publicGists);

   /**
    * @return the following
    */
   public abstract int getFollowing();

   /**
    * @param following the following to set
    */
   public abstract void setFollowing(int following);

   /**
    * @return the location
    */
   public abstract String getLocation();

   /**
    * @param location the location to set
    */
   public abstract void setLocation(String location);

   /**
    * @return the name
    */
   public abstract String getName();

   /**
    * @param name the name to set
    */
   public abstract void setName(String name);

   /**
    * @return the url
    */
   public abstract String getUrl();

   /**
    * @param url the url to set
    */
   public abstract void setUrl(String url);

   /**
    * @return the gravatar_id
    */
   public abstract String getGravatarId();

   /**
    * @param gravatarId the gravatar_id to set
    */
   public abstract void setGravatarId(String gravatarId);

   /**
    * @return the id
    */
   public abstract String getId();

   /**
    * @param id the id to set
    */
   public abstract void setId(String id);

   /**
    * @return the login
    */
   public abstract String getLogin();

   /**
    * @param login the login to set
    */
   public abstract void setLogin(String login);

}