package com.marwaneqada.orm.repository;

import com.marwaneqada.orm.domain.AppRole;
import com.marwaneqada.orm.domain.AppUser;
import com.marwaneqada.orm.domain.Appointment;
import com.marwaneqada.orm.domain.AppointmentStatus;
import com.marwaneqada.orm.domain.Consultation;
import com.marwaneqada.orm.domain.Doctor;
import com.marwaneqada.orm.domain.Patient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "app.seed-data=false")
class HospitalRelationshipsTest {

    @Autowired private PatientRepository patients;
    @Autowired private DoctorRepository doctors;
    @Autowired private AppointmentRepository appointments;
    @Autowired private AppRoleRepository roles;
    @Autowired private AppUserRepository users;

    @Test
    void persistsAppointmentAndItsConsultation() {
        Patient patient = patients.save(new Patient(
                "Omar Alaoui", LocalDate.of(1988, 3, 20), true, 64));
        Doctor doctor = doctors.save(new Doctor(
                "Dr. Lina Zahra", "lina@hospital.test", "Neurology"));
        Appointment appointment = new Appointment(
                LocalDateTime.of(2026, 8, 3, 10, 30), AppointmentStatus.PENDING,
                patient, doctor);
        appointment.attachConsultation(new Consultation(
                LocalDateTime.of(2026, 8, 3, 11, 0), "Routine follow-up."));

        Appointment saved = appointments.saveAndFlush(appointment);

        assertThat(appointments.findByPatientId(patient.getId())).hasSize(1);
        assertThat(saved.getConsultation().getId()).isNotNull();
        assertThat(saved.getConsultation().getAppointment()).isEqualTo(saved);
        assertThat(saved.getDoctor().getId()).isEqualTo(doctor.getId());
    }

    @Test
    void persistsManyToManyUserRoles() {
        AppRole admin = roles.save(new AppRole("ADMIN", "Administrator"));
        AppRole doctor = roles.save(new AppRole("DOCTOR", "Doctor"));
        AppUser user = new AppUser("lina", "secret", "lina@hospital.test");
        user.addRole(admin);
        user.addRole(doctor);

        users.saveAndFlush(user);

        assertThat(users.findByUsername("lina")).get()
                .extracting(saved -> saved.getRoles().size())
                .isEqualTo(2);
        assertThat(roles.findByName("DOCTOR")).isPresent();
    }
}
