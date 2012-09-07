import junit.framework.Assert;

import org.exoplatform.ide.git.server.github.GitHub;
import org.exoplatform.ide.git.shared.Collaborators;
import org.exoplatform.ide.git.shared.GitHubUser;
import org.junit.Test;


/*
 * Copyright (C) 2012 eXo Platform SAS.
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

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: GutHubClientTest.java Aug 6, 2012
 */
public class GitHubClientTest
{
   
   @Test
   public void testGetCollaborators() throws Exception
   {
     GitHub github = new GitHub("eXoIDE", null, null);
     Collaborators collaborators = github.getCollaborators("eXoIDE", "rails-demo");
     Assert.assertEquals(1, collaborators.getCollaborators().size());
     GitHubUser user = collaborators.getCollaborators().get(0);
     Assert.assertNotNull(user);
     Assert.assertNotNull(user.getAvatarUrl());
   }

}
