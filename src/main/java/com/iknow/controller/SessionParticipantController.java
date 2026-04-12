package com.iknow.controller;

import com.iknow.dto.request.SessionParticipantRequest;
import com.iknow.service.SessionParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sessions/{sessionId}/participants")
@RequiredArgsConstructor
public class SessionParticipantController {

    private final SessionParticipantService sessionParticipantService;

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void joinSession(@PathVariable String sessionId, @RequestBody SessionParticipantRequest request) {
        sessionParticipantService.joinSession(sessionId, request);
    }

    @PostMapping("/leave")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void leaveSession(@PathVariable String sessionId, @RequestBody SessionParticipantRequest request) {
        sessionParticipantService.leaveSession(sessionId, request);
    }
}
