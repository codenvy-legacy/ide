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
package com.codenvy.ide.ext.java.jdt.internal.corext.refactoring.types;

import com.codenvy.ide.ext.java.jdt.core.BindingKey;
import com.codenvy.ide.ext.java.jdt.core.dom.ASTParser;
import com.codenvy.ide.ext.java.jdt.core.dom.DefaultBindingResolver;
import com.codenvy.ide.ext.java.jdt.core.dom.ITypeBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.DefaultBindingResolver.BindingTables;
import com.codenvy.ide.ext.java.jdt.internal.compiler.CompilationResult;
import com.codenvy.ide.ext.java.jdt.internal.compiler.IErrorHandlingPolicy;
import com.codenvy.ide.ext.java.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.AccessRestriction;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.IBinaryType;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.ICompilationUnit;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.ISourceType;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.CompilerOptions;
import com.codenvy.ide.ext.java.jdt.internal.compiler.impl.ITypeRequestor;
import com.codenvy.ide.ext.java.jdt.internal.compiler.lookup.*;
import com.codenvy.ide.ext.java.jdt.internal.compiler.problem.DefaultProblemFactory;
import com.codenvy.ide.ext.java.jdt.internal.compiler.problem.ProblemReporter;

import com.codenvy.ide.ext.java.worker.WorkerMessageHandler;
import com.codenvy.ide.runtime.Assert;

import java.util.*;

/**
 * A type environment comprises a set of {@link TType}s that stand for Java {@link ITypeBinding}s.
 * In contrast to type bindings, TTypes of the same type environment also work across project boundaries and
 * across compiler environments, i.e. a type environment can handle bindings from multiple {@link ASTParser} sessions.
 *
 * @see TType
 */
public class TypeEnvironment implements TypeConstants {

    /** Type code for the primitive type "int". */
    public final PrimitiveType INT = new PrimitiveType(this, PrimitiveType.INT, BindingKey.createTypeBindingKey("int")); //$NON-NLS-1$

    /** Type code for the primitive type "char". */
    public final PrimitiveType CHAR = new PrimitiveType(this, PrimitiveType.CHAR,
                                                        BindingKey.createTypeBindingKey("char")); //$NON-NLS-1$

    /** Type code for the primitive type "boolean". */
    public final PrimitiveType BOOLEAN = new PrimitiveType(this, PrimitiveType.BOOLEAN,
                                                           BindingKey.createTypeBindingKey("boolean")); //$NON-NLS-1$

    /** Type code for the primitive type "short". */
    public final PrimitiveType SHORT = new PrimitiveType(this, PrimitiveType.SHORT,
                                                         BindingKey.createTypeBindingKey("short")); //$NON-NLS-1$

    /** Type code for the primitive type "long". */
    public final PrimitiveType LONG = new PrimitiveType(this, PrimitiveType.LONG,
                                                        BindingKey.createTypeBindingKey("long")); //$NON-NLS-1$

    /** Type code for the primitive type "float". */
    public final PrimitiveType FLOAT = new PrimitiveType(this, PrimitiveType.FLOAT,
                                                         BindingKey.createTypeBindingKey("float")); //$NON-NLS-1$

    /** Type code for the primitive type "double". */
    public final PrimitiveType DOUBLE = new PrimitiveType(this, PrimitiveType.DOUBLE,
                                                          BindingKey.createTypeBindingKey("double")); //$NON-NLS-1$

    /** Type code for the primitive type "byte". */
    public final PrimitiveType BYTE = new PrimitiveType(this, PrimitiveType.BYTE,
                                                        BindingKey.createTypeBindingKey("byte")); //$NON-NLS-1$

    /** Type code for the primitive type "null". */
    public final NullType NULL = new NullType(this);

    public final VoidType VOID = new VoidType(this);

    final PrimitiveType[] PRIMITIVE_TYPES = {INT, CHAR, BOOLEAN, SHORT, LONG, FLOAT, DOUBLE, BYTE};

    private static final String[] BOXED_PRIMITIVE_NAMES = new String[]{"java.lang.Integer", //$NON-NLS-1$
                                                                       "java.lang.Character", //$NON-NLS-1$
                                                                       "java.lang.Boolean", //$NON-NLS-1$
                                                                       "java.lang.Short", //$NON-NLS-1$
                                                                       "java.lang.Long", //$NON-NLS-1$
                                                                       "java.lang.Float", //$NON-NLS-1$
                                                                       "java.lang.Double", //$NON-NLS-1$
                                                                       "java.lang.Byte"}; //$NON-NLS-1$

    private TType OBJECT_TYPE = null;

    private List<Map<TType, ArrayType>> fArrayTypes = new ArrayList<Map<TType, ArrayType>>();

    private Map<String, StandardType> fStandardTypes = new HashMap<String, StandardType>();

    private Map<String, GenericType> fGenericTypes = new HashMap<String, GenericType>();

    private Map<String, ParameterizedType> fParameterizedTypes = new HashMap<String, ParameterizedType>();

    private Map<String, RawType> fRawTypes = new HashMap<String, RawType>();

    private Map<String, TypeVariable> fTypeVariables = new HashMap<String, TypeVariable>();

    private Map<TType, ExtendsWildcardType> fExtendsWildcardTypes = new HashMap<TType, ExtendsWildcardType>();

    private Map<TType, SuperWildcardType> fSuperWildcardTypes = new HashMap<TType, SuperWildcardType>();

    private UnboundWildcardType fUnboundWildcardType = null;

    private static final int MAX_ENTRIES = 1024;

    private Map<TypeTuple, Boolean> fSubTypeCache = new LinkedHashMap<TypeTuple, Boolean>(50, 0.75f, true) {
        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(Map.Entry<TypeTuple, Boolean> eldest) {
            return size() > MAX_ENTRIES;
        }
    };

    /**
     * Map from TType to its known subtypes, or <code>null</code> iff subtype
     * information was not requested in the constructor.
     */
    private Map<TType, ArrayList<TType>> fSubTypes;

    /**
     * If <code>true</code>, replace all capture types by their wildcard type.
     *
     * @since 3.7
     */
    private final boolean fRemoveCapures;

    private LookupEnvironment env;

    public TypeEnvironment() {
        this(false);
    }

    public TypeEnvironment(boolean rememberSubtypes) {
        this(rememberSubtypes, false);
    }

    public TypeEnvironment(boolean rememberSubtypes, boolean removeCapures) {
        if (rememberSubtypes) {
            fSubTypes = new HashMap<TType, ArrayList<TType>>();
        }
        fRemoveCapures = removeCapures;
    }

    Map<TypeTuple, Boolean> getSubTypeCache() {
        return fSubTypeCache;
    }

    public TType create(ITypeBinding binding) {
        if (binding.isPrimitive()) {
            return createPrimitiveType(binding);
        } else if (binding.isArray()) {
            return createArrayType(binding);
        } else if (binding.isRawType()) {
            return createRawType(binding);
        } else if (binding.isGenericType()) {
            return createGenericType(binding);
        } else if (binding.isParameterizedType()) {
            return createParameterizedType(binding);
        } else if (binding.isTypeVariable()) {
            return createTypeVariable(binding);
        } else if (binding.isWildcardType()) {
            if (binding.getBound() == null) {
                return createUnboundWildcardType(binding);
            } else if (binding.isUpperbound()) {
                return createExtendsWildCardType(binding);
            } else {
                return createSuperWildCardType(binding);
            }
        } else if (binding.isCapture()) {
            if (fRemoveCapures) {
                return create(binding.getWildcard());
            } else {
                return createCaptureType(binding);
            }
        }
        if ("null".equals(binding.getName())) //$NON-NLS-1$
            return NULL;
        return createStandardType(binding);
    }

    public TType[] create(ITypeBinding[] bindings) {
        TType[] result = new TType[bindings.length];
        for (int i = 0; i < bindings.length; i++) {
            result[i] = create(bindings[i]);
        }
        return result;
    }

    /**
     * Returns the TType for java.lang.Object.
     * <p/>
     * Warning: currently returns <code>null</code> unless this type environment
     * has already created its first hierarchy type.
     *
     * @return the TType for java.lang.Object
     */
    public TType getJavaLangObject() {
        return OBJECT_TYPE;
    }

    void initializeJavaLangObject(ITypeBinding object) {
        if (OBJECT_TYPE != null)
            return;

        TType objectType = createStandardType(object);
        Assert.isTrue(objectType.isJavaLangObject());
    }

    PrimitiveType createUnBoxed(StandardType type) {
        String name = type.getPlainPrettySignature();
        for (int i = 0; i < BOXED_PRIMITIVE_NAMES.length; i++) {
            if (BOXED_PRIMITIVE_NAMES[i].equals(name))
                return PRIMITIVE_TYPES[i];
        }
        return null;
    }

    StandardType createBoxed(PrimitiveType type) {
        StandardType result = fStandardTypes.get(type.getPlainPrettySignature());
        if (result != null)
            return result;

        ProblemReporter problemReporter = new ProblemReporter(new IErrorHandlingPolicy() {

            @Override
            public boolean stopOnFirstError() {
                return false;
            }

            @Override
            public boolean proceedOnErrors() {
                return true;
            }
        }, new CompilerOptions(), new DefaultProblemFactory());
        ITypeRequestor typeRequestor = new ITypeRequestor() {

            @Override
            public void accept(ISourceType[] sourceType, PackageBinding packageBinding, AccessRestriction accessRestriction) {
            }

            @Override
            public void accept(ICompilationUnit unit, AccessRestriction accessRestriction) {
            }

            @Override
            public void accept(IBinaryType binaryType, PackageBinding packageBinding, AccessRestriction accessRestriction) {
                env.cacheBinaryType(binaryType, accessRestriction);
            }
        };
        env =
                new LookupEnvironment(typeRequestor, new CompilerOptions(), problemReporter,
                                      WorkerMessageHandler.get().getNameEnvironment());
        TypeBinding knownType = Scope.getBaseType(type.getName().toCharArray());
        TypeBinding boxingType = env.computeBoxingType(knownType);
        boxingType.tagBits |= TagBits.HasMissingType;
        DefaultBindingResolver res = new DefaultBindingResolver(env, new BindingTables(), true, true);
        res.scope().referenceContext =
                new CompilationUnitDeclaration(problemReporter, new CompilationResult(new char[]{'e'}, 0, 1, 100), 1);

        return createStandardType(res.getTypeBinding(boxingType));
    }

    Map<TType, ArrayList<TType>> getSubTypes() {
        return fSubTypes;
    }

    private void cacheSubType(TType supertype, TType result) {
        if (fSubTypes == null)
            return;
        if (supertype == null)
            supertype = OBJECT_TYPE;

        ArrayList<TType> subtypes = fSubTypes.get(supertype);
        if (subtypes == null) {
            subtypes = new ArrayList<TType>(5);
            fSubTypes.put(supertype, subtypes);
        } else {
            Assert.isTrue(!subtypes.contains(result));
        }
        subtypes.add(result);
    }

    private void cacheSubTypes(TType[] interfaces, TType result) {
        for (int i = 0; i < interfaces.length; i++) {
            cacheSubType(interfaces[i], result);
        }
    }

    private TType createPrimitiveType(ITypeBinding binding) {
        String name = binding.getName();
        String[] names = PrimitiveType.NAMES;
        for (int i = 0; i < names.length; i++) {
            if (name.equals(names[i])) {
                return PRIMITIVE_TYPES[i];
            }
        }
        Assert.isTrue(false, "Primitive type " + name + "unkown"); //$NON-NLS-1$//$NON-NLS-2$
        return null;
    }

    private ArrayType createArrayType(ITypeBinding binding) {
        int index = binding.getDimensions() - 1;
        TType elementType = create(binding.getElementType());
        Map<TType, ArrayType> arrayTypes = getArrayTypesMap(index);
        ArrayType result = arrayTypes.get(elementType);
        if (result != null)
            return result;
        result = new ArrayType(this);
        arrayTypes.put(elementType, result);
        result.initialize(binding, elementType);
        return result;
    }

    public ArrayType createArrayType(TType elementType, int dimensions) {
        Assert.isTrue(!elementType.isArrayType());
        Assert.isTrue(!elementType.isAnonymous());
        Assert.isTrue(dimensions > 0);

        int index = dimensions - 1;
        Map<TType, ArrayType> arrayTypes = getArrayTypesMap(index);
        ArrayType result = arrayTypes.get(elementType);
        if (result != null)
            return result;
        result = new ArrayType(this, BindingKey.createArrayTypeBindingKey(elementType.getBindingKey(), dimensions));
        arrayTypes.put(elementType, result);
        result.initialize(elementType, dimensions);
        return result;
    }

    private Map<TType, ArrayType> getArrayTypesMap(int index) {
        int oldLength = fArrayTypes.size();
        if (index >= oldLength) {
            fArrayTypes.addAll(Collections.<Map<TType, ArrayType>>nCopies(index + 1 - oldLength, null));
        }
        Map<TType, ArrayType> arrayTypes = fArrayTypes.get(index);
        if (arrayTypes == null) {
            arrayTypes = new HashMap<TType, ArrayType>();
            fArrayTypes.set(index, arrayTypes);
        }
        return arrayTypes;
    }

    private StandardType createStandardType(ITypeBinding binding) {
        //      IJavaElement javaElement = binding.getJavaElement();
        StandardType result = fStandardTypes.get(binding.getBinaryName());
        if (result != null)
            return result;
        result = new StandardType(this);
        fStandardTypes.put(binding.getBinaryName(), result);
        result.initialize(binding);
        if (OBJECT_TYPE == null && result.isJavaLangObject())
            OBJECT_TYPE = result;
        return result;
    }

    private GenericType createGenericType(ITypeBinding binding) {
        //      IJavaElement javaElement = binding.getJavaElement();
        GenericType result = fGenericTypes.get(binding.getQualifiedName());
        if (result != null)
            return result;
        result = new GenericType(this);
        fGenericTypes.put(binding.getQualifiedName(), result);
        result.initialize(binding);
        cacheSubType(result.getSuperclass(), result);
        cacheSubTypes(result.getInterfaces(), result);
        return result;
    }

    private ParameterizedType createParameterizedType(ITypeBinding binding) {
        //      IJavaProject javaProject = binding.getJavaElement().getJavaProject();
        String bindingKey = binding.getKey();
        //      ProjectKeyPair pair = new ProjectKeyPair(javaProject, bindingKey);
        //      ParameterizedType result = fParameterizedTypes.get(pair);
        //      if (result != null)
        //         return result;
        ParameterizedType result = new ParameterizedType(this);
        //      fParameterizedTypes.put(pair, result);
        result.initialize(binding);
        cacheSubType(result.getSuperclass(), result);
        cacheSubTypes(result.getInterfaces(), result);
        return result;
    }

    private RawType createRawType(ITypeBinding binding) {
        //      IJavaElement javaElement = binding.getJavaElement();
        RawType result = fRawTypes.get(binding.getQualifiedName());
        if (result != null)
            return result;
        result = new RawType(this);
        fRawTypes.put(binding.getQualifiedName(), result);
        result.initialize(binding);
        cacheSubType(result.getSuperclass(), result);
        cacheSubTypes(result.getInterfaces(), result);
        return result;
    }

    private TType createUnboundWildcardType(ITypeBinding binding) {
        if (fUnboundWildcardType == null) {
            fUnboundWildcardType = new UnboundWildcardType(this);
            fUnboundWildcardType.initialize(binding);
        }
        return fUnboundWildcardType;
    }

    private TType createExtendsWildCardType(ITypeBinding binding) {
        TType bound = create(binding.getBound());
        ExtendsWildcardType result = fExtendsWildcardTypes.get(bound);
        if (result != null)
            return result;
        result = new ExtendsWildcardType(this);
        fExtendsWildcardTypes.put(bound, result);
        result.initialize(binding);
        return result;
    }

    private TType createSuperWildCardType(ITypeBinding binding) {
        TType bound = create(binding.getBound());
        SuperWildcardType result = fSuperWildcardTypes.get(bound);
        if (result != null)
            return result;
        result = new SuperWildcardType(this);
        fSuperWildcardTypes.put(bound, result);
        result.initialize(binding);
        return result;
    }

    private TypeVariable createTypeVariable(ITypeBinding binding) {
        //      IJavaElement javaElement = binding.getJavaElement();
        TypeVariable result = fTypeVariables.get(binding.getQualifiedName());
        if (result != null)
            return result;
        result = new TypeVariable(this);
        fTypeVariables.put(binding.getQualifiedName(), result);
        result.initialize(binding);
        return result;
    }

    private CaptureType createCaptureType(ITypeBinding binding) {
        //      IJavaProject javaProject = binding.getDeclaringClass().getJavaElement().getJavaProject();
        //      String bindingKey = binding.getKey();
        //      ProjectKeyPair pair = new ProjectKeyPair(javaProject, bindingKey);
        //      CaptureType result = fCaptureTypes.get(pair);
        //      if (result != null)
        //         return result;
        CaptureType result = new CaptureType(this);
        //      fCaptureTypes.put(pair, result);
        result.initialize(binding);
        return result;
    }
}
