/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Stephan Herrmann - contribution for bug 337868 - [compiler][model] incomplete support for package-info.java when using
 *     SearchableEnvironment
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.compiler;

import com.codenvy.ide.ext.java.jdt.core.compiler.CategorizedProblem;
import com.codenvy.ide.ext.java.jdt.core.compiler.IProblem;
import com.codenvy.ide.ext.java.jdt.internal.compiler.ast.ASTNode;
import com.codenvy.ide.ext.java.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import com.codenvy.ide.ext.java.jdt.internal.compiler.ast.ImportReference;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.AccessRestriction;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryType;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.INameEnvironment;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.ISourceType;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.CompilerOptions;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.CompilerStats;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.ITypeRequestor;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.LookupEnvironment;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.PackageBinding;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.ReferenceBinding;
import com.codenvy.ide.ext.java.jdt.internal.compiler.parser.Parser;
import com.codenvy.ide.ext.java.jdt.internal.compiler.problem.AbortCompilation;
import com.codenvy.ide.ext.java.jdt.internal.compiler.problem.AbortCompilationUnit;
import com.codenvy.ide.ext.java.jdt.internal.compiler.problem.DefaultProblem;
import com.codenvy.ide.ext.java.jdt.internal.compiler.problem.ProblemReporter;
import com.codenvy.ide.ext.java.jdt.internal.compiler.problem.ProblemSeverities;
import com.codenvy.ide.ext.java.jdt.internal.compiler.util.Messages;
import com.codenvy.ide.ext.java.jdt.internal.compiler.util.Util;

public class Compiler implements ITypeRequestor, ProblemSeverities {
    public Parser parser;

    public ICompilerRequestor requestor;

    public CompilerOptions options;

    public ProblemReporter problemReporter;

    public CompilerStats stats;

    public int remainingIterations = 1;

    // management of unit to be processed
    // public CompilationUnitResult currentCompilationUnitResult;
    public CompilationUnitDeclaration[] unitsToProcess;

    public int totalUnits; // (totalUnits-1) gives the last unit in unitToProcess

    // name lookup
    public LookupEnvironment lookupEnvironment;

    // ONCE STABILIZED, THESE SHOULD RETURN TO A FINAL FIELD
    public static boolean DEBUG = false;

    public int parseThreshold = -1;

    // public AbstractAnnotationProcessorManager annotationProcessorManager;

    // public int annotationProcessorStartIndex = 0;

    public ReferenceBinding[] referenceBindings;

    // public boolean useSingleThread = true; // by default the compiler will not use worker threads to read/process/write

    // number of initial units parsed at once (-1: none)

    //   /**
    //    * Answer a new compiler using the given name environment and compiler options. The environment and options will be in effect
    //    * for the lifetime of the compiler. When the compiler is run, compilation results are sent to the given requestor.
    //    *
    //    * @param environment org.eclipse.jdt.internal.compiler.api.env.INameEnvironment Environment used by the compiler in order to
    //    *           resolve type and package names. The name environment implements the actual connection of the compiler to the
    //    *           outside world (e.g. in batch mode the name environment is performing pure file accesses, reuse previous build
    //    *           state or connection to repositories). Note: the name environment is responsible for implementing the actual
    //    *           classpath rules.
    //    *
    //    * @param policy org.eclipse.jdt.internal.compiler.api.problem.IErrorHandlingPolicy Configurable part for problem handling,
    //    *           allowing the compiler client to specify the rules for handling problems (stop on first error or accumulate them
    //    *           all) and at the same time perform some actions such as opening a dialog in UI when compiling interactively.
    //    * @see com.codenvy.ide.java.client.internal.compiler.DefaultErrorHandlingPolicies
    //    *
    //    * @param options org.eclipse.jdt.internal.compiler.impl.CompilerOptions The options that control the compiler behavior.
    //    *
    //    * @param requestor org.eclipse.jdt.internal.compiler.api.ICompilerRequestor Component which will receive and persist all
    //    *           compilation results and is intended to consume them as they are produced. Typically, in a batch compiler, it is
    //    *           responsible for writing out the actual .class files to the file system.
    //    * @see com.codenvy.ide.java.client.internal.compiler.CompilationResult
    //    *
    //    * @param problemFactory org.eclipse.jdt.internal.compiler.api.problem.IProblemFactory Factory used inside the compiler to
    //    *           create problem descriptors. It allows the compiler client to supply its own representation of compilation problems
    //    *           in order to avoid object conversions. Note that the factory is not supposed to accumulate the created problems,
    //    *           the compiler will gather them all and hand them back as part of the compilation unit result.
    //    */
    //   public Compiler(INameEnvironment environment, IErrorHandlingPolicy policy, CompilerOptions options,
    //      final ICompilerRequestor requestor, IProblemFactory problemFactory)
    //   {
    //      this(environment, policy, options, requestor, problemFactory, null /* progress */);
    //   }

    // /**
    // * Answer a new compiler using the given name environment and compiler options.
    // * The environment and options will be in effect for the lifetime of the compiler.
    // * When the compiler is run, compilation results are sent to the given requestor.
    // *
    // * @param environment org.eclipse.jdt.internal.compiler.api.env.INameEnvironment
    // * Environment used by the compiler in order to resolve type and package
    // * names. The name environment implements the actual connection of the compiler
    // * to the outside world (e.g. in batch mode the name environment is performing
    // * pure file accesses, reuse previous build state or connection to repositories).
    // * Note: the name environment is responsible for implementing the actual classpath
    // * rules.
    // *
    // * @param policy org.eclipse.jdt.internal.compiler.api.problem.IErrorHandlingPolicy
    // * Configurable part for problem handling, allowing the compiler client to
    // * specify the rules for handling problems (stop on first error or accumulate
    // * them all) and at the same time perform some actions such as opening a dialog
    // * in UI when compiling interactively.
    // * @see com.codenvy.ide.java.client.internal.compiler.DefaultErrorHandlingPolicies
    // *
    // * @param options org.eclipse.jdt.internal.compiler.impl.CompilerOptions
    // * The options that control the compiler behavior.
    // *
    // * @param requestor org.eclipse.jdt.internal.compiler.api.ICompilerRequestor
    // * Component which will receive and persist all compilation results and is intended
    // * to consume them as they are produced. Typically, in a batch compiler, it is
    // * responsible for writing out the actual .class files to the file system.
    // * @see com.codenvy.ide.java.client.internal.compiler.CompilationResult
    // *
    // * @param problemFactory org.eclipse.jdt.internal.compiler.api.problem.IProblemFactory
    // * Factory used inside the compiler to create problem descriptors. It allows the
    // * compiler client to supply its own representation of compilation problems in
    // * order to avoid object conversions. Note that the factory is not supposed
    // * to accumulate the created problems, the compiler will gather them all and hand
    // * them back as part of the compilation unit result.
    // * @deprecated
    // */
    // public Compiler(
    // INameEnvironment environment,
    // IErrorHandlingPolicy policy,
    // CompilerOptions options,
    // final ICompilerRequestor requestor,
    // IProblemFactory problemFactory) {
    // this(environment, policy, options, requestor, problemFactory, null /* progress */);
    // }

    public Compiler(INameEnvironment environment, IErrorHandlingPolicy policy, CompilerOptions options,
                    final ICompilerRequestor requestor, IProblemFactory problemFactory) {

        this.options = options;

        // wrap requestor in DebugRequestor if one is specified
        this.requestor = requestor;
        this.problemReporter = new ProblemReporter(policy, this.options, problemFactory);
        this.lookupEnvironment = new LookupEnvironment(this, this.options, this.problemReporter, environment);
        this.stats = new CompilerStats();
        initializeParser();
    }

    /** Add an additional binary type */
    @Override
    public void accept(IBinaryType binaryType, PackageBinding packageBinding, AccessRestriction accessRestriction) {
        this.lookupEnvironment.createBinaryTypeFrom(binaryType, packageBinding, accessRestriction);
    }

    /**
     * Add an additional compilation unit into the loop -> build compilation unit declarations, their bindings and record their
     * results.
     */
    @Override
    public void accept(ICompilationUnit sourceUnit, AccessRestriction accessRestriction) {
        // Switch the current policy and compilation result for this unit to the requested one.
        CompilationResult unitResult =
                new CompilationResult(sourceUnit, this.totalUnits, this.totalUnits, this.options.maxProblemsPerUnit);
        unitResult.checkSecondaryTypes = true;
        try {
            // diet parsing for large collection of unit
            CompilationUnitDeclaration parsedUnit;
            if (this.totalUnits < this.parseThreshold) {
                parsedUnit = this.parser.parse(sourceUnit, unitResult);
            } else {
                parsedUnit = this.parser.dietParse(sourceUnit, unitResult);
            }
            parsedUnit.bits |= ASTNode.IsImplicitUnit;
            // initial type binding creation
            this.lookupEnvironment.buildTypeBindings(parsedUnit, accessRestriction);
            addCompilationUnit(sourceUnit, parsedUnit);

            // binding resolution
            this.lookupEnvironment.completeTypeBindings(parsedUnit);
        } catch (AbortCompilationUnit e) {
            // at this point, currentCompilationUnitResult may not be sourceUnit, but some other
            // one requested further along to resolve sourceUnit.
            if (unitResult.compilationUnit == sourceUnit) { // only report once
                this.requestor.acceptResult(unitResult.tagAsAccepted());
            } else {
                throw e; // want to abort enclosing request to compile
            }
        }
    }

    /** Add additional source types */
    @Override
    public void accept(ISourceType[] sourceTypes, PackageBinding packageBinding, AccessRestriction accessRestriction) {
        this.problemReporter.abortDueToInternalError(Messages.instance.abort_againstSourceModel(
                String.valueOf(sourceTypes[0].getName()), String.valueOf(sourceTypes[0].getFileName())));
    }

    protected synchronized void addCompilationUnit(ICompilationUnit sourceUnit, CompilationUnitDeclaration parsedUnit) {

        if (this.unitsToProcess == null) {
            return; // not collecting units
        }

        // append the unit to the list of ones to process later on
        int size = this.unitsToProcess.length;
        if (this.totalUnits == size) {
            // when growing reposition units starting at position 0
            System.arraycopy(this.unitsToProcess, 0, (this.unitsToProcess = new CompilationUnitDeclaration[size * 2]), 0,
                             this.totalUnits);
        }
        this.unitsToProcess[this.totalUnits++] = parsedUnit;
    }

    /**
     * Add the initial set of compilation units into the loop -> build compilation unit declarations, their bindings and record
     * their results.
     */
    protected void beginToCompile(ICompilationUnit[] sourceUnits) {
        int maxUnits = sourceUnits.length;
        this.totalUnits = 0;
        this.unitsToProcess = new CompilationUnitDeclaration[maxUnits];

        internalBeginToCompile(sourceUnits, maxUnits);
    }

    //   public synchronized CompilationUnitDeclaration getUnitToProcess(int next)
    //   {
    //      if (next < this.totalUnits)
    //      {
    //         CompilationUnitDeclaration unit = this.unitsToProcess[next];
    //         this.unitsToProcess[next] = null; // release reference to processed unit declaration
    //         return unit;
    //      }
    //      return null;
    //   }

    public void setBinaryTypes(ReferenceBinding[] binaryTypes) {
        this.referenceBindings = binaryTypes;
    }

    /* Compiler crash recovery in case of unexpected runtime exceptions */
    protected void handleInternalException(Throwable internalException, CompilationUnitDeclaration unit,
                                           CompilationResult result) {

        if (result == null && unit != null) {
            result = unit.compilationResult; // current unit being processed ?
        }
        // Lookup environment may be in middle of connecting types
        if (result == null && this.lookupEnvironment.unitBeingCompleted != null) {
            result = this.lookupEnvironment.unitBeingCompleted.compilationResult;
        }
        if (result == null) {
            synchronized (this) {
                if (this.unitsToProcess != null && this.totalUnits > 0) {
                    result = this.unitsToProcess[this.totalUnits - 1].compilationResult;
                }
            }
        }
        // last unit in beginToCompile ?

        boolean needToPrint = true;
        if (result != null) {
         /* create and record a compilation problem */
            // only keep leading portion of the trace
            String[] pbArguments =
                    new String[]{Messages.instance.compilation_internalError(Util.getExceptionSummary(internalException))};

            result.record(this.problemReporter.createProblem(result.getFileName(), IProblem.Unclassified, pbArguments,
                                                             pbArguments, Error, // severity
                                                             0, // source start
                                                             0, // source end
                                                             0, // line number
                                                             0),// column number
                          unit);

         /* hand back the compilation result */
            if (!result.hasBeenAccepted) {
                this.requestor.acceptResult(result.tagAsAccepted());
                needToPrint = false;
            }
        }
        if (needToPrint) {
         /* dump a stack trace to the console */
            internalException.printStackTrace();
        }
    }

    /* Compiler recovery in case of internal AbortCompilation event */
    protected void handleInternalException(AbortCompilation abortException, CompilationUnitDeclaration unit) {

      /*
       * special treatment for SilentAbort: silently cancelling the compilation process
       */
        if (abortException.isSilent) {
            if (abortException.silentException == null) {
                return;
            }
            throw abortException.silentException;
        }

      /* uncomment following line to see where the abort came from */
        // abortException.printStackTrace();

        // Exception may tell which compilation result it is related, and which problem caused it
        CompilationResult result = abortException.compilationResult;
        if (result == null && unit != null) {
            result = unit.compilationResult; // current unit being processed ?
        }
        // Lookup environment may be in middle of connecting types
        if (result == null && this.lookupEnvironment.unitBeingCompleted != null) {
            result = this.lookupEnvironment.unitBeingCompleted.compilationResult;
        }
        if (result == null) {
            synchronized (this) {
                if (this.unitsToProcess != null && this.totalUnits > 0) {
                    result = this.unitsToProcess[this.totalUnits - 1].compilationResult;
                }
            }
        }
        // last unit in beginToCompile ?
        if (result != null && !result.hasBeenAccepted) {
         /* distant problem which could not be reported back there? */
            if (abortException.problem != null) {
                recordDistantProblem:
                {
                    CategorizedProblem distantProblem = abortException.problem;
                    CategorizedProblem[] knownProblems = result.problems;
                    for (int i = 0; i < result.problemCount; i++) {
                        if (knownProblems[i] == distantProblem) { // already recorded
                            break recordDistantProblem;
                        }
                    }
                    if (distantProblem instanceof DefaultProblem) { // fixup filename TODO (philippe) should improve API to make this
                    // official
                        ((DefaultProblem)distantProblem).setOriginatingFileName(result.getFileName());
                    }
                    result.record(distantProblem, unit);
                }
            } else {
            /* distant internal exception which could not be reported back there */
                if (abortException.exception != null) {
                    this.handleInternalException(abortException.exception, null, result);
                    return;
                }
            }
         /* hand back the compilation result */
            if (!result.hasBeenAccepted) {
                this.requestor.acceptResult(result.tagAsAccepted());
            }
        } else {
            abortException.printStackTrace();
        }
    }

    public void initializeParser() {

        this.parser = new Parser(this.problemReporter, this.options.parseLiteralExpressionsAsConstants);
    }

    /**
     * Add the initial set of compilation units into the loop -> build compilation unit declarations, their bindings and record
     * their results.
     */
    protected void internalBeginToCompile(ICompilationUnit[] sourceUnits, int maxUnits) {
        // Switch the current policy and compilation result for this unit to the requested one.
        for (int i = 0; i < maxUnits; i++) {
            try {
                // diet parsing for large collection of units
                CompilationUnitDeclaration parsedUnit;
                CompilationResult unitResult =
                        new CompilationResult(sourceUnits[i], i, maxUnits, this.options.maxProblemsPerUnit);
                long parseStart = System.currentTimeMillis();
                if (this.totalUnits < this.parseThreshold) {
                    parsedUnit = this.parser.parse(sourceUnits[i], unitResult);
                } else {
                    parsedUnit = this.parser.dietParse(sourceUnits[i], unitResult);
                }
                long resolveStart = System.currentTimeMillis();
                this.stats.parseTime += resolveStart - parseStart;
                // initial type binding creation
                this.lookupEnvironment.buildTypeBindings(parsedUnit, null /*
                                                                       * no access restriction
                                                                       */);
                this.stats.resolveTime += System.currentTimeMillis() - resolveStart;
                addCompilationUnit(sourceUnits[i], parsedUnit);
                ImportReference currentPackage = parsedUnit.currentPackage;
                if (currentPackage != null) {
                    unitResult.recordPackageName(currentPackage.tokens);
                }
                // } catch (AbortCompilationUnit e) {
                // requestor.acceptResult(unitResult.tagAsAccepted());
            } finally {
                sourceUnits[i] = null; // no longer hold onto the unit
            }
        }
        // binding resolution
        this.lookupEnvironment.completeTypeBindings();
    }

    /** Process a compilation unit already parsed and build. */
    public void process(CompilationUnitDeclaration unit, int i) {
        this.lookupEnvironment.unitBeingCompleted = unit;
        long parseStart = System.currentTimeMillis();

        this.parser.getMethodBodies(unit);

        long resolveStart = System.currentTimeMillis();
        this.stats.parseTime += resolveStart - parseStart;

        // fault in fields & methods
        if (unit.scope != null) {
            unit.scope.faultInTypes();
        }

        // verify inherited methods
        if (unit.scope != null) {
            unit.scope.verifyMethods(this.lookupEnvironment.methodVerifier());
        }

        // type checking
        unit.resolve();

        long analyzeStart = System.currentTimeMillis();
        this.stats.resolveTime += analyzeStart - resolveStart;

        // No need of analysis or generation of code if statements are not required
        if (!this.options.ignoreMethodBodies) {
            unit.analyseCode(); // flow analysis
        }

        long generateStart = System.currentTimeMillis();
        this.stats.analyzeTime += generateStart - analyzeStart;

        // if (!this.options.ignoreMethodBodies) unit.generateCode(); // code generation

        // reference info
        if (this.options.produceReferenceInfo && unit.scope != null) {
            unit.scope.storeDependencyInfo();
        }

        // finalize problems (suppressWarnings)
        unit.finalizeProblems();

        this.stats.generateTime += System.currentTimeMillis() - generateStart;

        // refresh the total number of units known at this stage
        unit.compilationResult.totalUnitsKnown = this.totalUnits;

        this.lookupEnvironment.unitBeingCompleted = null;
    }

    // protected void processAnnotations()
    // {
    // int newUnitSize = 0;
    // int newClassFilesSize = 0;
    // int bottom = this.annotationProcessorStartIndex;
    // int top = this.totalUnits;
    // ReferenceBinding[] binaryTypeBindingsTemp = this.referenceBindings;
    // if (top == 0 && binaryTypeBindingsTemp == null)
    // return;
    // this.referenceBindings = null;
    // do
    // {
    // // extract units to process
    // int length = top - bottom;
    // CompilationUnitDeclaration[] currentUnits = new CompilationUnitDeclaration[length];
    // int index = 0;
    // for (int i = bottom; i < top; i++)
    // {
    // CompilationUnitDeclaration currentUnit = this.unitsToProcess[i];
    // if ((currentUnit.bits & ASTNode.IsImplicitUnit) == 0)
    // {
    // currentUnits[index++] = currentUnit;
    // }
    // }
    // if (index != length)
    // {
    // System.arraycopy(currentUnits, 0, (currentUnits = new CompilationUnitDeclaration[index]), 0, index);
    // }
    // this.annotationProcessorManager.processAnnotations(currentUnits, binaryTypeBindingsTemp, false);
    // ICompilationUnit[] newUnits = this.annotationProcessorManager.getNewUnits();
    // newUnitSize = newUnits.length;
    // ReferenceBinding[] newClassFiles = this.annotationProcessorManager.getNewClassFiles();
    // binaryTypeBindingsTemp = newClassFiles;
    // newClassFilesSize = newClassFiles.length;
    // if (newUnitSize != 0)
    // {
    // ICompilationUnit[] newProcessedUnits = com.codenvy.ide.java.client.internal.core.util.Util.clone(newUnits); // remember new units
    // in case a source type collision occurs
    // try
    // {
    // this.lookupEnvironment.isProcessingAnnotations = true;
    // internalBeginToCompile(newUnits, newUnitSize);
    // }
    // catch (SourceTypeCollisionException e)
    // {
    // e.newAnnotationProcessorUnits = newProcessedUnits;
    // throw e;
    // }
    // finally
    // {
    // this.lookupEnvironment.isProcessingAnnotations = false;
    // this.annotationProcessorManager.reset();
    // }
    // bottom = top;
    // top = this.totalUnits; // last unit added
    // }
    // else
    // {
    // bottom = top;
    // this.annotationProcessorManager.reset();
    // }
    // }
    // while (newUnitSize != 0 || newClassFilesSize != 0);
    //
    // this.annotationProcessorManager.processAnnotations(null, null, true);
    // // process potential units added in the final round see 329156
    // ICompilationUnit[] newUnits = this.annotationProcessorManager.getNewUnits();
    // newUnitSize = newUnits.length;
    // if (newUnitSize != 0)
    // {
    // ICompilationUnit[] newProcessedUnits = com.codenvy.ide.java.client.internal.core.util.Util.clone(newUnits); // remember new units
    // in case a source type collision occurs
    // try
    // {
    // this.lookupEnvironment.isProcessingAnnotations = true;
    // internalBeginToCompile(newUnits, newUnitSize);
    // }
    // catch (SourceTypeCollisionException e)
    // {
    // e.newAnnotationProcessorUnits = newProcessedUnits;
    // throw e;
    // }
    // finally
    // {
    // this.lookupEnvironment.isProcessingAnnotations = false;
    // this.annotationProcessorManager.reset();
    // }
    // }
    // else
    // {
    // this.annotationProcessorManager.reset();
    // }
    // }

    public void reset() {
        this.lookupEnvironment.reset();
        this.parser.scanner.source = null;
        this.unitsToProcess = null;
        this.problemReporter.reset();
    }

    /** Internal API used to resolve a given compilation unit. Can run a subset of the compilation process */
    public CompilationUnitDeclaration resolve(CompilationUnitDeclaration unit, ICompilationUnit sourceUnit,
                                              boolean verifyMethods, boolean analyzeCode, boolean generateCode) {

        try {
            if (unit == null) {
                // build and record parsed units
                this.parseThreshold = 0; // will request a full parse
                beginToCompile(new ICompilationUnit[]{sourceUnit});
                // find the right unit from what was injected via accept(ICompilationUnit,..):
                for (int i = 0; i < this.totalUnits; i++) {
                    if (this.unitsToProcess[i] != null
                        && this.unitsToProcess[i].compilationResult.compilationUnit == sourceUnit) {
                        unit = this.unitsToProcess[i];
                        break;
                    }
                }
                if (unit == null) {
                    unit = this.unitsToProcess[0]; // fall back to old behavior
                }

            } else {
                // initial type binding creation
                this.lookupEnvironment.buildTypeBindings(unit, null /*
                                                                 * no access restriction
                                                                 */);

                // binding resolution
                this.lookupEnvironment.completeTypeBindings();
            }
            this.lookupEnvironment.unitBeingCompleted = unit;
            this.parser.getMethodBodies(unit);
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
                if (analyzeCode) {
                    unit.analyseCode();
                }

                // code generation
                // if (generateCode) unit.generateCode();

                // finalize problems (suppressWarnings)
                unit.finalizeProblems();
            }
            if (this.unitsToProcess != null) {
                this.unitsToProcess[0] = null; // release reference to processed unit declaration
            }
            this.requestor.acceptResult(unit.compilationResult.tagAsAccepted());
            return unit;
        } catch (AbortCompilation e) {
            this.handleInternalException(e, unit);
            return unit == null ? this.unitsToProcess[0] : unit;
        } catch (Error e) {
            this.handleInternalException(e, unit, null);
            throw e; // rethrow
        } catch (RuntimeException e) {
            this.handleInternalException(e, unit, null);
            throw e; // rethrow
        } finally {
            // leave this.lookupEnvironment.unitBeingCompleted set to the unit, until another unit is resolved
            // other calls to dom can cause classpath errors to be detected, resulting in AbortCompilation exceptions

            // No reset is performed there anymore since,
            // within the CodeAssist (or related tools),
            // the compiler may be called *after* a call
            // to this resolve(...) method. And such a call
            // needs to have a compiler with a non-empty
            // environment.
            // this.reset();
        }
    }

    /** Internal API used to resolve a given compilation unit. Can run a subset of the compilation process */
    public CompilationUnitDeclaration resolve(ICompilationUnit sourceUnit, boolean verifyMethods, boolean analyzeCode,
                                              boolean generateCode) {

        return resolve(null, sourceUnit, verifyMethods, analyzeCode, generateCode);
    }
}
