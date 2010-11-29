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
package org.exoplatform.ide.client.autocompletion;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.ide.client.framework.codeassistant.api.TokenWidgetFactory;
import org.exoplatform.ide.client.framework.codeassistant.events.RegisterTokenWidgetFactoryEvent;
import org.exoplatform.ide.client.framework.codeassistant.events.RegisterTokenWidgetFactoryHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 26, 2010 3:55:15 PM evgen $
 *
 */
public class TokenFactories<T> implements RegisterTokenWidgetFactoryHandler
{
   
   private Map<String, TokenWidgetFactory<T>> factories = new HashMap<String, TokenWidgetFactory<T>>();
   
   public TokenFactories(HandlerManager eventBus)
   {
      eventBus.addHandler(RegisterTokenWidgetFactoryEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.events.RegisterTokenWidgetFactoryHandler#onRegisterTokenWidgetFactory(org.exoplatform.ide.client.framework.codeassistant.events.RegisterTokenWidgetFactoryEvent)
    */
   @SuppressWarnings("unchecked")
   public void onRegisterTokenWidgetFactory(RegisterTokenWidgetFactoryEvent event)
   {
      factories.put(event.getMimeType(), (TokenWidgetFactory<T>)event.getFactory());      
   }
   
   public TokenWidgetFactory<T> getFactory(String mimeType)
   {
      return factories.get(mimeType);
   }

}
