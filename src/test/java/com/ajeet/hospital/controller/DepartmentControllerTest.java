package com.ajeet.hospital.controller;

import com.ajeet.hospital.dto.DepartmentRequest;
import com.ajeet.hospital.dto.DepartmentResponse;
import com.ajeet.hospital.entity.Department;
import com.ajeet.hospital.exception.DepartmentNotFoundException;
import com.ajeet.hospital.service.DepartmentService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.security.oauth2.client.autoconfigure.OAuth2ClientAutoConfiguration;
import org.springframework.boot.security.oauth2.client.autoconfigure.servlet.OAuth2ClientWebSecurityAutoConfiguration;

import java.util.List;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@WebMvcTest(DepartmentController.class)
@ImportAutoConfiguration(exclude = {
        OAuth2ClientAutoConfiguration.class,
        OAuth2ClientWebSecurityAutoConfiguration.class
})
@AutoConfigureMockMvc(addFilters = false)
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentService departmentService;


    @Test
    void createDepartment_shouldReturn200AndDepartment()
            throws Exception {

        Department department = new Department();

        department.setId(1L);
        department.setName("Cardiology");
        department.setLocation("Delhi");


        when(departmentService.createDepartment(
                any(DepartmentRequest.class)
        )).thenReturn(department);


        mockMvc.perform(
                        post("/api/departments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                {
                                    "name": "Cardiology",
                                    "location": "Delhi"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name")
                        .value("Cardiology"))
                .andExpect(jsonPath("$.location")
                        .value("Delhi"));


        verify(departmentService)
                .createDepartment(
                        any(DepartmentRequest.class)
                );
    }

    @Test
    void createDepartment_shouldReturn400WhenNameIsBlank()
            throws Exception {

        mockMvc.perform(
                        post("/api/departments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "",
                                "location": "Delhi"
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                departmentService,
                never()
        ).createDepartment(any(DepartmentRequest.class));
    }

    @Test
    void createDepartment_shouldReturn400WhenLocationIsBlank()
            throws Exception {

        mockMvc.perform(
                        post("/api/departments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Cardiology",
                                "location": ""
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                departmentService,
                never()
        ).createDepartment(any(DepartmentRequest.class));
    }

    @Test
    void createDepartment_shouldReturn400WhenNameIsTooShort()
            throws Exception {

        mockMvc.perform(
                        post("/api/departments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "IT",
                                "location": "Delhi"
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                departmentService,
                never()
        ).createDepartment(any(DepartmentRequest.class));
    }

    @Test
    void createDepartment_shouldReturn400WhenNameIsTooLong()
            throws Exception {

        String longName = "A".repeat(101);

        mockMvc.perform(
                        post("/api/departments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "%s",
                                "location": "Delhi"
                            }
                            """.formatted(longName))
                )
                .andExpect(status().isBadRequest());

        verify(
                departmentService,
                never()
        ).createDepartment(any(DepartmentRequest.class));
    }

    @Test
    void createDepartment_shouldReturn400WhenLocationIsTooShort()
            throws Exception {

        mockMvc.perform(
                        post("/api/departments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Cardiology",
                                "location": "D"
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                departmentService,
                never()
        ).createDepartment(any(DepartmentRequest.class));
    }

    @Test
    void createDepartment_shouldReturn400WhenLocationIsTooLong()
            throws Exception {

        String longLocation = "A".repeat(101);

        mockMvc.perform(
                        post("/api/departments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Cardiology",
                                "location": "%s"
                            }
                            """.formatted(longLocation))
                )
                .andExpect(status().isBadRequest());

        verify(
                departmentService,
                never()
        ).createDepartment(any(DepartmentRequest.class));
    }

    @Test
    void getAllDepartments_shouldReturn200AndDepartments()
            throws Exception {

        Department department1 = new Department();
        department1.setId(1L);
        department1.setName("Cardiology");
        department1.setLocation("Delhi");

        Department department2 = new Department();
        department2.setId(2L);
        department2.setName("Neurology");
        department2.setLocation("Mumbai");

        when(departmentService.getAllDepartments())
                .thenReturn(List.of(department1, department2));

        mockMvc.perform(
                        get("/api/departments")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Cardiology"))
                .andExpect(jsonPath("$[0].location").value("Delhi"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Neurology"))
                .andExpect(jsonPath("$[1].location").value("Mumbai"));

        verify(departmentService)
                .getAllDepartments();
    }

    @Test
    void getDepartmentById_shouldReturn200AndDepartmentWithDoctors()
            throws Exception {

        DepartmentResponse response = new DepartmentResponse();

        response.setId(1L);
        response.setName("Cardiology");
        response.setLocation("Delhi");

        DepartmentResponse.DoctorSummary doctor =
                new DepartmentResponse.DoctorSummary();

        doctor.setId(10L);
        doctor.setName("Dr. Ajeet");
        doctor.setSpecialization("Cardiology");

        response.setDoctors(List.of(doctor));

        when(departmentService.getDepartmentWithDoctors(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/departments/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Cardiology"))
                .andExpect(jsonPath("$.location").value("Delhi"))
                .andExpect(jsonPath("$.doctors.length()").value(1))
                .andExpect(jsonPath("$.doctors[0].id").value(10))
                .andExpect(jsonPath("$.doctors[0].name")
                        .value("Dr. Ajeet"))
                .andExpect(jsonPath("$.doctors[0].specialization")
                        .value("Cardiology"));

        verify(departmentService)
                .getDepartmentWithDoctors(1L);
    }

    @Test
    void updateDepartment_shouldReturn200AndUpdatedDepartment()
            throws Exception {

        Department department = new Department();

        department.setId(1L);
        department.setName("Neurology");
        department.setLocation("Lucknow");

        when(departmentService.updateDepartment(
                eq(1L),
                any(DepartmentRequest.class)
        )).thenReturn(department);

        mockMvc.perform(
                        put("/api/departments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Neurology",
                                "location": "Lucknow"
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Neurology"))
                .andExpect(jsonPath("$.location").value("Lucknow"));

        verify(departmentService)
                .updateDepartment(
                        eq(1L),
                        any(DepartmentRequest.class)
                );
    }

    @Test
    void deleteDepartment_shouldReturn200AndSuccessMessage()
            throws Exception {

        mockMvc.perform(
                        delete("/api/departments/1")
                )
                .andExpect(status().isOk())
                .andExpect(
                        content().string(
                                "Department deleted successfully"
                        )
                );

        verify(departmentService)
                .deleteDepartment(1L);
    }

    @Test
    void updateDepartment_shouldReturn400WhenNameIsBlank()
            throws Exception {

        mockMvc.perform(
                        put("/api/departments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "",
                                "location": "Delhi"
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                departmentService,
                never()
        ).updateDepartment(
                eq(1L),
                any(DepartmentRequest.class)
        );
    }

    @Test
    void updateDepartment_shouldReturn400WhenLocationIsBlank()
            throws Exception {

        mockMvc.perform(
                        put("/api/departments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Cardiology",
                                "location": ""
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                departmentService,
                never()
        ).updateDepartment(
                eq(1L),
                any(DepartmentRequest.class)
        );
    }

    @Test
    void updateDepartment_shouldReturn400WhenNameIsTooShort()
            throws Exception {

        mockMvc.perform(
                        put("/api/departments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "IT",
                                "location": "Delhi"
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                departmentService,
                never()
        ).updateDepartment(
                eq(1L),
                any(DepartmentRequest.class)
        );
    }

    @Test
    void updateDepartment_shouldReturn400WhenNameIsTooLong()
            throws Exception {

        String longName = "A".repeat(101);

        mockMvc.perform(
                        put("/api/departments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "%s",
                                "location": "Delhi"
                            }
                            """.formatted(longName))
                )
                .andExpect(status().isBadRequest());

        verify(
                departmentService,
                never()
        ).updateDepartment(
                eq(1L),
                any(DepartmentRequest.class)
        );
    }

    @Test
    void updateDepartment_shouldReturn400WhenLocationIsTooShort()
            throws Exception {

        mockMvc.perform(
                        put("/api/departments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Cardiology",
                                "location": "D"
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                departmentService,
                never()
        ).updateDepartment(
                eq(1L),
                any(DepartmentRequest.class)
        );
    }

    @Test
    void updateDepartment_shouldReturn400WhenLocationIsTooLong()
            throws Exception {

        String longLocation = "A".repeat(101);

        mockMvc.perform(
                        put("/api/departments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Cardiology",
                                "location": "%s"
                            }
                            """.formatted(longLocation))
                )
                .andExpect(status().isBadRequest());

        verify(
                departmentService,
                never()
        ).updateDepartment(
                eq(1L),
                any(DepartmentRequest.class)
        );
    }

    @Test
    void getDepartmentById_shouldReturnDepartmentWithEmptyDoctors()
            throws Exception {

        DepartmentResponse response = new DepartmentResponse();

        response.setId(1L);
        response.setName("Cardiology");
        response.setLocation("Delhi");
        response.setDoctors(List.of());

        when(departmentService.getDepartmentWithDoctors(1L))
                .thenReturn(response);

        mockMvc.perform(
                        get("/api/departments/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Cardiology"))
                .andExpect(jsonPath("$.location").value("Delhi"))
                .andExpect(jsonPath("$.doctors").isArray())
                .andExpect(jsonPath("$.doctors.length()").value(0));

        verify(departmentService)
                .getDepartmentWithDoctors(1L);
    }

    @Test
    void getDepartmentById_shouldReturn404WhenDepartmentNotFound()
            throws Exception {

        when(departmentService.getDepartmentWithDoctors(999L))
                .thenThrow(
                        new DepartmentNotFoundException(
                                "Department with id 999 not found"
                        )
                );

        mockMvc.perform(
                        get("/api/departments/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Department with id 999 not found"));

        verify(departmentService)
                .getDepartmentWithDoctors(999L);
    }

    @Test
    void createDepartment_shouldReturn400WhenNameIsMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/departments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "location": "Delhi"
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                departmentService,
                never()
        ).createDepartment(any(DepartmentRequest.class));
    }

    @Test
    void createDepartment_shouldReturn400WhenLocationIsMissing()
            throws Exception {

        mockMvc.perform(
                        post("/api/departments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Cardiology"
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                departmentService,
                never()
        ).createDepartment(any(DepartmentRequest.class));
    }

    @Test
    void updateDepartment_shouldReturn400WhenNameIsMissing()
            throws Exception {

        mockMvc.perform(
                        put("/api/departments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "location": "Delhi"
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                departmentService,
                never()
        ).updateDepartment(
                eq(1L),
                any(DepartmentRequest.class)
        );
    }

    @Test
    void updateDepartment_shouldReturn400WhenLocationIsMissing()
            throws Exception {

        mockMvc.perform(
                        put("/api/departments/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "name": "Cardiology"
                            }
                            """)
                )
                .andExpect(status().isBadRequest());

        verify(
                departmentService,
                never()
        ).updateDepartment(
                eq(1L),
                any(DepartmentRequest.class)
        );
    }

    @Test
    void deleteDepartment_shouldReturn404WhenDepartmentNotFound()
            throws Exception {

        doThrow(
                new DepartmentNotFoundException(
                        "Department with id 999 not found"
                )
        ).when(departmentService)
                .deleteDepartment(999L);

        mockMvc.perform(
                        delete("/api/departments/999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Department with id 999 not found"));

        verify(departmentService)
                .deleteDepartment(999L);
    }
}