/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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
import java.util.Set;

/**
 * Native implementation for GitConnectionFactory
 *
 * @author Eugene Voevodin
 */
@Singleton
public class NativeGitConnectionFactory extends GitConnectionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(NativeGitConnectionFactory.class);

    private final SshKeysManager           keysManager;
    private final CredentialsLoader        credentialsLoader;
    private final UserProfileDao           userProfileDao;
    private final Set<CredentialsProvider> credentialsProviders;

    @Inject
    public NativeGitConnectionFactory(SshKeysManager keysManager, CredentialsLoader credentialsLoader, UserProfileDao userProfileDao,
                                      Set<CredentialsProvider> credentialsProviders) {
        this.keysManager = keysManager;
        this.credentialsLoader = credentialsLoader;
        this.userProfileDao = userProfileDao;
        this.credentialsProviders = credentialsProviders;
    }

    /** {@inheritDoc} */
    @Override
    public GitConnection getConnection(File workDir, GitUser user) throws GitException {
        return new NativeGitConnection(workDir, user, keysManager, credentialsLoader, credentialsProviders);
    }

    /** {@inheritDoc} */
    @Override
    public GitConnection getConnection(File workDir) throws GitException {
        return getConnection(workDir, getGitUser());
    }

    private GitUser getGitUser() throws GitException {
        List<Attribute> profileAttributes = null;
        try {
            Profile userProfile = userProfileDao.getById(EnvironmentContext.getCurrent().getUser().getId());
            if (userProfile != null) {
                profileAttributes = userProfile.getAttributes();
            }
        } catch (NotFoundException | ServerException e) {
            LOG.warn("Failed to obtain user information.", e);
            throw new GitException("Failed to obtain user information.");
        }

        String firstName = null, lastName = null, email = null;
        if (profileAttributes != null) {
            for (Attribute attribute : profileAttributes) {
                if ("firstName".equals(attribute.getName())) {
                    firstName = attribute.getValue();
                } else if ("lastName".equals(attribute.getName())) {
                    lastName = attribute.getValue();
                } else if ("email".equals(attribute.getName())) {
                    email = attribute.getValue();
                }
            }
        }

        GitUser gitUser = DtoFactory.getInstance().createDto(GitUser.class);
        if (firstName != null || lastName != null) {
            gitUser.withName(Strings.join(" ", Strings.nullToEmpty(firstName), Strings.nullToEmpty(lastName)));
        } else {
            gitUser.withName(EnvironmentContext.getCurrent().getUser().getName());
        }
        gitUser.withEmail(email != null ? email : EnvironmentContext.getCurrent().getUser().getName());

        return gitUser;
    }
}
