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

import com.amazonaws.services.ec2.model.Placement;
import com.amazonaws.services.ec2.model.Tag;

import org.exoplatform.ide.extension.aws.shared.ec2.InstanceInfo;
import org.exoplatform.ide.extension.aws.shared.ec2.InstanceState;

import java.util.*;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class InstanceInfoImpl implements InstanceInfo {
    private String              id;
    private String              publicDNSName;
    private String              imageId;
    private String              rootDeviceType;
    private InstanceState       state;
    private String              imageType;
    private String              availabilityZone;
    private String              keyName;
    private long                launchTime;
    private List<String>        securityGroupsNames;
    private Map<String, String> tags;

    public static class Builder {
        private String              id;
        private String              publicDNSName;
        private String              imageId;
        private String              rootDeviceType;
        private InstanceState       state;
        private String              imageType;
        private String              availabilityZone;
        private String              keyName;
        private long                launchTime;
        private List<String>        securityGroupsNames;
        private Map<String, String> tags;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder publicDNSName(String publicDNSName) {
            this.publicDNSName = publicDNSName;
            return this;
        }

        public Builder imageId(String imageId) {
            this.imageId = imageId;
            return this;
        }

        public Builder rootDeviceType(String rootDeviceType) {
            this.rootDeviceType = rootDeviceType;
            return this;
        }

        public Builder state(String state) {
            this.state = InstanceState.fromValue(state);
            return this;
        }

        public Builder imageType(String imageType) {
            this.imageType = imageType;
            return this;
        }

        public Builder availabilityZone(Placement placement) {
            if (placement == null) {
                this.availabilityZone = null;
                return this;
            }
            this.availabilityZone = placement.getAvailabilityZone();
            return this;
        }

        public Builder keyName(String keyName) {
            this.keyName = keyName;
            return this;
        }

        public Builder launchTime(Date launchTime) {
            if (launchTime == null) {
                this.launchTime = -1;
                return this;
            }
            this.launchTime = launchTime.getTime();
            return this;
        }

        public Builder securityGroupsNames(List<String> securityGroupsNames) {
            if (securityGroupsNames == null) {
                this.securityGroupsNames = null;
                return this;
            }
            this.securityGroupsNames = new ArrayList<String>(securityGroupsNames);
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

        public InstanceInfo build() {
            return new InstanceInfoImpl(this);
        }
    }

    private InstanceInfoImpl(Builder builder) {
        this.id = builder.id;
        this.publicDNSName = builder.publicDNSName;
        this.imageId = builder.imageId;
        this.rootDeviceType = builder.rootDeviceType;
        this.state = builder.state;
        this.imageType = builder.imageType;
        this.availabilityZone = builder.availabilityZone;
        this.keyName = builder.keyName;
        this.launchTime = builder.launchTime;
        this.securityGroupsNames = builder.securityGroupsNames;
        this.tags = builder.tags;
    }

    public InstanceInfoImpl() {
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getPublicDNSName() {
        return publicDNSName;
    }

    @Override
    public void setPublicDNSName(String publicDNSName) {
        this.publicDNSName = publicDNSName;
    }

    @Override
    public String getImageId() {
        return imageId;
    }

    @Override
    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    @Override
    public String getRootDeviceType() {
        return rootDeviceType;
    }

    @Override
    public void setRootDeviceType(String rootDeviceType) {
        this.rootDeviceType = rootDeviceType;
    }

    @Override
    public InstanceState getState() {
        return state;
    }

    @Override
    public void setState(InstanceState state) {
        this.state = state;
    }

    @Override
    public String getImageType() {
        return imageType;
    }

    @Override
    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    @Override
    public String getAvailabilityZone() {
        return availabilityZone;
    }

    @Override
    public void setAvailabilityZone(String availabilityZone) {
        this.availabilityZone = availabilityZone;
    }

    @Override
    public String getKeyName() {
        return keyName;
    }

    @Override
    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    @Override
    public long getLaunchTime() {
        return launchTime;
    }

    @Override
    public void setLaunchTime(long launchTime) {
        this.launchTime = launchTime;
    }

    @Override
    public List<String> getSetSecurityGroupsNames() {
        if (securityGroupsNames == null) {
            securityGroupsNames = new ArrayList<String>();
        }
        return securityGroupsNames;
    }

    @Override
    public void setSecurityGroupsNames(List<String> securityGroupsNames) {
        this.securityGroupsNames = securityGroupsNames;
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
        return "InstanceInfoImpl{" +
               "id='" + id + '\'' +
               ", publicDNSName='" + publicDNSName + '\'' +
               ", imageId='" + imageId + '\'' +
               ", rootDeviceType='" + rootDeviceType + '\'' +
               ", state=" + state +
               ", imageType='" + imageType + '\'' +
               ", availabilityZone='" + availabilityZone + '\'' +
               ", keyName='" + keyName + '\'' +
               ", launchTime=" + launchTime +
               ", securityGroupsNames=" + securityGroupsNames +
               ", tags=" + tags +
               '}';
    }
}
