package com.codenvy.ide.factory.server;

import com.codenvy.api.factory.FactoryUrlException;
import com.codenvy.api.factory.FactoryUrlValidator;
import com.codenvy.api.factory.dto.Factory;

/**
 * @author Vladyslav Zhukovskii
 */
public class DummyFactoryValidator implements FactoryUrlValidator {
    @Override
    public void validate(Factory factory, boolean b) throws FactoryUrlException {
    }
}
