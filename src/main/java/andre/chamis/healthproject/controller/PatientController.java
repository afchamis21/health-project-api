package andre.chamis.healthproject.controller;

import andre.chamis.healthproject.domain.patient.dto.GetPatientDTO;
import andre.chamis.healthproject.domain.patient.dto.UpdatePatientDTO;
import andre.chamis.healthproject.domain.patient.model.Patient;
import andre.chamis.healthproject.domain.response.ResponseMessage;
import andre.chamis.healthproject.domain.response.ResponseMessageBuilder;
import andre.chamis.healthproject.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("patient")
public class PatientController {
    private final PatientService patientService;

    @GetMapping("{patientId}")
    public ResponseEntity<ResponseMessage<GetPatientDTO>> getPatient(@PathVariable Long patientId) {
        GetPatientDTO response = patientService.getPatient(patientId);
        return ResponseMessageBuilder.build(response, HttpStatus.OK);
    }

    @PutMapping("{patientId}")
    public ResponseEntity<ResponseMessage<GetPatientDTO>> updatePatient(
            @PathVariable Long patientId,
            @RequestBody UpdatePatientDTO updatePatientDTO
    ) {
        Patient patient = patientService.updatePatient(patientId, updatePatientDTO);
        return ResponseMessageBuilder.build(GetPatientDTO.fromPatient(patient), HttpStatus.OK);
    }
}
