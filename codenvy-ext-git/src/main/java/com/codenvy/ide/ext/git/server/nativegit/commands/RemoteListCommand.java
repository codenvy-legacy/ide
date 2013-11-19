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
package com.codenvy.ide.ext.git.server.nativegit.commands;

import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.ext.git.shared.Remote;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Used for getting list of remotes
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class RemoteListCommand extends GitCommand<List<Remote>> {

    private String remoteName;

    public RemoteListCommand(File place) {
        super(place);
    }

    /** @see com.codenvy.ide.ext.git.server.nativegit.commands.GitCommand#execute() */
    @Override
    public List<Remote> execute() throws GitException {
        clear();
        commandLine.add("remote", "-v");
        start();
        //get all remotes with urls
        Map<String, String> remotes = new HashMap<>();
        for (String outLine : getOutput()) {
            String[] outArr = outLine.split("\t");
            remotes.put(outArr[0], outArr[1].split(" ")[0]);
        }
        //parse remtes
        List<Remote> remoteList = new LinkedList<>();
        if (remoteName != null) {
            if (remotes.get(remoteName) == null) {
                throw new GitException("No remote with name " + remoteName);
            }
            Remote remote = DtoFactory.getInstance().createDto(Remote.class).withName(remoteName).withUrl(remotes.get(remoteName));
            remoteList.add(remote);
        } else {
            for (Map.Entry<String, String> entry : remotes.entrySet()) {
                Remote remote = DtoFactory.getInstance().createDto(Remote.class).withName(entry.getKey()).withUrl(entry.getValue());
                remoteList.add(remote);
            }
        }
        return remoteList;
    }

    /**
     * @param remoteName
     *         remote name
     * @return RemoteListCommand with established remote name
     */
    public RemoteListCommand setRemoteName(String remoteName) {
        this.remoteName = remoteName;
        return this;
    }
}
