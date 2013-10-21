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
import org.exoplatform.ide.git.shared.TagCreateRequest;
import org.junit.Test;

/**
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class TagCreateTest extends BaseTest {

    @Test
    public void testCreateTag() throws GitException {
        NativeGit defaultGit = new NativeGit(getDefaultRepository());
        TagListCommand tagList = defaultGit.createTagListCommand();
        int beforeTagCount = tagList.execute().size();
        getDefaultConnection().tagCreate(new TagCreateRequest("v1", null, "first version", false));
        int afterTagCount = tagList.execute().size();
        assertEquals(beforeTagCount, afterTagCount - 1);
    }

    @Test
    public void testCreateTagForce() throws GitException {
        NativeGit defaultGit = new NativeGit(getDefaultRepository());
        getDefaultConnection().tagCreate(new TagCreateRequest("v1", null, "first version", false));
        try {
            getDefaultConnection().tagCreate(new TagCreateRequest("v1", null, "first version", false));
            fail("It is not force, should be exception.");
        } catch (GitException ignored) {
        }

        getDefaultConnection().tagCreate(new TagCreateRequest("v1", null, "second version", true));
        assertTrue(defaultGit.createTagListCommand().execute().get(0).getName().equals("v1"));
    }
}
