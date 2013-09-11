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
package test.javadoc;

import java.util.List;

/**
 * <p>
 * Class java doc<br>
 * with tags and<br>
 * few lines.
 * </p>
 *
 * @author Test class doclets
 */
public class JavaDocClass {

    public String fieldWithoutJavaDoc;

    /**
     * Field java doc
     *
     * @author Test field doclets
     */
    public String field;

    /** Constructor java doc with parameters */
    public JavaDocClass(int p1, Integer p2) {
    }

    public JavaDocClass() {
    }

    /** Method java doc */
    public void method() {
    }

    /** Method with primitive param */
    public void method(int p1) {
    }

    /** Method with object param */
    public void method(Double p1) {
    }

    /** Method with primitive and object params */
    public void method(int p1, Double p2) {
    }

    public void methodWithoutJavaDocs(Object p1) {
    }

    /** Private class with java doc */
    private class PrivateClass {

        /** Constructor of private class */
        public PrivateClass() {
        }

        /** Method of private class */
        public void method() {
        }

    }

    private class ClassWithoutJavadoc {

        /** Method with java docs in uncommented class */
        public void method() {
        }

    }

}

/** Second private class */
class PrivateClass {

}

/** Class with generics */
class ClassWithGenerics<T extends Number> {

    /** Field with generics */
    T genericField;

    /** Method with generics */
    public T method(T p1) {
        return null;
    }

    /** Method with list as parameter {@link asdf} */
    public T method(List<? extends Number> p1) {
        return null;
    }

    /**
     * Begin
     *
     * @see PrivateClass asdf Middle
     * @see ClassWithGenerics#method(List)
     * @author Author End
     */
    public void method() {
    }

}
