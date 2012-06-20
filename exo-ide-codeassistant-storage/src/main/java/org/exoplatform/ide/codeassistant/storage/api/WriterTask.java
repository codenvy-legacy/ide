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
package org.exoplatform.ide.codeassistant.storage.api;

import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class WriterTask
{

   private String artifact;

   private List<TypeInfo> typesInfo;

   private Set<String> packages;

   private Map<String, String> javaDock;

   /**
    * 
    */
   public WriterTask(String artifact, List<TypeInfo> typesInfo, Set<String> packages)
   {
      this(artifact, typesInfo, packages, null);
   }

   /**
    * 
    */
   public WriterTask(String artifact, Map<String, String> javaDock)
   {
      this(artifact, null, null, javaDock);
   }

   /**
    * @param artifact
    * @param typesInfo
    * @param packages
    * @param javaDock
    */
   public WriterTask(String artifact, List<TypeInfo> typesInfo, Set<String> packages, Map<String, String> javaDock)
   {
      super();
      this.artifact = artifact;
      this.typesInfo = typesInfo;
      this.packages = packages;
      this.javaDock = javaDock;
   }

   /**
    * @return the artifact
    */
   public String getArtifact()
   {
      return artifact;
   }

   /**
    * @param artifact the artifact to set
    */
   public void setArtifact(String artifact)
   {
      this.artifact = artifact;
   }

   /**
    * @return the typesInfo
    */
   public List<TypeInfo> getTypesInfo()
   {
      return typesInfo;
   }

   /**
    * @param typesInfo the typesInfo to set
    */
   public void setTypesInfo(List<TypeInfo> typesInfo)
   {
      this.typesInfo = typesInfo;
   }

   /**
    * @return the packages
    */
   public Set<String> getPackages()
   {
      return packages;
   }

   /**
    * @param packages the packages to set
    */
   public void setPackages(Set<String> packages)
   {
      this.packages = packages;
   }

   /**
    * @return the javaDock
    */
   public Map<String, String> getJavaDock()
   {
      return javaDock;
   }

   /**
    * @param javaDock the javaDock to set
    */
   public void setJavaDock(Map<String, String> javaDock)
   {
      this.javaDock = javaDock;
   }

}
