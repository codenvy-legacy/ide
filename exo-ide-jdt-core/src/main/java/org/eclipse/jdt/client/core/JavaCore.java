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
 *								   COMPILER_PB_MISSING_JAVADOC_TAG_DESCRIPTION_NO_TAG
 *								   COMPILER_PB_MISSING_JAVADOC_TAG_DESCRIPTION_RETURN_TAG
 *								   COMPILER_PB_MISSING_JAVADOC_TAG_DESCRIPTION_ALL_TAGS
 *     IBM Corporation - added the following constants:
 *                                 COMPILER_PB_REDUNDANT_SUPERINTERFACE
 *     IBM Corporation - added the following constant:
 *                                 COMPILER_PB_UNUSED_DECLARED_THROWN_EXCEPTION_EXEMPT_EXCEPTION_AND_THROWABLE
 *     IBM Corporation - added getOptionForConfigurableSeverity(int)
 *     Benjamin Muskalla - added COMPILER_PB_MISSING_SYNCHRONIZED_ON_INHERITED_METHOD
 *     Stephan Herrmann  - added COMPILER_PB_UNUSED_OBJECT_ALLOCATION
 *     Stephan Herrmann  - added COMPILER_PB_SUPPRESS_OPTIONAL_ERRORS
 *******************************************************************************/

package org.eclipse.jdt.client.core;

import org.eclipse.jdt.client.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.client.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.client.internal.core.util.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * The plug-in runtime class for the Java model plug-in containing the core
 * (UI-free) support for Java projects.
 * <p>
 * Like all plug-in runtime classes (subclasses of <code>Plugin</code>), this
 * class is automatically instantiated by the platform when the plug-in gets
 * activated. Clients must not attempt to instantiate plug-in runtime classes
 * directly.
 * </p>
 * <p>
 * The single instance of this class can be accessed from any plug-in declaring
 * the Java model plug-in as a prerequisite via
 * <code>JavaCore.getJavaCore()</code>. The Java model plug-in will be activated
 * automatically if not already active.
 * </p>
 * @noinstantiate This class is not intended to be instantiated by clients.
 */
public final class JavaCore
{

   /**
    * The plug-in identifier of the Java core support
    * (value <code>"org.eclipse.jdt.core"</code>).
    */
   private static final String PLUGIN_ID = "org.eclipse.jdt.core"; //$NON-NLS-1$

   // Begin configurable option IDs {



   

 

   /**
    * Compiler option ID: Defining Target Java Platform.
    * <p>For binary compatibility reason, .class files can be tagged to with certain VM versions and later.
    * <p>Note that <code>"1.4"</code> target requires to toggle compliance mode to <code>"1.4"</code>, <code>"1.5"</code> target requires
    *    to toggle compliance mode to <code>"1.5"</code>, <code>"1.6"</code> target requires to toggle compliance mode to <code>"1.6"</code> and
    *    <code>"1.7"</code> target requires to toggle compliance mode to <code>"1.7"</code>.
    *    <code>"cldc1.1"</code> requires the source version to be <code>"1.3"</code> and the compliance version to be <code>"1.4"</code> or lower.
    * <dl>
    * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.codegen.targetPlatform"</code></dd>
    * <dt>Possible values:</dt><dd><code>{ "1.1", "1.2", "1.3", "1.4", "1.5", "1.6", "1.7", "cldc1.1" }</code></dd>
    * <dt>Default:</dt><dd><code>"1.2"</code></dd>
    * </dl>
    * @category CompilerOptionID
    */
   public static final String COMPILER_CODEGEN_TARGET_PLATFORM = PLUGIN_ID + ".compiler.codegen.targetPlatform"; //$NON-NLS-1$

   /**
    * Compiler option ID: Inline JSR Bytecode Instruction.
    * <p>When enabled, the compiler will no longer generate JSR instructions, but rather inline corresponding
    *    subroutine code sequences (mostly corresponding to try finally blocks). The generated code will thus
    *    get bigger, but will load faster on virtual machines since the verification process is then much simpler.
    * <p>This mode is anticipating support for the Java Specification Request 202.
    * <p>Note that JSR inlining is optional only for target platform lesser than 1.5. From 1.5 on, the JSR
    *    inlining is mandatory (also see related setting {@link #COMPILER_CODEGEN_TARGET_PLATFORM}).
    * <dl>
    * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.codegen.inlineJsrBytecode"</code></dd>
    * <dt>Possible values:</dt><dd><code>{ "enabled", "disabled" }</code></dd>
    * <dt>Default:</dt><dd><code>"disabled"</code></dd>
    * </dl>
    * @since 3.0
    * @category CompilerOptionID
    */
   public static final String COMPILER_CODEGEN_INLINE_JSR_BYTECODE = PLUGIN_ID + ".compiler.codegen.inlineJsrBytecode"; //$NON-NLS-1$

   /**
    * Compiler option ID: Javadoc Comment Support.
    * <p>When this support is disabled, the compiler will ignore all javadoc problems options settings
    *    and will not report any javadoc problem. It will also not find any reference in javadoc comment and
    *    DOM AST Javadoc node will be only a flat text instead of having structured tag elements.
    * <dl>
    * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.doc.comment.support"</code></dd>
    * <dt>Possible values:</dt><dd><code>{ "enabled", "disabled" }</code></dd>
    * <dt>Default:</dt><dd><code>"enabled"</code></dd>
    * </dl>
    * @since 3.0
    * @category CompilerOptionID
    */
   public static final String COMPILER_DOC_COMMENT_SUPPORT = PLUGIN_ID + ".compiler.doc.comment.support"; //$NON-NLS-1$

   /**
    * Compiler option ID: Reporting Usage of <code>'assert'</code> Identifier.
    * <p>When enabled, the compiler will issue an error or a warning whenever <code>'assert'</code> is
    *    used as an identifier (reserved keyword in 1.4).
    * <dl>
    * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.assertIdentifier"</code></dd>
    * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
    * <dt>Default:</dt><dd><code>"warning"</code></dd>
    * </dl>
    * @since 2.0
    * @category CompilerOptionID
    */
   public static final String COMPILER_PB_ASSERT_IDENTIFIER = PLUGIN_ID + ".compiler.problem.assertIdentifier"; //$NON-NLS-1$

   /**
    * Compiler option ID: Reporting Usage of <code>'enum'</code> Identifier.
    * <p>When enabled, the compiler will issue an error or a warning whenever <code>'enum'</code> is
    *    used as an identifier (reserved keyword in 1.5).
    * <dl>
    * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.problem.enumIdentifier"</code></dd>
    * <dt>Possible values:</dt><dd><code>{ "error", "warning", "ignore" }</code></dd>
    * <dt>Default:</dt><dd><code>"warning"</code></dd>
    * </dl>
    * @since 3.1
    * @category CompilerOptionID
    */
   public static final String COMPILER_PB_ENUM_IDENTIFIER = PLUGIN_ID + ".compiler.problem.enumIdentifier"; //$NON-NLS-1$


   /**
    * Compiler option ID: Setting Source Compatibility Mode.
    * <p>Specify whether which source level compatibility is used. From 1.4 on, <code>'assert'</code> is a keyword
    *    reserved for assertion support. Also note, than when toggling to 1.4 mode, the target VM
    *    level should be set to <code>"1.4"</code> and the compliance mode should be <code>"1.4"</code>.
    * <p>Source level 1.5 is necessary to enable generics, autoboxing, covariance, annotations, enumerations
    *    enhanced for loop, static imports and varargs. Once toggled, the target VM level should be set to <code>"1.5"</code>
    *    and the compliance mode should be <code>"1.5"</code>.
    * <p>Source level 1.6 is necessary to enable the computation of stack map tables. Once toggled, the target
    *    VM level should be set to <code>"1.6"</code> and the compliance mode should be <code>"1.6"</code>.
    * <p>Once the source level 1.7 is toggled, the target VM level should be set to <code>"1.7"</code> and the compliance mode
    *    should be <code>"1.7"</code>.
    * <dl>
    * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.source"</code></dd>
    * <dt>Possible values:</dt><dd><code>{ "1.3", "1.4", "1.5", "1.6", "1.7" }</code></dd>
    * <dt>Default:</dt><dd><code>"1.3"</code></dd>
    * </dl>
    * @since 2.0
    * @category CompilerOptionID
    */
   public static final String COMPILER_SOURCE = PLUGIN_ID + ".compiler.source"; //$NON-NLS-1$

   /**
    * Compiler option ID: Setting Compliance Level.
    * <p>Select the compliance level for the compiler. In <code>"1.3"</code> mode, source and target settings
    *    should not go beyond <code>"1.3"</code> level.
    * <dl>
    * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.compliance"</code></dd>
    * <dt>Possible values:</dt><dd><code>{ "1.3", "1.4", "1.5", "1.6", "1.7" }</code></dd>
    * <dt>Default:</dt><dd><code>"1.4"</code></dd>
    * </dl>
    * @since 2.0
    * @category CompilerOptionID
    */
   public static final String COMPILER_COMPLIANCE = PLUGIN_ID + ".compiler.compliance"; //$NON-NLS-1$


   /**
    * Compiler option ID: Defining the Automatic Task Tags.
    * <p>When the tag list is not empty, the compiler will issue a task marker whenever it encounters
    *    one of the corresponding tags inside any comment in Java source code.
    * <p>Generated task messages will start with the tag, and range until the next line separator,
    *    comment ending, or tag.</p>
    * <p>When a given line of code bears multiple tags, each tag will be reported separately.
    *    Moreover, a tag immediately followed by another tag will be reported using the contents of the
    *    next non-empty tag of the line, if any.</p>
    * <p>Note that tasks messages are trimmed. If a tag is starting with a letter or digit, then it cannot be leaded by
    *    another letter or digit to be recognized (<code>"fooToDo"</code> will not be recognized as a task for tag <code>"ToDo"</code>, but <code>"foo#ToDo"</code>
    *    will be detected for either tag <code>"ToDo"</code> or <code>"#ToDo"</code>). Respectively, a tag ending with a letter or digit cannot be followed
    *    by a letter or digit to be recognized (<code>"ToDofoo"</code> will not be recognized as a task for tag <code>"ToDo"</code>, but <code>"ToDo:foo"</code> will
    *    be detected either for tag <code>"ToDo"</code> or <code>"ToDo:"</code>).</p>
    * <p>Task Priorities and task tags must have the same length. If task tags are set, then task priorities should also
    * be set.</p>
    * <dl>
    * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.compiler.taskTags"</code></dd>
    * <dt>Possible values:</dt><dd><code>{ "&lt;tag&gt;[,&lt;tag&gt;]*" }</code> where <code>&lt;tag&gt;</code> is a String without any wild-card or leading/trailing spaces</dd>
    * <dt>Default:</dt><dd><code>"TODO,FIXME,XXX"</code></dd>
    * </dl>
    * @since 2.1
    * @category CompilerOptionID
    * @see #COMPILER_TASK_PRIORITIES
    */
   public static final String COMPILER_TASK_TAGS = PLUGIN_ID + ".compiler.taskTags"; //$NON-NLS-1$

   /**
    * Core option ID: Default Source Encoding Format.
    * <p>Get the default encoding format of source files. This value is
    *    immutable and preset to the result of <code>ResourcesPlugin.getEncoding()</code>.
    * <p>It is offered as a convenience shortcut only.
    * <dl>
    * <dt>Option id:</dt><dd><code>"org.eclipse.jdt.core.encoding"</code></dd>
    * <dt>value:</dt><dd><code>&lt;immutable, platform default value&gt;</code></dd>
    * </dl>
    * @since 2.0
    * @category CoreOptionID
    */
   public static final String CORE_ENCODING = PLUGIN_ID + ".encoding"; //$NON-NLS-1$

   // end configurable option IDs }
   // Begin configurable option values {
   /**
    * Configurable option value: {@value}.
    * @category OptionValue
    */
   public static final String VERSION_1_1 = "1.1"; //$NON-NLS-1$

   /**
    * Configurable option value: {@value}.
    * @category OptionValue
    */
   public static final String VERSION_1_2 = "1.2"; //$NON-NLS-1$

   /**
    * Configurable option value: {@value}.
    * @since 2.0
    * @category OptionValue
    */
   public static final String VERSION_1_3 = "1.3"; //$NON-NLS-1$

   /**
    * Configurable option value: {@value}.
    * @since 2.0
    * @category OptionValue
    */
   public static final String VERSION_1_4 = "1.4"; //$NON-NLS-1$

   /**
    * Configurable option value: {@value}.
    * @since 3.0
    * @category OptionValue
    */
   public static final String VERSION_1_5 = "1.5"; //$NON-NLS-1$

   /**
    * Configurable option value: {@value}.
    * @since 3.2
    * @category OptionValue
    */
   public static final String VERSION_1_6 = "1.6"; //$NON-NLS-1$

   /**
    * Configurable option value: {@value}.
    * @since 3.3
    * @category OptionValue
    */
   public static final String VERSION_1_7 = "1.7"; //$NON-NLS-1$

   /**
    * Configurable option value: {@value}.
    * @since 3.4
    * @category OptionValue
    */
   public static final String VERSION_CLDC_1_1 = "cldc1.1"; //$NON-NLS-1$

   /**
    * Configurable option value: {@value}.
    * @category OptionValue
    */
   public static final String ERROR = "error"; //$NON-NLS-1$

   /**
    * Configurable option value: {@value}.
    * @category OptionValue
    */
   public static final String WARNING = "warning"; //$NON-NLS-1$
   
   /**
    * Configurable option value: {@value}.
    * @since 2.0
    * @category OptionValue
    */
   public static final String ENABLED = "enabled"; //$NON-NLS-1$

   /**
    * Configurable option value: {@value}.
    * @category OptionValue
    */
   public static final String IGNORE = "ignore"; //$NON-NLS-1$

   private static Map<String, String> options = new HashMap<String, String>();

   static
   {
      options.put(COMPILER_COMPLIANCE, VERSION_1_6);
      options.put(CORE_ENCODING, "UTF-8");
   }

   /**
    * Creates the Java core plug-in.
    * <p>
    * The plug-in instance is created automatically by the
    * Eclipse platform. Clients must not call.
    * </p>
    *
    * @since 3.0
    */
   public JavaCore()
   {
      super();
      //		JAVA_CORE_PLUGIN = this;
   }

   //	/**
   //	 * Adds the given listener for changes to Java elements.
   //	 * Has no effect if an identical listener is already registered.
   //	 *
   //	 * This listener will only be notified during the POST_CHANGE resource change notification
   //	 * and any reconcile operation (POST_RECONCILE).
   //	 * For finer control of the notification, use <code>addElementChangedListener(IElementChangedListener,int)</code>,
   //	 * which allows to specify a different eventMask.
   //	 *
   //	 * @param listener the listener
   //	 * @see ElementChangedEvent
   //	 */
   //	public static void addElementChangedListener(IElementChangedListener listener) {
   //		addElementChangedListener(listener, ElementChangedEvent.POST_CHANGE | ElementChangedEvent.POST_RECONCILE);
   //	}
   //
   //	/**
   //	 * Adds the given listener for changes to Java elements.
   //	 * Has no effect if an identical listener is already registered.
   //	 * After completion of this method, the given listener will be registered for exactly
   //	 * the specified events.  If they were previously registered for other events, they
   //	 * will be deregistered.
   //	 * <p>
   //	 * Once registered, a listener starts receiving notification of changes to
   //	 * java elements in the model. The listener continues to receive
   //	 * notifications until it is replaced or removed.
   //	 * </p>
   //	 * <p>
   //	 * Listeners can listen for several types of event as defined in <code>ElementChangeEvent</code>.
   //	 * Clients are free to register for any number of event types however if they register
   //	 * for more than one, it is their responsibility to ensure they correctly handle the
   //	 * case where the same java element change shows up in multiple notifications.
   //	 * Clients are guaranteed to receive only the events for which they are registered.
   //	 * </p>
   //	 *
   //	 * @param listener the listener
   //	 * @param eventMask the bit-wise OR of all event types of interest to the listener
   //	 * @see IElementChangedListener
   //	 * @see ElementChangedEvent
   //	 * @see #removeElementChangedListener(IElementChangedListener)
   //	 * @since 2.0
   //	 */
   //	public static void addElementChangedListener(IElementChangedListener listener, int eventMask) {
   //		JavaModelManager.getDeltaState().addElementChangedListener(listener, eventMask);
   //	}
   //
   //	/**
   //	 * Configures the given marker attribute map for the given Java element.
   //	 * Used for markers, which denote a Java element rather than a resource.
   //	 *
   //	 * @param attributes the mutable marker attribute map (key type: <code>String</code>,
   //	 *   value type: <code>String</code>)
   //	 * @param element the Java element for which the marker needs to be configured
   //	 */
   //	public static void addJavaElementMarkerAttributes(
   //		Map attributes,
   //		IJavaElement element) {
   //		if (element instanceof IMember)
   //			element = ((IMember) element).getClassFile();
   //		if (attributes != null && element != null)
   //			attributes.put(ATT_HANDLE_ID, element.getHandleIdentifier());
   //	}
   //
   //	private static void addNonJavaResources(Object[] nonJavaResources,
   //			IContainer container,
   //			int rootPathSegmentCounts,
   //			ArrayList collector) {
   //		for (int i = 0, max = nonJavaResources.length; i < max; i++) {
   //			Object nonJavaResource = nonJavaResources[i];
   //			if (nonJavaResource instanceof IFile) {
   //				IFile file = (IFile) nonJavaResource;
   //				IPath path = file.getFullPath().removeFirstSegments(rootPathSegmentCounts);
   //				IResource member = container.findMember(path);
   //				if (member != null && member.exists()) {
   //					collector.add(member);
   //				}
   //			} else if (nonJavaResource instanceof IFolder) {
   //				IFolder folder = (IFolder) nonJavaResource;
   //				IResource[] members = null;
   //				try {
   //					members = folder.members();
   //				} catch (CoreException e) {
   //					// ignore
   //				}
   //				if (members != null) {
   //					addNonJavaResources(members, container, rootPathSegmentCounts, collector);
   //				}
   //			}
   //		}
   //	}
   //
   //	/**
   //	 * Adds the given listener for POST_CHANGE resource change events to the Java core.
   //	 * The listener is guaranteed to be notified of the POST_CHANGE resource change event before
   //	 * the Java core starts processing the resource change event itself.
   //	 * <p>
   //	 * Has no effect if an identical listener is already registered.
   //	 * </p>
   //	 *
   //	 * @param listener the listener
   //	 * @see #removePreProcessingResourceChangedListener(IResourceChangeListener)
   //	 * @since 3.0
   //	 * @deprecated use addPreProcessingResourceChangedListener(listener, IResourceChangeEvent.POST_CHANGE) instead
   //	 */
   //	public static void addPreProcessingResourceChangedListener(IResourceChangeListener listener) {
   //		addPreProcessingResourceChangedListener(listener, IResourceChangeEvent.POST_CHANGE);
   //	}
   //
   //	/**
   //	 * Adds the given listener for resource change events of the given types to the Java core.
   //	 * The listener is guaranteed to be notified of the resource change event before
   //	 * the Java core starts processing the resource change event itself.
   //	 * <p>
   //	 * If an identical listener is already registered, the given event types are added to the event types
   //	 * of interest to the listener.
   //	 * </p>
   //	 * <p>
   //	 * Supported event types are:
   //	 * <ul>
   //	 * <li>{@link IResourceChangeEvent#PRE_BUILD}</li>
   //	 * <li>{@link IResourceChangeEvent#POST_BUILD}</li>
   //	 * <li>{@link IResourceChangeEvent#POST_CHANGE}</li>
   //	 * <li>{@link IResourceChangeEvent#PRE_DELETE}</li>
   //	 * <li>{@link IResourceChangeEvent#PRE_CLOSE}</li>
   //	 * </ul>
   //	 * This list may increase in the future.
   //	 * </p>
   //	 *
   //	 * @param listener the listener
   //	 * @param eventMask the bit-wise OR of all event types of interest to the
   //	 * listener
   //	 * @see #removePreProcessingResourceChangedListener(IResourceChangeListener)
   //	 * @see IResourceChangeEvent
   //	 * @since 3.2
   //	 */
   //	public static void addPreProcessingResourceChangedListener(IResourceChangeListener listener, int eventMask) {
   //		JavaModelManager.getDeltaState().addPreResourceChangedListener(listener, eventMask);
   //	}
   //
   //	/**
   //	 * Configures the given marker for the given Java element.
   //	 * Used for markers, which denote a Java element rather than a resource.
   //	 *
   //	 * @param marker the marker to be configured
   //	 * @param element the Java element for which the marker needs to be configured
   //	 * @exception CoreException if the <code>IMarker.setAttribute</code> on the marker fails
   //	 */
   //	public void configureJavaElementMarker(IMarker marker, IJavaElement element)
   //		throws CoreException {
   //		if (element instanceof IMember)
   //			element = ((IMember) element).getClassFile();
   //		if (marker != null && element != null)
   //			marker.setAttribute(ATT_HANDLE_ID, element.getHandleIdentifier());
   //	}
   //
   //	/**
   //	 * Returns the Java model element corresponding to the given handle identifier
   //	 * generated by <code>IJavaElement.getHandleIdentifier()</code>, or
   //	 * <code>null</code> if unable to create the associated element.
   //	 *
   //	 * @param handleIdentifier the given handle identifier
   //	 * @return the Java element corresponding to the handle identifier
   //	 */
   //	public static IJavaElement create(String handleIdentifier) {
   //		return create(handleIdentifier, DefaultWorkingCopyOwner.PRIMARY);
   //	}
   //
   //	/**
   //	 * Returns the Java model element corresponding to the given handle identifier
   //	 * generated by <code>IJavaElement.getHandleIdentifier()</code>, or
   //	 * <code>null</code> if unable to create the associated element.
   //	 * If the returned Java element is an <code>ICompilationUnit</code> or an element
   //	 * inside a compilation unit, the compilation unit's owner is the given owner if such a
   //	 * working copy exists, otherwise the compilation unit is a primary compilation unit.
   //	 *
   //	 * @param handleIdentifier the given handle identifier
   //	 * @param owner the owner of the returned compilation unit, ignored if the returned
   //	 *   element is not a compilation unit, or an element inside a compilation unit
   //	 * @return the Java element corresponding to the handle identifier
   //	 * @since 3.0
   //	 */
   //	public static IJavaElement create(String handleIdentifier, WorkingCopyOwner owner) {
   //		if (handleIdentifier == null) {
   //			return null;
   //		}
   //		if (owner == null)
   //			owner = DefaultWorkingCopyOwner.PRIMARY;
   //		MementoTokenizer memento = new MementoTokenizer(handleIdentifier);
   //		JavaModel model = JavaModelManager.getJavaModelManager().getJavaModel();
   //		return model.getHandleFromMemento(memento, owner);
   //	}
   //
   //	/**
   //	 * Returns the Java element corresponding to the given file, or
   //	 * <code>null</code> if unable to associate the given file
   //	 * with a Java element.
   //	 *
   //	 * <p>The file must be one of:<ul>
   //	 *	<li>a file with one of the {@link JavaCore#getJavaLikeExtensions()
   //	 *      Java-like extensions} - the element returned is the corresponding <code>ICompilationUnit</code></li>
   //	 *	<li>a <code>.class</code> file - the element returned is the corresponding <code>IClassFile</code></li>
   //	 *	<li>a ZIP archive (e.g. a <code>.jar</code>, a <code>.zip</code> file, etc.) - the element returned is the corresponding <code>IPackageFragmentRoot</code></li>
   //	 *	</ul>
   //	 * <p>
   //	 * Creating a Java element has the side effect of creating and opening all of the
   //	 * element's parents if they are not yet open.
   //	 *
   //	 * @param file the given file
   //	 * @return the Java element corresponding to the given file, or
   //	 * <code>null</code> if unable to associate the given file
   //	 * with a Java element
   //	 */
   //	public static IJavaElement create(IFile file) {
   //		return JavaModelManager.create(file, null/*unknown java project*/);
   //	}
   //	/**
   //	 * Returns the package fragment or package fragment root corresponding to the given folder, or
   //	 * <code>null</code> if unable to associate the given folder with a Java element.
   //	 * <p>
   //	 * Note that a package fragment root is returned rather than a default package.
   //	 * <p>
   //	 * Creating a Java element has the side effect of creating and opening all of the
   //	 * element's parents if they are not yet open.
   //	 *
   //	 * @param folder the given folder
   //	 * @return the package fragment or package fragment root corresponding to the given folder, or
   //	 * <code>null</code> if unable to associate the given folder with a Java element
   //	 */
   //	public static IJavaElement create(IFolder folder) {
   //		return JavaModelManager.create(folder, null/*unknown java project*/);
   //	}
   //	/**
   //	 * Returns the Java project corresponding to the given project.
   //	 * <p>
   //	 * Creating a Java Project has the side effect of creating and opening all of the
   //	 * project's parents if they are not yet open.
   //	 * <p>
   //	 * Note that no check is done at this time on the existence or the java nature of this project.
   //	 *
   //	 * @param project the given project
   //	 * @return the Java project corresponding to the given project, null if the given project is null
   //	 */
   //	public static IJavaProject create(IProject project) {
   //		if (project == null) {
   //			return null;
   //		}
   //		JavaModel javaModel = JavaModelManager.getJavaModelManager().getJavaModel();
   //		return javaModel.getJavaProject(project);
   //	}
   //	/**
   //	 * Returns the Java element corresponding to the given resource, or
   //	 * <code>null</code> if unable to associate the given resource
   //	 * with a Java element.
   //	 * <p>
   //	 * The resource must be one of:<ul>
   //	 *	<li>a project - the element returned is the corresponding <code>IJavaProject</code></li>
   //	 *	<li>a file with one of the {@link JavaCore#getJavaLikeExtensions()
   //	 *      Java-like extensions} - the element returned is the corresponding <code>ICompilationUnit</code></li>
   //	 *	<li>a <code>.class</code> file - the element returned is the corresponding <code>IClassFile</code></li>
   //	 *	<li>a ZIP archive (e.g. a <code>.jar</code>, a <code>.zip</code> file, etc.) - the element returned is the corresponding <code>IPackageFragmentRoot</code></li>
   //	 *  <li>a folder - the element returned is the corresponding <code>IPackageFragmentRoot</code>
   //	 *    	or <code>IPackageFragment</code></li>
   //	 *  <li>the workspace root resource - the element returned is the <code>IJavaModel</code></li>
   //	 *	</ul>
   //	 * <p>
   //	 * Creating a Java element has the side effect of creating and opening all of the
   //	 * element's parents if they are not yet open.
   //	 *
   //	 * @param resource the given resource
   //	 * @return the Java element corresponding to the given resource, or
   //	 * <code>null</code> if unable to associate the given resource
   //	 * with a Java element
   //	 */
   //	public static IJavaElement create(IResource resource) {
   //		return JavaModelManager.create(resource, null/*unknown java project*/);
   //	}
   //	/**
   //	 * Returns the Java element corresponding to the given file, its project being the given
   //	 * project. Returns <code>null</code> if unable to associate the given resource
   //	 * with a Java element.
   //	 *<p>
   //	 * The resource must be one of:<ul>
   //	 *	<li>a project - the element returned is the corresponding <code>IJavaProject</code></li>
   //	 *	<li>a file with one of the {@link JavaCore#getJavaLikeExtensions()
   //	 *      Java-like extensions} - the element returned is the corresponding <code>ICompilationUnit</code></li>
   //	 *	<li>a <code>.class</code> file - the element returned is the corresponding <code>IClassFile</code></li>
   //	 *	<li>a ZIP archive (e.g. a <code>.jar</code>, a <code>.zip</code> file, etc.) - the element returned is the corresponding <code>IPackageFragmentRoot</code></li>
   //	 *  <li>a folder - the element returned is the corresponding <code>IPackageFragmentRoot</code>
   //	 *    	or <code>IPackageFragment</code></li>
   //	 *  <li>the workspace root resource - the element returned is the <code>IJavaModel</code></li>
   //	 *	</ul>
   //	 * <p>
   //	 * Creating a Java element has the side effect of creating and opening all of the
   //	 * element's parents if they are not yet open.
   //	 *
   //	 * @param resource the given resource
   //	 * @return the Java element corresponding to the given file, or
   //	 * <code>null</code> if unable to associate the given file
   //	 * with a Java element
   //	 * @since 3.3
   //	 */
   //	public static IJavaElement create(IResource resource, IJavaProject project) {
   //		return JavaModelManager.create(resource, project);
   //	}
   //	/**
   //	 * Returns the Java model.
   //	 *
   //	 * @param root the given root
   //	 * @return the Java model, or <code>null</code> if the root is null
   //	 */
   //	public static IJavaModel create(IWorkspaceRoot root) {
   //		if (root == null) {
   //			return null;
   //		}
   //		return JavaModelManager.getJavaModelManager().getJavaModel();
   //	}
   //	/**
   //	 * Creates and returns a class file element for
   //	 * the given <code>.class</code> file. Returns <code>null</code> if unable
   //	 * to recognize the class file.
   //	 *
   //	 * @param file the given <code>.class</code> file
   //	 * @return a class file element for the given <code>.class</code> file, or <code>null</code> if unable
   //	 * to recognize the class file
   //	 */
   //	public static IClassFile createClassFileFrom(IFile file) {
   //		return JavaModelManager.createClassFileFrom(file, null);
   //	}
   //	/**
   //	 * Creates and returns a compilation unit element for
   //	 * the given source file (i.e. a file with one of the {@link JavaCore#getJavaLikeExtensions()
   //	 * Java-like extensions}). Returns <code>null</code> if unable
   //	 * to recognize the compilation unit.
   //	 *
   //	 * @param file the given source file
   //	 * @return a compilation unit element for the given source file, or <code>null</code> if unable
   //	 * to recognize the compilation unit
   //	 */
   //	public static ICompilationUnit createCompilationUnitFrom(IFile file) {
   //		return JavaModelManager.createCompilationUnitFrom(file, null/*unknown java project*/);
   //	}
   //	/**
   //	 * Creates and returns a handle for the given JAR file.
   //	 * The Java model associated with the JAR's project may be
   //	 * created as a side effect.
   //	 *
   //	 * @param file the given JAR file
   //	 * @return a handle for the given JAR file, or <code>null</code> if unable to create a JAR package fragment root.
   //	 * (for example, if the JAR file represents a non-Java resource)
   //	 */
   //	public static IPackageFragmentRoot createJarPackageFragmentRootFrom(IFile file) {
   //		return JavaModelManager.createJarPackageFragmentRootFrom(file, null/*unknown java project*/);
   //	}
   //
   //	/**
   //	 * Answers the project specific value for a given classpath container.
   //	 * In case this container path could not be resolved, then will answer <code>null</code>.
   //	 * Both the container path and the project context are supposed to be non-null.
   //	 * <p>
   //	 * The containerPath is a formed by a first ID segment followed with extra segments, which can be
   //	 * used as additional hints for resolution. If no container was ever recorded for this container path
   //	 * onto this project (using <code>setClasspathContainer</code>, then a
   //	 * <code>ClasspathContainerInitializer</code> will be activated if any was registered for this container
   //	 * ID onto the extension point "org.eclipse.jdt.core.classpathContainerInitializer".
   //	 * <p>
   //	 * There is no assumption that the returned container must answer the exact same containerPath
   //	 * when requested <code>IClasspathContainer#getPath</code>.
   //	 * Indeed, the containerPath is just an indication for resolving it to an actual container object.
   //	 * <p>
   //	 * Classpath container values are persisted locally to the workspace, but
   //	 * are not preserved from a session to another. It is thus highly recommended to register a
   //	 * <code>ClasspathContainerInitializer</code> for each referenced container
   //	 * (through the extension point "org.eclipse.jdt.core.ClasspathContainerInitializer").
   //	 * <p>
   //	 * @param containerPath the name of the container, which needs to be resolved
   //	 * @param project a specific project in which the container is being resolved
   //	 * @return the corresponding classpath container or <code>null</code> if unable to find one.
   //	 *
   //	 * @exception JavaModelException if an exception occurred while resolving the container, or if the resolved container
   //	 *   contains illegal entries (contains CPE_CONTAINER entries or null entries).
   //	 *
   //	 * @see ClasspathContainerInitializer
   //	 * @see IClasspathContainer
   //	 * @see #setClasspathContainer(IPath, IJavaProject[], IClasspathContainer[], IProgressMonitor)
   //	 * @since 2.0
   //	 */
   //	public static IClasspathContainer getClasspathContainer(IPath containerPath, IJavaProject project) throws JavaModelException {
   //
   //	    JavaModelManager manager = JavaModelManager.getJavaModelManager();
   //		IClasspathContainer container = manager.getClasspathContainer(containerPath, project);
   //		if (container == JavaModelManager.CONTAINER_INITIALIZATION_IN_PROGRESS) {
   //		    return manager.getPreviousSessionContainer(containerPath, project);
   //		}
   //		return container;
   //	}
   //
   //	/**
   //	 * Helper method finding the classpath container initializer registered for a given classpath container ID
   //	 * or <code>null</code> if none was found while iterating over the contributions to extension point to
   //	 * the extension point "org.eclipse.jdt.core.classpathContainerInitializer".
   //	 * <p>
   //	 * A containerID is the first segment of any container path, used to identify the registered container initializer.
   //	 * <p>
   //	 * @param containerID - a containerID identifying a registered initializer
   //	 * @return ClasspathContainerInitializer - the registered classpath container initializer or <code>null</code> if
   //	 * none was found.
   //	 * @since 2.1
   //	 */
   //	public static ClasspathContainerInitializer getClasspathContainerInitializer(String containerID) {
   //		Hashtable containerInitializersCache = JavaModelManager.getJavaModelManager().containerInitializersCache;
   //		ClasspathContainerInitializer initializer = (ClasspathContainerInitializer) containerInitializersCache.get(containerID);
   //		if (initializer == null) {
   //			initializer = computeClasspathContainerInitializer(containerID);
   //			if (initializer == null)
   //				return null;
   //			containerInitializersCache.put(containerID, initializer);
   //		}
   //		return initializer;
   //	}
   //
   //	private static ClasspathContainerInitializer computeClasspathContainerInitializer(String containerID) {
   //		Plugin jdtCorePlugin = JavaCore.getPlugin();
   //		if (jdtCorePlugin == null) return null;
   //
   //		IExtensionPoint extension = Platform.getExtensionRegistry().getExtensionPoint(JavaCore.PLUGIN_ID, JavaModelManager.CPCONTAINER_INITIALIZER_EXTPOINT_ID);
   //		if (extension != null) {
   //			IExtension[] extensions =  extension.getExtensions();
   //			for(int i = 0; i < extensions.length; i++){
   //				IConfigurationElement [] configElements = extensions[i].getConfigurationElements();
   //				for(int j = 0; j < configElements.length; j++){
   //					IConfigurationElement configurationElement = configElements[j];
   //					String initializerID = configurationElement.getAttribute("id"); //$NON-NLS-1$
   //					if (initializerID != null && initializerID.equals(containerID)){
   //						if (JavaModelManager.CP_RESOLVE_VERBOSE_ADVANCED)
   //							verbose_found_container_initializer(containerID, configurationElement);
   //						try {
   //							Object execExt = configurationElement.createExecutableExtension("class"); //$NON-NLS-1$
   //							if (execExt instanceof ClasspathContainerInitializer){
   //								return (ClasspathContainerInitializer)execExt;
   //							}
   //						} catch(CoreException e) {
   //							// executable extension could not be created: ignore this initializer
   //							if (JavaModelManager.CP_RESOLVE_VERBOSE || JavaModelManager.CP_RESOLVE_VERBOSE_FAILURE) {
   //								verbose_failed_to_instanciate_container_initializer(containerID, configurationElement);
   //								e.printStackTrace();
   //							}
   //						}
   //					}
   //				}
   //			}
   //		}
   //		return null;
   //	}
   //
   //	private static void verbose_failed_to_instanciate_container_initializer(String containerID, IConfigurationElement configurationElement) {
   //		Util.verbose(
   //			"CPContainer INIT - failed to instanciate initializer\n" + //$NON-NLS-1$
   //			"	container ID: " + containerID + '\n' + //$NON-NLS-1$
   //			"	class: " + configurationElement.getAttribute("class"), //$NON-NLS-1$ //$NON-NLS-2$
   //			System.err);
   //	}
   //
   //	private static void verbose_found_container_initializer(String containerID, IConfigurationElement configurationElement) {
   //		Util.verbose(
   //			"CPContainer INIT - found initializer\n" + //$NON-NLS-1$
   //			"	container ID: " + containerID + '\n' + //$NON-NLS-1$
   //			"	class: " + configurationElement.getAttribute("class")); //$NON-NLS-1$ //$NON-NLS-2$
   //	}

   //	/**
   //	 * Returns the path held in the given classpath variable.
   //	 * Returns <code>null</code> if unable to bind.
   //	 * <p>
   //	 * Classpath variable values are persisted locally to the workspace, and
   //	 * are preserved from session to session.
   //	 * <p>
   //	 * Note that classpath variables can be contributed registered initializers for,
   //	 * using the extension point "org.eclipse.jdt.core.classpathVariableInitializer".
   //	 * If an initializer is registered for a variable, its persisted value will be ignored:
   //	 * its initializer will thus get the opportunity to rebind the variable differently on
   //	 * each session.
   //	 *
   //	 * @param variableName the name of the classpath variable
   //	 * @return the path, or <code>null</code> if none
   //	 * @see #setClasspathVariable(String, IPath)
   //	 */
   //	public static IPath getClasspathVariable(final String variableName) {
   //
   //	    JavaModelManager manager = JavaModelManager.getJavaModelManager();
   //		IPath variablePath = manager.variableGet(variableName);
   //		if (variablePath == JavaModelManager.VARIABLE_INITIALIZATION_IN_PROGRESS){
   //		    return manager.getPreviousSessionVariable(variableName);
   //		}
   //
   //		if (variablePath != null) {
   //			if (variablePath == JavaModelManager.CP_ENTRY_IGNORE_PATH)
   //				return null;
   //			return variablePath;
   //		}
   //
   //		// even if persisted value exists, initializer is given priority, only if no initializer is found the persisted value is reused
   //		final ClasspathVariableInitializer initializer = JavaCore.getClasspathVariableInitializer(variableName);
   //		if (initializer != null){
   //			if (JavaModelManager.CP_RESOLVE_VERBOSE)
   //				verbose_triggering_variable_initialization(variableName, initializer);
   //			if (JavaModelManager.CP_RESOLVE_VERBOSE_ADVANCED)
   //				verbose_triggering_variable_initialization_invocation_trace();
   //			manager.variablePut(variableName, JavaModelManager.VARIABLE_INITIALIZATION_IN_PROGRESS); // avoid initialization cycles
   //			boolean ok = false;
   //			try {
   //				// let OperationCanceledException go through
   //				// (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=59363)
   //				initializer.initialize(variableName);
   //
   //				variablePath = manager.variableGet(variableName); // initializer should have performed side-effect
   //				if (variablePath == JavaModelManager.VARIABLE_INITIALIZATION_IN_PROGRESS) return null; // break cycle (initializer did not init or reentering call)
   //				if (JavaModelManager.CP_RESOLVE_VERBOSE_ADVANCED)
   //					verbose_variable_value_after_initialization(variableName, variablePath);
   //				manager.variablesWithInitializer.add(variableName);
   //				ok = true;
   //			} catch (RuntimeException e) {
   //				if (JavaModelManager.CP_RESOLVE_VERBOSE || JavaModelManager.CP_RESOLVE_VERBOSE_FAILURE)
   //					e.printStackTrace();
   //				throw e;
   //			} catch (Error e) {
   //				if (JavaModelManager.CP_RESOLVE_VERBOSE || JavaModelManager.CP_RESOLVE_VERBOSE_FAILURE)
   //					e.printStackTrace();
   //				throw e;
   //			} finally {
   //				if (!ok) JavaModelManager.getJavaModelManager().variablePut(variableName, null); // flush cache
   //			}
   //		} else {
   //			if (JavaModelManager.CP_RESOLVE_VERBOSE_ADVANCED || JavaModelManager.CP_RESOLVE_VERBOSE_FAILURE)
   //				verbose_no_variable_initializer_found(variableName);
   //		}
   //		return variablePath;
   //	}
   //
   //	private static void verbose_no_variable_initializer_found(String variableName) {
   //		Util.verbose(
   //			"CPVariable INIT - no initializer found\n" + //$NON-NLS-1$
   //			"	variable: " + variableName); //$NON-NLS-1$
   //	}
   //
   //	private static void verbose_variable_value_after_initialization(String variableName, IPath variablePath) {
   //		Util.verbose(
   //			"CPVariable INIT - after initialization\n" + //$NON-NLS-1$
   //			"	variable: " + variableName +'\n' + //$NON-NLS-1$
   //			"	variable path: " + variablePath); //$NON-NLS-1$
   //	}
   //
   //	private static void verbose_triggering_variable_initialization(String variableName, ClasspathVariableInitializer initializer) {
   //		Util.verbose(
   //			"CPVariable INIT - triggering initialization\n" + //$NON-NLS-1$
   //			"	variable: " + variableName + '\n' + //$NON-NLS-1$
   //			"	initializer: " + initializer); //$NON-NLS-1$
   //	}
   //
   //	private static void verbose_triggering_variable_initialization_invocation_trace() {
   //		Util.verbose(
   //			"CPVariable INIT - triggering initialization\n" + //$NON-NLS-1$
   //			"	invocation trace:"); //$NON-NLS-1$
   //		new Exception("<Fake exception>").printStackTrace(System.out); //$NON-NLS-1$
   //	}
   //
   //	/**
   //	 * Returns deprecation message of a given classpath variable.
   //	 *
   //	 * @param variableName
   //	 * @return A string if the classpath variable is deprecated, <code>null</code> otherwise.
   //	 * @since 3.3
   //	 */
   //	public static String getClasspathVariableDeprecationMessage(String variableName) {
   //	    JavaModelManager manager = JavaModelManager.getJavaModelManager();
   //
   //		// Returns the stored deprecation message
   //		String message = (String) manager.deprecatedVariables.get(variableName);
   //		if (message != null) {
   //		    return message;
   //		}
   //
   //	    // If the variable has been already initialized, then there's no deprecation message
   //		IPath variablePath = manager.variableGet(variableName);
   //		if (variablePath != null && variablePath != JavaModelManager.VARIABLE_INITIALIZATION_IN_PROGRESS) {
   //			return null;
   //		}
   //
   //		// Search for extension point to get the possible deprecation message
   //		Plugin jdtCorePlugin = JavaCore.getPlugin();
   //		if (jdtCorePlugin == null) return null;
   //
   //		IExtensionPoint extension = Platform.getExtensionRegistry().getExtensionPoint(JavaCore.PLUGIN_ID, JavaModelManager.CPVARIABLE_INITIALIZER_EXTPOINT_ID);
   //		if (extension != null) {
   //			IExtension[] extensions =  extension.getExtensions();
   //			for(int i = 0; i < extensions.length; i++){
   //				IConfigurationElement [] configElements = extensions[i].getConfigurationElements();
   //				for(int j = 0; j < configElements.length; j++){
   //					IConfigurationElement configElement = configElements[j];
   //					String varAttribute = configElement.getAttribute("variable"); //$NON-NLS-1$
   //					if (variableName.equals(varAttribute)) {
   //						String deprecatedAttribute = configElement.getAttribute("deprecated"); //$NON-NLS-1$
   //						if (deprecatedAttribute != null) {
   //							return deprecatedAttribute;
   //						}
   //					}
   //				}
   //			}
   //		}
   //		return null;
   //	}

   //	/**
   //	 * Helper method finding the classpath variable initializer registered for a given classpath variable name
   //	 * or <code>null</code> if none was found while iterating over the contributions to extension point to
   //	 * the extension point "org.eclipse.jdt.core.classpathVariableInitializer".
   //	 * <p>
   // 	 * @param variable the given variable
   // 	 * @return ClasspathVariableInitializer - the registered classpath variable initializer or <code>null</code> if
   //	 * none was found.
   //	 * @since 2.1
   // 	 */
   //	public static ClasspathVariableInitializer getClasspathVariableInitializer(String variable){
   //
   //		Plugin jdtCorePlugin = JavaCore.getPlugin();
   //		if (jdtCorePlugin == null) return null;
   //
   //		IExtensionPoint extension = Platform.getExtensionRegistry().getExtensionPoint(JavaCore.PLUGIN_ID, JavaModelManager.CPVARIABLE_INITIALIZER_EXTPOINT_ID);
   //		if (extension != null) {
   //			IExtension[] extensions =  extension.getExtensions();
   //			for(int i = 0; i < extensions.length; i++){
   //				IConfigurationElement [] configElements = extensions[i].getConfigurationElements();
   //				for(int j = 0; j < configElements.length; j++){
   //					IConfigurationElement configElement = configElements[j];
   //					try {
   //						String varAttribute = configElement.getAttribute("variable"); //$NON-NLS-1$
   //						if (variable.equals(varAttribute)) {
   //							if (JavaModelManager.CP_RESOLVE_VERBOSE_ADVANCED)
   //								verbose_found_variable_initializer(variable, configElement);
   //							Object execExt = configElement.createExecutableExtension("class"); //$NON-NLS-1$
   //							if (execExt instanceof ClasspathVariableInitializer){
   //								ClasspathVariableInitializer initializer = (ClasspathVariableInitializer)execExt;
   //								String deprecatedAttribute = configElement.getAttribute("deprecated"); //$NON-NLS-1$
   //								if (deprecatedAttribute != null) {
   //									JavaModelManager.getJavaModelManager().deprecatedVariables.put(variable, deprecatedAttribute);
   //								}
   //								String readOnlyAttribute = configElement.getAttribute("readOnly"); //$NON-NLS-1$
   //								if (JavaModelManager.TRUE.equals(readOnlyAttribute)) {
   //									JavaModelManager.getJavaModelManager().readOnlyVariables.add(variable);
   //								}
   //								return initializer;
   //							}
   //						}
   //					} catch(CoreException e){
   //						// executable extension could not be created: ignore this initializer
   //						if (JavaModelManager.CP_RESOLVE_VERBOSE || JavaModelManager.CP_RESOLVE_VERBOSE_FAILURE) {
   //							verbose_failed_to_instanciate_variable_initializer(variable, configElement);
   //							e.printStackTrace();
   //						}
   //					}
   //				}
   //			}
   //		}
   //		return null;
   //	}

   //	private static void verbose_failed_to_instanciate_variable_initializer(String variable, IConfigurationElement configElement) {
   //		Util.verbose(
   //			"CPContainer INIT - failed to instanciate initializer\n" + //$NON-NLS-1$
   //			"	variable: " + variable + '\n' + //$NON-NLS-1$
   //			"	class: " + configElement.getAttribute("class"), //$NON-NLS-1$ //$NON-NLS-2$
   //			System.err);
   //	}
   //
   //	private static void verbose_found_variable_initializer(String variable, IConfigurationElement configElement) {
   //		Util.verbose(
   //			"CPVariable INIT - found initializer\n" + //$NON-NLS-1$
   //			"	variable: " + variable + '\n' + //$NON-NLS-1$
   //			"	class: " + configElement.getAttribute("class")); //$NON-NLS-1$ //$NON-NLS-2$
   //	}

   //	/**
   //	 * Returns the names of all known classpath variables.
   //	 * <p>
   //	 * Classpath variable values are persisted locally to the workspace, and
   //	 * are preserved from session to session.
   //	 * <p>
   //	 *
   //	 * @return the list of classpath variable names
   //	 * @see #setClasspathVariable(String, IPath)
   //	 */
   //	public static String[] getClasspathVariableNames() {
   //		return JavaModelManager.getJavaModelManager().variableNames();
   //	}
   //
   /**
    * Returns a table of all known configurable options with their default values.
    * These options allow to configure the behaviour of the underlying components.
    * The client may safely use the result as a template that they can modify and
    * then pass to <code>setOptions</code>.
    * <p>
    * Helper constants have been defined on JavaCore for each of the option IDs
    * (categorized in Code assist option ID, Compiler option ID and Core option ID)
    * and some of their acceptable values (categorized in Option value). Some
    * options accept open value sets beyond the documented constant values.
    * <p>
    * Note: each release may add new options.
    *
    * @return a table of all known configurable options with their default values
    */
   public static HashMap<String, String> getDefaultOptions()
   {
      HashMap<String, String> defaultOptions = new HashMap<String, String>(10);
      // get encoding through resource plugin
      defaultOptions.put(JavaCore.CORE_ENCODING, JavaCore.getEncoding());

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
   private static String getEncoding()
   {
      return "UTF-8";
   }

   //	/**
   //	 * Returns an array that contains the resources generated by the Java builder when building the
   //	 * compilation units contained in the given region.
   //	 * <p>The contents of the array is accurate only if the elements of the given region have been built.</p>
   //	 * <p>The given region can contain instances of:</p>
   //	 * <ul>
   //	 * <li><code>org.eclipse.jdt.core.ICompilationUnit</code></li>
   //	 * <li><code>org.eclipse.jdt.core.IPackageFragment</code></li>
   //	 * <li><code>org.eclipse.jdt.core.IPackageFragmentRoot</code></li>
   //	 * <li><code>org.eclipse.jdt.core.IJavaProject</code></li>
   //	 * </ul>
   //	 * <p>All other types of <code>org.eclipse.jdt.core.IJavaElement</code> are ignored.</p>
   //	 *
   //	 * @param region the given region
   //	 * @param includesNonJavaResources a flag that indicates if non-java resources should be included
   //	 *
   //	 * @return an array that contains the resources generated by the Java builder when building the
   //	 * compilation units contained in the given region, an empty array if none
   //	 * @exception IllegalArgumentException if the given region is <code>null</code>
   //	 * @since 3.3
   //	 */
   //	public static IResource[] getGeneratedResources(IRegion region, boolean includesNonJavaResources) {
   //		if (region == null) throw new IllegalArgumentException("region cannot be null"); //$NON-NLS-1$
   //		IJavaElement[] elements = region.getElements();
   //		HashMap projectsStates = new HashMap();
   //		ArrayList collector = new ArrayList();
   //		for (int i = 0, max = elements.length; i < max; i++) {
   //			// collect all the java project
   //			IJavaElement element = elements[i];
   //			IJavaProject javaProject = element.getJavaProject();
   //			IProject project = javaProject.getProject();
   //			State state = null;
   //			State currentState = (State) projectsStates.get(project);
   //			if (currentState != null) {
   //				state = currentState;
   //			} else {
   //				state = (State) JavaModelManager.getJavaModelManager().getLastBuiltState(project, null);
   //				if (state != null) {
   //					projectsStates.put(project, state);
   //				}
   //			}
   //			if (state == null) continue;
   //			if (element.getElementType() == IJavaElement.JAVA_PROJECT) {
   //				IPackageFragmentRoot[] roots = null;
   //				try {
   //					roots = javaProject.getPackageFragmentRoots();
   //				} catch (JavaModelException e) {
   //					// ignore
   //				}
   //				if (roots == null) continue;
   //				IRegion region2 = JavaCore.newRegion();
   //				for (int j = 0; j < roots.length; j++) {
   //					region2.add(roots[j]);
   //				}
   //				IResource[] res = getGeneratedResources(region2, includesNonJavaResources);
   //				for (int j = 0, max2 = res.length; j < max2; j++) {
   //					collector.add(res[j]);
   //				}
   //				continue;
   //			}
   //			IPath outputLocation = null;
   //			try {
   //				outputLocation = javaProject.getOutputLocation();
   //			} catch (JavaModelException e) {
   //				// ignore
   //			}
   //			IJavaElement root = element;
   //			while (root != null && root.getElementType() != IJavaElement.PACKAGE_FRAGMENT_ROOT) {
   //				root = root.getParent();
   //			}
   //			if (root == null) continue;
   //			IPackageFragmentRoot packageFragmentRoot = (IPackageFragmentRoot) root;
   //			int rootPathSegmentCounts = packageFragmentRoot.getPath().segmentCount();
   //			try {
   //				IClasspathEntry entry = packageFragmentRoot.getRawClasspathEntry();
   //				IPath entryOutputLocation = entry.getOutputLocation();
   //				if (entryOutputLocation != null) {
   //					outputLocation = entryOutputLocation;
   //				}
   //			} catch (JavaModelException e) {
   //				e.printStackTrace();
   //			}
   //			if (outputLocation == null) continue;
   //			IContainer container = (IContainer) project.getWorkspace().getRoot().findMember(outputLocation);
   //			switch(element.getElementType()) {
   //				case IJavaElement.COMPILATION_UNIT :
   //					// get the .class files generated when this element was built
   //					ICompilationUnit unit = (ICompilationUnit) element;
   //					getGeneratedResource(unit, container, state, rootPathSegmentCounts, collector);
   //					break;
   //				case IJavaElement.PACKAGE_FRAGMENT :
   //					// collect all the .class files generated when all the units in this package were built
   //					IPackageFragment fragment = (IPackageFragment) element;
   //					ICompilationUnit[] compilationUnits = null;
   //					try {
   //						compilationUnits = fragment.getCompilationUnits();
   //					} catch (JavaModelException e) {
   //						// ignore
   //					}
   //					if (compilationUnits == null) continue;
   //					for (int j = 0, max2 = compilationUnits.length; j < max2; j++) {
   //						getGeneratedResource(compilationUnits[j], container, state, rootPathSegmentCounts, collector);
   //					}
   //					if (includesNonJavaResources) {
   //						// retrieve all non-java resources from the output location using the package fragment path
   //						Object[] nonJavaResources = null;
   //						try {
   //							nonJavaResources = fragment.getNonJavaResources();
   //						} catch (JavaModelException e) {
   //							// ignore
   //						}
   //						if (nonJavaResources != null) {
   //							addNonJavaResources(nonJavaResources, container, rootPathSegmentCounts, collector);
   //						}
   //					}
   //					break;
   //				case IJavaElement.PACKAGE_FRAGMENT_ROOT :
   //					// collect all the .class files generated when all the units in this package were built
   //					IPackageFragmentRoot fragmentRoot = (IPackageFragmentRoot) element;
   //					if (fragmentRoot.isArchive()) continue;
   //					IJavaElement[] children = null;
   //					try {
   //						children = fragmentRoot.getChildren();
   //					} catch (JavaModelException e) {
   //						// ignore
   //					}
   //					if (children == null) continue;
   //					for (int j = 0, max2 = children.length; j < max2; j++) {
   //						fragment = (IPackageFragment) children[j];
   //						ICompilationUnit[] units = null;
   //						try {
   //							units = fragment.getCompilationUnits();
   //						} catch (JavaModelException e) {
   //							// ignore
   //						}
   //						if (units == null) continue;
   //						for (int n = 0, max3 = units.length; n < max3; n++) {
   //							getGeneratedResource(units[n], container, state, rootPathSegmentCounts, collector);
   //						}
   //						if (includesNonJavaResources) {
   //							// retrieve all non-java resources from the output location using the package fragment path
   //							Object[] nonJavaResources = null;
   //							try {
   //								nonJavaResources = fragment.getNonJavaResources();
   //							} catch (JavaModelException e) {
   //								// ignore
   //							}
   //							if (nonJavaResources != null) {
   //								addNonJavaResources(nonJavaResources, container, rootPathSegmentCounts, collector);
   //							}
   //						}
   //					}
   //					break;
   //			}
   //		}
   //		int size = collector.size();
   //		if (size != 0) {
   //			IResource[] result = new IResource[size];
   //			collector.toArray(result);
   //			return result;
   //		}
   //		return NO_GENERATED_RESOURCES;
   //	}
   //
   //	private static void getGeneratedResource(ICompilationUnit unit,
   //			IContainer container,
   //			State state,
   //			int rootPathSegmentCounts,
   //			ArrayList collector) {
   //		IResource resource = unit.getResource();
   //		char[][] typeNames = state.getDefinedTypeNamesFor(resource.getProjectRelativePath().toString());
   //		if (typeNames != null) {
   //			IPath path = unit.getPath().removeFirstSegments(rootPathSegmentCounts).removeLastSegments(1);
   //			for (int j = 0, max2 = typeNames.length; j < max2; j++) {
   //				IPath localPath = path.append(new String(typeNames[j]) + ".class"); //$NON-NLS-1$
   //				IResource member = container.findMember(localPath);
   //				if (member != null && member.exists()) {
   //					collector.add(member);
   //				}
   //			}
   //		} else {
   //			IPath path = unit.getPath().removeFirstSegments(rootPathSegmentCounts).removeLastSegments(1);
   //			path = path.append(Util.getNameWithoutJavaLikeExtension(unit.getElementName()) + ".class"); //$NON-NLS-1$
   //			IResource member = container.findMember(path);
   //			if (member != null && member.exists()) {
   //				collector.add(member);
   //			}
   //		}
   //	}
   //
   //	/**
   //	 * Returns the single instance of the Java core plug-in runtime class.
   //	 * Equivalent to <code>(JavaCore) getPlugin()</code>.
   //	 *
   //	 * @return the single instance of the Java core plug-in runtime class
   //	 */
   //	public static JavaCore getJavaCore() {
   //		return (JavaCore) getPlugin();
   //	}
   //
   //	/**
   //	 * Returns the list of known Java-like extensions.
   //	 * Java like extension are defined in the {@link org.eclipse.core.runtime.Platform#getContentTypeManager()
   //	 * content type manager} for the {@link #JAVA_SOURCE_CONTENT_TYPE}.
   //	 * Note that a Java-like extension doesn't include the leading dot ('.').
   //	 * Also note that the "java" extension is always defined as a Java-like extension.
   //	 *
   //	 * @return the list of known Java-like extensions.
   //	 * @since 3.2
   //	 */
   //	public static String[] getJavaLikeExtensions() {
   //		return CharOperation.toStrings(Util.getJavaLikeExtensions());
   //	}
   //

   //	/**
   //	 * Returns the option that can be used to configure the severity of the
   //	 * compiler problem identified by <code>problemID</code> if any,
   //	 * <code>null</code> otherwise. Non-null return values are taken from the
   //	 * constants defined by this class whose names start with
   //	 * <code>COMPILER_PB</code> and for which the possible values of the
   //	 * option are defined by <code>{ "error", "warning", "ignore" }</code>. A
   //	 * null return value means that the provided problem ID is unknown or that
   //	 * it matches a problem whose severity cannot be configured.
   //	 * @param problemID one of the problem IDs defined by {@link IProblem}
   //	 * @return the option that can be used to configure the severity of the
   //	 *         compiler problem identified by <code>problemID</code> if any,
   //	 *         <code>null</code> otherwise
   //	 * @since 3.4
   //	 */
   //	public static String getOptionForConfigurableSeverity(int problemID) {
   //		return CompilerOptions.optionKeyFromIrritant(ProblemReporter.getIrritant(problemID));
   //	}

   /**
    * Returns the table of the current options. Initially, all options have their default values,
    * and this method returns a table that includes all known options.
    * <p>
    * Helper constants have been defined on JavaCore for each of the option IDs
    * (categorized in Code assist option ID, Compiler option ID and Core option ID)
    * and some of their acceptable values (categorized in Option value). Some
    * options accept open value sets beyond the documented constant values.
    * <p>
    * Note: each release may add new options.
    * <p>Returns a default set of options even if the platform is not running.</p>
    *
    * @return table of current settings of all options
    *   (key type: <code>String</code>; value type: <code>String</code>)
    * @see #getDefaultOptions()
    * @see JavaCorePreferenceInitializer for changing default settings
    */
   public static HashMap<String, String> getOptions()
   {
      HashMap<String, String> options = new HashMap<String, String>(10);
      // get encoding through resource plugin
      options.put(JavaCore.CORE_ENCODING, JavaCore.getEncoding());
      options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_6);
      options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_6);
      options.put(CompilerOptions.OPTION_TargetPlatform, JavaCore.VERSION_1_6);
      return options;
   }

   //	/**
   //	 * This is a helper method, which returns the resolved classpath entry denoted
   //	 * by a given entry (if it is a variable entry). It is obtained by resolving the variable
   //	 * reference in the first segment. Returns <code>null</code> if unable to resolve using
   //	 * the following algorithm:
   //	 * <ul>
   //	 * <li> if variable segment cannot be resolved, returns <code>null</code></li>
   //	 * <li> finds a project, JAR or binary folder in the workspace at the resolved path location</li>
   //	 * <li> if none finds an external JAR file or folder outside the workspace at the resolved path location </li>
   //	 * <li> if none returns <code>null</code></li>
   //	 * </ul>
   //	 * <p>
   //	 * Variable source attachment path and root path are also resolved and recorded in the resulting classpath entry.
   //	 * <p>
   //	 * NOTE: This helper method does not handle classpath containers, for which should rather be used
   //	 * <code>JavaCore#getClasspathContainer(IPath, IJavaProject)</code>.
   //	 * <p>
   //	 *
   //	 * @param entry the given variable entry
   //	 * @return the resolved library or project classpath entry, or <code>null</code>
   //	 *   if the given variable entry could not be resolved to a valid classpath entry
   //	 */
   //	public static IClasspathEntry getResolvedClasspathEntry(IClasspathEntry entry) {
   //		return JavaModelManager.getJavaModelManager().resolveVariableEntry(entry, false/*don't use previous session value*/);
   //	}

   //	/**
   //	 * Resolve a variable path (helper method).
   //	 *
   //	 * @param variablePath the given variable path
   //	 * @return the resolved variable path or <code>null</code> if none
   //	 */
   //	public static IPath getResolvedVariablePath(IPath variablePath) {
   //		return JavaModelManager.getJavaModelManager().getResolvedVariablePath(variablePath, false/*don't use previous session value*/);
   //	}

   //	/**
   //	 * Answers the shared working copies currently registered for this buffer factory.
   //	 * Working copies can be shared by several clients using the same buffer factory,see
   //	 * <code>IWorkingCopy.getSharedWorkingCopy</code>.
   //	 *
   //	 * @param factory the given buffer factory
   //	 * @return the list of shared working copies for a given buffer factory
   //	 * @since 2.0
   //	 * @deprecated Use {@link #getWorkingCopies(WorkingCopyOwner)} instead
   //	 */
   //	public static IWorkingCopy[] getSharedWorkingCopies(IBufferFactory factory){
   //
   //		// if factory is null, default factory must be used
   //		if (factory == null) factory = BufferManager.getDefaultBufferManager().getDefaultBufferFactory();
   //
   //		return getWorkingCopies(BufferFactoryWrapper.create(factory));
   //	}

   //	/**
   //	 * Returns the names of all defined user libraries. The corresponding classpath container path
   //	 * is the name appended to the USER_LIBRARY_CONTAINER_ID.
   //	 * @return Return an array containing the names of all known user defined.
   //	 * @since 3.0
   //	 */
   //	public static String[] getUserLibraryNames() {
   //		 return JavaModelManager.getUserLibraryManager().getUserLibraryNames();
   //	}

   //	/**
   //	 * Returns the working copies that have the given owner.
   //	 * Only compilation units in working copy mode are returned.
   //	 * If the owner is <code>null</code>, primary working copies are returned.
   //	 *
   //	 * @param owner the given working copy owner or <code>null</code> for primary working copy owner
   //	 * @return the list of working copies for a given owner
   //	 * @since 3.0
   //	 */
   //	public static ICompilationUnit[] getWorkingCopies(){
   //
   //		JavaModelManager manager = JavaModelManager.getJavaModelManager();
   //		if (owner == null) owner = DefaultWorkingCopyOwner.PRIMARY;
   //		ICompilationUnit[] result = manager.getWorkingCopies(owner, false/*don't add primary WCs*/);
   //		if (result == null) return JavaModelManager.NO_WORKING_COPY;
   //		return result;
   //	}

   //	/**
   //	 * Initializes JavaCore internal structures to allow subsequent operations (such
   //	 * as the ones that need a resolved classpath) to run full speed. A client may
   //	 * choose to call this method in a background thread early after the workspace
   //	 * has started so that the initialization is transparent to the user.
   //	 * <p>
   //	 * However calling this method is optional. Services will lazily perform
   //	 * initialization when invoked. This is only a way to reduce initialization
   //	 * overhead on user actions, if it can be performed before at some
   //	 * appropriate moment.
   //	 * </p><p>
   //	 * This initialization runs accross all Java projects in the workspace. Thus the
   //	 * workspace root scheduling rule is used during this operation.
   //	 * </p><p>
   //	 * This method may return before the initialization is complete. The
   //	 * initialization will then continue in a background thread.
   //	 * </p><p>
   //	 * This method can be called concurrently.
   //	 * </p>
   //	 *
   //	 * @param monitor a progress monitor, or <code>null</code> if progress
   //	 *    reporting and cancellation are not desired
   //	 * @exception CoreException if the initialization fails,
   //	 * 		the status of the exception indicates the reason of the failure
   //	 * @since 3.1
   //	 */
   //	public static void initializeAfterLoad(IProgressMonitor monitor) throws CoreException {
   //		try {
   //			if (monitor != null) {
   //				monitor.beginTask(Messages.javamodel_initialization, 100);
   //				monitor.subTask(Messages.javamodel_configuring_classpath_containers);
   //			}
   //
   //			// initialize all containers and variables
   //			JavaModelManager manager = JavaModelManager.getJavaModelManager();
   //			SubProgressMonitor subMonitor = null;
   //			try {
   //				if (monitor != null) {
   //					subMonitor = new SubProgressMonitor(monitor, 50); // 50% of the time is spent in initializing containers and variables
   //					subMonitor.beginTask("", 100); //$NON-NLS-1$
   //					subMonitor.worked(5); // give feedback to the user that something is happening
   //					manager.batchContainerInitializationsProgress.initializeAfterLoadMonitor.set(subMonitor);
   //				}
   //				if (manager.forceBatchInitializations(true/*initAfterLoad*/)) { // if no other thread has started the batch container initializations
   //					manager.getClasspathContainer(Path.EMPTY, null); // force the batch initialization
   //				} else { // else wait for the batch initialization to finish
   //					while (manager.batchContainerInitializations == JavaModelManager.BATCH_INITIALIZATION_IN_PROGRESS) {
   //						if (subMonitor != null) {
   //							subMonitor.subTask(manager.batchContainerInitializationsProgress.subTaskName);
   //							subMonitor.worked(manager.batchContainerInitializationsProgress.getWorked());
   //						}
   //						synchronized(manager) {
   //							try {
   //								manager.wait(100);
   //							} catch (InterruptedException e) {
   //								// continue
   //							}
   //						}
   //					}
   //				}
   //			} finally {
   //				if (subMonitor != null)
   //					subMonitor.done();
   //				manager.batchContainerInitializationsProgress.initializeAfterLoadMonitor.set(null);
   //			}
   //
   //			// avoid leaking source attachment properties (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=183413 )
   //			// and recreate links for external folders if needed
   //			if (monitor != null)
   //				monitor.subTask(Messages.javamodel_resetting_source_attachment_properties);
   //			final IJavaProject[] projects = manager.getJavaModel().getJavaProjects();
   //			HashSet visitedPaths = new HashSet();
   //			ExternalFoldersManager externalFoldersManager = JavaModelManager.getExternalManager();
   //			for (int i = 0, length = projects.length; i < length; i++) {
   //				JavaProject javaProject = (JavaProject) projects[i];
   //				IClasspathEntry[] classpath;
   //				try {
   //					classpath = javaProject.getResolvedClasspath();
   //				} catch (JavaModelException e) {
   //					// project no longer exist: ignore
   //					continue;
   //				}
   //				if (classpath != null) {
   //					for (int j = 0, length2 = classpath.length; j < length2; j++) {
   //						IClasspathEntry entry = classpath[j];
   //						if (entry.getSourceAttachmentPath() != null) {
   //							IPath entryPath = entry.getPath();
   //							if (visitedPaths.add(entryPath)) {
   //								Util.setSourceAttachmentProperty(entryPath, null);
   //							}
   //						}
   //						// else source might have been attached by IPackageFragmentRoot#attachSource(...), we keep it
   //						if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
   //							IPath entryPath = entry.getPath();
   //							if (ExternalFoldersManager.isExternalFolderPath(entryPath) && externalFoldersManager.getFolder(entryPath) == null) {
   //								externalFoldersManager.addFolder(entryPath, true);
   //							}
   //						}
   //					}
   //				}
   //			}
   //			try {
   //				externalFoldersManager.createPendingFolders(monitor);
   //			}
   //			catch(JavaModelException jme) {
   //				// Creation of external folder project failed. Log it and continue;
   //				Util.log(jme, "Error while processing external folders"); //$NON-NLS-1$
   //			}
   //			// initialize delta state
   //			if (monitor != null)
   //				monitor.subTask(Messages.javamodel_initializing_delta_state);
   //			manager.deltaState.rootsAreStale = true; // in case it was already initialized before we cleaned up the source attachment proprties
   //			manager.deltaState.initializeRoots(true/*initAfteLoad*/);
   //
   //			// dummy query for waiting until the indexes are ready
   //			if (monitor != null)
   //				monitor.subTask(Messages.javamodel_configuring_searchengine);
   //			SearchEngine engine = new SearchEngine();
   //			IJavaSearchScope scope = SearchEngine.createWorkspaceScope();
   //			try {
   //				engine.searchAllTypeNames(
   //					null,
   //					SearchPattern.R_EXACT_MATCH,
   //					"!@$#!@".toCharArray(), //$NON-NLS-1$
   //					SearchPattern.R_PATTERN_MATCH | SearchPattern.R_CASE_SENSITIVE,
   //					IJavaSearchConstants.CLASS,
   //					scope,
   //					new TypeNameRequestor() {
   //						public void acceptType(
   //							int modifiers,
   //							char[] packageName,
   //							char[] simpleTypeName,
   //							char[][] enclosingTypeNames,
   //							String path) {
   //							// no type to accept
   //						}
   //					},
   //					// will not activate index query caches if indexes are not ready, since it would take to long
   //					// to wait until indexes are fully rebuild
   //					IJavaSearchConstants.CANCEL_IF_NOT_READY_TO_SEARCH,
   //					monitor == null ? null : new SubProgressMonitor(monitor, 49) // 49% of the time is spent in the dummy search
   //				);
   //			} catch (JavaModelException e) {
   //				// /search failed: ignore
   //			} catch (OperationCanceledException e) {
   //				if (monitor != null && monitor.isCanceled())
   //					throw e;
   //				// else indexes were not ready: catch the exception so that jars are still refreshed
   //			}
   //
   //			// check if the build state version number has changed since last session
   //			// (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=98969)
   //			if (monitor != null)
   //				monitor.subTask(Messages.javamodel_getting_build_state_number);
   //			QualifiedName qName = new QualifiedName(JavaCore.PLUGIN_ID, "stateVersionNumber"); //$NON-NLS-1$
   //			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
   //			String versionNumber = null;
   //			try {
   //				versionNumber = root.getPersistentProperty(qName);
   //			} catch (CoreException e) {
   //				// could not read version number: consider it is new
   //			}
   //			final JavaModel model = manager.getJavaModel();
   //			String newVersionNumber = Byte.toString(State.VERSION);
   //			if (!newVersionNumber.equals(versionNumber)) {
   //				// build state version number has changed: touch every projects to force a rebuild
   //				if (JavaBuilder.DEBUG)
   //					System.out.println("Build state version number has changed"); //$NON-NLS-1$
   //				IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
   //					public void run(IProgressMonitor progressMonitor2) throws CoreException {
   //						for (int i = 0, length = projects.length; i < length; i++) {
   //							IJavaProject project = projects[i];
   //							try {
   //								if (JavaBuilder.DEBUG)
   //									System.out.println("Touching " + project.getElementName()); //$NON-NLS-1$
   //								new ClasspathValidation((JavaProject) project).validate(); // https://bugs.eclipse.org/bugs/show_bug.cgi?id=287164
   //								project.getProject().touch(progressMonitor2);
   //							} catch (CoreException e) {
   //								// could not touch this project: ignore
   //							}
   //						}
   //					}
   //				};
   //				if (monitor != null)
   //					monitor.subTask(Messages.javamodel_building_after_upgrade);
   //				try {
   //					ResourcesPlugin.getWorkspace().run(runnable, monitor);
   //				} catch (CoreException e) {
   //					// could not touch all projects
   //				}
   //				try {
   //					root.setPersistentProperty(qName, newVersionNumber);
   //				} catch (CoreException e) {
   //					Util.log(e, "Could not persist build state version number"); //$NON-NLS-1$
   //				}
   //			}
   //
   //			// ensure external jars are refreshed (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=93668)
   //			try {
   //				if (monitor != null)
   //					monitor.subTask(Messages.javamodel_refreshing_external_jars);
   //				model.refreshExternalArchives(
   //					null/*refresh all projects*/,
   //					monitor == null ? null : new SubProgressMonitor(monitor, 1) // 1% of the time is spent in jar refresh
   //				);
   //			} catch (JavaModelException e) {
   //				// refreshing failed: ignore
   //			}
   //
   //		} finally {
   //			if (monitor != null) monitor.done();
   //		}
   //	}

   //	/**
   //	 * Returns whether a given classpath variable is read-only or not.
   //	 *
   //	 * @param variableName
   //	 * @return <code>true</code> if the classpath variable is read-only,
   //	 * 	<code>false</code> otherwise.
   //	 * @since 3.3
   //	 */
   //	public static boolean isClasspathVariableReadOnly(String variableName) {
   //	    return JavaModelManager.getJavaModelManager().readOnlyVariables.contains(variableName);
   //	}
   //
   //	/**
   //	 * Returns whether the given file name's extension is a Java-like extension.
   //	 *
   //	 * @return whether the given file name's extension is a Java-like extension
   //	 * @see #getJavaLikeExtensions()
   //	 * @since 3.2
   //	 */
   //	public static boolean isJavaLikeFileName(String fileName) {
   //		return Util.isJavaLikeFileName(fileName);
   //	}
   //
   //	/**
   //	 * Returns whether the given marker references the given Java element.
   //	 * Used for markers, which denote a Java element rather than a resource.
   //	 *
   //	 * @param element the element
   //	 * @param marker the marker
   //	 * @return <code>true</code> if the marker references the element, false otherwise
   //	 * @exception CoreException if the <code>IMarker.getAttribute</code> on the marker fails
   //	 */
   //	public static boolean isReferencedBy(IJavaElement element, IMarker marker) throws CoreException {
   //
   //		// only match units or classfiles
   //		if (element instanceof IMember){
   //			IMember member = (IMember) element;
   //			if (member.isBinary()){
   //				element = member.getClassFile();
   //			} else {
   //				element = member.getCompilationUnit();
   //			}
   //		}
   //		if (element == null) return false;
   //		if (marker == null) return false;
   //
   //		String markerHandleId = (String)marker.getAttribute(ATT_HANDLE_ID);
   //		if (markerHandleId == null) return false;
   //
   //		IJavaElement markerElement = JavaCore.create(markerHandleId);
   //		while (true){
   //			if (element.equals(markerElement)) return true; // external elements may still be equal with different handleIDs.
   //
   //			// cycle through enclosing types in case marker is associated with a classfile (15568)
   //			if (markerElement instanceof IClassFile){
   //				IType enclosingType = ((IClassFile)markerElement).getType().getDeclaringType();
   //				if (enclosingType != null){
   //					markerElement = enclosingType.getClassFile(); // retry with immediate enclosing classfile
   //					continue;
   //				}
   //			}
   //			break;
   //		}
   //		return false;
   //	}

   //	/**
   //	 * Returns whether the given marker delta references the given Java element.
   //	 * Used for markers deltas, which denote a Java element rather than a resource.
   //	 *
   //	 * @param element the element
   //	 * @param markerDelta the marker delta
   //	 * @return <code>true</code> if the marker delta references the element
   //	 * @exception CoreException if the  <code>IMarkerDelta.getAttribute</code> on the marker delta fails
   //	 */
   //	public static boolean isReferencedBy(IJavaElement element, IMarkerDelta markerDelta) throws CoreException {
   //
   //		// only match units or classfiles
   //		if (element instanceof IMember){
   //			IMember member = (IMember) element;
   //			if (member.isBinary()){
   //				element = member.getClassFile();
   //			} else {
   //				element = member.getCompilationUnit();
   //			}
   //		}
   //		if (element == null) return false;
   //		if (markerDelta == null) return false;
   //
   //		String markerDeltarHandleId = (String)markerDelta.getAttribute(ATT_HANDLE_ID);
   //		if (markerDeltarHandleId == null) return false;
   //
   //		IJavaElement markerElement = JavaCore.create(markerDeltarHandleId);
   //		while (true){
   //			if (element.equals(markerElement)) return true; // external elements may still be equal with different handleIDs.
   //
   //			// cycle through enclosing types in case marker is associated with a classfile (15568)
   //			if (markerElement instanceof IClassFile){
   //				IType enclosingType = ((IClassFile)markerElement).getType().getDeclaringType();
   //				if (enclosingType != null){
   //					markerElement = enclosingType.getClassFile(); // retry with immediate enclosing classfile
   //					continue;
   //				}
   //			}
   //			break;
   //		}
   //		return false;
   //	}

   //	/**
   //	 * Creates and returns a new access rule with the given file pattern and kind.
   //	 * <p>
   //	 * The rule kind is one of {@link IAccessRule#K_ACCESSIBLE}, {@link IAccessRule#K_DISCOURAGED},
   //	 * or {@link IAccessRule#K_NON_ACCESSIBLE}, optionally combined with {@link IAccessRule#IGNORE_IF_BETTER},
   //	 * e..g. <code>IAccessRule.K_NON_ACCESSIBLE | IAccessRule.IGNORE_IF_BETTER</code>.
   //	 * </p>
   //	 *
   //	 * @param filePattern the file pattern this access rule should match
   //	 * @param kind one of {@link IAccessRule#K_ACCESSIBLE}, {@link IAccessRule#K_DISCOURAGED},
   //	 *                     or {@link IAccessRule#K_NON_ACCESSIBLE}, optionally combined with
   //	 *                     {@link IAccessRule#IGNORE_IF_BETTER}
   //	 * @return a new access rule
   //	 * @since 3.1
   //	 */
   //	public static IAccessRule newAccessRule(IPath filePattern, int kind) {
   //		return new ClasspathAccessRule(filePattern, kind);
   //	}
   //
   //	/**
   //	 * Creates and returns a new classpath attribute with the given name and the given value.
   //	 *
   //	 * @return a new classpath attribute
   //	 * @since 3.1
   //	 */
   //	public static IClasspathAttribute newClasspathAttribute(String name, String value) {
   //		return new ClasspathAttribute(name, value);
   //	}

   //	/**
   //	 * Creates and returns a new classpath entry of kind <code>CPE_CONTAINER</code>
   //	 * for the given path. This method is fully equivalent to calling
   //	 * {@link #newContainerEntry(IPath, IAccessRule[], IClasspathAttribute[], boolean)
   //	 * newContainerEntry(containerPath, new IAccessRule[0], new IClasspathAttribute[0], false)}.
   //	 * <p>
   //	 * @param containerPath the path identifying the container, it must be formed of two
   //	 * 	segments
   //	 * @return a new container classpath entry
   //	 *
   //	 * @see JavaCore#getClasspathContainer(IPath, IJavaProject)
   //	 * @since 2.0
   //	 */
   //	public static IClasspathEntry newContainerEntry(IPath containerPath) {
   //		return newContainerEntry(
   //		containerPath,
   //		ClasspathEntry.NO_ACCESS_RULES,
   //		ClasspathEntry.NO_EXTRA_ATTRIBUTES,
   //		false/*not exported*/);
   //	}

   //	/**
   //	 * Creates and returns a new classpath entry of kind <code>CPE_CONTAINER</code>
   //	 * for the given path. This method is fully equivalent to calling
   //	 * {@link #newContainerEntry(IPath, IAccessRule[], IClasspathAttribute[], boolean)
   //	 * newContainerEntry(containerPath, new IAccessRule[0], new IClasspathAttribute[0], isExported)}.
   //	 *
   //	 * @param containerPath the path identifying the container, it must be formed of at least
   //	 * 	one segment (ID+hints)
   //	 * @param isExported a boolean indicating whether this entry is contributed to dependent
   //	 *    projects in addition to the output location
   //	 * @return a new container classpath entry
   //	 *
   //	 * @see JavaCore#getClasspathContainer(IPath, IJavaProject)
   //	 * @see JavaCore#setClasspathContainer(IPath, IJavaProject[], IClasspathContainer[], IProgressMonitor)
   //	 * @since 2.0
   //	 */
   //	public static IClasspathEntry newContainerEntry(IPath containerPath, boolean isExported) {
   //		return newContainerEntry(
   //			containerPath,
   //			ClasspathEntry.NO_ACCESS_RULES,
   //			ClasspathEntry.NO_EXTRA_ATTRIBUTES,
   //			isExported);
   //	}

   //	/**
   //	 * Creates and returns a new classpath entry of kind <code>CPE_CONTAINER</code>
   //	 * for the given path. The path of the container will be used during resolution so as to map this
   //	 * container entry to a set of other classpath entries the container is acting for.
   //	 * <p>
   //	 * A container entry allows to express indirect references to a set of libraries, projects and variable entries,
   //	 * which can be interpreted differently for each Java project where it is used.
   //	 * A classpath container entry can be resolved using <code>JavaCore.getResolvedClasspathContainer</code>,
   //	 * and updated with <code>JavaCore.classpathContainerChanged</code>
   //	 * <p>
   //	 * A container is exclusively resolved by a <code>ClasspathContainerInitializer</code> registered onto the
   //	 * extension point "org.eclipse.jdt.core.classpathContainerInitializer".
   //	 * <p>
   //	 * A container path must be formed of at least one segment, where: <ul>
   //	 * <li> the first segment is a unique ID identifying the target container, there must be a container initializer registered
   //	 * 	onto this ID through the extension point  "org.eclipse.jdt.core.classpathContainerInitializer". </li>
   //	 * <li> the remaining segments will be passed onto the initializer, and can be used as additional
   //	 * 	hints during the initialization phase. </li>
   //	 * </ul>
   //	 * <p>
   //	 * Example of an ClasspathContainerInitializer for a classpath container denoting a default JDK container:
   //	 * <pre>
   //	 * containerEntry = JavaCore.newContainerEntry(new Path("MyProvidedJDK/default"));
   //	 *
   //	 * &lt;extension
   //	 *    point="org.eclipse.jdt.core.classpathContainerInitializer"&gt;
   //	 *    &lt;containerInitializer
   //	 *       id="MyProvidedJDK"
   //	 *       class="com.example.MyInitializer"/&gt;
   //	 * </pre>
   //	 * <p>
   //	 * The access rules determine the set of accessible source and class files
   //	 * in the container. If the list of access rules is empty, then all files
   //	 * in this container are accessible.
   //	 * See {@link IAccessRule} for a detailed description of access
   //	 * rules. Note that if an entry defined by the container defines access rules,
   //	 * then these access rules are combined with the given access rules.
   //	 * The given access rules are considered first, then the entry's access rules are
   //	 * considered.
   //	 * </p>
   //	 * <p>
   //	 * The <code>extraAttributes</code> list contains name/value pairs that must be persisted with
   //	 * this entry. If no extra attributes are provided, an empty array must be passed in.<br>
   //	 * Note that this list should not contain any duplicate name.
   //	 * </p>
   //	 * <p>
   //	 * The <code>isExported</code> flag indicates whether this entry is contributed to dependent
   //	 * projects. If not exported, dependent projects will not see any of the classes from this entry.
   //	 * If exported, dependent projects will concatenate the accessible files patterns of this entry with the
   //	 * accessible files patterns of the projects, and they will concatenate the non accessible files patterns of this entry
   //	 * with the non accessible files patterns of the project.
   //	 * </p>
   //	 * <p>
   //	 * Note that this operation does not attempt to validate classpath containers
   //	 * or access the resources at the given paths.
   //	 * </p>
   //	 *
   //	 * @param containerPath the path identifying the container, it must be formed of at least
   //	 * 	one segment (ID+hints)
   //	 * @param accessRules the possibly empty list of access rules for this entry
   //	 * @param extraAttributes the possibly empty list of extra attributes to persist with this entry
   //	 * @param isExported a boolean indicating whether this entry is contributed to dependent
   //	 *    projects in addition to the output location
   //	 * @return a new container classpath entry
   //	 *
   //	 * @see JavaCore#getClasspathContainer(IPath, IJavaProject)
   //	 * @see JavaCore#setClasspathContainer(IPath, IJavaProject[], IClasspathContainer[], IProgressMonitor)
   //	 * @see JavaCore#newContainerEntry(IPath, boolean)
   //	 * @see JavaCore#newAccessRule(IPath, int)
   //	 * @since 3.1
   //	 */
   //	public static IClasspathEntry newContainerEntry(
   //			IPath containerPath,
   //			IAccessRule[] accessRules,
   //			IClasspathAttribute[] extraAttributes,
   //			boolean isExported) {
   //
   //		if (containerPath == null) {
   //			throw new ClasspathEntry.AssertionFailedException("Container path cannot be null"); //$NON-NLS-1$
   //		} else if (containerPath.segmentCount() < 1) {
   //			throw new ClasspathEntry.AssertionFailedException("Illegal classpath container path: \'" + containerPath.makeRelative().toString() + "\', must have at least one segment (containerID+hints)"); //$NON-NLS-1$//$NON-NLS-2$
   //		}
   //		if (accessRules == null) {
   //			accessRules = ClasspathEntry.NO_ACCESS_RULES;
   //		}
   //		if (extraAttributes == null) {
   //			extraAttributes = ClasspathEntry.NO_EXTRA_ATTRIBUTES;
   //		}
   //		return new ClasspathEntry(
   //			IPackageFragmentRoot.K_SOURCE,
   //			IClasspathEntry.CPE_CONTAINER,
   //			containerPath,
   //			ClasspathEntry.INCLUDE_ALL, // inclusion patterns
   //			ClasspathEntry.EXCLUDE_NONE, // exclusion patterns
   //			null, // source attachment
   //			null, // source attachment root
   //			null, // specific output folder
   //			isExported,
   //			accessRules,
   //			true, // combine access rules
   //			extraAttributes);
   //	}

   //	/**
   //	 * Creates and returns a type hierarchy for all types in the given
   //	 * region, considering subtypes within that region and considering types in the
   //	 * working copies with the given owner.
   //	 * In other words, the owner's working copies will take
   //	 * precedence over their original compilation units in the workspace.
   //	 * <p>
   //	 * Note that if a working copy is empty, it will be as if the original compilation
   //	 * unit had been deleted.
   //	 * <p>
   //	 *
   //	 * @param monitor the given progress monitor
   //	 * @param region the given region
   //	 * @param owner the owner of working copies that take precedence over their original compilation units,
   //	 *   or <code>null</code> if the primary working copy owner should be used
   //	 * @exception JavaModelException if an element in the region does not exist or if an
   //	 *		exception occurs while accessing its corresponding resource
   //	 * @exception IllegalArgumentException if region is <code>null</code>
   //	 * @return a type hierarchy for all types in the given
   //	 * region, considering subtypes within that region
   //	 * @since 3.1
   //	 */
   //	public static ITypeHierarchy newTypeHierarchy(IRegion region, WorkingCopyOwner owner, IProgressMonitor monitor) throws JavaModelException {
   //		if (region == null) {
   //			throw new IllegalArgumentException(Messages.hierarchy_nullRegion);
   //		}
   //		ICompilationUnit[] workingCopies = JavaModelManager.getJavaModelManager().getWorkingCopies(owner, true/*add primary working copies*/);
   //		CreateTypeHierarchyOperation op =
   //			new CreateTypeHierarchyOperation(region, workingCopies, null, true/*compute subtypes*/);
   //		op.runOperation(monitor);
   //		return op.getResult();
   //	}

   //	/**
   //	 * Creates and returns a new non-exported classpath entry of kind <code>CPE_LIBRARY</code> for the
   //	 * JAR or folder identified by the given absolute path. This specifies that all package fragments
   //	 * within the root will have children of type <code>IClassFile</code>.
   //	 * This method is fully equivalent to calling
   //	 * {@link #newLibraryEntry(IPath, IPath, IPath, IAccessRule[], IClasspathAttribute[], boolean)
   //	 * newLibraryEntry(path, sourceAttachmentPath, sourceAttachmentRootPath, new IAccessRule[0], new IClasspathAttribute[0], false)}.
   //	 *
   //	 * @param path the path to the library
   //	 * @param sourceAttachmentPath the absolute path of the corresponding source archive or folder,
   //	 *    or <code>null</code> if none. Note, since 3.0, an empty path is allowed to denote no source attachment.
   //	 *    Since 3.4, this path can also denote a path external to the workspace.
   //	 *   and will be automatically converted to <code>null</code>.
   //	 * @param sourceAttachmentRootPath the location of the root of the source files within the source archive or folder
   //	 *    or <code>null</code> if this location should be automatically detected.
   //	 * @return a new library classpath entry
   //	 */
   //	public static IClasspathEntry newLibraryEntry(
   //		IPath path,
   //		IPath sourceAttachmentPath,
   //		IPath sourceAttachmentRootPath) {
   //
   //		return newLibraryEntry(
   //			path,
   //			sourceAttachmentPath,
   //			sourceAttachmentRootPath,
   //			ClasspathEntry.NO_ACCESS_RULES,
   //			ClasspathEntry.NO_EXTRA_ATTRIBUTES,
   //			false/*not exported*/);
   //	}

   //	/**
   //	 * Creates and returns a new classpath entry of kind <code>CPE_LIBRARY</code> for the JAR or folder
   //	 * identified by the given absolute path. This specifies that all package fragments within the root
   //	 * will have children of type <code>IClassFile</code>.
   //	 * This method is fully equivalent to calling
   //	 * {@link #newLibraryEntry(IPath, IPath, IPath, IAccessRule[], IClasspathAttribute[], boolean)
   //	 * newLibraryEntry(path, sourceAttachmentPath, sourceAttachmentRootPath, new IAccessRule[0], new IClasspathAttribute[0], isExported)}.
   //	 *
   //	 * @param path the path to the library
   //	 * @param sourceAttachmentPath the absolute path of the corresponding source archive or folder,
   //	 *    or <code>null</code> if none. Note, since 3.0, an empty path is allowed to denote no source attachment.
   //	 *   and will be automatically converted to <code>null</code>. Since 3.4, this path can also denote a path external
   //	 *   to the workspace.
   //	 * @param sourceAttachmentRootPath the location of the root of the source files within the source archive or folder
   //	 *    or <code>null</code> if this location should be automatically detected.
   //	 * @param isExported indicates whether this entry is contributed to dependent
   //	 * 	  projects in addition to the output location
   //	 * @return a new library classpath entry
   //	 * @since 2.0
   //	 */
   //	public static IClasspathEntry newLibraryEntry(
   //		IPath path,
   //		IPath sourceAttachmentPath,
   //		IPath sourceAttachmentRootPath,
   //		boolean isExported) {
   //
   //		return newLibraryEntry(
   //			path,
   //			sourceAttachmentPath,
   //			sourceAttachmentRootPath,
   //			ClasspathEntry.NO_ACCESS_RULES,
   //			ClasspathEntry.NO_EXTRA_ATTRIBUTES,
   //			isExported);
   //	}

   //	/**
   //	 * Creates and returns a new classpath entry of kind <code>CPE_LIBRARY</code> for the JAR or folder
   //	 * identified by the given absolute path. This specifies that all package fragments within the root
   //	 * will have children of type <code>IClassFile</code>.
   //	 * <p>
   //	 * A library entry is used to denote a prerequisite JAR or root folder containing binaries.
   //	 * The target JAR can either be defined internally to the workspace (absolute path relative
   //	 * to the workspace root), or externally to the workspace (absolute path in the file system).
   //	 * The target root folder can also be defined internally to the workspace (absolute path relative
   //	 * to the workspace root), or - since 3.4 - externally to the workspace (absolute path in the file system).
   //	 * Since 3.5, the path to the library can also be relative to the project using ".." as the first segment. 
   //	 * <p>
   //	 * e.g. Here are some examples of binary path usage<ul>
   //	 *	<li><code> "c:\jdk1.2.2\jre\lib\rt.jar" </code> - reference to an external JAR on Windows</li>
   //	 *	<li><code> "/Project/someLib.jar" </code> - reference to an internal JAR on Windows or Linux</li>
   //	 *	<li><code> "/Project/classes/" </code> - reference to an internal binary folder on Windows or Linux</li>
   //	 *	<li><code> "/home/usr/classes" </code> - reference to an external binary folder on Linux</li>
   //	 *	<li><code> "../../lib/someLib.jar" </code> - reference to an external JAR that is a sibbling of the workspace on either platform</li>
   //	 * </ul>
   //	 * Note that on non-Windows platform, a path <code>"/some/lib.jar"</code> is ambiguous.
   //	 * It can be a path to an external JAR (its file system path being <code>"/some/lib.jar"</code>)
   //	 * or it can be a path to an internal JAR (<code>"some"</code> being a project in the workspace).
   //	 * Such an ambiguity is solved when the classpath entry is used (e.g. in {@link IJavaProject#getPackageFragmentRoots()}).
   //	 * If the resource <code>"lib.jar"</code> exists in project <code>"some"</code>, then it is considered an
   //	 * internal JAR. Otherwise it is an external JAR.
   //	 * <p>Also note that this operation does not attempt to validate or access the
   //	 * resources at the given paths.
   //	 * </p><p>
   //	 * The access rules determine the set of accessible class files
   //	 * in the library. If the list of access rules is empty then all files
   //	 * in this library are accessible.
   //	 * See {@link IAccessRule} for a detailed description of access
   //	 * rules.
   //	 * </p>
   //	 * <p>
   //	 * The <code>extraAttributes</code> list contains name/value pairs that must be persisted with
   //	 * this entry. If no extra attributes are provided, an empty array must be passed in.<br>
   //	 * Note that this list should not contain any duplicate name.
   //	 * </p>
   //	 * <p>
   //	 * The <code>isExported</code> flag indicates whether this entry is contributed to dependent
   //	 * projects. If not exported, dependent projects will not see any of the classes from this entry.
   //	 * If exported, dependent projects will concatenate the accessible files patterns of this entry with the
   //	 * accessible files patterns of the projects, and they will concatenate the non accessible files patterns of this entry
   //	 * with the non accessible files patterns of the project.
   //	 * </p>
   //	 * <p>
   //	 * Since 3.5, if the libray is a ZIP archive, the "Class-Path" clause (if any) in the "META-INF/MANIFEST.MF" is read
   //	 * and referenced ZIP archives are added to the {@link IJavaProject#getResolvedClasspath(boolean) resolved classpath}.
   //	 * </p>
   //	 *
   //	 * @param path the path to the library
   //	 * @param sourceAttachmentPath the absolute path of the corresponding source archive or folder,
   //	 *    or <code>null</code> if none. Note, since 3.0, an empty path is allowed to denote no source attachment.
   //	 *   and will be automatically converted to <code>null</code>. Since 3.4, this path can also denote a path external
   //	 *   to the workspace.
   //	 * @param sourceAttachmentRootPath the location of the root of the source files within the source archive or folder
   //	 *    or <code>null</code> if this location should be automatically detected.
   //	 * @param accessRules the possibly empty list of access rules for this entry
   //	 * @param extraAttributes the possibly empty list of extra attributes to persist with this entry
   //	 * @param isExported indicates whether this entry is contributed to dependent
   //	 * 	  projects in addition to the output location
   //	 * @return a new library classpath entry
   //	 * @since 3.1
   //	 */
   //	public static IClasspathEntry newLibraryEntry(
   //			IPath path,
   //			IPath sourceAttachmentPath,
   //			IPath sourceAttachmentRootPath,
   //			IAccessRule[] accessRules,
   //			IClasspathAttribute[] extraAttributes,
   //			boolean isExported) {
   //
   //		if (path == null) throw new ClasspathEntry.AssertionFailedException("Library path cannot be null"); //$NON-NLS-1$
   //		if (accessRules == null) {
   //			accessRules = ClasspathEntry.NO_ACCESS_RULES;
   //		}
   //		if (extraAttributes == null) {
   //			extraAttributes = ClasspathEntry.NO_EXTRA_ATTRIBUTES;
   //		}
   //		boolean hasDotDot = ClasspathEntry.hasDotDot(path);
   //		if (!hasDotDot && !path.isAbsolute()) throw new ClasspathEntry.AssertionFailedException("Path for IClasspathEntry must be absolute: " + path); //$NON-NLS-1$
   //		if (sourceAttachmentPath != null) {
   //			if (sourceAttachmentPath.isEmpty()) {
   //				sourceAttachmentPath = null; // treat empty path as none
   //			} else if (!sourceAttachmentPath.isAbsolute()) {
   //				throw new ClasspathEntry.AssertionFailedException("Source attachment path '" //$NON-NLS-1$
   //						+ sourceAttachmentPath
   //						+ "' for IClasspathEntry must be absolute"); //$NON-NLS-1$
   //			}
   //		}
   //		return new ClasspathEntry(
   //			IPackageFragmentRoot.K_BINARY,
   //			IClasspathEntry.CPE_LIBRARY,
   //			hasDotDot ? path : JavaProject.canonicalizedPath(path),
   //			ClasspathEntry.INCLUDE_ALL, // inclusion patterns
   //			ClasspathEntry.EXCLUDE_NONE, // exclusion patterns
   //			sourceAttachmentPath,
   //			sourceAttachmentRootPath,
   //			null, // specific output folder
   //			isExported,
   //			accessRules,
   //			false, // no access rules to combine
   //			extraAttributes);
   //	}

   //	/**
   //	 * Creates and returns a new non-exported classpath entry of kind <code>CPE_PROJECT</code>
   //	 * for the project identified by the given absolute path.
   //	 * This method is fully equivalent to calling
   //	 * {@link #newProjectEntry(IPath, IAccessRule[], boolean, IClasspathAttribute[], boolean)
   //	 * newProjectEntry(path, new IAccessRule[0], true, new IClasspathAttribute[0], false)}.
   //	 *
   //	 * @param path the absolute path of the binary archive
   //	 * @return a new project classpath entry
   //	 */
   //	public static IClasspathEntry newProjectEntry(IPath path) {
   //		return newProjectEntry(path, false);
   //	}

   //	/**
   //	 * Creates and returns a new classpath entry of kind <code>CPE_PROJECT</code>
   //	 * for the project identified by the given absolute path.
   //	 * This method is fully equivalent to calling
   //	 * {@link #newProjectEntry(IPath, IAccessRule[], boolean, IClasspathAttribute[], boolean)
   //	 * newProjectEntry(path, new IAccessRule[0], true, new IClasspathAttribute[0], isExported)}.
   //	 *
   //	 * @param path the absolute path of the prerequisite project
   //	 * @param isExported indicates whether this entry is contributed to dependent
   //	 * 	  projects in addition to the output location
   //	 * @return a new project classpath entry
   //	 * @since 2.0
   //	 */
   //	public static IClasspathEntry newProjectEntry(IPath path, boolean isExported) {
   //
   //		if (!path.isAbsolute()) throw new ClasspathEntry.AssertionFailedException("Path for IClasspathEntry must be absolute"); //$NON-NLS-1$
   //
   //		return newProjectEntry(
   //			path,
   //			ClasspathEntry.NO_ACCESS_RULES,
   //			true,
   //			ClasspathEntry.NO_EXTRA_ATTRIBUTES,
   //			isExported);
   //	}

   //	/**
   //	 * Creates and returns a new classpath entry of kind <code>CPE_PROJECT</code>
   //	 * for the project identified by the given absolute path.
   //	 * <p>
   //	 * A project entry is used to denote a prerequisite project on a classpath.
   //	 * The referenced project will be contributed as a whole, either as sources (in the Java Model, it
   //	 * contributes all its package fragment roots) or as binaries (when building, it contributes its
   //	 * whole output location).
   //	 * </p>
   //	 * <p>
   //	 * A project reference allows to indirect through another project, independently from its internal layout.
   //	 * </p><p>
   //	 * The prerequisite project is referred to using an absolute path relative to the workspace root.
   //	 * </p>
   //	 * <p>
   //	 * The access rules determine the set of accessible class files
   //	 * in the project. If the list of access rules is empty then all files
   //	 * in this project are accessible.
   //	 * See {@link IAccessRule} for a detailed description of access rules.
   //	 * </p>
   //	 * <p>
   //	 * The <code>combineAccessRules</code> flag indicates whether access rules of one (or more)
   //	 * exported entry of the project should be combined with the given access rules. If they should
   //	 * be combined, the given access rules are considered first, then the entry's access rules are
   //	 * considered.
   //	 * </p>
   //	 * <p>
   //	 * The <code>extraAttributes</code> list contains name/value pairs that must be persisted with
   //	 * this entry. If no extra attributes are provided, an empty array must be passed in.<br>
   //	 * Note that this list should not contain any duplicate name.
   //	 * </p>
   //	 * <p>
   //	 * The <code>isExported</code> flag indicates whether this entry is contributed to dependent
   //	 * projects. If not exported, dependent projects will not see any of the classes from this entry.
   //	 * If exported, dependent projects will concatenate the accessible files patterns of this entry with the
   //	 * accessible files patterns of the projects, and they will concatenate the non accessible files patterns of this entry
   //	 * with the non accessible files patterns of the project.
   //	 * </p>
   //	 *
   //	 * @param path the absolute path of the prerequisite project
   //	 * @param accessRules the possibly empty list of access rules for this entry
   //	 * @param combineAccessRules whether the access rules of the project's exported entries should be combined with the given access rules
   //	 * @param extraAttributes the possibly empty list of extra attributes to persist with this entry
   //	 * @param isExported indicates whether this entry is contributed to dependent
   //	 * 	  projects in addition to the output location
   //	 * @return a new project classpath entry
   //	 * @since 3.1
   //	 */
   //	public static IClasspathEntry newProjectEntry(
   //			IPath path,
   //			IAccessRule[] accessRules,
   //			boolean combineAccessRules,
   //			IClasspathAttribute[] extraAttributes,
   //			boolean isExported) {
   //
   //		if (!path.isAbsolute()) throw new ClasspathEntry.AssertionFailedException("Path for IClasspathEntry must be absolute"); //$NON-NLS-1$
   //		if (accessRules == null) {
   //			accessRules = ClasspathEntry.NO_ACCESS_RULES;
   //		}
   //		if (extraAttributes == null) {
   //			extraAttributes = ClasspathEntry.NO_EXTRA_ATTRIBUTES;
   //		}
   //		return new ClasspathEntry(
   //			IPackageFragmentRoot.K_SOURCE,
   //			IClasspathEntry.CPE_PROJECT,
   //			path,
   //			ClasspathEntry.INCLUDE_ALL, // inclusion patterns
   //			ClasspathEntry.EXCLUDE_NONE, // exclusion patterns
   //			null, // source attachment
   //			null, // source attachment root
   //			null, // specific output folder
   //			isExported,
   //			accessRules,
   //			combineAccessRules,
   //			extraAttributes);
   //	}

   //	/**
   //	 * Returns a new empty region.
   //	 *
   //	 * @return a new empty region
   //	 */
   //	public static IRegion newRegion() {
   //		return new Region();
   //	}

   //	/**
   //	 * Creates and returns a new classpath entry of kind <code>CPE_SOURCE</code>
   //	 * for all files in the project's source folder identified by the given
   //	 * absolute workspace-relative path.
   //	 * <p>
   //	 * The convenience method is fully equivalent to:
   //	 * <pre>
   //	 * newSourceEntry(path, new IPath[] {}, new IPath[] {}, null);
   //	 * </pre>
   //	 * </p>
   //	 *
   //	 * @param path the absolute workspace-relative path of a source folder
   //	 * @return a new source classpath entry
   //	 * @see #newSourceEntry(IPath, IPath[], IPath[], IPath)
   //	 */
   //	public static IClasspathEntry newSourceEntry(IPath path) {
   //
   //		return newSourceEntry(path, ClasspathEntry.INCLUDE_ALL, ClasspathEntry.EXCLUDE_NONE, null /*output location*/);
   //	}
   //
   //	/**
   //	 * Creates and returns a new classpath entry of kind <code>CPE_SOURCE</code>
   //	 * for the project's source folder identified by the given absolute
   //	 * workspace-relative path but excluding all source files with paths
   //	 * matching any of the given patterns.
   //	 * <p>
   //	 * The convenience method is fully equivalent to:
   //	 * <pre>
   //	 * newSourceEntry(path, new IPath[] {}, exclusionPatterns, null);
   //	 * </pre>
   //	 * </p>
   //	 *
   //	 * @param path the absolute workspace-relative path of a source folder
   //	 * @param exclusionPatterns the possibly empty list of exclusion patterns
   //	 *    represented as relative paths
   //	 * @return a new source classpath entry
   //	 * @see #newSourceEntry(IPath, IPath[], IPath[], IPath)
   //	 * @since 2.1
   //	 */
   //	public static IClasspathEntry newSourceEntry(IPath path, IPath[] exclusionPatterns) {
   //
   //		return newSourceEntry(path, ClasspathEntry.INCLUDE_ALL, exclusionPatterns, null /*output location*/);
   //	}
   //
   //	/**
   //	 * Creates and returns a new classpath entry of kind <code>CPE_SOURCE</code>
   //	 * for the project's source folder identified by the given absolute
   //	 * workspace-relative path but excluding all source files with paths
   //	 * matching any of the given patterns, and associated with a specific output location
   //	 * (that is, ".class" files are not going to the project default output location).
   //	 * <p>
   //	 * The convenience method is fully equivalent to:
   //	 * <pre>
   //	 * newSourceEntry(path, new IPath[] {}, exclusionPatterns, specificOutputLocation);
   //	 * </pre>
   //	 * </p>
   //	 *
   //	 * @param path the absolute workspace-relative path of a source folder
   //	 * @param exclusionPatterns the possibly empty list of exclusion patterns
   //	 *    represented as relative paths
   //	 * @param specificOutputLocation the specific output location for this source entry (<code>null</code> if using project default ouput location)
   //	 * @return a new source classpath entry
   //	 * @see #newSourceEntry(IPath, IPath[], IPath[], IPath)
   //	 * @since 2.1
   //	 */
   //	public static IClasspathEntry newSourceEntry(IPath path, IPath[] exclusionPatterns, IPath specificOutputLocation) {
   //
   //	    return newSourceEntry(path, ClasspathEntry.INCLUDE_ALL, exclusionPatterns, specificOutputLocation);
   //	}
   //
   //	/**
   //	 * Creates and returns a new classpath entry of kind <code>CPE_SOURCE</code>
   //	 * for the project's source folder identified by the given absolute
   //	 * workspace-relative path but excluding all source files with paths
   //	 * matching any of the given patterns, and associated with a specific output location
   //	 * (that is, ".class" files are not going to the project default output location).
   //	 * <p>
   //	 * The convenience method is fully equivalent to:
   //	 * <pre>
   //	 * newSourceEntry(path, new IPath[] {}, exclusionPatterns, specificOutputLocation, new IClasspathAttribute[] {});
   //	 * </pre>
   //	 * </p>
   //	 *
   //	 * @param path the absolute workspace-relative path of a source folder
   //	 * @param inclusionPatterns the possibly empty list of inclusion patterns
   //	 *    represented as relative paths
   //	 * @param exclusionPatterns the possibly empty list of exclusion patterns
   //	 *    represented as relative paths
   //	 * @param specificOutputLocation the specific output location for this source entry (<code>null</code> if using project default ouput location)
   //	 * @return a new source classpath entry
   //	 * @see #newSourceEntry(IPath, IPath[], IPath[], IPath, IClasspathAttribute[])
   //	 * @since 3.0
   //	 */
   //	public static IClasspathEntry newSourceEntry(IPath path, IPath[] inclusionPatterns, IPath[] exclusionPatterns, IPath specificOutputLocation) {
   //		return newSourceEntry(path, inclusionPatterns, exclusionPatterns, specificOutputLocation, ClasspathEntry.NO_EXTRA_ATTRIBUTES);
   //	}
   //
   //	/**
   //	 * Creates and returns a new classpath entry of kind <code>CPE_SOURCE</code>
   //	 * for the project's source folder identified by the given absolute
   //	 * workspace-relative path using the given inclusion and exclusion patterns
   //	 * to determine which source files are included, and the given output path
   //	 * to control the output location of generated files.
   //	 * <p>
   //	 * The source folder is referred to using an absolute path relative to the
   //	 * workspace root, e.g. <code>/Project/src</code>. A project's source
   //	 * folders are located with that project. That is, a source classpath
   //	 * entry specifying the path <code>/P1/src</code> is only usable for
   //	 * project <code>P1</code>.
   //	 * </p>
   //	 * <p>
   //	 * The inclusion patterns determines the initial set of source files that
   //	 * are to be included; the exclusion patterns are then used to reduce this
   //	 * set. When no inclusion patterns are specified, the initial file set
   //	 * includes all relevent files in the resource tree rooted at the source
   //	 * entry's path. On the other hand, specifying one or more inclusion
   //	 * patterns means that all <b>and only</b> files matching at least one of
   //	 * the specified patterns are to be included. If exclusion patterns are
   //	 * specified, the initial set of files is then reduced by eliminating files
   //	 * matched by at least one of the exclusion patterns. Inclusion and
   //	 * exclusion patterns look like relative file paths with wildcards and are
   //	 * interpreted relative to the source entry's path. File patterns are
   //	 * case-sensitive can contain '**', '*' or '?' wildcards (see
   //	 * {@link IClasspathEntry#getExclusionPatterns()} for the full description
   //	 * of their syntax and semantics). The resulting set of files are included
   //	 * in the corresponding package fragment root; all package fragments within
   //	 * the root will have children of type <code>ICompilationUnit</code>.
   //	 * </p>
   //	 * <p>
   //	 * For example, if the source folder path is
   //	 * <code>/Project/src</code>, there are no inclusion filters, and the
   //	 * exclusion pattern is
   //	 * <code>com/xyz/tests/&#42;&#42;</code>, then source files
   //	 * like <code>/Project/src/com/xyz/Foo.java</code>
   //	 * and <code>/Project/src/com/xyz/utils/Bar.java</code> would be included,
   //	 * whereas <code>/Project/src/com/xyz/tests/T1.java</code>
   //	 * and <code>/Project/src/com/xyz/tests/quick/T2.java</code> would be
   //	 * excluded.
   //	 * </p>
   //	 * <p>
   //	 * Additionally, a source entry can be associated with a specific output location.
   //	 * By doing so, the Java builder will ensure that the generated ".class" files will
   //	 * be issued inside this output location, as opposed to be generated into the
   //	 * project default output location (when output location is <code>null</code>).
   //	 * Note that multiple source entries may target the same output location.
   //	 * The output location is referred to using an absolute path relative to the
   //	 * workspace root, e.g. <code>"/Project/bin"</code>, it must be located inside
   //	 * the same project as the source folder.
   //	 * </p>
   //	 * <p>
   //	 * Also note that all sources/binaries inside a project are contributed as
   //	 * a whole through a project entry
   //	 * (see <code>JavaCore.newProjectEntry</code>). Particular source entries
   //	 * cannot be selectively exported.
   //	 * </p>
   //	 * <p>
   //	 * The <code>extraAttributes</code> list contains name/value pairs that must be persisted with
   //	 * this entry. If no extra attributes are provided, an empty array must be passed in.<br>
   //	 * Note that this list should not contain any duplicate name.
   //	 * </p>
   //	 *
   //	 * @param path the absolute workspace-relative path of a source folder
   //	 * @param inclusionPatterns the possibly empty list of inclusion patterns
   //	 *    represented as relative paths
   //	 * @param exclusionPatterns the possibly empty list of exclusion patterns
   //	 *    represented as relative paths
   //	 * @param specificOutputLocation the specific output location for this source entry (<code>null</code> if using project default ouput location)
   //	 * @param extraAttributes the possibly empty list of extra attributes to persist with this entry
   //	 * @return a new source classpath entry with the given exclusion patterns
   //	 * @see IClasspathEntry#getInclusionPatterns()
   //	 * @see IClasspathEntry#getExclusionPatterns()
   //	 * @see IClasspathEntry#getOutputLocation()
   //	 * @since 3.1
   //	 */
   //	public static IClasspathEntry newSourceEntry(IPath path, IPath[] inclusionPatterns, IPath[] exclusionPatterns, IPath specificOutputLocation, IClasspathAttribute[] extraAttributes) {
   //
   //		if (path == null) throw new ClasspathEntry.AssertionFailedException("Source path cannot be null"); //$NON-NLS-1$
   //		if (!path.isAbsolute()) throw new ClasspathEntry.AssertionFailedException("Path for IClasspathEntry must be absolute"); //$NON-NLS-1$
   //		if (exclusionPatterns == null) {
   //			exclusionPatterns = ClasspathEntry.EXCLUDE_NONE;
   //		}
   //		if (inclusionPatterns == null) {
   //			inclusionPatterns = ClasspathEntry.INCLUDE_ALL;
   //		}
   //		if (extraAttributes == null) {
   //			extraAttributes = ClasspathEntry.NO_EXTRA_ATTRIBUTES;
   //		}
   //		return new ClasspathEntry(
   //			IPackageFragmentRoot.K_SOURCE,
   //			IClasspathEntry.CPE_SOURCE,
   //			path,
   //			inclusionPatterns,
   //			exclusionPatterns,
   //			null, // source attachment
   //			null, // source attachment root
   //			specificOutputLocation, // custom output location
   //			false,
   //			null,
   //			false, // no access rules to combine
   //			extraAttributes);
   //	}
   //
   //	/**
   //	 * Creates and returns a new non-exported classpath entry of kind <code>CPE_VARIABLE</code>
   //	 * for the given path. This method is fully equivalent to calling
   //	 * {@link #newVariableEntry(IPath, IPath, IPath, IAccessRule[], IClasspathAttribute[], boolean)
   //	 * newVariableEntry(variablePath, variableSourceAttachmentPath, sourceAttachmentRootPath, new IAccessRule[0], new IClasspathAttribute[0], false)}.
   //	 *
   //	 * @param variablePath the path of the binary archive; first segment is the
   //	 *   name of a classpath variable
   //	 * @param variableSourceAttachmentPath the path of the corresponding source archive,
   //	 *    or <code>null</code> if none; if present, the first segment is the
   //	 *    name of a classpath variable (not necessarily the same variable
   //	 *    as the one that begins <code>variablePath</code>)
   //	 * @param sourceAttachmentRootPath the location of the root of the source files within the source archive
   //	 *    or <code>null</code> if <code>variableSourceAttachmentPath</code> is also <code>null</code>
   //	 * @return a new library classpath entry
   //	 */
   //	public static IClasspathEntry newVariableEntry(
   //		IPath variablePath,
   //		IPath variableSourceAttachmentPath,
   //		IPath sourceAttachmentRootPath) {
   //
   //		return newVariableEntry(variablePath, variableSourceAttachmentPath, sourceAttachmentRootPath, false);
   //	}
   //
   //	/**
   //	 * Creates and returns a new classpath entry of kind <code>CPE_VARIABLE</code>
   //	 * for the given path. This method is fully equivalent to calling
   //	 * {@link #newVariableEntry(IPath, IPath, IPath, IAccessRule[], IClasspathAttribute[], boolean)
   //	 * newVariableEntry(variablePath, variableSourceAttachmentPath, sourceAttachmentRootPath, new IAccessRule[0], new IClasspathAttribute[0], isExported)}.
   //	 *
   //	 * @param variablePath the path of the binary archive; first segment is the
   //	 *   name of a classpath variable
   //	 * @param variableSourceAttachmentPath the path of the corresponding source archive,
   //	 *    or <code>null</code> if none; if present, the first segment is the
   //	 *    name of a classpath variable (not necessarily the same variable
   //	 *    as the one that begins <code>variablePath</code>)
   //	 * @param variableSourceAttachmentRootPath the location of the root of the source files within the source archive
   //	 *    or <code>null</code> if <code>variableSourceAttachmentPath</code> is also <code>null</code>
   //	 * @param isExported indicates whether this entry is contributed to dependent
   //	 * 	  projects in addition to the output location
   //	 * @return a new variable classpath entry
   //	 * @since 2.0
   //	 */
   //	public static IClasspathEntry newVariableEntry(
   //			IPath variablePath,
   //			IPath variableSourceAttachmentPath,
   //			IPath variableSourceAttachmentRootPath,
   //			boolean isExported) {
   //
   //		return newVariableEntry(
   //			variablePath,
   //			variableSourceAttachmentPath,
   //			variableSourceAttachmentRootPath,
   //			ClasspathEntry.NO_ACCESS_RULES,
   //			ClasspathEntry.NO_EXTRA_ATTRIBUTES,
   //			isExported);
   //	}
   //
   //	/**
   //	 * Creates and returns a new classpath entry of kind <code>CPE_VARIABLE</code>
   //	 * for the given path. The first segment of the path is the name of a classpath variable.
   //	 * The trailing segments of the path will be appended to resolved variable path.
   //	 * <p>
   //	 * A variable entry allows to express indirect references on a classpath to other projects or libraries,
   //	 * depending on what the classpath variable is referring.
   //	 * <p>
   //	 *	It is possible to register an automatic initializer (<code>ClasspathVariableInitializer</code>),
   //	 * which will be invoked through the extension point "org.eclipse.jdt.core.classpathVariableInitializer".
   //	 * After resolution, a classpath variable entry may either correspond to a project or a library entry.
   //	 * <p>
   //	 * e.g. Here are some examples of variable path usage<ul>
   //	 * <li> "JDTCORE" where variable <code>JDTCORE</code> is
   //	 *		bound to "c:/jars/jdtcore.jar". The resolved classpath entry is denoting the library "c:\jars\jdtcore.jar"</li>
   //	 * <li> "JDTCORE" where variable <code>JDTCORE</code> is
   //	 *		bound to "/Project_JDTCORE". The resolved classpath entry is denoting the project "/Project_JDTCORE"</li>
   //	 * <li> "PLUGINS/com.example/example.jar" where variable <code>PLUGINS</code>
   //	 *      is bound to "c:/eclipse/plugins". The resolved classpath entry is denoting the library "c:\eclipse\plugins\com.example\example.jar"</li>
   //	 * </ul>
   //	 * <p>
   //	 * The access rules determine the set of accessible class files
   //	 * in the project or library. If the list of access rules is empty then all files
   //	 * in this project or library are accessible.
   //	 * See {@link IAccessRule} for a detailed description of access rules.
   //	 * </p>
   //	 * <p>
   //	 * The <code>extraAttributes</code> list contains name/value pairs that must be persisted with
   //	 * this entry. If no extra attributes are provided, an empty array must be passed in.<br>
   //	 * Note that this list should not contain any duplicate name.
   //	 * </p>
   //	 * <p>
   //	 * The <code>isExported</code> flag indicates whether this entry is contributed to dependent
   //	 * projects. If not exported, dependent projects will not see any of the classes from this entry.
   //	 * If exported, dependent projects will concatenate the accessible files patterns of this entry with the
   //	 * accessible files patterns of the projects, and they will concatenate the non accessible files patterns of this entry
   //	 * with the non accessible files patterns of the project.
   //	 * </p>
   //	 * <p>
   //	 * Note that this operation does not attempt to validate classpath variables
   //	 * or access the resources at the given paths.
   //	 * </p>
   //	 *
   //	 * @param variablePath the path of the binary archive; first segment is the
   //	 *   name of a classpath variable
   //	 * @param variableSourceAttachmentPath the path of the corresponding source archive,
   //	 *    or <code>null</code> if none; if present, the first segment is the
   //	 *    name of a classpath variable (not necessarily the same variable
   //	 *    as the one that begins <code>variablePath</code>)
   //	 * @param variableSourceAttachmentRootPath the location of the root of the source files within the source archive
   //	 *    or <code>null</code> if <code>variableSourceAttachmentPath</code> is also <code>null</code>
   //	 * @param accessRules the possibly empty list of access rules for this entry
   //	 * @param extraAttributes the possibly empty list of extra attributes to persist with this entry
   //	 * @param isExported indicates whether this entry is contributed to dependent
   //	 * 	  projects in addition to the output location
   //	 * @return a new variable classpath entry
   //	 * @since 3.1
   //	 */
   //	public static IClasspathEntry newVariableEntry(
   //			IPath variablePath,
   //			IPath variableSourceAttachmentPath,
   //			IPath variableSourceAttachmentRootPath,
   //			IAccessRule[] accessRules,
   //			IClasspathAttribute[] extraAttributes,
   //			boolean isExported) {
   //
   //		if (variablePath == null) throw new ClasspathEntry.AssertionFailedException("Variable path cannot be null"); //$NON-NLS-1$
   //		if (variablePath.segmentCount() < 1) {
   //			throw new ClasspathEntry.AssertionFailedException("Illegal classpath variable path: \'" + variablePath.makeRelative().toString() + "\', must have at least one segment"); //$NON-NLS-1$//$NON-NLS-2$
   //		}
   //		if (accessRules == null) {
   //			accessRules = ClasspathEntry.NO_ACCESS_RULES;
   //		}
   //		if (extraAttributes == null) {
   //			extraAttributes = ClasspathEntry.NO_EXTRA_ATTRIBUTES;
   //		}
   //
   //		return new ClasspathEntry(
   //			IPackageFragmentRoot.K_SOURCE,
   //			IClasspathEntry.CPE_VARIABLE,
   //			variablePath,
   //			ClasspathEntry.INCLUDE_ALL, // inclusion patterns
   //			ClasspathEntry.EXCLUDE_NONE, // exclusion patterns
   //			variableSourceAttachmentPath, // source attachment
   //			variableSourceAttachmentRootPath, // source attachment root
   //			null, // specific output folder
   //			isExported,
   //			accessRules,
   //			false, // no access rules to combine
   //			extraAttributes);
   //	}
   //	
   //	/**
   //	 * Returns an array of classpath entries that are referenced directly or indirectly 
   //	 * by a given classpath entry. For the entry kind {@link IClasspathEntry#CPE_LIBRARY}, 
   //	 * the method returns the libraries that are included in the Class-Path section of 
   //	 * the MANIFEST.MF file. If a referenced JAR file has further references to other library 
   //	 * entries, they are processed recursively and added to the list. For entry kinds other 
   //	 * than {@link IClasspathEntry#CPE_LIBRARY}, this method returns an empty array.
   //	 * <p> 
   //	 * When a non-null project is passed, any additional attributes that may have been stored 
   //	 * previously in the project's .classpath file are retrieved and populated in the 
   //	 * corresponding referenced entry. If the project is <code>null</code>, the raw referenced
   //	 * entries are returned without any persisted attributes. 
   //	 * For more details on storing referenced entries, see 
   //	 * {@link IJavaProject#setRawClasspath(IClasspathEntry[], IClasspathEntry[], IPath, 
   //	 * IProgressMonitor)}. 
   //	 * </p>
   //	 * 
   //	 * @param libraryEntry the library entry whose referenced entries are sought 
   //	 * @param project project where the persisted referenced entries to be retrieved from. If <code>null</code>
   //	 * 			persisted attributes are not attempted to be retrived.
   //	 * @return an array of classpath entries that are referenced directly or indirectly by the given entry. 
   //	 * 			If not applicable, returns an empty array.
   //	 * @since 3.6
   //	 */
   //	public static IClasspathEntry[] getReferencedClasspathEntries(IClasspathEntry libraryEntry, IJavaProject project) {
   //		JavaModelManager manager = JavaModelManager.getJavaModelManager();
   //		return manager.getReferencedClasspathEntries(libraryEntry, project);
   //	}
   //	
   //	/**
   //	 * Removed the given classpath variable. Does nothing if no value was
   //	 * set for this classpath variable.
   //	 * <p>
   //	 * This functionality cannot be used while the resource tree is locked.
   //	 * <p>
   //	 * Classpath variable values are persisted locally to the workspace, and
   //	 * are preserved from session to session.
   //	 * <p>
   //	 *
   //	 * @param variableName the name of the classpath variable
   //	 * @see #setClasspathVariable(String, IPath)
   //	 *
   //	 * @deprecated Use {@link #removeClasspathVariable(String, IProgressMonitor)} instead
   //	 */
   //	public static void removeClasspathVariable(String variableName) {
   //		removeClasspathVariable(variableName, null);
   //	}
   //
   //	/**
   //	 * Removed the given classpath variable. Does nothing if no value was
   //	 * set for this classpath variable.
   //	 * <p>
   //	 * This functionality cannot be used while the resource tree is locked.
   //	 * <p>
   //	 * Classpath variable values are persisted locally to the workspace, and
   //	 * are preserved from session to session.
   //	 * <p>
   //	 *
   //	 * @param variableName the name of the classpath variable
   //	 * @param monitor the progress monitor to report progress
   //	 * @see #setClasspathVariable(String, IPath)
   //	 */
   //	public static void removeClasspathVariable(String variableName, IProgressMonitor monitor) {
   //		try {
   //			SetVariablesOperation operation = new SetVariablesOperation(new String[]{ variableName}, new IPath[]{ null }, true/*update preferences*/);
   //			operation.runOperation(monitor);
   //		} catch (JavaModelException e) {
   //			Util.log(e, "Exception while removing variable " + variableName); //$NON-NLS-1$
   //		}
   //	}
   //
   //	/**
   //	 * Removes the given element changed listener.
   //	 * Has no effect if an identical listener is not registered.
   //	 *
   //	 * @param listener the listener
   //	 */
   //	public static void removeElementChangedListener(IElementChangedListener listener) {
   //		JavaModelManager.getDeltaState().removeElementChangedListener(listener);
   //	}

   //
   //	/**
   //	 * Removes the given pre-processing resource changed listener.
   //	 * <p>
   //	 * Has no effect if an identical listener is not registered.
   //	 *
   //	 * @param listener the listener
   //	 * @since 3.0
   //	 */
   //	public static void removePreProcessingResourceChangedListener(IResourceChangeListener listener) {
   //		JavaModelManager.getDeltaState().removePreResourceChangedListener(listener);
   //	}
   //
   //
   //
   //	/**
   //	 * Runs the given action as an atomic Java model operation.
   //	 * <p>
   //	 * After running a method that modifies java elements,
   //	 * registered listeners receive after-the-fact notification of
   //	 * what just transpired, in the form of a element changed event.
   //	 * This method allows clients to call a number of
   //	 * methods that modify java elements and only have element
   //	 * changed event notifications reported at the end of the entire
   //	 * batch.
   //	 * </p>
   //	 * <p>
   //	 * If this method is called outside the dynamic scope of another such
   //	 * call, this method runs the action and then reports a single
   //	 * element changed event describing the net effect of all changes
   //	 * done to java elements by the action.
   //	 * </p>
   //	 * <p>
   //	 * If this method is called in the dynamic scope of another such
   //	 * call, this method simply runs the action.
   //	 * </p>
   //	 *
   //	 * @param action the action to perform
   //	 * @param monitor a progress monitor, or <code>null</code> if progress
   //	 *    reporting and cancellation are not desired
   //	 * @exception CoreException if the operation failed.
   //	 * @since 2.1
   //	 */
   //	public static void run(IWorkspaceRunnable action, IProgressMonitor monitor) throws CoreException {
   //		run(action, ResourcesPlugin.getWorkspace().getRoot(), monitor);
   //	}
   //	/**
   //	 * Runs the given action as an atomic Java model operation.
   //	 * <p>
   //	 * After running a method that modifies java elements,
   //	 * registered listeners receive after-the-fact notification of
   //	 * what just transpired, in the form of a element changed event.
   //	 * This method allows clients to call a number of
   //	 * methods that modify java elements and only have element
   //	 * changed event notifications reported at the end of the entire
   //	 * batch.
   //	 * </p>
   //	 * <p>
   //	 * If this method is called outside the dynamic scope of another such
   //	 * call, this method runs the action and then reports a single
   //	 * element changed event describing the net effect of all changes
   //	 * done to java elements by the action.
   //	 * </p>
   //	 * <p>
   //	 * If this method is called in the dynamic scope of another such
   //	 * call, this method simply runs the action.
   //	 * </p>
   //	 * <p>
   // 	 * The supplied scheduling rule is used to determine whether this operation can be
   //	 * run simultaneously with workspace changes in other threads. See
   //	 * <code>IWorkspace.run(...)</code> for more details.
   // 	 * </p>
   //	 *
   //	 * @param action the action to perform
   //	 * @param rule the scheduling rule to use when running this operation, or
   //	 * <code>null</code> if there are no scheduling restrictions for this operation.
   //	 * @param monitor a progress monitor, or <code>null</code> if progress
   //	 *    reporting and cancellation are not desired
   //	 * @exception CoreException if the operation failed.
   //	 * @since 3.0
   //	 */
   //	public static void run(IWorkspaceRunnable action, ISchedulingRule rule, IProgressMonitor monitor) throws CoreException {
   //		IWorkspace workspace = ResourcesPlugin.getWorkspace();
   //		if (workspace.isTreeLocked()) {
   //			new BatchOperation(action).run(monitor);
   //		} else {
   //			// use IWorkspace.run(...) to ensure that a build will be done in autobuild mode
   //			workspace.run(new BatchOperation(action), rule, IWorkspace.AVOID_UPDATE, monitor);
   //		}
   //	}
   //	/**
   //	 * Bind a container reference path to some actual containers (<code>IClasspathContainer</code>).
   //	 * This API must be invoked whenever changes in container need to be reflected onto the JavaModel.
   //	 * Containers can have distinct values in different projects, therefore this API considers a
   //	 * set of projects with their respective containers.
   //	 * <p>
   //	 * <code>containerPath</code> is the path under which these values can be referenced through
   //	 * container classpath entries (<code>IClasspathEntry#CPE_CONTAINER</code>). A container path
   //	 * is formed by a first ID segment followed with extra segments, which can be used as additional hints
   //	 * for the resolution. The container ID is used to identify a <code>ClasspathContainerInitializer</code>
   //	 * registered on the extension point "org.eclipse.jdt.core.classpathContainerInitializer".
   //	 * <p>
   //	 * There is no assumption that each individual container value passed in argument
   //	 * (<code>respectiveContainers</code>) must answer the exact same path when requested
   //	 * <code>IClasspathContainer#getPath</code>.
   //	 * Indeed, the containerPath is just an indication for resolving it to an actual container object. It can be
   //	 * delegated to a <code>ClasspathContainerInitializer</code>, which can be activated through the extension
   //	 * point "org.eclipse.jdt.core.ClasspathContainerInitializer").
   //	 * <p>
   //	 * In reaction to changing container values, the JavaModel will be updated to reflect the new
   //	 * state of the updated container. A combined Java element delta will be notified to describe the corresponding
   //	 * classpath changes resulting from the container update. This operation is batched, and automatically eliminates
   //	 * unnecessary updates (new container is same as old one). This operation acquires a lock on the workspace's root.
   //	 * <p>
   //	 * This functionality cannot be used while the workspace is locked, since
   //	 * it may create/remove some resource markers.
   //	 * <p>
   //	 * Classpath container values are persisted locally to the workspace, but
   //	 * are not preserved from a session to another. It is thus highly recommended to register a
   //	 * <code>ClasspathContainerInitializer</code> for each referenced container
   //	 * (through the extension point "org.eclipse.jdt.core.ClasspathContainerInitializer").
   //	 * <p>
   //	 * Note: setting a container to <code>null</code> will cause it to be lazily resolved again whenever
   //	 * its value is required. In particular, this will cause a registered initializer to be invoked
   //	 * again.
   //	 * <p>
   //	 * @param containerPath - the name of the container reference, which is being updated
   //	 * @param affectedProjects - the set of projects for which this container is being bound
   //	 * @param respectiveContainers - the set of respective containers for the affected projects
   //	 * @param monitor a monitor to report progress
   //	 * @throws JavaModelException
   //	 * @see ClasspathContainerInitializer
   //	 * @see #getClasspathContainer(IPath, IJavaProject)
   //	 * @see IClasspathContainer
   //	 * @since 2.0
   //	 */
   //	public static void setClasspathContainer(IPath containerPath, IJavaProject[] affectedProjects, IClasspathContainer[] respectiveContainers, IProgressMonitor monitor) throws JavaModelException {
   //		if (affectedProjects.length != respectiveContainers.length)
   //			throw new ClasspathEntry.AssertionFailedException("Projects and containers collections should have the same size"); //$NON-NLS-1$
   //		if (affectedProjects.length == 1) {
   //			IClasspathContainer container = respectiveContainers[0];
   //			if (container != null) {
   //				JavaModelManager manager = JavaModelManager.getJavaModelManager();
   //				IJavaProject project = affectedProjects[0];
   //				IClasspathContainer existingCointainer = manager.containerGet(project, containerPath);
   //				if (existingCointainer == JavaModelManager.CONTAINER_INITIALIZATION_IN_PROGRESS) {
   //					manager.containerBeingInitializedPut(project, containerPath, container);
   //					return;
   //				}
   //			}
   //		}
   //		SetContainerOperation operation = new SetContainerOperation(containerPath, affectedProjects, respectiveContainers);
   //		operation.runOperation(monitor);
   //	}
   //
   //	/**
   //	 * Sets the value of the given classpath variable.
   //	 * The path must have at least one segment.
   //	 * <p>
   //	 * This functionality cannot be used while the resource tree is locked.
   //	 * <p>
   //	 * Classpath variable values are persisted locally to the workspace, and
   //	 * are preserved from session to session.
   //	 * <p>
   //	 *
   //	 * @param variableName the name of the classpath variable
   //	 * @param path the path
   //	 * @throws JavaModelException
   //	 * @see #getClasspathVariable(String)
   //	 *
   //	 * @deprecated Use {@link #setClasspathVariable(String, IPath, IProgressMonitor)} instead
   //	 */
   //	public static void setClasspathVariable(String variableName, IPath path)
   //		throws JavaModelException {
   //
   //		setClasspathVariable(variableName, path, null);
   //	}
   //
   //	/**
   //	 * Sets the value of the given classpath variable.
   //	 * The path must not be null.
   //	 * Since 3.5, the path to a library can also be relative to the project using ".." as the first segment. 
   //	 * <p>
   //	 * This functionality cannot be used while the resource tree is locked.
   //	 * <p>
   //	 * Classpath variable values are persisted locally to the workspace, and
   //	 * are preserved from session to session.
   //	 * <p>
   //	 * Updating a variable with the same value has no effect.
   //	 *
   //	 * @param variableName the name of the classpath variable
   //	 * @param path the path
   //	 * @param monitor a monitor to report progress
   //	 * @throws JavaModelException
   //	 * @see #getClasspathVariable(String)
   //	 */
   //	public static void setClasspathVariable(
   //		String variableName,
   //		IPath path,
   //		IProgressMonitor monitor)
   //		throws JavaModelException {
   //
   //		if (path == null) throw new ClasspathEntry.AssertionFailedException("Variable path cannot be null"); //$NON-NLS-1$
   //		setClasspathVariables(new String[]{variableName}, new IPath[]{ path }, monitor);
   //	}
   //
   //	/**
   //	 * Sets the values of all the given classpath variables at once.
   //	 * Null paths can be used to request corresponding variable removal.
   //	 * Since 3.5, the path to a library can also be relative to the project using ".." as the first segment.
   //	 * <p>
   //	 * A combined Java element delta will be notified to describe the corresponding
   //	 * classpath changes resulting from the variables update. This operation is batched,
   //	 * and automatically eliminates unnecessary updates (new variable is same as old one).
   //	 * This operation acquires a lock on the workspace's root.
   //	 * <p>
   //	 * This functionality cannot be used while the workspace is locked, since
   //	 * it may create/remove some resource markers.
   //	 * <p>
   //	 * Classpath variable values are persisted locally to the workspace, and
   //	 * are preserved from session to session.
   //	 * <p>
   //	 * Updating a variable with the same value has no effect.
   //	 *
   //	 * @param variableNames an array of names for the updated classpath variables
   //	 * @param paths an array of path updates for the modified classpath variables (null
   //	 *       meaning that the corresponding value will be removed
   //	 * @param monitor a monitor to report progress
   //	 * @throws JavaModelException
   //	 * @see #getClasspathVariable(String)
   //	 * @since 2.0
   //	 */
   //	public static void setClasspathVariables(
   //		String[] variableNames,
   //		IPath[] paths,
   //		IProgressMonitor monitor)
   //		throws JavaModelException {
   //
   //		if (variableNames.length != paths.length)	throw new ClasspathEntry.AssertionFailedException("Variable names and paths collections should have the same size"); //$NON-NLS-1$
   //		SetVariablesOperation operation = new SetVariablesOperation(variableNames, paths, true/*update preferences*/);
   //		operation.runOperation(monitor);
   //	}

   /**
    * Sets the default compiler options inside the given options map according
    * to the given compliance.
    *
    * <p>The given compliance must be one of those supported by the compiler,
    * that is one of the acceptable values for option {@link #COMPILER_COMPLIANCE}.
    *
    * <p>The list of modified options is currently:</p>
    * <ul>
    * <li>{@link #COMPILER_COMPLIANCE}</li>
    * <li>{@link #COMPILER_SOURCE}</li>
    * <li>{@link #COMPILER_CODEGEN_TARGET_PLATFORM}</li>
    * <li>{@link #COMPILER_PB_ASSERT_IDENTIFIER}</li>
    * <li>{@link #COMPILER_PB_ENUM_IDENTIFIER}</li>
    * <li>{@link #COMPILER_CODEGEN_INLINE_JSR_BYTECODE} for compliance levels 1.5 and greater</li>
    * </ul>
    *
    * <p>If the given compliance is unknown, the given map is unmodified.</p>
    *
    * @param compliance the given compliance
    * @param options the given options map
    * @since 3.3
    */
   public static void setComplianceOptions(String compliance, Map options)
   {
      switch ((int)(CompilerOptions.versionToJdkLevel(compliance) >>> 16))
      {
         case ClassFileConstants.MAJOR_VERSION_1_3 :
            options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_3);
            options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_3);
            options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_1);
            options.put(JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaCore.IGNORE);
            options.put(JavaCore.COMPILER_PB_ENUM_IDENTIFIER, JavaCore.IGNORE);
            break;
         case ClassFileConstants.MAJOR_VERSION_1_4 :
            options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_4);
            options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_3);
            options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_2);
            options.put(JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaCore.WARNING);
            options.put(JavaCore.COMPILER_PB_ENUM_IDENTIFIER, JavaCore.WARNING);
            break;
         case ClassFileConstants.MAJOR_VERSION_1_5 :
            options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_5);
            options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_5);
            options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_5);
            options.put(JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaCore.ERROR);
            options.put(JavaCore.COMPILER_PB_ENUM_IDENTIFIER, JavaCore.ERROR);
            break;
         case ClassFileConstants.MAJOR_VERSION_1_6 :
            options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_6);
            options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_6);
            options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_6);
            options.put(JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaCore.ERROR);
            options.put(JavaCore.COMPILER_PB_ENUM_IDENTIFIER, JavaCore.ERROR);
            break;
         case ClassFileConstants.MAJOR_VERSION_1_7 :
            options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_7);
            options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_7);
            options.put(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JavaCore.VERSION_1_7);
            options.put(JavaCore.COMPILER_PB_ASSERT_IDENTIFIER, JavaCore.ERROR);
            options.put(JavaCore.COMPILER_PB_ENUM_IDENTIFIER, JavaCore.ERROR);
      }
   }

   //	/**
   //	 * Sets the current table of options. All and only the options explicitly
   //	 * included in the given table are remembered; all previous option settings
   //	 * are forgotten, including ones not explicitly mentioned.
   //	 * <p>
   //	 * Helper constants have been defined on JavaCore for each of the option IDs
   //	 * (categorized in Code assist option ID, Compiler option ID and Core option ID)
   //	 * and some of their acceptable values (categorized in Option value). Some
   //	 * options accept open value sets beyond the documented constant values.
   //	 * <p>
   //	 * Note: each release may add new options.
   //	 *
   //	 * @param newOptions
   //	 *            the new options (key type: <code>String</code>; value type:
   //	 *            <code>String</code>), or <code>null</code> to reset all
   //	 *            options to their default values
   //	 * @see JavaCore#getDefaultOptions()
   //	 * @see JavaCorePreferenceInitializer for changing default settings
   //	 */
   //	public static void setOptions(Hashtable newOptions) {
   //		JavaModelManager.getJavaModelManager().setOptions(newOptions);
   //	}
   //
   //	/* (non-Javadoc)
   //	 * Shutdown the JavaCore plug-in.
   //	 * <p>
   //	 * De-registers the JavaModelManager as a resource changed listener and save participant.
   //	 * <p>
   //	 * @see org.eclipse.core.runtime.Plugin#stop(BundleContext)
   //	 */
   //	public void stop(BundleContext context) throws Exception {
   //		try {
   //			JavaModelManager.getJavaModelManager().shutdown();
   //		} finally {
   //			// ensure we call super.stop as the last thing
   //			super.stop(context);
   //		}
   //	}

   //	/* (non-Javadoc)
   //	 * Startup the JavaCore plug-in.
   //	 * <p>
   //	 * Registers the JavaModelManager as a resource changed listener and save participant.
   //	 * Starts the background indexing, and restore saved classpath variable values.
   //	 * <p>
   //	 * @throws Exception
   //	 * @see org.eclipse.core.runtime.Plugin#start(BundleContext)
   //	 */
   //	public void start(BundleContext context) throws Exception {
   //		super.start(context);
   //		JavaModelManager.getJavaModelManager().startup();
   //	}
}