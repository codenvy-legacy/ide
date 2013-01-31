/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.paas;

import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.ide.json.JsonArray;

/**
 * Aggregate information about registered PaaS.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class PaaS
{
   /**
    * Id of the PaaS.
    */
   private String id;

   /**
    * Title of the PaaS.
    */
   private String title;

   /**
    * PaaS image.
    */
   private ImageResource image;

   /**
    * PaaS provides application's template or not.
    */
   private boolean providesTemplate;

   /**
    * List of project types, supported by the PaaS (can be deployed).
    */
   private JsonArray<String> supportedProjectTypes;

   public PaaS(String id, String title, ImageResource image, boolean providesTemplate,
      JsonArray<String> supportedProjectTypes)
   {
      this.id = id;
      this.title = title;
      this.image = image;
      this.providesTemplate = providesTemplate;
      this.supportedProjectTypes = supportedProjectTypes;
   }

   /**
    * @return {@link String} PaaS id
    */
   public String getId()
   {
      return id;
   }

   /**
    * @return the title
    */
   public String getTitle()
   {
      return title;
   }

   /**
    * @return the image
    */
   public ImageResource getImage()
   {
      return image;
   }

   /**
    * @return the providesTemplate
    */
   public boolean isProvidesTemplate()
   {
      return providesTemplate;
   }

   /**
    * @return the supportedProjectTypes
    */
   public JsonArray<String> getSupportedProjectTypes()
   {
      return supportedProjectTypes;
   }
}