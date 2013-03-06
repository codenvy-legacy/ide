/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.extension.cloudfoundry.client.command;

import com.codenvy.ide.core.expressions.Expression;
import com.codenvy.ide.menu.ExtendedCommand;

import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryResources;

import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.codenvy.ide.extension.cloudfoundry.client.apps.ApplicationsPresenter;

/**
 * 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class ShowApplicationsCommand implements ExtendedCommand
{
   private final ApplicationsPresenter presenter;

   private final CloudFoundryResources resources;

   /**
    * Create command.
    * 
    * @param presenter
    * @param resources
    */
   @Inject
   public ShowApplicationsCommand(ApplicationsPresenter presenter, CloudFoundryResources resources)
   {
      this.presenter = presenter;
      this.resources = resources;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void execute()
   {
      presenter.showDialog();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public ImageResource getIcon()
   {
      return resources.appsList();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getToolTip()
   {
      return "Shows registered applications on cloudfoundry.com";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Expression inContext()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Expression canExecute()
   {
      // TODO Auto-generated method stub
      return null;
   }
}