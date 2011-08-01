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
package org.exoplatform.ide.client.model.template;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;

import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public abstract class TemplateService
{

   private static TemplateService instance;

   public static TemplateService getInstance()
   {
      return instance;
   }

   protected TemplateService()
   {
      instance = this;
   }

   public abstract void getTemplates(AsyncRequestCallback<TemplateList> callback);

   public abstract void createTemplate(Template template, TemplateCreatedCallback callback);

   public abstract void deleteTemplate(Template template, TemplateDeletedCallback callback);
   
   public abstract void addFileTemplate(FileTemplate template, AsyncRequestCallback<FileTemplate> callback);
   
   public abstract void getFileTemplateList(AsyncRequestCallback<FileTemplateList> callback);
   
   public abstract void deleteFileTemplate(String templateName, AsyncRequestCallback<String> callback);
   
   public abstract void getProjectTemplateList(AsyncRequestCallback<ProjectTemplateList> callback);
   
   public abstract void deleteProjectTemplate(String templateName, AsyncRequestCallback<String> callback);
   
   public abstract void addProjectTemplate(ProjectTemplate projectTemplate, AsyncRequestCallback<String> callback);
   
   /*
    * Methods, used for templates transfer from registry to settings file.
    */
   public abstract void addFileTemplateList(List<FileTemplate> fileTemplates, AsyncRequestCallback<String> callback);
   
   public abstract void addProjectTemplateList(List<ProjectTemplate> projectTemplates, AsyncRequestCallback<String> callback);
   
   public abstract void deleteTemplatesFromRegistry(AsyncRequestCallback<String> callback);

}
