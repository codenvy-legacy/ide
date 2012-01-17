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
package org.exoplatform.ide.extension.chromattic.client.model.service.event;

import org.exoplatform.gwtframework.commons.exception.ServerExceptionEvent;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event is fired, when the result of node type gereration is received from server.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 6, 2010 $
 * 
 */
public class NodeTypeGenerationResultReceivedEvent extends
   ServerExceptionEvent<NodeTypeGenerationResultReceivedHandler>
{

   /**
    * Type used to register this event.
    */
   public static final GwtEvent.Type<NodeTypeGenerationResultReceivedHandler> TYPE =
      new GwtEvent.Type<NodeTypeGenerationResultReceivedHandler>();

   /**
    * Error while generating type.
    */
   private Throwable exception;

   /**
    * The generated node type result.
    */
   private StringBuilder generateNodeTypeResult;

   /**
    * @param generateNodeTypeResult
    */
   public NodeTypeGenerationResultReceivedEvent(StringBuilder generateNodeTypeResult)
   {
      this.generateNodeTypeResult = generateNodeTypeResult;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<NodeTypeGenerationResultReceivedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(NodeTypeGenerationResultReceivedHandler handler)
   {
      handler.onNodeTypeGenerationResultReceived(this);
   }

   /**
    * @see org.exoplatform.gwtframework.commons.exception.ServerExceptionEvent#setException(java.lang.Throwable)
    */
   @Override
   public void setException(Throwable exception)
   {
      this.exception = exception;
   }

   /**
    * @return the exception
    */
   public Throwable getException()
   {
      return exception;
   }

   /**
    * @return the generateNodeTypeResult
    */
   public StringBuilder getGenerateNodeTypeResult()
   {
      return generateNodeTypeResult;
   }
}
