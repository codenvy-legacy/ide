/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.texteditor;

import elemental.events.Event;
import elemental.events.EventListener;
import elemental.dom.Element;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.mvp.CompositeView;
import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Position;
import com.codenvy.ide.text.TextUtilities;
import com.codenvy.ide.text.annotation.Annotation;
import com.codenvy.ide.text.annotation.AnnotationModel;
import com.codenvy.ide.text.annotation.AnnotationModelEvent;
import com.codenvy.ide.text.annotation.AnnotationModelListener;
import com.codenvy.ide.texteditor.api.TextEditorOperations;
import com.codenvy.ide.texteditor.gutter.Gutter;
import com.codenvy.ide.texteditor.gutter.Gutter.ClickListener;
import com.codenvy.ide.ui.Tooltip;
import com.codenvy.ide.ui.menu.PositionController;
import com.codenvy.ide.util.ListenerRegistrar.Remover;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.UIObject;

import java.util.Iterator;

/**
 * Ruler that render annotations in left gutter.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class VerticalRuler {

    private final Gutter             view;
    private final TextEditorViewImpl editor;
    private       AnnotationModel    model;
    private       Remover            remover;
    private       InternalListener   listener;
    private       Array<Mark>        elements;

    /** @param leftNotificationGutter */
    public VerticalRuler(Gutter leftNotificationGutter, TextEditorViewImpl editor) {
        this.view = leftNotificationGutter;
        this.editor = editor;
        listener = new InternalListener();
        elements = Collections.createArray();
        view.getClickListenerRegistrar().add(new ClickListener() {

            @Override
            public void onClick(int y) {

                TextEditorViewImpl editor = VerticalRuler.this.editor;
                if (editor.canDoOperation(TextEditorOperations.QUICK_ASSIST)) {
                    int lineNumber = editor.getBuffer().convertYToLineNumber(y, true);
                    try {
                        int offset = editor.getDocument().getLineOffset(lineNumber);
                        editor.getSelection().setCursorPosition(offset);
                        editor.doOperation(TextEditorOperations.QUICK_ASSIST);
                    } catch (BadLocationException e) {
                        Log.error(getClass(), e);
                    }
                }
            }
        });
        UIObject.ensureDebugId((com.google.gwt.dom.client.Element)view.getGutterElement(), "leftNotificationGutter");
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
                    Tooltip.create(mark.getElement(), PositionController.VerticalAlign.MIDDLE, PositionController.HorizontalAlign.RIGHT,
                                   messages.get(0));
        } else if (messages.size() > 1) {
            tooltip = Tooltip.create(mark.getElement(), PositionController.VerticalAlign.MIDDLE, PositionController.HorizontalAlign.RIGHT,
                                     formatMultipleMessages(messages));
        }
        if (tooltip == null)
            return;
        tooltip.show();
    }

    /**
     *
     */
    private void update() {
        for (Mark e : elements.asIterable()) {
            view.removeUnmanagedElement(e.getElement());
        }
        elements.clear();

        for (Iterator<Annotation> iterator = model.getAnnotationIterator(); iterator.hasNext(); ) {
            Annotation annotation = iterator.next();
            if (annotation.getImage() == null)
                continue;
            Mark m = new Mark(annotation);
            Position position = model.getPosition(annotation);
            int lineNumber = getLineNumberForPosition(position);
            m.lineNumber = lineNumber;
            m.setTopPosition(editor.getBuffer().calculateLineTop(lineNumber), "px");
            UIObject.ensureDebugId((com.google.gwt.dom.client.Element)m.getElement(), "mark" + (lineNumber+1));
            view.addUnmanagedElement(m.getElement());
            elements.add(m);
        }
    }

    /** @param position */
    private int getLineNumberForPosition(Position position) {
        return TextUtilities.getLineLineNumber(editor.getDocument(), position.getOffset());
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
        private int        lineNumber;
        private Annotation annotation;

        /**
         *
         */
        public Mark(Annotation annotation) {
            this.annotation = annotation;
            setElement((Element)AbstractImagePrototype.create(annotation.getImage()).createElement());
            getElement().getStyle().setZIndex(annotation.getLayer());
            getElement().getStyle().setPosition("absolute");
            getElement().getStyle().setWidth("15px");
            getElement().getStyle().setLeft("0px");
            getElement().addEventListener(Event.MOUSEOVER, new EventListener() {
                @Override
                public void handleEvent(Event evt) {
                    showToolTip(Mark.this);
                }
            }, false);

        }

        public void setTopPosition(int top, String unit) {
            getElement().getStyle().setTop(top, unit);
        }
    }

}
