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
package com.codenvy.ide.extension.maven.shared;


/**
 * Status of build.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface BuildStatus
{
   public enum Status
   {
      IN_PROGRESS("In progress"), //
      SUCCESSFUL("Successful"), //
      FAILED("Failed"); //

      private final String value;

      private Status(String value)
      {
         this.value = value;
      }

      @Override
      public String toString()
      {
         return value;
      }
   }

   /**
    * Returns the current build job status.
    * 
    * @return current build job status
    */
   Status getStatus();

   int getExitCode();

   /**
    * Returns the specific details about the error.
    * 
    * @return an error message
    */
   String getError();

   /**
    * Returns the URL to download artifact.
    * 
    * @return URL to download artifact
    */
   String getDownloadUrl();

   /**
    * Sets the current build job status.
    * 
    * @param status current build job status
    */
   void setStatus(Status status);

   void setExitCode(int exitCode);

   /**
    * Sets the message that describes the specific details about the error.
    * 
    * @param error an error message
    */
   void setError(String error);

   /**
    * Changes the URL to download artifact
    * 
    * @param downloadUrl URL to download artifact
    */
   void setDownloadUrl(String downloadUrl);

   String getTime();

   void setTime(String time);
   
}
