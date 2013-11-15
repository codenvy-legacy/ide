/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.dto;

/**
 * Visitor pattern. Generally needed to register {@link DtoProvider}s (by generated code) in {@link DtoFactory}.
 * Class, which contains generated code for client side, implements this interface. When all implementations of this
 * interface is instantiated - its {@link #accept(DtoFactory)} method will be called.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 */
public interface DtoFactoryVisitor {
    void accept(DtoFactory dtoFactory);
}
