package com.marwaneqada.orm.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "consultations")
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime performedAt;
    private String report;

    @OneToOne(optional = false)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    protected Consultation() {
    }

    public Consultation(LocalDateTime performedAt, String report) {
        this.performedAt = performedAt;
        this.report = report;
    }

    public Long getId() { return id; }
    public LocalDateTime getPerformedAt() { return performedAt; }
    public void setPerformedAt(LocalDateTime performedAt) { this.performedAt = performedAt; }
    public String getReport() { return report; }
    public void setReport(String report) { this.report = report; }
    public Appointment getAppointment() { return appointment; }
    void setAppointment(Appointment appointment) { this.appointment = appointment; }
}
