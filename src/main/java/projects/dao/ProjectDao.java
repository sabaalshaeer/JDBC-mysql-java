package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import projects.entity.Project;
import projects.exception.DbException;
import provided.util.DaoBase;

//this class that will read and write to the MySQL database
public class ProjectDao extends DaoBase {
	
//we will write the values that were collected from the user and that are contained in a Project object
//to the project table using JDBC method calls.
	
	//declare constants for values that are used over and over
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";

	//this method to save the project details
	//1st, create the SQL statement. 
	//2nd,obtain a Connection and start a transaction. 
	//3rd,obtain a PreparedStatement and set the parameter values from the Project object. 
	//4th, save the data and commit the transaction. 
	public Project insertProject(Project project) {
		//write sql statement
		// @Formatter:off
		String sql = ""
				+"INSERT INTO "+ PROJECT_TABLE+ " "
				+"(project_name, estimated_hours, actual_hours, difficulty, notes) "
				+"VALUES "
				+"(?, ?, ?, ?, ?)";
		// @Formatter:on

		//try-with-resource
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				//set project details as parameters in the PreparedStatement
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				
				//save the project details
				stmt.executeUpdate();
				
				//calling the convenience method to allows to us to generate the projectId
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn);
				
				//set projectId
				project.setProjectId(projectId);
				return project;

			}catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		}catch(SQLException e) {
			throw new DbException(e);
		}
	}
	

}
