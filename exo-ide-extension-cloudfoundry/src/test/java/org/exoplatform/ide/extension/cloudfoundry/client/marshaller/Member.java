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
package org.exoplatform.ide.extension.cloudfoundry.client.marshaller;

import java.lang.reflect.Modifier;

/**
 * Member is reflects identifying information about a single member (a field or
 * a method) or a constructor.
 *
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 * @see TypeInfo
 * @see FieldInfo
 * @see MethodInfo
 *      <p/>
 *      Created by The eXo Platform SAS.
 */
public abstract class Member implements IMember {

    protected Integer modifiers;

    protected String name;

    public Member() {
    }

    public Member(Integer modifiers, String name) {
        this.modifiers = modifiers;
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public Integer getModifiers() {
        return modifiers;
    }

    /** {@inheritDoc} */
    @Override
    public String getName() {
        return name;
    }

    /** {@inheritDoc} */
    @Override
    public void setModifiers(Integer modifiers) {
        this.modifiers = modifiers;
    }

    /** {@inheritDoc} */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /** {@inheritDoc} */
    @Override
    public String modifierToString() {
        StringBuffer sb = new StringBuffer();
        int len;

        if ((modifiers & Modifier.PUBLIC) != 0) {
            sb.append("public ");
        }
        if ((modifiers & Modifier.PROTECTED) != 0) {
            sb.append("protected ");
        }
        if ((modifiers & Modifier.PRIVATE) != 0) {
            sb.append("private ");
        }

      /* Canonical order */
        if ((modifiers & Modifier.ABSTRACT) != 0) {
            sb.append("abstract ");
        }
        if ((modifiers & Modifier.STATIC) != 0) {
            sb.append("static ");
        }
        if ((modifiers & Modifier.FINAL) != 0) {
            sb.append("final ");
        }
        if ((modifiers & Modifier.TRANSIENT) != 0) {
            sb.append("transient ");
        }
        if ((modifiers & Modifier.VOLATILE) != 0) {
            sb.append("volatile ");
        }
        if ((modifiers & Modifier.SYNCHRONIZED) != 0) {
            sb.append("synchronized ");
        }
        if ((modifiers & Modifier.NATIVE) != 0) {
            sb.append("native ");
        }
        if ((modifiers & Modifier.STRICT) != 0) {
            sb.append("strictfp ");
        }
        if ((modifiers & Modifier.INTERFACE) != 0) {
            sb.append("interface ");
        }

        if ((len = sb.length()) > 0) {
            return sb.toString().substring(0, len - 1);
        }
        return "";
    }

}
