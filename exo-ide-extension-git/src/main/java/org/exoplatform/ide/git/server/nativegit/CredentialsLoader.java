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
package org.exoplatform.ide.git.server.nativegit;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.services.security.ConversationState;
import org.picocontainer.Startable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * Load credentials
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class CredentialsLoader implements Startable {

    private static final Logger LOG                          = LoggerFactory.getLogger(CredentialsLoader.class);
    private static final String GIT_ASK_PASS_SCRIPT_TEMPLATE = "META-INF/NativeGitAskPassTemplate";
    private static final String GIT_ASK_PASS_SCRIPT          = "ask_pass";
    private static List<CredentialsProvider> instances;

    /**
     * Searches for available CredentialsProviders in container and stores it if any exists
     *
     * @param username user name that will be stored
     * @param password password that will be stored
     * @return stored script
     */
    public File createGitAskPassScript(CredentialItem.Username username, CredentialItem.Password password)
            throws GitException {

        String gitAskPassTemplate = "";

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Thread.currentThread().getContextClassLoader()
                                            .getResourceAsStream(GIT_ASK_PASS_SCRIPT_TEMPLATE)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                gitAskPassTemplate = gitAskPassTemplate.concat(line);
            }
        } catch (Exception e) {
            LOG.error("Can't load template " + GIT_ASK_PASS_SCRIPT_TEMPLATE);
        }

        File askScriptDirectory = new File(System.getProperty("java.io.tmpdir")
                                           + "/" + ConversationState.getCurrent().getIdentity().getUserId());
        if (!askScriptDirectory.exists()) {
            askScriptDirectory.mkdirs();
        }
        File gitAskPassScript = new File(askScriptDirectory, GIT_ASK_PASS_SCRIPT);
        try (FileOutputStream fos = new FileOutputStream(gitAskPassScript)) {
            gitAskPassTemplate = gitAskPassTemplate.replace("$self", gitAskPassScript.getAbsolutePath())
                                                   .replace("$password", password.toString())
                                                   .replace("$username", username.getValue());
            fos.write(gitAskPassTemplate.getBytes());
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
     * @param url given URL
     * @return stored script
     * @throws GitException when it is not possible to store credentials
     */
    public File findCredentialsAndCreateGitAskPassScript(String url) throws GitException {
        CredentialItem.Username username = new CredentialItem.Username();
        CredentialItem.Password password = new CredentialItem.Password();
        boolean flag = false;
        for (int i = 0, size = instances.size(); i < size && !flag; i++) {
            flag = instances.get(i).get(url, username, password);
        }
        //if not available providers exist
        if (!flag) {
            username.setValue("");
            password.setValue("");
        }
        return createGitAskPassScript(username, password);
    }

    @Override
    public void start() {
        instances = ExoContainerContext.getCurrentContainer().getComponentInstancesOfType(CredentialsProvider.class);
    }

    @Override
    public void stop() {
        //nothing to do
    }
}

