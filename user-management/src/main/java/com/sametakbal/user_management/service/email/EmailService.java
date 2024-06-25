package com.sametakbal.user_management.service.email;

import com.sametakbal.user_management.dto.request.EmailRequest;

public interface EmailService {
    void sendEmail(EmailRequest emailRequest);
}
