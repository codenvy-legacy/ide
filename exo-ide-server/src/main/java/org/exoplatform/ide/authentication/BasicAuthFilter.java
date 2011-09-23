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
package org.exoplatform.ide.authentication;

import org.apache.commons.codec.binary.Base64;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Credential;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityRegistry;
import org.exoplatform.services.security.PasswordCredential;
import org.exoplatform.services.security.UsernameCredential;

import java.io.IOException;
import java.security.Principal;

import javax.security.auth.login.LoginException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class BasicAuthFilter implements Filter
{

   private static final Log LOG = ExoLogger.getLogger(BasicAuthFilter.class.getName());

   public void destroy()
   {
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
      ServletException
   {
      HttpServletRequest httpRequest = (HttpServletRequest)request;
      String auth = httpRequest.getHeader("Authorization");
      if (auth == null)
      {
         HttpServletResponse httpResponse = (HttpServletResponse)response;
         httpResponse.setStatus(401);
         httpResponse.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"exo-domain\"");
         return;
      }

      // cut 'basic '
      byte[] token = Base64.decodeBase64(auth.substring(6).getBytes());
      String username = null;
      String password = null;
      int colon = -1;
      for (int i = 0; i < token.length; i++)
         if (token[i] == ':')
            colon = i;
      if (colon < 0)
      {
         username = new String(token);
      }
      else
      {
         username = new String(token, 0, colon);
         password = new String(token, colon + 1, token.length - colon - 1);
      }
      ExoContainer container = ExoContainerContext.getCurrentContainer();
      if (container == null)
      {
         container = ExoContainerContext.getTopContainer();
      }

      Identity identity = null;
      try
      {
         Authenticator authenticator = (Authenticator)container.getComponentInstanceOfType(Authenticator.class);

         if (authenticator == null)
         {
            LOG.error("Authenticator not found");
         }

         Credential[] credentials =
            new Credential[]{new UsernameCredential(username), new PasswordCredential(password)};

         String userId = authenticator.validateUser(credentials);

         identity = authenticator.createIdentity(userId);

         IdentityRegistry ir = (IdentityRegistry)container.getComponentInstanceOfType(IdentityRegistry.class);
         if (ir == null)
         {
            LOG.error("Authenticator not found");
         }

         ir.register(identity);
         ConversationState.setCurrent(new ConversationState(identity));


      }
      catch (LoginException e)
      {
         HttpServletResponse httpResponse = (HttpServletResponse)response;
         httpResponse.setStatus(401);
         httpResponse.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"exo-domain\"");
         return;
      }
      catch (Exception e)
      {
         LOG.error("Error occurs ", e);
         HttpServletResponse httpResponse = (HttpServletResponse)response;
         httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
         return;
      }
      
      chain.doFilter(new AuthServletRequest(httpRequest, identity), response);

   }

   final class AuthServletRequest extends HttpServletRequestWrapper
   {
      private final Identity identity;

      public AuthServletRequest(HttpServletRequest request, Identity identity)
      {
         super(request);
         this.identity = identity;
      }

      public String getRemoteUser()
      {
         return identity.getUserId();
      }

      @Override
      public Principal getUserPrincipal()
      {

         return new MockPrincipal(identity.getUserId());
      }

      @Override
      public boolean isUserInRole(String role)
      {
         return identity.getRoles().contains(role);
      }
   }

   public void init(FilterConfig filterConfig) throws ServletException
   {
   }

   private class MockPrincipal implements Principal
   {
      private String username;

      public MockPrincipal(String username)
      {
         this.username = username;
      }

      public String getName()
      {
         return username;
      }
   }

}
