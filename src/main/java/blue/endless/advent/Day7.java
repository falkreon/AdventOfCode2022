package blue.endless.advent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day7 {
	public static String SAMPLE_INPUT = """
			$ cd /
			$ ls
			dir a
			14848514 b.txt
			8504156 c.dat
			dir d
			$ cd a
			$ ls
			dir e
			29116 f
			2557 g
			62596 h.lst
			$ cd e
			$ ls
			584 i
			$ cd ..
			$ cd ..
			$ cd d
			$ ls
			4060174 j
			8033020 d.log
			5626152 d.ext
			7214296 k
			""";
	
	public void run() {
		//run(AdventOfCode.processSampleInput(SAMPLE_INPUT));
		
		try {
			run(Files.readAllLines(Path.of("data", "day7.txt")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void run(List<String> input) {
		DirectoryNode root = DirectoryNode.fromTraversal(input);
		
		System.out.println(root);
		
		ArrayList<DirectoryNode> smallNodes = new ArrayList<>();
		int sizeLimit = 100_000;
		if (root.size() <= sizeLimit) smallNodes.add(root);
		addNodes(root, sizeLimit, smallNodes);
		System.out.println(smallNodes);
		
		int totalSize = smallNodes.stream().filter((it)->it.type==NodeType.DIRECTORY).collect(Collectors.summingInt(DirectoryNode::size));
		System.out.println(totalSize);
	}
	
	private void addNodes(DirectoryNode localRoot, int sizeLimit, List<DirectoryNode> list) {
		for(DirectoryNode n : localRoot.children) {
			if (n.size() <= sizeLimit) list.add(n);
			addNodes(n, sizeLimit, list);
		}
	}
	
	public void runPartB() {
		
	}
	
	public void runPartB(List<String> input) {
		
	}
	
	public static enum NodeType {
		FILE,
		DIRECTORY,
		UNKNOWN;
	}
	
	public static class DirectoryNode {
		public String name;
		public NodeType type = NodeType.UNKNOWN;
		public int size = -1;
		public List<DirectoryNode> children = new ArrayList<>();
		public DirectoryNode parent = null;
		
		public DirectoryNode(String name) {
			this.name = name;
		}
		
		public int size() {
			if (this.type==NodeType.FILE) return size;
			
			int result = 0;
			for(DirectoryNode n : children) {
				result += n.size();
			}
			return result;
		}
		
		public String list() {
			String result = "- "+this.name+" (";
			switch(type) {
				case UNKNOWN -> result = result + "unknown)";
				case FILE -> result = result + "file, size="+size+")";
				case DIRECTORY -> {
					if (size==-1) {
						result = result + "dir)";
					} else {
						result = result + "dir, estimatedSize="+size+")";
					}
					
				}
			}
			
			for(DirectoryNode node : children) {
				String childLine = node.toString();
				String[] parts = childLine.split("\\n");
				for(String part : parts) {
					result = result + "\n  " + part;
				}
			}
			
			return result;
		}
		
		@Override
		public String toString() {
			String result = this.name+" (";
			switch(type) {
				case UNKNOWN -> result = result + "unknown)";
				case FILE -> result = result + "file, size="+size+")";
				case DIRECTORY -> result = result + "dir)";
			}
			return result;
		}
		
		public DirectoryNode ensureSubdir(String name) {
			for(DirectoryNode n : children) {
				if (n.name.equals(name)) return n;
			}
			
			DirectoryNode subdir = new DirectoryNode(name);
			subdir.type = NodeType.DIRECTORY;
			subdir.parent = this;
			this.children.add(subdir);
			return subdir;
		}
		
		public DirectoryNode ensureFile(String name, int size) {
			for(DirectoryNode n : children) {
				if (n.name.equals(name)) return n;
			}
			
			DirectoryNode file = new DirectoryNode(name);
			file.type = NodeType.FILE;
			file.parent = this;
			file.size = size;
			this.children.add(file);
			
			if (this.size==-1) {
				this.size = size;
			} else {
				this.size += size;
			}
			
			return file;
		}
		
		public static DirectoryNode fromTraversal(List<String> input) {
			DirectoryNode result = new DirectoryNode("/");
			result.type = NodeType.DIRECTORY;
			
			DirectoryNode curNode = result;
			for(String s : input) {
				if (s.startsWith("$ cd ")) {
					String to = s.substring(5);
					if (to.equals("/")) {
						System.out.println("Going to root");
						curNode = result;
					} else if (to.equals("..")) {
						System.out.println("Going up a level...");
						curNode = curNode.parent;
					} else {
						System.out.println("Switching to \""+to+"\"");
						DirectoryNode switchTo = curNode.ensureSubdir(to);
						curNode = switchTo;
					}
					
				} else if (s.equals("$ ls")) {
					//Do nothing; we'll be capturing output
				} else if (s.startsWith("$")) {
					System.out.println("Unknown command: "+s);
				} else {
					//This is output information about curNode, probably from $ ls
					
					if (s.startsWith("dir ")) {
						String dirName = s.substring(4);
						
						curNode.ensureSubdir(dirName);
					} else {
						String[] parts = s.split("\\s");
						if (parts.length==2) {
							int size = Integer.parseInt(parts[0]);
							String name = parts[1];
							System.out.println("Recording file: "+name);
							curNode.ensureFile(name, size);
						} else {
							System.out.println(s);
							System.out.println(Arrays.toString(parts));
						}
					}
				}
			}
			
			return result;
		}
	}
}
