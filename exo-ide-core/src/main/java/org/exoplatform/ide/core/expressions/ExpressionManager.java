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
package org.exoplatform.ide.core.expressions;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.core.event.ActivePartChangedEvent;
import org.exoplatform.ide.core.event.ActivePartChangedHandler;
import org.exoplatform.ide.core.event.ExpressionsChangedEvent;
import org.exoplatform.ide.core.event.ProjectActionEvent;
import org.exoplatform.ide.core.event.ProjectActionHandler;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonIntegerMap;
import org.exoplatform.ide.json.JsonIntegerMap.IterationCallback;
import org.exoplatform.ide.part.PartPresenter;
import org.exoplatform.ide.resources.model.Project;

/**
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class ExpressionManager
{

   protected final EventBus eventBus;

   protected final JsonIntegerMap<Expression> expressions;

   @Inject
   public ExpressionManager(EventBus eventBus)
   {
      this.eventBus = eventBus;
      this.expressions = JsonCollections.createIntegerMap();

      // bind event handlers
      bind();
   }

   /**
    * Bind EventBus listeners 
    */
   protected void bind()
   {
      eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectChangedHandler());
      eventBus.addHandler(ActivePartChangedEvent.TYPE, new PartChangedHandler());
   }

   /**
    * Register new {@link Expression} in {@link ExpressionManager}
    * 
    * @param expression
    */
   public void registerExpression(Expression expression)
   {
      expressions.put(expression.getId(), expression);
   }

   /**
    * Removes {@link Expression} from {@link ExpressionManager}
    * 
    * @param expression
    */
   public void unRegisterExpression(Expression expression)
   {
      expressions.erase(expression.getId());
   }

   /**
    * Gathers the list of changed {@link ActivePartConstraintExpression}
    * 
    * @param project
    */
   private void calculateActivePartConstraintExpressions(final PartPresenter activePart)
   {
      final JsonIntegerMap<Boolean> changedExpressions = JsonCollections.createIntegerMap();
      expressions.iterate(new IterationCallback<Expression>()
      {
         @Override
         public void onIteration(int id, Expression expression)
         {
            if (expression instanceof ActivePartConstraintExpression)
            {
               boolean oldVal = expression.getValue();
               if (((ActivePartConstraintExpression)expression).onActivePartChanged(activePart) != oldVal)
               {
                  // value changed
                  changedExpressions.put(id, !oldVal);
               }
            }
         }
      });

      if (!changedExpressions.isEmpty())
      {
         fireChangedExpressions(changedExpressions);
      }
   }

   /**
    * Gathers the list of changed {@link ProjectConstraintExpression}
    * 
    * @param project
    */
   private void calculateProjectConstraintExpressions(final Project project)
   {
      final JsonIntegerMap<Boolean> changedExpressions = JsonCollections.createIntegerMap();
      expressions.iterate(new IterationCallback<Expression>()
      {
         @Override
         public void onIteration(int id, Expression expression)
         {
            if (expression instanceof ProjectConstraintExpression)
            {
               boolean oldVal = expression.getValue();
               if (((ProjectConstraintExpression)expression).onProjectChanged(project) != oldVal)
               {
                  // value changed
                  changedExpressions.put(id, !oldVal);
               }
            }
         }
      });

      if (!changedExpressions.isEmpty())
      {
         fireChangedExpressions(changedExpressions);
      }
   }

   /**
    * Fires the event, notifying that following Core Expressions have changed.
    * 
    * @param newValues
    */
   private void fireChangedExpressions(JsonIntegerMap<Boolean> newValues)
   {
      eventBus.fireEvent(new ExpressionsChangedEvent(newValues));
   }

   /**
    * Process {@link ActivePartChangedEvent}
    */
   private final class PartChangedHandler implements ActivePartChangedHandler
   {
      @Override
      public void onActivePartChanged(ActivePartChangedEvent event)
      {
         calculateActivePartConstraintExpressions(event.getActivePart());
      }
   }

   /**
    * Process {@link ProjectActionEvent}
    */
   private final class ProjectChangedHandler implements ProjectActionHandler
   {
      @Override
      public void onProjectOpened(ProjectActionEvent event)
      {
         calculateProjectConstraintExpressions(event.getProject());
      }

      @Override
      public void onProjectDescriptionChanged(ProjectActionEvent event)
      {
         calculateProjectConstraintExpressions(event.getProject());
      }

      @Override
      public void onProjectClosed(ProjectActionEvent event)
      {
         calculateProjectConstraintExpressions(null);
      }
   }
}
