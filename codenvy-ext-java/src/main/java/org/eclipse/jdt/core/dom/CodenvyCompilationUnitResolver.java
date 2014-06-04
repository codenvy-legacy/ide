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
package org.eclipse.jdt.core.dom;

import com.codenvy.ide.ext.java.jdt.core.dom.AST;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.compiler.CategorizedProblem;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.ast.AbstractMethodDeclaration;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.core.CancelableNameEnvironment;
import org.eclipse.jdt.internal.core.CancelableProblemFactory;
import org.eclipse.jdt.internal.core.INameEnvironmentWithProgress;
import org.eclipse.jdt.internal.core.NameLookup;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Evgen Vidolob
 */
public class CodenvyCompilationUnitResolver extends CompilationUnitResolver {


    /**
     * Answer a new CompilationUnitVisitor using the given name environment and compiler options.
     * The environment and options will be in effect for the lifetime of the compiler.
     * When the compiler is run, compilation results are sent to the given requestor.
     *
     * @param environment
     *         org.eclipse.jdt.internal.compiler.api.env.INameEnvironment
     *         Environment used by the compiler in order to resolve type and package
     *         names. The name environment implements the actual connection of the compiler
     *         to the outside world (for example, in batch mode the name environment is performing
     *         pure file accesses, reuse previous build state or connection to repositories).
     *         Note: the name environment is responsible for implementing the actual classpath
     *         rules.
     * @param policy
     *         org.eclipse.jdt.internal.compiler.api.problem.IErrorHandlingPolicy
     *         Configurable part for problem handling, allowing the compiler client to
     *         specify the rules for handling problems (stop on first error or accumulate
     *         them all) and at the same time perform some actions such as opening a dialog
     *         in UI when compiling interactively.
     * @param compilerOptions
     *         The compiler options to use for the resolution.
     * @param requestor
     *         org.eclipse.jdt.internal.compiler.api.ICompilerRequestor
     *         Component which will receive and persist all compilation results and is intended
     *         to consume them as they are produced. Typically, in a batch compiler, it is
     *         responsible for writing out the actual .class files to the file system.
     * @param problemFactory
     *         org.eclipse.jdt.internal.compiler.api.problem.IProblemFactory
     *         Factory used inside the compiler to create problem descriptors. It allows the
     *         compiler client to supply its own representation of compilation problems in
     *         order to avoid object conversions. Note that the factory is not supposed
     *         to accumulate the created problems, the compiler will gather them all and hand
     *         them back as part of the compilation unit result.
     * @param monitor
     * @param fromJavaProject
     *         @see org.eclipse.jdt.internal.compiler.DefaultErrorHandlingPolicies
     * @see org.eclipse.jdt.internal.compiler.CompilationResult
     */
    public CodenvyCompilationUnitResolver(INameEnvironment environment,
                                          IErrorHandlingPolicy policy,
                                          CompilerOptions compilerOptions,
                                          ICompilerRequestor requestor,
                                          IProblemFactory problemFactory,
                                          IProgressMonitor monitor, boolean fromJavaProject) {
        super(environment, policy, compilerOptions, requestor, problemFactory, monitor, fromJavaProject);
    }

    public static CompilationUnitDeclaration resolve(
            org.eclipse.jdt.internal.compiler.env.ICompilationUnit sourceUnit,
            IJavaProject javaProject,
            INameEnvironmentWithProgress environment,
            Map options,
            int flags,
            IProgressMonitor monitor) throws JavaModelException {

        CompilationUnitDeclaration unit = null;
//        INameEnvironmentWithProgress environment = null;
        CancelableProblemFactory problemFactory = null;
        CodenvyCompilationUnitResolver resolver = null;
        try {
//            if (javaProject == null) {
//                FileSystem.Classpath[] allEntries = new FileSystem.Classpath[classpaths.size()];
//                classpaths.toArray(allEntries);
//                environment = new NameEnvironmentWithProgress(allEntries, null, monitor);
//            } else {
//                environment = new CancelableNameEnvironment((JavaProject) javaProject, owner, monitor);
//            }
            problemFactory = new CancelableProblemFactory(monitor);
            CompilerOptions compilerOptions = getCompilerOptions(options, (flags & ICompilationUnit.ENABLE_STATEMENTS_RECOVERY) != 0);
            boolean ignoreMethodBodies = (flags & ICompilationUnit.IGNORE_METHOD_BODIES) != 0;
            compilerOptions.ignoreMethodBodies = ignoreMethodBodies;
            resolver =
                    new CodenvyCompilationUnitResolver(
                            environment,
                            getHandlingPolicy(),
                            compilerOptions,
                            getRequestor(),
                            problemFactory,
                            monitor,
                            javaProject != null);
            boolean analyzeAndGenerateCode = !ignoreMethodBodies;
            unit =
                    resolver.resolve(
                            null, // no existing compilation unit declaration
                            sourceUnit,
                            null,
                            true, // method verification
                            analyzeAndGenerateCode, // analyze code
                            analyzeAndGenerateCode); // generate code
            if (resolver.hasCompilationAborted) {
                // the bindings could not be resolved due to missing types in name environment
                // see https://bugs.eclipse.org/bugs/show_bug.cgi?id=86541
                CompilationUnitDeclaration unitDeclaration = parse(sourceUnit, null, options, flags);
                if (unit != null) {
                    final int problemCount = unit.compilationResult.problemCount;
                    if (problemCount != 0) {
                        unitDeclaration.compilationResult.problems = new CategorizedProblem[problemCount];
                        System.arraycopy(unit.compilationResult.problems, 0, unitDeclaration.compilationResult.problems, 0, problemCount);
                        unitDeclaration.compilationResult.problemCount = problemCount;
                    }
                } else if (resolver.abortProblem != null) {
                    unitDeclaration.compilationResult.problemCount = 1;
                    unitDeclaration.compilationResult.problems = new CategorizedProblem[] { resolver.abortProblem };
                }
                return unitDeclaration;
            }
            if (NameLookup.VERBOSE && environment instanceof CancelableNameEnvironment) {
                CancelableNameEnvironment cancelableNameEnvironment = (CancelableNameEnvironment) environment;
                System.out.println(Thread.currentThread() + " TIME SPENT in NameLoopkup#seekTypesInSourcePackage: " + cancelableNameEnvironment.nameLookup.timeSpentInSeekTypesInSourcePackage + "ms");  //$NON-NLS-1$ //$NON-NLS-2$
                System.out.println(Thread.currentThread() + " TIME SPENT in NameLoopkup#seekTypesInBinaryPackage: " + cancelableNameEnvironment.nameLookup.timeSpentInSeekTypesInBinaryPackage + "ms");  //$NON-NLS-1$ //$NON-NLS-2$
            }
            return unit;
        } finally {
            if (environment != null) {
                // don't hold a reference to this external object
                environment.setMonitor(null);
            }
            if (problemFactory != null) {
                problemFactory.monitor = null; // don't hold a reference to this external object
            }
        }
    }

    private CompilationUnitDeclaration resolve(
            CompilationUnitDeclaration unit,
            org.eclipse.jdt.internal.compiler.env.ICompilationUnit sourceUnit,
            NodeSearcher nodeSearcher,
            boolean verifyMethods,
            boolean analyzeCode,
            boolean generateCode) {

        try {

            if (unit == null) {
                // build and record parsed units
                this.parseThreshold = 0; // will request a full parse
                beginToCompile(new org.eclipse.jdt.internal.compiler.env.ICompilationUnit[] { sourceUnit });
                // find the right unit from what was injected via accept(ICompilationUnit,..):
                for (int i=0, max = this.totalUnits; i < max; i++) {
                    CompilationUnitDeclaration currentCompilationUnitDeclaration = this.unitsToProcess[i];
                    if (currentCompilationUnitDeclaration != null
                        && currentCompilationUnitDeclaration.compilationResult.compilationUnit == sourceUnit) {
                        unit = currentCompilationUnitDeclaration;
                        break;
                    }
                }
                if (unit == null) {
                    unit = this.unitsToProcess[0]; // fall back to old behavior
                }
            } else {
                // initial type binding creation
                this.lookupEnvironment.buildTypeBindings(unit, null /*no access restriction*/);

                // binding resolution
                this.lookupEnvironment.completeTypeBindings();
            }

            if (nodeSearcher == null) {
                this.parser.getMethodBodies(unit); // no-op if method bodies have already been parsed
            } else {
                int searchPosition = nodeSearcher.position;
                char[] source = sourceUnit.getContents();
                int length = source.length;
                if (searchPosition >= 0 && searchPosition <= length) {
                    unit.traverse(nodeSearcher, unit.scope);

                    org.eclipse.jdt.internal.compiler.ast.ASTNode node = nodeSearcher.found;

                    if (node != null) {
                        // save existing values to restore them at the end of the parsing process
                        // see bug 47079 for more details
                        int[] oldLineEnds = this.parser.scanner.lineEnds;
                        int oldLinePtr = this.parser.scanner.linePtr;

                        this.parser.scanner.setSource(source, unit.compilationResult);

                        org.eclipse.jdt.internal.compiler.ast.TypeDeclaration enclosingTypeDeclaration = nodeSearcher.enclosingType;
                        if (node instanceof AbstractMethodDeclaration) {
                            ((AbstractMethodDeclaration)node).parseStatements(this.parser, unit);
                        } else if (enclosingTypeDeclaration != null) {
                            if (node instanceof org.eclipse.jdt.internal.compiler.ast.Initializer) {
                                ((org.eclipse.jdt.internal.compiler.ast.Initializer) node).parseStatements(this.parser, enclosingTypeDeclaration, unit);
                            } else if (node instanceof org.eclipse.jdt.internal.compiler.ast.TypeDeclaration) {
                                ((org.eclipse.jdt.internal.compiler.ast.TypeDeclaration)node).parseMethods(this.parser, unit);
                            }
                        }
                        // this is done to prevent any side effects on the compilation unit result
                        // line separator positions array.
                        this.parser.scanner.lineEnds = oldLineEnds;
                        this.parser.scanner.linePtr = oldLinePtr;
                    }
                }
            }

            if (unit.scope != null) {
                // fault in fields & methods
                unit.scope.faultInTypes();
                if (unit.scope != null && verifyMethods) {
                    // http://dev.eclipse.org/bugs/show_bug.cgi?id=23117
                    // verify inherited methods
                    unit.scope.verifyMethods(this.lookupEnvironment.methodVerifier());
                }
                // type checking
                unit.resolve();

                // flow analysis
                if (analyzeCode) unit.analyseCode();

                // code generation
                if (generateCode) unit.generateCode();

                // finalize problems (suppressWarnings)
                unit.finalizeProblems();
            }
            if (this.unitsToProcess != null) this.unitsToProcess[0] = null; // release reference to processed unit declaration
            this.requestor.acceptResult(unit.compilationResult.tagAsAccepted());
            return unit;
        } catch (org.eclipse.jdt.internal.compiler.problem.AbortCompilation e) {
            this.handleInternalException(e, unit);
            return unit == null ? this.unitsToProcess[0] : unit;
        } catch (Error e) {
            this.handleInternalException(e, unit, null);
            throw e; // rethrow
        } catch (RuntimeException e) {
            this.handleInternalException(e, unit, null);
            throw e; // rethrow
        } finally {
            // No reset is performed there anymore since,
            // within the CodeAssist (or related tools),
            // the compiler may be called *after* a call
            // to this resolve(...) method. And such a call
            // needs to have a compiler with a non-empty
            // environment.
            // this.reset();
        }
    }

    public static CompilationUnit convert(CompilationUnitDeclaration compilationUnitDeclaration, char[] contents, int flags,
                                          Map<String, String> options) {
        DefaultBindingResolver.BindingTables bindingTables = new DefaultBindingResolver.BindingTables();

        CompilationUnit compilationUnit = convert(compilationUnitDeclaration, contents, AST.JLS4, options, true, null, bindingTables, flags,
                                          new NullProgressMonitor(), true);
        compilationUnit.setProperty("compilerBindingsToASTBindings", new HashMap(bindingTables.compilerBindingsToASTBindings));
        return compilationUnit;
    }

    public static CompilationUnit convert(
            CompilationUnitDeclaration compilationUnitDeclaration,
            char[] source,
            int apiLevel,
            Map options,
            boolean needToResolveBindings,
            WorkingCopyOwner owner,
            DefaultBindingResolver.BindingTables bindingTables,
            int flags,
            IProgressMonitor monitor,
            boolean fromJavaProject) {
        BindingResolver resolver = null;
        org.eclipse.jdt.core.dom.AST ast = org.eclipse.jdt.core.dom.AST.newAST(apiLevel);
        ast.setDefaultNodeFlag(ASTNode.ORIGINAL);
        CompilationUnit compilationUnit = null;
        ASTConverter converter = new ASTConverter(options, needToResolveBindings, monitor);
        if (needToResolveBindings) {
            resolver = new DefaultBindingResolver(compilationUnitDeclaration.scope, owner, bindingTables, (flags & ICompilationUnit.ENABLE_BINDINGS_RECOVERY) != 0, fromJavaProject);
            ast.setFlag(flags | org.eclipse.jdt.core.dom.AST.RESOLVED_BINDINGS);
        } else {
            resolver = new BindingResolver();
            ast.setFlag(flags);
        }
        ast.setBindingResolver(resolver);
        converter.setAST(ast);
        compilationUnit = converter.convert(compilationUnitDeclaration, source);
        compilationUnit.setLineEndTable(compilationUnitDeclaration.compilationResult.getLineSeparatorPositions());
        ast.setDefaultNodeFlag(0);
        ast.setOriginalModificationCount(ast.modificationCount());
        return compilationUnit;
    }

}
