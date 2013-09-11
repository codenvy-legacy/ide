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
package org.exoplatform.ide.codeassistant.jvm.bean;

import org.exoplatform.ide.codeassistant.jvm.shared.AnnotationValue;
import org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo;

import java.util.Collections;
import java.util.List;

public class MethodInfoBean extends MemberBean implements MethodInfo {
    /** Array FQN of exceptions throws by method */
    private List<String> exceptionTypes;

    /** FQN's of parameters */
    private List<String> parameterTypes;

    /** Names of parameters */
    private List<String> parameterNames;

    /**
     * Full Qualified Class Name where method declared Example: method equals()
     * declared in java.lang.String
     */
    private String declaringClass;

    /** Full Qualified Class Name that method return <code>java.lang.String</code> */
    private String returnType;

    /** true if method is a class constructor */
    private boolean isConstructor;

    /** The method's descriptor. */
    public String descriptor;

    /** The method's signature. May be <tt>null</tt>. */
    public String signature;

    /** Method default annotation value. May be <tt>null</tt>. */
    private AnnotationValue annotationValue;

    public MethodInfoBean() {
        this.parameterTypes = Collections.emptyList();
        this.parameterNames = Collections.emptyList();
        this.exceptionTypes = Collections.emptyList();
    }

    public MethodInfoBean(String name,//
                          int modifiers,//
                          List<String> exceptionTypes,//
                          List<String> parameterTypes,//
                          List<String> parameterNames,//
                          boolean isConstructor, //
                          String genericReturnType,//
                          String declaringClass, //
                          String descriptor, //
                          String signature,//
                          AnnotationValue annotationValue) {
        super(name, modifiers);
        this.isConstructor = isConstructor;
        this.returnType = genericReturnType;
        this.declaringClass = declaringClass;
        this.descriptor = descriptor;
        this.signature = signature;
        this.annotationValue = annotationValue;
        setExceptionTypes(exceptionTypes);
        setParameterNames(parameterNames);
        setParameterTypes(parameterTypes);

    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo#getDeclaringClass() */
    @Override
    public String getDeclaringClass() {
        return declaringClass;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo#getExceptionTypes() */
    @Override
    public List<String> getExceptionTypes() {

        return exceptionTypes;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo#getParameterNames() */
    @Override
    public List<String> getParameterNames() {
        return parameterNames;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo#getParameterTypes() */
    @Override
    public List<String> getParameterTypes() {
        return parameterTypes;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo#getReturnType() */
    @Override
    public String getReturnType() {
        return returnType;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo#isConstructor() */
    @Override
    public boolean isConstructor() {
        return isConstructor;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo#setConstructor(boolean) */
    @Override
    public void setConstructor(boolean isConstructor) {
        this.isConstructor = isConstructor;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo#setDeclaringClass(java.lang.String) */
    @Override
    public void setDeclaringClass(String declaringClass) {
        this.declaringClass = declaringClass;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo#setExceptionTypes(java.util.List) */
    @Override
    public void setExceptionTypes(List<String> exceptionTypes) {
        if (exceptionTypes == null) {
            this.exceptionTypes = Collections.emptyList();
        } else {
            this.exceptionTypes = Collections.unmodifiableList(exceptionTypes);
        }
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo#setParameterNames(java.util.List) */
    @Override
    public void setParameterNames(List<String> parameterNames) {
        if (parameterNames == null) {
            this.parameterNames = Collections.emptyList();
        } else {
            this.parameterNames = Collections.unmodifiableList(parameterNames);
        }

    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo#setParameterTypes(java.util.List) */
    @Override
    public void setParameterTypes(List<String> parameterTypes) {

        if (parameterTypes == null) {
            this.parameterTypes = Collections.emptyList();
        } else {
            this.parameterTypes = Collections.unmodifiableList(parameterTypes);
        }
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo#setReturnType(java.lang.String) */
    @Override
    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    @Override
    public String getDescriptor() {
        return descriptor;
    }

    @Override
    public String getSignature() {
        return signature;
    }

    @Override
    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    @Override
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.MemberBean#toString() */
    @Override
    public String toString() {
        StringBuilder buildString = new StringBuilder();

        buildString.append(modifierToString());
        buildString.append(' ');
        if (!isConstructor) {
            if (returnType == null || returnType.length() < 1) {
                buildString.append("void ");
            } else {
                buildString.append(returnType);
                buildString.append(' ');
            }
        }
        if (declaringClass != null && declaringClass.length() > 0 && !isConstructor) {
            buildString.append(declaringClass);
            buildString.append('.');
        }
        buildString.append(getName());
        buildString.append('(');

        if (parameterTypes != null) {
            for (int i = 0; i < parameterTypes.size(); i++) {
                if (i > 0) {
                    buildString.append(',');
                }
                buildString.append(parameterTypes.get(i));

            }
        }
        buildString.append(')');

        if (exceptionTypes != null && exceptionTypes.size() > 0) {
            buildString.append(" throws ");
            for (int i = 0; i < exceptionTypes.size(); i++) {
                if (i > 0) {
                    buildString.append(',');
                }
                buildString.append(exceptionTypes.get(i));

            }
        }
        return buildString.toString();
    }

    /** @see java.lang.Object#hashCode() */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (declaringClass == null ? 0 : declaringClass.hashCode());
        result = prime * result + (exceptionTypes == null ? 0 : exceptionTypes.hashCode());
        result = prime * result + (isConstructor ? 1231 : 1237);
        result = prime * result + (parameterNames == null ? 0 : parameterNames.hashCode());
        result = prime * result + (parameterTypes == null ? 0 : parameterTypes.hashCode());
        result = prime * result + (returnType == null ? 0 : returnType.hashCode());
        result = prime * result + (signature == null ? 0 : signature.hashCode());
        result = prime * result + (descriptor == null ? 0 : descriptor.hashCode());
        return result;
    }

    /** @see java.lang.Object#equals(java.lang.Object) */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MethodInfoBean other = (MethodInfoBean)obj;
        if (declaringClass == null) {
            if (other.declaringClass != null) {
                return false;
            }
        } else if (!declaringClass.equals(other.declaringClass)) {
            return false;
        }
        if (exceptionTypes == null) {
            if (other.exceptionTypes != null) {
                return false;
            }
        } else if (!exceptionTypes.equals(other.exceptionTypes)) {
            return false;
        }
        if (isConstructor != other.isConstructor) {
            return false;
        }
        if (parameterNames == null) {
            if (other.parameterNames != null) {
                return false;
            }
        } else if (!parameterNames.equals(other.parameterNames)) {
            return false;
        }
        if (parameterTypes == null) {
            if (other.parameterTypes != null) {
                return false;
            }
        } else if (!parameterTypes.equals(other.parameterTypes)) {
            return false;
        }
        if (returnType == null) {
            if (other.returnType != null) {
                return false;
            }
        } else if (!returnType.equals(other.returnType)) {
            return false;
        }
        return true;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo#getAnnotationDefault() */
    @Override
    public AnnotationValue getAnnotationDefault() {
        return annotationValue;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo#setAnnotationDefault(org.exoplatform.ide.codeassistant.jvm.shared
     * .AnnotationValue) */
    @Override
    public void setAnnotationDefault(AnnotationValue value) {
        annotationValue = value;
    }

}
