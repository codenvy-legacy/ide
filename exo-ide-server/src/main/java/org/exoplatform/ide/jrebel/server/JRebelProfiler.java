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
package org.exoplatform.ide.jrebel.server;

import org.exoplatform.ide.commons.JsonHelper;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.registry.RegistryEntry;
import org.exoplatform.services.jcr.ext.registry.RegistryService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.util.HashMap;
import java.util.Map;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

/**
 * @author <a href="vzhukovskii@exoplatform.com">Vladyslav Zhukovskii</a>
 * @version $Id: JRebelProfiler.java 34027 19.12.12 17:03Z vzhukovskii $
 */
public class JRebelProfiler
{
   private RegistryService registryService;

   private SessionProviderService sessionProviderService;

   private final static String IDE = RegistryService.EXO_APPLICATIONS + "/IDE";

   private final static String INVITES_ROOT = IDE + "/jrebel";

   private static final Log LOG_JREBEL_PROP = ExoLogger.getLogger("JRebel");

   public JRebelProfiler(SessionProviderService sessionProviderService, RegistryService registryService)
   {
      this.registryService = registryService;
      this.sessionProviderService = sessionProviderService;
   }

   public void sendProfileInfo(String userId, String firstName, String lastName, String phone)
      throws JRebelProfilerException
   {
      SessionProvider sessionProvider = sessionProviderService.getSystemSessionProvider(null);
      RegistryEntry registryEntry = getOrCreateProfileRoot(sessionProvider);

      saveProfileInfoIfNotExist(registryEntry, sessionProvider, firstName, lastName, phone);

      Map<String, String> profileInfo = new HashMap<String, String>();
      profileInfo.put("description", "jRebel user profile details");
      profileInfo.put("userId", userId);
      profileInfo.put("firstName", firstName);
      profileInfo.put("lastName", lastName);
      profileInfo.put("phone", phone);

      LOG_JREBEL_PROP.error(JsonHelper.toJson(profileInfo));
   }

   private void saveProfileInfoIfNotExist(RegistryEntry entry,
                                          SessionProvider sessionProvider,
                                          String firstName,
                                          String lastName,
                                          String phone) throws JRebelProfilerException
   {
      try
      {
         RegistryEntry jrebelProfileEntry = entry;
         Document profileDocument = jrebelProfileEntry.getDocument();
         Element profileElement = profileDocument.getDocumentElement();
         NodeList allInvites = profileElement.getChildNodes();
         int length = allInvites.getLength();

         if (length > 0)
         {
            //if node is not null, it means that user has stored info about it self for zeroturnaround
            return;
         }

         //otherwise we write to registry service information about user
         Element profileInfo = profileDocument.createElement("profileInfo");
         profileInfo.setAttribute("first_name", firstName);
         profileInfo.setAttribute("last_name", lastName);
         profileInfo.setAttribute("phone", phone);

         profileElement.appendChild(profileInfo);
         registryService.recreateEntry(sessionProvider, IDE, new RegistryEntry(profileDocument));
      }
      catch (RepositoryException e)
      {
         throw new JRebelProfilerException("Unable to register profile info. Please contact support.", e);
      }
   }

   public Map<String, String> getProfileInfo() throws JRebelProfilerException
   {
      SessionProvider sessionProvider = sessionProviderService.getSystemSessionProvider(null);
      RegistryEntry registryEntry = getOrCreateProfileRoot(sessionProvider);

      return getProfileInfo(registryEntry);
   }

   private Map<String, String> getProfileInfo(RegistryEntry entry)
   {
      RegistryEntry jrebelProfileEntry = entry;
      Document profileDocument = jrebelProfileEntry.getDocument();
      Element profileElement = profileDocument.getDocumentElement();
      NodeList nodes = profileElement.getChildNodes();

      Map<String, String> profileInfo = new HashMap<String, String>();
      if (nodes.getLength() == 1)
      {
         profileInfo.put(
            "first_name",
            nodes.item(0).getAttributes().getNamedItem("first_name").getNodeValue()
         );
         profileInfo.put(
            "last_name",
            nodes.item(0).getAttributes().getNamedItem("last_name").getNodeValue()
         );
         profileInfo.put(
            "phone",
            nodes.item(0).getAttributes().getNamedItem("phone").getNodeValue()
         );
      }

      return profileInfo;
   }

   private RegistryEntry getOrCreateProfileRoot(SessionProvider sessionProvider) throws JRebelProfilerException
   {
      try
      {
         return registryService.getEntry(sessionProvider, INVITES_ROOT);
      }
      catch (PathNotFoundException e)
      {
         try
         {
            registryService.createEntry(sessionProvider, IDE, new RegistryEntry("jrebel"));
            return registryService.getEntry(sessionProvider, INVITES_ROOT);
         }
         catch (Exception e1)
         {
            throw new JRebelProfilerException("Unable to save information about jrebel profile");
         }
      }
      catch (RepositoryException e)
      {
         throw new JRebelProfilerException("Unable to get information about jrebel profile");
      }
   }
}