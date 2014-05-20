/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.factory.server;

import com.codenvy.api.factory.FactoryUrlException;
import com.codenvy.api.factory.FactoryUrlValidator;
import com.codenvy.api.factory.dto.Factory;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

/**
 * @author Vladyslav Zhukovskii
 */
public class DummyFactoryValidator implements FactoryUrlValidator {
    @Override
    public Factory validate(URI factoryUrl, HttpServletRequest request) throws FactoryUrlException {
        return null;
    }

    @Override
    public void validate(Factory factory, boolean encoded, HttpServletRequest request) throws FactoryUrlException {
    }
}
