package com.greencross.alis.api;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ToString
public class Api {
	private final static String API_GET_PATIENT_TEST_WORKLIST_TEMPLATE = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
													   "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">" +
													   "  <soap12:Header>" +
													   "    <AuthenticationHeader xmlns=\"http://tempuri.org/\">" +
													   "      <UserName>{:username}</UserName>" +
													   "      <Password>{:password}</Password>" +
													   "    </AuthenticationHeader>" +
													   "  </soap12:Header>" +
													   "  <soap12:Body>" +
													   "    <GetPatientTestWorkList xmlns=\"http://tempuri.org/\">" +
													   "      <req>" +
													   "        <StatustIndex>{:status}</StatustIndex>" +
													   "        <WorkCode>{:workcode}</WorkCode>" +
													   "        <BeginDate>{:date-from}</BeginDate>" +
													   "        <EndDate>{:date-to}</EndDate>" +
													   "      </req>" +
													   "    </GetPatientTestWorkList>" +
													   "  </soap12:Body>" +
													   "</soap12:Envelope>";
	private final static String API_SET_UPLOAD_LAB_REGFILE_TEMPLATE = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
																	 "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">" +
																	   "<soap12:Header>" +
																	     "<AuthenticationHeader xmlns=\"http://tempuri.org/\">" +
																	       "<UserName>{:username}</UserName>" +
																	       "<Password>{:password}</Password>" +
																	      "</AuthenticationHeader>" +
																	   "</soap12:Header>" +
																	   "<soap12:Body>" +
																	     "<SetUploadLabRegFile xmlns=\"http://tempuri.org/\">" +
																	       "<req>" +
																             "<LabRegDate>{:date}</LabRegDate>" +
																             "<LabRegNo>{:reqno}</LabRegNo>" +
																             "<ReportCode>{:code}</ReportCode>" +
																             "<FileDisplayName>{:file-name}</FileDisplayName>" +
																             "<FileExt>{:file-ext}</FileExt>" +
																             "<FileSize>{:file-size}</FileSize>" +
																             "<FileKind>{:file-type}</FileKind>" +
																             "<FileBuffer>{:file-data}</FileBuffer>" +
																             "<FileCreateTime>{:file-date}</FileCreateTime>" +
																             "<FileDescription>{:file-desc}</FileDescription>" +
																           "</req>" +
																	     "</SetUploadLabRegFile>" +
																	   "</soap12:Body>" +
																	 "</soap12:Envelope>";
	private final static DateTimeFormatter DTF = new DateTimeFormatterBuilder().parseCaseInsensitive().appendPattern("yyyy-MM-dd").toFormatter();
	private final String url;
	private final String contentType;
	private final String username;
	private final String password;
	private final static ObjectMapper OM = new ObjectMapper().configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
															 .setLocale(Locale.KOREA)
															 .registerModule(new JavaTimeModule());
	public List<Request> findRequestAll(LocalDate from, LocalDate to) throws IOException {
		String dateFrom = DTF.format(from);
		String dateTo = DTF.format(to);
		String xml = API_GET_PATIENT_TEST_WORKLIST_TEMPLATE.replace("{:username}", username)
														   .replace("{:password}", password)
														   .replace("{:status}", "0")
														   .replace("{:workcode}", "")
														   .replace("{:date-from}", dateFrom)
														   .replace("{:date-to}", dateTo);
		Document response = Jsoup.connect(url).maxBodySize(0)
								 .method(Connection.Method.POST)
								 .header("content-type", contentType)
								 .requestBody(xml)
								 .parser(Parser.xmlParser())
								 .execute().parse();
		String values = response.select("GetPatientTestWorkListResult").text().replace("\"\"", "null");
		return Arrays.stream(OM.readValue(values, Request[].class)).filter(Request::isRoot).collect(Collectors.toList());
	}
	public Request findRequest() {
		return null;
	}
	public enum FileType {
		GENERAL, PDF, JPG, JPG_PER_PAGE, ETC
	}
	public boolean fileUpload(Request request, byte[] file, String fileName, FileType type, LocalDate create, String desc) throws IOException {
		// String fileName = file.getName();
		String ext = fileName.contains(".")?fileName.substring(fileName.lastIndexOf(".")+1):"";
		long size = /*file.length();*/file.length;
		String data = Base64.getEncoder().encodeToString(/*Files.toByteArray(file)*/file);
		String xml = API_SET_UPLOAD_LAB_REGFILE_TEMPLATE.replace("{:username}", username)
														.replace("{:password}", password)
														.replace("{:date}", DTF.format(request.getReqdte()))
														.replace("{:reqno}", String.valueOf(request.getReqno()))
														.replace("{:code}", request.getItemcd())
														.replace("{:file-name}", fileName)
														.replace("{:file-ext}", ext)
														.replace("{:file-size}", String.valueOf(size))
														.replace("{:file-type}", String.valueOf(type.ordinal()))
														.replace("{:file-date}", DTF.format(create))
														.replace("{:file-desc}", desc)
														.replace("{:file-data}", data);
		Document response = Jsoup.connect(url).maxBodySize(0)
								 .method(Connection.Method.POST)
								 .header("content-type", contentType)
								 .requestBody(xml)
								 .parser(Parser.xmlParser())
								 .execute().parse();
		String values = response.select("SetUploadLabRegFileResult").text().replace("\"\"", "null");
		return OM.readValue(values, Boolean.class);
	}
}