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

import com.codenvy.commons.lang.IoUtil;
import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.ide.ext.git.server.GitException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;


/**
 * Load credentials
 *
 * @author Eugene Voevodin
 */
@Singleton
public class CredentialsLoader {

    private static final Logger LOG                          = LoggerFactory.getLogger(CredentialsLoader.class);
    private static final String GIT_ASK_PASS_SCRIPT_TEMPLATE = "META-INF/NativeGitAskPassTemplate";
    private static final String GIT_ASK_PASS_SCRIPT          = "ask_pass";

    private String                   gitAskPassTemplate;
    private Set<CredentialsProvider> credentialsProviders;

    @Inject
    public CredentialsLoader(Set<CredentialsProvider> credentialsProviders) {
        this.credentialsProviders = credentialsProviders;
    }

    /**
     * Searches for available CredentialsProviders in container and stores it if any exists
     *
     * @param username
     *         user name that will be stored
     * @param password
     *         password that will be stored
     * @return stored script
     */
    public File createGitAskPassScript(CredentialItem.Username username, CredentialItem.Password password)
            throws GitException {
        File askScriptDirectory = new File(System.getProperty("java.io.tmpdir")
                                           + "/" + EnvironmentContext.getCurrent().getUser().getName());
        if (!askScriptDirectory.exists()) {
            askScriptDirectory.mkdirs();
        }
        File gitAskPassScript = new File(askScriptDirectory, GIT_ASK_PASS_SCRIPT);
        try (FileOutputStream fos = new FileOutputStream(gitAskPassScript)) {
            String actualGitAskPassTemplate = gitAskPassTemplate.replace("$self", gitAskPassScript.getAbsolutePath())
                                                                .replace("$password", password.toString())
                                                                .replace("$username", username.getValue());
            fos.write(actualGitAskPassTemplate.getBytes());
        } catch (IOException e) {
            LOG.error("It is not possible to store " + gitAskPassScript + " credentials", e);
            throw new GitException("Can't store credentials");
        }
        if (!gitAskPassScript.setExecutable(true)) {
            LOG.error("Can't make " + gitAskPassScript + " executable");
            throw new GitException("Can't set permissions to credentials");
        }
        return gitAskPassScript;
    }


    /**
     * Searches for CredentialsProvider instances and if needed instance exists, it stores
     * given credentials
     *
     * @param url
     *         given URL
     * @return stored script
     * @throws GitException
     *         when it is not possible to store credentials
     */
    public File findCredentialsAndCreateGitAskPassScript(String url) throws GitException {
        CredentialItem.Username username = new CredentialItem.Username();
        CredentialItem.Password password = new CredentialItem.Password();
        boolean isCredentialsPresent = false;
        for (CredentialsProvider cp : credentialsProviders) {
            if (isCredentialsPresent = cp.get(url, username, password)) {
                break;
            }
        }
        //if not available tokenProvider exist
        if (!isCredentialsPresent) {
            username.setValue("");
            password.setValue("");
        }
        return createGitAskPassScript(username, password);
    }

    public void removeAskPassScript() {
        File askScriptDirectory = new File(System.getProperty("java.io.tmpdir")
                                           + "/" + EnvironmentContext.getCurrent().getUser().getName());
        if (askScriptDirectory.exists()) {
            if (!IoUtil.deleteRecursive(askScriptDirectory))
                LOG.warn("Ask-pass script deletion failed.");
        }
    }

    @PostConstruct
    public void init() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream(GIT_ASK_PASS_SCRIPT_TEMPLATE)))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            gitAskPassTemplate = sb.toString();
        } catch (Exception e) {
            LOG.error("Can't load template " + GIT_ASK_PASS_SCRIPT_TEMPLATE);
        }
    }
}

