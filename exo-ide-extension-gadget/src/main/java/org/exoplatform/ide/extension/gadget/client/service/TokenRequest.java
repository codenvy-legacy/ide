/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.extension.gadget.client.service;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class TokenRequest
{

   /**
    * 
    */
   public static final String GADGET_URL = "gadgetURL";

   /**
    * 
    */
   public static final String OWNER = "owner";

   /**
    * 
    */
   public static final String VIEWER = "viewer";

   /**
    * 
    */
   public static final String MODULE_ID = "moduleId";

   /**
    * 
    */
   public static final String CONTAINER = "container";

   /**
    * 
    */
   public static final String DOMAIN = "domain";

   /**
    * 
    */
   private String gadgetURL;

   /**
    * 
    */
   private String owner;

   /**
    * 
    */
   private String viewer;

   /**
    * 
    */
   private Long moduleId;

   /**
    * 
    */
   private String container;

   /**
    * 
    */
   private String domain;

   /**
    * 
    */
   public TokenRequest()
   {
   }

   /**
    * @param gadgetURL
    * @param owner
    * @param viewer
    * @param moduleId
    * @param container
    * @param domain
    */
   public TokenRequest(String gadgetURL, String owner, String viewer, Long moduleId, String container, String domain)
   {
      this.gadgetURL = gadgetURL;
      this.owner = owner;
      this.viewer = viewer;
      this.moduleId = moduleId;
      this.container = container;
      this.domain = domain;
   }

   /**
    * @return the gadgetURL
    */
   public String getGadgetURL()
   {
      return gadgetURL;
   }

   /**
    * @param gadgetURL the gadgetURL to set
    */
   public void setGadgetURL(String gadgetURL)
   {
      this.gadgetURL = gadgetURL;
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
    * @return the viewer
    */
   public String getViewer()
   {
      return viewer;
   }

   /**
    * @param viewer the viewer to set
    */
   public void setViewer(String viewer)
   {
      this.viewer = viewer;
   }

   /**
    * @return the moduleId
    */
   public Long getModuleId()
   {
      return moduleId;
   }

   /**
    * @param moduleId the moduleId to set
    */
   public void setModuleId(Long moduleId)
   {
      this.moduleId = moduleId;
   }

   /**
    * @return the container
    */
   public String getContainer()
   {
      return container;
   }

   /**
    * @param container the container to set
    */
   public void setContainer(String container)
   {
      this.container = container;
   }

   /**
    * @return the domain
    */
   public String getDomain()
   {
      return domain;
   }

   /**
    * @param domain the domain to set
    */
   public void setDomain(String domain)
   {
      this.domain = domain;
   }

   @Override
   public String toString()
   {
      String json =
         "{\"" + GADGET_URL + "\":\"" + gadgetURL + "\",\"" + OWNER + "\":\"" + owner + "\",\"" + VIEWER + "\":\""
            + viewer + "\",\"" + MODULE_ID + "\":" + moduleId + ",\"" + CONTAINER + "\":\"" + container + "\",\""
            + DOMAIN + "\":\"" + domain + "\"}";
      return json;

   }

}
