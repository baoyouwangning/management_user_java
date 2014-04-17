package management_user.bean;

public class Course {
	private String courseName = new String();
	private float courseScore = 0;
	
	public Course() {
		this(null,0);
	}
	
	public Course(String aCourseName,float aCourseScore) {
		this.setCourseName(aCourseName);
		this.setCourseScore(aCourseScore);
	}
	
	public void setCourseName(String aCourseName) {
		this.courseName = aCourseName;
	}
	
	public String getCourseName() {
		return courseName;
	}
	
	public void setCourseScore(float aCourseScore) {
		this.courseScore = aCourseScore;
	}
	
	public float getCourseScore() {
		return courseScore;
	}
	
	
	

}
