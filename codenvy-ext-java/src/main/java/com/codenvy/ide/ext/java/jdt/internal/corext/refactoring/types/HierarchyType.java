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

import com.codenvy.ide.ext.java.jdt.core.dom.ITypeBinding;

import java.util.Map;

public abstract class HierarchyType extends TType {
    private HierarchyType fSuperclass;

    private HierarchyType[] fInterfaces;

    private ITypeBinding binding;


    protected HierarchyType(TypeEnvironment environment) {
        super(environment);
    }

    protected void initialize(ITypeBinding binding) {
        this.binding = binding;
        super.initialize(binding);
        TypeEnvironment environment = getEnvironment();
        ITypeBinding superclass = binding.getSuperclass();
        if (superclass != null) {
            fSuperclass = (HierarchyType)environment.create(superclass);
        }
        ITypeBinding[] interfaces = binding.getInterfaces();
        fInterfaces = new HierarchyType[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            fInterfaces[i] = (HierarchyType)environment.create(interfaces[i]);
        }
    }

    @Override
    public TType getSuperclass() {
        return fSuperclass;
    }

    @Override
    public TType[] getInterfaces() {
        return fInterfaces;
    }

    public ITypeBinding getJavaTypeBinding() {
        return binding;
    }

    public boolean isSubType(HierarchyType other) {
        if (getEnvironment() == other.getEnvironment()) {
            Map<TypeTuple, Boolean> cache = getEnvironment().getSubTypeCache();
            TypeTuple key = new TypeTuple(this, other);
            Boolean value = cache.get(key);
            if (value != null)
                return value.booleanValue();
            boolean isSub = doIsSubType(other);
            value = Boolean.valueOf(isSub);
            cache.put(key, value);
            return isSub;
        }
        return doIsSubType(other);
    }

    private boolean doIsSubType(HierarchyType other) {
        if (fSuperclass != null && (other.isTypeEquivalentTo(fSuperclass) || fSuperclass.doIsSubType(other)))
            return true;
        for (int i = 0; i < fInterfaces.length; i++) {
            if (other.isTypeEquivalentTo(fInterfaces[i]) || fInterfaces[i].doIsSubType(other))
                return true;
        }
        return false;
    }

    protected boolean canAssignToStandardType(StandardType target) {
        if (target.isJavaLangObject())
            return true;
        return isSubType(target);
    }
}
