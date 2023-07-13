package projects.service;

import java.util.List;
import java.util.NoSuchElementException;

import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

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

	public void modifyProjectDetails(Project project) {
		//if modification is not successful
		if(!projectDao.modifyProjectDetails(project)) {
			throw new DbException("Project with ID=" + project.getProjectId()+ " does not exist.");
		}
		
	}

	public void deleteProject(Integer projectId) {
		//if deletion is not successful
		if(!projectDao.deleteProject(projectId)) {
			throw new DbException("Project with ID=" + projectId+ " does not exist.");
		}
				
		
	}

}
