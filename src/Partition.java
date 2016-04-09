/**
 * Represents a partition of users that are connected
 * @author Nitin M
 *
 */
import java.util.HashSet;

public class Partition {
	private HashSet<Long> users = new HashSet<Long>();
	private String usingVersion = "";
	
	public Partition(String version){
		this.usingVersion = version;
	}
	
	public void addUser(Long userId){
		this.users.add(userId);
	}
	
	public void addUsers(HashSet<Long> theUsers){
		this.users.addAll(theUsers);
	}
	
	public int getSize(){
		return this.users.size();
	}
	
	public HashSet<Long> getUsers() {
		return this.users;
	}
	
	public String getVersion(){
		return this.usingVersion;
	}
	
	public void setVersion(String newVersion) {
		this.usingVersion = newVersion;
	}
	
	public String toString(){
		return "Version: " + this.usingVersion + "\n" + this.users.toString();
	}
}
