package com.ajeet.hospital.controller;

import com.ajeet.hospital.dto.DoctorResponse;
import com.ajeet.hospital.service.DoctorService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;
import org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@WebMvcTest(DoctorController.class)
@ImportAutoConfiguration(exclude = {
        OAuth2ClientAutoConfiguration.class,
        OAuth2ClientWebSecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DoctorService doctorService;


    // =========================================================
    // GET ALL DOCTORS
    // =========================================================

    @Test
    void getAllDoctors_shouldReturn200AndDoctors()
            throws Exception {

        DoctorResponse doctor1 = new DoctorResponse();

        doctor1.setId(1L);
        doctor1.setName("Dr. Ajeet");
        doctor1.setSpecialization("Cardiology");
        doctor1.setPhone("9876543210");
        doctor1.setDepartmentId(10L);
        doctor1.setDepartmentName("Cardiology");


        DoctorResponse doctor2 = new DoctorResponse();

        doctor2.setId(2L);
        doctor2.setName("Dr. Rahul");
        doctor2.setSpecialization("Neurology");
        doctor2.setPhone("9876543211");
        doctor2.setDepartmentId(20L);
        doctor2.setDepartmentName("Neurology");


        when(doctorService.getAllDoctors())
                .thenReturn(
                        List.of(doctor1, doctor2)
                );


        mockMvc.perform(
                        get("/api/doctors")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))

                .andExpect(
                        jsonPath("$[0].id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$[0].name")
                                .value("Dr. Ajeet")
                )
                .andExpect(
                        jsonPath("$[0].specialization")
                                .value("Cardiology")
                )
                .andExpect(
                        jsonPath("$[0].phone")
                                .value("9876543210")
                )
                .andExpect(
                        jsonPath("$[0].departmentId")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$[0].departmentName")
                                .value("Cardiology")
                )

                .andExpect(
                        jsonPath("$[1].name")
                                .value("Dr. Rahul")
                )
                .andExpect(
                        jsonPath("$[1].specialization")
                                .value("Neurology")
                );


        verify(doctorService)
                .getAllDoctors();
    }

    @Test
    void getDoctorById_shouldReturn200AndDoctor()
            throws Exception {

        DoctorResponse response = new DoctorResponse();

        response.setId(1L);
        response.setName("Dr. Ajeet");
        response.setSpecialization("Cardiology");
        response.setPhone("9876543210");
        response.setDepartmentId(10L);
        response.setDepartmentName("Cardiology");


        when(doctorService.getDoctorById(1L))
                .thenReturn(response);


        mockMvc.perform(
                        get("/api/doctors/1")
                )
                .andExpect(status().isOk())

                .andExpect(
                        jsonPath("$.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath("$.name")
                                .value("Dr. Ajeet")
                )
                .andExpect(
                        jsonPath("$.specialization")
                                .value("Cardiology")
                )
                .andExpect(
                        jsonPath("$.phone")
                                .value("9876543210")
                )
                .andExpect(
                        jsonPath("$.departmentId")
                                .value(10)
                )
                .andExpect(
                        jsonPath("$.departmentName")
                                .value("Cardiology")
                );


        verify(doctorService)
                .getDoctorById(1L);
    }

    @Test
    void createDoctor_shouldReturn200AndDoctorResponse()
            throws Exception {

        DoctorResponse response = new DoctorResponse();

        response.setId(1L);
        response.setName("Dr. Ajeet");
        response.setSpecialization("Cardiology");
        response.setPhone("9876543210");
        response.setDepartmentId(10L);
        response.setDepartmentName("Cardiology");


        when(doctorService.createDoctor(
                any(com.ajeet.hospital.dto.DoctorRequest.class)
        )).thenReturn(response);


        mockMvc.perform(
                        post("/api/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Dr. Ajeet",
                                "specialization": "Cardiology",
                                "phone": "9876543210",
                                "departmentId": 10
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Dr. Ajeet"))
                .andExpect(jsonPath("$.specialization")
                        .value("Cardiology"))
                .andExpect(jsonPath("$.phone")
                        .value("9876543210"))
                .andExpect(jsonPath("$.departmentId")
                        .value(10))
                .andExpect(jsonPath("$.departmentName")
                        .value("Cardiology"));


        verify(doctorService)
                .createDoctor(any());
    }

    @Test
    void createDoctor_shouldReturn400WhenNameIsBlank()
            throws Exception {

        mockMvc.perform(
                        post("/api/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "",
                                "specialization": "Cardiology",
                                "phone": "9876543210",
                                "departmentId": 10
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                doctorService,
                never()
        ).createDoctor(any());
    }

    @Test
    void updateDoctor_shouldReturn200AndUpdatedDoctor()
            throws Exception {

        DoctorResponse response = new DoctorResponse();

        response.setId(1L);
        response.setName("Dr. Ajeet Kumar");
        response.setSpecialization("Neurology");
        response.setPhone("9999999999");
        response.setDepartmentId(20L);
        response.setDepartmentName("Neurology");

        when(doctorService.updateDoctor(
                eq(1L),
                any(com.ajeet.hospital.dto.DoctorRequest.class)
        )).thenReturn(response);

        mockMvc.perform(
                        put("/api/doctors/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Dr. Ajeet Kumar",
                                "specialization": "Neurology",
                                "phone": "9999999999",
                                "departmentId": 20
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Dr. Ajeet Kumar"))
                .andExpect(jsonPath("$.specialization")
                        .value("Neurology"))
                .andExpect(jsonPath("$.phone")
                        .value("9999999999"))
                .andExpect(jsonPath("$.departmentId")
                        .value(20))
                .andExpect(jsonPath("$.departmentName")
                        .value("Neurology"));

        verify(doctorService)
                .updateDoctor(
                        eq(1L),
                        any(com.ajeet.hospital.dto.DoctorRequest.class)
                );
    }

    @Test
    void deleteDoctor_shouldReturn200AndSuccessMessage()
            throws Exception {

        mockMvc.perform(
                        delete("/api/doctors/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().string(
                                "Doctor deleted successfully"
                        )
                );

        verify(doctorService)
                .deleteDoctor(1L);
    }

    @Test
    void searchDoctorsBySpecializationAndDepartment_shouldReturn200AndDoctors()
            throws Exception {

        DoctorResponse response = new DoctorResponse();

        response.setId(1L);
        response.setName("Dr. Ajeet");
        response.setSpecialization("Cardiology");
        response.setPhone("9876543210");
        response.setDepartmentId(10L);
        response.setDepartmentName("Cardiology");


        when(
                doctorService.findBySpecializationAndDepartment(
                        "Cardiology",
                        10L
                )
        ).thenReturn(List.of(response));


        mockMvc.perform(
                        get("/api/doctors/search/advanced")
                                .param("specialization", "Cardiology")
                                .param("departmentId", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name")
                        .value("Dr. Ajeet"))
                .andExpect(jsonPath("$[0].specialization")
                        .value("Cardiology"))
                .andExpect(jsonPath("$[0].departmentId")
                        .value(10))
                .andExpect(jsonPath("$[0].departmentName")
                        .value("Cardiology"));


        verify(
                doctorService
        ).findBySpecializationAndDepartment(
                "Cardiology",
                10L
        );
    }

    @Test
    void searchByNameAndDepartment_shouldReturn200AndDoctors()
            throws Exception {

        DoctorResponse response = new DoctorResponse();

        response.setId(1L);
        response.setName("Dr. Ajeet");
        response.setSpecialization("Cardiology");
        response.setPhone("9876543210");
        response.setDepartmentId(10L);
        response.setDepartmentName("Cardiology");

        when(
                doctorService.searchByNameAndDepartment(
                        "Ajeet",
                        10L
                )
        ).thenReturn(List.of(response));

        mockMvc.perform(
                        get("/api/doctors/search/by-name")
                                .param("name", "Ajeet")
                                .param("departmentId", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name")
                        .value("Dr. Ajeet"))
                .andExpect(jsonPath("$[0].specialization")
                        .value("Cardiology"))
                .andExpect(jsonPath("$[0].phone")
                        .value("9876543210"))
                .andExpect(jsonPath("$[0].departmentId")
                        .value(10))
                .andExpect(jsonPath("$[0].departmentName")
                        .value("Cardiology"));

        verify(
                doctorService
        ).searchByNameAndDepartment(
                "Ajeet",
                10L
        );
    }

    @Test
    void searchDoctors_shouldReturn200WithPagination()
            throws Exception {

        DoctorResponse response = new DoctorResponse();

        response.setId(1L);
        response.setName("Dr. Ajeet");
        response.setSpecialization("Cardiology");
        response.setPhone("9876543210");
        response.setDepartmentId(10L);
        response.setDepartmentName("Cardiology");

        org.springframework.data.domain.Page<DoctorResponse> page =
                new org.springframework.data.domain.PageImpl<>(
                        List.of(response)
                );

        when(doctorService.searchDoctors(
                "Cardiology",
                0,
                10,
                "name",
                "asc"
        )).thenReturn(page);

        mockMvc.perform(
                        get("/api/doctors/search")
                                .param("specialization", "Cardiology")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sortBy", "name")
                                .param("direction", "asc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name")
                        .value("Dr. Ajeet"))
                .andExpect(jsonPath("$.content[0].specialization")
                        .value("Cardiology"))
                .andExpect(jsonPath("$.content[0].departmentId")
                        .value(10))
                .andExpect(jsonPath("$.content[0].departmentName")
                        .value("Cardiology"));

        verify(doctorService).searchDoctors(
                "Cardiology",
                0,
                10,
                "name",
                "asc"
        );
    }
    @Test
    void searchDoctors_shouldReturn400WhenPageIsNegative()
            throws Exception {

        mockMvc.perform(
                        get("/api/doctors/search")
                                .param("specialization", "Cardiology")
                                .param("page", "-1")
                                .param("size", "10")
                                .param("sortBy", "name")
                                .param("direction", "asc")
                )
                .andExpect(status().isBadRequest());

        verify(
                doctorService,
                never()
        ).searchDoctors(
                anyString(),
                anyInt(),
                anyInt(),
                anyString(),
                anyString()
        );
    }

    @Test
    void searchDoctors_shouldReturn400WhenSizeIsZero()
            throws Exception {

        mockMvc.perform(
                        get("/api/doctors/search")
                                .param("specialization", "Cardiology")
                                .param("page", "0")
                                .param("size", "0")
                                .param("sortBy", "name")
                                .param("direction", "asc")
                )
                .andExpect(status().isBadRequest());

        verify(
                doctorService,
                never()
        ).searchDoctors(
                anyString(),
                anyInt(),
                anyInt(),
                anyString(),
                anyString()
        );
    }

    @Test
    void searchDoctors_shouldReturn400WhenSizeIsGreaterThan100()
            throws Exception {

        mockMvc.perform(
                        get("/api/doctors/search")
                                .param("specialization", "Cardiology")
                                .param("page", "0")
                                .param("size", "101")
                                .param("sortBy", "name")
                                .param("direction", "asc")
                )
                .andExpect(status().isBadRequest());

        verify(
                doctorService,
                never()
        ).searchDoctors(
                anyString(),
                anyInt(),
                anyInt(),
                anyString(),
                anyString()
        );
    }

    @Test
    void searchDoctors_shouldUseDefaultPaginationParameters()
            throws Exception {

        org.springframework.data.domain.Page<DoctorResponse> page =
                new org.springframework.data.domain.PageImpl<>(
                        List.of()
                );

        when(doctorService.searchDoctors(
                "Cardiology",
                0,
                10,
                "name",
                "asc"
        )).thenReturn(page);

        mockMvc.perform(
                        get("/api/doctors/search")
                                .param("specialization", "Cardiology")
                )
                .andExpect(status().isOk());

        verify(doctorService).searchDoctors(
                "Cardiology",
                0,
                10,
                "name",
                "asc"
        );
    }

    @Test
    void searchDoctors_shouldSupportDescendingDirection()
            throws Exception {

        org.springframework.data.domain.Page<DoctorResponse> page =
                new org.springframework.data.domain.PageImpl<>(
                        List.of()
                );

        when(doctorService.searchDoctors(
                "Cardiology",
                0,
                10,
                "name",
                "desc"
        )).thenReturn(page);

        mockMvc.perform(
                        get("/api/doctors/search")
                                .param("specialization", "Cardiology")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sortBy", "name")
                                .param("direction", "desc")
                )
                .andExpect(status().isOk());

        verify(doctorService).searchDoctors(
                "Cardiology",
                0,
                10,
                "name",
                "desc"
        );
    }

    @Test
    void searchDoctors_shouldReturn400WhenSpecializationIsMissing()
            throws Exception {

        mockMvc.perform(
                        get("/api/doctors/search")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sortBy", "name")
                                .param("direction", "asc")
                )
                .andExpect(status().isBadRequest());

        verify(
                doctorService,
                never()
        ).searchDoctors(
                anyString(),
                anyInt(),
                anyInt(),
                anyString(),
                anyString()
        );
    }

    @Test
    void createDoctor_shouldReturn400WhenDepartmentIdIsMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Dr. Ajeet",
                                "specialization": "Cardiology",
                                "phone": "9876543210"
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                doctorService,
                never()
        ).createDoctor(any());
    }

    @Test
    void createDoctor_shouldReturn400WhenSpecializationIsBlank()
            throws Exception {

        mockMvc.perform(
                        post("/api/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Dr. Ajeet",
                                "specialization": "",
                                "phone": "9876543210",
                                "departmentId": 10
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                doctorService,
                never()
        ).createDoctor(any());
    }

    @Test
    void createDoctor_shouldReturn400WhenPhoneIsBlank()
            throws Exception {

        mockMvc.perform(
                        post("/api/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Dr. Ajeet",
                                "specialization": "Cardiology",
                                "phone": "",
                                "departmentId": 10
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                doctorService,
                never()
        ).createDoctor(any());
    }

    @Test
    void createDoctor_shouldReturn400WhenNameIsTooShort()
            throws Exception {

        mockMvc.perform(
                        post("/api/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Dr",
                                "specialization": "Cardiology",
                                "phone": "9876543210",
                                "departmentId": 10
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                doctorService,
                never()
        ).createDoctor(any());
    }

    @Test
    void createDoctor_shouldReturn400WhenSpecializationIsTooShort()
            throws Exception {

        mockMvc.perform(
                        post("/api/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Dr. Ajeet",
                                "specialization": "IT",
                                "phone": "9876543210",
                                "departmentId": 10
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                doctorService,
                never()
        ).createDoctor(any());
    }

    @Test
    void createDoctor_shouldReturn400WhenNameIsTooLong()
            throws Exception {

        String longName = "A".repeat(101);

        mockMvc.perform(
                        post("/api/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "%s",
                                "specialization": "Cardiology",
                                "phone": "9876543210",
                                "departmentId": 10
                            }
                            """.formatted(longName))
                )
                .andExpect(status().isBadRequest());

        verify(
                doctorService,
                never()
        ).createDoctor(any());
    }

    @Test
    void createDoctor_shouldReturn400WhenSpecializationIsTooLong()
            throws Exception {

        String longSpecialization = "A".repeat(101);

        mockMvc.perform(
                        post("/api/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Dr. Ajeet",
                                "specialization": "%s",
                                "phone": "9876543210",
                                "departmentId": 10
                            }
                            """.formatted(longSpecialization))
                )
                .andExpect(status().isBadRequest());

        verify(
                doctorService,
                never()
        ).createDoctor(any());
    }

    @Test
    void createDoctor_shouldReturn400WhenNameIsMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "specialization": "Cardiology",
                                "phone": "9876543210",
                                "departmentId": 10
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                doctorService,
                never()
        ).createDoctor(any());
    }

    @Test
    void createDoctor_shouldReturn400WhenSpecializationIsMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Dr. Ajeet",
                                "phone": "9876543210",
                                "departmentId": 10
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                doctorService,
                never()
        ).createDoctor(any());
    }

    @Test
    void createDoctor_shouldReturn400WhenPhoneIsMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/doctors")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Dr. Ajeet",
                                "specialization": "Cardiology",
                                "departmentId": 10
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                doctorService,
                never()
        ).createDoctor(any());
    }


}
