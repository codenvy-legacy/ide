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
package org.exoplatform.ide.authentication.openid;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageExtension;
import org.openid4java.message.Parameter;
import org.openid4java.message.ParameterList;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/auth/openid")
public class OpenIDAuthenticator
{
   private static final Log LOG = ExoLogger.getLogger(OpenIDAuthenticator.class);

   private static final Map<String, OpenIDProvider> openIdProviders;

   static
   {
      openIdProviders = new HashMap<String, OpenIDProvider>(1);

      openIdProviders.put("google", new OpenIDProvider("https://www.google.com/accounts/o8/id",
         "http://www.google.com/favicon.ico", "Sign in with a Google Account"));
      // yahoo => http://open.login.yahooapis.com/openid20/www.yahoo.com/xrds
      // facebook => http://www.facebook.com/openid/xrds.php
   }

   @Inject
   private UserStore userStore;

   @Path("authenticate")
   @GET
   public Response authenticate(@Context UriInfo uriInfo, @Context HttpServletRequest servletRequest) throws OpenIDException
   {
      final String openIDProviderName = uriInfo.getQueryParameters().getFirst("openid_provider");
      final OpenIDProvider openIDProvider = openIdProviders.get(openIDProviderName);
      if (openIDProvider == null)
      {
         LOG.error("Unsupported OpenID provider {} ", openIDProviderName);
         throw new WebApplicationException(Response
            .status(400)
            .entity("Unsupported OpenID provider " + openIDProviderName)
            .type(MediaType.TEXT_PLAIN).build());
      }

      HttpSession session = servletRequest.getSession();
      final String redirectAfterLogin = uriInfo.getQueryParameters().getFirst("redirect_after_login");
      if (redirectAfterLogin != null)
      {
         session.setAttribute("openid.redirect_after_login", redirectAfterLogin);
      }

      ConsumerManager consumerManager = new ConsumerManager();
      DiscoveryInformation discovered = consumerManager.associate(consumerManager.discover(openIDProvider.getDiscoveryUrl()));
      session.setAttribute("openid.consumer", consumerManager);
      session.setAttribute("openid.discovered", discovered);

      final String returnTo = uriInfo.getBaseUriBuilder().path(getClass(), "verify").build().toString();
      AuthRequest req = consumerManager.authenticate(discovered, returnTo);
      FetchRequest fetch = FetchRequest.createFetchRequest();
      fetch.addAttribute("email", "http://schema.openid.net/contact/email", true);
      req.addExtension(fetch);
      final boolean popup = null != uriInfo.getQueryParameters().getFirst("popup");
      session.setAttribute("openid.popup", popup);
      final boolean favicon = null != uriInfo.getQueryParameters().getFirst("favicon");
      if (popup || favicon)
      {
         req.addExtension(new UIExtension(popup ? "popup" : null, favicon));
      }
      return Response.temporaryRedirect(URI.create(req.getDestinationUrl(true))).build();
   }

   @Path("verify")
   @GET
   public Response verify(@Context UriInfo uriInfo,
                          @Context HttpServletRequest servletRequest) throws Exception
   {
      HttpSession session = servletRequest.getSession();
      try
      {
         DiscoveryInformation discovered = (DiscoveryInformation)session.getAttribute("openid.discovered");
         ConsumerManager consumerManager = (ConsumerManager)session.getAttribute("openid.consumer");
         ParameterList params = new ParameterList(servletRequest.getParameterMap());
         VerificationResult result = consumerManager.verify(uriInfo.getRequestUri().toString(), params, discovered);
         final Identifier identifier = result.getVerifiedId();
         if (identifier == null)
         {
            final String mode = result.getAuthResponse().getParameterValue("openid.mode");
            LOG.error("Cannot get openID identifier, result {}. ", mode);
            session.setAttribute("openid.mode", mode);
            // Lets user enter user ID and password.
            return Response.temporaryRedirect(URI.create((String)session.getAttribute("openid.redirect_after_login"))).build();
         }

         AuthSuccess authSuccess = (AuthSuccess)result.getAuthResponse();
         FetchResponse fetchResp = (FetchResponse)authSuccess.getExtension(AxMessage.OPENID_NS_AX);
         final String email = (String)fetchResp.getAttributeValues("email").get(0);

         final OpenIDUser user = new OpenIDUser(identifier);
         user.setAttribute("email", email);

         userStore.put(email, user);
         session.setAttribute("openid.user", user);

         return Response.temporaryRedirect(URI.create((String)session.getAttribute("openid.redirect_after_login"))).build();
      }
      finally
      {
         session.removeAttribute("openid.discovered");
         session.removeAttribute("openid.consumer");
         session.removeAttribute("openid.popup");
         session.removeAttribute("openid.redirect_after_login");
      }
   }

   private static class UIExtension implements MessageExtension
   {
      private final ParameterList params;

      UIExtension(String mode, boolean showFavicon)
      {
         this.params = new ParameterList();
         if (mode != null)
         {
            this.params.set(new Parameter("mode", mode));
         }
         this.params.set(new Parameter("icon", Boolean.toString(showFavicon)));
      }

      UIExtension(boolean showFavicon)
      {
         this(null, showFavicon);
      }

      UIExtension()
      {
         this(null, false);
      }

      @Override
      public String getTypeUri()
      {
         return "http://specs.openid.net/extensions/ui/1.0";
      }

      @Override
      public ParameterList getParameters()
      {
         return this.params;
      }

      @Override
      public void setParameters(ParameterList params)
      {
      }

      @Override
      public boolean providesIdentifier()
      {
         return false;
      }

      @Override
      public boolean signRequired()
      {
         return true;
      }
   }
}
