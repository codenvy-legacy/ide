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
package org.exoplatform.ide.client.project;

import org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable;
import org.exoplatform.gwtframework.commons.rest.copy.UnmarshallerException;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class ProjectUnmarshaller implements Unmarshallable<ProjectModel>
{
   
   private final ProjectModel projectModel;
   
   public ProjectUnmarshaller(ProjectModel model)
   {
      projectModel = model;
   }

   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      System.out.println("ProjectUnmarshaller.unmarshal()" + response.getText());
      projectModel.init(JSONParser.parseLenient(response.getText()).isObject());
   }

   @Override
   public ProjectModel getPayload()
   {
      return projectModel;
   }

}
