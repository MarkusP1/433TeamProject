Our system takes in ten command line inputs, in the following order:
1.) The input file
2.) int pen_coursemin,
3.) int pen_labsmin
4.) int pen_notpaired
5.) int pen_section
6.) float w_minfilled
7.) float w_pref
8.) float w_pair
9.) float w_secdiff
10.) boolean debugMode

'debugMode' tells the system if it should run in debug mode or not -- in debug mode,
it prints out its State along with various indicators to show its actions every time the State
changes.

Before running this system, place all of the enclosed files in a main directory and create 
two new directories in the main directory. One should be called 'inputs' and the 
other should be called 'outputs'.

Place input text files in the 'inputs' directory to enable our system to read them.

Once the system has been run with input file <filename>.txt, its output will appear in a 
text file called out_<filename>.txt in 'outputs'. You do not have to create or alter an existing 
file with this name for this to happen.

The system will periodically rewrite this output file as new, better solutions are found during
the search process.