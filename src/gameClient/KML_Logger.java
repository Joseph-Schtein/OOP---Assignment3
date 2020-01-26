package gameClient;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import Server.game_service;
import utils.Point3D;



/**
 * This class represents the KML logger.
 * it gets all of the info of the game and saves it to a text file as a kml format.
 * This kml format could be logged to google earth.
 * @author Yossi and Reuven
 *
 */

public class KML_Logger {

	
	private int scenario;
	private StringBuilder kmlFile;
	private game_service game;
	private int id;
	
	public KML_Logger(int scenario, game_service game, int id){
		this.game = game;
		this.scenario = scenario;
		this.kmlFile = new StringBuilder();
		this.id = id;
		startFile();
		addIconStyle();
	}
	
	/**
	 * This method creates the KML file.
	 */
	
	public void startFile(){
		kmlFile.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");//start of a KML file like I saw in the tutorial
		kmlFile.append("\n");
		kmlFile.append("  <kml xmlns=\"http://earth.google.com/kml/2.2\">");
		kmlFile.append("\n");
		kmlFile.append("    <Document>");
		kmlFile.append("\n");
		
		
		kmlFile.append("      <name>\t");//the game scenario as the name of the file
		kmlFile.append("game scenario: ");
		kmlFile.append(scenario);
		kmlFile.append("      </name>");
		kmlFile.append("\n");
	}
	/**
	 * This method determines what will the robots, bananas, apples, vertices and edges.
	 */
	
	public void addIconStyle(){
		kmlFile.append("            <description>giving a unique style for each Icon in the game</description>");
		kmlFile.append("\n");
		kmlFile.append("            <Style id=\"node\">");
		kmlFile.append("\n");
		kmlFile.append("              <IconStyle>");
		kmlFile.append("\n");
		kmlFile.append("                <Icon>");
		kmlFile.append("\n");
		kmlFile.append("                  <href>http://maps.google.com/mapfiles/kml/paddle/grn-blank.png</href>");// giving an icon for the nods in the graph
		kmlFile.append("\n");
		kmlFile.append("                </Icon>");
		kmlFile.append("\n");
		kmlFile.append("              </IconStyle>");
		kmlFile.append("\n");
		kmlFile.append("            </Style>");
		kmlFile.append("\n");
		
		
		kmlFile.append("          <Style id=\"robot\">");
		kmlFile.append("\n");
		kmlFile.append("             <IconStyle>");
		kmlFile.append("\n");
		kmlFile.append("               <Icon>");
		kmlFile.append("\n");
		kmlFile.append("                <href>http://maps.google.com/mapfiles/kml/shapes/man.png</href>");// giving an icon for the robots in the arena
		kmlFile.append("\n");
		kmlFile.append("              </Icon>");
		kmlFile.append("\n");
		kmlFile.append("            </IconStyle>");
		kmlFile.append("\n");
		kmlFile.append("          </Style>");
		kmlFile.append("\n");
		
		
		kmlFile.append("          <Style id=\"banana\">");
		kmlFile.append("\n");
		kmlFile.append("            <IconStyle>");
		kmlFile.append("\n");
		kmlFile.append("              <Icon>");
		kmlFile.append("\n");
		kmlFile.append("                <href>http://maps.google.com/mapfiles/kml/paddle/ylw-blank.png</href>");// giving an icon for the bananas in the arena
		kmlFile.append("\n");
		kmlFile.append("              </Icon>");
		kmlFile.append("\n");
		kmlFile.append("            </IconStyle>");
		kmlFile.append("\n");
		kmlFile.append("          </Style>");
		kmlFile.append("\n");
		
		
		kmlFile.append("          <Style id=\"apple\">");
		kmlFile.append("\n");
		kmlFile.append("            <IconStyle>");
		kmlFile.append("\n");
		kmlFile.append("              <Icon>");
		kmlFile.append("\n");
		kmlFile.append("                <href>http://maps.google.com/mapfiles/kml/paddle/red-circle.png</href>");// giving an icon for the apples in the arena
		kmlFile.append("\n");
		kmlFile.append("              </Icon>");
		kmlFile.append("\n");
		kmlFile.append("            </IconStyle>");
		kmlFile.append("\n");
		kmlFile.append("          </Style>");
		
		
		kmlFile.append("          <Style id=\"lines\">");
		kmlFile.append("\n");
		kmlFile.append("            <LineStyle>");
		kmlFile.append("\n");
		kmlFile.append("             <color>ffff0000</color>");//creating the routes base of the edges graph
		kmlFile.append("\n");
		kmlFile.append("             <width>4</width>");
		kmlFile.append("\n");
		kmlFile.append("            </LineStyle>");
		kmlFile.append("\n");
		kmlFile.append("          </Style>");
		kmlFile.append("\n");

	}
	
	
	public void addAMark(LocalDateTime date , String idIcon, Point3D location){
		
		kmlFile.append("          <Placemark>");
		kmlFile.append("\n");
		kmlFile.append("            <TimeStamp>");
		kmlFile.append("\n");
		kmlFile.append("              <when>");
		kmlFile.append(" "+date);//giving a time stamp
		kmlFile.append("</when>");
		kmlFile.append("\n");
		kmlFile.append("            </TimeStamp>");
		kmlFile.append("\n");
		kmlFile.append("              <styleUrl>#"+idIcon+"</styleUrl>");//using the unique icon I gave earlier
		kmlFile.append("\n");	
		kmlFile.append("            <Point>");
		kmlFile.append("\n");
		kmlFile.append("              <coordinates>"+location.toString()+"</coordinates>");
		kmlFile.append("\n");	
		kmlFile.append("            </Point>");
		kmlFile.append("\n");
		kmlFile.append("          </Placemark>");
		kmlFile.append("\n");
		
	}
	
	
	public void addAPaths(Point3D src, Point3D dest){
		kmlFile.append("             <Placemark>");
		kmlFile.append("\n");
		kmlFile.append("               <styleUrl>#lines</styleUrl>");//creating the route
		kmlFile.append("\n");
		kmlFile.append("			   <LineString>");
		kmlFile.append("			    <extrude>3</extrude>");
		kmlFile.append("\n");
		kmlFile.append("				 <tessellate>3</tessellate>");
		kmlFile.append("\n");
		kmlFile.append("                 <altitudeMode>clampToGround</altitudeMode>");
		kmlFile.append("\n");
		kmlFile.append("                 <coordinates>");
		kmlFile.append("\n");
		kmlFile.append(src.toString());
		kmlFile.append(",");
		kmlFile.append(dest.toString());
		kmlFile.append("\n");
		kmlFile.append("                 </coordinates>");
		kmlFile.append("\n");
		kmlFile.append("			   </LineString>");
		kmlFile.append("\n");
		kmlFile.append("             </Placemark>");
		kmlFile.append("\n");
	}
	
	/**
	 * This method ends, closes and saves the KML file.
	 * @throws FileNotFoundException
	 * @throws SQLException 
	 * @throws JSONException 
	 */
	
	public String endFile() throws FileNotFoundException, SQLException, JSONException{
		kmlFile.append("  </Document>");//end the document segment that I open in startFile()
		kmlFile.append("\n");
		kmlFile.append("</kml>");//same for KML while it is not clearly see that we open it in the second line
		
		String s = String.valueOf(scenario);
		String output =  s + ".KML";// we send it to data file in the project
		PrintWriter pw = new PrintWriter(new File(output)); 
		
		pw.write(kmlFile.toString());
		pw.close();
		
		return kmlFile.toString();
		
	}
}
