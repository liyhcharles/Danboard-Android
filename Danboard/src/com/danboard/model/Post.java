package com.danboard.model;

public class Post {
	//Post class - contains information about danbooru post
	
	private int id;
	private String previewUrl;
	private String fileUrl;
	private int fileSize;

	/**
	 * Empty constructor
	 */
	public Post() {
	}

	/**
	 * Constructor for Post
	 * @param id
	 * @param previewUrl
	 * @param fileUrl
	 * @param fileSize
	 */
	public Post(int id, String previewUrl, String fileUrl,
			int fileSize) {
		this.id = id;
		this.previewUrl = previewUrl;
		this.fileUrl = fileUrl;
		this.fileSize = fileSize;
	}

	/**
	 * @return the post ID
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * @return the previewUrl
	 */
	public String getPreviewUrl() {
		return this.previewUrl;
	}

	/**
	 * @param previewUrl
	 */
	public void setPreviewUrl(String previewUrl) {
		this.previewUrl = previewUrl;
	}

	/**
	 * @return the fileUrl
	 */
	public String getFileUrl() {
		return this.fileUrl;
	}

	/**
	 * @param fileUrl
	 */
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public int getFileSize() {
		return this.fileSize;
	}

	public void setFileSize(int fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * @return The image name as a String
	 */
	public String getImageName() {
		return this.getFileUrl().substring(this.getFileUrl().lastIndexOf('/'));
	}

	/**
	 * If both objects are posts and they have the same id, they are equal
	 */
	@Override
	public boolean equals(Object o) {
		Post p = (Post) o;
		if (p != null && p.getId() == this.getId()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * The unique HashCode for a post is its id
	 */
	@Override
	public int hashCode() {
		return this.getId();
	}
}
