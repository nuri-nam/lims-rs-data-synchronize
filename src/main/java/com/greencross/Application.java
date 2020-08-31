package com.greencross;

import com.greencross.rs.job.CreateReportAndSendToAlis;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.nio.file.Files;
import java.util.stream.Collectors;

@SpringBootApplication
public class Application implements ApplicationRunner {
	private final JobLauncher jobLauncher;
	private final CreateReportAndSendToAlis jb;
	public Application(JobLauncher jobLauncher, CreateReportAndSendToAlis jb) {
		this.jobLauncher = jobLauncher;
		this.jb = jb;
	}
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
	@Override
	public void run(ApplicationArguments args) throws Exception {
		File jf = new File(args.getSourceArgs()[0]);
		String json = Files.lines(jf.toPath()).collect(Collectors.joining());
		System.out.println("JSON Input:" + json);
		JobParameters param = new JobParametersBuilder().addString("JobID", String.valueOf(System.currentTimeMillis()))
														.addString("json", json).toJobParameters();
		jobLauncher.run(jb.build(), param);
	}
}
