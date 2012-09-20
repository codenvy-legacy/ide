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
package org.exoplatform.ide.resources.inject;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;

import org.exoplatform.ide.resources.ModelProvider;
import org.exoplatform.ide.resources.model.GenericModelProvider;

/**
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class ResourceGinModule extends AbstractGinModule
{

   /**
   * {@inheritDoc}
   */
   @Override
   protected void configure()
   {
//      bind(BootstrapController.class).in(Singleton.class);
//      bind(WorkspacePeresenter.Display.class).to(WorkspaceView.class).in(Singleton.class);
      
      // Generic Model Provider
      bind(ModelProvider.class).to(GenericModelProvider.class).in(Singleton.class);
   }

}
