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
package org.exoplatform.ide.extension.aws.server.ec2;

import com.amazonaws.services.ec2.model.Tag;

import org.exoplatform.ide.extension.aws.shared.ec2.ImageInfo;
import org.exoplatform.ide.extension.aws.shared.ec2.ImageState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ImageInfoImpl implements ImageInfo {
    private String              amiId;
    private String              manifest;
    private ImageState          state;
    private String              ownerId;
    private String              ownerAlias;
    private Map<String, String> tags;

    public static class Builder {
        private String              amiId;
        private String              manifest;
        private ImageState          state;
        private String              ownerId;
        private String              ownerAlias;
        private Map<String, String> tags;

        public Builder amiId(String amiId) {
            this.amiId = amiId;
            return this;
        }

        public Builder manifest(String manifest) {
            this.manifest = manifest;
            return this;
        }

        public Builder state(String state) {
            this.state = ImageState.fromValue(state);
            return this;
        }

        public Builder ownerId(String ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        public Builder ownerAlias(String ownerAlias) {
            this.ownerAlias = ownerAlias;
            return this;
        }

        public Builder tags(List<Tag> tags) {
            if (tags == null) {
                this.tags = null;
                return this;
            }
            this.tags = new HashMap<String, String>(tags.size());
            for (Tag tag : tags) {
                this.tags.put(tag.getKey(), tag.getValue());
            }
            return this;
        }

        public ImageInfo build() {
            return new ImageInfoImpl(this);
        }
    }

    private ImageInfoImpl(Builder builder) {
        this.amiId = builder.amiId;
        this.manifest = builder.manifest;
        this.state = builder.state;
        this.ownerId = builder.ownerId;
        this.ownerAlias = builder.ownerAlias;
        this.tags = builder.tags;
    }

    public ImageInfoImpl() {
    }

    @Override
    public String getAmiId() {
        return amiId;
    }

    @Override
    public void setAmiId(String amiId) {
        this.amiId = amiId;
    }

    @Override
    public String getManifest() {
        return manifest;
    }

    @Override
    public void setManifest(String manifest) {
        this.manifest = manifest;
    }

    @Override
    public ImageState getState() {
        return state;
    }

    @Override
    public void setState(ImageState state) {
        this.state = state;
    }

    @Override
    public String getOwnerId() {
        return ownerId;
    }

    @Override
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    @Override
    public String getOwnerAlias() {
        return ownerAlias;
    }

    @Override
    public void setOwnerAlias(String ownerAlias) {
        this.ownerAlias = ownerAlias;
    }

    @Override
    public Map<String, String> getTags() {
        if (tags == null) {
            tags = new HashMap<String, String>();
        }
        return tags;
    }

    @Override
    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "ImageInfoImpl{" +
               "amiId='" + amiId + '\'' +
               ", manifest='" + manifest + '\'' +
               ", state=" + state +
               ", ownerId='" + ownerId + '\'' +
               ", ownerAlias='" + ownerAlias + '\'' +
               ", tags=" + tags +
               '}';
    }
}
