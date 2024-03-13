package com.konasl.bookservice.exceptions;

import com.konasl.bookservice.payload.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class CustomException extends RuntimeException {
    HttpStatus status;
    Message errorMessage;
}
