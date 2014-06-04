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
package com.codenvy.ide.ext.java.client.editor;

import com.codenvy.ide.api.editor.TextEditorPartPresenter;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.js.JsoArray;
import com.codenvy.ide.ext.java.client.JavaResources;
import com.codenvy.ide.ext.java.jdt.codeassistant.api.IProblemLocation;
import com.codenvy.ide.ext.java.jdt.core.IJavaModelMarker;
import com.codenvy.ide.ext.java.messages.ProblemLocationMessage;
import com.codenvy.ide.ext.java.messages.WorkerProposal;
import com.codenvy.ide.ext.java.messages.impl.MessagesImpls;
import com.codenvy.ide.text.Position;
import com.codenvy.ide.text.annotation.Annotation;
import com.codenvy.ide.text.annotation.AnnotationModel;
import com.codenvy.ide.texteditor.api.CodeAssistCallback;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.api.codeassistant.CompletionProposal;
import com.codenvy.ide.texteditor.api.quickassist.QuickAssistInvocationContext;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaCorrectionProcessor implements com.codenvy.ide.texteditor.api.quickassist.QuickAssistProcessor {

    private final JavaCorrectionAssistant assistant;
    private       JavaParserWorker        worker;
    private JavaResources javaResources;
    private String errorMessage;

    /**
     *
     */
    public JavaCorrectionProcessor(JavaCorrectionAssistant assistant, JavaParserWorker worker, JavaResources javaResources) {
        this.assistant = assistant;
        this.worker = worker;
        this.javaResources = javaResources;
    }

    /**
     * @param annot
     * @return
     */
    public static boolean hasCorrections(IProblemLocation annot) {
        return QuickFixResolver.hasCorrections(annot.getProblemId());
    }

    public static boolean hasCorrections(Annotation annotation) {
        if (annotation instanceof JavaAnnotation) {
            JavaAnnotation javaAnnotation = (JavaAnnotation)annotation;
            int problemId = javaAnnotation.getId();
            if (problemId != -1) {
                return QuickFixResolver.hasCorrections(problemId);

            }
        }
        return false;
    }

    public static boolean isQuickFixableType(Annotation annotation) {
        return (annotation instanceof JavaAnnotation) && !annotation.isMarkedDeleted();
    }

    /** {@inheritDoc} */
    public void computeQuickAssistProposals(QuickAssistInvocationContext quickAssistContext, final CodeAssistCallback callback) {
        TextEditorPartView textView = quickAssistContext.getTextEditor();
        int documentOffset = quickAssistContext.getOffset();

        TextEditorPartPresenter part = assistant.getEditor();

        AnnotationModel model = part.getDocumentProvider().getAnnotationModel(part.getEditorInput());


        Annotation[] annotations = assistant.getAnnotationsAtOffset();
        int length = textView != null ? textView.getSelection().getSelectedRange().length : 0;
        errorMessage = null;
        if (model != null && annotations != null) {

            JsoArray<ProblemLocationMessage> problems = JsoArray.create();

            // collect problem locations and corrections from marker annotations
            for (Annotation curr : annotations) {
                ProblemLocationMessage problemLocation = null;
                if (curr instanceof JavaAnnotation) {
                    problemLocation = getProblemLocation((JavaAnnotation)curr, model);
                    if (problemLocation != null) {
                        problems.add(problemLocation);
                    }
                }
            }
         worker.computeQAProposals(textView.getDocument().get(), documentOffset,length ,assistant.isUpdatedOffset(), problems, new JavaParserWorker.WorkerCallback<WorkerProposal>() {
             @Override
             public void onResult(Array<WorkerProposal> problems) {
                 CompletionProposal[] proposals = new CompletionProposal[problems.size()];
                 for (int i = 0; i < problems.size(); i++) {
                     WorkerProposal proposal = problems.get(i);
                     proposals[i] = new CompletionProposalImpl(proposal.id(), JavaCodeAssistProcessor.insertStyle(javaResources,
                                                                                                                  proposal.displayText()),
                                                               JavaCodeAssistProcessor.getImage(javaResources, proposal.image()),
                                                               proposal.autoInsertable(), worker);
                 }
                 callback.proposalComputed(proposals);
             }
         });
        }

    }
    private static ProblemLocationMessage getProblemLocation(JavaAnnotation javaAnnotation, AnnotationModel model) {
        int problemId = javaAnnotation.getId();
        if (problemId != -1) {
            Position pos = model.getPosition((Annotation)javaAnnotation);
            if (pos != null) {
                MessagesImpls.ProblemLocationMessageImpl pl = MessagesImpls.ProblemLocationMessageImpl.make();
                pl.setOffset(pos.getOffset()).setLength(pos.getLength());
                pl.setIsError(CompilationUnitDocumentProvider.ProblemAnnotation.ERROR_ANNOTATION_TYPE.equals(javaAnnotation.getType()));
                String markerType = javaAnnotation.getMarkerType();
                pl.setMarkerType(markerType != null ? markerType : IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER);
                pl.setProblemId(javaAnnotation.getId());
                if(javaAnnotation.getArguments() != null){
                   pl.setProblemArguments(
                        JsoArray.from(javaAnnotation.getArguments()));
                }
                else pl.setProblemArguments(null);
                return pl;

            }
        }
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
