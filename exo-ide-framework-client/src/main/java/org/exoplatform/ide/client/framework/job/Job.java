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
package org.exoplatform.ide.client.framework.job;

/**
 * Simple been to manage Running async REST Services
 * 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Sep 19, 2011 evgen $
 * 
 */
public class Job
{

   public enum JobStatus {
      STARTED, FINISHED, ERROR
   }

   private String id;

   private JobStatus status;

   private String startMessage;

   private String finishMessage;

   private Throwable error;

   public Job(String id, JobStatus status)
   {
      this.id = id;
      this.status = status;
   }

   /**
    * @return the startMessage
    */
   public String getStartMessage()
   {
      return startMessage;
   }

   /**
    * @param startMessage the startMessage to set
    */
   public void setStartMessage(String startMessage)
   {
      this.startMessage = startMessage;
   }

   /**
    * @return the finishMessage
    */
   public String getFinishMessage()
   {
      return finishMessage;
   }

   /**
    * @param finishMessage the finishMessage to set
    */
   public void setFinishMessage(String finishMessage)
   {
      this.finishMessage = finishMessage;
   }

   /**
    * @return the error
    */
   public Throwable getError()
   {
      return error;
   }

   /**
    * @param error the error to set
    */
   public void setError(Throwable error)
   {
      this.error = error;
   }

   /**
    * @return the id
    */
   public String getId()
   {
      return id;
   }

   /**
    * @return the status
    */
   public JobStatus getStatus()
   {
      return status;
   }

}
