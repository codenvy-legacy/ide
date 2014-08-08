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
package com.codenvy.ide.texteditor.renderer;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.dto.client.ClientDocOpFactory;
import com.codenvy.ide.api.text.BadLocationException;
import com.codenvy.ide.text.DocumentImpl;
import com.codenvy.ide.api.text.Position;
import com.codenvy.ide.api.text.annotation.Annotation;
import com.codenvy.ide.api.text.annotation.AnnotationModel;
import com.codenvy.ide.api.text.annotation.AnnotationModelEvent;
import com.codenvy.ide.api.text.annotation.AnnotationModelListener;
import com.codenvy.ide.text.store.Line;
import com.codenvy.ide.text.store.LineFinder;
import com.codenvy.ide.texteditor.TextEditorViewImpl;
import com.codenvy.ide.texteditor.ot.PositionMigrator;
import com.codenvy.ide.util.loging.Log;

import java.util.Iterator;

/** @author Evgen Vidolob */
public class AnnotationRenderer implements AnnotationModelListener {

    private final PositionMigrator   positionMigrator;
    private final StringMap<String>  decorations;
    private       TextEditorViewImpl editor;
    private       AnnotationModel    annotationModel;
    private       ErrorRenderer      renderer;

    /** @param editor */
    public AnnotationRenderer(TextEditorViewImpl editor, StringMap<String> decorations, DtoFactory dtoFactory) {
        super();
        this.editor = editor;
        this.decorations = decorations;
        renderer = new ErrorRenderer();
        editor.addLineRenderer(renderer);
        this.positionMigrator = new PositionMigrator(ClientDocOpFactory.getInstance(dtoFactory));
    }

    /** {@inheritDoc} */
    @Override
    public void modelChanged(AnnotationModelEvent event) {

        Array<AnnotationCode> annotations = Collections.createArray();
        for (Iterator<Annotation> iterator = annotationModel.getAnnotationIterator(); iterator.hasNext(); ) {
            Annotation annotation = iterator.next();
            //only annotation with decoration
            if (decorations.containsKey(annotation.getType())) {
                Position position = annotationModel.getPosition(annotation);
                AnnotationCode ac =
                        new AnnotationCode(getDocumentPosition(position.offset), getDocumentPosition(position.length + position.offset - 1),
                                           decorations.get(annotation.getType()));
                annotations.add(ac);
            }
        }
        onAnnotationsChanged(annotations);
    }

    private DocumentPosition getDocumentPosition(int offset) {

        try {
            int lineNumber = editor.getDocument().getLineOfOffset(offset);
            return new DocumentPosition(lineNumber, offset - editor.getDocument().getLineOffset(lineNumber));
        } catch (BadLocationException e) {
            Log.error(getClass(), e);
        }
        return null;
    }

    private void onAnnotationsChanged(Array<AnnotationCode> newErrors) {
        if (editor.getDocument() == null) {
            return;
        }
        Array<Line> linesToRender = Collections.createArray();
        getLinesOfErrorsInViewport(renderer.getCodeErrors(), linesToRender);
        getLinesOfErrorsInViewport(newErrors, linesToRender);
        positionMigrator.reset();
        renderer.setCodeErrors(newErrors, positionMigrator);

        for (int i = 0; i < linesToRender.size(); i++) {
            editor.getRenderer().requestRenderLine(linesToRender.get(i));
        }
        editor.getRenderer().renderChanges();
    }

    private void getLinesOfErrorsInViewport(Array<AnnotationCode> errors, Array<Line> lines) {
        LineFinder lineFinder = ((DocumentImpl)editor.getDocument()).getTextStore().getLineFinder();
        int topLineNumber = editor.getViewport().getTopLineNumber();
        int bottomLineNumber = editor.getViewport().getBottomLineNumber();
        for (int i = 0; i < errors.size(); i++) {
            AnnotationCode error = errors.get(i);
            for (int j = error.getStart().getLineNumber(); j <= error.getEnd().getLineNumber(); j++) {
                if (j >= topLineNumber && j <= bottomLineNumber) {
                    lines.add(lineFinder.findLine(j).line());
                }
            }
        }
    }

    /** @param annotationModel */
    public void setMode(AnnotationModel annotationModel) {
        this.annotationModel = annotationModel;
        annotationModel.addAnnotationModelListener(this);
        positionMigrator.start(editor.getTextStore().getTextListenerRegistrar());
    }

}
