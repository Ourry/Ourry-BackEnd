package com.bluewhaletech.Ourry.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.firebase.messaging.Message;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FcmMessageDTO {
    @JsonProperty("validate_only")
    private boolean validateOnly;
    private Message message;
}