package com.cloudcampus.backend.controller;

import com.cloudcampus.backend.dto.CandidateRegistrationRequest;
import com.cloudcampus.backend.dto.CandidateRegistrationResponse;
import com.cloudcampus.backend.entity.Candidate;
import com.cloudcampus.backend.service.CandidateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @PostMapping
    public ResponseEntity<CandidateRegistrationResponse> register(
            @Valid @RequestBody CandidateRegistrationRequest request) {

        Candidate candidate = candidateService.register(request);

        CandidateRegistrationResponse response =
                new CandidateRegistrationResponse(
                        candidate.getId(),
                        candidate.getFirstName(),
                        candidate.getLastName(),
                        candidate.getEmail(),
                        candidate.getCreatedAt()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
