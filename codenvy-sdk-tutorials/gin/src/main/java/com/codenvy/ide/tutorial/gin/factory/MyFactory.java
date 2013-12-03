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
package com.codenvy.ide.tutorial.gin.factory;

import com.codenvy.ide.tutorial.gin.factory.assited.SomeInterface;

/**
 * The factory for creating instances of {@link MyFactoryClass} and {@link SomeInterface}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface MyFactory {
    /**
     * Create an instance of {@link MyFactoryClass} with a given string value.
     *
     * @param someText
     *         string value that needs to be injected in the creating instance
     * @return an instance of {@link MyFactoryClass}
     */
    MyFactoryClass createMyFactoryClass(String someText);

    /**
     * Create an instance of {@link SomeInterface} with a given string value.
     *
     * @param text
     *         string value that needs to be injected in the creating instance
     * @return an instance of {@link SomeInterface}
     */
    SomeInterface createSomeInterface(String text);
}