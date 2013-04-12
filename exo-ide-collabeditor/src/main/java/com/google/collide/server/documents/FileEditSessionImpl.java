// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.collide.server.documents;

import com.google.collide.dto.DocOp;
import com.google.collide.dto.DocumentSelection;
import com.google.collide.server.documents.VersionedDocument.DocumentOperationException;
import com.google.collide.server.documents.VersionedDocument.VersionedText;
import com.google.collide.server.shared.merge.ConflictChunk;
import com.google.collide.server.shared.merge.MergeChunk;
import com.google.collide.server.shared.merge.MergeResult;
import com.google.collide.shared.document.anchor.Anchor;
import com.google.collide.shared.document.anchor.Anchor.ShiftListener;
import com.google.collide.shared.document.anchor.AnchorManager;
import com.google.collide.shared.document.anchor.AnchorType;
import com.google.collide.shared.document.anchor.InsertionPlacementStrategy;
import com.google.collide.shared.ot.DocOpUtils;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.protobuf.ByteString;

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent;
import org.exoplatform.ide.vfs.server.observation.ChangeEventFilter;
import org.exoplatform.ide.vfs.server.observation.EventListener;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.ide.vfs.server.observation.PathFilter;
import org.exoplatform.ide.vfs.server.observation.TypeFilter;
import org.exoplatform.ide.vfs.server.observation.VfsIDFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.annotation.Nullable;
import javax.ws.rs.core.MediaType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Default implementation of {@link FileEditSession}.
 * <p/>
 * <p/>
 * This class is thread-safe.
 */
final class FileEditSessionImpl implements FileEditSession {
    private static final Log                         LOG            = ExoLogger.getLogger(FileEditSessionImpl.class);
    /** The list of conflict chunks for this file. */
    private final        List<AnchoredConflictChunk> conflictChunks = Lists.newArrayList();
    /** The ID of the resource this edit session is opened for. */
    private       String    resourceId;
    private       String    path;
    private final MediaType mediaType;
    private final Set<String> editSessionParticipants         = new CopyOnWriteArraySet<String>();
    /*
     * The size and sha1 fields don't actually need to stay in lock-step with the doc contents since
     * there's no public API for retrieving a snapshot of both values. Thus, we don't need blocking
     * synchronization. We do however need to ensure that updates made by one thread are seen by other
     * threads, so they must be declared volatile.
     */
    private final Set<String> editSessionParticipantsReadOnly = Collections.unmodifiableSet(editSessionParticipants);
    private final String            editSessionKey;
    private final VirtualFileSystem vfs;
    /** Document that contains the file contents. */
    private       VersionedDocument contents;
    /** Size of the file, in bytes. Lazily computed by {@link #getSize()}. */
    private Integer    size = null;
    /** SHA-1 hash of the file contents. Lazily computed by {@link #getSha1()}. */
    private ByteString sha1 = null;
    /** CC revision of the document that we last saved */
    private int lastSavedCcRevision;
    /** CC revision of the document after the last mutation was applied */
    private int lastMutationCcRevision;
    /** True if the file-edit session has been closed */
    private boolean closed = false;
    /** When this file edit session was closed. Makes sense only if closed = true. */
    private long              closedTimeMs;
    /** Time that this FileEditSession was created (millis since epoch) */
    //private final long createdAt = System.currentTimeMillis();

    private OnCloseListener   onCloseListener;

    FileEditSessionImpl(String editSessionKey,
                        VirtualFileSystem vfs,
                        EventListenerList listenerList,
                        String resourceId,
                        String path,
                        String mediaType,
                        String initialContents,
                        @Nullable MergeResult mergeResult) {
        this.editSessionKey = editSessionKey;
        this.vfs = vfs;
        this.resourceId = resourceId;
        this.mediaType = MediaType.valueOf(mediaType);
        this.contents = new VersionedDocument(initialContents);
        this.path = path;

        if (mergeResult != null) {
            // Construct conflict chunks.
            List<ConflictChunk> chunks = constructConflictChunks(mergeResult);
            this.contents = new VersionedDocument(mergeResult.getMergedText());
            if (chunks.size() == 0) {
                LOG.error(String.format(
                        "Non-null MergeResult passed to FileEditSession for file that should have merged cleanly: [%s]", this));
            }

            for (ConflictChunk chunk : chunks) {
                this.conflictChunks.add(new AnchoredConflictChunk(chunk, contents));
            }
        }

        final long createdAt = System.currentTimeMillis();
        this.lastSavedCcRevision = contents.getCcRevision();
        this.lastMutationCcRevision = 0;
        LOG.debug("FileEditSession {} was created at {}", this, createdAt);
    }

    /** Given a merge result from the originally conflicted state, construct conflict chunks for it. */
    private static List<ConflictChunk> constructConflictChunks(MergeResult mergeResult) {
        List<ConflictChunk> conflicts = Lists.newArrayList();
        for (MergeChunk mergeChunk : mergeResult.getMergeChunks()) {
            if (mergeChunk.hasConflict()) {
                conflicts.add(new ConflictChunk(mergeChunk));
            }
        }
        return conflicts;
    }

    private static boolean hasUnresolvedConflictChunks(List<? extends ConflictChunk> conflictChunks) {
        for (ConflictChunk conflict : conflictChunks) {
            if (!conflict.isResolved()) {
                return true;
            }
        }
        return false;
    }

    private void checkNotClosed() {
        if (closed) {
            throw new FileEditSessionClosedException(editSessionKey, closedTimeMs);
        }
    }

    @Override
    public void close() {
        // if already closed, do nothing and silently return
        if (closed) {
            return;
        }
        closedTimeMs = System.currentTimeMillis();

        // TODO: Maybe change the semantics of this method to block until
        // all outstanding calls to other methods guarded by checkNotClosed()
        // finish. IncrementableCountDownLatch would do the trick.

        if (hasChanges()) {
            LOG.warn(String.format("FileEditSession [%s] closed while dirty", this));
        }

        if (onCloseListener != null) {
            onCloseListener.onClosed();
        }
        closed = true;
    }

    @Override
    public synchronized void setOnCloseListener(OnCloseListener listener) {
        if (this.onCloseListener != null) {
            throw new IllegalStateException("One listener already registered.");
        }
        this.onCloseListener = listener;
    }

    @Override
    public VersionedDocument.ConsumeResult consume(List<DocOp> docOps,
                                                   String authorClientId,
                                                   int intendedCcRevision,
                                                   DocumentSelection selection) throws DocumentOperationException {

        checkNotClosed();

        boolean containsMutation = DocOpUtils.containsMutation(docOps);

        VersionedDocument.ConsumeResult result = contents.consume(docOps, authorClientId, intendedCcRevision, selection);

        if (containsMutation) {
            lastMutationCcRevision = contents.getCcRevision();

            // Reset the cached size and SHA-1. We'll wait until someone actually calls getSize() or
            // getSha1() to recompute them.
            size = null;
            sha1 = null;
        }
        return result;
    }

    private String getText() {
        return contents.asText().text;
    }

    @Override
    public String getContents() {
        checkNotClosed();
        return getText();
    }

    public int getCcRevision() {
        return lastMutationCcRevision;
    }

    @Override
    public int getSize() {
        checkNotClosed();

        if (size == null) {
            try {
                size = getText().getBytes("UTF-8").length;
            } catch (UnsupportedEncodingException e) {
                // UTF-8 is a charset required by Java spec, per javadoc of
                // java.nio.charset.Charset, so this can't happen.
                throw new RuntimeException("UTF-8 not supported in this JVM?!", e);
            }
        }
        return size;
    }

    @Override
    public ByteString getSha1() {
        checkNotClosed();

        if (sha1 == null) {
            sha1 = ByteString.copyFrom(Hashing.sha1().hashString(getText()).asBytes());
        }
        return sha1;
    }

    @Override
    public VersionedDocument getDocument() {
        checkNotClosed();
        return contents;
    }

    @Override
    public String getFileEditSessionKey() {
        // probably ok to call on a closed FileEditSession
        return editSessionKey;
    }

    @Override
    public boolean hasChanges() {
        return lastSavedCcRevision < lastMutationCcRevision;
    }

    @Override
    public void save() throws IOException {
        checkNotClosed();

        // Get a consistent snapshot of the raw text and conflict chunks
        VersionedTextAndConflictChunksImpl snapshot = getContentsAndConflictChunks();
        String text = snapshot.getVersionedText().text;
        List<AnchoredConflictChunk> conflictChunks = snapshot.getConflictChunks();

        if (hasUnresolvedConflictChunks(conflictChunks)) {
            // TODO: There are conflict chunks in this file that need resolving.
            saveConflictChunks(text, conflictChunks);
        } else {
            // Remove all conflict chunk anchors from the document
            for (AnchoredConflictChunk conflict : conflictChunks) {
                contents.removeAnchor(conflict.startLineAnchor);
                contents.removeAnchor(conflict.endLineAnchor);
            }
            conflictChunks.clear();
        }

        saveChanges(text);

        lastSavedCcRevision = snapshot.getVersionedText().ccRevision;
    }

    private void saveChanges(String text) throws IOException {
        LOG.debug("Saving file: {}", path);
        try {
            vfs.updateContent(resourceId, mediaType, new ByteArrayInputStream(text.getBytes()), null);
        } catch (VirtualFileSystemException e) {
            throw new IOException(e.getMessage(), e);
        }
    }

    private void saveConflictChunks(String text, List<AnchoredConflictChunk> conflictChunks) {
        // TODO: Write the conflict chunks to some out of band location.
    }

    @Override
    public List<ConflictChunk> getConflictChunks() {
        return Lists.newArrayList((Iterable<? extends ConflictChunk>)conflictChunks);
    }

    @Override
    public VersionedTextAndConflictChunksImpl getContentsAndConflictChunks() {
        checkNotClosed();

        return new VersionedTextAndConflictChunksImpl(contents.asText(), Lists.newArrayList(conflictChunks));
    }

    @Override
    public boolean resolveConflictChunk(int chunkIndex) throws IOException {
        checkNotClosed();

        AnchoredConflictChunk chunk = conflictChunks.get(chunkIndex);
        if (chunk.isResolved()) {
      /*
       * This chunk can't be resolved because it is already resolved. This can happen if another
       * collaborator resolved the chunk, but this client did not get the notification until they
       * sent their own resolve message.
       */
            return false;
        }
        chunk.markResolved(true);

    /*
     * Immediately save the file.
     *
     * TODO: how to store chunk resolution?
     */
        save();
        return true;
    }

    @Override
    public boolean hasUnresolvedConflictChunks() {
        return hasUnresolvedConflictChunks(getConflictChunks());
    }

    @Override
    public String toString() {
        return editSessionKey;
    }

    @Override
    public String getResourceId() {
        return resourceId;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public Set<String> getCollaborators() {
        return editSessionParticipantsReadOnly;
    }

    @Override
    public boolean addCollaborator(String clientId) {
        return editSessionParticipants.add(clientId);
    }

    @Override
    public boolean removeCollaborator(String clientId) {
        return editSessionParticipants.remove(clientId);
    }

    @Override
    public void setResourceId(String newResourceId) {
        resourceId = newResourceId;
    }

    @Override
    public void setPath(String newPath) {
        path = newPath;
    }

    /** Bundles together a snapshot of the text of this file with any conflict chunks. */
    private static class VersionedTextAndConflictChunksImpl implements VersionedTextAndConflictChunks {
        private final VersionedText               text;
        private final List<AnchoredConflictChunk> conflictChunks;

        VersionedTextAndConflictChunksImpl(VersionedText text, List<AnchoredConflictChunk> conflictChunks) {
            this.text = text;
            this.conflictChunks = conflictChunks;
        }

        @Override
        public VersionedText getVersionedText() {
            return text;
        }

        @Override
        public List<AnchoredConflictChunk> getConflictChunks() {
            return conflictChunks;
        }
    }

    private static class AnchoredConflictChunk extends ConflictChunk {

        private static final AnchorType CONFLICT_CHUNK_START_LINE = AnchorType.create(FileEditSessionImpl.class, "conflictChunkStart");
        private static final AnchorType CONFLICT_CHUNK_END_LINE   = AnchorType.create(FileEditSessionImpl.class, "conflictChunkEnd");
        public final Anchor startLineAnchor;
        public final Anchor endLineAnchor;

        public AnchoredConflictChunk(ConflictChunk chunk, VersionedDocument doc) {
            super(chunk, chunk.isResolved());

            // Add anchors at the conflict regions' boundaries, so their position/size
            // gets adjusted automatically as the user enters text in and around them.
            startLineAnchor = doc.addAnchor(
                    CONFLICT_CHUNK_START_LINE, chunk.getStartLine(), AnchorManager.IGNORE_COLUMN);
            startLineAnchor.setRemovalStrategy(Anchor.RemovalStrategy.SHIFT);
            startLineAnchor.getShiftListenerRegistrar().add(new ShiftListener() {
                @Override
                public void onAnchorShifted(Anchor anchor) {
                    setStartLine(anchor.getLineNumber());
                }
            });
            endLineAnchor =
                    doc.addAnchor(CONFLICT_CHUNK_END_LINE, chunk.getEndLine(), AnchorManager.IGNORE_COLUMN);
            endLineAnchor.setInsertionPlacementStrategy(InsertionPlacementStrategy.LATER);
            endLineAnchor.setRemovalStrategy(Anchor.RemovalStrategy.SHIFT);
            endLineAnchor.getShiftListenerRegistrar().add(new ShiftListener() {
                @Override
                public void onAnchorShifted(Anchor anchor) {
                    setEndLine(anchor.getLineNumber());
                }
            });
        }
    }
}
