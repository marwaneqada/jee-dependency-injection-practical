package com.marwaneqada.orm.config;

import com.marwaneqada.orm.domain.AppRole;
import com.marwaneqada.orm.domain.AppUser;
import com.marwaneqada.orm.domain.Appointment;
import com.marwaneqada.orm.domain.AppointmentStatus;
import com.marwaneqada.orm.domain.Consultation;
import com.marwaneqada.orm.domain.Doctor;
import com.marwaneqada.orm.domain.Patient;
import com.marwaneqada.orm.domain.Product;
import com.marwaneqada.orm.repository.AppRoleRepository;
import com.marwaneqada.orm.repository.AppUserRepository;
import com.marwaneqada.orm.repository.AppointmentRepository;
import com.marwaneqada.orm.repository.DoctorRepository;
import com.marwaneqada.orm.repository.PatientRepository;
import com.marwaneqada.orm.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Configuration
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true", matchIfMissing = true)
public class DemoDataConfiguration {

    @Bean
    CommandLineRunner demonstrateOrm(ProductRepository products,
                                     PatientRepository patients,
                                     DoctorRepository doctors,
                                     AppointmentRepository appointments,
                                     AppRoleRepository roles,
                                     AppUserRepository users) {
        return args -> {
            demonstrateProductManagement(products);
            demonstrateHospitalModel(patients, doctors, appointments);
            demonstrateUserRoles(roles, users);
        };
    }

    private void demonstrateProductManagement(ProductRepository products) {
        Product laptop = products.save(new Product("Laptop", new BigDecimal("12500.00"), 8));
        products.save(new Product("Printer", new BigDecimal("2300.00"), 5));
        products.save(new Product("Keyboard", new BigDecimal("320.00"), 25));

        System.out.println("\n--- Product management ---");
        products.findAll().forEach(System.out::println);
        products.findByNameContainingIgnoreCase("print").forEach(
                product -> System.out.println("Search result: " + product));

        laptop.setQuantity(10);
        products.save(laptop);
        products.findById(laptop.getId()).ifPresent(
                product -> System.out.println("Updated: " + product));

        Product temporary = products.save(new Product("Temporary", BigDecimal.ONE, 1));
        products.deleteById(temporary.getId());
    }

    private void demonstrateHospitalModel(PatientRepository patients,
                                          DoctorRepository doctors,
                                          AppointmentRepository appointments) {
        Patient patient = patients.save(
                new Patient("Salma Amrani", LocalDate.of(1994, 6, 12), true, 72));
        Doctor doctor = doctors.save(
                new Doctor("Dr. Yassine Idrissi", "yassine@hospital.test", "Cardiology"));

        Appointment appointment = new Appointment(
                LocalDateTime.now().plusDays(1), AppointmentStatus.PENDING, patient, doctor);
        appointment.attachConsultation(new Consultation(
                LocalDateTime.now(), "Initial examination completed."));
        appointments.save(appointment);

        System.out.println("\n--- Hospital system ---");
        appointments.findByPatientId(patient.getId()).forEach(saved ->
                System.out.printf("Appointment #%d: %s with %s (%s)%n",
                        saved.getId(), saved.getPatient().getFullName(),
                        saved.getDoctor().getFullName(), saved.getStatus()));
    }

    private void demonstrateUserRoles(AppRoleRepository roles, AppUserRepository users) {
        AppRole admin = roles.save(new AppRole("ADMIN", "Hospital administrator"));
        AppRole practitioner = roles.save(new AppRole("PRACTITIONER", "Medical practitioner"));

        AppUser user = new AppUser("hospital.admin", "change-me", "admin@hospital.test");
        user.addRole(admin);
        user.addRole(practitioner);
        users.save(user);

        System.out.println("\n--- Users and roles ---");
        users.findByUsername("hospital.admin").ifPresent(saved ->
                System.out.println(saved.getUsername() + " -> "
                        + saved.getRoles().stream().map(AppRole::getName).toList()));
    }
}
