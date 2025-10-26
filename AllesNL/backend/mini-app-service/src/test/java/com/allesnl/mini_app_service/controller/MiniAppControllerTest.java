package com.allesnl.mini_app_service.controller;


import com.allesnl.mini_app_service.contracts.miniapp.MiniAppDetails;
import com.allesnl.mini_app_service.contracts.miniapp.MiniAppInfo;
import com.allesnl.mini_app_service.contracts.template.RegistrationRequest;
import com.allesnl.mini_app_service.contracts.template.RegistrationResponse;
import com.allesnl.mini_app_service.service.MiniAppClientService;
import com.allesnl.mini_app_service.service.MiniAppRegistryService;
import com.allesnl.mini_app_service.service.MiniAppService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class MiniAppControllerTest {

    // --- UNIT TEST SECTION ---
    @Nested
    @ExtendWith(MockitoExtension.class)
    class UnitTests {
        @Mock
        private MiniAppService miniAppService;
        @Mock
        private MiniAppRegistryService miniAppRegistryService;
        @Mock
        private MiniAppClientService miniAppClientService;

        private MiniAppController controller;

        @BeforeEach
        void setup() {
            controller = new MiniAppController(miniAppService, miniAppRegistryService, miniAppClientService);
        }


        @Test
        public void registerMiniApp_shouldReturnMiniAppId() {
            // Given
            MiniAppInfo expectedMiniAppInfo = new MiniAppInfo(1L,
                    "Sample MiniApp",
                    "The best mini app",
                    "http://sample-miniapp-url:8080/internal",
                    "http://logo-url/logo.png");
            RegistrationRequest registration = new RegistrationRequest(expectedMiniAppInfo.getAppName(), expectedMiniAppInfo.getBaseUrl());
            when(miniAppRegistryService.register(new MiniAppInfo(-1L,
                    expectedMiniAppInfo.getAppName(),
                    "",
                    expectedMiniAppInfo.getBaseUrl(),
                    ""))).thenReturn(new MiniAppInfo(1L,
                    expectedMiniAppInfo.getAppName(),
                    "",
                    expectedMiniAppInfo.getBaseUrl(),
                    ""));
            when(miniAppClientService.getMiniAppDetails(1L)).thenReturn(Optional.of(new MiniAppDetails(
                    1L,
                    expectedMiniAppInfo.getAppName(),
                    expectedMiniAppInfo.getAppDescription(),
                    expectedMiniAppInfo.getLogoUrl()
            )));
            when(miniAppRegistryService.replace(expectedMiniAppInfo)).thenReturn(Optional.of(expectedMiniAppInfo));

            // When
            RegistrationResponse response = controller.registerMiniApp(registration).getBody();

            // Then
            assertNotNull(response);
            assertEquals(expectedMiniAppInfo.getAppId(), response.registrationId());

            verify(miniAppRegistryService).register(new MiniAppInfo(-1L,
                    expectedMiniAppInfo.getAppName(),
                    "",
                    expectedMiniAppInfo.getBaseUrl(),
                    ""));
            verify(miniAppClientService).getMiniAppDetails(1L);
            verify(miniAppRegistryService).replace(expectedMiniAppInfo);
        }

        @Test
        public void getAllMiniApps_shouldReturnAllMiniAppDetails() {
            // Given
            MiniAppInfo expectedInfo1 = new MiniAppInfo(1L, "App1", "Description1", "BaseUrl1","LogoUrl1");
            MiniAppInfo expectedInfo2 = new MiniAppInfo(2L, "App2", "Description2", "BaseUrl2","LogoUrl2");
            MiniAppDetails expectedDetails1 = new MiniAppDetails(1L, "App1", "Description1","LogoUrl1");
            MiniAppDetails expectedDetails2 = new MiniAppDetails(2L, "App2", "Description2","LogoUrl2");

            List<MiniAppDetails> expectedList = Arrays.asList(expectedDetails1, expectedDetails2);
            Collection<MiniAppInfo> returnedList = Arrays.asList(expectedInfo1, expectedInfo2);
            when(miniAppRegistryService.getAll()).thenReturn(returnedList);

            // When
            ResponseEntity<List<MiniAppDetails>> response = controller.getAllMiniApps();

            // Then
            assertNotNull(response);
            assertEquals(expectedList, response.getBody());
            verify(miniAppRegistryService).getAll();
        }

        @Test
        public void testGetMiniAppInfo() {
            // Given
            MiniAppInfo expectedInfo = new MiniAppInfo(1L, "App1", "Description1", "BaseUrl1","LogoUrl1");
            MiniAppDetails expectedDetails = new MiniAppDetails(1L, "App1", "Description1","LogoUrl1");

            when(miniAppRegistryService.get(1L)).thenReturn(Optional.of(expectedInfo));

            // When
            ResponseEntity<MiniAppDetails> response = controller.getMiniAppInfo(expectedInfo.getAppId());

            // Then
            assertNotNull(response);
            assertEquals(expectedDetails, response.getBody());
            verify(miniAppRegistryService).get(1L);
        }
    }

    // --- INTEGRATION TEST SECTION ---
    @Nested
    @WebMvcTest
    class IntegrationTests {

    }


}
