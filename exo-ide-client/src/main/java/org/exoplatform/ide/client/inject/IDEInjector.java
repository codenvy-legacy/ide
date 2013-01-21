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
package org.exoplatform.ide.client.inject;

import com.google.gwt.inject.client.GinModules;
import com.google.gwt.inject.client.Ginjector;

import org.exoplatform.ide.client.BootstrapController;
import org.exoplatform.ide.core.inject.CoreGinModule;
import org.exoplatform.ide.java.client.inject.JavaGinModule;

/**
 * Interface for GIN Injector, that provides access to the top level
 * application components. Implementation of Injector is generated
 * on compile time.
 * 
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Jul 24, 2012  
 */
@GinModules({IDEClientModule.class,CoreGinModule.class, JavaGinModule.class})
public interface IDEInjector extends Ginjector
{
   /**
    * @return the instance of BootstrapController
    */
   BootstrapController getBootstrapController();
}
