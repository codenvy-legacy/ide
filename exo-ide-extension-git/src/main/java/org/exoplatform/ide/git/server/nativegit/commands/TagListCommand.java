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
package org.exoplatform.ide.git.server.nativegit.commands;

import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.shared.Tag;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Get list of tags
 *
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class TagListCommand extends GitCommand<List<Tag>> {

    private String pattern;

    public TagListCommand(File repository) {
        super(repository);
    }

    /**
     * @see org.exoplatform.ide.git.server.nativegit.commands.GitCommand#execute()
     */
    @Override
    public List<Tag> execute() throws GitException {
        clear();
        commandLine.add("tag", "-l");
        if (pattern != null) {
            commandLine.add(pattern);
        }
        start();
        List<Tag> listOfTags = new LinkedList<>();
        for (String outLine : getOutput()) {
            listOfTags.add(new Tag(outLine));
        }
        return listOfTags;
    }

    /**
     * @param pattern tag pattern
     * @return TagListCommand with established pattern
     */
    public TagListCommand setPattern(String pattern) {
        this.pattern = pattern;
        return this;
    }
}
