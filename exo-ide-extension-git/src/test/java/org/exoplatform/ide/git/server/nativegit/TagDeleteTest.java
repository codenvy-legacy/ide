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

import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.server.nativegit.commands.TagListCommand;
import org.exoplatform.ide.git.shared.Tag;
import org.exoplatform.ide.git.shared.TagDeleteRequest;
import org.junit.Test;

import java.util.List;

/**
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class TagDeleteTest extends BaseTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        NativeGit defaultGit = new NativeGit(getDefaultRepository());
        defaultGit.createTagCreateCommand().setName("first-tag").execute();
        defaultGit.createTagCreateCommand().setName("second-tag").execute();
    }

    @Test
    public void testDeleteTag() throws GitException {
        TagListCommand tagListCommand = new TagListCommand(getDefaultRepository());
        assertTrue(tagExists(tagListCommand.execute(), "first-tag"));
        assertTrue(tagExists(tagListCommand.execute(), "second-tag"));
        //delete first-tag
        getDefaultConnection().tagDelete(new TagDeleteRequest("first-tag"));
        //check not exists more
        assertFalse(tagExists(tagListCommand.execute(), "first-tag"));
        assertTrue(tagExists(tagListCommand.execute(), "second-tag"));
    }

    private boolean tagExists(List<Tag> list, String name) {
        for (Tag tag : list) {
            if (tag.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
