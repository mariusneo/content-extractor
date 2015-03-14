package com.linkresearchtools.jobapplication.contract;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Article {
    private String url;

    private String title;

    private String content;

    private String author;

    private Date publishingDate;

    public List<SocialShareCount> socialShareCounts = new ArrayList<>();

    public Article() {
    }

    public Article(String url, String title, String content, String author, Date publishingDate) {
        this.url = url;
        this.title = title;
        this.content = content;
        this.author = author;
        this.publishingDate = publishingDate;

    }

    public void addSocialShareCounts(SocialShareCount... socialShareCounts) {
        for (SocialShareCount socialShareCount : socialShareCounts) {
            if (socialShareCount != null) {
                this.socialShareCounts.add(socialShareCount);
            }
        }
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getPublishingDate() {
        return publishingDate;
    }

    public void setPublishingDate(Date publishingDate) {
        this.publishingDate = publishingDate;
    }

    @Override
    public String toString() {
        return "Article{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", author='" + author + '\'' +
                ", publishingDate=" + publishingDate +
                ", socialShareCounts=" + socialShareCounts +
                '}';
    }
}
