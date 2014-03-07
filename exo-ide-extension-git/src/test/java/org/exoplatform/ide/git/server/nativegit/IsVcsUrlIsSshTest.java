/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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

import org.exoplatform.ide.git.server.provider.GitVendorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

/**
 * @author Alexander Garagatyi
 */
@RunWith(Parameterized.class)
public class IsVcsUrlIsSshTest {
    private String  url;
    private boolean expected;

    public IsVcsUrlIsSshTest(String url, Boolean expected) {
        this.url = url;
        this.expected = expected;
    }

    @Test
    public void shouldBeAbleToIdentifySshUrl() {
        assertEquals(expected, GitVendorService.isVcsUrlIsSSH(url));
    }

    @Parameterized.Parameters
    public static Collection data() {
        final Object[][] data = new Object[][]{
                // not a ssh urls
                {"https://github.com/ssh/ssh.git", false},
                {"git://ssh.com/ssh/ssh.git", false},
                {"git://garagatyi@ssh.com/ssh/ssh.git", false},
                {"https://ssh@ssh.com/ssh/ssh.git", false},
                //{"git@github.com/codenvy/cloud-ide.git", false},
                {"garagatyi@git@github.com/codenvy/cloud-ide.git", false},
                //{"ssh://git@github.com:codenvy/cloud-ide.git", false},
                //{"git@github.com:456:codenvy/cloud-ide.git", false},
                //{"ssh@github.com:789:codenvy/cloud-ide.git", false},
                //{"ssh://garagatyi@review.gerrithub.io:garagatyi/JdbmKeyValueStorage", false},
                // valid ssh urls
                {"git@github.com:codenvy/cloud-ide.git", true},
                {"git@github.com:codenvy/cloud-ide", true},
                {"ssh://git@github.com/codenvy/cloud-ide.git", true},
                {"ssh://git@github.com/codenvy/cloud-ide", true},
                {"ssh://garagatyi@review.gerrithub.io:29418/garagatyi/JdbmKeyValueStorage", true}
        };

        return Arrays.asList(data);
    }
}
