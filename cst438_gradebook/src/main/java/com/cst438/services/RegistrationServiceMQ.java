package com.cst438.services;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import com.cst438.domain.Course;
import com.cst438.domain.CourseDTOG;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;
import org.springframework.web.server.ResponseStatusException;


public class RegistrationServiceMQ extends RegistrationService {

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public RegistrationServiceMQ() {
		System.out.println("MQ registration service ");
	}

	// ----- configuration of message queues

	@Autowired
	Queue registrationQueue;


	// ----- end of configuration of message queue

	// receiver of messages from Registration service
	
	@RabbitListener(queues = "gradebook-queue")
	@Transactional
	public void receive(EnrollmentDTO enrollmentDTO) {
		Enrollment e = new Enrollment();
		e.setStudentEmail(enrollmentDTO.studentEmail);
		e.setStudentName(enrollmentDTO.studentName);
		Course c = courseRepository.findById(enrollmentDTO.course_id).orElse(null);
		if(c == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course not found");
		}
		e.setCourse(c);
		e = enrollmentRepository.save(e);
		enrollmentDTO.id = e.getId();
		
	}

	// sender of messages to Registration Service
	@Override
	public void sendFinalGrades(int course_id, CourseDTOG courseDTO) {
		System.out.println("Sending final grades " + course_id + " " + courseDTO);
		rabbitTemplate.convertAndSend(registrationQueue.getName(), courseDTO);
		System.out.println("After sending final grades");
	}

}
