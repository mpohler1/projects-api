package com.masonpohler.api.projects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void get_all_projects_returns_empty_list_when_there_are_no_projects() {
        when(mockedRepository.findAll()).thenReturn(new LinkedList<Project>());
        List<Project> actualProjectList = controller.getAllProjects();
        assert actualProjectList.size() == 0;
    }

    @Test
    void get_all_projects_returns_all_projects() {
        List<Project> expectedProjectList = createDummyProjectList();
        when(mockedRepository.findAll()).thenReturn(expectedProjectList);
        List<Project> actualProjectList = controller.getAllProjects();
        assertEquals(expectedProjectList, actualProjectList);
    }

    @Test
    void get_project_by_id_throws_project_not_found_exception_when_project_not_in_repository() {
        Project dummyProject = createDummyProject();
        when(mockedRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThrows(ProjectNotFoundException.class, () -> controller.getProjectById(dummyProject.getId()));
    }

    @Test
    void get_project_by_id_returns_project_when_project_is_in_repository() {
        List<Project> dummyProjectList = createDummyProjectList();
        Project expectedProject = dummyProjectList.get(0);

        when(mockedRepository.findById(any(Long.class)))
                .thenAnswer((Answer<Optional<Project>>) invocationOnMock -> {
                    long id = invocationOnMock.getArgument(0);
                    Project project = findProjectInListById(dummyProjectList, id);
                    return Optional.ofNullable(project);
                });

        Project actualProject = controller.getProjectById(expectedProject.getId());
        assertEquals(expectedProject, actualProject);
    }

    @Test
    void create_project_returns_created_project() {
        Project expectedProject = createDummyProject();
        when(mockedRepository.save(any(Project.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());
        Project actualProject = controller.createProject(expectedProject);
        assertEquals(expectedProject, actualProject);
    }

    @Test
    void create_project_adds_project_to_repository() {
        Project dummyProject = createDummyProject();
        List<Project> dummyProjectList = createDummyProjectList();

        doAnswer((Answer<Project>) invocationOnMock -> {
            dummyProjectList.add(dummyProject);
            return dummyProject;
        }).when(mockedRepository).save(dummyProject);

        when(mockedRepository.findAll()).thenReturn(dummyProjectList);

        List<Project> expectedProjectList = new LinkedList<>(dummyProjectList);
        expectedProjectList.add(dummyProject);

        controller.createProject(dummyProject);
        List<Project> actualProjectList = mockedRepository.findAll();

        assertEquals(expectedProjectList, actualProjectList);
    }

    @Test
    void delete_project_removes_project_from_repository() {
        List<Project> dummyProjectList = createDummyProjectList();
        Project dummyProject = dummyProjectList.get(0);

        doAnswer((Answer<List<Project>>) invocationOnMock -> {
            dummyProjectList.remove(dummyProject);
            return dummyProjectList;
        }).when(mockedRepository).delete(dummyProject);

        when(mockedRepository.findAll()).thenReturn(dummyProjectList);

        List<Project> expectedProjectList = new LinkedList<>(dummyProjectList);
        expectedProjectList.remove(dummyProject);

        controller.deleteProject(dummyProject);
        List<Project> actualProjectList = mockedRepository.findAll();

        assertEquals(expectedProjectList, actualProjectList);
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

    private Project findProjectInListById(List<Project> projectList, long id) {
        for (Project project : projectList) {
            if (project.getId() == id) {
                return project;
            }
        }

        return null;
    }
}
