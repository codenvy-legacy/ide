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
package org.eclipse.jdt.client;

import com.codenvy.ide.client.util.logging.Log;
import com.google.collide.client.CollabEditor;
import com.google.collide.client.CollabEditorExtension;
import com.google.collide.client.code.autocomplete.AutocompleteBox;
import com.google.collide.client.code.autocomplete.integration.AutocompleteUiController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;

import org.eclipse.jdt.client.codeassistant.*;
import org.eclipse.jdt.client.codeassistant.api.IJavaCompletionProposal;
import org.eclipse.jdt.client.codeassistant.ui.ProposalSelectedHandler;
import org.eclipse.jdt.client.compiler.batch.CompilationUnit;
import org.eclipse.jdt.client.core.*;
import org.eclipse.jdt.client.core.dom.AST;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.ASTParser;
import org.eclipse.jdt.client.event.CancelParseEvent;
import org.eclipse.jdt.client.internal.codeassist.CompletionEngine;
import org.eclipse.jdt.client.internal.compiler.flow.UnconditionalFlowInfo.AssertionFailedException;
import org.eclipse.jdt.client.runtime.NullProgressMonitor;
import org.eclipse.jdt.client.templates.TemplateProposal;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.editor.api.codeassitant.RunCodeAssistantEvent;
import org.exoplatform.ide.editor.api.codeassitant.RunCodeAssistantHandler;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.api.event.EditorHotKeyPressedEvent;
import org.exoplatform.ide.editor.client.api.event.EditorHotKeyPressedHandler;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.*;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 24, 2012 5:11:46 PM evgen $
 */
public class CodeAssistantPresenter implements RunCodeAssistantHandler, EditorActiveFileChangedHandler,
                                               ProposalSelectedHandler, EditorHotKeyPressedHandler {

    private FileModel currentFile;

    private Editor currentEditor;

    private int completionPosition;

    private TemplateCompletionProposalComputer templateCompletionProposalComputer =
            new TemplateCompletionProposalComputer();

    private HandlerRegistration keyHandler;

    private AssistDisplay display;

    private final SupportedProjectResolver resolver;

    /**
     *
     */
    public CodeAssistantPresenter(SupportedProjectResolver resolver) {
        this.resolver = resolver;
        IDE.addHandler(RunCodeAssistantEvent.TYPE, this);
        IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.editor.api.codeassitant.RunCodeAssistantHandler#onRunCodeAssistant(org.exoplatform.ide.editor.api
     * .codeassitant.RunCodeAssistantEvent) */
    @Override
    public void onRunCodeAssistant(RunCodeAssistantEvent event) {
        if (currentFile == null || currentEditor == null)
            return;

        if (!resolver.isProjectSupported(currentFile.getProject().getProjectType()))
            return;

        IDE.fireEvent(new CancelParseEvent());
        GWT.runAsync(new RunAsyncCallback() {

            @Override
            public void onSuccess() {
                codecomplete();
            }

            @Override
            public void onFailure(Throwable reason) {
                IDE.fireEvent(new OutputEvent(reason.getMessage(), Type.ERROR));
            }
        });
    }

    private IJavaCompletionProposal[] createProposals(boolean useOldAST) {
        IDocument document = currentEditor.getDocument();
        if (!useOldAST) {
            ASTParser parser = ASTParser.newParser(AST.JLS3);
            parser.setSource(document.get().toCharArray());
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            parser.setUnitName(currentFile.getName().substring(0, currentFile.getName().lastIndexOf('.')));
            parser.setNameEnvironment(new NameEnvironment(currentFile.getProject().getId()));
            parser.setResolveBindings(true);
            ASTNode ast = parser.createAST(null);
            unit = (org.eclipse.jdt.client.core.dom.CompilationUnit)ast;
        }
        try {
            completionPosition =
                    currentEditor.getDocument().getLineOffset(currentEditor.getCursorRow() - 1)
                    + currentEditor.getCursorColumn() - 1;
        } catch (BadLocationException e1) {
            e1.printStackTrace();
        }
        // unit.getPosition(currentEditor.getCursorRow(), currentEditor.getCursorCol() - 1);
        CompletionProposalCollector collector = createCollector(document);
        char[] fileContent = document.get().toCharArray();
        CompletionEngine e =
                new CompletionEngine(new NameEnvironment(currentFile.getProject().getId()), collector, JavaCore.getOptions(),
                                     new NullProgressMonitor());
        try {
            e.complete(
                    new CompilationUnit(fileContent,
                                        currentFile.getName().substring(0, currentFile.getName().lastIndexOf('.')), "UTF-8"),
                    completionPosition, 0);

            IJavaCompletionProposal[] javaCompletionProposals = collector.getJavaCompletionProposals();
            List<IJavaCompletionProposal> types =
                    new ArrayList<IJavaCompletionProposal>(Arrays.asList(javaCompletionProposals));
            if (types.size() > 0 && collector.getInvocationContext().computeIdentifierPrefix().length() == 0) {
                IType expectedType = collector.getInvocationContext().getExpectedType();
                if (expectedType != null) {
                    // empty prefix completion - insert LRU types if known, but prune if they already occur in the core list

                    // compute minmimum relevance and already proposed list
                    int relevance = Integer.MAX_VALUE;
                    Set<String> proposed = new HashSet<String>();
                    for (Iterator<IJavaCompletionProposal> it = types.iterator(); it.hasNext(); ) {
                        AbstractJavaCompletionProposal p = (AbstractJavaCompletionProposal)it.next();
                        IJavaElement element = p.getJavaElement();
                        if (element instanceof IType)
                            proposed.add(((IType)element).getFullyQualifiedName());
                        relevance = Math.min(relevance, p.getRelevance());
                    }

                    // insert history types
                    List<String> history =
                            JdtExtension.get().getContentAssistHistory().getHistory(expectedType.getFullyQualifiedName())
                                        .getTypes();
                    relevance -= history.size() + 1;
                    for (Iterator<String> it = history.iterator(); it.hasNext(); ) {
                        String type = it.next();
                        if (proposed.contains(type))
                            continue;

                        IJavaCompletionProposal proposal =
                                createTypeProposal(relevance, type, collector.getInvocationContext());

                        if (proposal != null)
                            types.add(proposal);
                        relevance++;
                    }
                }
            }

            List<IJavaCompletionProposal> templateProposals =
                    templateCompletionProposalComputer.computeCompletionProposals(collector.getInvocationContext(), null);
            IJavaCompletionProposal[] array =
                    templateProposals.toArray(new IJavaCompletionProposal[templateProposals.size()]);
            javaCompletionProposals = types.toArray(new IJavaCompletionProposal[0]);
            IJavaCompletionProposal[] proposals =
                    new IJavaCompletionProposal[javaCompletionProposals.length + array.length];
            System.arraycopy(javaCompletionProposals, 0, proposals, 0, javaCompletionProposals.length);
            System.arraycopy(array, 0, proposals, javaCompletionProposals.length, array.length);

            Arrays.sort(proposals, comparator);
            return proposals;
        } catch (AssertionFailedException ex) {
            IDE.fireEvent(new OutputEvent(ex.getMessage(), Type.ERROR));

        } catch (Exception ex) {
//         String st = ex.getClass().getName() + ": " + ex.getMessage();
//         for (StackTraceElement ste : ex.getStackTrace())
//            st += "\n" + ste.toString();
//         IDE.fireEvent(new OutputEvent(st, Type.ERROR));
            Log.error(getClass(), ex);
        }
        return new IJavaCompletionProposal[0];
    }

    private IJavaCompletionProposal createTypeProposal(int relevance, String fullyQualifiedType,
                                                       JavaContentAssistInvocationContext context) {
        IType type = TypeInfoStorage.get().getTypeByFqn(fullyQualifiedType);

        if (type == null)
            return null;

        CompletionProposal proposal =
                CompletionProposal.create(CompletionProposal.TYPE_REF, context.getInvocationOffset());
        proposal.setCompletion(fullyQualifiedType.toCharArray());
        proposal.setDeclarationSignature(Signature.getQualifier(type.getFullyQualifiedName().toCharArray()));
        proposal.setFlags(type.getFlags());
        proposal.setRelevance(relevance);
        proposal.setReplaceRange(context.getInvocationOffset(), context.getInvocationOffset());
        proposal.setSignature(Signature.createTypeSignature(fullyQualifiedType, true).toCharArray());

        return new LazyGenericTypeProposal(proposal, context);

    }

    /**
     * @param document
     * @return
     */
    private CompletionProposalCollector createCollector(IDocument document) {
        CompletionProposalCollector collector =
                new FillArgumentNamesCompletionProposalCollector(unit, document, completionPosition, currentFile.getProject()
                                                                                                                .getId(),
                                                                 JdtExtension.DOC_CONTEXT);
        collector
                .setAllowsRequiredProposals(CompletionProposal.CONSTRUCTOR_INVOCATION, CompletionProposal.TYPE_REF, true);
        collector.setAllowsRequiredProposals(CompletionProposal.ANONYMOUS_CLASS_CONSTRUCTOR_INVOCATION,
                                             CompletionProposal.TYPE_REF, true);
        collector.setAllowsRequiredProposals(CompletionProposal.ANONYMOUS_CLASS_DECLARATION, CompletionProposal.TYPE_REF,
                                             true);

        collector.setIgnored(CompletionProposal.ANNOTATION_ATTRIBUTE_REF, false);
        collector.setIgnored(CompletionProposal.ANONYMOUS_CLASS_DECLARATION, false);
        collector.setIgnored(CompletionProposal.ANONYMOUS_CLASS_CONSTRUCTOR_INVOCATION, false);
        collector.setIgnored(CompletionProposal.FIELD_REF, false);
        collector.setIgnored(CompletionProposal.FIELD_REF_WITH_CASTED_RECEIVER, false);
        collector.setIgnored(CompletionProposal.KEYWORD, false);
        collector.setIgnored(CompletionProposal.LABEL_REF, false);
        collector.setIgnored(CompletionProposal.LOCAL_VARIABLE_REF, false);
        collector.setIgnored(CompletionProposal.METHOD_DECLARATION, false);
        collector.setIgnored(CompletionProposal.METHOD_NAME_REFERENCE, false);
        collector.setIgnored(CompletionProposal.METHOD_REF, false);
        collector.setIgnored(CompletionProposal.CONSTRUCTOR_INVOCATION, false);
        collector.setIgnored(CompletionProposal.METHOD_REF_WITH_CASTED_RECEIVER, false);
        collector.setIgnored(CompletionProposal.PACKAGE_REF, false);
        collector.setIgnored(CompletionProposal.POTENTIAL_METHOD_DECLARATION, false);
        collector.setIgnored(CompletionProposal.VARIABLE_DECLARATION, false);
        collector.setIgnored(CompletionProposal.TYPE_REF, false);
        collector.setRequireExtendedContext(true);
        return collector;
    }

    /**
     *
     */
    private void codecomplete() {
//      int posX = currentEditor.getCursorOffsetLeft() + 2;
//      int posY = currentEditor.getCursorOffsetTop() + 15;
        keyHandler = IDE.addHandler(EditorHotKeyPressedEvent.TYPE, this);
        AutocompleteBox popup = new AutocompleteUiController(((CollabEditor)currentEditor).getEditor(),
                                                             CollabEditorExtension.get().getContext().getResources());

        popup.positionAndShow(createProposals(false));
//      display = new CodeAssitantForm(posX, posY, createProposals(false), this);
    }

    private Comparator<IJavaCompletionProposal> comparator = new Comparator<IJavaCompletionProposal>() {

        @Override
        public int compare(IJavaCompletionProposal o1, IJavaCompletionProposal o2) {

            if (o1.getRelevance() > o2.getRelevance())
                return -1;
            else if (o1.getRelevance() < o2.getRelevance())
                return 1;
            else
                return 0;
        }
    };

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        if (event.getFile() == null)
            return;
        if (event.getFile().getMimeType().equals(MimeType.APPLICATION_JAVA)) {
            currentFile = event.getFile();
            currentEditor = event.getEditor();
        } else {
            currentFile = null;
            currentEditor = null;
        }
    }

    /** @see org.eclipse.jdt.client.codeassistant.ui.ProposalSelectedHandler#onTokenSelected(org.eclipse.jdt.client.codeassistant.ui
     * .ProposalWidget) */
    @Override
    public void onTokenSelected(org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal proposal, boolean editorHasFocus) {
        try {
            IDocument document = currentEditor.getDocument();
            proposal.apply(document);
            int cursorPosition = completionPosition;
            int replacementOffset = 0;
            if (proposal instanceof AbstractJavaCompletionProposal) {
                AbstractJavaCompletionProposal proposal2 = (AbstractJavaCompletionProposal)proposal;
                cursorPosition = proposal2.getCursorPosition();
                replacementOffset = proposal2.getReplacementOffset();
            } else if (proposal instanceof TemplateProposal) {
                cursorPosition = ((TemplateProposal)proposal).getCursorPosition();
                replacementOffset = completionPosition;
            }
            String string = document.get(0, replacementOffset + cursorPosition);
            String[] split = string.split("\n");
            currentEditor.setCursorPosition(split.length, split[split.length - 1].length() + 1);
        } catch (Exception e) {
            e.printStackTrace();
            IDE.fireEvent(new OutputEvent(e.getMessage(), Type.ERROR));
        } finally {
            onCancelAutoComplete(editorHasFocus);
        }
    }

    /** @see org.eclipse.jdt.client.codeassistant.ui.ProposalSelectedHandler#onCancelAutoComplete() */
    @Override
    public void onCancelAutoComplete(boolean editorHasFocus) {
        if (!editorHasFocus)
            currentEditor.setFocus();
        keyHandler.removeHandler();
        display = null;
    }

    private Timer timer = new Timer() {

        @Override
        public void run() {
            IJavaCompletionProposal[] proposals = createProposals(true);
            if (proposals.length == 0)
                display.cancelCodeAssistant();
            else
                display.setNewProposals(proposals);
        }
    };

    private org.eclipse.jdt.client.core.dom.CompilationUnit unit;

    /**
     *
     */
    private void generateNewProposals() {
        IDE.fireEvent(new CancelParseEvent());
        timer.cancel();
        timer.schedule(1000);
    }

    /** @see org.exoplatform.ide.editor.client.api.event.EditorHotKeyPressedHandler#onEditorHotKeyPressed(org.exoplatform.ide.editor
     * .client.api.event.EditorHotKeyPressedEvent) */
    @Override
    public void onEditorHotKeyPressed(EditorHotKeyPressedEvent event) {
        switch (event.getKeyCode()) {
            case KeyCodes.KEY_DOWN:
                display.moveSelectionDown();
                event.setHotKeyHandled(true);
                break;

            case KeyCodes.KEY_UP:
                display.moveSelectionUp();
                event.setHotKeyHandled(true);
                break;

            case KeyCodes.KEY_ENTER:
                display.proposalSelected();
                event.setHotKeyHandled(true);
                break;

            case KeyCodes.KEY_ESCAPE:
                display.cancelCodeAssistant();
                event.setHotKeyHandled(true);
                break;

            case KeyCodes.KEY_RIGHT:
                if (currentEditor.getCursorColumn() + 1 > currentEditor.getLineText(currentEditor.getCursorRow()).length())
                    display.cancelCodeAssistant();
                else
                    generateNewProposals();
                break;

            case KeyCodes.KEY_LEFT:
                if (currentEditor.getCursorColumn() - 1 <= 0)
                    display.cancelCodeAssistant();
                else
                    generateNewProposals();
                break;

            default:
                generateNewProposals();
                break;
        }
    }

}
