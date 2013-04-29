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
package com.codenvy.ide.core.expressions;

import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.event.ActivePartChangedEvent;
import com.codenvy.ide.api.event.ActivePartChangedHandler;
import com.codenvy.ide.api.event.ChangeToggleItemStateEvent;
import com.codenvy.ide.api.event.ChangeToggleItemStateHandler;
import com.codenvy.ide.api.event.EditorDirtyStateChangedEvent;
import com.codenvy.ide.api.event.EditorDirtyStateChangedHandler;
import com.codenvy.ide.api.event.ExpressionsChangedEvent;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.expressions.ActivePartConstraintExpression;
import com.codenvy.ide.api.expressions.EditorDirtyConstraintExpression;
import com.codenvy.ide.api.expressions.Expression;
import com.codenvy.ide.api.expressions.ExpressionManager;
import com.codenvy.ide.api.expressions.ProjectConstraintExpression;
import com.codenvy.ide.api.expressions.ToggleStateExpression;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonIntegerMap;
import com.codenvy.ide.json.JsonIntegerMap.IterationCallback;
import com.codenvy.ide.resources.model.Project;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;


/** @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> */
public class ExpressionManagerImpl implements ExpressionManager {

    protected final EventBus eventBus;

    protected final JsonIntegerMap<Expression> expressions;

    @Inject
    public ExpressionManagerImpl(EventBus eventBus) {
        this.eventBus = eventBus;
        this.expressions = JsonCollections.createIntegerMap();

        // bind event handlers
        bind();
    }

    /** Bind EventBus listeners */
    protected void bind() {
        eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectChangedHandler());
        eventBus.addHandler(ActivePartChangedEvent.TYPE, new PartChangedHandler());
        eventBus.addHandler(EditorDirtyStateChangedEvent.TYPE, new EditorDirtyChangedHandler());
        eventBus.addHandler(ChangeToggleItemStateEvent.TYPE, new ChangeItemStateHandler());
    }

    /** {@inheritDoc} */
    @Override
    public void registerExpression(Expression expression) {
        expressions.put(expression.getId(), expression);
    }

    /** {@inheritDoc} */
    @Override
    public void unRegisterExpression(Expression expression) {
        expressions.erase(expression.getId());
    }

    /**
     * Gathers the list of changed {@link ActivePartConstraintExpression}
     *
     * @param activePart
     */
    private void calculateActivePartConstraintExpressions(final PartPresenter activePart) {
        final JsonIntegerMap<Boolean> changedExpressions = JsonCollections.createIntegerMap();
        expressions.iterate(new IterationCallback<Expression>() {
            @Override
            public void onIteration(int id, Expression expression) {
                if (expression instanceof ActivePartConstraintExpression) {
                    boolean oldVal = expression.getValue();
                    if (((ActivePartConstraintExpression)expression).onActivePartChanged(activePart) != oldVal) {
                        // value changed
                        changedExpressions.put(id, !oldVal);
                    }
                }
            }
        });

        if (!changedExpressions.isEmpty()) {
            fireChangedExpressions(changedExpressions);
        }
    }

    /**
     * Gathers the list of changed {@link ProjectConstraintExpression}
     *
     * @param project
     */
    private void calculateProjectConstraintExpressions(final Project project) {
        final JsonIntegerMap<Boolean> changedExpressions = JsonCollections.createIntegerMap();
        expressions.iterate(new IterationCallback<Expression>() {
            @Override
            public void onIteration(int id, Expression expression) {
                if (expression instanceof ProjectConstraintExpression) {
                    boolean oldVal = expression.getValue();
                    if (((ProjectConstraintExpression)expression).onProjectChanged(project) != oldVal) {
                        // value changed
                        changedExpressions.put(id, !oldVal);
                    }
                }
            }
        });

        if (!changedExpressions.isEmpty()) {
            fireChangedExpressions(changedExpressions);
        }
    }

    /**
     * Gathers the list of changed {@link EditorDirtyConstraintExpression}
     *
     * @param editor
     */
    private void calculateEditorDirtyStateChanged(final EditorPartPresenter editor) {
        final JsonIntegerMap<Boolean> changedExpressions = JsonCollections.createIntegerMap();
        expressions.iterate(new IterationCallback<Expression>() {
            @Override
            public void onIteration(int id, Expression expression) {
                if (expression instanceof EditorDirtyConstraintExpression) {
                    boolean oldVal = expression.getValue();
                    if (((EditorDirtyConstraintExpression)expression).onEditorDirtyChanged(editor) != oldVal) {
                        // value changed
                        changedExpressions.put(id, !oldVal);
                    }
                }
            }
        });
        if (!changedExpressions.isEmpty()) {
            fireChangedExpressions(changedExpressions);
        }
    }

    /**
     * Fires the event, notifying that following Core Expressions have changed.
     *
     * @param newValues
     */
    private void fireChangedExpressions(JsonIntegerMap<Boolean> newValues) {
        eventBus.fireEvent(new ExpressionsChangedEvent(newValues));
    }

    /** Process {@link ActivePartChangedEvent} */
    private final class PartChangedHandler implements ActivePartChangedHandler {
        @Override
        public void onActivePartChanged(ActivePartChangedEvent event) {
            calculateActivePartConstraintExpressions(event.getActivePart());
        }
    }

    /** Process {@link ProjectActionEvent} */
    private final class ProjectChangedHandler implements ProjectActionHandler {
        @Override
        public void onProjectOpened(ProjectActionEvent event) {
            calculateProjectConstraintExpressions(event.getProject());
        }

        @Override
        public void onProjectDescriptionChanged(ProjectActionEvent event) {
            calculateProjectConstraintExpressions(event.getProject());
        }

        @Override
        public void onProjectClosed(ProjectActionEvent event) {
            calculateProjectConstraintExpressions(null);
        }
    }

    /** Process {@link EditorDirtyStateChangedEvent} */
    private final class EditorDirtyChangedHandler implements EditorDirtyStateChangedHandler {

        /** {@inheritDoc} */
        @Override
        public void onEditorDirtyStateChanged(EditorDirtyStateChangedEvent event) {
            calculateEditorDirtyStateChanged(event.getEditor());
        }

    }

    /** Process {@link ChangeToggleItemStateEvent} */
    private final class ChangeItemStateHandler implements ChangeToggleItemStateHandler {

        /** {@inheritDoc} */
        @Override
        public void onStateChanged(ChangeToggleItemStateEvent event) {
            final JsonIntegerMap<Boolean> changedExpressions = JsonCollections.createIntegerMap();
            final int idExpression = event.getIdExpression();
            expressions.iterate(new IterationCallback<Expression>() {
                @Override
                public void onIteration(int id, Expression expression) {
                    if (expression instanceof ToggleStateExpression && expression.getId() == idExpression) {
                        // value changed
                        changedExpressions.put(id, expression.getValue());
                    }
                }
            });

            if (!changedExpressions.isEmpty()) {
                fireChangedExpressions(changedExpressions);
            }
        }
    }
}
