package com.greencross.rs.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greencross.alis.api.AlisResult;
import com.greencross.alis.api.Api;
import com.greencross.alis.api.Result;
import com.greencross.lims.api.RequestService;
import com.greencross.lims.dto.AnalysisDTO;
import com.greencross.lims.dto.BatchDTO;
import com.greencross.lims.dto.RequestDTO;
import com.greencross.lims.map.RS;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

/*
 * LIMS의 요청을 받아서 A-LIS로 검사 결과를 전송하고 결과지를 생성하여 양쪽 시스템에 저장한다.
 * LIMS로부터 전송 요청 시
 *  1. 파일로부터 JSON 객체 생성
 *  2. JSON 객체를 BatchDTO로 변환
 *  3. BatchDTO의 각 AnalysisDTO마다 다음을 수행
 *    3-1. AnalysisDTO로부터 결과지 DTO로 변환(필요시 AnalysisDTO를 사용하여 ReceptDTO 읽기)
 *    3-2. PDF 생성
 *    3-3. PDF를 LIMS에 전송, 저장
 *    3-4. PDF를 A-LIS에 전송, 저장
 *    3-5. AnalysisDTO를 사용하여 A-LIS에 검사 결과 저장
 */
@Component
@RequiredArgsConstructor
@Configuration
public class CreateReportAndSendToAlis {
	private final static Log Log = LogFactory.getLog(CreateReportAndSendToAlis.class);
	private final static ObjectMapper OM = new ObjectMapper();
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private String title;
	@Autowired
	private Result alis;
	@Autowired
	private Api alisApi;
	public Job build() {
		return jobBuilderFactory.get("CreateReportAndSendToAlis").start(readJson(null))
								.next(mapToBatchDTO())
								.next(processAnalysis())
								.build();
	}
	@Bean
	@JobScope
	protected Step readJson(@Value("#{jobParameters[json]}") String json) {	//1. 파일로부터 JSON 객체 생성
		return stepBuilderFactory.get("Read JSON").tasklet((contribution, chunkContext)->{
			chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("JSON", json);
			return RepeatStatus.FINISHED;
		}).build();
	}
	protected Step mapToBatchDTO() {	//2. JSON 객체를 BatchDTO로 변환..
		return stepBuilderFactory.get("Map to BatchDTO").tasklet((contribution, chunkContext)->{
			String json = (String) chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("JSON");
			BatchDTO batch = OM.readValue(json, BatchDTO.class);
			chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("Batch", batch);
			return RepeatStatus.FINISHED;
		}).build();
	}
	protected Step processAnalysis() {
		return stepBuilderFactory.get("For each analysisDTO").tasklet((contribution, chunkContext)->{
			BatchDTO batch = (BatchDTO) chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("Batch");
			batch.getAnalysis().stream().forEach(analysis->{

				try {
					/* PDF 작업 이후에!!!
					PDDocument doc = null;
					if("S036".equals(analysis.getCode()))
						//doc = createPdfN006(analysis, toDTON006(analysis));	//3-2. PDF 생성
					else if("S037".equals(analysis.getCode()))
						//doc = createPdfN019(analysis, toDTON019(analysis));	//3-2. PDF 생성
					ByteArrayOutputStream bao = new ByteArrayOutputStream();
					doc.save(bao);
					doc.close();
					bao.close();
					byte[] bytes = bao.toByteArray();
					sendToLims(bytes , analysis);		//3-3. PDF를 LIMS에 전송, 저장
					sendToAlis(bytes, analysis);		//3-4. PDF를 A-LIS에 전송, 저장		2030년 의뢰로 테스트 진행해봐야함!!!!!!!
				 */
					sendToAlis(analysis);	//3-5. AnalysisDTO를 사용하여 A-LIS에 검사 결과 저장	2030년 의뢰로 테스트 진행해봐야함!!!!!!
				} catch(Exception e) {
					Log.error(e.getMessage(), e);
				}
			});
			return RepeatStatus.FINISHED;
		}).build();
	}

	private void sendToAlis(AnalysisDTO analysis) throws IOException {
		Long barcode = analysis.getBarcode();
		if (barcode == null) return;
		RequestDTO[] requests = RequestService.getAlisRequest(barcode);
		if (requests == null) return;
		RequestDTO target = Arrays.stream(requests).filter(a -> a.getCode().equals(analysis.getCode())).findFirst().orElse(null);
		if (target == null) return;

		if ("S036".equals(analysis.getCode())) {	//Stroke - kr
			AlisResult[] results = RS.strokeKr(analysis);
			for (AlisResult result : results) alis.result(target.getId(), analysis.getCode(), result, "LIMS", "LIMS");
		} else if ("S037".equals(analysis.getCode())) {	//hyperlipidemia - kr
			AlisResult[] results = RS.hyperKr(analysis);
			for (AlisResult result : results) alis.result(target.getId(), analysis.getCode(), result, "LIMS", "LIMS");
		} else if("OS036".equals(analysis.getCode())) {		//Stroke - en
			AlisResult[] results = RS.strokeEn(analysis);
			for (AlisResult result : results) alis.result(target.getId(), analysis.getCode(), result, "LIMS", "LIMS");
		}  else if("OS037".equals(analysis.getCode())) {	//hyperlipidemia - en
			AlisResult[] results = RS.hyperEn(analysis);
			for (AlisResult result : results) alis.result(target.getId(), analysis.getCode(), result, "LIMS", "LIMS");
		}
	}
}
