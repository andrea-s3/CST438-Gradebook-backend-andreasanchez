package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.EnrollmentRepository;

@SpringBootTest
public class EndToEndTestAddAssignment {

	public static final String CHROME_DRIVER_FILE_LOCATION = "C:/chromedriver_win32/chromedriver.exe";
	public static final String URL = "http://localhost:3000/addAssignment";
	public static final String TEST_USER_EMAIL = "test@csumb.edu";
	public static final String TEST_INSTRUCTOR_EMAIL = "dwisneski@csumb.edu";
	public static final int SLEEP_DURATION = 1000; // 1 second.
	public static final String TEST_ASSIGNMENT_NAME = "Test Assignment";
	public static final String TEST_COURSE_TITLE = "Test Course";
	public static final String TEST_STUDENT_NAME = "Test";
	public static final String TEST_DUE_DATE = "04-26-2023";
	public static final int TEST_COURSE_ID = 131313;
	
	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	AssignmentGradeRepository assignnmentGradeRepository;

	@Autowired
	AssignmentRepository assignmentRepository;

	@Test
	public void addNewAssignment() throws Exception {

//		Database setup:  create course		
		Course course = new Course();
		course.setCourse_id(131313);
		course.setInstructor(TEST_INSTRUCTOR_EMAIL);
		course.setSemester("Spring");
		course.setYear(2023);
		course.setTitle(TEST_COURSE_TITLE);

		courseRepository.save(course);

		System.setProperty("webdriver.chrome.driver",
                CHROME_DRIVER_FILE_LOCATION);
		//TODO update the class ChromeDriver()  for your browser
		WebDriver driver = new ChromeDriver();

		try {
			WebElement webEl;

			driver.get(URL);
			// must have a short wait to allow time for the page to download 
			Thread.sleep(SLEEP_DURATION);

			//enter assignment name
			webEl = driver.findElement(By.name("name"));
			webEl.sendKeys(TEST_ASSIGNMENT_NAME);

			// due date entry
			webEl = driver.findElement(By.name("dueDate"));
			webEl.sendKeys(TEST_DUE_DATE);

			//course ID entry
			webEl = driver.findElement(By.name("courseID"));
			webEl.sendKeys(String.valueOf(TEST_COURSE_ID));
			// submit button
			webEl = driver.findElement(By.id("submit"));
			webEl.click();
			Thread.sleep(SLEEP_DURATION);

			// verify the correct message
			webEl = driver.findElement(By.id("statusMsg"));
			String message = webEl.getText();
			assertEquals("Assignment added successfully", message);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;

		} finally {
			driver.quit();
		}
	}

	@Test
	public void addNewAssignmentFail() throws Exception {

//		Course Creation		
		Course course = new Course();
		course.setCourse_id(131313);
		course.setInstructor(TEST_INSTRUCTOR_EMAIL);
		course.setSemester("Spring");
		course.setYear(2023);
		course.setTitle(TEST_COURSE_TITLE);
		courseRepository.save(course);

		System.setProperty("webdriver.chrome.driver",
                CHROME_DRIVER_FILE_LOCATION);
		WebDriver driver = new ChromeDriver();

		try {
			WebElement webEl;
			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);
			webEl = driver.findElement(By.name("name"));
			webEl.sendKeys(TEST_ASSIGNMENT_NAME);
			webEl = driver.findElement(By.name("dueDate"));
			webEl.sendKeys(TEST_DUE_DATE);
			webEl = driver.findElement(By.name("courseID"));
			webEl.sendKeys(String.valueOf(1));
			webEl = driver.findElement(By.id("submit"));
			webEl.click();
			Thread.sleep(SLEEP_DURATION);
			webEl = driver.findElement(By.id("statusMsg"));
			String message = webEl.getText();
			assertEquals("Assignment was unable to be added", message);

		} catch (Exception ex) {
			ex.printStackTrace();
			throw ex;

		} finally {
			driver.quit();
		}

	}
} 
