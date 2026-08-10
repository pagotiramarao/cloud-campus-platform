package com.cloudcampus.backend.service;

import com.cloudcampus.backend.dto.CandidateRegistrationRequest;
import com.cloudcampus.backend.entity.Candidate;
import com.cloudcampus.backend.repository.CandidateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CandidateService candidateService;

    @Test
    void shouldRegisterCandidateSuccessfully() {

        CandidateRegistrationRequest request =
                new CandidateRegistrationRequest();

        request.setFirstName("Pandu");
        request.setLastName("Test");
        request.setEmail("unit.test@example.com");
        request.setPassword("Password123");

        when(candidateRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.getPassword()))
                .thenReturn("$2a$10$hashedPassword");

        Candidate savedCandidate = new Candidate();
        savedCandidate.setId(1L);
        savedCandidate.setFirstName("Pandu");
        savedCandidate.setLastName("Test");
        savedCandidate.setEmail("unit.test@example.com");
        savedCandidate.setPasswordHash("$2a$10$hashedPassword");

        when(candidateRepository.save(any(Candidate.class)))
                .thenReturn(savedCandidate);

        Candidate result = candidateService.register(request);

        assertNotNull(result);
        assertEquals("unit.test@example.com", result.getEmail());
        assertEquals("$2a$10$hashedPassword", result.getPasswordHash());

        verify(passwordEncoder)
                .encode("Password123");

        verify(candidateRepository)
                .save(any(Candidate.class));
    }
}
