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
package com.codenvy.ide.texteditor.renderer;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.dto.client.ClientDocOpFactory;
import com.codenvy.ide.collections.JsonStringMap;
import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.DocumentImpl;
import com.codenvy.ide.text.Position;
import com.codenvy.ide.text.annotation.Annotation;
import com.codenvy.ide.text.annotation.AnnotationModel;
import com.codenvy.ide.text.annotation.AnnotationModelEvent;
import com.codenvy.ide.text.annotation.AnnotationModelListener;
import com.codenvy.ide.text.store.Line;
import com.codenvy.ide.text.store.LineFinder;
import com.codenvy.ide.texteditor.TextEditorViewImpl;
import com.codenvy.ide.texteditor.ot.PositionMigrator;
import com.codenvy.ide.util.loging.Log;

import java.util.Iterator;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class AnnotationRenderer implements AnnotationModelListener {

    private final PositionMigrator   positionMigrator;
    private       TextEditorViewImpl editor;

    private AnnotationModel annotationModel;

    private final JsonStringMap<String> decorations;

    private ErrorRenderer renderer;

    /** @param editor */
    public AnnotationRenderer(TextEditorViewImpl editor, JsonStringMap<String> decorations) {
        super();
        this.editor = editor;
        this.decorations = decorations;
        renderer = new ErrorRenderer();
        editor.addLineRenderer(renderer);
        this.positionMigrator = new PositionMigrator(ClientDocOpFactory.INSTANCE);
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
                        new AnnotationCode(getDocumentPosition(position.offset), getDocumentPosition(position.length
                                                                                                     + position.offset - 1),
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
