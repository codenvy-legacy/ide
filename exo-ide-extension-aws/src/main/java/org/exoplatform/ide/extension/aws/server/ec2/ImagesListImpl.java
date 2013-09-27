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

import org.exoplatform.ide.extension.aws.shared.ec2.ImageInfo;
import org.exoplatform.ide.extension.aws.shared.ec2.ImagesList;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ImagesListImpl implements ImagesList {
    private List<ImageInfo> images;
    private int total = -1;
    private boolean hasMore;
    private int skip     = -1;
    private int maxItems = -1;

    @Override
    public List<ImageInfo> getImages() {
        if (images == null) {
            images = new ArrayList<ImageInfo>();
        }
        return images;
    }

    @Override
    public void setImages(List<ImageInfo> images) {
        this.images = images;
    }

    @Override
    public int getTotal() {
        return total;
    }

    @Override
    public void setTotal(int total) {
        this.total = total;
    }

    @Override
    public boolean isHasMore() {
        return hasMore;
    }

    @Override
    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    @Override
    public int getNextSkip() {
        return skip;
    }

    @Override
    public void setNextSkip(int skip) {
        this.skip = skip;
    }

    @Override
    public int getMaxItems() {
        return maxItems;
    }

    @Override
    public void setMaxItems(int maxItems) {
        this.maxItems = maxItems;
    }

    @Override
    public String toString() {
        return "ImagesListImpl{" +
               "images=" + images +
               ", total=" + total +
               ", hasMore=" + hasMore +
               ", skip=" + skip +
               ", maxItems=" + maxItems +
               '}';
    }
}
