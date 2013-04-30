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
package com.codenvy.ide.texteditor;

import elemental.html.Element;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.mvp.CompositeView;
import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.annotation.Annotation;
import com.codenvy.ide.text.annotation.AnnotationModel;
import com.codenvy.ide.text.annotation.AnnotationModelEvent;
import com.codenvy.ide.text.annotation.AnnotationModelListener;
import com.codenvy.ide.texteditor.gutter.Gutter;
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
    private       JsonArray<Element>        elements;

    public OverviewRuler(Gutter view, TextEditorViewImpl editor) {
        this.view = view;
        this.editor = editor;
        listener = new InternalListener();
        elements = JsonCollections.createArray();
    }

    private void update() {

        for (Element e : elements.asIterable()) {
            view.removeUnmanagedElement(e);
        }
        elements.clear();
        int numberOfLines = editor.getDocument().getNumberOfLines();
        for (Iterator<Annotation> iterator = model.getAnnotationIterator(); iterator.hasNext(); ) {
            Annotation annotation = iterator.next();

            Mark mark = new Mark(annotation);
            try {
                int lineNumber = editor.getDocument().getLineOfOffset(model.getPosition(annotation).getOffset());

                mark.setTopPosition((100 * lineNumber) / numberOfLines, "%");
                mark.getElement().setAttribute("data-line-number", String.valueOf(lineNumber));
                mark.getElement().setClassName(model.getAnnotationStyle().get(annotation.getType()));
                view.addUnmanagedElement(mark.getElement());
                elements.add(mark.getElement());
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

    class InternalListener implements AnnotationModelListener {

        /** {@inheritDoc} */
        @Override
        public void modelChanged(AnnotationModelEvent event) {
            update();
        }

    }

    class Mark extends CompositeView<Annotation> {
        /**
         *
         */
        public Mark(Annotation annotation) {
            setElement(Elements.createDivElement());
            getElement().getStyle().setZIndex(annotation.getLayer());
            getElement().setTitle(annotation.getText());
        }

        public void setTopPosition(int top, String unit) {
            getElement().getStyle().setTop(top, unit);
        }
    }


}
