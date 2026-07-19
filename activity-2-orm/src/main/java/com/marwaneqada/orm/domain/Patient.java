package com.marwaneqada.orm.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fullName;
    private LocalDate birthDate;
    private boolean sick;
    private int score;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>();

    protected Patient() {
    }

    public Patient(String fullName, LocalDate birthDate, boolean sick, int score) {
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.sick = sick;
        this.score = score;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public boolean isSick() { return sick; }
    public void setSick(boolean sick) { this.sick = sick; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public List<Appointment> getAppointments() { return appointments; }
}
