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

import org.exoplatform.ide.client.framework.codeassistant.TokenCollector;
import org.exoplatform.ide.client.framework.codeassistant.api.TokenCollectorExt;
import org.exoplatform.ide.client.framework.codeassistant.events.RegisterTokenCollectorEvent;
import org.exoplatform.ide.client.framework.codeassistant.events.RegisterTokenCollectorHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 26, 2010 12:19:16 PM evgen $
 *
 */
public class TokenExtCollectors implements RegisterTokenCollectorHandler
{

   private Map<String, TokenCollectorExt> collectors = new HashMap<String, TokenCollectorExt>();

   public TokenExtCollectors(HandlerManager eventBus)
   {
      eventBus.addHandler(RegisterTokenCollectorEvent.TYPE, this);

   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.events.RegisterTokenCollectorHandler#onRegisterTokenCollector(org.exoplatform.ide.client.framework.codeassistant.events.RegisterTokenCollectorEvent)
    */
   public void onRegisterTokenCollector(RegisterTokenCollectorEvent event)
   {
      collectors.put(event.getMimeType(), event.getCollector());
   }

   public TokenCollectorExt getTokenCollector(String mimeType)
   {
      return collectors.get(mimeType);
   }

}
