import java.io.*;
import java.util.ArrayList;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReaderThing {
	public static void main(String args[]){
		ArrayList<String> input = collectInput(args); 
		int i = 0;
		while((!input.isEmpty())&&(i < input.size())){
			System.out.println(input.get(i));
			i += 1;
		}
		i=0;
		
	}

	public static ArrayList<String> collectInput(String[] args){
		ArrayList<String> totalInput = new ArrayList<String>();		
		//File inputFile = new File(args[0]);//args[0] in brackets
		try{ 
		BufferedReader input = new BufferedReader(new FileReader("inputs/input.txt")); //should look something like "inputs/" + inputFile
		String line = ""; 
		while((line = input.readLine()) != null){
			totalInput.add(line);
		}
		input.close();
		} catch(FileNotFoundException er){
			System.err.println("File not found.");
		} catch(IOException er){
			System.err.println("Unable to read file.");
		}
		return totalInput;
	}

	//write methods that take the individual pieces of the input and do things with them
	//creat slot objects for courses
	public void writeCourseSlots() {

	}

	//create slot objects for labs
	public void writeLabSlots() {

	}

	//create course objects for lectures
	public void writeCourses() {

	}

	//create course objects for labs
	public void writeLabs() {

	}

	//Note: we have not decided what to do about the following yet, however they seem to be mostly pairs of classes and slots, and pairs of classes
	//Idea: class and slot pairs with positive score/attribute = preferences, pairs with maxed out score = partial assignments, negative score = unwanted
	//Idea: course pairs with positive score = pairs, course pairs with negative score = Not Compatible

	//create whatever we're using to store non compatible classes
	public void writeNotCompatible() {

	}

	//create whatever we're using to create unwanted class and slot pairs
	public void writeUnwanted() {

	}

	//create whatever we're using to store wanted class and slot pairs
	public void writePreferences() {

	}

	//create something for course pairs
	public void writePair() {

	}

	//create something for partial assignments of courses and slots
	public void writePartialAssignment() {

	}
}