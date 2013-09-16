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
package org.exoplatform.ide.extension.gadget.server.opensocial.model;

import java.util.List;

/**
 * Albums support collections of media items (video, image, sound).
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Nov 19, 2010 $
 */
public class Album {
    /** Description of the album. */
    private String description;

    /** Unique identifier for the album. */
    private String id;

    /** Location corresponding to the album. */
    private Address location;

    /** Number of items in the album. */
    private Integer mediaItemCount;

    /** Array of strings identifying the mime-types of media items in the Album. */
    private List<String> mediaMimeType;

    /** Array of MediaItem types, types are one of: audio, image, video. */
    private List<String> mediaType;

    /** ID of the owner of the album. */
    private String ownerId;

    /** URL to a thumbnail cover of the album. */
    private String thumbnailUrl;

    /** The title of the album. */
    private String title;

    /** @return the description */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *         the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return the id */
    public String getId() {
        return id;
    }

    /**
     * @param id
     *         the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /** @return the location */
    public Address getLocation() {
        return location;
    }

    /**
     * @param location
     *         the location to set
     */
    public void setLocation(Address location) {
        this.location = location;
    }

    /** @return the mediaItemCount */
    public Integer getMediaItemCount() {
        return mediaItemCount;
    }

    /**
     * @param mediaItemCount
     *         the mediaItemCount to set
     */
    public void setMediaItemCount(Integer mediaItemCount) {
        this.mediaItemCount = mediaItemCount;
    }

    /** @return the mediaMimeType */
    public List<String> getMediaMimeType() {
        return mediaMimeType;
    }

    /**
     * @param mediaMimeType
     *         the mediaMimeType to set
     */
    public void setMediaMimeType(List<String> mediaMimeType) {
        this.mediaMimeType = mediaMimeType;
    }

    /** @return the mediaType */
    public List<String> getMediaType() {
        return mediaType;
    }

    /**
     * @param mediaType
     *         the mediaType to set
     */
    public void setMediaType(List<String> mediaType) {
        this.mediaType = mediaType;
    }

    /** @return the ownerId */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * @param ownerId
     *         the ownerId to set
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    /** @return the thumbnailUrl */
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    /**
     * @param thumbnailUrl
     *         the thumbnailUrl to set
     */
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    /** @return the title */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *         the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }
}
