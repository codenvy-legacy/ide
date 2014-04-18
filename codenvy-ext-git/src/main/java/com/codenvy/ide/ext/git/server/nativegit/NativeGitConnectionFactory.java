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
package com.codenvy.ide.ext.git.server.nativegit;

import com.codenvy.api.core.NotFoundException;
import com.codenvy.api.core.ServerException;
import com.codenvy.api.user.server.dao.UserProfileDao;
import com.codenvy.api.user.shared.dto.Attribute;
import com.codenvy.api.user.shared.dto.Profile;
import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.commons.lang.Strings;
import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.ext.git.server.GitConnection;
import com.codenvy.ide.ext.git.server.GitConnectionFactory;
import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.ext.git.shared.GitUser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.List;

/**
 * Native implementation for GitConnectionFactory
 *
 * @author Eugene Voevodin
 */
@Singleton
public class NativeGitConnectionFactory extends GitConnectionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(NativeGitConnectionFactory.class);

    private final SshKeysManager    keysManager;
    private final CredentialsLoader credentialsLoader;
    private final UserProfileDao    userProfileDao;

    @Inject
    public NativeGitConnectionFactory(SshKeysManager keysManager, CredentialsLoader credentialsLoader, UserProfileDao userProfileDao) {
        this.keysManager = keysManager;
        this.credentialsLoader = credentialsLoader;
        this.userProfileDao = userProfileDao;
    }

    /** {@inheritDoc} */
    @Override
    public GitConnection getConnection(File workDir, GitUser user) throws GitException {
        return new NativeGitConnection(workDir, user, keysManager, credentialsLoader);
    }

    /** {@inheritDoc} */
    @Override
    public GitConnection getConnection(File workDir) throws GitException {
        List<Attribute> profileAttributes = null;
        try {
            Profile userProfile = userProfileDao.getById(EnvironmentContext.getCurrent().getUser().getId());
            if (userProfile != null) {
                profileAttributes = userProfile.getAttributes();
            }
        } catch (NotFoundException e) {
            LOG.warn("Failed to obtain user information.", e);
            throw new GitException("Failed to obtain user information.");
        } catch (ServerException e) {
            LOG.warn("Failed to obtain user information.", e);
            throw new GitException("Failed to obtain user information.");
        }

        String firstName = null, lastName = null;

        if (profileAttributes != null) {
            for (Attribute attribute : profileAttributes) {
                if ("firstName".equals(attribute.getName())) {
                    firstName = attribute.getValue();
                } else if ("lastName".equals(attribute.getName())) {
                    lastName = attribute.getValue();
                }
            }
        }

        GitUser gitUser = DtoFactory.getInstance().createDto(GitUser.class);

        if (firstName != null || lastName != null) {
            gitUser.withEmail(Strings.join(" ", Strings.nullToEmpty(firstName), Strings.nullToEmpty(lastName)))
                   .withEmail(EnvironmentContext.getCurrent().getUser().getName());
        } else {
            gitUser.withEmail(EnvironmentContext.getCurrent().getUser().getName());
        }

        return getConnection(workDir, gitUser);
    }
}
