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
package org.eclipse.jdt.client.internal.corext.codemanipulation;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class CodemanipulationModule extends AbstractGinModule
{

   /**
    * @see com.google.gwt.inject.client.AbstractGinModule#configure()
    */
   @Override
   protected void configure()
   {
      bind(AddGetterSetterPresenter.Display.class).to(AddGetterSetterView.class);
      bind(new TypeLiteral<MultiSelectionModel<Object>>()
      {
      });
      bind(GetterSetterTreeModel.class);
      bind(GenerateNewConstructorUsingFieldsPresenter.Display.class).to(GenerateNewConstructorUsingFieldsView.class);
   }

   @Provides
   @Singleton
   GetterSetterEntryProvider provide()
   {
      return AddGetterSetterPresenter.get();
   }
}
