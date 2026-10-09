package com.doritech.api.Entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

@Entity
public class ProductChecklistEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String question; // save all question here
	private String answer; // save all answer here
	private String questionType;// save all question types;
	@Lob
	@Column(columnDefinition = "LONGTEXT")
	private String imagePath;// save all images path
	@ManyToOne
	@JoinColumn(name = "vendor_branch_id") // Foreign Key
	private VendorBranchMappedEntity vendorBranch;

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

	public VendorBranchMappedEntity getVendorBranch() {
		return vendorBranch;
	}

	public void setVendorBranch(VendorBranchMappedEntity vendorBranch) {
		this.vendorBranch = vendorBranch;
	}

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	public String getQuestionType() {
		return questionType;
	}

	public void setQuestionType(String questionType) {
		this.questionType = questionType;
	}

}
