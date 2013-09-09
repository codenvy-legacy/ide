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

import org.exoplatform.ide.codeassistant.jvm.shared.FieldInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.Member;
import org.exoplatform.ide.codeassistant.jvm.shared.MethodInfo;
import org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo;

import java.util.Collections;
import java.util.List;

/**
 *
 */
public class TypeInfoBean extends ShortTypeInfoBean implements TypeInfo {
    private List<MethodInfo> methods;

    private List<FieldInfo> fields;

    private String superClass;

    private List<String> interfaces;

    private String signature;

    private List<Member> nestedTypes;

    public TypeInfoBean() {
        this.methods = Collections.emptyList();
        this.fields = Collections.emptyList();
        this.nestedTypes = Collections.emptyList();
    }

    public TypeInfoBean(String name, int modifiers, List<MethodInfo> methods, List<FieldInfo> fields, String superClass,
                        List<String> interfaces, String type, String signature, List<Member> nestedTypes) {
        super(name, modifiers, type, signature);
        this.superClass = superClass;
        this.nestedTypes = nestedTypes;
        setMethods(methods);
        setFields(fields);
        setInterfaces(interfaces);
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo#getFields() */
    @Override
    public List<FieldInfo> getFields() {
        return fields;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo#getInterfaces() */
    @Override
    public List<String> getInterfaces() {
        return interfaces;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo#getMethods() */
    @Override
    public List<MethodInfo> getMethods() {
        return methods;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo#getSuperClass() */
    @Override
    public String getSuperClass() {
        return superClass;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo#setFields(java.util.List) */
    @Override
    public void setFields(List<FieldInfo> fields) {
        if (fields == null) {
            this.fields = Collections.emptyList();
        } else {
            this.fields = Collections.unmodifiableList(fields);
        }
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo#setInterfaces(java.util.List) */
    @Override
    public void setInterfaces(List<String> interfaces) {
        if (interfaces == null) {
            this.interfaces = Collections.emptyList();
        } else {
            this.interfaces = Collections.unmodifiableList(interfaces);
        }
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo#setMethods(java.util.List) */
    @Override
    public void setMethods(List<MethodInfo> methods) {
        this.methods = Collections.unmodifiableList(methods);
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo#setSuperClass(java.lang.String) */
    @Override
    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    /** @see java.lang.Object#hashCode() */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (fields == null ? 0 : fields.hashCode());
        result = prime * result + (interfaces == null ? 0 : interfaces.hashCode());
        result = prime * result + (methods == null ? 0 : methods.hashCode());
        result = prime * result + (superClass == null ? 0 : superClass.hashCode());
        result = prime * result + (signature == null ? 0 : signature.hashCode());
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
        TypeInfoBean other = (TypeInfoBean)obj;
        if (fields == null) {
            if (other.fields != null) {
                return false;
            }
        } else if (!fields.equals(other.fields)) {
            return false;
        }
        if (interfaces == null) {
            if (other.interfaces != null) {
                return false;
            }
        } else if (!interfaces.equals(other.interfaces)) {
            return false;
        }
        if (methods == null) {
            if (other.methods != null) {
                return false;
            }
        } else if (!methods.equals(other.methods)) {
            return false;
        }
        if (superClass == null) {
            if (other.superClass != null) {
                return false;
            }
        } else if (!superClass.equals(other.superClass)) {
            return false;
        } else if (!signature.equals(other.signature)) {
            return false;
        }
        return true;
    }

    @Override
    public String getSignature() {
        return signature;
    }

    @Override
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo#getNestedTypes() */
    @Override
    public List<Member> getNestedTypes() {
        return nestedTypes;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.TypeInfo#setNestedTypes(java.util.List) */
    @Override
    public void setNestedTypes(List<Member> types) {
        nestedTypes = types;
    }

}
