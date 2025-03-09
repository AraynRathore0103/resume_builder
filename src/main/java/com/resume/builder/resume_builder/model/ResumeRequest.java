package com.resume.builder.resume_builder.model;

import java.util.List;

public class ResumeRequest {
    private String fullName;
    private String phone;
    private String email;
    private String linkedIn;
    private String summary;
    private List<String> technicalSkills;
    private List<String> softSkills;
    private String education;
    private List<WorkExperience> workExperience;
    private List<String> projects;
    private List<String> certifications;
    private List<String> awards;
    private List<String> languages;
    public ResumeRequest() {
        // No-arg constructor required for JSON deserialization
    }

    public static class WorkExperience {
        private String jobTitle;
        private List<String> responsibilities;

        public WorkExperience() {}  // No-arg constructor

        public String getJobTitle() { return jobTitle; }
        public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

        public List<String> getResponsibilities() { return responsibilities; }
        public void setResponsibilities(List<String> responsibilities) { this.responsibilities = responsibilities; }
    }


	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLinkedIn() {
		return linkedIn;
	}

	public void setLinkedIn(String linkedIn) {
		this.linkedIn = linkedIn;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public List<String> getTechnicalSkills() {
		return technicalSkills;
	}

	public void setTechnicalSkills(List<String> technicalSkills) {
		this.technicalSkills = technicalSkills;
	}

	public List<String> getSoftSkills() {
		return softSkills;
	}

	public void setSoftSkills(List<String> softSkills) {
		this.softSkills = softSkills;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}

	public List<WorkExperience> getWorkExperience() {
		return workExperience;
	}

	public void setWorkExperience(List<WorkExperience> workExperience) {
		this.workExperience = workExperience;
	}

	public List<String> getProjects() {
		return projects;
	}

	public void setProjects(List<String> projects) {
		this.projects = projects;
	}

	public List<String> getCertifications() {
		return certifications;
	}

	public void setCertifications(List<String> certifications) {
		this.certifications = certifications;
	}

	public List<String> getAwards() {
		return awards;
	}

	public void setAwards(List<String> awards) {
		this.awards = awards;
	}

	public List<String> getLanguages() {
		return languages;
	}

	public void setLanguages(List<String> languages) {
		this.languages = languages;
	}
    

    // Getters & Setters for all fields
}
