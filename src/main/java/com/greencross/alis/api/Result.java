package com.greencross.alis.api;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;

@Component
public class Result {
	public Result(EntityManager em) {
		this.em = em;
	}
	private final EntityManager em;

	@Transactional("transactionManagerAlis")
	public void result(long patient, String code, AlisResult data, String member, String machine) {
		String labRegDate = String.valueOf(patient/10000000);
		int labRegNo = (int)(patient%10000000);
		StoredProcedureQuery query = em.createStoredProcedureQuery("Interface_SetPatientResult");
		query.registerStoredProcedureParameter(1, String.class, ParameterMode.IN)        // LabRegDate
			 .registerStoredProcedureParameter(2, Integer.class, ParameterMode.IN)       // LabRegNo
			 .registerStoredProcedureParameter(3, String.class, ParameterMode.IN)        // OrderCode
			 .registerStoredProcedureParameter(4, String.class, ParameterMode.IN)        // TestCode
			 .registerStoredProcedureParameter(5, String.class, ParameterMode.IN)        // TestSubCode
			 .registerStoredProcedureParameter(6, String.class, ParameterMode.IN)        // Result01
			 .registerStoredProcedureParameter(7, String.class, ParameterMode.IN)        // Result02
			 .registerStoredProcedureParameter(8, String.class, ParameterMode.IN)        // TestResultText
			 .registerStoredProcedureParameter(9, String.class, ParameterMode.IN)        // EditorMemberId
			 .registerStoredProcedureParameter(10, String.class, ParameterMode.IN)       // MachineCode
			 .setParameter(1, labRegDate)
			 .setParameter(2, labRegNo)
			 .setParameter(3, code)
			 .setParameter(4, code)
			 .setParameter(5, data.subCode())
			 .setParameter(6, data.result1())
			 .setParameter(7, data.result2())
			 .setParameter(8, data.text())
			 .setParameter(9, member)
			 .setParameter(10, machine);
		query.executeUpdate();
	}
}
