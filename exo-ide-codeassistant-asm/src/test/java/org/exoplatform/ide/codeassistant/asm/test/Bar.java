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

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public @interface Bar {

    int i() default 42;

    String strring() default "";

    E enu() default E.ONE;

    Class<?> clazz() default String.class;

    String[] str() default "str";

    double[] vertex() default {1.4, 2.04, 5.0007};

    Class<?>[] clazzs() default {Integer.class, List.class, Math.class};

    Foo getF() default @Foo(foo = 5, bar = {"aaa", "bbb"});

    Foo[] getArr() default {@Foo(foo = 5, bar = {"aaa", "bbb"}), @Foo(foo = 10, bar = {"ccc, ddd"})};

}
