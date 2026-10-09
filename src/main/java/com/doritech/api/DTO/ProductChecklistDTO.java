package com.doritech.api.DTO;

public class ProductChecklistDTO {

	private int id;
	private String question;
	private String answer;
	private String questionType;
	private String imagePath;
	private int vendorBranchId;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public int getVendorBranchId() {
		return vendorBranchId;
	}

	public void setVendorBranchId(int vendorBranchId) {
		this.vendorBranchId = vendorBranchId;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

}
