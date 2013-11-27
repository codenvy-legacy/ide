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

import com.codenvy.ide.ext.java.jdt.MultiStatus;
import com.codenvy.ide.ext.java.jdt.codeassistant.api.IProblemLocation;
import com.codenvy.ide.ext.java.jdt.codeassistant.api.JavaCompletionProposal;
import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.AdvancedQuickAssistProcessor;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.CorrectionMessages;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.ProblemLocation;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.QuickAssistProcessorImpl;
import com.codenvy.ide.ext.java.jdt.internal.text.correction.QuickFixProcessorImpl;
import com.codenvy.ide.ext.java.jdt.quickassist.api.InvocationContext;
import com.codenvy.ide.ext.java.jdt.quickassist.api.QuickAssistProcessor;
import com.codenvy.ide.ext.java.jdt.quickassist.api.QuickFixProcessor;
import com.codenvy.ide.runtime.CoreException;
import com.codenvy.ide.runtime.IStatus;
import com.codenvy.ide.runtime.Status;
import com.codenvy.ide.text.Position;
import com.codenvy.ide.text.annotation.Annotation;
import com.codenvy.ide.text.annotation.AnnotationModel;
import com.codenvy.ide.texteditor.api.CodeAssistCallback;
import com.codenvy.ide.texteditor.api.quickassist.QuickAssistInvocationContext;
import com.codenvy.ide.util.loging.Log;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaCorrectionProcessor implements com.codenvy.ide.texteditor.api.quickassist.QuickAssistProcessor {

    private static QuickFixProcessor fixProcessor;

    private static QuickAssistProcessor[] assistProcessors;

    private final JavaCorrectionAssistant assistant;

    private String fErrorMessage;

    private CompilationUnit cu;

    /**
     *
     */
    public JavaCorrectionProcessor(JavaCorrectionAssistant assistant, JavaParserWorker worker) {
        this.assistant = assistant;
        if (fixProcessor == null) {
            fixProcessor = new QuickFixProcessorImpl();
            assistProcessors =
                    new QuickAssistProcessor[]{new QuickAssistProcessorImpl(), new AdvancedQuickAssistProcessor()};
        }
//        astProvider.addAstListener(new AstListener() {
//
//            @Override
//            public void onCompilationUnitChanged(CompilationUnit cUnit) {
//                cu = cUnit;
//            }
//        });
    }

    /*
     * @see IContentAssistProcessor#computeCompletionProposals(ITextViewer, int)
     */
    public void computeQuickAssistProposals(QuickAssistInvocationContext quickAssistContext, CodeAssistCallback callback) {
//        TextEditorPartView textView = quickAssistContext.getTextEditor();
//        int documentOffset = quickAssistContext.getOffset();
//
//        TextEditorPartPresenter part = assistant.getEditor();
//
//        AnnotationModel model = part.getDocumentProvider().getAnnotationModel(part.getEditorInput());
//
//        AssistContext context = null;
//        if (cu != null) {
//            int length = textView != null ? textView.getSelection().getSelectedRange().length : 0;
//            context = new AssistContext(textView, textView.getDocument(), documentOffset, length, cu);
//        }
//
//        Annotation[] annotations = assistant.getAnnotationsAtOffset();
//
//        fErrorMessage = null;
//        CompletionProposal[] res = null;
//        try {
//            if (model != null && context != null && annotations != null) {
//                ArrayList<JavaCompletionProposal> proposals = new ArrayList<JavaCompletionProposal>(10);
//                IStatus status =
//                        collectProposals(context, model, annotations, true, !assistant.isUpdatedOffset(), proposals);
//                res = proposals.toArray(new CompletionProposal[proposals.size()]);
//                if (!status.isOK()) {
//                    fErrorMessage = status.getMessage();
//                    //TODO
//                    //JavaPlugin.log(status);
//                }
//            }
//        } catch (CoreException e) {
//            Log.error(getClass(), e);
//        }
//        if (res == null || res.length == 0) {
//            return new CompletionProposal[0];//{new ChangeCorrectionProposal("EDIT ME", new NullChange(""), 0, null)}; //$NON-NLS-1$
//        }
//        if (res.length > 1) {
//            Arrays.sort(res, new CompletionProposalComparator());
//        }
//        return res;
    }

    public static IStatus collectProposals(InvocationContext context, AnnotationModel model, Annotation[] annotations,
                                           boolean addQuickFixes, boolean addQuickAssists, Collection<JavaCompletionProposal> proposals)
            throws CoreException {
        ArrayList<ProblemLocation> problems = new ArrayList<ProblemLocation>();

        // collect problem locations and corrections from marker annotations
        for (int i = 0; i < annotations.length; i++) {
            Annotation curr = annotations[i];
            ProblemLocation problemLocation = null;
            if (curr instanceof JavaAnnotation) {
                problemLocation = getProblemLocation((JavaAnnotation)curr, model);
                if (problemLocation != null) {
                    problems.add(problemLocation);
                }
            }
        }
        ProblemLocation[] problemLocations = problems.toArray(new ProblemLocation[problems.size()]);
        MultiStatus resStatus = null;
        //
        if (addQuickFixes) {
            IStatus status = collectCorrections(context, problemLocations, proposals);
            if (!status.isOK()) {
                resStatus =
                        new MultiStatus(JavaCore.PLUGIN_ID, IStatus.ERROR,
                                        CorrectionMessages.INSTANCE.JavaCorrectionProcessor_error_quickfix_message(), null);
                resStatus.add(status);
            }
        }
        if (addQuickAssists) {
            IStatus status = collectAssists(context, problemLocations, proposals);
            if (!status.isOK()) {
                if (resStatus == null) {
                    resStatus =
                            new MultiStatus(JavaCore.PLUGIN_ID, IStatus.ERROR,
                                            CorrectionMessages.INSTANCE.JavaCorrectionProcessor_error_quickassist_message(), null);
                }
                resStatus.add(status);
            }
        }
        if (resStatus != null) {
            return resStatus;
        }
        return Status.OK_STATUS;
    }

    private static ProblemLocation getProblemLocation(JavaAnnotation javaAnnotation, AnnotationModel model) {
        int problemId = javaAnnotation.getId();
        if (problemId != -1) {
            Position pos = model.getPosition((Annotation)javaAnnotation);
            if (pos != null) {
                return new ProblemLocation(pos.getOffset(), pos.getLength(),
                                           javaAnnotation); // java problems all handled by the quick assist processors
            }
        }
        return null;
    }

    public static IStatus collectCorrections(InvocationContext context, IProblemLocation[] locations,
                                             Collection<JavaCompletionProposal> proposals) throws CoreException {
        JavaCompletionProposal[] res;
        res = fixProcessor.getCorrections(context, locations);
        if (res != null) {
            for (int k = 0; k < res.length; k++) {
                proposals.add(res[k]);
            }
        }
        return Status.OK_STATUS;
    }

    public static IStatus collectAssists(InvocationContext context, IProblemLocation[] locations,
                                         Collection<JavaCompletionProposal> proposals) throws CoreException {

        for (QuickAssistProcessor curr : assistProcessors) {
            JavaCompletionProposal[] res;
            res = curr.getAssists(context, locations);
            if (res != null) {
                for (int k = 0; k < res.length; k++) {
                    proposals.add(res[k]);
                }
            }
        }
        return Status.OK_STATUS;

    }

    /**
     * @param annot
     * @return
     */
    public static boolean hasCorrections(IProblemLocation annot) {
        return fixProcessor.hasCorrections(annot.getProblemId());
    }

    public static boolean hasCorrections(Annotation annotation) {
        if (annotation instanceof JavaAnnotation) {
            JavaAnnotation javaAnnotation = (JavaAnnotation)annotation;
            int problemId = javaAnnotation.getId();
            if (problemId != -1) {
                //            CompilationUnit cu = javaAnnotation.getCompilationUnit();
                return fixProcessor.hasCorrections(problemId);
                //            if (cu != null)
                //            {
                //            }
            }
        }
        return false;
    }

    public static boolean isQuickFixableType(Annotation annotation) {
        return (annotation instanceof JavaAnnotation) && !annotation.isMarkedDeleted();
    }

    //   /**
    //    * @param context
    //    * @return
    //    * @throws CoreException
    //    */
    public static boolean hasAssists(InvocationContext context) {
        for (QuickAssistProcessor curr : assistProcessors) {
            try {
                if (curr.hasAssists(context))
                    return true;
            } catch (CoreException e) {
                Log.error(JavaCorrectionProcessor.class, e);
                return false;
            }
        }

        return false;
    }

    /** {@inheritDoc} */
    @Override
    public String getErrorMessage() {
        return fErrorMessage;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canFix(Annotation annotation) {
        return hasCorrections(annotation);
    }

    /** {@inheritDoc} */
    @Override
    public boolean canAssist(QuickAssistInvocationContext invocationContext) {
        if (invocationContext instanceof InvocationContext)
            return hasAssists((InvocationContext)invocationContext);
        return false;
    }

}
