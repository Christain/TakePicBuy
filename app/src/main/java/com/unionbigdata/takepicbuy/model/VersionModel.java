package com.unionbigdata.takepicbuy.model;

public class VersionModel {

	private int code;
	private String version;
	private String size;
	private String feature;
	private String ver_url;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getName() {
		if (version != null) {
			return version;
		} else {
			return "";
		}
	}

	public void setName(String name) {
		this.version = name;
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
		if (ver_url != null) {
			return ver_url;
		} else {
			return "";
		}
	}

	public void setVer_url(String ver_url) {
		this.ver_url = ver_url;
	}

	public String getSize() {
		return (size != null ? size : "");
	}

	public void setSize(String size) {
		this.size = size;
	}
}
