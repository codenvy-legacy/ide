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
package org.exoplatform.ide.git.client;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.ide.git.client.marshaller.BranchListUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.BranchUnmarshaller;
import org.exoplatform.ide.git.shared.Branch;

import java.util.ArrayList;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 27, 2011 3:32:28 PM anya $
 */
public class BranchesUnmarshallerGwtTest extends BaseGwtTest {
    private final String BRANCH_LIST_RESPONSE =
            "[{\"name\":\"refs/heads/12\",\"active\":false,\"displayName\":\"12\"},{\"name\":\"refs/heads/master\",\"active\":true," +
            "\"displayName\":\"master\"}]";

    private final String BRANCH_RESPONSE = "{\"name\":\"refs/heads/12\",\"active\":false,\"displayName\":\"12\"}";

    /**
     * Test branch list response unmarshaller.
     *
     * @throws UnmarshallerException
     */
    public void testBranchListUnmarshaller() throws UnmarshallerException {
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
    public void testBranchUnmarshaller() throws UnmarshallerException {
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
    public void testBranchListEmptyUnmarshaller() throws UnmarshallerException {
        java.util.List<Branch> branches = new ArrayList<Branch>();
        BranchListUnmarshaller unmarshaller = new BranchListUnmarshaller(branches);
        unmarshaller.unmarshal(new MockResponse(""));

        assertEquals(0, branches.size());
    }
}
