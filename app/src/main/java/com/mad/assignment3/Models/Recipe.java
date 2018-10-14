package com.mad.assignment3.Models;

import com.google.gson.annotations.SerializedName;

import java.net.URL;

public class Recipe {

    @SerializedName("image_url")
    private URL mImageURL;
    @SerializedName("source_url")
    private URL mSourceURL;
    @SerializedName("f2f_url")
    private URL mF2FURL;
    @SerializedName("title")
    private String mTitle;
    @SerializedName("publisher")
    private String mPublisher;
    @SerializedName("publisher_url")
    private URL mPublisherURL;
    @SerializedName("social_rank")
    private Double mSocialRank;

    public Recipe(){}

    /**
     * This constructor is using the fields contained from the Food2Fork API
     * Not all fields are used
     * @param imageURL this is the URL for a picture of the recipe
     * @param sourceURL this is the URL where the recipe is originally found
     * @param f2FURL this is the Food2Fork URL
     * @param title this is the name of the recipe
     * @param publisher this is the name of the recipe's publisher
     * @param publisherURL this is the URL of the publisher's homepage
     * @param socialRank this is the ranking given to the recipe
     * @param page this is used to gain access to even more recipes
     */
    public Recipe(URL imageURL, URL sourceURL, URL f2FURL, String title, String publisher, URL publisherURL, double socialRank, int page) {
        this.mImageURL = imageURL;
        this.mSourceURL = sourceURL;
        this.mF2FURL = f2FURL;
        this.mTitle = title;
        this.mPublisher = publisher;
        this.mPublisherURL = publisherURL;
        this.mSocialRank = socialRank;
    }

    public URL getImageURL() {
        return mImageURL;
    }

    public void setImageURL(URL imageURL) {
        mImageURL = imageURL;
    }

    public URL getSourceURL() {
        return mSourceURL;
    }

    public void setSourceURL(URL sourceURL) {
        mSourceURL = sourceURL;
    }

    public URL getF2FURL() {
        return mF2FURL;
    }

    public void setF2FURL(URL f2FURL) {
        mF2FURL = f2FURL;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getPublisher() {
        return mPublisher;
    }

    public void setPublisher(String publisher) {
        mPublisher = publisher;
    }

    public URL getPublisherURL() {
        return mPublisherURL;
    }

    public void setPublisherURL(URL publisherURL) {
        mPublisherURL = publisherURL;
    }

    public Double getSocialRank() {
        return mSocialRank;
    }

    public void setSocialRank(Double socialRank) {
        mSocialRank = socialRank;
    }

}
