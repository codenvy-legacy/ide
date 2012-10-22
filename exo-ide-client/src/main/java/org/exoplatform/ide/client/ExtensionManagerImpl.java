/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client;

import com.google.gwt.inject.client.AsyncProvider;

import org.exoplatform.ide.DemoExtension;
import org.exoplatform.ide.extension.ExtensionDescription;
import org.exoplatform.ide.extension.ExtensionManager;
import org.exoplatform.ide.extension.ExtensionManagerGenerator;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;

import com.google.inject.Inject;

import java.util.List;

/**
 * 
 * EXAMPLE! Doesn't used in IDE. This is a sample class, that 
 * {@link ExtensionManagerGenerator} would generate on compile
 * time.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class ExtensionManagerImpl implements ExtensionManager
{
   JsonStringMap<ExtensionDescription> extensions = JsonCollections.createStringMap();

   @Inject
   public ExtensionManagerImpl(AsyncProvider<DemoExtension> demoExtProvider)
   {
      extensions.put("ide.ext.demo", new ExtensionDescription("ide.ext.demo", "1.0.0", dependencies, demoExtProvider));
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public List<ExtensionDescription> getExtensions()
   {
      return null;
   }

}
