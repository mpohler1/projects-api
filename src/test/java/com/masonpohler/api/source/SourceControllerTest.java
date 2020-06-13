package com.masonpohler.api.source;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class SourceControllerTest {

    @Mock
    private SourceRepository mockedRepository;

    @InjectMocks
    private SourceController controller;

    @BeforeEach
    void set_up() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void get_all_sources_returns_an_empty_list_when_no_sources_are_in_repository() {
        when(mockedRepository.findAll()).thenReturn(new LinkedList<Source>());
        List<Source> actualSourceList = controller.getAllSources();
        assert actualSourceList.size() == 0;
    }

    @Test
    void get_all_sources_returns_list_of_all_sources() {
        List<Source> expectedSourceList = createDummyListOfSources();
        when(mockedRepository.findAll()).thenReturn(expectedSourceList);
        List<Source> actualSourceList = controller.getAllSources();
        assertEquals(expectedSourceList, actualSourceList);
    }

    private List<Source> createDummyListOfSources() {
        Source loginAPI = new Source();
        loginAPI.setId(0);
        loginAPI.setName("Login API");
        loginAPI.setUrl("https://example.com/login-api");

        Source productAPI = new Source();
        productAPI.setId(1);
        productAPI.setName("Product API");
        productAPI.setUrl("https://example.com/product-api");

        Source locationAPI = new Source();
        locationAPI.setId(2);
        locationAPI.setName("Location API");
        locationAPI.setUrl("https://example.com/location-api");

        Source ecommerceApp = new Source();
        ecommerceApp.setId(3);
        ecommerceApp.setName("Ecommerce App");
        ecommerceApp.setUrl("https://example.com/ecommerce-app");

        List<Source> dummySourceList = new LinkedList<>();
        dummySourceList.add(loginAPI);
        dummySourceList.add(productAPI);
        dummySourceList.add(locationAPI);
        dummySourceList.add(ecommerceApp);

        return dummySourceList;
    }
}
