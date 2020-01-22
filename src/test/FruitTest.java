package test;

import static org.junit.Assert.*;
import gameClient.Fruit;

import org.junit.Test;

import utils.Point3D;

public class FruitTest {

	
	
	@Test
	public void testFruitComperator(){
		Point3D pos1 = new Point3D(Math.random(),Math.random(),Math.random());
		Point3D pos2 = new Point3D(Math.random(),Math.random(),Math.random());
		Fruit f1 = new Fruit(pos1,10.32,-1);
		Fruit f2 = new Fruit(pos2,10.31,-1);
		int compare = f1.compare(f2);
		assertEquals(compare, 1);
	}
	
	@Test
	public void testFruitPosition(){
		Point3D pos1 = new Point3D(35,32,0);
		Fruit f1 = new Fruit(pos1,10.32,-1);
		boolean check = f1.getLocation().x()==35 && f1.getLocation().y()==32;
		assertEquals(check, true);
	}
	
	
	@Test
	public void testFruitType(){
		Point3D pos1 = new Point3D(Math.random(),Math.random(),Math.random());
		Point3D pos2 = new Point3D(Math.random(),Math.random(),Math.random());
		Fruit f1 = new Fruit(pos1,10.32,-1);
		Fruit f2 = new Fruit(pos2,10.31,-1);
		boolean check = f1.getType() == f2.getType();
		assertEquals(check, true);
	}
}
