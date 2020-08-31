package com.greencross.alis.api;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Builder
@Accessors(fluent = true)
public class AlisResult {
	private String subCode;
	private String result1;
	private String result2;
	private String text;
	public String result1() {
		if(result1 == null) return "";
		else return result1;
	}
	public String result2() {
		if(result2 == null) return "";
		else return result2;
	}
	public String text() {
		if(text == null) return "";
		else return text;
	}
}
