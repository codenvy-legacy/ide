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
package com.codenvy.ide.client.inject;

import com.codenvy.ide.client.BootstrapController;
import com.codenvy.ide.client.StyleInjector;
import com.codenvy.ide.extension.ExtensionGinModule;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;


/**
 * GIN Client module for ide-client subproject. Used to maintain relations of
 * ide-client specific components.  
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@ExtensionGinModule
public class IDEClientModule extends AbstractGinModule
{
   /**
    * {@inheritDoc}
    */
   @Override
   protected void configure()
   {
      bind(BootstrapController.class).in(Singleton.class);
      bind(StyleInjector.class).in(Singleton.class);
   }
}
