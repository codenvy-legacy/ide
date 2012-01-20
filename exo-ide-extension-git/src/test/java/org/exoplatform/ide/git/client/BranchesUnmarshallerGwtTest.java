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
package org.exoplatform.ide.git.client;

import org.exoplatform.gwtframework.commons.rest.copy.UnmarshallerException;
import org.exoplatform.ide.git.client.marshaller.BranchListUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.BranchUnmarshaller;
import org.exoplatform.ide.git.shared.Branch;

import java.util.ArrayList;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 27, 2011 3:32:28 PM anya $
 * 
 */
public class BranchesUnmarshallerGwtTest extends BaseGwtTest
{
   private final String BRANCH_LIST_RESPONSE =
      "[{\"name\":\"refs/heads/12\",\"active\":false,\"displayName\":\"12\"},{\"name\":\"refs/heads/master\",\"active\":true,\"displayName\":\"master\"}]";

   private final String BRANCH_RESPONSE = "{\"name\":\"refs/heads/12\",\"active\":false,\"displayName\":\"12\"}";

   /**
    * Test branch list response unmarshaller.
    * 
    * @throws UnmarshallerException
    */
   public void testBranchListUnmarshaller() throws UnmarshallerException
   {
      java.util.List<Branch> branches = new ArrayList<Branch>();
      BranchListUnmarshaller unmarshaller = new BranchListUnmarshaller(branches);
      unmarshaller.unmarshal(new MockResponse(BRANCH_LIST_RESPONSE));

      assertEquals(2, branches.size());
      Branch branch = branches.get(0);
      assertEquals("refs/heads/12", branch.getName());
      assertEquals("12", branch.getDisplayName());
      assertFalse(branch.isActive());

      branch = branches.get(1);
      assertEquals("refs/heads/master", branch.getName());
      assertEquals("master", branch.getDisplayName());
      assertTrue(branch.isActive());
   }

   /**
    * Test branch unmarshaller.
    * 
    * @throws UnmarshallerException
    */
   public void testBranchUnmarshaller() throws UnmarshallerException
   {
      Branch branch = new Branch();
      BranchUnmarshaller unmarshaller = new BranchUnmarshaller(branch);
      unmarshaller.unmarshal(new MockResponse(BRANCH_RESPONSE));

      assertEquals("refs/heads/12", branch.getName());
      assertEquals("12", branch.getDisplayName());
      assertFalse(branch.isActive());
   }

   /**
    * Test branch unmarshaller.
    * 
    * @throws UnmarshallerException
    */
   public void testBranchListEmptyUnmarshaller() throws UnmarshallerException
   {
      java.util.List<Branch> branches = new ArrayList<Branch>();
      BranchListUnmarshaller unmarshaller = new BranchListUnmarshaller(branches);
      unmarshaller.unmarshal(new MockResponse(""));

      assertEquals(0, branches.size());
   }
}
