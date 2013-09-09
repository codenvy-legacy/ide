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
import org.exoplatform.ide.git.shared.TagDeleteRequest;

import java.util.Map;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: TagDeleteTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class TagDeleteTest extends BaseTest {
    private RevTag goodTag;

    private RevTag badTag;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Repository repo = getDefaultRepository();

        RevWalk revWalker = new RevWalk(repo);
        Git git = new Git(repo);

        Ref goodRef = git.tag().setName("good-tag").setMessage("good-tag").call();
        Ref badRef = git.tag().setName("bad-tag").setMessage("bad-tag").call();

        goodTag = revWalker.parseTag(goodRef.getLeaf().getObjectId());
        badTag = revWalker.parseTag(badRef.getLeaf().getObjectId());
    }

    public void testDeleteTag() throws Exception {
        Map<String, Ref> tags = getDefaultRepository().getTags();
        assertTrue(tags.containsKey(badTag.getTagName()));
        assertTrue(tags.containsKey(goodTag.getTagName()));

        getDefaultConnection().tagDelete(new TagDeleteRequest(badTag.getTagName()));

        tags = getDefaultRepository().getTags();
        assertFalse(tags.containsKey(badTag.getTagName()));
        assertTrue(tags.containsKey(goodTag.getTagName()));
    }
}
