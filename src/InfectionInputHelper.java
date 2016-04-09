import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import java.util.HashSet;

public class InfectionInputHelper {
	/**
	 * 
	 * @param theFileName - Name of the file containing the input
	 * 
	 * This file parses the input file and creates the necessary relationship maps
	 */
	public static void parseInput(Infecter theInf, String fileName) throws IOException{
		BufferedReader br = null;
		String line;
		File file = new File(fileName);
		
		try {
			br = new BufferedReader(new FileReader(file));
			
			// Read a line, parse the line, create the relationships
			line = br.readLine();
			while (line != null ){
				parseLineAndAdd(theInf, line.trim());
				line = br.readLine();
			}
		}
	    finally {
	    	br.close();
		}
	}

	/**
	 * This method parses a line and adds the necessary objects to the Infection class.
	 * @param inf
	 * @param inStr
	 */
	private static void parseLineAndAdd(Infecter inf, String inStr){
		StringTokenizer strTok = new StringTokenizer(inStr, ",", false);
		
		// Get the first token
		String coachId = null;
		if ( strTok.hasMoreTokens() ) {
			coachId = strTok.nextToken().trim();  // First token is a potential coach id
		} else {
			// The line does not have any token. Error line.
			return ;
		}
		
		String version = null;
		// Get the next token (this is the version) 
		if ( strTok.hasMoreTokens() ) {
			version = strTok.nextToken().trim();  // Second token is version
			inf.addVersionToUser(Long.valueOf(coachId), version);
		} else {
			// The line has only one token.  This user may be an independent user.
			inf.addIndependentUser(Long.valueOf(coachId));
		}
		
		HashSet<Long> students = new HashSet<Long>();
		while (strTok.hasMoreTokens()){  // Each token is a student id
			String studentId = strTok.nextToken().trim();
			// Add student to coach relationship
			inf.addStudentToCoach(Long.valueOf(studentId), Long.valueOf(coachId));
			inf.addVersionToUser(Long.valueOf(studentId), version);
			// Add this student to the students list for this coach
			students.add(Long.valueOf(studentId));
		}
		// Add Coach to Students relationship
		inf.addCoachToStudent(Long.valueOf(coachId), students);
	}
}
