package com.mrt.models;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EmailMessage {
    private String recipient;
    private String subject;
    private String body;
    private List<File> attachments = new ArrayList<>();

    public EmailMessage(String recipient, String subject, String body) {
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
    }

    public String getRecipient() { return recipient; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public List<File> getAttachments() { return attachments; }

    public void addAttachment(File file) {
        attachments.add(file);
    }
}
