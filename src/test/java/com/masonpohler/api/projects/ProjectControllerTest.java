package com.masonpohler.api.projects;

import com.masonpohler.api.source.Source;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

class ProjectControllerTest {

    @Mock
    private ProjectRepository mockedRepository;

    @InjectMocks
    private ProjectController controller;

    @BeforeEach
    void set_up() {
        MockitoAnnotations.initMocks(this);
    }

    // getAllProjects

    @Test
    void get_all_projects_returns_empty_list_when_there_are_no_projects() {
        mockFindAll(new LinkedList<>());
        List<Project> actualProjectList = controller.getAllProjects();
        assert actualProjectList.size() == 0;
    }

    @Test
    void get_all_projects_returns_all_projects() {
        List<Project> expectedProjectList = createDummyProjectList();
        mockFindAll(expectedProjectList);
        List<Project> actualProjectList = controller.getAllProjects();
        assertEquals(expectedProjectList, actualProjectList);
    }

    // getProjectById

    @Test
    void get_project_by_id_throws_project_not_found_exception_when_project_not_in_repository() {
        mockFindById(new LinkedList<>());
        assertThrows(ProjectNotFoundException.class, () -> controller.getProjectById(0));
    }

    @Test
    void get_project_by_id_does_not_throw_exception_when_project_is_in_repository() {
        List<Project> dummyProjectList = createDummyProjectList();
        mockFindById(dummyProjectList);

        Project project = dummyProjectList.get(0);
        assertDoesNotThrow(() -> controller.getProjectById(project.getId()));
    }

    @Test
    void get_project_by_id_returns_project_when_project_is_in_repository() {
        List<Project> dummyProjectList = createDummyProjectList();
        mockFindById(dummyProjectList);

        Project expectedProject = dummyProjectList.get(0);
        Project actualProject = controller.getProjectById(expectedProject.getId());
        assertEquals(expectedProject, actualProject);
    }

    // createProject

    @Test
    void create_project_returns_created_project() {
        mockSave(new LinkedList<>());
        Project expectedProject = createDummyProject();
        Project actualProject = controller.createProject(expectedProject);
        assertEquals(expectedProject, actualProject);
    }

    @Test
    void create_project_adds_project_to_repository() {
        List<Project> dummyProjectList = createDummyProjectList();
        mockFindAll(dummyProjectList);
        mockSave(dummyProjectList);

        Project dummyProject = createDummyProject();

        List<Project> expectedProjectList = new LinkedList<>(dummyProjectList);
        expectedProjectList.add(dummyProject);

        controller.createProject(dummyProject);
        List<Project> actualProjectList = mockedRepository.findAll();

        assertEquals(expectedProjectList, actualProjectList);
    }

    // addSourceToProject

    @Test
    void add_source_to_project_throws_project_not_found_exception_when_project_is_not_in_repository() {
        mockFindById(new LinkedList<>());

        Project dummyProject = createDummyProject();
        Source dummySource = createDummySource();

        assertThrows(ProjectNotFoundException.class, () -> controller.addSourceToProject(dummySource, dummyProject.getId()));
    }

    @Test
    void add_source_to_project_does_not_throw_exception_when_project_with_id_is_found() {
        List<Project> dummyProjectList = createDummyProjectList();
        mockFindById(dummyProjectList);

        Project project = dummyProjectList.get(0);
        Source source = createDummySource();
        assertDoesNotThrow(() -> controller.addSourceToProject(source, project.getId()));
    }

    @Test
    void add_source_to_project_that_already_has_source_returns_project() {
        List<Project> dummyProjectList = createDummyProjectList();
        mockFindById(dummyProjectList);
        mockSave(dummyProjectList);

        Source dummySource = createDummySource();
        Project expectedProject = dummyProjectList.get(0);
        expectedProject.getSources().add(dummySource);

        Project actualProject = controller.addSourceToProject(dummySource, expectedProject.getId());

        assertEquals(expectedProject, actualProject);
    }

    @Test
    void add_source_to_project_adds_source_to_project() {
        List<Project> dummyProjectList = createDummyProjectList();
        mockFindById(dummyProjectList);
        mockSave(dummyProjectList);

        Source dummySource = createDummySource();

        Project project = controller.addSourceToProject(dummySource, dummyProjectList.get(0).getId());

        assert project.getSources().contains(dummySource);
    }

    // removeSourceFromProject

    @Test
    void remove_source_from_project_throws_project_not_found_exception_when_project_not_in_repository() {
        mockFindById(new LinkedList<>());

        Project dummyProject = createDummyProject();
        Source dummySource = createDummySource();
        assertThrows(ProjectNotFoundException.class, () -> controller.removeSourceFromProject(dummySource, dummyProject.getId()));
    }

    @Test
    void remove_source_from_project_does_not_throw_exception_when_project_is_in_repository() {
        List<Project> dummyProjectList = createDummyProjectList();
        mockFindById(dummyProjectList);

        Project project = dummyProjectList.get(0);
        Source source = createDummySource();
        assertDoesNotThrow(() -> controller.removeSourceFromProject(source, project.getId()));
    }

    @Test
    void remove_source_from_project_returns_project_when_source_was_not_in_project_source_list() {
        List<Project> dummyProjectList = createDummyProjectList();
        mockFindById(dummyProjectList);
        mockSave(dummyProjectList);

        List<Source> dummySourceList = createDummySourceList();
        Project expectedProject = dummyProjectList.get(0);
        expectedProject.setSources(new HashSet<>(dummySourceList));

        Source dummySource = createDummySource();
        Project actualProject = controller.removeSourceFromProject(dummySource, expectedProject.getId());

        assertEquals(expectedProject, actualProject);
    }

    @Test
    void remove_source_from_project_removes_source_from_project() {
        List<Project> dummyProjectList = createDummyProjectList();
        mockFindById(dummyProjectList);
        mockSave(dummyProjectList);

        List<Source> dummySourceList = createDummySourceList();
        Project expectedProject = dummyProjectList.get(0);
        expectedProject.setSources(new HashSet<>(dummySourceList));

        Source dummySource = dummySourceList.get(0);
        Project actualProject = controller.removeSourceFromProject(dummySource, expectedProject.getId());

        assertEquals(expectedProject, actualProject);
    }

    // deleteProject

    @Test
    void delete_project_removes_project_from_repository() {
        List<Project> dummyProjectList = createDummyProjectList();
        mockFindAll(dummyProjectList);
        mockDelete(dummyProjectList);

        Project dummyProject = dummyProjectList.get(0);

        List<Project> expectedProjectList = new LinkedList<>(dummyProjectList);
        expectedProjectList.remove(dummyProject);

        controller.deleteProject(dummyProject);
        List<Project> actualProjectList = mockedRepository.findAll();

        assertEquals(expectedProjectList, actualProjectList);
    }

    // helper functions

    private void mockFindAll(List<Project> projectList) {
        when(mockedRepository.findAll()).thenReturn(projectList);
    }

    private void mockFindById(List<Project> projectList) {
        when(mockedRepository.findById(any(Long.class)))
                .thenAnswer(invocationOnMock -> {
                    long id = invocationOnMock.getArgument(0);
                    Project foundProject = findProjectInListById(projectList, id);
                    return Optional.ofNullable(foundProject);
                });
    }

    private void mockSave(List<Project> projectList) {
        when(mockedRepository.save(any(Project.class)))
                .thenAnswer((Answer<Project>) invocationOnMock -> {
                    Project project = invocationOnMock.getArgument(0);
                    projectList.add(project);
                    return project;
                });
    }

    private void mockDelete(List<Project> projectList) {
        doAnswer((Answer<List<Project>>) invocationOnMock -> {
            Project project = invocationOnMock.getArgument(0);
            projectList.remove(project);
            return projectList;
        }).when(mockedRepository).delete(any(Project.class));
    }

    private Project createDummyProject() {
        Project dummyProject = new Project();
        dummyProject.setId(3);
        dummyProject.setName("Projects API");
        dummyProject.setDescription("An API for Projects.");
        dummyProject.setDetailedDescription("An API for storing information on all your Projects.");
        dummyProject.setPreviewURL("https://example.com/projects-api/image.jpg");
        dummyProject.setLiveURL("https://example.com/projects-api");
        return dummyProject;
    }

    private List<Project> createDummyProjectList() {
        Project meatbol = new Project();
        meatbol.setId(0);
        meatbol.setName("Meatbol Interpreter");
        meatbol.setDescription("Meatbol Interpreter.");
        meatbol.setDetailedDescription("An interpreter for the Meatbol language.");
        meatbol.setPreviewURL("https://example.com/meatbol/image.jpg");
        meatbol.setLiveURL("https://example.com/meatbol");

        Project pcWonder = new Project();
        pcWonder.setId(1);
        pcWonder.setName("PC Wonder");
        pcWonder.setDescription("Ecommerce Website.");
        pcWonder.setDetailedDescription("An ecommerce website for computer hardware.");
        pcWonder.setPreviewURL("https://example.com/pc-wonder/image.jpg");
        pcWonder.setLiveURL("https://example.com/pc-wonder");

        Project portfolio = new Project();
        portfolio.setId(2);
        portfolio.setName("Portfolio");
        portfolio.setDescription("A Portfolio.");
        portfolio.setDetailedDescription("This is my Portfolio.");
        portfolio.setPreviewURL("https://example.com/portfolio/image.jpg");
        portfolio.setLiveURL("https://example.com/portfolio");

        List<Project> dummyProjectsList = new LinkedList<>();
        dummyProjectsList.add(meatbol);
        dummyProjectsList.add(pcWonder);
        dummyProjectsList.add(portfolio);

        return dummyProjectsList;
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

    private Project findProjectInListById(List<Project> projectList, long id) {
        for (Project project : projectList) {
            if (project.getId() == id) {
                return project;
            }
        }

        return null;
    }
}
