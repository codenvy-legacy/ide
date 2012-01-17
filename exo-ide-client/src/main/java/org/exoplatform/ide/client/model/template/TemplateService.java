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

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

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

   public abstract void getTemplates(AsyncRequestCallback<List<Template>> callback) throws RequestException;

   public abstract void createTemplate(Template template, TemplateCreatedCallback callback) throws RequestException;

   public abstract void deleteTemplate(Template template, TemplateDeletedCallback callback) throws RequestException;

   public abstract void addFileTemplate(FileTemplate template, AsyncRequestCallback<FileTemplate> callback) throws RequestException;

   public abstract void getFileTemplateList(AsyncRequestCallback<List<FileTemplate>> callback) throws RequestException;

   public abstract void deleteFileTemplate(String templateName, AsyncRequestCallback<String> callback) throws RequestException;

   public abstract void getProjectTemplateList(AsyncRequestCallback<List<ProjectTemplate>> callback) throws RequestException;

   public abstract void deleteProjectTemplate(String templateName, AsyncRequestCallback<String> callback) throws RequestException;

   public abstract void addProjectTemplate(ProjectTemplate projectTemplate, AsyncRequestCallback<String> callback) throws RequestException;

   /**
    * Create new project
    * 
    * @param parentId id of the parent folder
    * @param name name of the project
    * @param templateName name of template
    * @param callback
    * @throws RequestException
    */
   public abstract void createProjectFromTemplate(String vfsId, String parentId, String name, String templateName,
      org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<ProjectModel> callback)
      throws RequestException;

   /*
    * Methods, used for templates transfer from registry to settings file.
    */
   public abstract void addFileTemplateList(List<FileTemplate> fileTemplates, AsyncRequestCallback<String> callback) throws RequestException;

   public abstract void addProjectTemplateList(List<ProjectTemplate> projectTemplates,
      AsyncRequestCallback<String> callback) throws RequestException;

   public abstract void deleteTemplatesFromRegistry(AsyncRequestCallback<String> callback) throws RequestException;

}
