package gameClient;

import java.util.Comparator;
 
import utils.Point3D;

/**
 * This class represents the the fruit class and the fields.
 * @author Yossi and Reuven
 *
 */

public class Fruit {
	
	private Point3D pos;
	private double value;
	private int type;
	private boolean isGonnaEat;
	
	public Fruit(Point3D pos,double value,int type){
		this.pos = pos;
		this.value = value;
		this.type = type;
		isGonnaEat =false;
	}

	public Point3D getLocation() {
		return pos;
	}

	public void setLocation(Point3D pos) {
		this.pos = pos;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isGonnaEat() {
		return isGonnaEat;
	}

	public void setGonnaEat(boolean isGonnaEat) {
		this.isGonnaEat = isGonnaEat;
	}
}
