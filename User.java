import java.util.List;

/**
 * @author  Rohan Dhoopar (rdhoopar@princeton.edu)
 * @version 1.0
 *
 * Representation of a KA user
 */
public class User {
	/**
	 * A user's name
	 */
	private String name; 
	/**
	 * The current version a user is on
	 */
	private String version; 
	/**
	 * Whether or not the user has been "infected" with an update
	 */
	private boolean infected; 
	/**
	 * List of other users that are connected to the user by a coaching relationship
	 */
	private List<User> adjacents; 

	/** Constructor */
	public User(String name, String version, boolean infected, List<User> adjacents) {
		this.name = name; 
		this.version = version; 
		this.infected = infected; 
		this.adjacents = adjacents; 
	}

	/** name getter */
	public String name() {
		return this.name; 
	}

	/** name setter */
	public void setName(String name) {
		this.name = name; 
	}

	/** version getter */ 
	public String version() {
		return this.version; 
	}

	/** version setter */ 
	public void setVersion(String version) {
		this.version = version; 
	}

	/** infected getter */ 
	public boolean infected() {
		return this.infected; 
	}

	/** infected setter */ 
	public void setInfected(boolean infected) {
		this.infected = infected; 
	}

	/** flips (sets to opposite) user's infected status */
	public void flipInfected() {
		this.infected = !this.infected; 
	}

	/* adjacents getter */ 
	public List<User> adjacents() {
		return this.adjacents; 
	}

	/* adjacents setter */ 
	public void setAdjacents(List<User> adjacents) {
		this.adjacents = adjacents; 
	}

	/** String representation of the user, used for testing/debugging */ 
	public String toString() {
		return "My name is " + this.name + " and I am on version " + this.version;  
	}
}