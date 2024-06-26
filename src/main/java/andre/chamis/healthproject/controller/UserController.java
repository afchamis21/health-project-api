package andre.chamis.healthproject.controller;

import andre.chamis.healthproject.domain.health.attendance.dto.GetAttendanceDTO;
import andre.chamis.healthproject.domain.auth.annotation.ClientAuthenticated;
import andre.chamis.healthproject.domain.auth.annotation.JwtAuthenticated;
import andre.chamis.healthproject.domain.health.collaborator.dto.CreateCollaboratorDTO;
import andre.chamis.healthproject.domain.health.collaborator.dto.GetCollaboratorDTO;
import andre.chamis.healthproject.domain.health.patient.dto.CreatePatientDTO;
import andre.chamis.healthproject.domain.health.patient.dto.GetPatientSummaryDTO;
import andre.chamis.healthproject.infra.request.request.PaginationInfo;
import andre.chamis.healthproject.infra.request.response.PaginatedResponse;
import andre.chamis.healthproject.infra.request.response.ResponseMessage;
import andre.chamis.healthproject.infra.request.response.ResponseMessageBuilder;
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

import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@JwtAuthenticated
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Endpoint for retrieving user information.
     *
     * @param userId Optional parameter specifying the user ID.
     * @return A ResponseEntity containing a ResponseMessage with the requested user's information on success.
     */
    @GetMapping("")
    public ResponseEntity<ResponseMessage<GetUserDTO>> getUser(@RequestParam Optional<Long> userId) {
        GetUserDTO getUserDTO = userService.getUserById(userId);
        return ResponseMessageBuilder.build(getUserDTO, HttpStatus.OK);
    }

    /**
     * Endpoint for registering a new user.
     *
     * @param createUserDTO The DTO containing user registration information.
     * @return A ResponseEntity containing a ResponseMessage with the registered user's information on success.
     */
    @ClientAuthenticated
    @PostMapping("register")
    public ResponseEntity<ResponseMessage<GetUserDTO>> register(@RequestBody CreateUserDTO createUserDTO) {
        GetUserDTO getUserDTO = userService.handleRegisterUser(createUserDTO);
        return ResponseMessageBuilder.build(getUserDTO, HttpStatus.CREATED);
    }

    /**
     * Endpoint for updating user information.
     *
     * @param updateUserDTO The DTO containing user information to be updated.
     * @return A ResponseEntity containing a ResponseMessage with the updated user's information on success.
     */
    @PutMapping("update")
    public ResponseEntity<ResponseMessage<GetUserDTO>> updateUser(@RequestBody UpdateUserDTO updateUserDTO) {
        GetUserDTO getUserDTO = userService.updateUser(updateUserDTO);
        return ResponseMessageBuilder.build(getUserDTO, HttpStatus.OK);
    }

    /**
     * Endpoint for completing user registration.
     *
     * @param completeRegistrationDTO The DTO containing information to complete user registration.
     * @return A ResponseEntity containing a ResponseMessage with the completed user's information on success.
     */
    @PutMapping("complete-registration")
    public ResponseEntity<ResponseMessage<GetUserDTO>> completeRegistration(@RequestBody CompleteRegistrationDTO completeRegistrationDTO) {
        GetUserDTO getUserDTO = userService.handleCompleteRegistration(completeRegistrationDTO);
        return ResponseMessageBuilder.build(getUserDTO, HttpStatus.OK);
    }

    /**
     * Endpoint for activating a user.
     *
     * @param userId The user ID to be activated.
     * @return A ResponseEntity containing a ResponseMessage with the activated user's information on success.
     */
    @ClientAuthenticated
    @PatchMapping("activate")
    public ResponseEntity<ResponseMessage<GetUserDTO>> activateUser(@RequestParam Long userId) {
        GetUserDTO getUserDTO = userService.activateUser(userId);
        return ResponseMessageBuilder.build(getUserDTO, HttpStatus.OK);
    }

    /**
     * Endpoint for deactivating a user.
     *
     * @param userId The user ID to be deactivated.
     * @return A ResponseEntity containing a ResponseMessage with the deactivated user's information on success.
     */
    @ClientAuthenticated
    @PatchMapping("deactivate")
    public ResponseEntity<ResponseMessage<GetUserDTO>> deactivateUser(@RequestParam Long userId) {
        GetUserDTO getUserDTO = userService.deactivateUser(userId);
        return ResponseMessageBuilder.build(getUserDTO, HttpStatus.OK);
    }

    /**
     * Endpoint for deleting a user.
     *
     * @param userId The user ID to be deleted.
     * @return A ResponseEntity containing a ResponseMessage indicating success on deletion.
     */
    @ClientAuthenticated
    @DeleteMapping("delete")
    public ResponseEntity<ResponseMessage<Void>> deleteUser(@RequestParam Long userId) {
        userService.deleteUser(userId);
        return ResponseMessageBuilder.build(HttpStatus.OK);
    }

    /**
     * Searches user patients.
     *
     * @param name           The name to search for.
     * @param paginationInfo Information for pagination.
     * @return A ResponseEntity containing a ResponseMessage with user patients information.
     */
    @GetMapping("patient")
    public ResponseEntity<ResponseMessage<PaginatedResponse<GetPatientSummaryDTO>>> searchUserPatients(
            @RequestParam(required = false, defaultValue = "") String name,
            PaginationInfo paginationInfo
    ) {
        PaginatedResponse<GetPatientSummaryDTO> body = userService.searchPatientByNameAndCollaboratorId(name, paginationInfo);
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }

    @PostMapping("patient")
    public ResponseEntity<ResponseMessage<GetPatientSummaryDTO>> createPatient(@RequestBody CreatePatientDTO createPatientDTO) {
        GetPatientSummaryDTO body = userService.addPatient(createPatientDTO);
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }

    @PostMapping("patient/collaborator")
    public ResponseEntity<ResponseMessage<GetCollaboratorDTO>> addCollaborator(@RequestBody CreateCollaboratorDTO createCollaboratorDTO) {
        GetCollaboratorDTO body = userService.addCollaboratorToPatient(createCollaboratorDTO);
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }


    @PostMapping("/clock-in")
    public ResponseEntity<ResponseMessage<GetAttendanceDTO>> clockIn(@RequestParam Long patientId) {
        GetAttendanceDTO body = userService.clockIn(patientId);
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }

    @PostMapping("/clock-out")
    public ResponseEntity<ResponseMessage<List<GetAttendanceDTO>>> clockOut() {
        List<GetAttendanceDTO> body = userService.clockOut();
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }
}
