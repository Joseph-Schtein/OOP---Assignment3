package gameClient;

import java.util.Comparator;

import utils.Point3D;

public class Fruit {
	
	private Point3D pos;
	private double value;
	private int type;
	
	
	public Fruit(Point3D pos,double value,int type){
		this.pos = pos;
		this.value = value;
		this.type = type;
		
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
	
	public int compare(Fruit o1) {
			
		
		if(this.getValue()>o1.getValue()){
			return 1;
		}
		else if(this.getValue()<o1.getValue()){
			return -1;
		}
	
		else
			return 0;
		
	}
}
