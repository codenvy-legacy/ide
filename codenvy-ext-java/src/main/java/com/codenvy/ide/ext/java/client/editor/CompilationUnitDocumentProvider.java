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
package com.codenvy.ide.ext.java.client.editor;

import elemental.dom.Element;

import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.core.editor.ResourceDocumentProvider;
import com.codenvy.ide.ext.java.client.JavaCss;
import com.codenvy.ide.ext.java.client.JavaResources;
import com.codenvy.ide.ext.java.jdt.JavaPartitions;
import com.codenvy.ide.ext.java.jdt.core.IProblemRequestor;
import com.codenvy.ide.ext.java.jdt.core.compiler.CategorizedProblem;
import com.codenvy.ide.ext.java.jdt.core.compiler.IProblem;
import com.codenvy.ide.ext.java.jdt.internal.ui.text.FastJavaPartitionScanner;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.runtime.Assert;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.DocumentFactory;
import com.codenvy.ide.text.Position;
import com.codenvy.ide.text.annotation.Annotation;
import com.codenvy.ide.text.annotation.AnnotationModel;
import com.codenvy.ide.text.annotation.AnnotationModelImpl;
import com.codenvy.ide.text.rules.FastPartitioner;
import com.codenvy.ide.texteditor.TextEditorViewImpl;
import com.codenvy.ide.texteditor.TextEditorViewImpl.Css;
import com.codenvy.ide.texteditor.api.quickassist.QuickFixableAnnotation;
import com.codenvy.ide.util.dom.Elements;
import com.google.gwt.user.client.ui.Image;
import com.google.web.bindery.event.shared.EventBus;

import org.vectomatic.dom.svg.ui.SVGImage;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Evgen Vidolob
 */
public class CompilationUnitDocumentProvider extends ResourceDocumentProvider {

    /** Array with legal content types. */
    private final static String[] LEGAL_CONTENT_TYPES = new String[]{
            JavaPartitions.JAVA_DOC,
            JavaPartitions.JAVA_MULTI_LINE_COMMENT,
            JavaPartitions.JAVA_SINGLE_LINE_COMMENT,
            JavaPartitions.JAVA_STRING,
            JavaPartitions.JAVA_CHARACTER
    };

    private TextEditorViewImpl.Css css;
    private Map<File, AnnotationModel> modelStringMap = new HashMap<File, AnnotationModel>();
    private JavaCss javaCss;

    /**
     * @param css
     * @param javaCss
     * @param documentFactory
     */
    public CompilationUnitDocumentProvider(Css css, JavaCss javaCss, DocumentFactory documentFactory, EventBus eventBus) {
        super(documentFactory, eventBus);
        this.css = css;
        this.javaCss = javaCss;
    }

    /** {@inheritDoc} */
    @Override
    public AnnotationModel getAnnotationModel(@Nullable EditorInput input) {
        File file = input.getFile();
        if (!modelStringMap.containsKey(file)) {
            modelStringMap.put(file, new JavaAnnotationModel());
        }
        return modelStringMap.get(file);
    }

    /** {@inheritDoc} */
    @Override
    public void getDocument(@Nullable EditorInput input, @NotNull final DocumentCallback callback) {
        super.getDocument(input, new DocumentCallback() {
            @Override
            public void onDocument(Document document) {
                FastPartitioner partitioner = new FastPartitioner(new FastJavaPartitionScanner(), LEGAL_CONTENT_TYPES);
                document.setDocumentPartitioner(JavaPartitions.JAVA_PARTITIONING,
                                                partitioner);
                partitioner.connect(document);
                callback.onDocument(document);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void documentClosed(@NotNull Document document) {
        super.documentClosed(document);
        modelStringMap.remove(document);
    }

    /** Annotation representing an <code>IProblem</code>. */
    static public class ProblemAnnotation extends Annotation implements JavaAnnotation, QuickFixableAnnotation {

        public static final String ERROR_ANNOTATION_TYPE   = "org.eclipse.jdt.ui.error"; //$NON-NLS-1$
        public static final String WARNING_ANNOTATION_TYPE = "org.eclipse.jdt.ui.warning"; //$NON-NLS-1$
        public static final String INFO_ANNOTATION_TYPE    = "org.eclipse.jdt.ui.info"; //$NON-NLS-1$
        public static final String TASK_ANNOTATION_TYPE    = "org.eclipse.ui.workbench.texteditor.task"; //$NON-NLS-1$
        /** The layer in which task problem annotations are located. */
        private static final int           TASK_LAYER;
        /** The layer in which info problem annotations are located. */
        private static final int           INFO_LAYER;
        /** The layer in which warning problem annotations representing are located. */
        private static final int           WARNING_LAYER;
        /** The layer in which error problem annotations representing are located. */
        private static final int           ERROR_LAYER;
        /** The resources in which image and CSS links are located. */
        private static final JavaResources javaRes;

        static {
            //TODO configure this
            TASK_LAYER = 0;
            INFO_LAYER = 1;
            WARNING_LAYER = 2;
            ERROR_LAYER = 3;
            javaRes = JavaResources.INSTANCE;
            javaRes.css().ensureInjected();
        }

        private static Element fgQuickFixElement      = (Element)(new SVGImage(javaRes.markWarning())).getElement();
        private static Element fgQuickFixErrorElement = (Element)(new SVGImage(javaRes.markError())).getElement();
        private static Element fgTaskElement          = (Element)(new Image(javaRes.taskmrk())).getElement();
        private static Element fgInfoElement          = (Element)(new Image(javaRes.imp_obj())).getElement();
        private static Element fgWarningElement       = (Element)(new SVGImage(javaRes.markWarning())).getElement();
        private static Element fgErrorElement         = (Element)(new SVGImage(javaRes.markError())).getElement();
        private List<JavaAnnotation> fOverlaids;
        private IProblem             fProblem;
        private Element fImageElement = null;
        private int     fLayer        = 0;
        private boolean fIsQuickFixable;
        private boolean fIsQuickFixableStateSet = false;

        public ProblemAnnotation(IProblem problem) {

            fProblem = problem;

            if (IProblem.Task == fProblem.getID()) {
                setType(TASK_ANNOTATION_TYPE);
                fLayer = TASK_LAYER;
            } else if (fProblem.isWarning()) {
                setType(WARNING_ANNOTATION_TYPE);
                fLayer = WARNING_LAYER;
            } else if (fProblem.isError()) {
                setType(ERROR_ANNOTATION_TYPE);
                fLayer = ERROR_LAYER;
            } else {
                setType(INFO_ANNOTATION_TYPE);
                fLayer = INFO_LAYER;
            }
        }

        /** @return  */
        public int getLayer() {
            return fLayer;
        }

        private void initializeImage() {
            Element selectedImageElement = null;
            if (!isQuickFixableStateSet())
                setQuickFixable(isProblem() && JavaCorrectionProcessor.hasCorrections(this)); // no light bulb for tasks
            if (isQuickFixable()) {
                if (ERROR_ANNOTATION_TYPE.equals(getType()))
                    selectedImageElement = fgQuickFixErrorElement;
                else
                    selectedImageElement = fgQuickFixElement;
            } else {
                String type = getType();
                if (TASK_ANNOTATION_TYPE.equals(type))
                    selectedImageElement = fgTaskElement;
                else if (INFO_ANNOTATION_TYPE.equals(type))
                    selectedImageElement = fgInfoElement;
                else if (WARNING_ANNOTATION_TYPE.equals(type))
                    selectedImageElement = fgWarningElement;
                else if (ERROR_ANNOTATION_TYPE.equals(type))
                    selectedImageElement = fgErrorElement;
            }
            selectedImageElement.setAttribute("class", javaRes.css().markElementIcon());
            fImageElement = Elements.createDivElement();
            fImageElement.setAttribute("class", javaRes.css().markElement());
            fImageElement.setInnerHTML(selectedImageElement.getOuterHTML());
        }

        /** {@inheritDoc} */
        @Override
        public String getText() {
            return fProblem.getMessage();
        }

        /** {@inheritDoc} */
        public String[] getArguments() {
            return isProblem() ? fProblem.getArguments() : null;
        }

        /** {@inheritDoc} */
        public int getId() {
            return fProblem.getID();
        }

        /** {@inheritDoc} */
        public boolean isProblem() {
            String type = getType();
            return WARNING_ANNOTATION_TYPE.equals(type) || ERROR_ANNOTATION_TYPE.equals(type);
        }

        /** {@inheritDoc} */
        public boolean hasOverlay() {
            return false;
        }

        /** {@inheritDoc} */
        public JavaAnnotation getOverlay() {
            return null;
        }

        /** {@inheritDoc} */
        public void addOverlaid(JavaAnnotation annotation) {
            if (fOverlaids == null)
                fOverlaids = new ArrayList<JavaAnnotation>(1);
            fOverlaids.add(annotation);
        }

        /** {@inheritDoc} */
        public void removeOverlaid(JavaAnnotation annotation) {
            if (fOverlaids != null) {
                fOverlaids.remove(annotation);
                if (fOverlaids.size() == 0)
                    fOverlaids = null;
            }
        }

        /** {@inheritDoc} */
        public Iterator<JavaAnnotation> getOverlaidIterator() {
            if (fOverlaids != null)
                return fOverlaids.iterator();
            return null;
        }

        /** {@inheritDoc} */
        public String getMarkerType() {
            if (fProblem instanceof CategorizedProblem)
                return ((CategorizedProblem)fProblem).getMarkerType();
            return null;
        }

        /** {@inheritDoc} */
        public boolean isQuickFixableStateSet() {
            return fIsQuickFixableStateSet;
        }

        /** {@inheritDoc} */
        public boolean isQuickFixable() {
            Assert.isTrue(isQuickFixableStateSet());
            return fIsQuickFixable;
        }

        /** {@inheritDoc} */
        public void setQuickFixable(boolean state) {
            fIsQuickFixable = state;
            fIsQuickFixableStateSet = true;
        }

        /** {@inheritDoc} */
        @Override
        public Element getElement() {
            if (fImageElement == null)
                initializeImage();
            return fImageElement;
        }
    }

    class JavaAnnotationModel extends AnnotationModelImpl implements AnnotationModel, IProblemRequestor {

        private List<IProblem> reportedProblems;
        //      private List<JavaMarkerAnnotation> fPreviouslyOverlaid= null;
        //
        //      private List<JavaMarkerAnnotation> fCurrentlyOverlaid= new ArrayList<JavaMarkerAnnotation>();
        private List<ProblemAnnotation> fGeneratedAnnotations = new ArrayList<ProblemAnnotation>();

        protected Position createPositionFromProblem(IProblem problem) {
            int start = problem.getSourceStart();
            int end = problem.getSourceEnd();

            if (start == -1 && end == -1)
                return new Position(0);

            if (start == -1)
                return new Position(end);

            if (end == -1)
                return new Position(start);

            int length = end - start + 1;
            if (length < 0)
                return null;

            return new Position(start, length);
        }

        /** {@inheritDoc} */
        @Override
        public void acceptProblem(IProblem problem) {
            reportedProblems.add(problem);
        }

        /** {@inheritDoc} */
        @Override
        public void beginReporting() {
            reportedProblems = new ArrayList<IProblem>();
        }

        /** {@inheritDoc} */
        @Override
        public void endReporting() {
            reportProblems(reportedProblems);

        }

        /** @param problems */
        private void reportProblems(List<IProblem> problems) {
            boolean temporaryProblemsChanged = false;

            //            fPreviouslyOverlaid= fCurrentlyOverlaid;
            //            fCurrentlyOverlaid= new ArrayList<JavaMarkerAnnotation>();

            if (fGeneratedAnnotations.size() > 0) {
                temporaryProblemsChanged = true;
                removeAnnotations(fGeneratedAnnotations, false, true);
                fGeneratedAnnotations.clear();
            }

            if (reportedProblems != null && reportedProblems.size() > 0) {

                Iterator<IProblem> e = reportedProblems.iterator();
                while (e.hasNext()) {

                    IProblem problem = e.next();
                    Position position = createPositionFromProblem(problem);
                    if (position != null) {

                        ProblemAnnotation annotation = new ProblemAnnotation(problem);
                        addAnnotation(annotation, position, false);
                        fGeneratedAnnotations.add(annotation);

                        temporaryProblemsChanged = true;
                    }
                }

            }

            if (temporaryProblemsChanged)
                fireModelChanged();
        }

        /** {@inheritDoc} */
        @Override
        public boolean isActive() {
            // TODO Auto-generated method stub
            return false;
        }

        /** {@inheritDoc} */
        @Override
        public StringMap<String> getAnnotationDecorations() {
            StringMap<String> decorations = Collections.createStringMap();
            //TODO configure this
            decorations.put("org.eclipse.jdt.ui.error", css.lineError());
            decorations.put("org.eclipse.jdt.ui.warning", css.lineWarning());

            return decorations;
        }

        /** {@inheritDoc} */
        @Override
        public StringMap<String> getAnnotationStyle() {
            StringMap<String> decorations = Collections.createStringMap();
//            //TODO configure this
            decorations.put("org.eclipse.jdt.ui.error", javaCss.overviewMarkError());
            decorations.put("org.eclipse.jdt.ui.warning", javaCss.overviewMarkWarning());
            decorations.put("org.eclipse.jdt.ui.info", javaCss.overviewMarkTask());
            decorations.put("org.eclipse.ui.workbench.texteditor.task", javaCss.overviewMarkTask());
            return decorations;
        }
    }
}
