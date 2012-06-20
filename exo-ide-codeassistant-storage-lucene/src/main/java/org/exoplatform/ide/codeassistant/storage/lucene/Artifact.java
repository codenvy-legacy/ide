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
package org.exoplatform.ide.codeassistant.storage.lucene;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class Artifact
{

   private String path;

   private String artifactID;

   private String groupID;

   private String version;

   private String type;

   /**
    * 
    */
   public Artifact()
   {
   }

   /**
    * @return the path
    */
   public String getPath()
   {
      return path;
   }

   /**
    * @param path the path to set
    */
   public void setPath(String path)
   {
      this.path = path;
   }

   /**
    * @return the artifactID
    */
   public String getArtifactID()
   {
      return artifactID;
   }

   /**
    * @param artifactID the artifactID to set
    */
   public void setArtifactID(String artifactID)
   {
      this.artifactID = artifactID;
   }

   /**
    * @return the groupID
    */
   public String getGroupID()
   {
      return groupID;
   }

   /**
    * @param groupID the groupID to set
    */
   public void setGroupID(String groupID)
   {
      this.groupID = groupID;
   }

   /**
    * @return the version
    */
   public String getVersion()
   {
      return version;
   }

   /**
    * @param version the version to set
    */
   public void setVersion(String version)
   {
      this.version = version;
   }

   /**
    * @return the type
    */
   public String getType()
   {
      return type;
   }

   /**
    * @param type the type to set
    */
   public void setType(String type)
   {
      this.type = type;
   }

   public String getArtifactString()
   {
      return groupID + ":" + artifactID + ":" + version + ":" + type;
   }

}
