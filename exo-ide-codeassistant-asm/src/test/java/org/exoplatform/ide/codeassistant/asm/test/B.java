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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 */
public class B extends A implements I {

    public List<Boolean> booleans;

    private final Collection<Double> doubles;

    public B() throws ClassFormatError, ClassNotFoundException {
        doubles = new ArrayList<Double>();
    }

    public B(List<Boolean> booleans, Collection<Double> doubles) throws ClassFormatError, ClassNotFoundException {
        this.booleans = booleans;
        this.doubles = doubles;
    }

    /**
     * @param s
     * @param ss
     * @param clazz
     * @return
     * @throws ClassFormatError
     * @throws ClassNotFoundException
     */
    public A createA(String s, List<String> ss, Class<?> clazz) throws ClassFormatError, ClassNotFoundException {
        return new A() {
        };
    }

    public Collection<Double> getDoubles() {
        return doubles;
    }

    public String getName() {
        return null;
    }

    public String[] getName(Long[] longs) {
        return null;
    }

    public <T extends Number, InputStream> T genericMethod(T number) {
        return null;
    }

    public <T extends Number, InputStream, K extends Number, V extends List<? extends Object>> T methodWithMap(
            Map<Map<Map<K, ? extends Map<? super List<Number>, V>>, Number>, V> map) {
        return null;
    }

}
