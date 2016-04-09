# ProjectInfection

## Contact Information
* Nitin Manivasagan <nitinmani@berkeley.edu>

#### Detailed documentation for all three tasks is in the pdf. Please look through the `InfectionDocumentation.pdf` for a detailed explanation of how I have implemented the different types of infections. 
#### A brief overview of each function is given at the end of this documentation.

### How To Run 
  * Compile all the files in the `src` folder. 
  * Then run this command `java Infecter txtFile`, where `txtFile` is a relative path to file of users. Each line of that file should be formatted as such:  
   * [UserID][Version][StudentId1][StudentId2]...[StudentIdN]
  * All of the ids must be long values
  * After doing so, you will get prompted with the type of infection you want to perform, and depending on the infection, you will get prompted with other needed information. It should be very self explanatory once you type in the command, on what you will need to input.
  * I have added a folder with sample files that you can use to test the algorithm
 
#### Total Infection
* This is a slightly modified DFS algorithm, but essentially iterates through the connected component the user belongs to and infects everyone in it.

#### Limited Infection
* I interpretted Limited Infection as an infection where if the user inputs `numToInfect` number of users and a tolerance `tolerance`,  the algorithm will succeed only if it can infect anywhere from `numToInfect - tolerance` to `numToInfect + tolerance` number of users. 
* To do this, I implemented a slightly modified version of the knapsack algorithm.

#### Exact Infection
* Exact Infection is the same as Limited Infection with `tolerance` equal to 0.

###### Note: For testing, I created sample user files and ran each one manually, using many different commands to test the algorithms. Some of these files are in the `sample_user_data` file
