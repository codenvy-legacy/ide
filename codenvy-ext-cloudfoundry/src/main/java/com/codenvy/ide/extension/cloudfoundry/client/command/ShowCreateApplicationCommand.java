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

import com.codenvy.ide.api.expressions.Expression;

import com.codenvy.ide.api.ui.menu.ExtendedCommand;

import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryResources;
import com.codenvy.ide.extension.cloudfoundry.client.create.CreateApplicationPresenter;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Command for "PaaS/CloudFoudry/Create Application" action.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class ShowCreateApplicationCommand implements ExtendedCommand
{
   private final CreateApplicationPresenter presenter;

   private final CloudFoundryResources resources;

   private final CloudFoundryProjectOpenedExpression expression;

   /**
    * Create command.
    * 
    * @param presenter
    * @param resources
    */
   @Inject
   public ShowCreateApplicationCommand(CreateApplicationPresenter presenter, CloudFoundryResources resources,
      CloudFoundryProjectOpenedExpression expression)
   {
      this.presenter = presenter;
      this.resources = resources;
      this.expression = expression;
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
      return resources.createApp();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getToolTip()
   {
      return "Create new application on cloudfoundry.com";
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Expression inContext()
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Expression canExecute()
   {
      return expression;
   }
}