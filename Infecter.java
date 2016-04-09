/**
 * 
 * @author Nitin M
 * Class to calculate infections for a given input set.
 *
 */
import java.util.HashMap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;
import java.util.Stack;

public class Infecter {
	private String fileName;  // Input file name is stored for reset purpose
	private ArrayList<Partition> allPartitions = new ArrayList<Partition>();  // Stores all partitions here
	
	// This HashMap stores Coach to Student Relationships (coaches relationship)
	private HashMap<Long, HashSet<Long>> coachToStudentsMap = new HashMap<Long, HashSet<Long>>();
	
	// This HashMap stores Student to Coach Relationships (coachedBy relationship)
	private HashMap<Long, HashSet<Long>> studentToCoachesMap = new HashMap<Long, HashSet<Long>>();
	
	private HashSet<Long> independentUsers = new HashSet<Long>();
	
	// Constructor
	public Infecter(String inputFileName){
		this.fileName = inputFileName;
		try {
		  InfectionInputHelper.parseInput(this, this.fileName);
		} catch (Exception e){
			System.out.println("Error occurred while parsing the input file: " + inputFileName + " " + e);
			System.exit(0);  // TODO:  Handle in a better way
		}
		
		// System.out.println("No of Elements in coachesMap " + this.coachToStudentsMap.size() + "\n" + coachToStudentsMap);
		// System.out.println("No of Elements in studentsMap " + this.studentToCoachesMap.size() + "\n" + studentToCoachesMap);
		// Create a temporary copy of the map to compute all partitions
		
		HashMap<Long, HashSet<Long>> copyOfCoachToStudentsMap = (HashMap<Long, HashSet<Long>>) coachToStudentsMap.clone();
		HashMap<Long, HashSet<Long>> copyOfStudentToCoachesMap = (HashMap<Long, HashSet<Long>>) studentToCoachesMap.clone();
		this.createAllPartitions();
		
		// System.out.println("No of Partitions: " + this.allPartitions.size());
		// Restore the old value to the HashMaps
		coachToStudentsMap = copyOfCoachToStudentsMap;
		studentToCoachesMap = copyOfStudentToCoachesMap;
		
		// System.out.println("After Restoring...");
		// System.out.println("No of Elements in coachesMap " + this.coachToStudentsMap.size() + "\n" + coachToStudentsMap);
		// System.out.println("No of Elements in studentsMap " + this.studentToCoachesMap.size() + "\n" + studentToCoachesMap);
	}

	/**
	 * Adds an independent user to the system after checking
	 * @param userId
	 */
	public void addIndependentUser(Long userId) {
		// See if this user is really an independent user
		if ( coachToStudentsMap.containsKey(userId) || studentToCoachesMap.containsKey(userId) ) {
			return;  // The user is not an independent user
		} else {
		    independentUsers.add(userId);	
		}
	}

	/**
	 * Adds student-coach relationship.  Takes care of removing them from independent users, if present.
	 * @param studentId
	 * @param coachId
	 */
	public void addStudentToCoach(Long studentId, Long coachId) {
		if ( coachId == null ){
			return;  // Invalid input.
		}
		
		// This is a really student-coach relationship.  
		// So if this student or coach had been marked as independent user, remove from the list.
		independentUsers.remove(studentId);
		independentUsers.remove(coachId);
		
		// Now add the student-coach relationship
		HashSet<Long> currCoaches = studentToCoachesMap.get(studentId);
		if ( currCoaches == null ){
			currCoaches = new HashSet<Long>();
		}
		currCoaches.add(coachId);  // TODO:  Check if this object has to be put back in the HashMap
		this.studentToCoachesMap.put(studentId, currCoaches);
	}

	public void addCoachToStudent(Long coachId, HashSet<Long> students) {
		if ( coachId == null || students == null ){
			return;  // Invalid input
		}
		
		// Remove the coach from independents list, if present.
		this.independentUsers.remove(coachId);
		
		// Remove all students from the independents list, if present.
		this.independentUsers.removeAll(students);
		
		// Now add the coach-student relationship
		HashSet<Long> currStudentsForCoach = this.coachToStudentsMap.get(coachId);
		if ( currStudentsForCoach == null ) {
			currStudentsForCoach = new HashSet<Long>();
		}
		currStudentsForCoach.addAll(students);
		this.coachToStudentsMap.put(coachId, currStudentsForCoach);
	}
	
	public Partition total_infection(Long userId, String version, boolean removeProcessed){
		if ( userId == null ){
			return null;  // Partition (or subgraph can't be created using null user
		}
		
		// Check if the user is an independent user
		if ( this.independentUsers.contains(userId) ){
			// Remove the user from the independents list
			//this.independentUsers.remove(userId);
			Partition p = new Partition(version);
			p.addUser(userId);
			return p;
		}
		
		HashSet<Long> inf_set = new HashSet<Long>();

		// Check if the user is a Coach
		if ( this.coachToStudentsMap.containsKey(userId) ) {
			// Add the user id to the inf_set
			inf_set.add(userId);
			// Add all the students to the inf_set
			inf_set.addAll(this.coachToStudentsMap.get(userId));
			// Remove this user from the HashMap as a partition is being created
			if ( removeProcessed ) {
				this.coachToStudentsMap.remove(userId);
			}
		}
		
		// Check if this user is a student
		if ( this.studentToCoachesMap.containsKey(userId) ){
		    // Add this user to inf_set
			inf_set.add(userId);
			// Add all the coaches for this user to the inf_set
			inf_set.addAll(this.studentToCoachesMap.get(userId));
			// Remove this user from the HashMap as a partition is being created
			if ( removeProcessed ) {
			    this.studentToCoachesMap.remove(userId);
			}
		}
		
		ArrayList<Long> toVisit = new ArrayList<Long>();
		HashSet<Long> visited = new HashSet<Long>();
		toVisit.addAll(inf_set);
		for(int i = 0; i < toVisit.size(); ++i){
			Long nextId = toVisit.get(i);
			if ( !visited.contains(nextId) ){
			  // Add all students for nextId to inf_set
			  HashSet<Long> theStudents = this.coachToStudentsMap.get(nextId);
			  if ( theStudents != null ) {
				  inf_set.addAll(theStudents);
				  // Add theStudents to toVisit list
				  toVisit.addAll(theStudents);
			  }

			  // Remove from coach-student map
				if ( removeProcessed ) {
					this.coachToStudentsMap.remove(nextId);
				}
			  
			  // Add all coaches for this nextId
			  HashSet<Long> theCoaches = this.studentToCoachesMap.get(nextId);
			  if ( theCoaches != null ) {
				  inf_set.addAll(theCoaches);
				  // Add theCoaches to the toVisit list
				  toVisit.addAll(theCoaches);
			  }

			  // Remove from student-coach map
				if ( removeProcessed ) {
					this.studentToCoachesMap.remove(nextId);
				}
			  visited.add(nextId);
			}
		}
		
		// Create a new Partition
		Partition p = new Partition(version);
		p.addUsers(inf_set);
		return p;
	}
	
	private void createAllPartitions(){
		// Create partitions using the coachToStudents map first
		Set<Long> keys = this.coachToStudentsMap.keySet();
        while ( keys.size() > 0) {
        	Long[] userIds = keys.toArray(new Long[1]);
        	Partition p = total_infection(userIds[0], "1.0", true);
        	if ( p != null ){
        		this.allPartitions.add(p);
        	}
        	keys = this.coachToStudentsMap.keySet();
        }
        
        // Create partitions using studentToCoaches map next 
        keys = this.studentToCoachesMap.keySet();
        while ( keys.size() > 0) {
        	Long[] userIds = keys.toArray(new Long[1]);
        	Partition p = total_infection(userIds[0], "1.0", true);
        	if ( p != null ){
        		this.allPartitions.add(p);
        	}
        	keys = this.studentToCoachesMap.keySet();
        }
        
        // Create partitions for individuals
        Iterator<Long> iter = this.independentUsers.iterator();
        while (iter.hasNext()){
        	this.allPartitions.add(this.total_infection(iter.next(), "1.0", true));
        }
        sortAllPartitionsDesc();
    }
	
	private void sortAllPartitionsDesc(){
		int noOfParts = this.allPartitions.size();
		for(int i = 0; i < noOfParts; ++i){
			for(int j = i+1; j < noOfParts; ++j){
				if ( allPartitions.get(i).getSize() < allPartitions.get(j).getSize() ){
					Partition temp = allPartitions.get(i);
					allPartitions.set(i, allPartitions.get(j));
					allPartitions.set(j, temp);
				}
			}
		}
	}
	
	private ArrayList<Partition> collectPartitionsWithSizeLTE(long n){
		ArrayList<Partition> parts = new ArrayList<Partition>();
		int noOfPartitions = this.allPartitions.size();
		int pos = 0;
		for(pos = 0; pos < noOfPartitions; pos++){
			if (this.allPartitions.get(pos).getSize() <= n){
				break;
			}
		}

		for(int i = pos; i < noOfPartitions; ++i){
			parts.add(allPartitions.get(i));
		}
		return parts;
	}
	
	/**
	 * Recursive method to calculate partitions
	 * @param totalReq
	 * @param tolerance
	 * @param remReq
	 * @param parts
	 * @param solStack
	 * @return
	 */
	private boolean findPartitions(long totalReq, int tolerance, long remRequired, ArrayList<Partition> parts, 
			                       Stack<Partition> solStack){
		// First check if the partitions have enough users to cover the required number of users
		if ( !haveEnoughUsers(parts, remRequired-tolerance) ){
			return false;
		}
		
		// Add the first partition to the solution stack and see if it meets the requirement
		int noOfParts = parts.size();
		int i = 0;
		while (i < noOfParts){
			solStack.push(parts.get(i));
			long noOfUsersSoFar = calculateTotal(solStack); 
			if( (totalReq-tolerance <= noOfUsersSoFar) && (noOfUsersSoFar <= totalReq + tolerance) ) return true;
			ArrayList<Partition> reducedPartitions = reducePartitions(parts, totalReq-noOfUsersSoFar+tolerance, i+1);
			if ( findPartitions(totalReq, tolerance, remRequired - parts.get(i).getSize(), reducedPartitions, solStack) ) {
				return true;
			} else {
				solStack.pop();
			}
			i++;
		}
		return false;
	}
	
	private ArrayList<Partition> reducePartitions(ArrayList<Partition> parts, long maxSize, int startIndex) {
		ArrayList<Partition> temp = new ArrayList<Partition>();
		int noOfParts = parts.size();
		for(int i = startIndex; i < noOfParts; ++i){
			if ( parts.get(i).getSize() <= maxSize ){
				temp.add(parts.get(i)) ;
			}
		}
		return temp;
	}

	/**
	 * Calculates the total number of users in the solution stack.
	 * @param solutionStack
	 * @return
	 */
	private long calculateTotal(Stack<Partition> solutionStack){
		if (solutionStack.isEmpty()) return 0;
		int noOfParts = solutionStack.size();
		long sum = 0;
		for(int i = 0; i < noOfParts; ++i){
			sum = sum + solutionStack.get(i).getSize();
		}
		return sum;
	}
	
	/**
	 * Checks if the Partitions list has enough number of users to meet the required size
	 * @param parts
	 * @return
	 */
	private boolean haveEnoughUsers(ArrayList<Partition> parts, long reqSize){
		long sum = 0;
		int noOfParts = parts.size();
		for(int i = 0; i < noOfParts; ++i){
			sum = sum + parts.get(i).getSize();
			if ( sum >= reqSize) {
				return true;
			}
		}
		return false;
	}
	
	public Partition[] limited_infection(long n, int tolerance, String version){
		// this.createAllPartitions();  // Already done
		ArrayList<Partition> suitableParts = collectPartitionsWithSizeLTE(n+tolerance);
		Stack<Partition> solStack = new Stack<Partition>();
		boolean foundSolution = findPartitions(n, tolerance, n, suitableParts, solStack);
		if ( foundSolution ){
			for(Partition p : solStack) {
				p.setVersion(version);
			}
			return solStack.toArray(new Partition[0]);
		} else {
			return null;
		}
	}
	
	public Partition[] exact_infection(int n, String version) {
		return limited_infection(n, 0, version);
	}
	
	public static void main(String args[]){
		Infecter infecter = new Infecter("users.txt");
		System.out.println("____________________________________________");
		
		Partition p1 = infecter.total_infection(new Long(5), "2.0", false);
		System.out.println("total_infection(5) " + p1);
		
		System.out.println("____________________________________________");

		Partition p2 = infecter.total_infection(new Long(31), "2.0", false);
		System.out.println("total_infection(31) " + p2);
		
		System.out.println("____________________________________________");
		
		Partition[] pArr = infecter.limited_infection(5, 1, "2.0");
		System.out.println("limited_infection(5,1)");
		if(pArr != null) {
			for(Partition pElem : pArr)
				System.out.println("The Users using: " + pElem);
		}
		System.out.println("____________________________________________");
		
		Partition p3 = infecter.total_infection(new Long(36), "2.0", false);
		System.out.println("total_infection(36) " + p3);
		System.out.println("____________________________________________");
		
		pArr = infecter.limited_infection(12, 1, "2.0");
		System.out.println("limited_infection(12,1)");
		if(pArr != null) {
			for(Partition pElem : pArr)
				System.out.println("The Users using: " + pElem);
		}
		System.out.println("____________________________________________");
		
		pArr = infecter.limited_infection(5, 0, "2.0");
		System.out.println("limited_infection(5,0)");
		if(pArr != null) {
			for(Partition pElem : pArr)
				System.out.println("The Users using: " + pElem);
		}
		System.out.println("____________________________________________");
		
		pArr = infecter.limited_infection(3, 0, "2.0");
		System.out.println("limited_infection(3,0)");
		if(pArr != null) {
			for(Partition pElem : pArr)
				System.out.println("The Users using: " + pElem);
		}
		System.out.println("____________________________________________");
		
		Partition p4 = infecter.total_infection(new Long(27), "2.0", false);
		System.out.println("total_infection(27) " + p4);
		System.out.println("____________________________________________");
		
		pArr = infecter.limited_infection(1, 1, "2.0");
		System.out.println("limited_infection(1,1)");
		if(pArr != null) {
			for(Partition pElem : pArr)
				System.out.println("The Users using: " + pElem);
		}
		System.out.println("____________________________________________");
		
		pArr = infecter.limited_infection(4, 1, "2.0");
		System.out.println("limited_infection(4,1)");
		if(pArr != null) {
			for(Partition pElem : pArr)
				System.out.println("The Users using: " + pElem);
		}
		System.out.println("____________________________________________");
	}
}
