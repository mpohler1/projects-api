package com.masonpohler.api.source;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    // getAllSources

    @Test
    void get_all_sources_returns_an_empty_list_when_no_sources_are_in_repository() {
        mockFindAll(new LinkedList<>());
        List<Source> actualSourceList = controller.getAllSources();
        assert actualSourceList.size() == 0;
    }

    @Test
    void get_all_sources_returns_list_of_all_sources() {
        List<Source> expectedSourceList = createDummySourceList();
        mockFindAll(expectedSourceList);

        List<Source> actualSourceList = controller.getAllSources();
        assertEquals(expectedSourceList, actualSourceList);
    }

    // getSourceById

    @Test
    void get_source_by_id_throws_source_not_found_exception_when_source_not_in_repostiory() {
        mockFindById(new LinkedList<>());
        assertThrows(SourceNotFoundException.class, () -> controller.getSourceById(0));
    }

    @Test
    void get_source_by_id_returns_source_when_source_is_in_repository() {
        List<Source> dummySourceList = createDummySourceList();
        mockFindById(dummySourceList);

        Source expectedSource = dummySourceList.get(0);
        Source actualSource = controller.getSourceById(expectedSource.getId());
        assertEquals(expectedSource, actualSource);
    }

    // createSource

    @Test
    void create_source_returns_created_source() {
        List<Source> dummySourceList = createDummySourceList();
        mockSave(dummySourceList);

        Source expectedSource = createDummySource();
        Source actualSource = controller.createSource(expectedSource);
        assertEquals(expectedSource, actualSource);
    }

    @Test
    void create_source_adds_source_to_repository() {
        List<Source> dummySourceList = createDummySourceList();
        mockFindAll(dummySourceList);
        mockSave(dummySourceList);

        Source dummySource = createDummySource();
        List<Source> expectedSourceList = new LinkedList<>(dummySourceList);
        expectedSourceList.add(dummySource);

        controller.createSource(dummySource);
        List<Source> actualSourceList = mockedRepository.findAll();

        assertEquals(expectedSourceList, actualSourceList);
    }

    // deleteSource

    @Test
    void delete_source_removes_source_from_repository() {
        List<Source> dummySourceList = createDummySourceList();
        mockFindAll(dummySourceList);
        mockDelete(dummySourceList);

        Source dummySource = dummySourceList.get(0);
        List<Source> expectedSourceList = new LinkedList<>(dummySourceList);
        expectedSourceList.remove(dummySource);

        controller.deleteSource(dummySource);
        List<Source> actualSourceList = mockedRepository.findAll();

        assertEquals(expectedSourceList, actualSourceList);
    }

    // helper functions

    void mockFindAll(List<Source> sourceList) {
        when(mockedRepository.findAll()).thenReturn(sourceList);
    }

    void mockFindById(List<Source> sourceList) {
        when(mockedRepository.findById(any(Long.class)))
                .thenAnswer((Answer<Optional<Source>>) invocationOnMock -> {
                    long id = invocationOnMock.getArgument(0);
                    Source source = findSourceInListById(sourceList, id);
                    return Optional.ofNullable(source);
                });
    }

    void mockSave(List<Source> sourceList) {
        when(mockedRepository.save(any(Source.class)))
                .thenAnswer((Answer<Source>) invocationOnMock -> {
                    Source source = invocationOnMock.getArgument(0);
                    sourceList.add(source);
                    return source;
                });
    }

    void mockDelete(List<Source> sourceList) {
        doAnswer((Answer<List<Source>>) invocationOnMock -> {
            Source source = invocationOnMock.getArgument(0);
            sourceList.remove(source);
            return sourceList;
        }).when(mockedRepository).delete(any(Source.class));
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

    private Source findSourceInListById(List<Source> sourceList, long id) {
        for (Source source : sourceList) {
            if (source.getId() == id) {
                return source;
            }
        }

        return null;
    }
}
