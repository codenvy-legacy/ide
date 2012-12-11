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
package org.exoplatform.ide.codeassistant.storage;

import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage;
import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * REST interface for {@link CodeAssistantStorage}
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
@Path("storage/get")
public class Storage
{

   @Inject
   private CodeAssistantStorage storage;

   @POST
   @Path("/annotations")
   @Produces(MediaType.APPLICATION_JSON)
   public List<ShortTypeInfo> getAnnotation(@QueryParam("prefix") String prefix, Set<String> dependencys)
   {
      if (dependencys == null)
         return null;
      try
      {
         return storage.getAnnotations(prefix, dependencys);
      }
      catch (CodeAssistantException e)
      {
         return null;
      }
   }

   @POST
   @Path("/classes")
   @Produces(MediaType.APPLICATION_JSON)
   public List<ShortTypeInfo> getClasses(@QueryParam("prefix") String prefix, Set<String> dependencys)
   {
      if (dependencys == null)
         return null;
      try
      {
         return storage.getClasses(prefix, dependencys);
      }
      catch (CodeAssistantException e)
      {
         return null;
      }
   }

   @POST
   @Path("/interfaces")
   @Produces(MediaType.APPLICATION_JSON)
   public List<ShortTypeInfo> getInterfaces(@QueryParam("prefix") String prefix, Set<String> dependencys)
   {
      if (dependencys == null)
         return null;
      try
      {
         return storage.getInterfaces(prefix, dependencys);
      }
      catch (CodeAssistantException e)
      {
         return null;
      }
   }

   @POST
   @Path("/type-by-fqn")
   @Produces(MediaType.APPLICATION_JSON)
   public TypeInfo getTypeByFqn(@QueryParam("fqn") String fqn, Set<String> dependencys)
   {
      if (dependencys == null)
         return null;
      try
      {
         return storage.getTypeByFqn(fqn, dependencys);
      }
      catch (CodeAssistantException e)
      {
         return null;
      }
   }

   @POST
   @Path("/type-by-fqn-prefix")
   @Produces(MediaType.APPLICATION_JSON)
   public List<ShortTypeInfo> getTypeByFqnPrefix(@QueryParam("prefix") String fqnPrefix, Set<String> dependencys)
   {
      if (dependencys == null)
         return null;
      try
      {
         return storage.getTypesByFqnPrefix(fqnPrefix, dependencys);
      }
      catch (CodeAssistantException e)
      {
         return null;
      }
   }

   @POST
   @Path("/type-by-name-prefix")
   @Produces(MediaType.APPLICATION_JSON)
   public List<ShortTypeInfo> getTypeByNamePrefix(@QueryParam("prefix") String namePrefix, Set<String> dependencys)
   {
      if (dependencys == null)
         return null;
      try
      {
         return storage.getTypesByNamePrefix(namePrefix, dependencys);
      }
      catch (CodeAssistantException e)
      {
         return null;
      }
   }

   @POST
   @Path("/types-info-by-name-prefix")
   @Produces(MediaType.APPLICATION_JSON)
   public List<TypeInfo> getTypesInfoByNamePrefix(@QueryParam("prefix") String namePrefix, Set<String> dependencys)
   {
      if (dependencys == null)
         return null;
      try
      {
         return storage.getTypesInfoByNamePrefix(namePrefix, dependencys);
      }
      catch (CodeAssistantException e)
      {
         return null;
      }
   }

   @POST
   @Path("/class-doc")
   @Produces(MediaType.TEXT_PLAIN)
   public String getClassDoc(@QueryParam("fqn") String fqn, Set<String> dependencys)
   {
      if (dependencys == null)
         return null;
      try
      {
         return storage.getClassJavaDoc(fqn, dependencys);
      }
      catch (CodeAssistantException e)
      {
         return null;
      }
   }

   @POST
   @Path("/member-doc")
   @Produces(MediaType.TEXT_PLAIN)
   public String getMemeberDoc(@QueryParam("fqn") String fqn, Set<String> dependencys)
   {
      if (dependencys == null)
         return null;
      try
      {
         return storage.getMemberJavaDoc(fqn, dependencys);
      }
      catch (CodeAssistantException e)
      {
         return null;
      }
   }

   /**
    * Get list of package names
    */
   @POST
   @Path("/find-packages")
   @Produces(MediaType.APPLICATION_JSON)
   public List<String> getPackages(@QueryParam("package") String packagePrefix, Set<String> dependencys)
   {
      if (dependencys == null)
         return null;
      try
      {
         return storage.getPackages(packagePrefix, dependencys);
      }
      catch (CodeAssistantException e)
      {
         return null;
      }
   }
   
   /**
    * Get list of all package names in dependencys 
    */
   @POST
   @Path("/get-packages")
   @Produces(MediaType.APPLICATION_JSON)
   public List<String> getAllPackages(Set<String> dependencys)
   {
      if (dependencys == null)
         return null;
      try
      {
         return storage.getAllPackages(dependencys);
      }
      catch (CodeAssistantException e)
      {
         return null;
      }
   }

}
