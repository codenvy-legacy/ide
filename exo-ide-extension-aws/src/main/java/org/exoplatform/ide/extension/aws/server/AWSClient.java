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
package org.exoplatform.ide.extension.aws.server;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.STSSessionCredentialsProvider;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalk;
import com.amazonaws.services.elasticbeanstalk.AWSElasticBeanstalkClient;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.codenvy.ide.commons.cache.SLRUCache;

import org.exoplatform.ide.security.paas.Credential;
import org.exoplatform.ide.security.paas.CredentialStore;
import org.exoplatform.ide.security.paas.CredentialStoreException;
import org.exoplatform.services.security.ConversationState;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class AWSClient {
    private final SLRUCache<String, AmazonS3>            s3Clients;
    private final SLRUCache<String, AmazonEC2>           ec2Clients;
    private final SLRUCache<String, AWSElasticBeanstalk> elasticBeanstalkClients;
    private final Lock                                   lock;
    private final CredentialStore                        credentialStore;

    public AWSClient(CredentialStore credentialStore) {
        this.credentialStore = credentialStore;
        this.s3Clients = new SLRUCache<String, AmazonS3>(20, 10);
        this.ec2Clients = new SLRUCache<String, AmazonEC2>(20, 10) {
            @Override
            protected void evict(String key, AmazonEC2 value) {
                value.shutdown();
            }
        };
        this.elasticBeanstalkClients = new SLRUCache<String, AWSElasticBeanstalk>(20, 10) {
            @Override
            protected void evict(String key, AWSElasticBeanstalk value) {
                value.shutdown();
            }
        };
        this.lock = new ReentrantLock();
    }

    /**
     * Login AWS API. Specified access and secret keys stored for next usage by current user.
     *
     * @param accessKey
     *         AWS access key
     * @param secretKey
     *         AWS secret key
     * @throws AWSException
     *         if any error occurs when attempt to login to Amazon server
     * @throws org.exoplatform.ide.security.paas.CredentialStoreException
     *         if failed to store credential in CredentialStore
     */
    public final void login(String accessKey, String secretKey) throws AWSException, CredentialStoreException {
        final String userId = getUserId();
        removeAWSClients(userId); // If user already logged remove cached AWS clients.
        newCredentialsProvider(accessKey, secretKey); // Throws exception if credentials is invalid.
        final Credential credential = new Credential();
        credentialStore.load(userId, "aws", credential);
        credential.setAttribute("access_key", accessKey);
        credential.setAttribute("secret_key", secretKey);
        credentialStore.save(userId, "aws", credential);
    }

    /**
     * Remove access and secret keys previously saved for current user. User will be not able to use this class any more
     * before next login.
     *
     * @throws org.exoplatform.ide.security.paas.CredentialStoreException
     *         if failed to store credential in CredentialStore
     */
    public final void logout() throws CredentialStoreException {
        final String userId = getUserId();
        removeAWSClients(userId);
        final Credential credential = new Credential();
        credentialStore.load(userId, "aws", credential);
        credential.removeAttribute("access_key");
        credential.removeAttribute("secret_key");
        credentialStore.save(userId, "aws", credential);
    }

    private void removeAWSClients(String userId) {
        lock.lock();
        try {
            elasticBeanstalkClients.remove(userId);
            ec2Clients.remove(userId);
            s3Clients.remove(userId);
        } finally {
            lock.unlock();
        }
    }

    protected final AWSElasticBeanstalk getBeanstalkClient() throws AWSException, CredentialStoreException {
        final String userId = getUserId();
        lock.lock();
        try {
            AWSElasticBeanstalk client = elasticBeanstalkClients.get(userId);
            if (client == null) {
                final AWSCredentialsProvider credentialsProvider = getCredentialsProvider(userId);
                if (credentialsProvider == null) {
                    throw new AWSException("Authentication required.");
                }
                client = new AWSElasticBeanstalkClient(credentialsProvider);
                elasticBeanstalkClients.put(userId, client);
            }
            return client;
        } finally {
            lock.unlock();
        }
    }

    protected final AmazonS3 getS3Client() throws AWSException, CredentialStoreException {
        final String userId = getUserId();
        lock.lock();
        try {
            AmazonS3 client = s3Clients.get(userId);
            if (client == null) {
                final AWSCredentialsProvider credentialsProvider = getCredentialsProvider(userId);
                if (credentialsProvider == null) {
                    throw new AWSException("Authentication required.");
                }
                client = new AmazonS3Client(credentialsProvider);
                s3Clients.put(userId, client);
            }
            return client;
        } finally {
            lock.unlock();
        }
    }

    protected final AmazonEC2 getEC2Client() throws AWSException, CredentialStoreException {
        final String userId = getUserId();
        lock.lock();
        try {
            AmazonEC2 client = ec2Clients.get(userId);
            if (client == null) {
                final AWSCredentialsProvider credentialsProvider = getCredentialsProvider(userId);
                if (credentialsProvider == null) {
                    throw new AWSException("Authentication required.");
                }
                client = new AmazonEC2Client(credentialsProvider);
                ec2Clients.put(userId, client);
            }
            return client;
        } finally {
            lock.unlock();
        }
    }

    private AWSCredentialsProvider getCredentialsProvider(String userId) throws CredentialStoreException {
        final Credential credential = new Credential();
        credentialStore.load(userId, "aws", credential);
        try {
            return newCredentialsProvider(credential.getAttribute("access_key"), credential.getAttribute("secret_key"));
        } catch (Exception e) {
            // User needs re-login.
            return null;
        }
    }

    private AWSCredentialsProvider newCredentialsProvider(String accessKey, String secret) throws AWSException {
        try {
            AWSCredentialsProvider sessionCredentialsProvider =
                    new STSSessionCredentialsProvider(new BasicAWSCredentials(accessKey, secret));
            sessionCredentialsProvider.getCredentials(); // initialize session
            return sessionCredentialsProvider;
        } catch (AmazonServiceException e) {
            throw new AWSException("Unable login. " + e.getMessage(), e);
        }
    }

    private String getUserId() {
        return ConversationState.getCurrent().getIdentity().getUserId();
    }
}
