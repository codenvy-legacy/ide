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
package org.exoplatform.ide.extension.chromattic.client.model.service;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.extension.chromattic.client.model.EnumAlreadyExistsBehaviour;
import org.exoplatform.ide.extension.chromattic.client.model.EnumNodeTypeFormat;
import org.exoplatform.ide.extension.chromattic.client.model.GenerateNodeTypeResult;

/**
 * Service is used to do actions with chromattic application and data objects.
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public abstract class ChrommaticService
{

   /**
    * Instance of {@link ChrommaticService}
    */
   private static ChrommaticService instance;

   /**
    * @return {@link ChrommaticService}
    */
   public static ChrommaticService getInstance()
   {
      return instance;
   }

   protected ChrommaticService()
   {
      instance = this;
   }

   /**
    * Compile groovy file.
    * @param file
    * @param callback - callback to handler result from server
    */
   public abstract void compile(File file, AsyncRequestCallback<String> callback);
   
   /**
    * Generate the node type XML representation.
    * 
    * @param location location of groovy file with node type definition
    * @param nodeTypeFormat node type format
    * @param callback - callback to handler result from server
    */
   public abstract void generateNodeType(String location, EnumNodeTypeFormat nodeTypeFormat, AsyncRequestCallback<GenerateNodeTypeResult> callback);
   
   /**
    * Create (deploy) new node type.
    * 
    * @param nodeType XML node type definition
    * @param nodeTypeFormat node type format
    * @param alreadyExistsBehaviour the behavior if node type already exists
    * @param callback - callback to handler result from server
    */
   public abstract void createNodeType(String nodeType, EnumNodeTypeFormat nodeTypeFormat, EnumAlreadyExistsBehaviour alreadyExistsBehaviour,
      AsyncRequestCallback<String> callback);

}
