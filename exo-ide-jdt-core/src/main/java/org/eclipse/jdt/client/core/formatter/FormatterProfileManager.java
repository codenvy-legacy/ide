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
package org.eclipse.jdt.client.core.formatter;

import com.google.gwt.xml.client.NamedNodeMap;

import com.google.gwt.xml.client.Node;

import com.google.gwt.xml.client.NodeList;

import com.google.gwt.xml.client.Document;

import com.google.gwt.xml.client.XMLParser;

import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;

import com.google.gwt.core.client.GWT;

import com.google.gwt.resources.client.ExternalTextResource;

import com.google.gwt.resources.client.TextResource;

import com.google.gwt.resources.client.ClientBundle;

import com.google.gwt.event.shared.HandlerManager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 12:33:26 PM Apr 3, 2012 evgen $
 * 
 */
public class FormatterProfileManager
{
   public final static String ECLIPSE_PROFILE = "org.eclipse.jdt.ui.default.eclipse_profile";

   public final static String EXO_PROFILE = "org.exoplatform.ide.default_profile";

   private final HandlerManager eventBus;

   public class Profile
   {
      private final String name;

      private final String id;

      private final Map<String, String> settings;

      /**
       * @param name
       * @param id
       * @param settings
       */
      public Profile(String name, String id, Map<String, String> settings)
      {
         super();
         this.name = name;
         this.id = id;
         this.settings = settings;
      }

      /**
       * @return the name
       */
      public String getName()
      {
         return name;
      }

      /**
       * @return the id
       */
      public String getId()
      {
         return id;
      }

      /**
       * @return the settings
       */
      public Map<String, String> getSettings()
      {
         return settings;
      }

   }

   private Map<String, Profile> profiles = new HashMap<String, FormatterProfileManager.Profile>();

   interface ExoProfile extends ClientBundle
   {
      @Source("exo-jboss-codestyle.xml")
      ExternalTextResource eXoProfile();
   }

   /**
    * 
    */
   public FormatterProfileManager(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      profiles.put(ECLIPSE_PROFILE, new Profile("Eclipse [built-in]", ECLIPSE_PROFILE, DefaultCodeFormatterOptions
         .getEclipseDefaultSettings().getMap()));

      ExoProfile prof = GWT.create(ExoProfile.class);
      try
      {
         prof.eXoProfile().getText(new ResourceCallback<TextResource>()
         {

            @Override
            public void onSuccess(TextResource resource)
            {
               Document exoSettings = XMLParser.parse(resource.getText());
               NodeList nodeList = exoSettings.getElementsByTagName("setting");
               HashMap<String, String> settings = new HashMap<String, String>();
               for (int i = 0; i < nodeList.getLength(); i++)
               {
                  NamedNodeMap attributes = nodeList.item(i).getAttributes();
                  settings.put(attributes.getNamedItem("id").getNodeValue(), attributes.getNamedItem("value")
                     .getNodeValue());
               }
               profiles.put(EXO_PROFILE, new Profile(EXO_PROFILE, "eXo [built-in]", settings));

            }

            @Override
            public void onError(ResourceException e)
            {
               e.printStackTrace();
            }
         });
      }
      catch (ResourceException e)
      {
         e.printStackTrace();
      }
   }

   public Profile getProfile(String id)
   {
      return profiles.get(id);
   }

   public Profile getDefault()
   {
      return profiles.get(EXO_PROFILE);
   }
}
