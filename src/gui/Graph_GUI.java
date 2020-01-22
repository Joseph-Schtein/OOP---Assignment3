package gui;


import utils.Point3D;
import utils.Range;
import utils.StdDraw;

import java.awt.*;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


import algorithms.Graph_Algo;

import java.awt.Graphics;

import dataStructure.DGraph;
import dataStructure.Vertex;
import dataStructure.edge_data;
import dataStructure.graph;
import dataStructure.node_data;
import java.util.Scanner;

public class Graph_GUI extends JFrame implements ActionListener {
	
	private graph gr;
	private int algoCount;
	private String src;
	private String dest;
	private double dist;
	private Collection<node_data> path;
	private final int ARR_SIZE = 8;
	
	public Graph_GUI(graph gr){
		src=dest="";
		dist=0;
		this.gr = gr;
		algoCount = 0;
		path = new LinkedList<>();
		init();
		
	}
	
	
	private void init() {// crate a new pane (window) for gui
		this.setSize(1000, 1000);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		this.setTitle("graph");
		this.setVisible(true);

		MenuBar menuBar = new MenuBar();
		Menu file = new Menu("File");
		Menu algo = new Menu("Algorithem");
		
		menuBar.add(file);
		menuBar.add(algo);
		this.setMenuBar(menuBar);
		
		
		MenuItem file1 = new MenuItem("Save");
		file1.addActionListener(this);
		
		MenuItem file2 = new MenuItem("Load");
		file2.addActionListener(this);
		
		file.add(file1);
		file.add(file2);
		
		MenuItem algo1 = new MenuItem("Is connected");
		algo1.addActionListener(this);
		
		MenuItem algo2 = new MenuItem("Shortest path distance");
		algo2.addActionListener(this);
		
		MenuItem algo3 = new MenuItem("Shortest Path");
		algo3.addActionListener(this);
		
		MenuItem algo4 = new MenuItem("TSP");
		algo4.addActionListener(this); 
		
		algo.add(algo1);
		algo.add(algo2);
		algo.add(algo3);
		algo.add(algo4);
		//this.addMouseListener(this);
		
	}
	
	
	public void paint(Graphics g){//paint the final result of perform algorithm
		super.paint(g);
		Collection<node_data> vertcies = gr.getV();
		if(algoCount == 0) {
				
			
			
			for(node_data v : vertcies) {
				g.setColor(Color.WHITE);
				g.fillOval((int)v.getLocation().x()-3, (int)v.getLocation().y()-3, 15, 15);
				g.setColor(Color.BLACK);
				g.drawOval((int)v.getLocation().x()-3, (int)v.getLocation().y()-3, 15, 15);
				g.drawString(String.valueOf(v.getKey()), (int)(v.getLocation().x()-5),(int)v.getLocation().y()-5);
			
			}	
			
			for(node_data source : vertcies) {
				Collection<edge_data> edges = gr.getE(source.getKey());
				for(edge_data e : edges) {
					node_data dest = gr.getNode(e.getDest());
					g.setColor(Color.RED);
					drawArrow(g,(int)source.getLocation().x(),(int)source.getLocation().y(),(int)dest.getLocation().x(),(int)dest.getLocation().y());

					
					g.setColor(Color.BLACK);
					g.drawString(String.valueOf(e.getWeight()), (int)(source.getLocation().x()+dest.getLocation().x())/2, (int)(source.getLocation().y()+dest.getLocation().y())/2);
				}
			}
		}
		
		
		else if(algoCount==1){

			for(node_data v : vertcies) {
				if(v.getTag()==0){
					g.setColor(Color.WHITE);
					g.fillOval((int)v.getLocation().x()-3, (int)v.getLocation().y()-3, 15, 15);
				}
				
				else if(v.getTag()!=0) {
					g.setColor(Color.GRAY);
					g.fillOval((int)v.getLocation().x()-3, (int)v.getLocation().y()-3, 15, 15);
				}
				g.setColor(Color.BLACK);
				g.drawOval((int)v.getLocation().x()-3, (int)v.getLocation().y()-3, 15, 15);
				g.drawString(String.valueOf(v.getKey()), (int)v.getLocation().x()-5,(int)v.getLocation().y()-5);
				
			}
			
			for(node_data source : vertcies) {
				Collection<edge_data> edges = gr.getE(source.getKey());
				for(edge_data e : edges) {
					node_data destination = gr.getNode(e.getDest());
					if(source.getTag()==0 || destination.getTag()==0) {
						g.setColor(Color.RED);
						drawArrow(g,(int)source.getLocation().x(),(int)source.getLocation().y(),(int)destination.getLocation().x(),(int)destination.getLocation().y());
						g.setColor(Color.BLACK);
						g.drawString(String.valueOf(e.getWeight()), (int)(source.getLocation().x()+destination.getLocation().x())/2, (int)(source.getLocation().y()+destination.getLocation().y())/2);
					}
					else {
						g.setColor(Color.GREEN);
						drawArrow(g,(int)source.getLocation().x(),(int)source.getLocation().y(),(int)destination.getLocation().x(),(int)destination.getLocation().y());
						g.setColor(Color.BLACK);
						g.drawString(String.valueOf(e.getWeight()), (int)(source.getLocation().x()+destination.getLocation().x())/2, (int)(source.getLocation().y()+destination.getLocation().y())/2);
					}
				}
			
			}
		}
		
		
		
		
		
		else if(algoCount == 2) {
						
			for(node_data v : vertcies) {
				g.setColor(Color.WHITE);
				g.fillOval((int)v.getLocation().x()-3, (int)v.getLocation().y()-3, 15, 15);
				g.setColor(Color.BLACK);
				g.drawOval((int)v.getLocation().x()-3, (int)v.getLocation().y()-3, 15, 15);
				g.drawString(String.valueOf(v.getKey()), (int)v.getLocation().x(),(int)v.getLocation().y()-5);

			}
			
			for(node_data source : vertcies) {
				Collection<edge_data> edges = gr.getE(source.getKey());
				for(edge_data e : edges) {
					node_data destination = gr.getNode(e.getDest());
					g.setColor(Color.RED);
					drawArrow(g,(int)source.getLocation().x(),(int)source.getLocation().y(),(int)destination.getLocation().x(),(int)destination.getLocation().y());
					g.setColor(Color.BLACK);
					g.drawString(String.valueOf(e.getWeight()), (int)(source.getLocation().x()+destination.getLocation().x())/2, (int)(source.getLocation().y()+destination.getLocation().y())/2);
				}
			}
			
			if(dist<Double.MAX_VALUE) {
				int d = Integer.valueOf(dest);
				int s = Integer.valueOf(src);
				node_data destination = gr.getNode(d);
				int previous = Integer.parseInt(destination.getInfo());
				List<node_data> toDraw = new LinkedList<>();
				toDraw.add(0,destination);
				
				while(s != previous) {
					node_data pre = gr.getNode(previous);
					toDraw.add(0,pre);
					previous = Integer.parseInt(pre.getInfo());
					pre = gr.getNode(previous);
				}
				node_data source = gr.getNode(s);
				toDraw.add(0,source);	
			
				
				
				
				
					
				Iterator<node_data> iter = toDraw.iterator();
				node_data current = iter.next(); 
				while(iter.hasNext()) {
					node_data next = iter.next();
					g.setColor(Color.GRAY);
					g.fillOval((int)current.getLocation().x()-3, (int)current.getLocation().y()-3, 15, 15);
					g.setColor(Color.BLACK);
					g.drawString(String.valueOf(current.getKey()), (int)current.getLocation().x(),(int)current.getLocation().y()-5);
					g.drawOval((int)current.getLocation().x()-3, (int)current.getLocation().y()-3, 15, 15);
					current=next;
				}	
					
				g.setColor(Color.GRAY);
				g.fillOval((int)current.getLocation().x()-3, (int)current.getLocation().y()-3, 15, 15);					g.setColor(Color.BLACK);
				g.drawString(String.valueOf(current.getKey()), (int)current.getLocation().x(),(int)current.getLocation().y()-5);
					
				iter = toDraw.iterator();
				current = iter.next();
				
				while(iter.hasNext()) {
					node_data next = iter.next();
					edge_data e = gr.getEdge(current.getKey(), next.getKey());
					g.setColor(Color.GREEN);
					drawArrow(g,(int)current.getLocation().x(),(int)current.getLocation().y(),(int)next.getLocation().x(),(int)next.getLocation().y());
					g.drawString(String.valueOf(e.getWeight()), (int)(current.getLocation().x()+next.getLocation().x())/2, (int)(current.getLocation().y()+next.getLocation().y())/2);
					current=next;
				}
					
			}	
		
			else
				System.out.println("There is no path from: "+src+" to:"+dest);
			
		
		}
		
		
		
		
		
		else if(algoCount == 3){
			
			for(node_data v : vertcies) {
				g.setColor(Color.WHITE);
				g.fillOval((int)v.getLocation().x()-3, (int)v.getLocation().y()-3, 15, 15);
				g.setColor(Color.BLACK);
				g.drawOval((int)v.getLocation().x()-3, (int)v.getLocation().y()-3, 15, 15);
				g.drawString(String.valueOf(v.getKey()), (int)v.getLocation().x(),(int)v.getLocation().y()-5);

			}
			
			
			for(node_data source : vertcies) {
				Collection<edge_data> edges = gr.getE(source.getKey());
				for(edge_data e : edges) {
					node_data destination = gr.getNode(e.getDest());
					g.setColor(Color.RED);
					drawArrow(g,(int)source.getLocation().x(),(int)source.getLocation().y(),(int)destination.getLocation().x(),(int)destination.getLocation().y());
					g.setColor(Color.BLACK);
					g.drawString(String.valueOf(e.getWeight()), (int)(source.getLocation().x()+destination.getLocation().x())/2, (int)(source.getLocation().y()+destination.getLocation().y())/2);
				}
			}
			
			
			
			
			
			Iterator<node_data> iter = path.iterator();
			if(iter.hasNext()) { 
				node_data current = iter.next();
			
					while(iter.hasNext()) {
						node_data next = iter.next();
						g.setColor(Color.GRAY);
						g.fillOval((int)current.getLocation().x()-3, (int)current.getLocation().y()-3, 15, 15);
						g.setColor(Color.BLACK);
						g.drawString(String.valueOf(current.getKey()), (int)current.getLocation().x(),(int)current.getLocation().y()-5);
						g.drawOval((int)current.getLocation().x()-3, (int)current.getLocation().y()-3, 15, 15);
						current=next;
					}	
			
				
				g.setColor(Color.GRAY);
				g.fillOval((int)current.getLocation().x()-3, (int)current.getLocation().y()-3, 15, 15);					g.setColor(Color.BLACK);
				g.drawString(String.valueOf(current.getKey()), (int)current.getLocation().x(),(int)current.getLocation().y()-5);
				
				iter = path.iterator();
				current = iter.next();
			
				while(iter.hasNext()) {
					node_data next = iter.next();
					edge_data e = gr.getEdge(current.getKey(), next.getKey());
					g.setColor(Color.GREEN);
					drawArrow(g,(int)current.getLocation().x(),(int)current.getLocation().y(),(int)next.getLocation().x(),(int)next.getLocation().y());
					g.drawString(String.valueOf(e.getWeight()), (int)(current.getLocation().x()+next.getLocation().x())/2, (int)(current.getLocation().y()+next.getLocation().y())/2);
					current=next;
				}
			}
			else
				System.out.println("There is no path from: "+src+" to:"+dest);
			
		}	
	
		
		
	
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {//enter on one of the option on the gui window
		String str = e.getActionCommand();
		Graph_Algo algo = new Graph_Algo();
		algo.init(gr);
		reset();
		
		if(str.equals("Save")) {
			
			FileDialog chooser = new FileDialog(this, "choose the name for the file to save", FileDialog.SAVE);
			chooser.setVisible(true);
			String filename = chooser.getFile();
			algo.save(filename);
		}
		
		else if(str.equals("Load")) {
			FileDialog chooser = new FileDialog(this, "choose the name for the file to load", FileDialog.LOAD);
			chooser.setVisible(true);
			String filename = chooser.getFile();
			Graph_Algo tmp = new Graph_Algo();
			try {
				tmp.init(filename);
			}
			
			catch (IOException e1) {
				e1.printStackTrace();
			}
			
			gr = tmp.copy();
			repaint();
			
		}
		
		else if(str.equals("Is connected")) {
			algoCount = 1;
			boolean connect = algo.isConnected();
			System.out.println(connect);
			repaint();
			
		}
		
		else if(str.equals("Shortest path distance")) {
			algoCount = 2;
			JFrame f = new JFrame("Shortest Path Dist");
	        src = JOptionPane.showInputDialog(f, "Enter src: ");
	        dest = JOptionPane.showInputDialog(f, "Enter dest: ");
	        boolean isNumber = true;
	        for(int i = 0; i<src.length() && isNumber;i++){
	        	if(src.charAt(i)<=47 || src.charAt(i)>=58)
	        		isNumber =false;
	        }
	        
	        for(int i = 0; i<dest.length() && isNumber;i++){
	        	if(dest.charAt(i)<=47 || dest.charAt(i)>=58)
	        		isNumber =false;
	        }
	        if(src.length()==0 || dest.length()==0)
	        	isNumber=false;
	        
	        if(src != null && dest != null && isNumber) {
	            double answer = algo.shortestPathDist(Integer.parseInt(src), Integer.parseInt(dest));
	            JOptionPane.showMessageDialog(f, "The shortest dist between " + src + " and " + dest +" is: " + answer);
	            int d = Integer.valueOf(dest);
				int s = Integer.valueOf(src);
				dist = algo.shortestPathDist(s, d);
				System.out.println(dist);
				dist = 0;
				repaint();
				
	        }
	        
	        else
	        	throw new IllegalArgumentException("You enter a non valid source or destination");
	        
	      
		}
		
		else if(str.equals("Shortest Path")) {
			algoCount = 3;
			JFrame f = new JFrame("Shortest Path Dist");
	        src = JOptionPane.showInputDialog(f, "Enter src: ");
	        dest = JOptionPane.showInputDialog(f, "Enter dest: ");
	        boolean isNumber = true;
	        for(int i = 0; i<src.length() && isNumber;i++){
	        	if(src.charAt(i)<=47 || src.charAt(i)>=58)
	        		isNumber =false;
	        }
	        
	        for(int i = 0; i<dest.length() && isNumber;i++){
	        	if(dest.charAt(i)<=47 || dest.charAt(i)>=58)
	        		isNumber =false;
	        }
	        if(src.length()==0 || dest.length()==0)
	        	isNumber=false;
	        
	        if(src != null && dest != null && isNumber) {
	            int d = Integer.valueOf(dest);
				int s = Integer.valueOf(src);
				path = algo.shortestPath(s, d);
				
				Iterator<node_data> iter = path.iterator();
				while(iter.hasNext()) {
					node_data current = iter.next();
					if(iter.hasNext()) {
						System.out.print(current.getKey()+", ");
					}
					else
						System.out.print(current.getKey());
				}
				
				
				System.out.println();
				repaint();
				
	        }
	        
	        else
	        	throw new IllegalArgumentException("You enter a non valid source or destination");
	        
		}
		
		else {
			algoCount = 3;
			List<Integer> targets = new LinkedList<>(); 
			JFrame f = new JFrame("Shortest Path Dist");
	        src = JOptionPane.showInputDialog(f, "Enter a list of integer of the vertices\n you want to check for the TSP\n "
	        		+ "while you sepreate every integer with a cumma");
	        
	        src = src.replace(" ", ""); 
	        String[] ver = src.split(",");
	        for(String s: ver) {
	        	boolean isNumber = true;
	        	for(int i = 0; i < s.length() && !isNumber;i++) {
	        		if(s.charAt(i)<48 || s.charAt(i)>57)
	        			isNumber = false;
	        	}
	        	if(isNumber)
	        		targets.add(Integer.parseInt(s));
	        }
			path = algo.TSP(targets);
			
			Iterator<node_data> iter = path.iterator();
			while(iter.hasNext()) {
				node_data current = iter.next();
				if(iter.hasNext()) {
					System.out.print(current.getKey()+", ");
				}
				else
					System.out.print(current.getKey());
			}
			
			System.out.println();
			repaint();
			
		}
	}
	
	private void reset() {// reset the graph for draw a new result
		Collection<node_data> ver = gr.getV();
		for(node_data tmp : ver) {
			Vertex v = (Vertex)tmp;
			v.setTag(0);
			v.setInfo("");
			v.setWeight(Double.MAX_VALUE);	
		}
	}
	
	

    private void drawArrow(Graphics g1, int x1, int y1, int x2, int y2) {// draw an arrow 
        Graphics2D g = (Graphics2D) g1.create();
        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx*dx + dy*dy);
        AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
        at.concatenate(AffineTransform.getRotateInstance(angle));
        g.transform(at);

        // Draw horizontal arrow starting in (0, 0)
        g.drawLine(0, 0, len, 0);
        g.setColor(Color.BLACK);
        g.fillPolygon(new int[] {len, len-ARR_SIZE, len-ARR_SIZE, len},
                      new int[] {0, -ARR_SIZE, ARR_SIZE, 0}, 4);
    }
}
