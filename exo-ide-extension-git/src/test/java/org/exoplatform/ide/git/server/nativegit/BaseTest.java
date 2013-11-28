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
package org.exoplatform.ide.git.server.nativegit;

import junit.framework.TestCase;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.ide.git.server.GitConnection;
import org.exoplatform.ide.git.server.GitConnectionFactory;
import org.exoplatform.ide.git.server.GitException;
import org.exoplatform.ide.git.server.nativegit.commands.EmptyGitCommand;
import org.exoplatform.ide.git.server.nativegit.commands.ListFilesCommand;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.git.shared.GitUser;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Base test for others.
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public abstract class BaseTest extends TestCase {

    protected final String CONTENT = "git repository content\n";
    protected final String DEFAULT_URI = "user@host.com:login/repo";
    protected final List<File> forClean = new ArrayList<>();
    private final String DEFAULT_REPO_NAME = "repository1";
    protected GitConnectionFactory connectionFactory;
    protected ExoContainer container;
    private File defaultRepository;
    private GitConnection defaultConnection;
    private File target;
    private GitUser defaultUser = new GitUser("user", "user@email.com");

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        URL testCls = Thread.currentThread().getContextClassLoader().getResource(".");
        target = new File(testCls.toURI()).getParentFile();
        defaultRepository = new File(target.getAbsolutePath(), DEFAULT_REPO_NAME);
        defaultRepository.mkdir();
        //Initialize new repository target/repository1 with 1 new file README.txt
        NativeGit ngit = new NativeGit(defaultRepository);
        ngit.createInitCommand().execute();
        //set up configuration
        ngit.createConfig().setUser(new GitUser("alex", "alex@gmail.com")).saveUser();
        //add new file
        addFile(defaultRepository, "README.txt", CONTENT);
        ngit.createAddCommand().setFilePattern(new String[]{"README.txt"}).execute();
        ngit.createCommitCommand().setMessage("Initial Commit").execute();
        forClean.add(defaultRepository);
        URL url = Thread.currentThread().getContextClassLoader().getResource("conf/test-configuration.xml");
        StandaloneContainer.setConfigurationPath(url.getPath());
        container = StandaloneContainer.getInstance();
        ConversationState.setCurrent(new ConversationState(new Identity("user")));
        connectionFactory = (GitConnectionFactory) container
                .getComponentInstanceOfType(GitConnectionFactory.class);
        defaultConnection = connectionFactory.getConnection(getDefaultRepository(), getDefaultUser());
    }

    @Override
    protected void tearDown() throws Exception {
        for (File file : forClean)
            delete(file);
        forClean.clear();
        super.tearDown();
    }

    public File getTarget() {
        return target;
    }

    public GitUser getDefaultUser() {
        return defaultUser;
    }

    public GitConnection getDefaultConnection() {
        return defaultConnection;
    }

    public File getDefaultRepository() {
        return defaultRepository;
    }

    protected void delete(File fileOrDir) {
        if (!fileOrDir.exists())
            return;

        if (fileOrDir.isDirectory()) {
            File[] fileList = fileOrDir.listFiles();
            if (fileList != null) {
                for (File i : fileList)
                    delete(i);
            }
        }
        if (!fileOrDir.delete()) {
            for (int attempt = 0; fileOrDir.exists() && attempt < 5; attempt++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // ignore
                }
                if (fileOrDir.delete())
                    break;
            }
            if (fileOrDir.exists())
                throw new RuntimeException("Unable delete " + fileOrDir.getAbsolutePath());
        }
    }

    protected File addFile(File parent, String name, String content) throws IOException {
        File file = new File(parent, name);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileWriter writer = null;
        try {
            writer = new FileWriter(file);
            writer.write(content);
            writer.flush();
        } finally {
            if (writer != null)
                writer.close();
        }
        return file;
    }

    protected String readFile(File file) throws IOException {
        if (file.isDirectory())
            throw new IllegalArgumentException("Can't read content from directory " + file.getAbsolutePath());
        FileReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            reader = new FileReader(file);
            int ch = -1;
            while ((ch = reader.read()) != -1)
                content.append((char) ch);
        } finally {
            if (reader != null)
                reader.close();
        }
        return content.toString();
    }

    protected int getCountOfCommitsInCurrentBranch(File repo) throws GitException {
        EmptyGitCommand emptyGitCommand = new EmptyGitCommand(repo);
        emptyGitCommand.setNextParameter("rev-list")
                .setNextParameter("HEAD")
                .setNextParameter("--count")
                .execute();
        return Integer.parseInt(emptyGitCommand.getOutputMessage());
    }

    protected void validateBranchList(List<Branch> toValidate, List<Branch> pattern) {
        l1:
        for (Branch tb : toValidate) {
            for (Branch pb : pattern) {
                if (tb.getName().equals(pb.getName()) //
                        && tb.getDisplayName().equals(pb.getDisplayName()) //
                        && tb.isActive() == pb.isActive())
                    continue l1;
            }
            fail("List of branches is not matches to expected. Branch " + tb + " is not expected in result. ");
        }
    }

    protected void checkNotCached(File repository, String... fileNames) throws GitException {
        ListFilesCommand lf = new ListFilesCommand(repository);
        lf.setCached(true).execute();
        List<String> output = lf.getOutput();
        for (String fName : fileNames) {
            if (output.contains(fName)) {
                fail("Cache contains " + fName);
            }
        }
    }

    protected void checkCached(File repository, String... fileNames) throws GitException {
        ListFilesCommand lf = new ListFilesCommand(repository);
        lf.setCached(true).execute();
        List<String> output = lf.getOutput();
        for (String fName : fileNames) {
            if (!output.contains(fName)) {
                fail("Cache not contains " + fName);
            }
        }
    }


}

