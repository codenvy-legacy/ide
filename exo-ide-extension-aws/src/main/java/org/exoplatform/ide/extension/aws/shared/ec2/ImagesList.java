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
package org.exoplatform.ide.extension.aws.shared.ec2;

import java.util.List;

/**
 * Information about available AMIs
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ImagesList {
    /**
     * Get list of AMIs.
     *
     * @return list of AMIs
     */
    List<ImageInfo> getImages();

    /**
     * Set list of AMIs.
     *
     * @param images
     *         list of AMIs
     */
    void setImages(List<ImageInfo> images);

    /**
     * Get count of described AMIs.
     *
     * @return total count of AMIs
     */
    int getTotal();

    /**
     * Set count of described AMIs.
     *
     * @param total
     *         total count of described AMIs
     */
    void setTotal(int total);

    /**
     * Get true if there are more images available to show in result set if max items less then all images count.
     *
     * @return true if there are more images available to show if max items less then all images count otherwise false
     */
    boolean isHasMore();

    /**
     * Set true if there are more images available to show in result set if max items less then all images count.
     *
     * @param hasMore
     *         true if there are more images available to show if max items less then all images count otherwise false
     */
    void setHasMore(boolean hasMore);

    /**
     * Get value from which should skipped images while viewing result set
     *
     * @return skip count
     */
    int getNextSkip();

    /**
     * Set value from which should skipped images while viewing result set
     *
     * @param skip
     *         skip count
     */
    void setNextSkip(int skip);

    /**
     * Get items count which will be included in result set.
     *
     * @return items count which will be included in result set
     */
    int getMaxItems();

    /**
     * Set items count which will be included in result set.
     *
     * @param maxItems
     *         items count which will be included in result set, if maxItems -1 then include all available images
     */
    void setMaxItems(int maxItems);
}
