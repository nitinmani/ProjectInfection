# ProjectInfection

## Contact Information
* Nitin Manivasagan <nitinmani@berkeley.edu>

#### Detailed documentation for all three tasks is in the pdf. Please look through the `InfectionDocumentation.pdf` for a detailed explanation of how I have implemented the different types of infections. 
#### A brief overview of each function is given at the end of this documentation.

### How To Run 
  * Compile all the files in the `src` directory. 
  * Then run this command `java Infecter txtFile`, where `txtFile` is a relative path to file of users. Each line of that file should be formatted as such:  
   * [UserID],[Version],[StudentId1],[StudentId2],...,[StudentIdN]
   * The above line signifies that a user with `UserID` uses version `Version`, and coaches students with student Ids `StudentId1` to `StudentIdN`
   * Obviously, this means that all the students operate on the same version, and I am reasonably making the assumption that there will be no inconsisties in the input file which cause the students to operate on a different version.
  * All of the ids must be long values
  * After doing so, you will get prompted with the type of infection you want to perform, and depending on the infection, you will get prompted with other needed information. It should be very self explanatory once you type in the command, on what you will need to input.
  * I have added a folder with sample files that you can use to test the algorithm
 
#### Total Infection
* This is a slightly modified DFS algorithm, but essentially iterates through the connected component the user belongs to and infects everyone in it.

#### Limited Infection
* I decided that it is imperative to maintain the condition that everyone in the connected component of a user should have the same version, so there will be no connected componenents where users within a connected component use different versions of the website.
* I interpretted Limited Infection as an infection which if the user inputs `numToInfect` number of users and a tolerance `tolerance`,  the algorithm will succeed (an infect the users) only if it can infect anywhere from `numToInfect - tolerance` to `numToInfect + tolerance` number of users, maintaining the condition stated in the first bullet point. Limited infection will fail if it cannot find a group of connected components that meet that requirement.
* To implement this algorithm, I coded up a slightly modified (and optimized) version of the knapsack algorithm.

#### Exact Infection
* Exact Infection is the same as Limited Infection with `tolerance` equal to 0.

##### Additional Thoughts:
* Getting a visualization would have been really cool to do. For now, every time the user wants to run an infection algorithm, he/she can see a crude visualization of the current state. My algorithm prints out the userId to version mapping before an algorithm is run (to see the current state), and I also print out the infected users and their new versions after running the algorithm.
* Obviously, the number of Khan Academy users is too large to be listed in just a file. If I had more time (and more resources), I would have also tried to use JDBC templates to try to store the information in a database after the user runs the algorithms. 

###### Note: For testing, I created sample user files and ran each one manually, using many different commands to test the algorithms. Some of these files are in the `sample_user_data` directory.
