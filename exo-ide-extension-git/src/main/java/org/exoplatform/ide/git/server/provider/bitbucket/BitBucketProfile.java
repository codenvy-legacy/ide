package org.exoplatform.ide.git.server.provider.bitbucket;

import java.util.ArrayList;
import java.util.List;

/**
 * POJO of BitBucket Profile information.
 */
public class BitBucketProfile {
    private List<BitBucketRepository> repositories = new ArrayList<BitBucketRepository>();
    private BitBucketUser bitBucketUser;

    public List<BitBucketRepository> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<BitBucketRepository> repositories) {
        this.repositories = repositories;
    }

    public BitBucketUser getUser() {
        return bitBucketUser;
    }

    public void setUser(BitBucketUser bitBucketUser) {
        this.bitBucketUser = bitBucketUser;
    }
}
