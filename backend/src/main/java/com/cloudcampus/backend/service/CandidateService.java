package com.cloudcampus.backend.service;

import com.cloudcampus.backend.dto.CandidateRegistrationRequest;
import com.cloudcampus.backend.entity.Candidate;
import com.cloudcampus.backend.repository.CandidateRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CandidateService {

    private final CandidateRepository candidateRepository;
    private final PasswordEncoder passwordEncoder;

    public CandidateService(
            CandidateRepository candidateRepository,
            PasswordEncoder passwordEncoder) {

        this.candidateRepository = candidateRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Candidate register(CandidateRegistrationRequest request) {

        if (candidateRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        Candidate candidate = new Candidate();

        candidate.setFirstName(request.getFirstName());
        candidate.setLastName(request.getLastName());
        candidate.setEmail(request.getEmail());

        String hashedPassword =
                passwordEncoder.encode(request.getPassword());

        candidate.setPasswordHash(hashedPassword);

        candidate.setCreatedAt(LocalDateTime.now());

        return candidateRepository.save(candidate);
    }
}
