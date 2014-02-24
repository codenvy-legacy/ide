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
package com.codenvy.ide.factory.server;

import com.codenvy.organization.client.WorkspaceManager;

import org.exoplatform.ide.git.server.GitConnectionFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.*;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

@Listeners(value = {MockitoTestNGListener.class})
public class GitRepositoryPrivacyCheckerTest {
    @Mock
    private WorkspaceManager            workspaceManager;
    @Mock
    private GitConnectionFactory        gitConnectionFactory;
    @InjectMocks
    private GitRepositoryPrivacyChecker permissionSetter;

    private Pattern sshPattern;

    @BeforeMethod
    public void setUp() throws Exception {
        Field field = GitRepositoryPrivacyChecker.class.getDeclaredField("SSH_URL");
        field.setAccessible(true);
        sshPattern = (Pattern)field.get(null);
    }

    @Test(dataProvider = "validSshUrlsProvider")
    public void sshPatternShouldMatchSshUrls(String url) {
        boolean flag = sshPattern.matcher(url).matches();
        assertTrue(flag);
    }

    @DataProvider(name = "validSshUrlsProvider")
    public Object[][] validSshUrlsProvider() {
        return new Object[][]{{"git@github.com:git/git.git"},
                              {"ssh://git@github.com/git/git.git"},
                              {"git@bitbucket.org:codenvy/test.git"},
                              {"ssh://git@bitbucket.org/codenvy/test.git"},
                              {"ssh://garagatyi@review.gerrithub.io:29418/codenvy/test"},
                              {"ssh://git@github.com/ssh/ssh.git"},

        };
    }

    @Test(dataProvider = "notSshUrlsProvider")
    public void sshPatternShouldNotMatchNotSshUrls(String url) {
        assertFalse(sshPattern.matcher(url).matches());
    }

    @DataProvider(name = "notSshUrlsProvider")
    public Object[][] notSshUrlsProvider() {
        return new Object[][]{{"https://github.com/git/git.git"},
                              {"https://garagatyi@github.com/git/git.git"},
                              {"http://github.com/git/git.git"},
                              {"http://garagatyi@github.com/git/git.git"},
                              {"git://github.com/git/git.git"},
                              {"https://bitbucket.org/codenvy/test.git"},
                              {"https://agaragatyi@bitbucket.org/codenvy/test.git"},
                              {"http://bitbucket.org/codenvy/test.git"},
                              {"http://agaragatyi@bitbucket.org/codenvy/test.git"},
                              {"http://codenvy.com/git/c4/1a/b9/workspace0nzgq70h7qpz7epd/PrivateProject2"},
                              {"https://garagatyi@review.gerrithub.io/codenvy/test"},
                              {"https://review.gerrithub.io/codenvy/test"},
        };
    }
}
