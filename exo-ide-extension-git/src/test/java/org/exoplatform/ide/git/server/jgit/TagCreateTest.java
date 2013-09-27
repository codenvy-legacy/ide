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

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.exoplatform.ide.git.shared.Tag;
import org.exoplatform.ide.git.shared.TagCreateRequest;

import java.util.Map;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: TagCreateTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class TagCreateTest extends BaseTest {
    public void testCreateTag() throws Exception {
        Tag tag =
                getDefaultConnection().tagCreate(new TagCreateRequest("new_tag", null/*From HEAD*/, "test create tag"));
        Map<String, Ref> tags = getDefaultRepository().getTags();

        Ref refTag = tags.get(tag.getName());
        assertNotNull(refTag);

        RevTag revTag = new RevWalk(getDefaultRepository()).parseTag(refTag.getLeaf().getObjectId());

        assertEquals("test create tag", revTag.getFullMessage());
        PersonIdent tagger = revTag.getTaggerIdent();
        assertNotNull(tagger);
        assertEquals("andrey", tagger.getName());
        assertEquals("andrey@mail.com", tagger.getEmailAddress());
        assertEquals("new_tag", revTag.getTagName());
    }
}
