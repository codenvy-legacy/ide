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
package org.exoplatform.ide.codeassistant.asm.test;

import java.util.List;
import java.util.Set;

/**
 */
public abstract class A {
    private String string;

    protected Integer integer;

    public long l;

    public A() throws ClassNotFoundException {

    }

    public A(Set<Class<?>> classes) throws ClassNotFoundException, ClassFormatError {
    }

    /** @return the string */
    public String getString() {
        return string;
    }

    /**
     * @param string
     *         the string to set
     */
    public void setString(String string) {
        this.string = string;
    }

    /** @return the integer */
    public Integer getInteger() {
        return integer;
    }

    /**
     * @param integer
     *         the integer to set
     */
    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    /** @return the l */
    public long getL() {
        return l;
    }

    /**
     * @param l
     *         the l to set
     */
    public void setL(long l) {
        this.l = l;
    }

    public A(String string, Integer integer, List<String> tt) {
        super();
        this.string = string;
        this.integer = integer;
        this.l = 10;
    }

}
