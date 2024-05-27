package com.yangyoung.english.mail;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailRequest {
    private String to;
    private String subject;
    private String text;
}
