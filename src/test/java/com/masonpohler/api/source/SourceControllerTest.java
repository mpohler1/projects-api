package com.masonpohler.api.source;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
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
        List<Source> expectedSourceList = createDummySourceList();
        when(mockedRepository.findAll()).thenReturn(expectedSourceList);
        List<Source> actualSourceList = controller.getAllSources();
        assertEquals(expectedSourceList, actualSourceList);
    }

    @Test
    void create_source_returns_created_source() {
        Source expectedSource = createDummySource();
        when(mockedRepository.save(any(Source.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
        Source actualSource = controller.createSource(expectedSource);
        assertEquals(expectedSource, actualSource);
    }

    @Test
    void create_source_adds_source_to_repository() {
        Source dummySource = createDummySource();
        List<Source> dummySourceList = createDummySourceList();

        doAnswer((Answer<Source>) invocationOnMock -> {
            Source source = invocationOnMock.getArgument(0);
            dummySourceList.add(source);
            return source;
        }).when(mockedRepository).save(any(Source.class));

        when(mockedRepository.findAll()).thenReturn(dummySourceList);

        List<Source> expectedSourceList = new LinkedList<>(dummySourceList);
        expectedSourceList.add(dummySource);

        controller.createSource(dummySource);
        List<Source> actualSourceList = mockedRepository.findAll();

        assertEquals(expectedSourceList, actualSourceList);
    }

    @Test
    void delete_source_removes_source_from_repository() {
        List<Source> dummySourceList = createDummySourceList();
        Source dummySource = dummySourceList.get(0);

        doAnswer((Answer<List<Source>>) invocationOnMock -> {
            Source source = invocationOnMock.getArgument(0);
            dummySourceList.remove(source);
            return dummySourceList;
        }).when(mockedRepository).delete(any(Source.class));

        when(mockedRepository.findAll()).thenReturn(dummySourceList);

        List<Source> expectedSourceList = new LinkedList<>(dummySourceList);
        expectedSourceList.remove(dummySource);

        controller.deleteSource(dummySource);
        List<Source> actualSourceList = mockedRepository.findAll();

        assertEquals(expectedSourceList, actualSourceList);
    }

    private Source createDummySource() {
        Source dummySource = new Source();
        dummySource.setId(4);
        dummySource.setName("Dummy Source");
        dummySource.setUrl("https://example.com/dummy-source");
        return dummySource;
    }

    private List<Source> createDummySourceList() {
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
