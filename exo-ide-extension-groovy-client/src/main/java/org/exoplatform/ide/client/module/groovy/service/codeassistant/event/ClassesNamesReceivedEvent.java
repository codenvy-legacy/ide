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
package org.exoplatform.ide.client.module.groovy.service.codeassistant.event;

import java.util.List;

import org.exoplatform.ide.client.framework.codeassistant.TokenExt;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 17, 2010 4:52:59 PM evgen $
 *
 */
public class ClassesNamesReceivedEvent extends GwtEvent<ClassesNamesReceivedHandler>
{

   public static GwtEvent.Type<ClassesNamesReceivedHandler> TYPE = new Type<ClassesNamesReceivedHandler>();

   private List<TokenExt> tokens;

   /**
    * @param tokens
    */
   public ClassesNamesReceivedEvent(List<TokenExt> tokens)
   {
      this.tokens = tokens;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#getAssociatedType()
    */
   @Override
   public com.google.gwt.event.shared.GwtEvent.Type<ClassesNamesReceivedHandler> getAssociatedType()
   {
      return TYPE;
   }

   /**
    * @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler)
    */
   @Override
   protected void dispatch(ClassesNamesReceivedHandler handler)
   {
      handler.onClassesNamesReceived(this);
   }

   /**
    * Classes FQN
    * @return {@link List} of {@link TokenExt}
    */
   public List<TokenExt> getTokens()
   {
      return tokens;
   }
}
