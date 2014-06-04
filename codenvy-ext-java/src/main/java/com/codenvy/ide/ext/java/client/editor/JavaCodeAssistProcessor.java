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

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.api.editor.TextEditorPartPresenter;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.java.client.JavaResources;
import com.codenvy.ide.ext.java.jdt.Images;
import com.codenvy.ide.ext.java.messages.WorkerProposal;
import com.codenvy.ide.texteditor.api.CodeAssistCallback;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.api.codeassistant.CodeAssistProcessor;
import com.codenvy.ide.texteditor.api.codeassistant.CompletionProposal;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaCodeAssistProcessor implements CodeAssistProcessor {

    private TextEditorPartPresenter editor;
    private JavaParserWorker        worker;
    private JavaResources           javaResources;
    private AnalyticsEventLogger    eventLogger;

    public JavaCodeAssistProcessor(TextEditorPartPresenter editor,
                                   JavaParserWorker worker,
                                   JavaResources javaResources,
                                   AnalyticsEventLogger eventLogger) {
        this.editor = editor;
        this.worker = worker;
        this.javaResources = javaResources;
        this.eventLogger = eventLogger;
    }

    /** {@inheritDoc} */
    @Override
    public void computeCompletionProposals(TextEditorPartView view, int offset, final CodeAssistCallback callback) {
        eventLogger.log("Autocompleting");
        worker.computeCAProposals(view.getDocument().get(), offset, editor.getEditorInput().getFile().getName(),
                                  editor.getEditorInput().getFile().getProject().getPath(),
                                  new JavaParserWorker.WorkerCallback<WorkerProposal>() {
                                      @Override
                                      public void onResult(Array<WorkerProposal> problems) {
                                          CompletionProposal[] proposals = new CompletionProposal[problems.size()];
                                          for (int i = 0; i < problems.size(); i++) {
                                              WorkerProposal proposal = problems.get(i);
                                              proposals[i] = new CompletionProposalImpl(proposal.id(),
                                                                                        insertStyle(javaResources, proposal.displayText()),
                                                                                        getImage(javaResources, proposal.image()),
                                                                                        proposal.autoInsertable(), worker);
                                          }

                                          callback.proposalComputed(proposals);
                                      }
                                  });
    }

    public  static String insertStyle(JavaResources javaResources, String display) {
        if (display.contains("#FQN#"))
            return display.replace("#FQN#", javaResources.css().fqnStyle());
        else if (display.contains("#COUNTER#"))
            return display.replace("#COUNTER#", javaResources.css().counter());
        else return display;
    }

    public static Image getImage(JavaResources javaResources, String image) {
        if (image == null) {
            return null;
        }
        Images i = Images.valueOf(image);
        ImageResource img = null;
        switch (i) {
            case VARIABLE:
                img = javaResources.variable();
                break;
            case JSP_TAG_ITEM:
                img = javaResources.jspTagItem();
                break;
            case publicMethod:
                img = javaResources.publicMethod();
                break;
            case protectedMethod:
                img = javaResources.protectedMethod();
                break;
            case privateMethod:
                img = javaResources.privateMethod();
                break;
            case defaultMethod:
                img = javaResources.defaultMethod();
                break;
            case enumItem:
                img = javaResources.enumItem();
                break;
            case annotationItem:
                img = javaResources.annotationItem();
                break;
            case interfaceItem:
                img = javaResources.interfaceItem();
                break;
            case classItem:
                img = javaResources.classItem();
                break;
            case publicField:
                img = javaResources.publicField();
                break;
            case protectedField:
                img = javaResources.protectedField();
                break;
            case privateField:
                img = javaResources.privateField();
                break;
            case defaultField:
                img = javaResources.defaultField();
                break;
            case packageItem:
                img = javaResources.packageItem();
                break;
            case classDefaultItem:
                img = javaResources.classDefaultItem();
                break;
            case correction_change:
                img = javaResources.correction_change();
                break;
            case local_var:
                img = javaResources.local_var();
                break;
            case delete_obj:
                img = javaResources.delete_obj();
                break;
            case field_public:
                img = javaResources.field_public();
                break;
            case correction_cast:
                img = javaResources.correction_cast();
                break;
            case add_obj:
                img = javaResources.add_obj();
                break;
            case remove_correction:
                img = javaResources.remove_correction();
                break;
            case template:
                img = javaResources.template();
                break;
            case javadoc:
                img = javaResources.javadoc();
                break;
            case exceptionProp:
                img = javaResources.exceptionProp();
                break;
            case correction_delete_import:
                img = javaResources.correction_delete_import();
                break;
            case imp_obj:
                img = javaResources.imp_obj();
                break;
        }
        return new Image(img);
    }

    /** {@inheritDoc} */
    @Override
    public char[] getCompletionProposalAutoActivationCharacters() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getErrorMessage() {
        return null;
    }

}
