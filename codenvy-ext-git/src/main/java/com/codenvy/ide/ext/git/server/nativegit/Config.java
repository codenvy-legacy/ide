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

import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.ext.git.server.nativegit.commands.GetConfigCommand;
import com.codenvy.ide.ext.git.server.nativegit.commands.SetConfigCommand;
import com.codenvy.ide.ext.git.shared.GitUser;

import java.io.File;

/**
 * Config is useful for git repository configuration manipulation.
 * For now it is available to load and save information about git repository user.
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class Config {

    private final File   repository;
    private       String username;
    private       String email;

    /**
     * @param repository
     *         git repository
     */
    public Config(File repository) {
        this.repository = repository;
    }

    /** @return repository user name */
    public String getUsername() {
        return username;
    }

    /** @return repository user email */
    public String getEmail() {
        return email;
    }

    /**
     * @param username
     *         set repository user name
     * @return Config object with user name parameter
     */
    public Config setUsername(String username) {
        this.username = username;
        return this;
    }

    /**
     * @param user
     *         repository user
     * @return Config object with repository user parameter
     */
    public Config setUser(GitUser user) {
        this.username = user.getName();
        this.email = user.getEmail();
        return this;
    }

    /**
     * @param email
     *         repository user email
     * @return
     */
    public Config setEmail(String email) {
        this.email = email;
        return this;
    }

    /** @return repository user */
    public GitUser getUser() {
        return DtoFactory.getInstance().createDto(GitUser.class).withName(username).withEmail(email);
    }

    /**
     * Saves user config.
     *
     * @throws GitException
     */
    public void saveUser() throws GitException {
        saveValue("user.name", username);
        saveValue("user.email", email);
    }

    /**
     * @param parameter
     *         git config file parameter such as user.name
     * @return value that responsible to parameter
     * @throws GitException
     */
    public String loadValue(String parameter) throws GitException {
        GetConfigCommand command = new GetConfigCommand(repository);
        command.setAttribute(parameter);
        command.execute();
        return command.getOutputMessage();
    }

    /**
     * @param parameter
     *         git config file parameter such as user.name
     * @param value
     *         value that will be written into git config file
     * @throws GitException
     *         when some error occurs
     */
    public void saveValue(String parameter, String value) throws GitException {
        SetConfigCommand command = new SetConfigCommand(repository);
        command.setValue(parameter, value);
        command.execute();
    }

    /**
     * Loads user config.
     *
     * @return Config with user parameters
     * @throws GitException
     *         when some error occurs
     */
    public Config loadUser() throws GitException {
        username = loadValue("user.name");
        email = loadValue("user.email");
        return this;
    }
}
