/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.git.server.jgit;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevTag;
import org.eclipse.jgit.revwalk.RevWalk;
import org.exoplatform.ide.git.shared.GitUser;
import org.exoplatform.ide.git.shared.Tag;
import org.exoplatform.ide.git.shared.TagCreateRequest;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: TagCreateTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class TagCreateTest extends BaseTest
{
   public void testCreateTag() throws Exception
   {
      Tag tag =
         getConnection().tagCreate(
            new TagCreateRequest("new_tag", null/*From HEAD*/, new GitUser("andrey", "andrey@mail.com"),
               "test create tag"));
      java.util.Map<String, Ref> tags = getRepository().getTags();

      Ref refTag = tags.get(tag.getName());
      assertNotNull(refTag);

      RevTag revTag = new RevWalk(getRepository()).parseTag(refTag.getLeaf().getObjectId());

      assertEquals("test create tag", revTag.getFullMessage());
      PersonIdent tagger = revTag.getTaggerIdent();
      assertNotNull(tagger);
      assertEquals("andrey", tagger.getName());
      assertEquals("andrey@mail.com", tagger.getEmailAddress());
      assertEquals("new_tag", revTag.getTagName());
   }
}
