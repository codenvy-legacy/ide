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
package org.exoplatform.ide.invite;

import static org.exoplatform.ide.invite.StreamUtil.readStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.exoplatform.container.configuration.ConfigurationException;
import org.exoplatform.container.xml.Deserializer;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * Class provides extra functionality for building templates by using map of properties and deserializing them in
 * created template.
 */
public class TemplateResolver
{

   private static final Log LOG = ExoLogger.getLogger(TemplateResolver.class);

   private final Map<String, String> parameters = new HashMap<String, String>();

   public TemplateResolver(InitParams initParams) throws IOException
   {
      Iterator<ValueParam> iterator = initParams.getValueParamIterator();
      while (iterator.hasNext())
      {
         ValueParam valueParam = iterator.next();
         String name = valueParam.getName();
         String path = valueParam.getValue();
         if (name == null || path == null)
         {
            throw new IllegalArgumentException("Invalid key-value pair : " + name + " => " + path);
         }
         this.parameters.put(name, path);
      }
   }

   public TemplateResolver(Map<String, String> parameters)
   {
      for (Map.Entry<String, String> e : parameters.entrySet())
      {
         if (e.getKey() == null || e.getValue() == null)
         {
            throw new IllegalArgumentException("Invalid key-value pair : " + e.getKey() + " => " + e.getValue());
         }
      }
      this.parameters.putAll(parameters);
   }

   /**
    * @param key
    *    name of value parameter
    * @param properties
    *    map of properties
    * @return string value of template with deserialized variables
    * @throws org.exoplatform.container.configuration.ConfigurationException
    *    if there is no such template
    * @throws java.io.IOException
    *    exception during reading template
    */
   public String resolveTemplate(String key, Map<String, Object> properties) throws ConfigurationException, IOException
   {
      LOG.debug("Resolve template {} with params {}", key, properties);
      // Get path to template from configuration if exists
      String templatePath = parameters.get(key);
      if (templatePath == null)
      {
         throw new ConfigurationException("Parameter " + key + " not found in configuration. Please contact support.");
      }

      InputStream templateInputStream = null;
      try
      {

         File template = new File(templatePath);
         if (template.exists() && !template.isFile())
         {
            throw new IOException(template.getAbsolutePath() + " is not a file. ");
         }
         templateInputStream = template.exists() ? new FileInputStream(templatePath) : getClass().getResourceAsStream
            (templatePath);
         if (templateInputStream == null)
         {
            throw new IOException("Not found template file: " + templatePath);
         }

         return Deserializer.resolveVariables(readStream(templateInputStream), properties);
      }
      finally
      {
         if (templateInputStream != null)
         {
            try
            {
               templateInputStream.close();
            }
            catch (IOException ignored)
            {
               LOG.error(ignored.getLocalizedMessage(), ignored);
            }
         }
      }
   }
}
