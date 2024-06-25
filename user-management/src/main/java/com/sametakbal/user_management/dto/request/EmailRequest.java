package com.sametakbal.user_management.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EmailRequest {
    private String to;
    private String subject;
    private String text;
}
