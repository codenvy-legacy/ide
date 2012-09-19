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
package org.exoplatform.ide.extension.aws.server.ec2;

import com.amazonaws.services.ec2.model.Tag;

import org.exoplatform.ide.extension.aws.shared.ec2.ImageInfo;
import org.exoplatform.ide.extension.aws.shared.ec2.ImageState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ImageInfoImpl implements ImageInfo
{
   private String amiId;
   private String manifest;
   private ImageState state;
   private String ownerId;
   private String ownerAlias;
   private Map<String, String> tags;

   public static class Builder
   {
      private String amiId;
      private String manifest;
      private ImageState state;
      private String ownerId;
      private String ownerAlias;
      private Map<String, String> tags;

      public Builder amiId(String amiId)
      {
         this.amiId = amiId;
         return this;
      }

      public Builder manifest(String manifest)
      {
         this.manifest = manifest;
         return this;
      }

      public Builder state(String state)
      {
         this.state = ImageState.fromValue(state);
         return this;
      }

      public Builder ownerId(String ownerId)
      {
         this.ownerId = ownerId;
         return this;
      }

      public Builder ownerAlias(String ownerAlias)
      {
         this.ownerAlias = ownerAlias;
         return this;
      }

      public Builder tags(List<Tag> tags)
      {
         if (tags == null)
         {
            this.tags = null;
            return this;
         }
         this.tags = new HashMap<String, String>(tags.size());
         for (Tag tag : tags)
         {
            this.tags.put(tag.getKey(), tag.getValue());
         }
         return this;
      }

      public  ImageInfo build()
      {
         return new ImageInfoImpl(this);
      }
   }

   private ImageInfoImpl(Builder builder)
   {
      this.amiId = builder.amiId;
      this.manifest = builder.manifest;
      this.state = builder.state;
      this.ownerId = builder.ownerId;
      this.ownerAlias = builder.ownerAlias;
      this.tags = builder.tags;
   }

   public ImageInfoImpl()
   {
   }

   @Override
   public String getAmiId()
   {
      return amiId;
   }

   @Override
   public void setAmiId(String amiId)
   {
      this.amiId = amiId;
   }

   @Override
   public String getManifest()
   {
      return manifest;
   }

   @Override
   public void setManifest(String manifest)
   {
      this.manifest = manifest;
   }

   @Override
   public ImageState getState()
   {
      return state;
   }

   @Override
   public void setState(ImageState state)
   {
      this.state = state;
   }

   @Override
   public String getOwnerId()
   {
      return ownerId;
   }

   @Override
   public void setOwnerId(String ownerId)
   {
      this.ownerId = ownerId;
   }

   @Override
   public String getOwnerAlias()
   {
      return ownerAlias;
   }

   @Override
   public void setOwnerAlias(String ownerAlias)
   {
      this.ownerAlias = ownerAlias;
   }

   @Override
   public Map<String, String> getTags()
   {
      return tags;
   }

   @Override
   public void setTags(Map<String, String> tags)
   {
      this.tags = tags;
   }

   @Override
   public String toString()
   {
      return "ImageInfoImpl{" +
         "amiId='" + amiId + '\'' +
         ", manifest='" + manifest + '\'' +
         ", state=" + state +
         ", ownerId='" + ownerId + '\'' +
         ", ownerAlias='" + ownerAlias + '\'' +
         ", tags=" + tags +
         '}';
   }
}
