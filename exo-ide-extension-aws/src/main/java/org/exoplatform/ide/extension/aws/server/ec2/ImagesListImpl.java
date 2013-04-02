/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
