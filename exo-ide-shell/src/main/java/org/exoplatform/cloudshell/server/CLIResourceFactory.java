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
package org.exoplatform.cloudshell.server;

import org.exoplatform.cloudshell.shared.CLIResource;
import org.exoplatform.cloudshell.shared.CLIResourceParameter;
import org.exoplatform.services.rest.ComponentLifecycleScope;
import org.exoplatform.services.rest.impl.resource.AbstractResourceDescriptorImpl;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;
import org.exoplatform.services.rest.resource.ResourceMethodDescriptor;
import org.exoplatform.services.rest.resource.ResourceMethodMap;
import org.exoplatform.services.rest.resource.SubResourceLocatorDescriptor;
import org.exoplatform.services.rest.resource.SubResourceLocatorMap;
import org.exoplatform.services.rest.resource.SubResourceMethodDescriptor;
import org.exoplatform.services.rest.resource.SubResourceMethodMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

public class CLIResourceFactory
{
   private final TransformRules transformRules;

   public CLIResourceFactory()
   {
      this.transformRules = TransformRules.getInstance();
   }

   public Map<String, CLIResource> getCLIResources(AbstractResourceDescriptor resource)
   {
      String rootPath = resource.getPathValue().getPath();
      Map<String, CLIResource> map = new HashMap<String, CLIResource>();
      processResource(rootPath, resource, map);
      return map;
   }

   private void processResource(String rootPath, AbstractResourceDescriptor resource, Map<String, CLIResource> toAdd)
   {
      processResourceMethods(rootPath, resource.getResourceMethods(), toAdd);
      processSubResourceMethods(rootPath, resource.getSubResourceMethods(), toAdd);
      processSubResourceLocators(rootPath, resource.getSubResourceLocators(), toAdd);
   }

   private void processResourceMethods(String rootPath, ResourceMethodMap<ResourceMethodDescriptor> resourceMethods,
      Map<String, CLIResource> toAdd)
   {
      for (List<ResourceMethodDescriptor> l : resourceMethods.values())
      {
         for (ResourceMethodDescriptor resourceMethod : l)
         {
            String command = transformRules.getCommand(rootPath, resourceMethod.getHttpMethod());
            if (command != null)
            {
               toAdd.put(command, fromResourceMethod(command, rootPath, resourceMethod));
            }
         }
      }
   }

   private void processSubResourceMethods(String rootPath, SubResourceMethodMap subResourceMethods,
      Map<String, CLIResource> toAdd)
   {
      for (ResourceMethodMap<SubResourceMethodDescriptor> resourceMethods : subResourceMethods.values())
      {
         for (List<SubResourceMethodDescriptor> l : resourceMethods.values())
         {
            for (SubResourceMethodDescriptor subResourceMethod : l)
            {
               String methodPath = subResourceMethod.getPathValue().getPath();
               String subResourcePath = rootPath + ((methodPath.startsWith("/") && rootPath.endsWith("/")) //
                  ? methodPath.substring(0) //
                  : (methodPath.startsWith("/") || rootPath.endsWith("/")) //
                     ? methodPath //
                     : "/" + methodPath);
               String command = transformRules.getCommand(subResourcePath, subResourceMethod.getHttpMethod());
               if (command != null)
               {
                  toAdd.put(command, fromResourceMethod(command, subResourcePath, subResourceMethod));
               }
            }
         }
      }
   }

   private void processSubResourceLocators(String rootPath, SubResourceLocatorMap subResourceLocators,
      Map<String, CLIResource> toAdd)
   {
      for (SubResourceLocatorDescriptor subResourceLocator : subResourceLocators.values())
      {
         String methodPath = subResourceLocator.getPathValue().getPath();
         String subResourcePath = rootPath + ((methodPath.startsWith("/") && rootPath.endsWith("/")) //
            ? methodPath.substring(0) //
            : (methodPath.startsWith("/") || rootPath.endsWith("/")) //
               ? methodPath //
               : "/" + methodPath);
         AbstractResourceDescriptor subResourceDescriptor =
            new AbstractResourceDescriptorImpl(subResourceLocator.getMethod().getReturnType(),
               ComponentLifecycleScope.SINGLETON);
         processResource(subResourcePath, subResourceDescriptor, toAdd);
      }
   }

   private CLIResource fromResourceMethod(String command, String path, ResourceMethodDescriptor resourceMethod)
   {
      String httpMethod = resourceMethod.getHttpMethod();
      List<MediaType> consumes = resourceMethod.consumes();
      List<MediaType> produces = resourceMethod.produces();
      Map<String, CLIResourceParameter> cliParams = transformRules.getParameters(command, resourceMethod);
      CLIResource cli = new CLIResource(path, //
         httpMethod, //
         new HashSet<MediaType>(consumes), //
         new HashSet<MediaType>(produces), //
         cliParams);
      return cli;
   }
}
