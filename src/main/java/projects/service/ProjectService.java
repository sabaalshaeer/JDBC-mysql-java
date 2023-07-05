package projects.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import projects.dao.ProjectDao;
import projects.entity.Project;

//this file acts as a pass-through between the main application file that runs the menu
//(Main.java) and the DAO file in the data layer (ProjectDao.java).
public class ProjectService {
	//inject ProjectDao 
	private ProjectDao projectDao = new ProjectDao();

	public Project addProject(Project project) {
		return projectDao.insertProject(project);
	}

	public List<Project> fetchAllProjects() {
		return projectDao.fetchAllProjects();
	}

	public Project fetchProjectById(Integer project_id) {
		return projectDao.fetchProjectById(project_id)
				.orElseThrow(() -> new NoSuchElementException("Project with Id = " + project_id + "does not exist.")) ;
		
		}

}
