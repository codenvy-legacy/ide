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
package com.codenvy.ide.ext.aws.shared.ec2;

import com.codenvy.ide.dto.DTO;
import com.codenvy.ide.json.JsonArray;

/**
 * Information about available AMIs
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@DTO
public interface ImagesList {
    /**
     * Get list of AMIs.
     *
     * @return list of AMIs
     */
    JsonArray<ImageInfo> getImages();

    /**
     * Get count of described AMIs.
     *
     * @return total count of AMIs
     */
    int getTotal();

    /**
     * Get true if there are more images available to show in result set if max items less then all images count.
     *
     * @return true if there are more images available to show if max items less then all images count otherwise false
     */
    boolean isHasMore();

    /**
     * Get value from which should skipped images while viewing result set
     *
     * @return skip count
     */
    int getNextSkip();

    /**
     * Get items count which will be included in result set.
     *
     * @return items count which will be included in result set
     */
    int getMaxItems();
}
