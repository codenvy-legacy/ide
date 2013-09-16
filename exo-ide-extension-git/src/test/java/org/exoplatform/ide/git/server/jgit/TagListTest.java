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
package org.exoplatform.ide.git.server.jgit;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.exoplatform.ide.git.shared.Tag;
import org.exoplatform.ide.git.shared.TagListRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: TagListTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class TagListTest extends BaseTest {
    private RevTag bugfixTag;

    private RevTag featureTag;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Repository repo = getDefaultRepository();

        RevWalk revWalker = new RevWalk(repo);
        Git git = new Git(repo);

        Ref bugfixRef = git.tag().setName("bugfix-tag").setMessage("bugfix-tag").call();
        Ref featureRef = git.tag().setName("feature-tag").setMessage("feature-tag").call();

        bugfixTag = revWalker.parseTag(bugfixRef.getLeaf().getObjectId());
        featureTag = revWalker.parseTag(featureRef.getLeaf().getObjectId());
    }

    public void testListAllTag() throws Exception {
        List<Tag> tagList = getDefaultConnection().tagList(new TagListRequest());
        validateTags(tagList, bugfixTag.getTagName(), featureTag.getTagName());
    }

    public void testListTagPattern() throws Exception {
        List<Tag> tagList = getDefaultConnection().tagList(new TagListRequest("feature*"));
        validateTags(tagList, featureTag.getTagName());
    }

    private void validateTags(List<Tag> tagList, String... expNames) {
        assertEquals(expNames.length, tagList.size());
        List<String> names = new ArrayList<String>(tagList.size());
        for (Tag t : tagList)
            names.add(t.getName());
        for (String name : expNames)
            assertTrue("Expected tag " + name + " not found in result. ", names.contains(name));
    }
}
