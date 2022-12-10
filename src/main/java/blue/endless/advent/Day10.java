package blue.endless.advent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class Day10 {
	public static final String SAMPLE_INPUT = """
			noop
			addx 3
			addx -5
			""";

	
	public void run() {
		//run(AdventOfCode.processSampleInput(SAMPLE_INPUT_2));
		
		try {
			run(Files.readAllLines(Path.of("data", "day10.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(List<String> input) {
		CRT crt = new CRT();
		crt.loadProgram(input);
		
		ArrayList<Integer> signals = new ArrayList<>();
		int period = 40;
		int target = 20;
		//System.out.println("Booting. X="+crt.getX());
		
		while(crt.isRunning()) {
			crt.cycle();
			if(crt.clocksExecuted == target) {
				
				signals.add(crt.getX() * target);
				
				target += period;
			}
			if (crt.isRunning()) System.out.println(crt.clocksExecuted+": Running, X="+crt.getX());
		}
		
		System.out.println(crt.clocksExecuted+": Halted. X="+crt.getX());
		System.out.println(signals);
		int sum = signals.stream().collect(Collectors.summingInt(it->it));
		System.out.println("Sum: "+sum);
	}
	
	public void runPartB() {
		//runPartB(AdventOfCode.processSampleInput(SAMPLE_INPUT_2));
		
		try {
			runPartB(Files.readAllLines(Path.of("data", "day10.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void runPartB(List<String> input) {
		CRT crt = new CRT();
		crt.loadProgram(input);
		
		while(crt.isRunning()) {
			crt.cycle();
		}
		System.out.println(crt.clocksExecuted+": Halted.");
		System.out.println();
		crt.printScreen();
	}
	
	private static void addx(CRT host, String line) {
		String[] parts = line.split("\\s");
		if (parts.length!=2) host.halt("addx: Can't parse argument from '"+line+"'");
		int arg = Integer.parseInt(parts[1]);
		host.setX(host.getX() + arg);
	}
	
	public static class CRT {
		protected List<String> program = new ArrayList<>();
		protected String[] screen = new String[6];
		boolean running = false;
		protected int x = 1;
		protected int programCounter = 0;
		protected int clocksExecuted = 0;
		protected int clocksRemaining = 1;
		protected String instruction = "noop";
		protected BiConsumer<CRT, String> instructionCode = (self,s)->{};
		
		protected HashMap<String, BiConsumer<CRT, String>> opcodes = new HashMap<>();
		protected HashMap<String, Integer> opcodeTimings = new HashMap<>();
		
		public CRT() {
			addOpcode("noop", (self,s)->{}, 1);
			addOpcode("addx", Day10::addx, 2);
		}
		
		public void loadProgram(List<String> program) {
			this.program = program;
			reset();
			running = true;
		}
		
		public void reset() {
			x = 1;
			programCounter = 0;
			clocksExecuted = 0;
			clocksRemaining = 1;
			instruction = "boot";
			instructionCode = (self,s)->{};
			for(int i=0; i<screen.length; i++) {
				screen[i] = "";
			}
		}
		
		public void addOpcode(String opcode, BiConsumer<CRT, String> code, int timing) {
			opcodes.put(opcode, code);
			opcodeTimings.put(opcode, timing);
		}
		
		public int getX() {
			return x;
		}
		
		public void setX(int value) {
			this.x = value;
		}
		
		public void printScreen() {
			for(String s : screen) {
				System.out.println(s);
			}
		}
		
		public boolean isRunning() {
			return running;
		}
		
		public void halt(String error) {
			instruction = "halt";
			instructionCode = (self,s)->{};
			clocksRemaining = Integer.MAX_VALUE;
			running = false;
			System.out.println(error);
		}
		
		public void cycle() {
			
			clocksRemaining--;
			if (clocksRemaining<=0) {
				//System.out.println(clocksExecuted+": "+instruction);
				instructionCode.accept(this, instruction);
				
				if (programCounter>=program.size()) {
					halt("Program ended successfully.");
					//Don't return out of this, we want to fall through to increase clocksExecuted
				} else {
					instruction = program.get(programCounter);
					programCounter++;
					String[] parts = instruction.split("\\s");
					instructionCode = opcodes.get(parts[0]);
					if (instructionCode==null) {
						halt("Unknown opcode: "+parts[0]);
						return;
					}
					Integer clocks = opcodeTimings.get(parts[0]);
					if (clocks==null) {
						halt("No opcode timings provided for: "+parts[0]);
						return;
					}
					clocksRemaining = clocks;
				}
			}
			
			clocksExecuted++;
			
			//update scanline
			int y = (clocksExecuted-1) / 40;
			int x = (clocksExecuted-1) % 40;
			
			if (y>=0 && y<screen.length) {
				if (x==0) screen[y] = "";
				boolean spriteHit = Math.abs(getX()-x) <= 1;
				screen[y] = screen[y] + ((spriteHit) ? '#' : '.');
			}
		}
	}
	
	public static String SAMPLE_INPUT_2 = """
			addx 15
			addx -11
			addx 6
			addx -3
			addx 5
			addx -1
			addx -8
			addx 13
			addx 4
			noop
			addx -1
			addx 5
			addx -1
			addx 5
			addx -1
			addx 5
			addx -1
			addx 5
			addx -1
			addx -35
			addx 1
			addx 24
			addx -19
			addx 1
			addx 16
			addx -11
			noop
			noop
			addx 21
			addx -15
			noop
			noop
			addx -3
			addx 9
			addx 1
			addx -3
			addx 8
			addx 1
			addx 5
			noop
			noop
			noop
			noop
			noop
			addx -36
			noop
			addx 1
			addx 7
			noop
			noop
			noop
			addx 2
			addx 6
			noop
			noop
			noop
			noop
			noop
			addx 1
			noop
			noop
			addx 7
			addx 1
			noop
			addx -13
			addx 13
			addx 7
			noop
			addx 1
			addx -33
			noop
			noop
			noop
			addx 2
			noop
			noop
			noop
			addx 8
			noop
			addx -1
			addx 2
			addx 1
			noop
			addx 17
			addx -9
			addx 1
			addx 1
			addx -3
			addx 11
			noop
			noop
			addx 1
			noop
			addx 1
			noop
			noop
			addx -13
			addx -19
			addx 1
			addx 3
			addx 26
			addx -30
			addx 12
			addx -1
			addx 3
			addx 1
			noop
			noop
			noop
			addx -9
			addx 18
			addx 1
			addx 2
			noop
			noop
			addx 9
			noop
			noop
			noop
			addx -1
			addx 2
			addx -37
			addx 1
			addx 3
			noop
			addx 15
			addx -21
			addx 22
			addx -6
			addx 1
			noop
			addx 2
			addx 1
			noop
			addx -10
			noop
			noop
			addx 20
			addx 1
			addx 2
			addx 2
			addx -6
			addx -11
			noop
			noop
			noop
			""";
}
