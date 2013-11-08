package org.xserver.component.json.bean;

import com.fasterxml.jackson.annotation.JsonFilter;

/**
 * Front side use jQuery lib, this instance is template tab like the
 * jquery.ui.tabs. When front side need generate tab, use the javaBean easy.
 * 
 * @author postonzhang
 * 
 */
@JsonFilter("except-field-filter")
public class JQueryTab {
	private String id;
	private String title;
	private String content;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
