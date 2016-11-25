package com.tencent.apk_auto_test.data;

import android.util.Log;

public class TestCase {
	private String name;
	private String className;
	private double timeGene;
	private String runState;
	private String[] caseName;
	private String[] caseOrder;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = name;
	}

	public double getTimeGene() {
		return timeGene;
	}

	public void setTimeGene(String timeGene) {
		this.timeGene = Double.parseDouble(timeGene);
	}

	public String getRunState() {
		return runState;
	}

	public void setRunState(String runState) {
		this.runState = runState;
	}

	public String[] getCaseName() {
		return caseName;
	}

	public void setCaseName(String caseName) {
		if (null == caseName) {
			return;
		}
		this.caseName = caseName.split(",");
	}

	public String[] getCaseOrder() {
		return caseOrder;
	}

	public void setCaseOrder(String caseOrder) {
		if (null == caseOrder) {
			return;
		}
		this.caseOrder = caseOrder.split(",");
	}
}
