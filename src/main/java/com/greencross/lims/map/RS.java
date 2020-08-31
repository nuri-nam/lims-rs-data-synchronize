package com.greencross.lims.map;

import com.greencross.alis.api.AlisResult;
import com.greencross.lims.dto.AnalysisDTO;
import com.greencross.rs.job.ID;

public class RS {
	public static AlisResult[] strokeKr(AnalysisDTO dto) {
		String apoe = dto.getValue().get(ID.RESULT1);
		String mthfr677 = dto.getValue().get(ID.RESULT2);
		String mthfr1298a = dto.getValue().get(ID.RESULT3);
		String rnf213 = dto.getValue().get(ID.RESULT4);
		String notch3		= dto.getValue().get(ID.RESULT5);

		return new AlisResult[]{
			AlisResult.builder().subCode("S036010").result1(apoe).build(),
			AlisResult.builder().subCode("S036020").result1(mthfr677).build(),
			AlisResult.builder().subCode("S036030").result1(mthfr1298a).build(),
			AlisResult.builder().subCode("S036040").result1(rnf213).build(),
			AlisResult.builder().subCode("S036050").result1(notch3).build()
		};
	}

	public static AlisResult[] strokeEn(AnalysisDTO dto) {
		String apoe = dto.getValue().get(ID.RESULT1);
		String mthfr677 = dto.getValue().get(ID.RESULT2);
		String mthfr1298a = dto.getValue().get(ID.RESULT3);
		String rnf213 = dto.getValue().get(ID.RESULT4);
		String notch3		= dto.getValue().get(ID.RESULT5);

		return new AlisResult[]{
				AlisResult.builder().subCode("OS036010").result1(apoe).build(),
				AlisResult.builder().subCode("OS036020").result1(mthfr677).build(),
				AlisResult.builder().subCode("OS036030").result1(mthfr1298a).build(),
				AlisResult.builder().subCode("OS036040").result1(rnf213).build(),
				AlisResult.builder().subCode("OS036050").result1(notch3).build()
		};
	}

	public static AlisResult[] hyperKr(AnalysisDTO dto) {
		String apoe = dto.getValue().get(ID.RESULT1);
		String apoa5_553 = dto.getValue().get(ID.RESULT2);
		String apoa5_56 = dto.getValue().get(ID.RESULT3);
		String coq2 = dto.getValue().get(ID.RESULT4);

		return new AlisResult[]{
			AlisResult.builder().subCode("S037010").result1(apoe).build(),
			AlisResult.builder().subCode("S037020").result1(apoa5_553).build(),
			AlisResult.builder().subCode("S037030").result1(apoa5_56).build(),
			AlisResult.builder().subCode("S037040").result1(coq2).build()
		};
	}

	public static AlisResult[] hyperEn(AnalysisDTO dto) {
		String apoe = dto.getValue().get(ID.RESULT1);
		String apoa5_553 = dto.getValue().get(ID.RESULT2);
		String apoa5_56 = dto.getValue().get(ID.RESULT3);
		String coq2 = dto.getValue().get(ID.RESULT4);

		return new AlisResult[]{
				AlisResult.builder().subCode("OS037010").result1(apoe).build(),
				AlisResult.builder().subCode("OS037020").result1(apoa5_553).build(),
				AlisResult.builder().subCode("OS037030").result1(apoa5_56).build(),
				AlisResult.builder().subCode("OS037040").result1(coq2).build()
		};
	}
}
