package com.cloudcampus.backend.dto;

import java.time.LocalDateTime;

public class CandidateRegistrationResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime createdAt;

    public CandidateRegistrationResponse() {
    }

    public CandidateRegistrationResponse(
            Long id,
            String firstName,
            String lastName,
            String email,
            LocalDateTime createdAt) {

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
