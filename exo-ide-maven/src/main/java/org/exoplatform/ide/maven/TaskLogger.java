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
package org.exoplatform.ide.maven;

import org.apache.maven.shared.invoker.InvocationOutputHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.*;

/**
 * File based TaskLogger.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class TaskLogger implements InvocationOutputHandler {

    private static final Logger LOG = LoggerFactory.getLogger(TaskLogger.class);

    /** Log file. */
    private final File   file;
    /** Log writer. */
    private final Writer writer;

    /**
     * InvocationOutputHandler for proxying log messages.
     * It may be useful if need store log messages in file and at the same time print them to stdout.
     */
    private final InvocationOutputHandler delegate;

    /**
     * @param file
     *         the log tile
     * @param delegate
     *         instance of InvocationOutputHandler for proxying of logs. This parameter may be <code>null</code>
     *         if not need to proxying of log messages
     */
    public TaskLogger(File file, InvocationOutputHandler delegate) {
        this.file = file;
        this.delegate = delegate;
        try {
            writer = new BufferedWriter(new FileWriter(file, true));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe.getMessage(), ioe);
        }
    }

    /**
     * @param file
     *         the log tile
     */
    public TaskLogger(File file) {
        this(file, null);
    }

    /**
     * Get Reader of build log.
     *
     * @return reader
     * @throws java.io.IOException
     *         if any i/o errors occur
     */
    public Reader getLogReader() throws IOException {
        return new FileReader(file);
    }

    /** @see org.codehaus.plexus.util.cli.StreamConsumer#consumeLine(java.lang.String) */
    @Override
    public void consumeLine(String line) {
        try {
            if (line != null) {
                writer.write(line);
            }
            writer.write('\n');
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

        if (delegate != null) {
            delegate.consumeLine(line);
        }
    }

    /** Close current TaskLogger. It should release any resources allocated by the TaskLogger such as file, etc. */
    public void close() {
        try {
            writer.close();
        } catch (IOException ignored) {
            // Ignore close error.
        }
    }

    /**
     * Get log file.
     *
     * @return the log file
     */
    public File getFile() {
        return file;
    }
}
