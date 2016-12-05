# Infection

User.java models a user. 

Infection.java models the deployment of new software features to students and teachers at a school as an "infection" spreading through a graph of users. I considered being “infected” as being upgraded to the version of the feature given in the function call (presumably the latest). 

Infection reads in data from a .csv file (really any comma separated text file) and assumes the data is formatted as such: each line represents a separate classroom, where the first String in the line is the classroom’s teacher and all other Strings in the line are the students. A sample test file, school.csv, is provided. 

To compile: `javac Infection.java`

To run: `java Infection < school.csv`

Infection unit tests its important methods. The output should be:

`My name is D and I am on version V4`

`My name is E and I am on version V3`

`My name is F and I am on version V3`

`My name is G and I am on version V3`

`My name is A and I am on version V4`

`My name is B and I am on version V4`

`My name is C and I am on version V4`

`My name is L and I am on version V3`

`My name is M and I am on version V3`

`My name is N and I am on version V3`

`My name is O and I am on version V3`

`My name is H and I am on version V3`

`My name is I and I am on version V2`

`My name is J and I am on version V2`

`My name is K and I am on version V2`

`My name is Q and I am on version V3`

`My name is P and I am on version V3`

The ordering of lines may be different but the letter names and versions should match the above text. This output has been verified for correctness. 

At a high level, the total_infection method works by running BFS on the component of the graph connected to the given user. The limited_infection method works by modeling the problem as the knapsack problem then running total_infection on the teachers whose classroom sizes are returned in the knapsack problem solution. The limited_infection_exact method works by keeping a count of infected users and stopping when that count reaches the limit (without regards to whether that will leave connected components only partially infected, but proceeding by infecting one connected component in full then moving on to another). 

Built for the Khan Academy “Infection” project-based interview. 
