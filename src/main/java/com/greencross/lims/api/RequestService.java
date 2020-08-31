package com.greencross.lims.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greencross.lims.dto.RequestDTO;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class RequestService {
	public static RequestDTO[] getRequest(long id) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet("http://172.19.210.215/api2/request/" + id);
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
			return om.readValue(sb.toString(), RequestDTO[].class);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			client.close();
		}
	}


	public static RequestDTO[] getAlisRequest(long barcode) throws IOException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet("http://172.19.210.215/api2/request2/barcode/" + barcode);
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
			return om.readValue(sb.toString(), RequestDTO[].class);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			client.close();
		}
	}
}
