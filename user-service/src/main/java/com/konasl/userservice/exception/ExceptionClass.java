package com.konasl.userservice.exception;

import com.konasl.userservice.payload.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Data
public class ExceptionClass extends RuntimeException {
    HttpStatus status;
    Message errorMessage;
}
