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
import org.eclipse.jgit.revwalk.RevTag;
import org.exoplatform.ide.git.shared.Tag;
import org.exoplatform.ide.git.shared.TagListRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: TagListTest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class TagListTest extends BaseTest
{
   private RevTag bugfixTag;
   private RevTag featureTag;

   @Override
   protected void setUp() throws Exception
   {
      super.setUp();
      Git git = new Git(getRepository());
      bugfixTag = git.tag().setName("bugfix-tag").setMessage("bugfix-tag").call();
      featureTag = git.tag().setName("feature-tag").setMessage("feature-tag").call();
   }

   public void testListAllTag() throws Exception
   {
      List<Tag> tagList = getConnection().tagList(new TagListRequest());
      validateTags(tagList, bugfixTag.getTagName(), featureTag.getTagName());
   }

   public void testListTagPattern() throws Exception
   {
      List<Tag> tagList = getConnection().tagList(new TagListRequest("feature*"));
      validateTags(tagList, featureTag.getTagName());
   }

   private void validateTags(List<Tag> tagList, String... expNames)
   {
      assertEquals(expNames.length, tagList.size());
      List<String> names = new ArrayList<String>(tagList.size());
      for (Tag t : tagList)
         names.add(t.getName());
      for (String name : expNames)
         assertTrue("Expected tag " + name + " not found in result. ", names.contains(name));
   }
}
