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
package com.codenvy.ide.ext.git.server;

import com.codenvy.ide.ext.git.shared.GitUser;
import com.codenvy.ide.ext.git.shared.Log;
import com.codenvy.ide.ext.git.shared.Revision;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: LogPage.java 79499 2012-02-15 15:42:46Z andrew00x $
 */
public class LogPage implements Log, InfoPage {
    // The same as C git does.
    private static final String     DATE_FORMAT = "EEE MMM dd HH:mm:ss yyyy ZZZZZ";

    private static final DateFormat dateFormat;
    
    protected List<Revision> commits;
    
    static {
        dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
        TimeZone timeZone = TimeZone.getDefault();
        dateFormat.setTimeZone(timeZone);
    }

    public LogPage(List<Revision> commits) {
        this.commits = commits;
    }

    /** @see com.codenvy.ide.ext.git.server.InfoPage#writeTo(java.io.OutputStream) */
    @Override
    public void writeTo(OutputStream out) throws IOException {
        PrintWriter writer = new PrintWriter(out);
        DateFormat df = (DateFormat)dateFormat.clone();
        for (Revision commit : commits) {
            writer.format("commit %s\n", commit.getId());

            GitUser commiter = commit.getCommitter();
            if (commiter != null) {
                writer.format("Author: %1$s <%2$s>\n", commiter.getName(), commiter.getEmail());
            }

            long commitTime = commit.getCommitTime();
            if (commitTime > 0) {
                writer.format("Date:   %s\n", df.format(new Date(commitTime)));
            }

            writer.println();

            // Message with indent.
            String[] lines = commit.getMessage().split("\n");
            for (String line : lines) {
                writer.format("    %s\n", line);
            }

            writer.println();
        }
        writer.flush();
    }

    /** {@inheritDoc} */
    @Override
    public List<Revision> getCommits() {
        return commits;
    }
}
