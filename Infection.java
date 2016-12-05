import java.util.List; 
import java.util.Map; 
import java.util.HashMap; 
import java.util.ArrayList; 
import java.util.Scanner;
import java.util.Queue; 
import java.util.ArrayDeque; 
import java.util.Arrays;
import java.util.Collections;

/**
 * @author  Rohan Dhoopar
 * @version 1.0
 *
 * Models the deployment of new KA features as an "infection" affecting a graph of users
 */
public class Infection {

	/**
	 * Maps user names to User objects
	 */
	private Map<String, User> users; 
	/**
	 * List of teachers, where a "teacher" is a User with >1 adjacent users
	 */
	private List<User> teachers;
	/**
	 * List of class sizes, where a "class" is a connected component of the graph
	 */
	private List<Integer> classSizes;

	/**
	 * Initial version of KA that every user views (prior to any infections)
	 */
	private static final String INIT_VERSION = "V1"; 
	/**
	 * Default infected value for all users
	 */
	private static final boolean INIT_INFECTED = false; 

	/** Constructor */
	public Infection() {
		//Initialize instance variables
		this.users = new HashMap<String, User>(); 
		this.teachers = new ArrayList<User>(); 
		this.classSizes = new ArrayList<Integer>(); 

		//Initialize a scanner
		Scanner sc = new Scanner(System.in); 

		//Read until out of input
		while (sc.hasNext()) {
			//Each classroom (i.e. a teacher followed by students) is on a separate line
			String[] classroom = sc.nextLine().split(",");

			//Create the teacher from first string and its list of students that will become its adjacents field
			User teacher = new User(classroom[0], INIT_VERSION, INIT_INFECTED, null); 
			List<User> students = new ArrayList<User>(); 

			//Create students out of the rest of the line
			for (int i = 1; i < classroom.length; i++) {
				//A student's only adjacent will be the teacher
				List<User> adjacents = new ArrayList<User>(); 
				adjacents.add(teacher); 

				//Instantiate student and add to map
				User student = new User(classroom[i], INIT_VERSION, INIT_INFECTED, adjacents);
				users.put(classroom[i], student); 

				//Add this student to the teacher's adjacency list
				students.add(student); 
			}

			//Wrap up by finishing the teacher and storing its relevant info in the instance variables
			teacher.setAdjacents(students); 
			users.put(classroom[0], teacher); 
			teachers.add(teacher); 
			classSizes.add(classroom.length); 
		}
	}

	/**
	 * Run a total infection on a connected component of the user graph to simulate feature deployment
	 * 
	 * @param patientZero The user to start the infection (i.e. first to receive the feature)
	 * @param newVersion  The name of the new version of KA these users will be seeing
	 */
	public void total_infection(User patientZero, String newVersion) {
		//Handle null patientZero
		if (patientZero == null) return; 

		/**
		 * Store the infected value of patient zero. The infection will spread by updating those that
		 * have not been touched yet
		 */
		boolean notTouched = patientZero.infected(); 

		//Infect patient zero
		patientZero.flipInfected(); 
		patientZero.setVersion(newVersion); 

		//Infect all connected users using a BFS -- guaranteed to reach entirety of the connected component
		Queue<User> queue = new ArrayDeque<User>(); 
		queue.add(patientZero); 
		while (!queue.isEmpty()) {
			User current = queue.poll(); 
			for (User adjacent : current.adjacents()) {

				//Update if not touched
				if (adjacent.infected() == notTouched) {

					//Then mark as touched by flipping infected value
					adjacent.flipInfected();

					adjacent.setVersion(newVersion); 
					queue.add(adjacent); 
				}
			}
		}
	}

	/**
	 * Run a limited infection, infecting close to a given number of users
	 * 
	 * @param limit      Max number of users to be infected
	 * @param newVersion The name of the new version of KA these users will be seeing
	 */
	public void limited_infection(int limit, String newVersion) {
		//Handle non-positive limit
		if (limit <= 0) return;  

		/**
		 * Get the teachers whose sum of classroom sizes are as close to but less than limit
		 * (i.e. the knapsack problem)
		 */
		List<User> teachersToInfect = knapsack(limit); 

		/**
		 * Infect the smallest classroom if limit is smaller than the smallest classroom and
		 * is closer to the size of the smallest classroom than to 0
		 */
		if (teachersToInfect.isEmpty()) {
			int min = Collections.min(classSizes); 
			if (min-limit <= limit) {
				total_infection(teachers.get(classSizes.indexOf(min)), newVersion); 
			}
		}

		//Else run total infections starting with all the teachers to infect
		else {
			for (User teacher : teachersToInfect) {
				total_infection(teacher, newVersion); 
			}
		}
	}

	/**
	 * Determines the classrooms to infect by modelling it as the knapsack problem (i.e. get
	 * the teachers whose sum of classroom sizes are as close to but less than W
	 * 
	 * @param  W 				Upper bound on number to infect
	 * @return infectedTeachers List of teachers who teach the classrooms to be infected
	 */
	private List<User> knapsack(int W) {
		int[] sizes = listToArray(classSizes);  
		int N = sizes.length; 
		int[][] matrix = new int[N+1][W+1]; 

		//Basic solution to knapsack problem using DP
		for (int i = 0; i < N+1; i++) {
			for (int j = 0; j < W+1; j++) {
				if (i == 0 || j == 0) {
					matrix[i][j] = 0; 
				}
				else if (sizes[i-1] <= j) { 
					matrix[i][j] = Math.max(sizes[i-1] + matrix[i-1][j-sizes[i-1]], matrix[i-1][j]); 
				}
				else {
					matrix[i][j] = matrix[i-1][j]; 
				}
			}
		}

		//Work backwards through the matrix to create the list of teachers to infect
		List<User> teachersToInfect = new ArrayList<User>(); 
		for (int i = N, j = W; i > 0; i--) {
			if (j >= sizes[i-1]) {
				if (matrix[i][j] == sizes[i-1] + matrix[i-1][j-sizes[i-1]]) {
					/**
					 * Key is that although the matrix consists of integer classroom sizes, the
					 * teacher at teachers[i] is the corresponding teacher user with that sized class
					 */
					teachersToInfect.add(teachers.get(i-1)); 
					j -= sizes[i-1]; 
				}
			}
		}		

		return teachersToInfect; 
	}

	/**
	 * Converts a list of Integers to an int[] array
	 * 
	 * @param  list   A list of Integers
	 * @return result Those integers as an int[] array
	 */
	private int[] listToArray(List<Integer> list) {
		int[] result = new int[list.size()]; 
		for (int i = 0; i < result.length; i++) {
			result[i] = list.get(i); 
		}
		return result; 
	}

	/**
	 * Run a limited infection that infects exactly the given number of users (without
	 * regards to whether that will leave connected components only partially infected, 
	 * but proceeding by infecting one connected component in full then moving on to
	 * another)
	 * 
	 * @param  limit 	  Exact max number of users to be infected
	 * @param  newVersion The name of the new version of KA these users will be seeing
	 * @return boolean 	  Whether or not the exact infection was successful
	 */
	public boolean limited_infection_exact(int limit, String newVersion) {
		//Handle negative limit
		if (limit < 0) return false;  

		//Do nothing if limit is 0
		if (limit == 0) return true; 

		//Exact infection is unsuccessful iff there are fewer users than the limit
		if (limit > users.values().size()) return false; 

		//Infect users until this count reaches the limit
		int count = 0; 

		/**
		 * Infect by starting at each teacher -- each teacher is part of a separate connected
		 * component (i.e. a classroom)
		 */
		for (User teacher : teachers) {
			teacher.setVersion(newVersion); 
			count++; 
			if (count == limit) break; 

			//The teacher's adjacents will comprise the entirety of the connected component
			for (User student : teacher.adjacents()) {
				student.setVersion(newVersion); 
				count++; 
				if (count == limit) return true; 
			}
		}

		return true; 
	}

	/** users getter */ 
	public Map<String, User> users() {
		return this.users; 
	}

	/** teachers getter */ 
	public List<User> teachers() {
		return this.teachers; 
	}

	/** classSizes getter */ 
	public List<Integer> classSizes() {
		return this.classSizes(); 
	}

	/** Print every user in the graph */ 
	public void print() {
		for (User user : users.values()) {
			System.out.println(user); 
		}
	}

	/* Tests the Infection class */ 
	public static void main(String[] args) {
		//Create Infection instance
		Infection infection = new Infection(); 

		/**
		 * Test total_infection with V2
		 *
		 * You can replace "I" with any valid user name of your choice ("valid" means the
		 * name appears in the input file, but if it doesn't, total_infection has bad parameter
		 * handling)
		 */
		infection.total_infection(infection.users().get("I"), "V2"); 

		/**
		 * Test limited_infection with V3
		 *
		 * You can replace 10 with any number of your choice
		 */
		infection.limited_infection(10, "V3"); 

		/**
		 * Test limited_infection_exact with V4
		 *
		 * You can replace 5 with any number of your choice
		 */
		infection.limited_infection_exact(4, "V4");

		//Print the user graph
		infection.print(); 
	}
}