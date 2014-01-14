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
package com.codenvy.ide.everrest;

import org.everrest.core.impl.method.MethodInvokerDecorator;
import org.everrest.core.impl.method.MethodInvokerDecoratorFactory;
import org.everrest.core.method.MethodInvoker;

/**
 * Create new instance of WebSocketMethodInvokerDecorator.
 *
 * @author andrew00x
 * @see WebSocketMethodInvokerDecorator
 */
public class WebSocketMethodInvokerDecoratorFactory implements MethodInvokerDecoratorFactory {
    @Override
    public MethodInvokerDecorator makeDecorator(MethodInvoker invoker) {
        return new WebSocketMethodInvokerDecorator(invoker);
    }
}
