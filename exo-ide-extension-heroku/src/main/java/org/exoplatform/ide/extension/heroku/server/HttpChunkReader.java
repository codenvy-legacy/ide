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
package org.exoplatform.ide.extension.heroku.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.apache.commons.codec.binary.Base64.encodeBase64;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class HttpChunkReader {
    private static final byte[] NO_DATA = new byte[0];
    private URL nextChunk;

    public HttpChunkReader(URL firstChunk) {
        this.nextChunk = firstChunk;
    }

    /** @return <code>true</code> if end of file reached <code>false</code> otherwise */
    public boolean eof() {
        return nextChunk == null;
    }

    /**
     * Read next portion of data.
     *
     * @return set of bytes from heroku server or empty array if data not ready yet. If empty array returned then caller
     *         should check method {@link #eof()} and try again if end of file is not reached yet
     * @throws IOException
     *         if any i/o error occurs
     * @throws HerokuException
     *         if heroku server return unexpected or error status for request
     */
    public byte[] next() throws IOException, HerokuException {
        if (eof())
            throw new IllegalStateException("End of output reached. ");

        HttpURLConnection http = null;
        try {
            http = (HttpURLConnection)nextChunk.openConnection();
            http.setRequestMethod("GET");

            http.setRequestProperty("Authorization",
                                    "Basic " +
                                    new String(encodeBase64((nextChunk.getUserInfo() + ":").getBytes("ISO-8859-1")), "ISO-8859-1"));

            int status = http.getResponseCode();

            if (!(status == 200 || status == 204))
                throw Heroku.fault(http);

            byte[] data = NO_DATA;

            if (status == 200) {
                InputStream input = http.getInputStream();
                try {
                    final int length = http.getContentLength();
                    if (length > 0) {
                        data = new byte[length];
                        for (int r = -1, off = 0; (r = input.read(data, off, length - off)) > 0; off += r) //
                            ;
                    } else if (length < 0) {
                        byte[] buf = new byte[1024];
                        ByteArrayOutputStream bout = new ByteArrayOutputStream();
                        int point = -1;
                        while ((point = input.read(buf)) != -1)
                            bout.write(buf, 0, point);
                        data = bout.toByteArray();
                    }
                } finally {
                    input.close();
                }
            }

            String location = http.getHeaderField("Location");
            if (location == null && status != 204)
                nextChunk = null;
            else if (location != null)
                nextChunk = new URL(location);
            return data;
        } finally {
            if (http != null) {
                http.disconnect();
            }
        }
    }
}
