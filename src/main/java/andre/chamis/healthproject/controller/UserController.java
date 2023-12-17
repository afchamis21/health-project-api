package andre.chamis.healthproject.controller;

import andre.chamis.healthproject.domain.auth.annotation.ClientAuthenticated;
import andre.chamis.healthproject.domain.auth.annotation.JwtAuthenticated;
import andre.chamis.healthproject.domain.auth.annotation.NonAuthenticated;
import andre.chamis.healthproject.domain.response.ResponseMessage;
import andre.chamis.healthproject.domain.response.ResponseMessageBuilder;
import andre.chamis.healthproject.domain.user.dto.CompleteRegistrationDTO;
import andre.chamis.healthproject.domain.user.dto.CreateUserDTO;
import andre.chamis.healthproject.domain.user.dto.GetUserDTO;
import andre.chamis.healthproject.domain.user.dto.UpdateUserDTO;
import andre.chamis.healthproject.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@JwtAuthenticated
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @NonAuthenticated
    @PostMapping("register")
    public ResponseEntity<ResponseMessage<GetUserDTO>> register(@RequestBody CreateUserDTO createUserDTO) {
        GetUserDTO getUserDTO = userService.handleRegisterUser(createUserDTO);
        return ResponseMessageBuilder.build(getUserDTO, HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<ResponseMessage<GetUserDTO>> getUser(@RequestParam Optional<Long> userId) {
        GetUserDTO getUserDTO = userService.getUserById(userId);
        return ResponseMessageBuilder.build(getUserDTO, HttpStatus.OK);
    }

    @PutMapping("update")
    public ResponseEntity<ResponseMessage<GetUserDTO>> updateUser(@RequestBody UpdateUserDTO updateUserDTO) {
        GetUserDTO getUserDTO = userService.updateUser(updateUserDTO);
        return ResponseMessageBuilder.build(getUserDTO, HttpStatus.OK);
    }

    @PutMapping("complete-registration")
    public ResponseEntity<ResponseMessage<GetUserDTO>> completeRegistration(@RequestBody CompleteRegistrationDTO completeRegistrationDTO) {
        GetUserDTO getUserDTO = userService.handleCompleteRegistration(completeRegistrationDTO);
        return ResponseMessageBuilder.build(getUserDTO, HttpStatus.OK);
    }

    @ClientAuthenticated
    @PatchMapping("activate")
    public ResponseEntity<ResponseMessage<GetUserDTO>> activateUser(@RequestParam Long userId) {
        GetUserDTO getUserDTO = userService.activateUser(userId);
        return ResponseMessageBuilder.build(getUserDTO, HttpStatus.OK);
    }

    @ClientAuthenticated
    @PatchMapping("deactivate")
    public ResponseEntity<ResponseMessage<GetUserDTO>> deactivateUser(@RequestParam Long userId) {
        GetUserDTO getUserDTO = userService.deactivateUser(userId);
        return ResponseMessageBuilder.build(getUserDTO, HttpStatus.OK);
    }

    @ClientAuthenticated
    @DeleteMapping("delete")
    public ResponseEntity<ResponseMessage<Void>> deleteUser(@RequestParam Long userId) {
        userService.deleteUser(userId);
        return ResponseMessageBuilder.build(HttpStatus.OK);
    }

}
