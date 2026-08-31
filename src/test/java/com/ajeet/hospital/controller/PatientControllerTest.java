package com.ajeet.hospital.controller;

import com.ajeet.hospital.dto.PatientRequest;
import com.ajeet.hospital.dto.PatientResponse;
import com.ajeet.hospital.service.PatientService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.springframework.http.MediaType;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;
import org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration;


@WebMvcTest(PatientController.class)
@ImportAutoConfiguration(exclude = {
        OAuth2ClientAutoConfiguration.class,
        OAuth2ClientWebSecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PatientService patientService;


    // =========================================================
    // CREATE PATIENT
    // =========================================================

    @Test
    void createPatient_shouldReturn200AndPatientResponse()
            throws Exception {

        PatientResponse response = new PatientResponse();

        response.setId(1L);
        response.setName("Ajeet");
        response.setDateOfBirth(
                LocalDate.of(2000, 1, 15)
        );
        response.setGender("Male");
        response.setPhone("9876543210");
        response.setEmail("ajeet@gmail.com");
        response.setAddress("Delhi");


        when(patientService.createPatient(
                any(PatientRequest.class)
        )).thenReturn(response);


        mockMvc.perform(
                        post("/api/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "name": "Ajeet",
                                    "dateOfBirth": "2000-01-15",
                                    "gender": "Male",
                                    "phone": "9876543210",
                                    "email": "ajeet@gmail.com",
                                    "address": "Delhi"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ajeet"))
                .andExpect(jsonPath("$.dateOfBirth")
                        .value("2000-01-15"))
                .andExpect(jsonPath("$.gender").value("Male"))
                .andExpect(jsonPath("$.phone")
                        .value("9876543210"))
                .andExpect(jsonPath("$.email")
                        .value("ajeet@gmail.com"))
                .andExpect(jsonPath("$.address")
                        .value("Delhi"));


        verify(patientService)
                .createPatient(any(PatientRequest.class));
    }


    // =========================================================
    // CREATE PATIENT - VALIDATION
    // =========================================================

    @Test
    void createPatient_shouldReturn400WhenNameIsBlank()
            throws Exception {

        mockMvc.perform(
                        post("/api/patients")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "name": "",
                                    "dateOfBirth": "2000-01-15",
                                    "gender": "Male",
                                    "phone": "9876543210",
                                    "email": "ajeet@gmail.com",
                                    "address": "Delhi"
                                }
                                """)
                )
                .andExpect(status().isBadRequest());


        verify(
                patientService,
                never()
        ).createPatient(any(PatientRequest.class));
    }


    // =========================================================
    // GET ALL PATIENTS
    // =========================================================

    @Test
    void getAllPatients_shouldReturn200AndPatients()
            throws Exception {

        PatientResponse patient1 = new PatientResponse();

        patient1.setId(1L);
        patient1.setName("Ajeet");
        patient1.setGender("Male");
        patient1.setPhone("9876543210");
        patient1.setEmail("ajeet@gmail.com");
        patient1.setAddress("Delhi");


        PatientResponse patient2 = new PatientResponse();

        patient2.setId(2L);
        patient2.setName("Rahul");
        patient2.setGender("Male");
        patient2.setPhone("9876543211");
        patient2.setEmail("rahul@gmail.com");
        patient2.setAddress("Mumbai");


        when(patientService.getAllPatients())
                .thenReturn(List.of(
                        patient1,
                        patient2
                ));


        mockMvc.perform(
                        get("/api/patients")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name")
                        .value("Ajeet"))
                .andExpect(jsonPath("$[1].name")
                        .value("Rahul"));


        verify(patientService)
                .getAllPatients();
    }


    // =========================================================
    // GET PATIENT BY ID
    // =========================================================

    @Test
    void getPatientById_shouldReturn200AndPatient()
            throws Exception {

        PatientResponse response = new PatientResponse();

        response.setId(1L);
        response.setName("Ajeet");
        response.setGender("Male");
        response.setPhone("9876543210");
        response.setEmail("ajeet@gmail.com");
        response.setAddress("Delhi");


        when(patientService.getPatientById(1L))
                .thenReturn(response);


        mockMvc.perform(
                        get("/api/patients/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Ajeet"));


        verify(patientService)
                .getPatientById(1L);
    }


    // =========================================================
    // UPDATE PATIENT
    // =========================================================

    @Test
    void updatePatient_shouldReturn200AndUpdatedPatient()
            throws Exception {

        PatientResponse response = new PatientResponse();

        response.setId(1L);
        response.setName("Ajeet Kumar");
        response.setGender("Male");
        response.setPhone("9999999999");
        response.setEmail("ajeet@gmail.com");
        response.setAddress("Lucknow");


        when(patientService.updatePatient(
                eq(1L),
                any(PatientRequest.class)
        )).thenReturn(response);


        mockMvc.perform(
                        put("/api/patients/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "name": "Ajeet Kumar",
                                    "dateOfBirth": "2000-01-15",
                                    "gender": "Male",
                                    "phone": "9999999999",
                                    "email": "ajeet@gmail.com",
                                    "address": "Lucknow"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Ajeet Kumar"))
                .andExpect(jsonPath("$.phone")
                        .value("9999999999"))
                .andExpect(jsonPath("$.address")
                        .value("Lucknow"));


        verify(patientService)
                .updatePatient(
                        eq(1L),
                        any(PatientRequest.class)
                );
    }


    // =========================================================
    // DELETE PATIENT
    // =========================================================

    @Test
    void deletePatient_shouldReturn200AndSuccessMessage()
            throws Exception {

        mockMvc.perform(
                        delete("/api/patients/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().string(
                                "Patient deleted successfully"
                        )
                );


        verify(patientService)
                .deletePatient(1L);
    }


    // =========================================================
    // PAGINATION
    // =========================================================

    @Test
    void getPatients_shouldReturn200()
            throws Exception {

        when(patientService.getPatients(
                0,
                10,
                "id",
                "asc"
        )).thenReturn(
                new org.springframework.data.domain.PageImpl<>(
                        List.of()
                )
        );


        mockMvc.perform(
                        get("/api/patients/page")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sortBy", "id")
                                .param("direction", "asc")
                )
                .andExpect(status().isOk());


        verify(patientService)
                .getPatients(
                        0,
                        10,
                        "id",
                        "asc"
                );
    }


    // =========================================================
    // SEARCH
    // =========================================================

    @Test
    void searchPatients_shouldReturn200AndPatients()
            throws Exception {

        PatientResponse response = new PatientResponse();

        response.setId(1L);
        response.setName("Ajeet");


        when(patientService.searchPatients("Ajeet"))
                .thenReturn(List.of(response));


        mockMvc.perform(
                        get("/api/patients/search")
                                .param("name", "Ajeet")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()")
                        .value(1))
                .andExpect(jsonPath("$[0].name")
                        .value("Ajeet"));


        verify(patientService)
                .searchPatients("Ajeet");
    }
}
