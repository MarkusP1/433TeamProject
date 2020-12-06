import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException; 
import java.io.File;
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
		}

		public static ArrayList<String> collectInput(String[] args){
				ArrayList<String> totalInput = new ArrayList<String>();
				
				File inputFile = new File(args[0]);
				
				try{ 
				BufferedReader input = new BufferedReader(new FileReader(inputFile));
				String line = ""; 
				while((line = input.readLine()) != null){
					totalInput.add(line);
				}
				} catch(FileNotFoundException er){
					System.err.println("File not found.");
				} catch(IOException er){
					System.err.println("Unable to read file.");
				}
				return totalInput;
			}			
}