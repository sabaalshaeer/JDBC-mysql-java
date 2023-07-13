package projects.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbException;
import provided.util.DaoBase;


//this class that will read and write to the MySQL database
public class ProjectDao extends DaoBase {

//we will write the values that were collected from the user and that are contained in a Project object
//to the project table using JDBC method calls.

	// declare constants for values that are used over and over
	private static final String CATEGORY_TABLE = "category";
	private static final String MATERIAL_TABLE = "material";
	private static final String PROJECT_TABLE = "project";
	private static final String PROJECT_CATEGORY_TABLE = "project_category";
	private static final String STEP_TABLE = "step";

	// this method to save the project details
	// 1st, create the SQL statement.
	// 2nd,obtain a Connection and start a transaction.
	// 3rd,obtain a PreparedStatement and set the parameter values from the Project
	// object.
	// 4th, save the data and commit the transaction.
	public Project insertProject(Project project) {
		// write sql statement
		// @Formatter:off
		String sql = "" + "INSERT INTO " + PROJECT_TABLE + " "
				+ "(project_name, estimated_hours, actual_hours, difficulty, notes) " + "VALUES " + "(?, ?, ?, ?, ?)";
		// @Formatter:on

		// try-with-resource
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				// set project details as parameters in the PreparedStatement
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);

				// save the project details
				stmt.executeUpdate();

				// calling the convenience method to allows to us to generate the projectId
				Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
				commitTransaction(conn);

				// set projectId
				project.setProjectId(projectId);
				return project;

			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	public List<Project> fetchAllProjects() {
		// write sql statement

		String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER By project_name";

		// try-with-resource
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				try (ResultSet rs = stmt.executeQuery()) {
					List<Project> projects = new LinkedList<>();

					while (rs.next()) {
						// projects.add(extract(rs, Project.class));

						Project project = new Project(rs.getInt("project_id"), rs.getString("project_name"),
								rs.getBigDecimal("estimated_hours"), rs.getBigDecimal("actual_hours"),
								rs.getInt("difficulty"), rs.getString("notes"));

						projects.add(project);

//						Project project = new Project();
//						project.setActualHours(rs.getBigDecimal("actual_hours"));
//						project.setDifficulty(rs.getObject("difficulty", Integer.class));
//						project.setEstimatedHours(rs.getBigDecimal("estimatedHours"));
//						project.setNotes(rs.getString("notes"));
//						project.setProjectId(rs.getObject("projectId", Integer.class));
//						project.setProjectName(rs.getString("projectName"));
//						projects.add(project);

					}
					return projects;
				}

			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException(e);
		}
	}

	//
	public Optional<Project> fetchProjectById(Integer project_id) {
		String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";
		try (Connection conn = DbConnection.getConnection()) {
			startTransaction(conn);
			try {// create project object
				Project project = null;
				// start getting the project details
				try (PreparedStatement stmt = conn.prepareStatement(sql)) {
					// setParameter(stmt, 1, project_id, Integer.class);
					stmt.setInt(1, project_id);
					try (ResultSet rs = stmt.executeQuery()) {
						if (rs.next()) {
							project = extract(rs, Project.class);
						}
					}
				}
				// get materials,steps and category using addAll because each method returns a
				// list
				if (Objects.nonNull(project)) {
					project.getMaterials().addAll(fetchProjectMaterials(conn, project_id));
					project.getSteps().addAll(fetchProjectSteps(conn, project_id));
					project.getCategories().addAll(fetchProjectCategories(conn, project_id));

				}
				commitTransaction(conn);

				return Optional.ofNullable(project);
			} catch (Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
		} catch (SQLException e) {
			throw new DbException();
		}
	}

	private List<Category> fetchProjectCategories(Connection conn, Integer project_id) throws SQLException {
		// @formatter:off
		String sql = ""
		+ "SELECT c. * FROM "+ CATEGORY_TABLE + " c "
		+ "JOIN "+ PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
		+ "WHERE project_id = ?";
		// @formatter:on

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, project_id);
			// setParameter(stmt, 1, project_id, Integer.class);
			try (ResultSet rs = stmt.executeQuery()) {
				List<Category> categories = new LinkedList<>();

				while (rs.next()) {
					Category category = extract(rs, Category.class);
//					for(Category category: categories) {
//						System.out.println(category);
//					}

					if (category != null) {
						categories.add(category);
					}
				}

				return categories;

				// Check if categories is null before invoking toArray()
				// return categories != null ? categories : Collections.emptyList();
			}
		}

	}

	private List<Step> fetchProjectSteps(Connection conn, Integer project_id) throws SQLException {

		String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, project_id);
			// setParameter(stmt, 1, project_id, Integer.class);
			try (ResultSet rs = stmt.executeQuery()) {
				List<Step> steps = new LinkedList<>();

				while (rs.next()) {
					steps.add(extract(rs, Step.class));
				}

				return steps;

			}
		}
	}

	private List<Material> fetchProjectMaterials(Connection conn, Integer project_id) throws SQLException {
		String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";

		try (PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setInt(1, project_id);
			// setParameter(stmt, 1, project_id, Integer.class);
			try (ResultSet rs = stmt.executeQuery()) {
				List<Material> materials = new LinkedList<>();

				while (rs.next()) {
					materials.add(extract(rs, Material.class));
				}

				return materials;

			}
		}
	}

	public boolean modifyProjectDetails(Project project) {
		// @formatter:off
		String sql =""
				+ "UPDATE "+ PROJECT_TABLE+ " SET "
				+"project_name = ?, "
				+"estimated_hours =?, "
				+"actual_hours = ?, "
				+"difficulty = ?, "
				+"notes =? "
				+"WHERE project_id = ?";
		//@formatter:on
		
		//get connection
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			
			//set parameters
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				setParameter(stmt, 1, project.getProjectName(), String.class);
				setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
				setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
				setParameter(stmt, 4, project.getDifficulty(), Integer.class);
				setParameter(stmt, 5, project.getNotes(), String.class);
				setParameter(stmt, 6, project.getProjectId(), Integer.class);
				
				//if updating works successfully we get 1 
				boolean updatedProject = stmt.executeUpdate() == 1;
				commitTransaction(conn);
				
				return updatedProject;
				
				
			}catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
			
		}catch(SQLException e) {
			throw new DbException(e);
		}
	}

	public boolean deleteProject(Integer projectId) {
		//sql statement
		String sql ="DELETE FROM "+ PROJECT_TABLE+ " WHERE project_id = ?";
		
		//connection
		try(Connection conn = DbConnection.getConnection()){
			startTransaction(conn);
			//prepared statement and set the parameter
			try(PreparedStatement stmt = conn.prepareStatement(sql)){
				setParameter(stmt, 1, projectId, Integer.class);
				
				//if updating works successfully we get 1 
				boolean deletedProject = stmt.executeUpdate() == 1;
				commitTransaction(conn);
				
				return deletedProject;
				
			}catch(Exception e) {
				rollbackTransaction(conn);
				throw new DbException(e);
			}
			
		}catch(SQLException e) {
			throw new DbException(e);
		}
	}

}
