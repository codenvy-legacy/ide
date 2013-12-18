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
package com.codenvy.runner.docker;

import com.codenvy.api.core.util.Pair;
import com.codenvy.api.core.util.SystemInfo;
import com.codenvy.commons.json.JsonHelper;
import com.codenvy.commons.json.JsonNameConvention;
import com.codenvy.commons.json.JsonParseException;
import com.codenvy.runner.docker.json.ContainerConfig;
import com.codenvy.runner.docker.json.ContainerCreated;
import com.codenvy.runner.docker.json.ContainerExitStatus;
import com.codenvy.runner.docker.json.ContainerInfo;
import com.codenvy.runner.docker.json.HostConfig;
import com.codenvy.runner.docker.json.Image;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.sun.jna.LastErrorException;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;

import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

/** @author andrew00x */
public class DockerConnector {

    private static class InstanceHolder {
        private static final DockerConnector INSTANCE = new DockerConnector();
    }

    public static DockerConnector getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static final Logger LOG = LoggerFactory.getLogger(DockerConnector.class);

    public static final String DOCKER_SOCKET_PATH = "/var/run/docker.sock"; // Default path to unix socket file.

    /* ========== NATIVE ========== */
    private static final int AF_UNIX     = 1; // Defined in 'sys/socket.h'
    private static final int SOCK_STREAM = 1; // Defined in 'sys/socket.h'

    // Defined in 'unix.h', see http://man7.org/linux/man-pages/man7/unix.7.html
    public static class SockAddrUn extends Structure {
        public static final int UNIX_PATH_MAX = 108;

        public short  sun_family;
        public byte[] sun_path;

        private SockAddrUn(String path) {
            byte[] pathBytes = path.getBytes();
            if (pathBytes.length > UNIX_PATH_MAX) {
                throw new IllegalArgumentException(String.format("Path '%s' is too long. ", path));
            }
            sun_family = AF_UNIX;
            sun_path = new byte[pathBytes.length + 1];
            System.arraycopy(pathBytes, 0, sun_path, 0, Math.min(sun_path.length - 1, pathBytes.length));
            allocateMemory();
        }

        @Override
        protected List getFieldOrder() {
            return Arrays.asList("sun_family", "sun_path");
        }
    }

    // C language functions
    private interface CLibrary extends Library {
        int socket(int domain, int type, int protocol);

        int connect(int fd, SockAddrUn sock_addr, int addr_len);

        int send(int fd, byte[] buffer, int count, int flags);

        int recv(int fd, byte[] buffer, int count, int flags);

        int close(int fd);

        String strerror(int errno);
    }


    private static final CLibrary C_LIBRARY;

    static {
        CLibrary tmp = null;
        if (SystemInfo.isUnix()) {
            try {
                tmp = ((CLibrary)Native.loadLibrary("c", CLibrary.class));
            } catch (Exception e) {
                LOG.error("Cannot load native library", e);
            }
        }
        C_LIBRARY = tmp;
    }

     /* ========== END NATIVE ========== */

    private static void checkCLibrary() {
        if (C_LIBRARY == null) {
            throw new IllegalStateException("Can't load native library. Not unix system?");
        }
    }

    private DockerConnector() {
    }


    public Image[] listImages() throws IOException {
        final int fd = connect();
        try {
            final DockerResponse response = request(fd, "GET", "/images/json", Collections.<Pair<String, ?>>emptyList(), (String)null);
            final int status = response.getStatus();
            if (200 != status) {
                final String msg = CharStreams.toString(new InputStreamReader(response.getInputStream()));
                throw new IOException(String.format("Error response from docker API, status: %d, message: %s", status, msg));
            }
            return JsonHelper.fromJson(response.getInputStream(), Image[].class, null, FIRST_LETTER_LOWERCASE);
        } catch (JsonParseException e) {
            throw new IOException(e.getMessage(), e);
        } finally {
            close(fd);
        }
    }


    public void createImage(java.io.File dockerFile, java.io.File application, String name, Appendable output) throws IOException {
        final java.io.File tar = Files.createTempFile(application.getName(), "tar.gz").toFile();
        createTarGzArchive(tar, dockerFile, application);
        final int fd = connect();
        try (InputStream tarInput = new FileInputStream(tar)) {
            final List<Pair<String, ?>> headers = new ArrayList<>(2);
            headers.add(Pair.of("Content-Type", "application/tar"));
            headers.add(Pair.of("Content-Length", tar.length()));
            final DockerResponse response = request(fd, "POST", String.format("/build?t=%s&rm=%d", name, 1), headers, tarInput);
            final int status = response.getStatus();
            if (200 != status) {
                final String msg = CharStreams.toString(new InputStreamReader(response.getInputStream()));
                throw new IOException(String.format("Error response from docker API, status: %d, message: %s", status, msg));
            }
            CharStreams.copy(new InputStreamReader(response.getInputStream()), output);
        } finally {
            close(fd);
            if (!tar.delete()) {
                LOG.warn("Can't delete {}", tar);
            }
        }
    }

//  TODO: finish it
//    public void inspectImage(String image) throws IOException {
//        final int fd = connect();
//        try {
//            final DockerResponse response = request(fd,
//                                                    "GET",
//                                                    String.format("/images/%s/json", image),
//                                                    Collections.<Pair<String, ?>>emptyList(),
//                                                    (String)null);
//            final int status = response.getStatus();
//            if (200 != status) {
//                final String msg = CharStreams.toString(new InputStreamReader(response.getInputStream()));
//                throw new IOException(String.format("Error response from docker API, status: %d, message: %s", status, msg));
//            }
//            System.out.println(CharStreams.toString(new InputStreamReader(response.getInputStream())));
//        } finally {
//            close(fd);
//        }
//    }

    public void removeImage(String name) throws IOException {
        final int fd = connect();
        try {
            final DockerResponse response = request(fd,
                                                    "DELETE",
                                                    String.format("/images/%s", name),
                                                    Collections.<Pair<String, ?>>emptyList(),
                                                    (String)null);
            final int status = response.getStatus();
            if (200 != status) {
                final String msg = CharStreams.toString(new InputStreamReader(response.getInputStream()));
                throw new IOException(String.format("Error response from docker API, status: %d, message: %s", status, msg));
            }
            String output = CharStreams.toString(new InputStreamReader(response.getInputStream()));
            LOG.debug("remove image: {}", output);
        } finally {
            close(fd);
        }
    }


    public ContainerCreated createContainer(ContainerConfig config) throws IOException {
        final int fd = connect();
        try {
            final List<Pair<String, ?>> headers = new ArrayList<>(2);
            headers.add(Pair.of("Content-Type", "application/json"));
            final String body = JsonHelper.toJson(config, FIRST_LETTER_LOWERCASE);
            headers.add(Pair.of("Content-Length", body.getBytes().length));
            final DockerResponse response = request(fd, "POST", "/containers/create", headers, body);
            final int status = response.getStatus();
            if (201 != status) {
                final String msg = CharStreams.toString(new InputStreamReader(response.getInputStream()));
                throw new IOException(String.format("Error response from docker API, status: %d, message: %s", status, msg));
            }
            return JsonHelper.fromJson(response.getInputStream(), ContainerCreated.class, null, FIRST_LETTER_LOWERCASE);
        } catch (JsonParseException e) {
            throw new IOException(e.getMessage(), e);
        } finally {
            close(fd);
        }
    }


    public void startContainer(String container, HostConfig config) throws IOException {
        final int fd = connect();
        try {
            final List<Pair<String, ?>> headers = new ArrayList<>(2);
            headers.add(Pair.of("Content-Type", "application/json"));
            final String body = config == null ? "{}" : JsonHelper.toJson(config, FIRST_LETTER_LOWERCASE);
            headers.add(Pair.of("Content-Length", body.getBytes().length));
            final DockerResponse response = request(fd, "POST", String.format("/containers/%s/start", container), headers, body);
            final int status = response.getStatus();
            if (204 != status) {
                final String msg = CharStreams.toString(new InputStreamReader(response.getInputStream()));
                throw new IOException(String.format("Error response from docker API, status: %d, message: %s", status, msg));
            }
        } finally {
            close(fd);
        }
    }


    public void stopContainer(String container, long timeOut, TimeUnit timeunit) throws IOException {
        final int fd = connect();
        try {
            final List<Pair<String, ?>> headers = new ArrayList<>(2);
            headers.add(Pair.of("Content-Type", "text/plain"));
            headers.add(Pair.of("Content-Length", 0));
            final DockerResponse response = request(fd,
                                                    "POST",
                                                    String.format("/containers/%s/stop?t=%d", container, timeunit.toSeconds(timeOut)),
                                                    headers,
                                                    (String)null);
            final int status = response.getStatus();
            if (204 != status) {
                final String msg = CharStreams.toString(new InputStreamReader(response.getInputStream()));
                throw new IOException(String.format("Error response from docker API, status: %d, message: %s", status, msg));
            }
        } finally {
            close(fd);
        }
    }

    public void removeContainer(String container, boolean removeVolumes) throws IOException {
        final int fd = connect();
        try {
            final DockerResponse response = request(fd,
                                                    "DELETE",
                                                    String.format("/containers/%s?v=%d", container, removeVolumes ? 1 : 0),
                                                    Collections.<Pair<String, ?>>emptyList(),
                                                    (String)null);
            final int status = response.getStatus();
            if (204 != status) {
                final String msg = CharStreams.toString(new InputStreamReader(response.getInputStream()));
                throw new IOException(String.format("Error response from docker API, status: %d, message: %s", status, msg));
            }
        } finally {
            close(fd);
        }
    }


    public ContainerExitStatus waitContainer(String container) throws IOException {
        final int fd = connect();
        try {
            final List<Pair<String, ?>> headers = new ArrayList<>(2);
            headers.add(Pair.of("Content-Type", "text/plain"));
            headers.add(Pair.of("Content-Length", 0));
            final DockerResponse response = request(fd,
                                                    "POST",
                                                    String.format("/containers/%s/wait", container),
                                                    headers,
                                                    (String)null);
            final int status = response.getStatus();
            if (200 != status) {
                final String msg = CharStreams.toString(new InputStreamReader(response.getInputStream()));
                throw new IOException(String.format("Error response from docker API, status: %d, message: %s", status, msg));
            }
            return JsonHelper.fromJson(response.getInputStream(), ContainerExitStatus.class, null, FIRST_LETTER_LOWERCASE);
        } catch (JsonParseException e) {
            throw new IOException(e.getMessage(), e);
        } finally {
            close(fd);
        }
    }


    public ContainerInfo inspectContainer(String container) throws IOException {
        final int fd = connect();
        try {
            final DockerResponse response = request(fd,
                                                    "GET",
                                                    String.format("/containers/%s/json", container),
                                                    Collections.<Pair<String, ?>>emptyList(),
                                                    (String)null);
            final int status = response.getStatus();
            if (200 != status) {
                final String msg = CharStreams.toString(new InputStreamReader(response.getInputStream()));
                throw new IOException(String.format("Error response from docker API, status: %d, message: %s", status, msg));
            }
            return JsonHelper.fromJson(response.getInputStream(), ContainerInfo.class, null, FIRST_LETTER_LOWERCASE);
        } catch (JsonParseException e) {
            throw new IOException(e.getMessage(), e);
        } finally {
            close(fd);
        }
    }


    public void getContainerLogs(String container, Appendable output) throws IOException {
        final int fd = connect();
        try {
            final List<Pair<String, ?>> headers = new ArrayList<>(2);
            headers.add(Pair.of("Content-Type", "text/plain"));
            headers.add(Pair.of("Content-Length", 0));
            final DockerResponse response = request(fd,
                                                    "POST",
                                                    String.format("/containers/%s/attach?logs=%d&stdout=%d&stderr=%d", container, 1, 1, 1),
                                                    headers,
                                                    (String)null);
            final int status = response.getStatus();
            if (200 != status) {
                final String msg = CharStreams.toString(new InputStreamReader(response.getInputStream()));
                throw new IOException(String.format("Error response from docker API, status: %d, message: %s", status, msg));
            }
            CharStreams.copy(new InputStreamReader(response.getInputStream()), output);
        } finally {
            close(fd);
        }
    }


    private DockerResponse request(int fd, String method, String path, List<Pair<String, ?>> headers, InputStream input)
            throws IOException {
        final OutputStream output = new BufferedOutputStream(openOutputStream(fd));
        writeHttpHeaders(output, method, path, headers);
        if (input != null) {
            ByteStreams.copy(input, output);
            output.flush();
        }
        return new DockerResponse(new BufferedInputStream(openInputStream(fd)));
    }


    private DockerResponse request(int fd, String method, String path, List<Pair<String, ?>> headers, String body) throws IOException {
        final OutputStream output = new BufferedOutputStream(openOutputStream(fd));
        writeHttpHeaders(output, method, path, headers);
        if (body != null) {
            output.write(body.getBytes());
            output.flush();
        }
        return new DockerResponse(new BufferedInputStream(openInputStream(fd)));
    }


    private void writeHttpHeaders(OutputStream output, String method, String path, List<Pair<String, ?>> headers) throws IOException {
        final Writer writer = new OutputStreamWriter(output);
        writer.write(method);
        writer.write(' ');
        // TODO: encode path
        writer.write(path);
        writer.write(" HTTP/1.1\r\n");
        for (Pair<String, ?> header : headers) {
            writer.write(header.first);
            writer.write(": ");
            writer.write(String.valueOf(header.second));
            writer.write("\r\n");
        }
        writer.write("\r\n");
        writer.flush();
    }

    // Unfortunately we can't use generated DTO here.
    // Docker uses uppercase in first letter in names of json objects, e.g. {"Id":"123"} instead of {"id":"123"}
    private static JsonNameConvention FIRST_LETTER_LOWERCASE = new JsonNameConvention() {
        @Override
        public String toJsonName(String javaName) {
            return Character.toUpperCase(javaName.charAt(0)) + javaName.substring(1);
        }

        @Override
        public String toJavaName(String jsonName) {
            return Character.toLowerCase(jsonName.charAt(0)) + jsonName.substring(1);
        }
    };


    private int connect() throws IOException {
        return connect(DOCKER_SOCKET_PATH);
    }


    private int connect(final String path) throws IOException {
        checkCLibrary();
        int fd = C_LIBRARY.socket(AF_UNIX, SOCK_STREAM, 0);
        if (fd == -1) {
            throw new IOException(String.format("Unable connect to unix socket: '%s'", path));
        }
        SockAddrUn sockAddr = new SockAddrUn(path);
        int c = C_LIBRARY.connect(fd, sockAddr, sockAddr.size());
        if (c == -1) {
            throw new IOException(String.format("Unable connect to unix socket: '%s'", path));
        }
        return fd;
    }


    private void close(int fd) {
        C_LIBRARY.close(fd);
    }


    private InputStream openInputStream(final int fd) {
        // Don't need to close it.
        return new InputStream() {
            @Override
            public int read() throws IOException {
                final byte[] bytes = new byte[1];
                if (read(bytes) == 0) {
                    return -1;
                }
                return bytes[0];
            }

            @Override
            public int read(byte[] b) throws IOException {
                return read(b, 0, b.length);
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                if (b == null) {
                    throw new NullPointerException();
                }
                if (off < 0 || len < 0 || len > b.length - off) {
                    throw new IndexOutOfBoundsException();
                }
                if (len == 0) {
                    return 0;
                }
                int n;
                try {
                    n = C_LIBRARY.recv(fd, b, len, 0);
                } catch (LastErrorException e) {
                    throw new IOException("error: " + C_LIBRARY.strerror(e.getErrorCode()));
                }
                if (n == 0) {
                    return -1;
                }
                return n;
            }
        };
    }


    private OutputStream openOutputStream(final int fd) {
        // Don't need to close it.
        return new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                write(new byte[]{(byte)b}, 0, 1);
            }

            public void write(byte[] b) throws IOException {
                write(b, 0, b.length);
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                int n;
                try {
                    n = C_LIBRARY.send(fd, b, len, 0);
                } catch (LastErrorException e) {
                    throw new IOException("error: " + C_LIBRARY.strerror(e.getErrorCode()));
                }
                if (n != len) {
                    throw new IOException(String.format("Failed writing %d bytes", len));
                }
            }
        };
    }


    private void createTarGzArchive(java.io.File tar, java.io.File... files) throws IOException {
        final FileOutputStream fOut = new FileOutputStream(tar);
        final GZIPOutputStream gzipOut = new GZIPOutputStream(fOut);
        final TarOutputStream tarOut = new TarOutputStream(gzipOut);
        for (java.io.File file : files) {
            if (file.isFile()) {
                addFile(tarOut, file, "");
            }
        }
        tarOut.close();
    }


    private void addFile(TarOutputStream tarOut, java.io.File file, String base) throws IOException {
        final String entryName = base + file.getName();
        tarOut.putNextEntry(new TarEntry(file, entryName));
        if (file.isFile()) {
            Files.copy(file.toPath(), tarOut);
            tarOut.closeEntry();
        } else {
            java.io.File[] children = file.listFiles();
            if (children != null) {
                for (java.io.File child : children) {
                    addFile(tarOut, child, entryName + '/');
                }
            }
        }
    }

    private static class DockerResponse {
        private final InputStream rawData;

        private InputStream data;
        private String[]    headersFields;
        private int         status;

        DockerResponse(InputStream input) {
            rawData = input;
            status = -1;
        }

        int getStatus() throws IOException {
            if (status != -1) {
                return status;
            }
            getInputStream();
            String statusLine = headersFields[0];
            if (statusLine.startsWith("HTTP/1.")) {
                int startCode = statusLine.indexOf(' ');
                if (startCode > 0) {
                    int endCode = statusLine.indexOf(' ', startCode + 1);
                    if (endCode < 0) {
                        endCode = statusLine.length();
                    }

                    try {
                        return status = Integer.parseInt(statusLine.substring(startCode + 1, endCode));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            return -1;
        }

        int getContentLength() throws IOException {
            final String header = getHeader("Content-Length");
            if (header != null) {
                try {
                    return Integer.parseInt(header);
                } catch (NumberFormatException ignored) {
                }
            }
            return -1;
        }

        String getContentType() throws IOException {
            return getHeader("Content-Type");
        }

        String getHeader(String name) throws IOException {
            getInputStream();
            String lowerCaseName = name.toLowerCase();
            for (String field : headersFields) {
                if (field.toLowerCase().startsWith(lowerCaseName)) {
                    int colonPos = field.indexOf(':');
                    if (colonPos > 0) {
                        return field.substring(colonPos + 1).trim();
                    }
                }
            }
            return null;
        }

        String[] getHeaders(String name) throws IOException {
            getInputStream();
            String lowerCaseName = name.toLowerCase();
            List<String> headers = new ArrayList<>(4);
            for (String field : headersFields) {
                if (field.toLowerCase().startsWith(lowerCaseName)) {
                    int colonPos = field.indexOf(':');
                    if (colonPos > 0) {
                        headers.add(field.substring(colonPos + 1).trim());
                    }
                }
            }
            return headers.toArray(new String[headers.size()]);
        }

        synchronized InputStream getInputStream() throws IOException {
            if (this.headersFields != null) {
                // already parsed
                return data;
            }
            List<String> headerFields = new ArrayList<>(4);
            StringBuilder buf = new StringBuilder();
            for (; ; ) {
                int r = rawData.read();
                if (r == '\n') {
                    if (buf.length() == 0) {
                        break;
                    }
                    headerFields.add(buf.toString());
                    buf.setLength(0);
                } else if (r != '\r') {
                    buf.append((char)r);
                }
            }
            this.headersFields = headerFields.toArray(new String[headerFields.size()]);
            final int contentLength = getContentLength();
            if (contentLength == 0) {
                return data = EMPTY;
            }
            if (contentLength > 0) {
                return data = new LimitedInputStream(rawData, contentLength);
            }
            return data = "chunked".equals(getHeader("Transfer-Encoding")) ? new ChunkedInputStream(rawData) : rawData;
        }
    }

    private static final InputStream EMPTY = new InputStream() {
        @Override
        public int read() throws IOException {
            return -1;
        }
    };

    private static class LimitedInputStream extends InputStream {
        final InputStream input;
        final int         limit;

        int pos;

        LimitedInputStream(InputStream input, int limit) {
            this.input = input;
            this.limit = limit;
        }

        @Override
        public synchronized int read() throws IOException {
            final byte[] b = new byte[1];
            if (doRead(b, 0, 1) == -1) {
                return -1;
            }
            return b[0];
        }

        @Override
        public synchronized int read(byte[] b) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            }
            return doRead(b, 0, b.length);
        }

        @Override
        public synchronized int read(byte[] b, int off, int len) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            }
            if (off < 0 || len < 0 || len > b.length - off) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return 0;
            }

            return doRead(b, 0, len);
        }

        private int doRead(byte[] b, int off, int len) throws IOException {
            if (pos >= limit) {
                return -1;
            }
            int n = input.read(b, 0, Math.min(len - off, limit - pos));
            pos += n;
            return n;
        }
    }

    private static class ChunkedInputStream extends InputStream {
        final InputStream input;
        StringBuilder chunkSizeBuf;
        int           chunkSize;
        int           chunkPos;
        boolean       eof;

        ChunkedInputStream(InputStream input) {
            this.input = input;
            chunkSizeBuf = new StringBuilder();
        }

        @Override
        public synchronized int read() throws IOException {
            final byte[] b = new byte[1];
            if (doRead(b, 0, 1) == -1) {
                return -1;
            }
            return b[0];
        }

        @Override
        public synchronized int read(byte[] b) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            }
            return doRead(b, 0, b.length);
        }

        @Override
        public synchronized int read(byte[] b, int off, int len) throws IOException {
            if (b == null) {
                throw new NullPointerException();
            }
            if (off < 0 || len < 0 || len > b.length - off) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return 0;
            }

            return doRead(b, 0, len);
        }

        private int doRead(byte[] b, int off, int len) throws IOException {
            if (eof) {
                return -1;
            }
            if (chunkSize == 0) {
                chunkPos = 0;
                for (; ; ) {
                    int i = input.read();
                    if (i < 0) {
                        throw new IOException("Can't read size of chunk");
                    }
                    if (i == '\n') {
                        break;
                    }
                    chunkSizeBuf.append((char)i);
                }

                int l = chunkSizeBuf.length();
                int endSize = 0;
                while (endSize < l && Character.digit(chunkSizeBuf.charAt(endSize), 16) != -1) {
                    endSize++;
                }
                try {
                    chunkSize = Integer.parseInt(chunkSizeBuf.substring(0, endSize), 16);
                } catch (NumberFormatException e) {
                    throw new IOException("Invalid chunk size");
                }
                chunkSizeBuf.setLength(0);
                if (chunkSize == 0) {
                    eof = true;
                }
            }
            final int n = input.read(b, 0, Math.min(len - off, chunkSize - chunkPos));
            chunkPos += n;
            if (chunkPos == chunkSize) {
                if ('\r' != input.read()) { // skip '\r'
                    throw new IOException("CR character is missing");
                }
                if ('\n' != input.read()) { // skip '\n'
                    throw new IOException("LF character is missing");
                }
                chunkSize = 0;
                chunkPos = 0;
            }
            if (eof) {
                return -1;
            }
            return n;
        }
    }
}
