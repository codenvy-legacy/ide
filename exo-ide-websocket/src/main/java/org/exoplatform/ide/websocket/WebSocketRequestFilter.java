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
package org.exoplatform.ide.websocket;

import org.everrest.core.Filter;
import org.everrest.core.GenericContainerRequest;
import org.everrest.core.RequestFilter;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.IdentityRegistry;
import org.exoplatform.services.security.Identity;
import java.security.Principal;
import java.util.List;
import javax.inject.Inject;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Filter
public class WebSocketRequestFilter implements RequestFilter
{
   IdentityRegistry identityRegistry;

   @Override
   public void doFilter(GenericContainerRequest request)
   {
      List<String> headers = request.getRequestHeader("x-everrest-protocol");

      if (headers != null && headers.contains("websocket"))
      {
         Principal principal = request.getUserPrincipal();

         if (principal != null)
         {
            ExoContainer container = ExoContainerContext.getCurrentContainer();
            identityRegistry = (IdentityRegistry)container.getComponentInstanceOfType(IdentityRegistry.class);
            Identity identity = identityRegistry.getIdentity(principal.getName());

            ConversationState.setCurrent(new ConversationState(identity));
         }
      }
   }
}
