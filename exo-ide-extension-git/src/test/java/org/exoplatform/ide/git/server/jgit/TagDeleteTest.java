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

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevTag;
import org.exoplatform.ide.git.shared.TagDeleteRequest;

import java.util.Map;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: TagDeleteTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class TagDeleteTest extends BaseTest
{
   private RevTag goodTag;
   private RevTag badTag;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      Git git = new Git(getRepository());
      goodTag = git.tag().setName("good-tag").setMessage("good-tag").call();
      badTag = git.tag().setName("bad-tag").setMessage("bad-tag").call();
   }

   public void testDeleteTag() throws Exception
   {
      Map<String, Ref> tags = getRepository().getTags();
      assertTrue(tags.containsKey(badTag.getTagName()));
      assertTrue(tags.containsKey(goodTag.getTagName()));

      getClient().tagDelete(new TagDeleteRequest(badTag.getTagName()));

      tags = getRepository().getTags();
      assertFalse(tags.containsKey(badTag.getTagName()));
      assertTrue(tags.containsKey(goodTag.getTagName()));
   }
}
