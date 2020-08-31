package com.greencross.lims.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greencross.lims.dto.AnalysisDTO;
import com.greencross.lims.dto.BatchDTO;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.UUID;

public class AnalysisService {
	private static final org.apache.commons.logging.Log Log = LogFactory.getLog(AnalysisService.class);
	private final static ObjectMapper OM = new ObjectMapper();

	public static AnalysisDTO[] getBatchAnalysis(UUID id, int idx) throws UnsupportedCharsetException, ClientProtocolException, IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet("http://172.19.210.215/api2/batch/" + id + "/" + idx + "/analysis?page=0&size=99999&order=row&is_asc=true");
		HttpResponse response = client.execute(get);
		BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while(true) {
			line = br.readLine();
			if(line == null || line.isEmpty()) break;
			sb.append(line.trim());
		}
		AnalysisDTO[] value = OM.readValue(sb.toString(), AnalysisDTO[].class);
		client.close();
		return value;
	}

	public static AnalysisDTO[] getAnalysis(long id, String code) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet("http://172.19.210.215/api2/request/" + id + "/" + code + "/analysis");
		get.setHeader("Content-Type","application/json;charset=UTF-8");
		HttpResponse response = client.execute(get);
		BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while(true) {
			line = br.readLine();
			if(line == null || line.isEmpty()) break;
			sb.append(line.trim());
		}
		try {
			ObjectMapper om = new ObjectMapper();
			return om.readValue(sb.toString(), AnalysisDTO[].class);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			client.close();
		}
	}

	public static String upload(AnalysisDTO value, UUID colId, byte[] bytes, String fileName) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost("http://172.19.210.215/api2/batch/" + value.getTemplate() + "/" + value.getBatch() + "/analysis/" + value.getRow() + "/" + colId);
		HttpEntity entity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
				.addBinaryBody("file", bytes, ContentType.DEFAULT_BINARY, fileName)
				.build();
		post.setEntity(entity);
		post.setHeader(entity.getContentType());
		HttpResponse response = client.execute(post);
		BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while(true) {
			line = br.readLine();
			if(line == null || line.isEmpty()) break;
			sb.append(line.trim());
		}
		String result = sb.toString();
		client.close();
		return result;
	}

	public static AnalysisDTO setAnalysis(AnalysisDTO value) {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPut put = new HttpPut("http://172.19.210.215/api2/batch/" + value.getTemplate() + "/" + value.getBatch() + "/analysis/" + value.getRow());
		put.setHeader("Content-Type","application/json;charset=UTF-8");
		try {
			put.setEntity(new StringEntity(OM.writeValueAsString(value), "UTF-8"));
			HttpResponse response = client.execute(put);
			BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), StandardCharsets.UTF_8));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while (true) {
				line = br.readLine();
				if (line == null || line.isEmpty()) break;
				sb.append(line.trim());
			}
			value = OM.readValue(sb.toString(), AnalysisDTO.class);
			client.close();
			return value;
		} catch(Exception e) {
			Log.error(e.getMessage(), e);
			return null;
		}
	}

	public static BatchDTO[] getBatch(UUID id) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet("http://172.19.210.215/api2/batch/" + id + "?page=0&size=20&order=idx&is_asc=false&state=CREATE");
		get.setHeader("Content-Type","application/json;charset=UTF-8");
		HttpResponse response = client.execute(get);
		BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line = null;
		StringBuilder sb = new StringBuilder();
		while(true) {
			line = br.readLine();
			if(line == null || line.isEmpty()) break;
			sb.append(line.trim());
		}
		try {
			return OM.readValue(sb.toString(), BatchDTO[].class);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			client.close();
		}
	}
/*
	public static AnalysisDTO setAlisPDF() {		//A-LIS에 PDF 전송/저장
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder parser = factory.newDocumentBuilder();

			String sendMessage = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
					"<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">\n" +
					"  <soap12:Header>\n" +
					"    <AuthenticationHeader xmlns=\"http://tempuri.org/\">\n" +
					"      <UserName>string</UserName>\n" +
					"      <Password>string</Password>\n" +
					"    </AuthenticationHeader>\n" +
					"  </soap12:Header>\n" +
					"  <soap12:Body>\n" +
					"    <SetUploadLabRegFile xmlns=\"http://tempuri.org/\">\n" +
					"      <req>\n" +
					"        <LabRegDate>dateTime</LabRegDate>\n" +
					"        <LabRegNo>int</LabRegNo>\n" +
					"        <ReportCode>string</ReportCode>\n" +
					"        <FileDisplayName>string</FileDisplayName>\n" +
					"        <FileExt>string</FileExt>\n" +
					"        <FileSize>long</FileSize>\n" +
					"        <FileKind>int</FileKind>\n" +
					"        <FileBuffer>base64Binary</FileBuffer>\n" +
					"        <FileCreateTime>dateTime</FileCreateTime>\n" +
					"        <FileDescription>string</FileDescription>\n" +
					"      </req>\n" +
					"      <errorMsg>string</errorMsg>\n" +
					"    </SetUploadLabRegFile>\n" +
					"  </soap12:Body>\n" +
					"</soap12:Envelope>";

			StringReader reader = new StringReader(sendMessage);
			InputSource is = new InputSource(reader);
			Document document = parser.parse(is);
			DOMSource requestSource = new DOMSource(document);

			MessageFactory messageFactory = MessageFactory.newInstance();
			SOAPMessage requestSoapMessage = messageFactory.createMessage();
			SOAPPart requestSoapPart = requestSoapMessage.getSOAPPart();
		} catch (Exception e) {

		}

	}*/
}
