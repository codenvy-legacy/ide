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
package org.exoplatform.ide.extension.jenkins.client.marshal;

import com.google.gwt.json.client.JSONValue;

import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable;
import org.exoplatform.gwtframework.commons.rest.copy.UnmarshallerException;
import org.exoplatform.ide.extension.jenkins.shared.JobStatus;
import org.exoplatform.ide.extension.jenkins.shared.JobStatus.Status;

/**
 * Unmarshaller for Jenkins ob status request
 * 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 * 
 */
public class JenkinsJobStatusUnmarshaller implements Unmarshallable<JobStatus>
{

   private JobStatus jobStatus;

   /**
    * @param jobStatus
    */
   public JenkinsJobStatusUnmarshaller(JobStatus jobStatus)
   {
      super();
      this.jobStatus = jobStatus;
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         if (response.getText() == null || response.getText().isEmpty())
         {
            return;
         }

         JSONObject object = JSONParser.parseLenient(response.getText()).isObject();
         if (object == null)
         {
            return;
         }

         JSONValue lastBuild = object.get("lastBuildResult");
         if (lastBuild.isString() != null)
         {
            jobStatus.setLastBuildResult(lastBuild.isString().stringValue());
         }

         JSONValue artifactUrl = object.get("artifactUrl");
         if (artifactUrl.isString() != null)
         {
            jobStatus.setArtifactUrl(artifactUrl.isString().stringValue());
         }

         jobStatus.setName(object.get("name").isString().stringValue());
         jobStatus.setStatus(Status.valueOf(object.get("status").isString().stringValue()));

      }
      catch (Exception e)
      {
         throw new UnmarshallerException("Can't parse Jenkins job status");
      }
   }

   /**
    * @see org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable#getPayload()
    */
   @Override
   public JobStatus getPayload()
   {
      return jobStatus;
   }

}
