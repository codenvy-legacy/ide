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
package org.exoplatform.ide.extension.cloudfoundry.server.ext;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.extension.cloudfoundry.server.Cloudfoundry;
import org.exoplatform.ide.extension.cloudfoundry.server.CloudfoundryAuthenticator;
import org.exoplatform.ide.extension.cloudfoundry.server.SimpleAuthenticator;
import org.exoplatform.ide.security.paas.Credential;
import org.exoplatform.ide.security.paas.CredentialStore;
import org.exoplatform.ide.security.paas.CredentialStoreException;
import org.exoplatform.ide.security.paas.DummyCredentialStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Pool of pre-configured clients to Cloud Foundry servers.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudfoundryPool {
    private volatile Balancer balancer;

    private final CopyOnWriteArrayList<CloudfoundryServerConfiguration> configs;
    private final ReentrantLock lock = new ReentrantLock();

    public CloudfoundryPool(InitParams initParams) {
        this(getConfigurations(initParams));
    }

    private static List<CloudfoundryServerConfiguration> getConfigurations(InitParams params) {
        List<CloudfoundryServerConfiguration> configs = new ArrayList<CloudfoundryServerConfiguration>();
        if (params != null) {
            List<CloudfoundryServerConfiguration> objectParams = params.getObjectParamValues(CloudfoundryServerConfiguration.class);
            if (!(objectParams == null || objectParams.isEmpty())) {
                configs.addAll(objectParams);
            }
        }
        return configs;
    }

    protected CloudfoundryPool(List<CloudfoundryServerConfiguration> configs) {
        this.configs = new CopyOnWriteArrayList<CloudfoundryServerConfiguration>(configs);
        init();
    }

    public Cloudfoundry next() {
        return balancer.next();
    }

    public Cloudfoundry byTargetName(String target) {
        for (Cloudfoundry cf : balancer.available()) {
            String t = null;
            try {
                t = cf.getTarget();
            } catch (CredentialStoreException ignored) {
                // Never happen since we do not read target value from anywhere.
            }

            if (target.equals(t)) {
                return cf;
            }
        }
        return null;
    }

    public void addConfiguration(CloudfoundryServerConfiguration config) {
        if (configs.addIfAbsent(config)) {
            init();
        }
    }

    public void removeConfiguration(CloudfoundryServerConfiguration config) {
        if (configs.remove(config)) {
            init();
        }
    }

    public CloudfoundryServerConfiguration[] getConfigurations() {
        return configs.toArray(new CloudfoundryServerConfiguration[configs.size()]);
    }

    private void init() {
        lock.lock();
        try {
            List<Cloudfoundry> list = new ArrayList<Cloudfoundry>(configs.size());
            for (CloudfoundryServerConfiguration config : configs) {
                SimpleAuthenticator authenticator =
                        new SimpleAuthenticator(config.getTarget(), config.getUser(), config.getPassword());
                list.add(new MyCloudfoundry(authenticator, new MyCredentialStore(new DummyCredentialStore(), authenticator)));
            }
            balancer = new Balancer(list);
        } finally {
            lock.unlock();
        }
    }

    /** Get target server URL from SimpleAuthenticator. */
    private static class MyCloudfoundry extends Cloudfoundry {
        private final CloudfoundryAuthenticator myAuthenticator;

        MyCloudfoundry(SimpleAuthenticator authenticator, MyCredentialStore credentialStore) {
            super(authenticator, credentialStore);
            this.myAuthenticator = authenticator;
        }

        @Override
        public String getTarget() throws CredentialStoreException {
            return myAuthenticator.getTarget();
        }

        @Override
        protected String getUserId() {
            return null;
        }
    }

    /**
     * Store credentials for predefined user instead of current codenvy user.
     * Get username from {@link org.exoplatform.ide.extension.cloudfoundry.server.SimpleAuthenticator#getEmail()}
     */
    private static class MyCredentialStore implements CredentialStore {
        private final DummyCredentialStore delegate;
        private final SimpleAuthenticator  myAuthenticator;

        private MyCredentialStore(DummyCredentialStore delegate, SimpleAuthenticator myAuthenticator) {
            this.delegate = delegate;
            this.myAuthenticator = myAuthenticator;
        }

        @Override
        public boolean load(String user, String target, Credential credential) throws CredentialStoreException {
            return delegate.load(myAuthenticator.getEmail(), "codenvy_runtime", credential);
        }

        @Override
        public void save(String user, String target, Credential credential) throws CredentialStoreException {
            delegate.save(myAuthenticator.getEmail(), "codenvy_runtime", credential);
        }

        @Override
        public boolean delete(String user, String target) throws CredentialStoreException {
            return delegate.delete(myAuthenticator.getEmail(), "codenvy_runtime");
        }
    }

    private static class Balancer {
        private final List<Cloudfoundry> queue;
        private final AtomicInteger position = new AtomicInteger();

        Balancer(List<Cloudfoundry> coll) {
            if (coll == null || coll.isEmpty()) {
                throw new IllegalArgumentException("At least one server must be configured. ");
            }
            this.queue = Collections.unmodifiableList(coll);
        }

        Cloudfoundry next() {
            return queue.get(nextIndex());
        }

        List<Cloudfoundry> available() {
            return queue;
        }

        private int nextIndex() {
            for (; ; ) {
                int current = position.get();
                int next = current + 1;
                if (next >= queue.size()) {
                    next = 0;
                }
                if (position.compareAndSet(current, next)) {
                    return current;
                }
            }
        }
    }
}
