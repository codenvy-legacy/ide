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
package com.codenvy.ide.ext.git.server.nativegit.commands;

import com.codenvy.dto.server.DtoFactory;
import com.codenvy.ide.ext.git.server.GitException;
import com.codenvy.ide.ext.git.shared.Tag;

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
     * @see com.codenvy.ide.ext.git.server.nativegit.commands.GitCommand#execute()
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
            listOfTags.add(DtoFactory.getInstance().createDto(Tag.class).withName(outLine));
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
