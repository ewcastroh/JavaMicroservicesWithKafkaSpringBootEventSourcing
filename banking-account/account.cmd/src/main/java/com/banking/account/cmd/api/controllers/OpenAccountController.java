package com.banking.account.cmd.api.controllers;

import com.banking.account.cmd.api.command.OpenAccountCommand;
import com.banking.account.cmd.api.dto.OpenAccountResponse;
import com.banking.account.common.dto.BaseResponse;
import com.banking.cqrs.core.infrastructure.CommandDispatcher;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@AllArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/openBankAccount")
public class OpenAccountController {

    private final Logger logger = Logger.getLogger(OpenAccountController.class.getName());

    private final CommandDispatcher commandDispatcher;

    @PostMapping
    public ResponseEntity<BaseResponse> openAccount(@RequestBody OpenAccountCommand command) {
        var id = UUID.randomUUID().toString();
        command.setId(id);
        try {
            commandDispatcher.send(command);
            return new ResponseEntity<>(
                    new OpenAccountResponse("New banking account has been created successfully", id),
                    HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            logger.log(Level.WARNING, MessageFormat.format("It could not create the new banking account - {0}", e.toString()));
            return new ResponseEntity<>(new BaseResponse(e.toString()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            var safeErrorMessage = MessageFormat.format("Errors processing request - {0}", id);
            logger.log(Level.SEVERE, safeErrorMessage, e);
            return new ResponseEntity<>(new OpenAccountResponse(safeErrorMessage, id), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
