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

import com.codenvy.ide.api.expressions.AbstractExpression;
import com.codenvy.ide.api.expressions.ExpressionManager;
import com.codenvy.ide.api.expressions.ProjectConstraintExpression;
import com.codenvy.ide.resources.model.Project;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Detects opening CloudFoundry project.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class CloudFoundryProjectOpenedExpression extends AbstractExpression implements ProjectConstraintExpression
{

   /**
    * Create expression.
    * 
    * @param expressionManager
    */
   @Inject
   public CloudFoundryProjectOpenedExpression(ExpressionManager expressionManager)
   {
      super(expressionManager, false);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean onProjectChanged(Project project)
   {
      value = project.getProperty("cloudfoundry-application") != null;
      return value;
   }
}