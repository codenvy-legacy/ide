/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     IBM Corporation - added the following constants:
 *                                 COMPILER_PB_DEPRECATION_IN_DEPRECATED_CODE
 *                                 COMPILER_PB_STATIC_ACCESS_RECEIVER
 *                                 COMPILER_TASK_TAGS
 *                                 CORE_CIRCULAR_CLASSPATH
 *                                 CORE_INCOMPLETE_CLASSPATH
 *     IBM Corporation - added run(IWorkspaceRunnable, IProgressMonitor)
 *     IBM Corporation - added exclusion patterns to source classpath entries
 *     IBM Corporation - added specific output location to source classpath entries
 *     IBM Corporation - added the following constants:
 *                                 CORE_JAVA_BUILD_CLEAN_OUTPUT_FOLDER
 *                                 CORE_JAVA_BUILD_RECREATE_MODIFIED_CLASS_FILES_IN_OUTPUT_FOLDER
 *                                 CLEAN
 *     IBM Corporation - added getClasspathContainerInitializer(String)
 *     IBM Corporation - added the following constants:
 *                                 CODEASSIST_ARGUMENT_PREFIXES
 *                                 CODEASSIST_ARGUMENT_SUFFIXES
 *                                 CODEASSIST_FIELD_PREFIXES
 *                                 CODEASSIST_FIELD_SUFFIXES
 *                                 CODEASSIST_LOCAL_PREFIXES
 *                                 CODEASSIST_LOCAL_SUFFIXES
 *                                 CODEASSIST_STATIC_FIELD_PREFIXES
 *                                 CODEASSIST_STATIC_FIELD_SUFFIXES
 *                                 COMPILER_PB_CHAR_ARRAY_IN_STRING_CONCATENATION
 *     IBM Corporation - added the following constants:
 *                                 COMPILER_PB_LOCAL_VARIABLE_HIDING
 *                                 COMPILER_PB_SPECIAL_PARAMETER_HIDING_FIELD
 *                                 COMPILER_PB_FIELD_HIDING
 *                                 COMPILER_PB_POSSIBLE_ACCIDENTAL_BOOLEAN_ASSIGNMENT
 *                                 CORE_INCOMPATIBLE_JDK_LEVEL
 *                                 VERSION_1_5
 *                                 COMPILER_PB_EMPTY_STATEMENT
 *     IBM Corporation - added the following constants:
 *                                 COMPILER_PB_INDIRECT_STATIC_ACCESS
 *                                 COMPILER_PB_BOOLEAN_METHOD_THROWING_EXCEPTION
 *                                 COMPILER_PB_UNNECESSARY_CAST
 *     IBM Corporation - added the following constants:
 *                                 COMPILER_PB_INVALID_JAVADOC
 *                                 COMPILER_PB_INVALID_JAVADOC_TAGS
 *                                 COMPILER_PB_INVALID_JAVADOC_TAGS_VISIBILITY
 *                                 COMPILER_PB_MISSING_JAVADOC_TAGS
 *                                 COMPILER_PB_MISSING_JAVADOC_TAGS_VISIBILITY
 *                                 COMPILER_PB_MISSING_JAVADOC_TAGS_OVERRIDING
 *                                 COMPILER_PB_MISSING_JAVADOC_COMMENTS
 *                                 COMPILER_PB_MISSING_JAVADOC_COMMENTS_VISIBILITY
 *                                 COMPILER_PB_MISSING_JAVADOC_COMMENTS_OVERRIDING
 *                                 COMPILER_PB_DEPRECATION_WHEN_OVERRIDING_DEPRECATED_METHOD
 *                                 COMPILER_PB_UNUSED_DECLARED_THROWN_EXCEPTION_WHEN_OVERRIDING
 *     IBM Corporation - added the following constants:
 *                                 TIMEOUT_FOR_PARAMETER_NAME_FROM_ATTACHED_JAVADOC
 *     IBM Corporation - added the following constants:
 *                                 COMPILER_PB_FALLTHROUGH_CASE
 *                                 COMPILER_PB_PARAMETER_ASSIGNMENT
 *                                 COMPILER_PB_NULL_REFERENCE
 *     IBM Corporation - added the following constants:
 *                                 CODEASSIST_DEPRECATION_CHECK
 *     IBM Corporation - added the following constants:
 *                                 COMPILER_PB_POTENTIAL_NULL_REFERENCE
 *                                 COMPILER_PB_REDUNDANT_NULL_CHECK
 *     IBM Corporation - added the following constants:
 *                                 COMPILER_PB_UNUSED_PARAMETER_INCLUDE_DOC_COMMENT_REFERENCE
 *     IBM Corporation - added the following constants:
 *                                 COMPILER_PB_UNUSED_DECLARED_THROWN_EXCEPTION_INCLUDE_DOC_COMMENT_REFERENCE
 *     IBM Corporation - added the following constants:
 *                                 COMPILER_PB_MISSING_JAVADOC_TAG_DESCRIPTION
 *                           COMPILER_PB_MISSING_JAVADOC_TAG_DESCRIPTION_NO_TAG
 *                           COMPILER_PB_MISSING_JAVADOC_TAG_DESCRIPTION_RETURN_TAG
 *                           COMPILER_PB_MISSING_JAVADOC_TAG_DESCRIPTION_ALL_TAGS
 *     IBM Corporation - added the following constants:
 *                                 COMPILER_PB_REDUNDANT_SUPERINTERFACE
 *     IBM Corporation - added the following constant:
 *                                 COMPILER_PB_UNUSED_DECLARED_THROWN_EXCEPTION_EXEMPT_EXCEPTION_AND_THROWABLE
 *     IBM Corporation - added getOptionForConfigurableSeverity(int)
 *     Benjamin Muskalla - added COMPILER_PB_MISSING_SYNCHRONIZED_ON_INHERITED_METHOD
 *     Stephan Herrmann  - added COMPILER_PB_UNUSED_OBJECT_ALLOCATION
 *     Stephan Herrmann  - added COMPILER_PB_SUPPRESS_OPTIONAL_ERRORS
 *******************************************************************************/

package com.codenvy.ide.ext.java.worker.core;

import com.codenvy.ide.ext.java.worker.WorkerMessageHandler;
import com.codenvy.ide.ext.java.worker.core.compiler.IProblem;
import com.codenvy.ide.ext.java.worker.internal.compiler.ClassFileConstants;
import com.codenvy.ide.ext.java.worker.internal.compiler.impl.CompilerOptions;
import com.codenvy.ide.ext.java.worker.internal.compiler.problem.ProblemReporter;

import java.util.HashMap;
import java.util.Map;

/**
 * The plug-in runtime class for the Java model plug-in containing the core (UI-free) support for Java projects.
 * <p>
 * Like all plug-in runtime classes (subclasses of <code>Plugin</code>), this class is automatically instantiated by the platform
 * when the plug-in gets activated. Clients must not attempt to instantiate plug-in runtime classes directly.
 * </p>
 * <p>
 * The single instance of this class can be accessed from any plug-in declaring the Java model plug-in as a prerequisite via
 * <code>JavaCore.getJavaCore()</code>. The Java model plug-in will be activated automatically if not already active.
 * </p>
 *
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public final class JavaCore {

    /** The plug-in identifier of the Java core support (value <code>"org.eclipse.jdt.core"</code>). */
    public static final String PLUGIN_ID = "org.eclipse.jdt.core"; //$NON-NLS-1$

    // Begin configurable option IDs {

    /**
     * Compiler option ID: Defining Target Java Platform.
     * <p/>
     * For binary compatibility reason, .class files can be tagged to with certain VM versions and later.
     * <p/>
     * Note that <code>"1.4"</code> target requires to toggle compliance mode to <code>"1.4"</code>, <code>"1.5"</code> target
     * requires to toggle compliance mode to <code>"1.5"</code>, <code>"1.6"</code> target requires to toggle compliance mode to
     * <code>"1.6"</code> and <code>"1.7"</code> target requires to toggle compliance mode to <code>"1.7"</code>.
     * <code>"cldc1.1"</code> requires the source version to be <code>"1.3"</code> and the compliance version to be
     * <code>"1.4"</code> or lower.
     * <dl>
     * <dt>Option id:</dt>
     * <dd><code>"org.eclipse.jdt.core.compiler.codegen.targetPlatform"</code></dd>
     * <dt>Possible values:</dt>
     * <dd>
     * <code>{ "1.1", "1.2", "1.3", "1.4", "1.5", "1.6", "1.7", "cldc1.1" }</code></dd>
     * <dt>Default:</dt>
     * <dd><code>"1.2"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     */
    public static final String COMPILER_CODEGEN_TARGET_PLATFORM = PLUGIN_ID + ".compiler.codegen.targetPlatform"; //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Unused Parameter.
     * <p>When enabled, the compiler will issue an error or a warning for unused method
     * parameters (that is, parameters never read from).
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.unusedParameter"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt><dd><code>"ignore"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     */
    public static final String COMPILER_PB_UNUSED_PARAMETER = PLUGIN_ID + ".compiler.problem.unusedParameter"; //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Assignment with no Effect.
     * <p>When enabled, the compiler will issue an error or a warning whenever an assignment
     * has no effect (e.g <code>'x = x'</code>).
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.noEffectAssignment"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt><dd><code>"warning"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 2.1
     */
    public static final String COMPILER_PB_NO_EFFECT_ASSIGNMENT = PLUGIN_ID + ".compiler.problem.noEffectAssignment"; //$NON-NLS-1$

    /**
     * Compiler option ID: Inline JSR Bytecode Instruction.
     * <p/>
     * When enabled, the compiler will no longer generate JSR instructions, but rather inline corresponding subroutine code
     * sequences (mostly corresponding to try finally blocks). The generated code will thus get bigger, but will load faster on
     * virtual machines since the verification process is then much simpler.
     * <p/>
     * This mode is anticipating support for the Java Specification Request 202.
     * <p/>
     * Note that JSR inlining is optional only for target platform lesser than 1.5. From 1.5 on, the JSR inlining is mandatory
     * (also see related setting {@link #COMPILER_CODEGEN_TARGET_PLATFORM}).
     * <dl>
     * <dt>Option id:</dt>
     * <dd><code>"org.eclipse.jdt.core.compiler.codegen.inlineJsrBytecode"</code></dd>
     * <dt>Possible values:</dt>
     * <dd><code>{ "enabled", "disabled" }</code></dd>
     * <dt>Default:</dt>
     * <dd><code>"disabled"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 3.0
     */
    public static final String COMPILER_CODEGEN_INLINE_JSR_BYTECODE = PLUGIN_ID + ".compiler.codegen.inlineJsrBytecode"; //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Unnecessary Type Check.
     * <p>When enabled, the compiler will issue an error or a warning when a cast or an <code>instanceof</code> operation
     * is unnecessary.
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.unnecessaryTypeCheck"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt><dd><code>"ignore"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 3.0
     */
    public static final String COMPILER_PB_UNNECESSARY_TYPE_CHECK = PLUGIN_ID + ".compiler.problem.unnecessaryTypeCheck"; //$NON-NLS-1$

    /**
     * Compiler option ID: Consider Reference in Doc Comment for Unused Parameter Check.
     * <p>When enabled, the compiler will consider doc comment references to parameters (i.e. <code>@param</code> clauses) for the unused
     * parameter check. Thus, documented parameters will be considered as mandated as per doc contract.
     * <p>The severity of the unused parameter problem is controlled with option {@link #COMPILER_PB_UNUSED_PARAMETER}.
     * <p>Note: this option has no effect until the doc comment support is enabled according to the
     * option {@link #COMPILER_DOC_COMMENT_SUPPORT}.
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.unusedParameterIncludeDocCommentReference"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "enabled", "disabled" }</code></dd>
     * <dt>Default:</dt><dd><code>"enabled"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 3.3
     */
    public static final String COMPILER_PB_UNUSED_PARAMETER_INCLUDE_DOC_COMMENT_REFERENCE = PLUGIN_ID
                                                                                            +
                                                                                            ".compiler.problem" +
                                                                                            ".unusedParameterIncludeDocCommentReference";
    //$NON-NLS-1$

    /**
     * Compiler option ID: Consider Reference in Doc Comment for Unused Declared Thrown Exception Check.
     * <p>When enabled, the compiler will consider doc comment references to
     * exceptions (i.e. <code>@throws</code> clauses) for the unused
     * declared thrown exception check. Thus, documented exceptions will be
     * considered as mandated as per doc contract.
     * <p>The severity of the unused declared thrown exception problem is controlled with option {@link
     * #COMPILER_PB_UNUSED_DECLARED_THROWN_EXCEPTION}.
     * <p>Note: this option has no effect until the doc comment support is enabled according to the
     * option {@link #COMPILER_DOC_COMMENT_SUPPORT}.
     * <p>This diagnostic is further tuned by options
     * {@link #COMPILER_PB_UNUSED_DECLARED_THROWN_EXCEPTION_EXEMPT_EXCEPTION_AND_THROWABLE}
     * and {@link #COMPILER_PB_UNUSED_DECLARED_THROWN_EXCEPTION_WHEN_OVERRIDING}.
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem
     * .unusedDeclaredThrownExceptionIncludeDocCommentReference"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "enabled", "disabled" }</code></dd>
     * <dt>Default:</dt><dd><code>"enabled"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 3.4
     */
    public static final String COMPILER_PB_UNUSED_DECLARED_THROWN_EXCEPTION_INCLUDE_DOC_COMMENT_REFERENCE =
            PLUGIN_ID + ".compiler.problem" + ".unusedDeclaredThrownExceptionIncludeDocCommentReference";
    //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Missing Javadoc Tags for Method Type Parameters.
     * <p>Specify whether a missing <code>@param</code> for a type parameter in a method declaration should be reported.
     * When enabled, the compiler will issue a missing Javadoc tag error or warning for a type parameter without a
     * corresponding <code>@param</code> tag.</p>
     * <p>This option only has an effect if the compiler compliance is 1.5 or greater.</p>
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.missingJavadocTagsMethodTypeParameters"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "enabled", "disabled" }</code></dd>
     * <dt>Default:</dt><dd><code>"disabled"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 3.7
     */
    public static final String COMPILER_PB_MISSING_JAVADOC_TAGS_METHOD_TYPE_PARAMETERS = PLUGIN_ID
                                                                                         +
                                                                                         ".compiler.problem" +
                                                                                         ".missingJavadocTagsMethodTypeParameters";
    //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Non-Static Reference to a Static Member.
     * <p>When enabled, the compiler will issue an error or a warning whenever a static field
     * or method is accessed with an expression receiver. A reference to a static member should
     * be qualified with a type name.
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.staticAccessReceiver"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt><dd><code>"warning"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 2.1
     */
    public static final String COMPILER_PB_STATIC_ACCESS_RECEIVER = PLUGIN_ID + ".compiler.problem.staticAccessReceiver"; //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Indirect Reference to a Static Member.
     * <p>When enabled, the compiler will issue an error or a warning whenever a static field
     * or method is accessed in an indirect way. A reference to a static member should
     * preferably be qualified with its declaring type name.
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.indirectStaticAccess"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt><dd><code>"ignore"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 3.0
     */
    public static final String COMPILER_PB_INDIRECT_STATIC_ACCESS = PLUGIN_ID + ".compiler.problem.indirectStaticAccess"; //$NON-NLS-1$

    /**
     * Compiler option ID: Javadoc Comment Support.
     * <p/>
     * When this support is disabled, the compiler will ignore all javadoc problems options settings and will not report any
     * javadoc problem. It will also not find any reference in javadoc comment and DOM AST Javadoc node will be only a flat text
     * instead of having structured tag elements.
     * <dl>
     * <dt>Option id:</dt>
     * <dd><code>"org.eclipse.jdt.core.compiler.doc.comment.support"</code></dd>
     * <dt>Possible values:</dt>
     * <dd><code>{ "enabled", "disabled" }</code></dd>
     * <dt>Default:</dt>
     * <dd><code>"enabled"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 3.0
     */
    public static final String COMPILER_DOC_COMMENT_SUPPORT = PLUGIN_ID + ".compiler.doc.comment.support"; //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Usage of <code>'assert'</code> Identifier.
     * <p/>
     * When enabled, the compiler will issue an error or a warning whenever <code>'assert'</code> is used as an identifier
     * (reserved keyword in 1.4).
     * <dl>
     * <dt>Option id:</dt>
     * <dd><code>"org.eclipse.jdt.core.compiler.problem.assertIdentifier"</code></dd>
     * <dt>Possible values:</dt>
     * <dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt>
     * <dd><code>"warning"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 2.0
     */
    public static final String COMPILER_PB_ASSERT_IDENTIFIER = PLUGIN_ID + ".compiler.problem.assertIdentifier"; //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Usage of <code>'enum'</code> Identifier.
     * <p/>
     * When enabled, the compiler will issue an error or a warning whenever <code>'enum'</code> is used as an identifier (reserved
     * keyword in 1.5).
     * <dl>
     * <dt>Option id:</dt>
     * <dd><code>"org.eclipse.jdt.core.compiler.problem.enumIdentifier"</code></dd>
     * <dt>Possible values:</dt>
     * <dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt>
     * <dd><code>"warning"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 3.1
     */
    public static final String COMPILER_PB_ENUM_IDENTIFIER = PLUGIN_ID + ".compiler.problem.enumIdentifier"; //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Unused Private Members.
     * <p>When enabled, the compiler will issue an error or a warning whenever a private
     * method or field is declared but never used within the same unit.
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.unusedPrivateMember"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt><dd><code>"warning"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 2.1
     */
    public static final String COMPILER_PB_UNUSED_PRIVATE_MEMBER = PLUGIN_ID + ".compiler.problem.unusedPrivateMember"; //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Unused Import.
     * <p>When enabled, the compiler will issue an error or a warning for unused import
     * reference.
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.unusedImport"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt><dd><code>"warning"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 2.0
     */
    public static final String COMPILER_PB_UNUSED_IMPORT = PLUGIN_ID + ".compiler.problem.unusedImport";

    /**
     * Compiler option ID: Reporting Unused Local.
     * <p>When enabled, the compiler will issue an error or a warning for unused local
     * variables (that is, variables never read from).
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.unusedLocal"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt><dd><code>"warning"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     */
    public static final String COMPILER_PB_UNUSED_LOCAL = PLUGIN_ID + ".compiler.problem.unusedLocal";

    /**
     * Compiler option ID: Setting Source Compatibility Mode.
     * <p/>
     * Specify whether which source level compatibility is used. From 1.4 on, <code>'assert'</code> is a keyword reserved for
     * assertion support. Also note, than when toggling to 1.4 mode, the target VM level should be set to <code>"1.4"</code> and
     * the compliance mode should be <code>"1.4"</code>.
     * <p/>
     * Source level 1.5 is necessary to enable generics, autoboxing, covariance, annotations, enumerations enhanced for loop,
     * static imports and varargs. Once toggled, the target VM level should be set to <code>"1.5"</code> and the compliance mode
     * should be <code>"1.5"</code>.
     * <p/>
     * Source level 1.6 is necessary to enable the computation of stack map tables. Once toggled, the target VM level should be set
     * to <code>"1.6"</code> and the compliance mode should be <code>"1.6"</code>.
     * <p/>
     * Once the source level 1.7 is toggled, the target VM level should be set to <code>"1.7"</code> and the compliance mode should
     * be <code>"1.7"</code>.
     * <dl>
     * <dt>Option id:</dt>
     * <dd><code>"org.eclipse.jdt.core.compiler.source"</code></dd>
     * <dt>Possible values:</dt>
     * <dd><code>{ "1.3", "1.4", "1.5", "1.6", "1.7" }</code></dd>
     * <dt>Default:</dt>
     * <dd><code>"1.3"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 2.0
     */
    public static final String COMPILER_SOURCE = PLUGIN_ID + ".compiler.source"; //$NON-NLS-1$

    /**
     * Compiler option ID: Setting Compliance Level.
     * <p/>
     * Select the compliance level for the compiler. In <code>"1.3"</code> mode, source and target settings should not go beyond
     * <code>"1.3"</code> level.
     * <dl>
     * <dt>Option id:</dt>
     * <dd><code>"org.eclipse.jdt.core.compiler.compliance"</code></dd>
     * <dt>Possible values:</dt>
     * <dd><code>{ "1.3", "1.4", "1.5", "1.6", "1.7" }</code></dd>
     * <dt>Default:</dt>
     * <dd><code>"1.4"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 2.0
     */
    public static final String COMPILER_COMPLIANCE = PLUGIN_ID + ".compiler.compliance"; //$NON-NLS-1$

    /**
     * Compiler option ID: Defining the Automatic Task Tags.
     * <p>
     * When the tag list is not empty, the compiler will issue a task marker whenever it encounters one of the corresponding tags
     * inside any comment in Java source code.
     * <p>
     * Generated task messages will start with the tag, and range until the next line separator, comment ending, or tag.
     * </p>
     * <p>
     * When a given line of code bears multiple tags, each tag will be reported separately. Moreover, a tag immediately followed by
     * another tag will be reported using the contents of the next non-empty tag of the line, if any.
     * </p>
     * <p>
     * Note that tasks messages are trimmed. If a tag is starting with a letter or digit, then it cannot be leaded by another
     * letter or digit to be recognized (<code>"fooToDo"</code> will not be recognized as a task for tag <code>"ToDo"</code>, but
     * <code>"foo#ToDo"</code> will be detected for either tag <code>"ToDo"</code> or <code>"#ToDo"</code>). Respectively, a tag
     * ending with a letter or digit cannot be followed by a letter or digit to be recognized (<code>"ToDofoo"</code> will not be
     * recognized as a task for tag <code>"ToDo"</code>, but <code>"ToDo:foo"</code> will be detected either for tag
     * <code>"ToDo"</code> or <code>"ToDo:"</code>).
     * </p>
     * <p>
     * Task Priorities and task tags must have the same length. If task tags are set, then task priorities should also be set.
     * </p>
     * <dl>
     * <dt>Option id:</dt>
     * <dd><code>"org.eclipse.jdt.core.compiler.taskTags"</code></dd>
     * <dt>Possible values:</dt>
     * <dd><code>{ "&lt;tag&gt;[,&lt;tag&gt;]*" }</code> where <code>&lt;tag&gt;</code> is a String without any wild-card or
     * leading/trailing spaces</dd>
     * <dt>Default:</dt>
     * <dd><code>"TODO,FIXME,XXX"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @see #COMPILER_TASK_PRIORITIES
     * @since 2.1
     */
    public static final String COMPILER_TASK_TAGS = PLUGIN_ID + ".compiler.taskTags"; //$NON-NLS-1$

    /**
     * Core option ID: Default Source Encoding Format.
     * <p/>
     * Get the default encoding format of source files. This value is immutable and preset to the result of
     * <code>ResourcesPlugin.getEncoding()</code>.
     * <p/>
     * It is offered as a convenience shortcut only.
     * <dl>
     * <dt>Option id:</dt>
     * <dd><code>"org.eclipse.jdt.core.encoding"</code></dd>
     * <dt>value:</dt>
     * <dd><code>&lt;immutable, platform default value&gt;</code></dd>
     * </dl>
     *
     * @category CoreOptionID
     * @since 2.0
     */
    public static final String CORE_ENCODING = PLUGIN_ID + ".encoding"; //$NON-NLS-1$

    /**
     * Code assist option ID: Activate Camel Case Sensitive Completion.
     * <p/>
     * When enabled, completion shows proposals whose name match the CamelCase pattern.
     * <dl>
     * <dt>Option id:</dt>
     * <dd><code>"org.eclipse.jdt.core.codeComplete.camelCaseMatch"</code></dd>
     * <dt>Possible values:</dt>
     * <dd><code>{ "enabled", "disabled" }</code></dd>
     * <dt>Default:</dt>
     * <dd><code>"enabled"</code></dd>
     * </dl>
     *
     * @category CodeAssistOptionID
     * @since 3.2
     */
    public static final String CODEASSIST_CAMEL_CASE_MATCH = PLUGIN_ID + ".codeComplete.camelCaseMatch"; //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Empty Statements and Unnecessary Semicolons.
     * <p>When enabled, the compiler will issue an error or a warning if an empty statement or a
     * unnecessary semicolon is encountered.
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.emptyStatement"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt><dd><code>"ignore"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 3.0
     */
    public static final String COMPILER_PB_EMPTY_STATEMENT = PLUGIN_ID + ".compiler.problem.emptyStatement"; //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Unused Declared Thrown Exception.
     * <p>When enabled, the compiler will issue an error or a warning when a
     * method or a constructor is declaring a checked exception as thrown,
     * but its body actually raises neither that exception, nor any other
     * exception extending it.
     * <p>This diagnostic is further tuned by options
     * {@link #COMPILER_PB_UNUSED_DECLARED_THROWN_EXCEPTION_INCLUDE_DOC_COMMENT_REFERENCE},
     * {@link #COMPILER_PB_UNUSED_DECLARED_THROWN_EXCEPTION_EXEMPT_EXCEPTION_AND_THROWABLE},
     * and {@link #COMPILER_PB_UNUSED_DECLARED_THROWN_EXCEPTION_WHEN_OVERRIDING}.
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.unusedDeclaredThrownException"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt><dd><code>"ignore"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 3.0
     */
    public static final String COMPILER_PB_UNUSED_DECLARED_THROWN_EXCEPTION = PLUGIN_ID
                                                                              + ".compiler.problem.unusedDeclaredThrownException";
    //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Unqualified Access to Field.
     * <p>When enabled, the compiler will issue an error or a warning when a field is access without any qualification.
     * In order to improve code readability, it should be qualified, e.g. <code>'x'</code> should rather be written <code>'this.x'</code>.
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.unqualifiedFieldAccess"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt><dd><code>"ignore"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 3.0
     */
    public static final String COMPILER_PB_UNQUALIFIED_FIELD_ACCESS = PLUGIN_ID
                                                                      + ".compiler.problem.unqualifiedFieldAccess"; //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Field Declaration Hiding another Variable.
     * <p>When enabled, the compiler will issue an error or a warning whenever a field
     * declaration is hiding some field or local variable (either locally, inherited or defined in enclosing type).
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.fieldHiding"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt><dd><code>"ignore"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 3.0
     */
    public static final String COMPILER_PB_FIELD_HIDING = PLUGIN_ID + ".compiler.problem.fieldHiding"; //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Local Variable Declaration Hiding another Variable.
     * <p>When enabled, the compiler will issue an error or a warning whenever a local variable
     * declaration is hiding some field or local variable (either locally, inherited or defined in enclosing type).
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.localVariableHiding"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt><dd><code>"ignore"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 3.0
     */
    public static final String COMPILER_PB_LOCAL_VARIABLE_HIDING = PLUGIN_ID + ".compiler.problem.localVariableHiding"; //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Unnecessary Else.
     * <p>When enabled, the compiler will issue an error or a warning when a statement is unnecessarily
     * nested within an <code>else</code> clause (in situation where then clause is not completing normally).
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.unnecessaryElse"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt><dd><code>"ignore"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 3.0
     */
    public static final String COMPILER_PB_UNNECESSARY_ELSE = PLUGIN_ID + ".compiler.problem.unnecessaryElse"; //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Dead Code.
     * <p>When enabled, the compiler will issue an error or a warning if some non fatal dead code is detected. For instance,
     * <code>if (false)
     * foo();</code>
     * is not reported as truly unreachable code by the Java Language Specification. If this diagnostic is enabled, then the invocation of
     * <code>foo()</code> is
     * going to be signaled as being dead code.
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.deadCode"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt><dd><code>"warning"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 3.5
     */
    public static final String COMPILER_PB_DEAD_CODE = PLUGIN_ID + ".compiler.problem.deadCode"; //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Allocation of an Unused Object.
     * <p>When enabled, the compiler will issue an error or a warning if an object is allocated but never used,
     * neither by holding a reference nor by invoking one of the object's methods.
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.unusedObjectAllocation"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt><dd><code>"ignore"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 3.6
     */
    public static final String COMPILER_PB_UNUSED_OBJECT_ALLOCATION = PLUGIN_ID
                                                                      + ".compiler.problem.unusedObjectAllocation"; //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Missing HashCode Method.
     * <p>When enabled, the compiler will issue an error or a warning if a type
     * overrides Object.equals(Object) but does not override hashCode().
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.missingHashCodeMethod"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt><dd><code>"ignore"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 3.5
     */
    public static final String COMPILER_PB_MISSING_HASHCODE_METHOD = PLUGIN_ID + ".compiler.problem.missingHashCodeMethod"; //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Unchecked Type Operation.
     * <p>When enabled, the compiler will issue an error or a warning whenever an operation involves generic types, and potentially
     * invalidates type safety since involving raw types (e.g. invoking <code>#foo(X&lt;String&gt;)</code> with arguments <code>(X)</code>).
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.uncheckedTypeOperation"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt><dd><code>"warning"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 3.1
     */
    public static final String COMPILER_PB_UNCHECKED_TYPE_OPERATION = PLUGIN_ID
                                                                      + ".compiler.problem.uncheckedTypeOperation"; //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Raw Type Reference.
     * <p>When enabled, the compiler will issue an error or a warning when detecting references to raw types. Raw types are
     * discouraged, and are intended to help interfacing with legacy code. In the future, the language specification may
     * reject raw references to generic types.
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.rawTypeReference"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt><dd><code>"warning"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 3.2
     */
    public static final String COMPILER_PB_RAW_TYPE_REFERENCE = PLUGIN_ID + ".compiler.problem.rawTypeReference"; //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Switch Fall-Through Case.
     * <p>When enabled, the compiler will issue an error or a warning if a case may be
     * entered by falling through previous case. Empty cases are allowed.
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.fallthroughCase"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt><dd><code>"ignore"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 3.2
     */
    public static final String COMPILER_PB_FALLTHROUGH_CASE = PLUGIN_ID + ".compiler.problem.fallthroughCase"; //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Type Declaration Hiding another Type.
     * <p>When enabled, the compiler will issue an error or a warning in situations where a type parameter
     * declaration is hiding some type, when a nested type is hiding some type parameter, or when
     * a nested type is hiding another nested type defined in same unit.
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.typeParameterHiding"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt><dd><code>"warning"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 3.1
     */
    public static final String COMPILER_PB_TYPE_PARAMETER_HIDING = PLUGIN_ID + ".compiler.problem.typeParameterHiding"; //$NON-NLS-1$

    /**
     * Compiler option ID: Reporting Incomplete Enum Switch.
     * <p>When enabled, the compiler will issue an error or a warning whenever
     * an enum constant has no corresponding case label in an enum switch
     * statement.
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.incompleteEnumSwitch"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
     * <dt>Default:</dt><dd><code>"ignore"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 3.1
     */
    public static final String COMPILER_PB_INCOMPLETE_ENUM_SWITCH = PLUGIN_ID + ".compiler.problem.incompleteEnumSwitch"; //$NON-NLS-1$

    /**
     * Compiler option ID: Determining Effect of <code>@SuppressWarnings</code>.
     * <p>When enabled, the <code>@SuppressWarnings</code> annotation can be used to suppress some compiler warnings.
     * <p>When disabled, all <code>@SupressWarnings</code> annotations are ignored; i.e., warnings are reported.
     * <dl>
     * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.suppressWarnings"</code></dd>
     * <dt>Possible values:</dt><dd><code>{ "enabled", "disabled" }</code></dd>
     * <dt>Default:</dt><dd><code>"enabled"</code></dd>
     * </dl>
     *
     * @category CompilerOptionID
     * @since 3.1
     */
    public static final String COMPILER_PB_SUPPRESS_WARNINGS = PLUGIN_ID + ".compiler.problem.suppressWarnings"; //$NON-NLS-1$

    // end configurable option IDs }
    // Begin configurable option values {
    /**
     * Configurable option value: {@value} .
     *
     * @category OptionValue
     */
    public static final String VERSION_1_1 = "1.1"; //$NON-NLS-1$

    /**
     * Configurable option value: {@value} .
     *
     * @category OptionValue
     */
    public static final String VERSION_1_2 = "1.2"; //$NON-NLS-1$

    /**
     * Configurable option value: {@value} .
     *
     * @category OptionValue
     * @since 2.0
     */
    public static final String VERSION_1_3 = "1.3"; //$NON-NLS-1$

    /**
     * Configurable option value: {@value} .
     *
     * @category OptionValue
     * @since 2.0
     */
    public static final String VERSION_1_4 = "1.4"; //$NON-NLS-1$

    /**
     * Configurable option value: {@value} .
     *
     * @category OptionValue
     * @since 3.0
     */
    public static final String VERSION_1_5 = "1.5"; //$NON-NLS-1$

    /**
     * Configurable option value: {@value} .
     *
     * @category OptionValue
     * @since 3.2
     */
    public static final String VERSION_1_6 = "1.6"; //$NON-NLS-1$

    /**
     * Configurable option value: {@value} .
     *
     * @category OptionValue
     * @since 3.3
     */
    public static final String VERSION_1_7 = "1.7"; //$NON-NLS-1$

    /**
     * Configurable option value: {@value} .
     *
     * @category OptionValue
     * @since 3.4
     */
    public static final String VERSION_CLDC_1_1 = "cldc1.1"; //$NON-NLS-1$

    /**
     * Configurable option value: {@value} .
     *
     * @category OptionValue
     */
    public static final String ERROR = "error"; //$NON-NLS-1$

    /**
     * Configurable option value: {@value} .
     *
     * @category OptionValue
     */
    public static final String WARNING = "warning"; //$NON-NLS-1$

    /**
     * Configurable option value: {@value} .
     *
     * @category OptionValue
     * @since 2.0
     */
    public static final String ENABLED = "enabled"; //$NON-NLS-1$

    /**
     * Configurable option value: {@value} .
     *
     * @category OptionValue
     */
    public static final String IGNORE = "ignore"; //$NON-NLS-1$

    /**
     * Configurable option value: {@value} .
     *
     * @category OptionValue
     * @since 2.0
     */
    public static final String INSERT = "insert"; //$NON-NLS-1$

    /**
     * Configurable option value: {@value} .
     *
     * @category OptionValue
     * @since 2.0
     */
    public static final String DO_NOT_INSERT = "do not insert"; //$NON-NLS-1$

    /**
     * Configurable option value: {@value} .
     *
     * @category OptionValue
     * @since 2.0
     */
    public static final String TAB = "tab"; //$NON-NLS-1$

    /**
     * Configurable option value: {@value}.
     *
     * @category OptionValue
     * @since 2.0
     */
    public static final String DISABLED = "disabled"; //$NON-NLS-1$

    /**
     * Configurable option value: {@value} .
     *
     * @category OptionValue
     * @since 2.0
     */
    public static final String SPACE = "space"; //$NON-NLS-1$

    private static HashMap<String, String> defaultOptions = new HashMap<String, String>(10);

    static {
        defaultOptions.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_6);
        defaultOptions.put(JavaCore.CORE_ENCODING, "UTF-8");
        defaultOptions.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_6);
        defaultOptions.put(CompilerOptions.OPTION_TargetPlatform, JavaCore.VERSION_1_6);
//        defaultOptions.put(
//                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_PAREN_IN_METHOD_INVOCATION,
//                JavaCore.DO_NOT_INSERT);
//        defaultOptions.put(DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_PAREN_IN_METHOD_INVOCATION,
//                           JavaCore.DO_NOT_INSERT);
//        defaultOptions.put(
//                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_METHOD_INVOCATION_ARGUMENTS,
//                JavaCore.DO_NOT_INSERT);
//        defaultOptions.put(
//                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_METHOD_INVOCATION_ARGUMENTS, INSERT);
//        defaultOptions.put(
//                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_PAREN_IN_METHOD_INVOCATION,
//                JavaCore.DO_NOT_INSERT);
//        defaultOptions.put(
//                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BETWEEN_EMPTY_PARENS_IN_METHOD_INVOCATION,
//                JavaCore.DO_NOT_INSERT);
//
//        defaultOptions
//                .put(
//                        DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_OPENING_ANGLE_BRACKET_IN_PARAMETERIZED_TYPE_REFERENCE,
//                        JavaCore.DO_NOT_INSERT);
//        defaultOptions
//                .put(
//                        DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_OPENING_ANGLE_BRACKET_IN_PARAMETERIZED_TYPE_REFERENCE,
//                        JavaCore.DO_NOT_INSERT);
//        defaultOptions.put(
//                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_COMMA_IN_PARAMETERIZED_TYPE_REFERENCE,
//                JavaCore.DO_NOT_INSERT);
//        defaultOptions.put(
//                DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_AFTER_COMMA_IN_PARAMETERIZED_TYPE_REFERENCE, INSERT);
//        defaultOptions
//                .put(
//                        DefaultCodeFormatterConstants.FORMATTER_INSERT_SPACE_BEFORE_CLOSING_ANGLE_BRACKET_IN_PARAMETERIZED_TYPE_REFERENCE,
//                        JavaCore.DO_NOT_INSERT);
        defaultOptions.put(JavaCore.COMPILER_DOC_COMMENT_SUPPORT, JavaCore.ENABLED);
        defaultOptions.put(JavaCore.COMPILER_PB_UNUSED_PARAMETER_INCLUDE_DOC_COMMENT_REFERENCE, JavaCore.ENABLED);
        defaultOptions.put(JavaCore.COMPILER_PB_UNUSED_DECLARED_THROWN_EXCEPTION_INCLUDE_DOC_COMMENT_REFERENCE, JavaCore.ENABLED);
    }

    /**
     * Creates the Java core plug-in.
     * <p>
     * The plug-in instance is created automatically by the Eclipse platform. Clients must not call.
     * </p>
     *
     * @since 3.0
     */
    public JavaCore() {
        super();
        // JAVA_CORE_PLUGIN = this;
    }

    /**
     * Returns a table of all known configurable options with their default values. These options allow to configure the behaviour
     * of the underlying components. The client may safely use the result as a template that they can modify and then pass to
     * <code>setOptions</code>.
     * <p/>
     * Helper constants have been defined on JavaCore for each of the option IDs (categorized in Code assist option ID, Compiler
     * option ID and Core option ID) and some of their acceptable values (categorized in Option value). Some options accept open
     * value sets beyond the documented constant values.
     * <p/>
     * Note: each release may add new options.
     *
     * @return a table of all known configurable options with their default values
     */
    public static HashMap<String, String> getDefaultOptions() {
        // get encoding through resource plugin
        return defaultOptions;
    }

    /**
     * Returns the workspace root default charset encoding.
     *
     * @return the name of the default charset encoding for workspace root.
     * @see IContainer#getDefaultCharset()
     * @see ResourcesPlugin#getEncoding()
     * @since 3.0
     */
    private static String getEncoding() {
        return "UTF-8";
    }

    /**
     * Returns the table of the current options. Initially, all options have their default values, and this method returns a table
     * that includes all known options.
     * <p>
     * Helper constants have been defined on JavaCore for each of the option IDs (categorized in Code assist option ID, Compiler
     * option ID and Core option ID) and some of their acceptable values (categorized in Option value). Some options accept open
     * value sets beyond the documented constant values.
     * <p>
     * Note: each release may add new options.
     * <p>
     * Returns a default set of options even if the platform is not running.
     * </p>
     *
     * @return table of current settings of all options (key type: <code>String</code>; value type: <code>String</code>)
     * @see #getDefaultOptions()
     * @see JavaCorePreferenceInitializer for changing default settings
     */
    public static HashMap<String, String> getOptions() {
        // get encoding through resource plugin
        if (WorkerMessageHandler.get() == null)
            return getDefaultOptions();

        return WorkerMessageHandler.get().getOptions();
    }

    public static String getOption(String key) {
        return getOptions().get(key);
    }

    /**
     * Returns the option that can be used to configure the severity of the
     * compiler problem identified by <code>problemID</code> if any,
     * <code>null</code> otherwise. Non-null return values are taken from the
     * constants defined by this class whose names start with
     * <code>COMPILER_PB</code> and for which the possible values of the
     * option are defined by <code>{ "error", "warning", "ignore" }</code>. A
     * null return value means that the provided problem ID is unknown or that
     * it matches a problem whose severity cannot be configured.
     *
     * @param problemID
     *         one of the problem IDs defined by {@link IProblem}
     * @return the option that can be used to configure the severity of the
     *         compiler problem identified by <code>problemID</code> if any,
     *         <code>null</code> otherwise
     * @since 3.4
     */
    public static String getOptionForConfigurableSeverity(int problemID) {
        return CompilerOptions.optionKeyFromIrritant(ProblemReporter.getIrritant(problemID));
    }

    /**
     * Sets the default compiler options inside the given options map according to the given compliance.
     * <p/>
     * <p>
     * The given compliance must be one of those supported by the compiler, that is one of the acceptable values for option
     * {@link #COMPILER_COMPLIANCE}.
     * <p/>
     * <p>
     * The list of modified options is currently:
     * </p>
     * <ul>
     * <li>{@link #COMPILER_COMPLIANCE}</li>
     * <li>{@link #COMPILER_SOURCE}</li>
     * <li>{@link #COMPILER_CODEGEN_TARGET_PLATFORM}</li>
     * <li>{@link #COMPILER_PB_ASSERT_IDENTIFIER}</li>
     * <li>{@link #COMPILER_PB_ENUM_IDENTIFIER}</li>
     * <li>{@link #COMPILER_CODEGEN_INLINE_JSR_BYTECODE} for compliance levels 1.5 and greater</li>
     * </ul>
     * <p/>
     * <p>
     * If the given compliance is unknown, the given map is unmodified.
     * </p>
     *
     * @param compliance
     *         the given compliance
     * @param options
     *         the given options map
     * @since 3.3
     */
    public static void setComplianceOptions(String compliance, Map options) {
        switch ((int)(CompilerOptions.versionToJdkLevel(compliance) >>> 16)) {
            case ClassFileConstants.MAJOR_VERSION_1_3:
                options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_3);
                options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_3);
                options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_1);
                options.put(JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaCore.IGNORE);
                options.put(JavaCore.COMPILER_PB_ENUM_IDENTIFIER, JavaCore.IGNORE);
                break;
            case ClassFileConstants.MAJOR_VERSION_1_4:
                options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_4);
                options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_3);
                options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_2);
                options.put(JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaCore.WARNING);
                options.put(JavaCore.COMPILER_PB_ENUM_IDENTIFIER, JavaCore.WARNING);
                break;
            case ClassFileConstants.MAJOR_VERSION_1_5:
                options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
                options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
                options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_5);
                options.put(JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaCore.ERROR);
                options.put(JavaCore.COMPILER_PB_ENUM_IDENTIFIER, JavaCore.ERROR);
                break;
            case ClassFileConstants.MAJOR_VERSION_1_6:
                options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_6);
                options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_6);
                options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_6);
                options.put(JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaCore.ERROR);
                options.put(JavaCore.COMPILER_PB_ENUM_IDENTIFIER, JavaCore.ERROR);
                break;
            case ClassFileConstants.MAJOR_VERSION_1_7:
                options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_7);
                options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_7);
                options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_7);
                options.put(JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaCore.ERROR);
                options.put(JavaCore.COMPILER_PB_ENUM_IDENTIFIER, JavaCore.ERROR);
        }
    }
}