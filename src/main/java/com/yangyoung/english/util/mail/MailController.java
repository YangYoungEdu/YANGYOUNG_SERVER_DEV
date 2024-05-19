package com.yangyoung.english.util.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mail")
public class MailController {

    private final MailService mailService;

    @PostMapping("/send")
    public String sendMail(@RequestBody MailRequest mailRequest) {
        mailService.sendSimpleMessage(mailRequest.getTo(), mailRequest.getSubject(), mailRequest.getText());
        return "Mail sent successfully";
    }
}
