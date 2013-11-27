/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.codeassistant;

import com.codenvy.ide.ext.java.jdt.Images;
import com.codenvy.ide.ext.java.jdt.codeassistant.api.JavaCompletionProposal;
import com.codenvy.ide.ext.java.jdt.codeassistant.ui.StyledString;
import com.codenvy.ide.ext.java.jdt.core.CompletionContext;
import com.codenvy.ide.ext.java.jdt.core.CompletionProposal;
import com.codenvy.ide.ext.java.jdt.core.CompletionRequestor;
import com.codenvy.ide.ext.java.jdt.core.Signature;
import com.codenvy.ide.ext.java.jdt.core.compiler.CharOperation;
import com.codenvy.ide.ext.java.jdt.core.compiler.IProblem;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.core.dom.ITypeBinding;
import com.codenvy.ide.ext.java.jdt.core.formatter.CodeFormatter;
import com.codenvy.ide.ext.java.jdt.core.util.TypeFinder;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.ReferenceBinding;
import com.codenvy.ide.ext.java.jdt.internal.corext.util.CodeFormatterUtil;
import com.codenvy.ide.runtime.Assert;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.util.loging.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Java UI implementation of <code>CompletionRequestor</code>. Produces {@link JavaCompletionProposal}s from the proposal
 * descriptors received via the <code>CompletionRequestor</code> interface.
 * <p>
 * The lifecycle of a <code>CompletionProposalCollector</code> instance is very simple:
 * <p/>
 * <pre>
 * ICompilationUnit unit= ...
 * int offset= ...
 *
 * CompletionProposalCollector collector= new CompletionProposalCollector(unit);
 * unit.codeComplete(offset, collector);
 * IJavaCompletionProposal[] proposals= collector.getJavaCompletionProposals();
 * String errorMessage= collector.getErrorMessage();
 *
 * &#x2f;&#x2f; display &#x2f; process proposals
 * </pre>
 * <p/>
 * Note that after a code completion operation, the collector will store any received proposals, which may require a considerable
 * amount of memory, so the collector should not be kept as a reference after a completion operation.
 * </p>
 * <p>
 * Clients may instantiate or subclass.
 * </p>
 */
public class CompletionProposalCollector extends CompletionRequestor {

    /** Tells whether this class is in debug mode. */
    private static final boolean DEBUG = false;
//"true".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.jdt.ui/debug/ResultCollector"));  //$NON-NLS-1$//$NON-NLS-2$

    /** Triggers for method proposals without parameters. Do not modify. */
    protected final static char[] METHOD_TRIGGERS = new char[]{';', ',', '.', '\t', '[', ' '};

    /** Triggers for method proposals. Do not modify. */
    protected final static char[] METHOD_WITH_ARGUMENTS_TRIGGERS = new char[]{'(', '-', ' '};

    /** Triggers for types. Do not modify. */
    protected final static char[] TYPE_TRIGGERS = new char[]{'.', '\t', '[', '(', ' '};

    /** Triggers for variables. Do not modify. */
    protected final static char[] VAR_TRIGGER = new char[]{'\t', ' ', '=', ';', '.'};

    private final CompletionProposalLabelProvider fLabelProvider = new CompletionProposalLabelProvider();

    private final List<JavaCompletionProposal> fJavaProposals = new ArrayList<JavaCompletionProposal>();

    private final List<JavaCompletionProposal> fKeywords = new ArrayList<JavaCompletionProposal>();

    private final Set<String> fSuggestedMethodNames = new HashSet<String>();

    private int fUserReplacementLength;

    private CompletionContext fContext;

    private IProblem fLastProblem;

    /* performance instrumentation */
    private long fStartTime;

    private long fUITime;

    private CompilationUnit fCompilationUnit;

    private int invocationOffset;

    /** The UI invocation context or <code>null</code>. */
    private JavaContentAssistInvocationContext fInvocationContext;

    private final Document document;

    private final String docContext;

    private final String projectId;

    private final String vfsId;

    public CompletionProposalCollector(CompilationUnit compilationUnit, boolean ignoreAll, Document document,
                                       int invocationOffset, String projectId, String docContext, String vfsId) {
        super(ignoreAll);
        this.document = document;
        this.invocationOffset = invocationOffset;
        fCompilationUnit = compilationUnit;
        this.projectId = projectId;
        this.docContext = docContext;
        this.vfsId = vfsId;

        fUserReplacementLength = -1;
        if (!ignoreAll) {
            setRequireExtendedContext(true);
        }
        getInvocationContext();
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.jdt.core.CompletionRequestor#setIgnored(int, boolean)
     */
    @Override
    public void setIgnored(int completionProposalKind, boolean ignore) {
        super.setIgnored(completionProposalKind, ignore);
        if (completionProposalKind == CompletionProposal.METHOD_DECLARATION && !ignore) {
            setRequireExtendedContext(true);
        }
    }

    /**
     * Sets the invocation context.
     * <p>
     * Subclasses may extend.
     * </p>
     *
     * @param context
     *         the invocation context
     * @see #getInvocationContext()
     * @since 3.2
     */
    public void setInvocationContext(JavaContentAssistInvocationContext context) {
        Assert.isNotNull(context);
        fInvocationContext = context;
        context.setCollector(this);
    }

    /**
     * Returns the invocation context. If none has been set via {@link #setInvocationContext(JavaContentAssistInvocationContext)},
     * a new one is created.
     *
     * @return invocationContext the invocation context
     * @since 3.2
     */
    public final JavaContentAssistInvocationContext getInvocationContext() {
        if (fInvocationContext == null) {
            setInvocationContext(new JavaContentAssistInvocationContext(fCompilationUnit, document, invocationOffset,
                                                                        projectId, docContext, vfsId));
            fInvocationContext.setCollector(this);
        }

        return fInvocationContext;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Subclasses may replace, but usually should not need to. Consider replacing
     * {@linkplain #createJavaCompletionProposal(CompletionProposal) createJavaCompletionProposal} instead.
     * </p>
     */
    @Override
    public void accept(CompletionProposal proposal) {
        long start = DEBUG ? System.currentTimeMillis() : 0;
        try {
            if (isFiltered(proposal))
                return;

            if (proposal.getKind() == CompletionProposal.POTENTIAL_METHOD_DECLARATION) {
                acceptPotentialMethodDeclaration(proposal);
            } else {
                JavaCompletionProposal javaProposal = createJavaCompletionProposal(proposal);
                if (javaProposal != null) {
                    fJavaProposals.add(javaProposal);
                    if (proposal.getKind() == CompletionProposal.KEYWORD)
                        fKeywords.add(javaProposal);
                }
            }
        } catch (IllegalArgumentException e) {
            // all signature processing method may throw IAEs
            // https://bugs.eclipse.org/bugs/show_bug.cgi?id=84657
            // don't abort, but log and show all the valid proposals
            Log.error(getClass(), e);
        }

        if (DEBUG)
            fUITime += System.currentTimeMillis() - start;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Subclasses may extend, but usually should not need to.
     * </p>
     *
     * @see #getContext()
     */
    @Override
    public void acceptContext(CompletionContext context) {
        fContext = context;
        fLabelProvider.setContext(context);
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Subclasses may extend, but must call the super implementation.
     */
    @Override
    public void beginReporting() {
        if (DEBUG) {
            fStartTime = System.currentTimeMillis();
            fUITime = 0;
        }

        fLastProblem = null;
        fJavaProposals.clear();
        fKeywords.clear();
        fSuggestedMethodNames.clear();
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Subclasses may extend, but must call the super implementation.
     */
    @Override
    public void completionFailure(IProblem problem) {
        fLastProblem = problem;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * Subclasses may extend, but must call the super implementation.
     */
    @Override
    public void endReporting() {
        if (DEBUG) {
            long total = System.currentTimeMillis() - fStartTime;
            System.err.println("Core Collector (core):\t" + (total - fUITime)); //NOSONAR //$NON-NLS-1$
            System.err.println("Core Collector (ui):\t" + fUITime); //NOSONAR //$NON-NLS-1$
        }
    }

    /**
     * Returns an error message about any error that may have occurred during code completion, or the empty string if none.
     * <p>
     * Subclasses may replace or extend.
     * </p>
     *
     * @return an error message or the empty string
     */
    public String getErrorMessage() {
        if (fLastProblem != null)
            return fLastProblem.getMessage();
        return ""; //$NON-NLS-1$
    }

    /**
     * Returns the unsorted list of received proposals.
     *
     * @return the unsorted list of received proposals
     */
    public final JavaCompletionProposal[] getJavaCompletionProposals() {
        return fJavaProposals.toArray(new JavaCompletionProposal[fJavaProposals.size()]);
    }

    /**
     * Returns the unsorted list of received keyword proposals.
     *
     * @return the unsorted list of received keyword proposals
     */
    public final JavaCompletionProposal[] getKeywordCompletionProposals() {
        return fKeywords.toArray(new JavaCompletionProposal[fKeywords.size()]);
    }

    /**
     * If the replacement length is set, it overrides the length returned from the content assist infrastructure. Use this setting
     * if code assist is called with a none empty selection.
     *
     * @param length
     *         the new replacement length, relative to the code assist offset. Must be equal to or greater than zero.
     */
    public final void setReplacementLength(int length) {
        Assert.isLegal(length >= 0);
        fUserReplacementLength = length;
    }

    /**
     * Computes the relevance for a given <code>CompletionProposal</code>.
     * <p>
     * Subclasses may replace, but usually should not need to.
     * </p>
     *
     * @param proposal
     *         the proposal to compute the relevance for
     * @return the relevance for <code>proposal</code>
     */
    protected int computeRelevance(CompletionProposal proposal) {
        final int baseRelevance = proposal.getRelevance() * 16;
        switch (proposal.getKind()) {
            case CompletionProposal.PACKAGE_REF:
                return baseRelevance + 0;
            case CompletionProposal.LABEL_REF:
                return baseRelevance + 1;
            case CompletionProposal.KEYWORD:
                return baseRelevance + 2;
            case CompletionProposal.TYPE_REF:
            case CompletionProposal.ANONYMOUS_CLASS_DECLARATION:
            case CompletionProposal.ANONYMOUS_CLASS_CONSTRUCTOR_INVOCATION:
                return baseRelevance + 3;
            case CompletionProposal.METHOD_REF:
            case CompletionProposal.CONSTRUCTOR_INVOCATION:
            case CompletionProposal.METHOD_NAME_REFERENCE:
            case CompletionProposal.METHOD_DECLARATION:
            case CompletionProposal.ANNOTATION_ATTRIBUTE_REF:
                return baseRelevance + 4;
            case CompletionProposal.POTENTIAL_METHOD_DECLARATION:
                return baseRelevance + 4 /* + 99 */;
            case CompletionProposal.FIELD_REF:
                return baseRelevance + 5;
            case CompletionProposal.LOCAL_VARIABLE_REF:
            case CompletionProposal.VARIABLE_DECLARATION:
                return baseRelevance + 6;
            default:
                return baseRelevance;
        }
    }

    /**
     * Creates a new java completion proposal from a core proposal. This may involve computing the display label and setting up
     * some context.
     * <p>
     * This method is called for every proposal that will be displayed to the user, which may be hundreds. Implementations should
     * therefore defer as much work as possible: Labels should be computed lazily to leverage virtual table usage, and any
     * information only needed when <em>applying</em> a proposal should not be computed yet.
     * </p>
     * <p>
     * Implementations may return <code>null</code> if a proposal should not be included in the list presented to the user.
     * </p>
     * <p>
     * Subclasses may extend or replace this method.
     * </p>
     *
     * @param proposal
     *         the core completion proposal to create a UI proposal for
     * @return the created java completion proposal, or <code>null</code> if no proposal should be displayed
     */
    protected JavaCompletionProposal createJavaCompletionProposal(CompletionProposal proposal) {
        switch (proposal.getKind()) {
            case CompletionProposal.KEYWORD:
                return createKeywordProposal(proposal);
            case CompletionProposal.PACKAGE_REF:
                return createPackageProposal(proposal);
            case CompletionProposal.TYPE_REF:
                return createTypeProposal(proposal);
            case CompletionProposal.JAVADOC_TYPE_REF:
                return createJavadocLinkTypeProposal(proposal);
            case CompletionProposal.FIELD_REF:
            case CompletionProposal.JAVADOC_FIELD_REF:
            case CompletionProposal.JAVADOC_VALUE_REF:
                return createFieldProposal(proposal);
            case CompletionProposal.FIELD_REF_WITH_CASTED_RECEIVER:
                return createFieldWithCastedReceiverProposal(proposal);
            case CompletionProposal.METHOD_REF:
            case CompletionProposal.CONSTRUCTOR_INVOCATION:
            case CompletionProposal.METHOD_REF_WITH_CASTED_RECEIVER:
            case CompletionProposal.METHOD_NAME_REFERENCE:
            case CompletionProposal.JAVADOC_METHOD_REF:
                return createMethodReferenceProposal(proposal);
            case CompletionProposal.METHOD_DECLARATION:
                return createMethodDeclarationProposal(proposal);
            case CompletionProposal.ANONYMOUS_CLASS_CONSTRUCTOR_INVOCATION:
                return createAnonymousTypeProposal(proposal, getInvocationContext());
            case CompletionProposal.ANONYMOUS_CLASS_DECLARATION:
                return createAnonymousTypeProposal(proposal, getInvocationContext());
            case CompletionProposal.LABEL_REF:
                return createLabelProposal(proposal);
            case CompletionProposal.LOCAL_VARIABLE_REF:
            case CompletionProposal.VARIABLE_DECLARATION:
                return createLocalVariableProposal(proposal);
            case CompletionProposal.ANNOTATION_ATTRIBUTE_REF:
                return createAnnotationAttributeReferenceProposal(proposal);
            case CompletionProposal.JAVADOC_BLOCK_TAG:
            case CompletionProposal.JAVADOC_PARAM_REF:
                return createJavadocSimpleProposal(proposal);
            case CompletionProposal.JAVADOC_INLINE_TAG:
                return createJavadocInlineTagProposal(proposal);
            case CompletionProposal.POTENTIAL_METHOD_DECLARATION:
            default:
                return null;
        }
    }

    // /**
    // * Creates the context information for a given method reference proposal.
    // * The passed proposal must be of kind {@link CompletionProposal#METHOD_REF}.
    // *
    // * @param methodProposal the method proposal for which to create context information
    // * @return the context information for <code>methodProposal</code>
    // */
    // protected final IContextInformation createMethodContextInformation(CompletionProposal methodProposal) {
    // Assert.isTrue(methodProposal.getKind() == CompletionProposal.METHOD_REF);
    // return new ProposalContextInformation(methodProposal);
    // }

    // /**
    // * Returns the compilation unit that the receiver operates on, or
    // * <code>null</code> if the <code>IJavaProject</code> constructor was
    // * used to create the receiver.
    // *
    // * @return the compilation unit that the receiver operates on, or
    // * <code>null</code>
    // */
    // protected final ICompilationUnit getCompilationUnit() {
    // return fCompilationUnit;
    // }

    /**
     * Returns the <code>CompletionContext</code> for this completion operation.
     *
     * @return the <code>CompletionContext</code> for this completion operation
     * @see CompletionRequestor#acceptContext(CompletionContext)
     */
    protected final CompletionContext getContext() {
        return fContext;
    }

    /**
     * Returns a cached image for the given descriptor.
     *
     * @param descriptor
     *         the image descriptor to get an image for, may be <code>null</code>
     * @return the image corresponding to <code>descriptor</code>
     */
    protected final Images getImage(Images descriptor) {
//        return new Images(descriptor);
        return descriptor;
    }

    /**
     * Returns the proposal label provider used by the receiver.
     *
     * @return the proposal label provider used by the receiver
     */
    protected final CompletionProposalLabelProvider getLabelProvider() {
        return fLabelProvider;
    }

    /**
     * Returns the replacement length of a given completion proposal. The replacement length is usually the difference between the
     * return values of <code>proposal.getReplaceEnd</code> and <code>proposal.getReplaceStart</code>, but this behavior may be
     * overridden by calling {@link #setReplacementLength(int)}.
     *
     * @param proposal
     *         the completion proposal to get the replacement length for
     * @return the replacement length for <code>proposal</code>
     */
    protected final int getLength(CompletionProposal proposal) {
        int start = proposal.getReplaceStart();
        int end = proposal.getReplaceEnd();
        int length;
        if (fUserReplacementLength == -1) {
            length = end - start;
        } else {
            length = fUserReplacementLength;
            // extend length to begin at start
            int behindCompletion = proposal.getCompletionLocation() + 1;
            if (start < behindCompletion) {
                length += behindCompletion - start;
            }
        }
        return length;
    }

    /**
     * Returns <code>true</code> if <code>proposal</code> is filtered, e.g. should not be proposed to the user, <code>false</code>
     * if it is valid.
     * <p>
     * Subclasses may extends this method. The default implementation filters proposals set to be ignored via
     * {@linkplain CompletionRequestor#setIgnored(int, boolean) setIgnored} and types set to be ignored in the preferences.
     * </p>
     *
     * @param proposal
     *         the proposal to filter
     * @return <code>true</code> to filter <code>proposal</code>, <code>false</code> to let it pass
     */
    protected boolean isFiltered(CompletionProposal proposal) {
        if (isIgnored(proposal.getKind()))
            return true;
        //       char[] declaringType = getDeclaringType(proposal);
        // TODO
        // return declaringType != null; //&& TypeFilter.isFiltered(declaringType);
        return false;
    }

    /**
     * Returns the type signature of the declaring type of a <code>CompletionProposal</code>, or <code>null</code> for proposals
     * that do not have a declaring type. The return value is <em>not</em> <code>null</code> for proposals of the following kinds:
     * <ul>
     * <li>METHOD_DECLARATION</li>
     * <li>METHOD_NAME_REFERENCE</li>
     * <li>METHOD_REF</li>
     * <li>ANNOTATION_ATTRIBUTE_REF</li>
     * <li>POTENTIAL_METHOD_DECLARATION</li>
     * <li>ANONYMOUS_CLASS_DECLARATION</li>
     * <li>FIELD_REF</li>
     * <li>PACKAGE_REF (returns the package, but no type)</li>
     * <li>TYPE_REF</li>
     * </ul>
     *
     * @param proposal
     *         the completion proposal to get the declaring type for
     * @return the type signature of the declaring type, or <code>null</code> if there is none
     * @see Signature#toCharArray(char[])
     */
    protected final char[] getDeclaringType(CompletionProposal proposal) {
        switch (proposal.getKind()) {
            case CompletionProposal.METHOD_DECLARATION:
            case CompletionProposal.METHOD_NAME_REFERENCE:
            case CompletionProposal.JAVADOC_METHOD_REF:
            case CompletionProposal.METHOD_REF:
            case CompletionProposal.CONSTRUCTOR_INVOCATION:
            case CompletionProposal.ANONYMOUS_CLASS_CONSTRUCTOR_INVOCATION:
            case CompletionProposal.METHOD_REF_WITH_CASTED_RECEIVER:
            case CompletionProposal.ANNOTATION_ATTRIBUTE_REF:
            case CompletionProposal.POTENTIAL_METHOD_DECLARATION:
            case CompletionProposal.ANONYMOUS_CLASS_DECLARATION:
            case CompletionProposal.FIELD_REF:
            case CompletionProposal.FIELD_REF_WITH_CASTED_RECEIVER:
            case CompletionProposal.JAVADOC_FIELD_REF:
            case CompletionProposal.JAVADOC_VALUE_REF:
                char[] declaration = proposal.getDeclarationSignature();
                // special methods may not have a declaring type: methods defined on arrays etc.
                // Currently known: class literals don't have a declaring type - use Object
                if (declaration == null)
                    return "java.lang.Object".toCharArray(); //$NON-NLS-1$
                return Signature.toCharArray(declaration);
            case CompletionProposal.PACKAGE_REF:
                return proposal.getDeclarationSignature();
            case CompletionProposal.JAVADOC_TYPE_REF:
            case CompletionProposal.TYPE_REF:
                return Signature.toCharArray(proposal.getSignature());
            case CompletionProposal.LOCAL_VARIABLE_REF:
            case CompletionProposal.VARIABLE_DECLARATION:
            case CompletionProposal.KEYWORD:
            case CompletionProposal.LABEL_REF:
            case CompletionProposal.JAVADOC_BLOCK_TAG:
            case CompletionProposal.JAVADOC_INLINE_TAG:
            case CompletionProposal.JAVADOC_PARAM_REF:
                return null;
            default:
                Assert.isTrue(false);
                return null;
        }
    }

    private void acceptPotentialMethodDeclaration(CompletionProposal proposal) {

        TypeFinder finder = new TypeFinder(proposal.getCompletionLocation());
        fCompilationUnit.accept(finder);
        if (finder.type != null) {
            ITypeBinding type = finder.type.resolveBinding();
            String prefix = String.valueOf(proposal.getName());
            int completionStart = proposal.getReplaceStart();
            int completionEnd = proposal.getReplaceEnd();
            int relevance = computeRelevance(proposal);

            GetterSetterCompletionProposal.evaluateProposals(type, prefix, completionStart, completionEnd
                                                                                            - completionStart, relevance + 2,
                                                             fSuggestedMethodNames, fJavaProposals, getInvocationContext());
            MethodDeclarationCompletionProposal.evaluateProposals(finder.type, prefix, completionStart, completionEnd
                                                                                                        - completionStart, relevance,
                                                                  fSuggestedMethodNames, fJavaProposals, getInvocationContext());
        }

    }

    private JavaCompletionProposal createAnnotationAttributeReferenceProposal(CompletionProposal proposal) {
        StyledString displayString = fLabelProvider.createLabelWithTypeAndDeclaration(proposal);
        Images descriptor = fLabelProvider.createMethodImageDescriptor(proposal);
        String completion = String.valueOf(proposal.getCompletion());
        JavaCompletionProposal javaProposal =
                new JavaCompletionProposalImpl(completion, proposal.getReplaceStart(), getLength(proposal),
                                               getImage(descriptor), displayString, computeRelevance(proposal), false, fInvocationContext);
        // TODO
        // if (fJavaProject != null)
        // javaProposal.setProposalInfo(new AnnotationAtttributeProposalInfo(fJavaProject, proposal));
        return javaProposal;
    }

    private JavaCompletionProposal createAnonymousTypeProposal(CompletionProposal proposal,
                                                               JavaContentAssistInvocationContext invocationContext) {
        if (fCompilationUnit == null)
            return null;

        char[] declarationKey = proposal.getDeclarationKey();
        if (declarationKey == null)
            return null;

        String completion = String.valueOf(proposal.getCompletion());
        int start = proposal.getReplaceStart();
        int length = getLength(proposal);
        int relevance = computeRelevance(proposal);

        StyledString label = fLabelProvider.createAnonymousTypeLabel(proposal);

        char[] typeErasure = Signature.getTypeErasure(declarationKey);
        CharOperation.replace(typeErasure, '/', '.');
        ReferenceBinding referenceBinding =
                lookupEnvironment.askForType(CharOperation.splitOn('.', Signature.toCharArray(typeErasure)));
        if (referenceBinding == null)
            return null;
        ITypeBinding typeBinding = fCompilationUnit.getAST().getBindingResolver().getTypeBinding(referenceBinding);
        if (typeBinding == null)
            return null;
        JavaCompletionProposalImpl javaProposal =
                new AnonymousTypeCompletionProposal(invocationContext, start, length, completion, label,
                                                    String.valueOf(proposal.getDeclarationSignature()), typeBinding, relevance);
        javaProposal.setProposalInfo(new AnonymousTypeProposalInfo(proposal, projectId, docContext, vfsId));
        return javaProposal;
    }

    private JavaCompletionProposal createFieldProposal(CompletionProposal proposal) {
        String completion = String.valueOf(proposal.getCompletion());
        int start = proposal.getReplaceStart();
        int length = getLength(proposal);
        StyledString label = fLabelProvider.createStyledLabel(proposal);
        Images image = getImage(CompletionProposalLabelProvider.createFieldImageDescriptor(proposal.getFlags()));
        int relevance = computeRelevance(proposal);

        JavaCompletionProposalImpl javaProposal =
                new JavaCompletionProposalImpl(completion, start, length, image, label, relevance, getContext().isInJavadoc(),
                                               getInvocationContext());
        // if (fJavaProject != null)
        javaProposal.setProposalInfo(new FieldProposalInfo(proposal, projectId, docContext, vfsId));

        javaProposal.setTriggerCharacters(VAR_TRIGGER);

        return javaProposal;
    }

    /**
     * Creates the Java completion proposal for the JDT Core {@link CompletionProposal#FIELD_REF_WITH_CASTED_RECEIVER} proposal.
     *
     * @param proposal
     *         the JDT Core proposal
     * @return the Java completion proposal
     * @since 3.4
     */
    private JavaCompletionProposal createFieldWithCastedReceiverProposal(CompletionProposal proposal) {
        String completion = String.valueOf(proposal.getCompletion());
        completion = CodeFormatterUtil.format(CodeFormatter.K_EXPRESSION, completion, 0, "\n"); //$NON-NLS-1$
        int start = proposal.getReplaceStart();
        int length = getLength(proposal);
        StyledString label = fLabelProvider.createStyledLabel(proposal);
        Images image = getImage(CompletionProposalLabelProvider.createFieldImageDescriptor(proposal.getFlags()));
        int relevance = computeRelevance(proposal);

        JavaCompletionProposalImpl javaProposal =
                new JavaFieldWithCastedReceiverCompletionProposal(completion, start, length, image, label, relevance,
                                                                  getContext().isInJavadoc(), getInvocationContext(), proposal);
        // if (fJavaProject != null)
        javaProposal.setProposalInfo(new FieldProposalInfo(proposal, projectId, docContext, vfsId));

        javaProposal.setTriggerCharacters(VAR_TRIGGER);

        return javaProposal;
    }

    private JavaCompletionProposal createJavadocSimpleProposal(CompletionProposal javadocProposal) {
        // TODO do better with javadoc proposals
        // String completion= String.valueOf(proposal.getCompletion());
        // int start= proposal.getReplaceStart();
        // int length= getLength(proposal);
        // String label= fLabelProvider.createSimpleLabel(proposal);
        // Image image= getImage(fLabelProvider.createImageDescriptor(proposal));
        // int relevance= computeRelevance(proposal);
        //
        // JavaCompletionProposal javaProposal= new JavaCompletionProposal(completion, start, length, image, label, relevance);
        // if (fJavaProject != null)
        // javaProposal.setProposalInfo(new FieldProposalInfo(fJavaProject, proposal));
        //
        // javaProposal.setTriggerCharacters(VAR_TRIGGER);
        //
        // return javaProposal;
        LazyJavaCompletionProposal proposal = new LazyJavaCompletionProposal(javadocProposal, getInvocationContext());
        // adaptLength(proposal, javadocProposal);
        return proposal;
    }

    private JavaCompletionProposal createJavadocInlineTagProposal(CompletionProposal javadocProposal) {
        LazyJavaCompletionProposal proposal =
                new JavadocInlineTagCompletionProposal(javadocProposal, getInvocationContext());
        adaptLength(proposal, javadocProposal);
        return proposal;
    }

    private JavaCompletionProposal createKeywordProposal(CompletionProposal proposal) {
        String completion = String.valueOf(proposal.getCompletion());
        int start = proposal.getReplaceStart();
        int length = getLength(proposal);
        StyledString label = fLabelProvider.createSimpleLabel(proposal);
        int relevance = computeRelevance(proposal);
        return new JavaCompletionProposalImpl(completion, start, length, null, label, relevance, false,
                                              fInvocationContext);
    }

    private JavaCompletionProposal createLabelProposal(CompletionProposal proposal) {
        String completion = String.valueOf(proposal.getCompletion());
        int start = proposal.getReplaceStart();
        int length = getLength(proposal);
        StyledString label = fLabelProvider.createSimpleLabel(proposal);
        int relevance = computeRelevance(proposal);

        return new JavaCompletionProposalImpl(completion, start, length, null, label, relevance, false,
                                              fInvocationContext);
    }

    private JavaCompletionProposal createLocalVariableProposal(CompletionProposal proposal) {
        String completion = String.valueOf(proposal.getCompletion());
        int start = proposal.getReplaceStart();
        int length = getLength(proposal);
        Images image = getImage(fLabelProvider.createLocalImageDescriptor(proposal));
        StyledString label = fLabelProvider.createSimpleLabelWithType(proposal);
        int relevance = computeRelevance(proposal);
        final JavaCompletionProposalImpl javaProposal =
                new JavaCompletionProposalImpl(completion, start, length, image, label, relevance, false, fInvocationContext);
        javaProposal.setTriggerCharacters(VAR_TRIGGER);
        return javaProposal;
    }

    private JavaCompletionProposal createMethodDeclarationProposal(CompletionProposal proposal) {
        if (fCompilationUnit == null)
            return null;

        String name = String.valueOf(proposal.getName());
        String[] paramTypes = Signature.getParameterTypes(String.valueOf(proposal.getSignature()));
        for (int index = 0; index < paramTypes.length; index++)
            paramTypes[index] = Signature.toString(paramTypes[index]);
        int start = proposal.getReplaceStart();
        int length = getLength(proposal);

        StyledString label = fLabelProvider.createOverrideMethodProposalLabel(proposal);

        JavaCompletionProposalImpl javaProposal =
                new OverrideCompletionProposal(name, paramTypes, start, length, label,
                                               String.valueOf(proposal.getCompletion()), fInvocationContext);
        javaProposal.setImage(getImage(fLabelProvider.createMethodImageDescriptor(proposal)));
        javaProposal.setProposalInfo(new MethodProposalInfo(proposal, projectId, docContext, vfsId));
        javaProposal.setRelevance(computeRelevance(proposal));

        fSuggestedMethodNames.add(new String(name));
        return javaProposal;
    }

    private JavaCompletionProposal createMethodReferenceProposal(CompletionProposal methodProposal) {
        LazyJavaCompletionProposal proposal = new JavaMethodCompletionProposal(methodProposal, getInvocationContext());
        adaptLength(proposal, methodProposal);
        return proposal;
    }

    private void adaptLength(LazyJavaCompletionProposal proposal, CompletionProposal coreProposal) {
        if (fUserReplacementLength != -1) {
            proposal.setReplacementLength(getLength(coreProposal));
        }
    }

    private JavaCompletionProposal createPackageProposal(CompletionProposal proposal) {
        String completion = String.valueOf(proposal.getCompletion());
        int start = proposal.getReplaceStart();
        int length = getLength(proposal);
        StyledString label = fLabelProvider.createSimpleLabel(proposal);
        Images image = getImage(fLabelProvider.createPackageImageDescriptor(proposal));
        int relevance = computeRelevance(proposal);

        return new JavaCompletionProposalImpl(completion, start, length, image, label, relevance, false,
                                              fInvocationContext);
    }

    private JavaCompletionProposal createTypeProposal(CompletionProposal typeProposal) {
        LazyJavaCompletionProposal proposal = new LazyJavaTypeCompletionProposal(typeProposal, getInvocationContext());
        adaptLength(proposal, typeProposal);
        return proposal;
    }

    private JavaCompletionProposal createJavadocLinkTypeProposal(CompletionProposal typeProposal) {
        LazyJavaCompletionProposal proposal = new JavadocLinkTypeCompletionProposal(typeProposal, getInvocationContext());
        adaptLength(proposal, typeProposal);
        return proposal;
    }
}
