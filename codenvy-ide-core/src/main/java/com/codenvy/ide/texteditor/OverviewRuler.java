/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.texteditor;

import elemental.events.Event;
import elemental.events.EventListener;

import com.codenvy.ide.api.text.BadLocationException;
import com.codenvy.ide.api.text.annotation.Annotation;
import com.codenvy.ide.api.text.annotation.AnnotationModel;
import com.codenvy.ide.api.text.annotation.AnnotationModelEvent;
import com.codenvy.ide.api.text.annotation.AnnotationModelListener;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.mvp.CompositeView;
import com.codenvy.ide.texteditor.gutter.Gutter;
import com.codenvy.ide.ui.Tooltip;
import com.codenvy.ide.ui.menu.PositionController;
import com.codenvy.ide.util.ListenerRegistrar;
import com.codenvy.ide.util.dom.Elements;
import com.codenvy.ide.util.loging.Log;

import java.util.Iterator;

/**
 * Ruler that render annotations in right overview gutter.
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class OverviewRuler {

    private final Gutter                    view;
    private final TextEditorViewImpl        editor;
    private       AnnotationModel           model;
    private       ListenerRegistrar.Remover remover;
    private       InternalListener          listener;
    private       Array<Mark>               elements;

    public OverviewRuler(Gutter view, TextEditorViewImpl editor) {
        this.view = view;
        this.editor = editor;
        listener = new InternalListener();
        elements = Collections.createArray();
    }

    private void update() {

        for (Mark e : elements.asIterable()) {
            view.removeUnmanagedElement(e.getElement());
        }
        elements.clear();
        int numberOfLines = editor.getDocument().getNumberOfLines();
        for (Iterator<Annotation> iterator = model.getAnnotationIterator(); iterator.hasNext(); ) {
            Annotation annotation = iterator.next();

            Mark mark = new Mark(annotation);
            try {
                int lineNumber = editor.getDocument().getLineOfOffset(model.getPosition(annotation).getOffset());

                mark.setTopPosition(editor.getBuffer().calculateLineTop(lineNumber), "px");
                mark.setLineNumber(lineNumber);
                mark.getElement().setAttribute("data-line-number", String.valueOf(lineNumber));
                mark.getElement().setClassName(model.getAnnotationStyle().get(annotation.getType()));
                view.addUnmanagedElement(mark.getElement());
                elements.add(mark);
            } catch (BadLocationException e) {
                Log.error(getClass(), e);
            }


        }
    }

    /** @param annotationModel */
    public void setModel(AnnotationModel annotationModel) {
        if (model != annotationModel) {
            if (remover != null) {
                remover.remove();
            }
            model = annotationModel;
            remover = model.addAnnotationModelListener(listener);
        }
    }

    private void showToolTip(Mark mark) {
        Array<String> messages = Collections.createArray();
        for (Mark m : elements.asIterable()) {
            if (m.lineNumber == mark.lineNumber) {
                messages.add(m.annotation.getText());
            }
        }
        Tooltip tooltip = null;
        if (messages.size() == 1) {
            tooltip =
                    Tooltip.create(mark.getElement(), PositionController.VerticalAlign.MIDDLE, PositionController.HorizontalAlign.LEFT,
                                   messages.get(0));
        } else if (messages.size() > 1) {
            tooltip = Tooltip.create(mark.getElement(), PositionController.VerticalAlign.MIDDLE, PositionController.HorizontalAlign.LEFT,
                                     formatMultipleMessages(messages));
        }
        if (tooltip == null)
            return;
        tooltip.show();
    }

    /**
     * @param messages
     *         the messages to format (element type: {@link String})
     * @return the formatted message
     */
    protected String[] formatMultipleMessages(Array<String> messages) {
        String[] message = new String[messages.size() + 1];
        message[0] = "Multiple markers at this line";
        for (int i = 0; i < messages.size(); i++) {
            message[i + 1] = " - " + messages.get(i);
        }
        return message;
    }

    class InternalListener implements AnnotationModelListener {

        /** {@inheritDoc} */
        @Override
        public void modelChanged(AnnotationModelEvent event) {
            update();
        }

    }

    class Mark extends CompositeView<Annotation> {
        private Annotation annotation;
        private int        lineNumber;

        /**
         *
         */
        public Mark(final Annotation annotation) {
            this.annotation = annotation;
            setElement(Elements.createDivElement());
            getElement().getStyle().setZIndex(annotation.getLayer());
            getElement().addEventListener(Event.MOUSEOVER, new EventListener() {
                @Override
                public void handleEvent(Event event) {
                    showToolTip(Mark.this);
                }
            }, false);
            getElement().addEventListener(Event.MOUSEDOWN, new EventListener() {
                @Override
                public void handleEvent(Event event) {
                    editor.getSelection().setCursorPosition(model.getPosition(annotation).getOffset());
                }
            }, false);
        }

        public void setTopPosition(int top, String unit) {
            getElement().getStyle().setTop(top, unit);
        }

        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }
    }

}
