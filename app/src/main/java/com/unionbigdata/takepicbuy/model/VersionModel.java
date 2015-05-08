package com.unionbigdata.takepicbuy.model;

public class VersionModel {

	private int versionCode;
	private String versionName;
	private String size;
	private String feature;
	private String downloadUrl;

	public int getCode() {
		return versionCode;
	}

	public void setCode(int code) {
		this.versionCode = code;
	}

	public String getName() {
		if (versionName != null) {
			return versionName;
		} else {
			return "";
		}
	}

	public void setName(String name) {
		this.versionName = name;
	}

	public String getDescri() {
		if (feature != null) {
			return feature;
		} else {
			return "";
		}
	}

	public void setDescri(String descri) {
		this.feature = descri;
	}

	public String getVer_url() {
		if (downloadUrl != null) {
			return downloadUrl;
		} else {
			return "";
		}
	}

	public void setVer_url(String ver_url) {
		this.downloadUrl = ver_url;
	}

	public String getSize() {
		return (size != null ? size : "");
	}

	public void setSize(String size) {
		this.size = size;
	}
}
