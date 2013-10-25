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
import org.exoplatform.ide.git.shared.Tag;
import org.exoplatform.ide.git.shared.TagListRequest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="maito:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class TagListTest extends BaseTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        NativeGit defaultGit = new NativeGit(getDefaultRepository());
        defaultGit.createTagCreateCommand().setName("first-tag").execute();
        defaultGit.createTagCreateCommand().setName("first-tag-other").execute();
        defaultGit.createTagCreateCommand().setName("second-tag").execute();
    }

    @Test
    public void testTagList() throws GitException {
        validateTags(getDefaultConnection().tagList(
                new TagListRequest()), "first-tag", "first-tag-other", "second-tag");
    }

    @Test
    public void testTagListPattern() throws GitException {
        validateTags(getDefaultConnection().tagList(
                new TagListRequest("first*")), "first-tag", "first-tag-other");
    }

    protected void validateTags(List<Tag> tagList, String... expNames) {
        assertEquals(expNames.length, tagList.size());
        List<String> names = new ArrayList<String>(tagList.size());
        for (Tag t : tagList)
            names.add(t.getName());
        for (String name : expNames)
            assertTrue("Expected tag " + name + " not found in result. ", names.contains(name));
    }

}
