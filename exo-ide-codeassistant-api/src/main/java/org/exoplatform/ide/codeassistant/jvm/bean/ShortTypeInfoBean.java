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

import org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo;

public class ShortTypeInfoBean extends MemberBean implements ShortTypeInfo {

    /** Means this is CLASS, INTERFACE or ANNOTATION */
    private String type;

    /** The signature of the class. Mayt be <tt>null</tt>. */
    public String signature;

    public ShortTypeInfoBean() {
    }

    public ShortTypeInfoBean(String name, int modifiers, String type, String siganture) {
        super(name, modifiers);
        this.type = type;
        this.signature = siganture;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo#getType() */
    @Override
    public String getType() {
        return type;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.shared.ShortTypeInfo#setType(java.lang.String) */
    @Override
    public void setType(String type) {
        this.type = type;
    }

    /** {@inheritDoc} */
    @Override
    public String getSignature() {
        return signature;
    }

    /** {@inheritDoc} */
    @Override
    public void setSignature(String signature) {
        this.signature = signature;
    }

    /** @see org.exoplatform.ide.codeassistant.jvm.MemberBean#toString() */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(modifierToString());
        builder.append(' ');
        builder.append(type);
        builder.append(' ');
        builder.append(getName());

        return builder.toString();
    }

    /** @see java.lang.Object#hashCode() */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (type == null ? 0 : type.hashCode());
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
        ShortTypeInfoBean other = (ShortTypeInfoBean)obj;
        if (type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!type.equals(other.type)) {
            return false;
        }
        return true;
    }

}
