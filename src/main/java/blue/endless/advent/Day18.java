package blue.endless.advent;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import blue.endless.advent.util.ArrayGrid3d;
import blue.endless.advent.util.Day;
import blue.endless.advent.util.Grid3d;
import blue.endless.advent.util.Vec3i;

public class Day18 extends Day {

	@Override
	public void partOne(List<String> input) {
		Set<Vec3i> blocks = new HashSet<>();
		for(String s : input) {
			String[] parts = s.split(",");
			blocks.add(new Vec3i(
					Integer.parseInt(parts[0])-1,
					Integer.parseInt(parts[1])-1,
					Integer.parseInt(parts[2])-1
					));
		}
		
		int surface = 0;
		for(Vec3i vec : blocks) {
			if (!blocks.contains(new Vec3i(vec.x(), vec.y(), vec.z()-1))) surface++;
			if (!blocks.contains(new Vec3i(vec.x(), vec.y(), vec.z()+1))) surface++;
			if (!blocks.contains(new Vec3i(vec.x(), vec.y()-1, vec.z()))) surface++;
			if (!blocks.contains(new Vec3i(vec.x(), vec.y()+1, vec.z()))) surface++;
			if (!blocks.contains(new Vec3i(vec.x()-1, vec.y(), vec.z()))) surface++;
			if (!blocks.contains(new Vec3i(vec.x()+1, vec.y(), vec.z()))) surface++;
		}
		
		System.out.println("Surface area: "+surface);
	}

	@Override
	public void partTwo(List<String> input) {
		Set<Vec3i> blocks = new HashSet<>();
		
		int xsize = 0;
		int ysize = 0;
		int zsize = 0;
		for(String s : input) {
			String[] parts = s.split(",");
			Vec3i vec = new Vec3i(
					Integer.parseInt(parts[0])-1,
					Integer.parseInt(parts[1])-1,
					Integer.parseInt(parts[2])-1
					);
			blocks.add(vec);
			xsize = Math.max(xsize, vec.x()+1);
			ysize = Math.max(ysize, vec.y()+1);
			zsize = Math.max(zsize, vec.z()+1);
		}
		xsize+= 2;
		ysize+= 2;
		zsize+= 2;
		
		ArrayGrid3d<Material> droplet = new ArrayGrid3d<>(xsize, ysize, zsize, Material.AIR);
		for(Vec3i vec : blocks) {
			droplet.set(vec.x()+1, vec.y()+1, vec.z()+1, Material.OBSIDIAN);
		}
		
		System.out.println("Immersing the droplet in water / steam");
		//Floodfill in from every empty edge space
		for(int z=0; z<zsize; z++) {
			for(int y=0; y<ysize; y++) {
				for(int x=0; x<xsize; x++) {
					if (x==0 || y==0 || z==0 || x==xsize-1 || y==ysize-1 || z==zsize-1) {
						flood(droplet, new Vec3i(x, y, z));
					}
				}
			}
		}
		
		droplet.setDefault(Material.STEAM); //Now the droplet is surrounded by steam/water instead of air
		
		/*
		int airCount = 0;
		int obsidianCount = 0;
		int steamCount = 0;
		for(int z=0; z<zsize; z++) {
			for(int y=0; y<ysize; y++) {
				for(int x=0; x<xsize; x++) {
					Material cur = droplet.get(x,y,z);
					if (cur==Material.AIR) {
						airCount++;
						System.out.println("Air exists at "+x+","+y+","+z);
					}
					if (cur==Material.STEAM) steamCount++;
					if (cur==Material.OBSIDIAN) obsidianCount++;
				}
			}
		}
		System.out.println("Complete. Steam: "+steamCount+" Air: "+airCount+" Obsidian: "+obsidianCount);
		System.out.println("Size: "+xsize+"x"+ysize+"x"+zsize);
		*/
		
		
		System.out.println("Getting surface area...");
		
		int surface = 0;
		for(int z=0; z<zsize; z++) {
			for(int y=0; y<ysize; y++) {
				for(int x=0; x<xsize; x++) {
					Material cur = droplet.get(x,y,z);
					if (cur==Material.OBSIDIAN) {
						if (droplet.get(x-1, y, z) == Material.STEAM) surface++;
						if (droplet.get(x+1, y, z) == Material.STEAM) surface++;
						if (droplet.get(x, y-1, z) == Material.STEAM) surface++;
						if (droplet.get(x, y+1, z) == Material.STEAM) surface++;
						if (droplet.get(x, y, z-1) == Material.STEAM) surface++;
						if (droplet.get(x, y, z+1) == Material.STEAM) surface++;
					}
				}
			}
		}
		
		System.out.println("Diagnostics:");
		for(int z=0; z<zsize; z++) {
			System.out.println("=== Slice at z="+z+" ===");
			for(int y=0; y<ysize; y++) {
				StringBuilder line = new StringBuilder("  ");
				for(int x=0; x<xsize; x++) {
					Material cur = droplet.get(x,y,z);
					line.append(switch(cur) {
						case AIR -> '.';
						case STEAM -> '@';
						case OBSIDIAN -> '#';
					});
				}
				System.out.println(line);
			}
		}
		
		System.out.println("Surface area: "+surface);
		//NOTE: 2558 is incorrect
	}
	
	public static enum Material {
		AIR,
		STEAM,
		OBSIDIAN;
	}
	
	public void flood(Grid3d<Material> grid, Vec3i from) {
		Deque<Vec3i> queue = new ArrayDeque<>();
		queue.addFirst(from);
		
		while(!queue.isEmpty()) {
			Vec3i here = queue.removeLast();
			if (here.x()<0 || here.y()<0 || here.z()<0 || here.x()>=grid.getXSize() || here.y()>=grid.getYSize() || here.z()>=grid.getZSize()) continue;
			Material hereMaterial = grid.get(here);
			if (hereMaterial==Material.AIR) {
				grid.set(here, Material.STEAM);
				queue.addFirst(new Vec3i(here.x()-1, here.y(), here.z()));
				queue.addFirst(new Vec3i(here.x()+1, here.y(), here.z()));
				queue.addFirst(new Vec3i(here.x(), here.y()-1, here.z()));
				queue.addFirst(new Vec3i(here.x(), here.y()+1, here.z()));
				queue.addFirst(new Vec3i(here.x(), here.y(), here.z()-1));
				queue.addFirst(new Vec3i(here.x(), here.y(), here.z()+1));
			}
		}
	}

	@Override
	protected String getRawSampleData() {
		return """
				2,2,2
				1,2,2
				3,2,2
				2,1,2
				2,3,2
				2,2,1
				2,2,3
				2,2,4
				2,2,6
				1,2,5
				3,2,5
				2,1,5
				2,3,5
				""";
	}
}
