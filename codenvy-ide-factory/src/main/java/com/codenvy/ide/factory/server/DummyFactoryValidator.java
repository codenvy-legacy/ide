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
    public Factory validate(URI uri, HttpServletRequest httpServletRequest) throws FactoryUrlException {
        return null;
    }

    @Override
    public void validate(Factory factory, boolean b, HttpServletRequest httpServletRequest) throws FactoryUrlException {
    }
}
