import java.io.InputStreamReader;
import java.io.BufferedReader;	
import java.util.LinkedList;
import java.util.Queue;
/**
 * 10033 - Interpreter
 * 
 * @date 26.12.2019
 * @author Samuel Keusch
 */
public class Main {
	
	public static final StringBuilder OUTPUT = new StringBuilder();
	
    public static void main(String[] cmdArgs) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			int testcaseNr = Integer.parseInt(reader.readLine().trim());
			reader.readLine(); // skip blank line
			
			int counter = 0; // testcase counter
			// each test case
			while(reader.ready()){
				if(counter != 0){
					OUTPUT.append(System.lineSeparator());
					// clear computer after each run
					RAM = new short[1000];
					REGISTERS = new int[10];
					CURRENT_RAM_LOC = 0;
				}
				
				// fill the RAM with instructions
				int i = 0;
				String line = reader.readLine().trim();
				// each ram instruction, until end of input or blank line
				while(reader.ready() && !line.equals("")){
					RAM[i++] = Short.parseShort(line);
					line = reader.readLine().trim(); // parse line id advance to be able to check in while-condition
				}
				
				// exec program and output number of executed instructions
				OUTPUT.append(execProgram());
				OUTPUT.append(System.lineSeparator());
				
				counter++;
				// output after last testcase
				if(counter == testcaseNr){
					System.out.print(OUTPUT);
					return;
				}
			}
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
	
	public static short[] RAM = new short[1000]; // the RAM
	public static int[] REGISTERS = new int[10]; // int for easier use
	public static int CURRENT_RAM_LOC = 0; // the current ram location (which is being executed)
	
	public static int execProgram(){
		int instrCounter = 0;
		while(CURRENT_RAM_LOC != -1){ // -1 is the marker for HALT
			exec(); // exec current command
			instrCounter++;
		}
		return instrCounter;
	}
	
	public static void exec(){
		short instr = RAM[CURRENT_RAM_LOC];
		int type = (instr / 100) % 10; // extract first digit
		int arg1 = (instr / 10) % 10; // extract second digit
		int arg2 = instr % 10; // extract third digit
		
		switch(type){
			case 0: // goto the location in register arg1 unless register arg2 contains 0
				if(REGISTERS[arg2] != 0){
					CURRENT_RAM_LOC = REGISTERS[arg1];
				} else {
					CURRENT_RAM_LOC++;
				}
				return;
			case 1: // halt
				CURRENT_RAM_LOC = -1; // -1 is the marker for HALT
				return;
			case 2: // set register arg1 to arg2 (between 0 and 9)
				REGISTERS[arg1] = arg2;
				CURRENT_RAM_LOC++;
				return;
			case 3: // add arg2 to register arg1
				REGISTERS[arg1] = (REGISTERS[arg1] + arg2) % 1000;
				CURRENT_RAM_LOC++;
				return;
			case 4: // multiply register arg1 by Arg2
				REGISTERS[arg1] = (REGISTERS[arg1] * arg2) % 1000;
				CURRENT_RAM_LOC++;
				return;
			case 5: // set register arg1 to the value of register arg2
				REGISTERS[arg1] = REGISTERS[arg2];
				CURRENT_RAM_LOC++;
				return;
			case 6: // add the value of register arg2 to register arg1
				REGISTERS[arg1] = (REGISTERS[arg1] + REGISTERS[arg2]) % 1000;
				CURRENT_RAM_LOC++;
				return;
			case 7: // multiply register arg1 by the value of register arg2
				REGISTERS[arg1] =  (REGISTERS[arg1] * REGISTERS[arg2]) % 1000;
				CURRENT_RAM_LOC++;
				return;
			case 8: // set register arg1 to the value in RAM whose address is in register arg2
				REGISTERS[arg1] = RAM[REGISTERS[arg2]];
				CURRENT_RAM_LOC++;
				return;
			case 9: // set the value in RAM whose address is in register arg2 to the value of register 1
				RAM[REGISTERS[arg2]] = (short) REGISTERS[arg1];
				CURRENT_RAM_LOC++;
				return;
			default:
				throw new RuntimeException("Unrecognized command");
		}
	}
}