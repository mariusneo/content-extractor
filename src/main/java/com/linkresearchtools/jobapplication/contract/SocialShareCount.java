package com.linkresearchtools.jobapplication.contract;

public class SocialShareCount {
    private SocialSite socialSite;

    private int count;

    public SocialShareCount() {
    }

    public SocialShareCount(SocialSite socialSite, int count) {
        this.socialSite = socialSite;
        this.count = count;
    }

    public SocialSite getSocialSite() {
        return socialSite;
    }

    public void setSocialSite(SocialSite socialSite) {
        this.socialSite = socialSite;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "SocialShareCount{" +
                "socialSite=" + socialSite +
                ", count=" + count +
                '}';
    }
}
