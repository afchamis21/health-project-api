package andre.chamis.healthproject.controller;

import andre.chamis.healthproject.domain.auth.annotation.RequiresPaidSubscription;
import andre.chamis.healthproject.domain.health.attendance.dto.GetAttendanceWithUsernameDTO;
import andre.chamis.healthproject.domain.health.collaborator.dto.GetCollaboratorDTO;
import andre.chamis.healthproject.domain.health.patient.dto.GetPatientDTO;
import andre.chamis.healthproject.domain.health.patient.dto.GetPatientSummaryDTO;
import andre.chamis.healthproject.domain.health.patient.dto.UpdatePatientDTO;
import andre.chamis.healthproject.domain.health.patient.model.Patient;
import andre.chamis.healthproject.domain.user.dto.GetUsernameAndIdDTO;
import andre.chamis.healthproject.infra.request.request.PaginationInfo;
import andre.chamis.healthproject.infra.request.response.PaginatedResponse;
import andre.chamis.healthproject.infra.request.response.ResponseMessage;
import andre.chamis.healthproject.infra.request.response.ResponseMessageBuilder;
import andre.chamis.healthproject.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("patient")
public class PatientController {
    private final PatientService patientService;

    @GetMapping("")
    public ResponseEntity<ResponseMessage<GetPatientDTO>> getPatient(@RequestParam Long patientId) {
        GetPatientDTO response = patientService.getPatient(patientId);
        return ResponseMessageBuilder.build(response, HttpStatus.OK);
    }

    @RequiresPaidSubscription
    @PutMapping("")
    public ResponseEntity<ResponseMessage<GetPatientDTO>> updatePatient(
            @RequestParam Long patientId,
            @RequestBody UpdatePatientDTO updatePatientDTO
    ) {
        Patient patient = patientService.updatePatient(patientId, updatePatientDTO);
        return ResponseMessageBuilder.build(GetPatientDTO.fromPatient(patient), HttpStatus.OK);
    }

    @RequiresPaidSubscription
    @DeleteMapping("")
    public ResponseEntity<ResponseMessage<Void>> deletePatient(@RequestParam Long patientId) {
        patientService.deletePatient(patientId);
        return ResponseMessageBuilder.build(HttpStatus.OK);
    }

    @GetMapping("summary")
    public ResponseEntity<ResponseMessage<GetPatientSummaryDTO>> getPatientSummary(@RequestParam Long patientId) {
        return ResponseMessageBuilder.build(patientService.getPatientSummaryDTOById(patientId), HttpStatus.OK);
    }

    @RequiresPaidSubscription
    @PatchMapping("activate")
    public ResponseEntity<ResponseMessage<Void>> activatePatient(@RequestParam Long patientId) {
        patientService.activePatient(patientId);
        return ResponseMessageBuilder.build(HttpStatus.OK);
    }

    @RequiresPaidSubscription
    @PatchMapping("deactivate")
    public ResponseEntity<ResponseMessage<Void>> deactivatePatient(@RequestParam Long patientId) {
        patientService.deactivatePatient(patientId);
        return ResponseMessageBuilder.build(HttpStatus.OK);
    }

    @GetMapping("attendance")
    public ResponseEntity<ResponseMessage<PaginatedResponse<GetAttendanceWithUsernameDTO>>> getAttendances(
            @RequestParam Long patientId,
            @RequestParam(required = false) Optional<Long> userId,
            PaginationInfo paginationInfo
    ) {
        PaginatedResponse<GetAttendanceWithUsernameDTO> body = patientService.getAttendances(patientId, userId, paginationInfo);
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }

    @GetMapping("collaborators")
    public ResponseEntity<ResponseMessage<PaginatedResponse<GetCollaboratorDTO>>> getCollaborators(
            @RequestParam Long patientId, PaginationInfo paginationInfo

    ) {
        PaginatedResponse<GetCollaboratorDTO> body = patientService.getCollaborators(patientId, paginationInfo);
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }

    @GetMapping("collaborators/names")
    public ResponseEntity<ResponseMessage<List<GetUsernameAndIdDTO>>> getCollaboratorsNames(
            @RequestParam Long patientId
    ) {
        List<GetUsernameAndIdDTO> body = patientService.getAllCollaboratorNames(patientId);
        return ResponseMessageBuilder.build(body, HttpStatus.OK);
    }

    @RequiresPaidSubscription
    @PatchMapping("collaborators/activate")
    public ResponseEntity<ResponseMessage<Void>> activateCollaborator(@RequestParam Long patientId, @RequestParam Long userId) {
        patientService.activateCollaborator(patientId, userId);
        return ResponseMessageBuilder.build(HttpStatus.OK);
    }

    @RequiresPaidSubscription
    @PatchMapping("collaborators/deactivate")
    public ResponseEntity<ResponseMessage<Void>> deactivateCollaborator(@RequestParam Long patientId, @RequestParam Long userId) {
        patientService.deactivateCollaborator(patientId, userId);
        return ResponseMessageBuilder.build(HttpStatus.OK);
    }
}
