package com.greencross.alis.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Request implements Serializable {
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd a h:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime reqdte;
	private long reqno;
	private String itemcd;
	private String itemnm;
	private String patnm;
	private String idno;
	private String sampnm1;
	private String cstnm;
	private String cstcd;
	private String hosno;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd a h:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime wrkdte;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd a h:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime dday;
	@JsonProperty("DUERING")
	private int during;
	private String remark;
	private String prgweek;
	private String canyn;
	@JsonProperty("CSTNM_LABS")
	private String cstnmLabs;
	@JsonProperty("CSTCD_LABS")
	private String cstcdLabs;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd a h:mm:ss", timezone = "Asia/Seoul")
	private LocalDateTime samdte;
	@JsonProperty("LABREGDATE")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd a h:mm:ss", timezone = "Asia/Seoul")
	private LocalDate labRegDate;
	@JsonProperty("LABREGNO")
	private int labRegNo;
	public boolean isCanceled() {
		return "Y".equalsIgnoreCase(canyn);
	}
	public boolean isRoot() { return itemcd!=null && itemcd.length() <= 4; }
}
