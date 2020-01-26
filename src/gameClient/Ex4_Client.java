package gameClient;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import Server.Game_Server;
import Server.game_service;
import oop_dataStructure.OOP_DGraph;
import oop_dataStructure.oop_edge_data;
import oop_dataStructure.oop_graph;
import oop_utils.OOP_Point3D;
/**
* This class represents a simple example for using the GameServer API:
* the main file performs the following tasks:
* 0. login as a user ("999") for testing - do use your ID.
* 1. Creates a game_service [0,23] (user "999" has stage 9, can play in scenarios [0,9] not above
*    Note: you can also choose -1 for debug (allowing a 600 second game).
* 2. Constructs the graph from JSON String
* 3. Gets the scenario JSON String 
* 5. Add a set of robots  // note: in general a list of robots should be added
* 6. Starts game 
* 7. Main loop (vary simple thread)
* 8. move the robot along the current edge 
* 9. direct to the next edge (if on a node) 
* 10. prints the game results (after "game over"), and write a KML: 
*     Note: will NOT work on case -1 (debug).
*  
* @author boaz.benmoshe
*
*/
public class Ex4_Client implements Runnable{
	public static void main(String[] a) {
		Thread client = new Thread(new Ex4_Client());
		client.start();
	}
	
	@Override
	public void run() {
		int scenario_num = 0; // current "stage is 9, can play[0,9], can NOT 10 or above
		int id = 999;
		Game_Server.login(id);
		game_service game = Game_Server.getServer(scenario_num); // you have [0,23] games
		
		String g = game.getGraph();
		List<String> fruits = game.getFruits();
		OOP_DGraph gg = new OOP_DGraph();
		gg.init(g);
		init(game);
		
		game.startGame();
		int ind=0;
		long dt=200;
		int jj = 0;
		while(game.isRunning()) {
			moveRobots(game, gg);
			try {
				List<String> stat = game.getRobots();
				for(int i=0;i<stat.size();i++) {
					System.out.println(jj+") "+stat.get(i));
				}
				ind++;
				Thread.sleep(dt);
				jj++;
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		String res = game.toString();
		String remark = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"  <kml xmlns=\"http://earth.google.com/kml/2.2\">\n" + 
				"    <Document>\n" + 
				"      <name>	game scenario: 0      </name>\n" + 
				"            <description>giving a unique style for each Icon in the game</description>\n" + 
				"            <Style id=\"node\">\n" + 
				"              <IconStyle>\n" + 
				"                <Icon>\n" + 
				"                  <href>http://maps.google.com/mapfiles/kml/paddle/grn-blank.png</href>\n" + 
				"                </Icon>\n" + 
				"              </IconStyle>\n" + 
				"            </Style>\n" + 
				"          <Style id=\"robot\">\n" + 
				"             <IconStyle>\n" + 
				"               <Icon>\n" + 
				"                <href>http://maps.google.com/mapfiles/kml/shapes/man.png</href>\n" + 
				"              </Icon>\n" + 
				"            </IconStyle>\n" + 
				"          </Style>\n" + 
				"          <Style id=\"banana\">\n" + 
				"            <IconStyle>\n" + 
				"              <Icon>\n" + 
				"                <href>http://maps.google.com/mapfiles/kml/paddle/ylw-blank.png</href>\n" + 
				"              </Icon>\n" + 
				"            </IconStyle>\n" + 
				"          </Style>\n" + 
				"          <Style id=\"apple\">\n" + 
				"            <IconStyle>\n" + 
				"              <Icon>\n" + 
				"                <href>http://maps.google.com/mapfiles/kml/paddle/red-circle.png</href>\n" + 
				"              </Icon>\n" + 
				"            </IconStyle>\n" + 
				"          </Style>          <Style id=\"lines\">\n" + 
				"            <LineStyle>\n" + 
				"             <color>ffff0000</color>\n" + 
				"             <width>4</width>\n" + 
				"            </LineStyle>\n" + 
				"          </Style>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:45.307249600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.197656770719604,32.10191878639921,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:46.707058</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19597880064568,32.10154696638656,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:46.708094400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#node</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18753053591606,32.10378225882353,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:46.708094400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#node</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18958953510896,32.10785303529412,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:46.708094400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#node</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19341035835351,32.10610841680672,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:46.708094400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#node</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.197528356739305,32.1053088,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:46.708094400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#node</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.2016888087167,32.10601755126051,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:46.708094400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#node</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20582803389831,32.10625380168067,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:46.708094400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#node</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20792948668281,32.10470908739496,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:46.708094400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#node</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20746249717514,32.10254648739496,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:46.708094400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#node</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20319591121872,32.1031462,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:46.710407600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#node</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19597880064568,32.10154696638656,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:46.710407600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#node</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18910131880549,32.103618700840336,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"             <Placemark>\n" + 
				"               <styleUrl>#lines</styleUrl>\n" + 
				"			   <LineString>			    <extrude>3</extrude>\n" + 
				"				 <tessellate>3</tessellate>\n" + 
				"                 <altitudeMode>clampToGround</altitudeMode>\n" + 
				"                 <coordinates>\n" + 
				"35.18753053591606,32.10378225882353,0.0,35.18958953510896,32.10785303529412,0.0\n" + 
				"                 </coordinates>\n" + 
				"			   </LineString>\n" + 
				"             </Placemark>\n" + 
				"             <Placemark>\n" + 
				"               <styleUrl>#lines</styleUrl>\n" + 
				"			   <LineString>			    <extrude>3</extrude>\n" + 
				"				 <tessellate>3</tessellate>\n" + 
				"                 <altitudeMode>clampToGround</altitudeMode>\n" + 
				"                 <coordinates>\n" + 
				"35.18753053591606,32.10378225882353,0.0,35.18910131880549,32.103618700840336,0.0\n" + 
				"                 </coordinates>\n" + 
				"			   </LineString>\n" + 
				"             </Placemark>\n" + 
				"             <Placemark>\n" + 
				"               <styleUrl>#lines</styleUrl>\n" + 
				"			   <LineString>			    <extrude>3</extrude>\n" + 
				"				 <tessellate>3</tessellate>\n" + 
				"                 <altitudeMode>clampToGround</altitudeMode>\n" + 
				"                 <coordinates>\n" + 
				"35.18958953510896,32.10785303529412,0.0,35.18753053591606,32.10378225882353,0.0\n" + 
				"                 </coordinates>\n" + 
				"			   </LineString>\n" + 
				"             </Placemark>\n" + 
				"             <Placemark>\n" + 
				"               <styleUrl>#lines</styleUrl>\n" + 
				"			   <LineString>			    <extrude>3</extrude>\n" + 
				"				 <tessellate>3</tessellate>\n" + 
				"                 <altitudeMode>clampToGround</altitudeMode>\n" + 
				"                 <coordinates>\n" + 
				"35.18958953510896,32.10785303529412,0.0,35.19341035835351,32.10610841680672,0.0\n" + 
				"                 </coordinates>\n" + 
				"			   </LineString>\n" + 
				"             </Placemark>\n" + 
				"             <Placemark>\n" + 
				"               <styleUrl>#lines</styleUrl>\n" + 
				"			   <LineString>			    <extrude>3</extrude>\n" + 
				"				 <tessellate>3</tessellate>\n" + 
				"                 <altitudeMode>clampToGround</altitudeMode>\n" + 
				"                 <coordinates>\n" + 
				"35.19341035835351,32.10610841680672,0.0,35.18958953510896,32.10785303529412,0.0\n" + 
				"                 </coordinates>\n" + 
				"			   </LineString>\n" + 
				"             </Placemark>\n" + 
				"             <Placemark>\n" + 
				"               <styleUrl>#lines</styleUrl>\n" + 
				"			   <LineString>			    <extrude>3</extrude>\n" + 
				"				 <tessellate>3</tessellate>\n" + 
				"                 <altitudeMode>clampToGround</altitudeMode>\n" + 
				"                 <coordinates>\n" + 
				"35.19341035835351,32.10610841680672,0.0,35.197528356739305,32.1053088,0.0\n" + 
				"                 </coordinates>\n" + 
				"			   </LineString>\n" + 
				"             </Placemark>\n" + 
				"             <Placemark>\n" + 
				"               <styleUrl>#lines</styleUrl>\n" + 
				"			   <LineString>			    <extrude>3</extrude>\n" + 
				"				 <tessellate>3</tessellate>\n" + 
				"                 <altitudeMode>clampToGround</altitudeMode>\n" + 
				"                 <coordinates>\n" + 
				"35.197528356739305,32.1053088,0.0,35.19341035835351,32.10610841680672,0.0\n" + 
				"                 </coordinates>\n" + 
				"			   </LineString>\n" + 
				"             </Placemark>\n" + 
				"             <Placemark>\n" + 
				"               <styleUrl>#lines</styleUrl>\n" + 
				"			   <LineString>			    <extrude>3</extrude>\n" + 
				"				 <tessellate>3</tessellate>\n" + 
				"                 <altitudeMode>clampToGround</altitudeMode>\n" + 
				"                 <coordinates>\n" + 
				"35.197528356739305,32.1053088,0.0,35.2016888087167,32.10601755126051,0.0\n" + 
				"                 </coordinates>\n" + 
				"			   </LineString>\n" + 
				"             </Placemark>\n" + 
				"             <Placemark>\n" + 
				"               <styleUrl>#lines</styleUrl>\n" + 
				"			   <LineString>			    <extrude>3</extrude>\n" + 
				"				 <tessellate>3</tessellate>\n" + 
				"                 <altitudeMode>clampToGround</altitudeMode>\n" + 
				"                 <coordinates>\n" + 
				"35.2016888087167,32.10601755126051,0.0,35.197528356739305,32.1053088,0.0\n" + 
				"                 </coordinates>\n" + 
				"			   </LineString>\n" + 
				"             </Placemark>\n" + 
				"             <Placemark>\n" + 
				"               <styleUrl>#lines</styleUrl>\n" + 
				"			   <LineString>			    <extrude>3</extrude>\n" + 
				"				 <tessellate>3</tessellate>\n" + 
				"                 <altitudeMode>clampToGround</altitudeMode>\n" + 
				"                 <coordinates>\n" + 
				"35.2016888087167,32.10601755126051,0.0,35.20582803389831,32.10625380168067,0.0\n" + 
				"                 </coordinates>\n" + 
				"			   </LineString>\n" + 
				"             </Placemark>\n" + 
				"             <Placemark>\n" + 
				"               <styleUrl>#lines</styleUrl>\n" + 
				"			   <LineString>			    <extrude>3</extrude>\n" + 
				"				 <tessellate>3</tessellate>\n" + 
				"                 <altitudeMode>clampToGround</altitudeMode>\n" + 
				"                 <coordinates>\n" + 
				"35.20582803389831,32.10625380168067,0.0,35.2016888087167,32.10601755126051,0.0\n" + 
				"                 </coordinates>\n" + 
				"			   </LineString>\n" + 
				"             </Placemark>\n" + 
				"             <Placemark>\n" + 
				"               <styleUrl>#lines</styleUrl>\n" + 
				"			   <LineString>			    <extrude>3</extrude>\n" + 
				"				 <tessellate>3</tessellate>\n" + 
				"                 <altitudeMode>clampToGround</altitudeMode>\n" + 
				"                 <coordinates>\n" + 
				"35.20582803389831,32.10625380168067,0.0,35.20792948668281,32.10470908739496,0.0\n" + 
				"                 </coordinates>\n" + 
				"			   </LineString>\n" + 
				"             </Placemark>\n" + 
				"             <Placemark>\n" + 
				"               <styleUrl>#lines</styleUrl>\n" + 
				"			   <LineString>			    <extrude>3</extrude>\n" + 
				"				 <tessellate>3</tessellate>\n" + 
				"                 <altitudeMode>clampToGround</altitudeMode>\n" + 
				"                 <coordinates>\n" + 
				"35.20792948668281,32.10470908739496,0.0,35.20582803389831,32.10625380168067,0.0\n" + 
				"                 </coordinates>\n" + 
				"			   </LineString>\n" + 
				"             </Placemark>\n" + 
				"             <Placemark>\n" + 
				"               <styleUrl>#lines</styleUrl>\n" + 
				"			   <LineString>			    <extrude>3</extrude>\n" + 
				"				 <tessellate>3</tessellate>\n" + 
				"                 <altitudeMode>clampToGround</altitudeMode>\n" + 
				"                 <coordinates>\n" + 
				"35.20792948668281,32.10470908739496,0.0,35.20746249717514,32.10254648739496,0.0\n" + 
				"                 </coordinates>\n" + 
				"			   </LineString>\n" + 
				"             </Placemark>\n" + 
				"             <Placemark>\n" + 
				"               <styleUrl>#lines</styleUrl>\n" + 
				"			   <LineString>			    <extrude>3</extrude>\n" + 
				"				 <tessellate>3</tessellate>\n" + 
				"                 <altitudeMode>clampToGround</altitudeMode>\n" + 
				"                 <coordinates>\n" + 
				"35.20746249717514,32.10254648739496,0.0,35.20792948668281,32.10470908739496,0.0\n" + 
				"                 </coordinates>\n" + 
				"			   </LineString>\n" + 
				"             </Placemark>\n" + 
				"             <Placemark>\n" + 
				"               <styleUrl>#lines</styleUrl>\n" + 
				"			   <LineString>			    <extrude>3</extrude>\n" + 
				"				 <tessellate>3</tessellate>\n" + 
				"                 <altitudeMode>clampToGround</altitudeMode>\n" + 
				"                 <coordinates>\n" + 
				"35.20746249717514,32.10254648739496,0.0,35.20319591121872,32.1031462,0.0\n" + 
				"                 </coordinates>\n" + 
				"			   </LineString>\n" + 
				"             </Placemark>\n" + 
				"             <Placemark>\n" + 
				"               <styleUrl>#lines</styleUrl>\n" + 
				"			   <LineString>			    <extrude>3</extrude>\n" + 
				"				 <tessellate>3</tessellate>\n" + 
				"                 <altitudeMode>clampToGround</altitudeMode>\n" + 
				"                 <coordinates>\n" + 
				"35.20319591121872,32.1031462,0.0,35.20746249717514,32.10254648739496,0.0\n" + 
				"                 </coordinates>\n" + 
				"			   </LineString>\n" + 
				"             </Placemark>\n" + 
				"             <Placemark>\n" + 
				"               <styleUrl>#lines</styleUrl>\n" + 
				"			   <LineString>			    <extrude>3</extrude>\n" + 
				"				 <tessellate>3</tessellate>\n" + 
				"                 <altitudeMode>clampToGround</altitudeMode>\n" + 
				"                 <coordinates>\n" + 
				"35.20319591121872,32.1031462,0.0,35.19597880064568,32.10154696638656,0.0\n" + 
				"                 </coordinates>\n" + 
				"			   </LineString>\n" + 
				"             </Placemark>\n" + 
				"             <Placemark>\n" + 
				"               <styleUrl>#lines</styleUrl>\n" + 
				"			   <LineString>			    <extrude>3</extrude>\n" + 
				"				 <tessellate>3</tessellate>\n" + 
				"                 <altitudeMode>clampToGround</altitudeMode>\n" + 
				"                 <coordinates>\n" + 
				"35.19597880064568,32.10154696638656,0.0,35.20319591121872,32.1031462,0.0\n" + 
				"                 </coordinates>\n" + 
				"			   </LineString>\n" + 
				"             </Placemark>\n" + 
				"             <Placemark>\n" + 
				"               <styleUrl>#lines</styleUrl>\n" + 
				"			   <LineString>			    <extrude>3</extrude>\n" + 
				"				 <tessellate>3</tessellate>\n" + 
				"                 <altitudeMode>clampToGround</altitudeMode>\n" + 
				"                 <coordinates>\n" + 
				"35.19597880064568,32.10154696638656,0.0,35.18910131880549,32.103618700840336,0.0\n" + 
				"                 </coordinates>\n" + 
				"			   </LineString>\n" + 
				"             </Placemark>\n" + 
				"             <Placemark>\n" + 
				"               <styleUrl>#lines</styleUrl>\n" + 
				"			   <LineString>			    <extrude>3</extrude>\n" + 
				"				 <tessellate>3</tessellate>\n" + 
				"                 <altitudeMode>clampToGround</altitudeMode>\n" + 
				"                 <coordinates>\n" + 
				"35.18910131880549,32.103618700840336,0.0,35.18753053591606,32.10378225882353,0.0\n" + 
				"                 </coordinates>\n" + 
				"			   </LineString>\n" + 
				"             </Placemark>\n" + 
				"             <Placemark>\n" + 
				"               <styleUrl>#lines</styleUrl>\n" + 
				"			   <LineString>			    <extrude>3</extrude>\n" + 
				"				 <tessellate>3</tessellate>\n" + 
				"                 <altitudeMode>clampToGround</altitudeMode>\n" + 
				"                 <coordinates>\n" + 
				"35.18910131880549,32.103618700840336,0.0,35.19597880064568,32.10154696638656,0.0\n" + 
				"                 </coordinates>\n" + 
				"			   </LineString>\n" + 
				"             </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:46.885029300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.197656770719604,32.10191878639921,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:46.885029300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19597880064568,32.10154696638656,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:47.174566100</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:47.174566100</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19597880064568,32.10154696638656,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:47.457136400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:47.457136400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19597880064568,32.10154696638656,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:47.744487200</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:47.744487200</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19597880064568,32.10154696638656,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:48.015325500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:48.015325500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19597880064568,32.10154696638656,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:48.297930300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:48.297930300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20319591121872,32.1031462,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:48.589224400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:48.590272600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20319591121872,32.1031462,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:48.857866900</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:48.857866900</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20319591121872,32.1031462,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:49.117581700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:49.117581700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20319591121872,32.1031462,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:49.393091800</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:49.393091800</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20319591121872,32.1031462,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:49.683871300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:49.683871300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20319591121872,32.1031462,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:49.967787100</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:49.967787100</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20319591121872,32.1031462,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:50.249822400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:50.249822400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20746249717514,32.10254648739496,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:50.526572600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:50.533711200</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20746249717514,32.10254648739496,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:50.796070800</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:50.796070800</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20746249717514,32.10254648739496,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:51.062813300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:51.063406400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20746249717514,32.10254648739496,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:51.323762</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:51.323762</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20792948668281,32.10470908739496,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:51.582772500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:51.586770300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20792948668281,32.10470908739496,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:51.851909</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:51.851909</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20792948668281,32.10470908739496,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:52.117527100</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:52.117527100</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20792948668281,32.10470908739496,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:52.382365900</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:52.382365900</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20792948668281,32.10470908739496,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:52.644715700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:52.644715700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20792948668281,32.10470908739496,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:52.914768500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:52.914768500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20582803389831,32.10625380168067,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:53.172647800</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:53.172647800</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20582803389831,32.10625380168067,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:53.436050400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:53.436050400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20582803389831,32.10625380168067,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:53.701195500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:53.701195500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20582803389831,32.10625380168067,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:53.975208700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:53.975208700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20582803389831,32.10625380168067,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:54.243143400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:54.243143400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20582803389831,32.10625380168067,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:54.525023500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.199963710098416,32.105723673136964,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:54.525023500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.2016888087167,32.10601755126051,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:54.787845700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.195224052340706,32.10575624080796,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:54.787845700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.2016888087167,32.10601755126051,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:55.054779400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.195224052340706,32.10575624080796,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:55.054779400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.2016888087167,32.10601755126051,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:55.318598400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.195224052340706,32.10575624080796,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:55.319122300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.2016888087167,32.10601755126051,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:55.583885400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.195224052340706,32.10575624080796,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:55.583885400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.2016888087167,32.10601755126051,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:55.845565300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.195224052340706,32.10575624080796,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:55.845565300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.2016888087167,32.10601755126051,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:56.124856500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.195224052340706,32.10575624080796,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:56.124856500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.197528356739305,32.1053088,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:56.390979500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19494883961552,32.105809680537625,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:56.390979500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.197528356739305,32.1053088,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:56.658731900</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19038634163924,32.10748920705224,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:56.658731900</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.197528356739305,32.1053088,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:56.916378200</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19038634163924,32.10748920705224,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:56.916378200</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.197528356739305,32.1053088,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:57.184132200</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19038634163924,32.10748920705224,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:57.184132200</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19341035835351,32.10610841680672,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:57.441737800</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19038634163924,32.10748920705224,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:57.441737800</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19341035835351,32.10610841680672,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:57.717533600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19038634163924,32.10748920705224,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:57.717533600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19341035835351,32.10610841680672,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:57.994695</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19038634163924,32.10748920705224,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:57.994695</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19341035835351,32.10610841680672,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:58.268616900</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.1992728373109,32.105605979924384,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:58.268616900</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19341035835351,32.10610841680672,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:58.541027300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.1992728373109,32.105605979924384,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:58.541027300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19341035835351,32.10610841680672,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:58.803605400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.1992728373109,32.105605979924384,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:58.803605400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19341035835351,32.10610841680672,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:59.066462400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.1992728373109,32.105605979924384,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:59.066462400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18958953510896,32.10785303529412,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:59.326464700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.1992728373109,32.105605979924384,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:59.326464700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18958953510896,32.10785303529412,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:59.587078600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.1992728373109,32.105605979924384,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:59.587078600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18958953510896,32.10785303529412,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:59.848924600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.1992728373109,32.105605979924384,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:20:59.848924600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18958953510896,32.10785303529412,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:00.115213800</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.1992728373109,32.105605979924384,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:00.115213800</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18958953510896,32.10785303529412,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:00.369820600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.1992728373109,32.105605979924384,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:00.369820600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18958953510896,32.10785303529412,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:00.637427200</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.1992728373109,32.105605979924384,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:00.638606500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18958953510896,32.10785303529412,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:00.899688300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.1992728373109,32.105605979924384,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:00.899688300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19341035835351,32.10610841680672,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:01.160289500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.1992728373109,32.105605979924384,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:01.160289500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19341035835351,32.10610841680672,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:01.427618500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.1992728373109,32.105605979924384,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:01.427618500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19341035835351,32.10610841680672,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:01.689504600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.1992728373109,32.105605979924384,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:01.689504600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19341035835351,32.10610841680672,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:01.959797600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.1992728373109,32.105605979924384,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:01.959797600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.197528356739305,32.1053088,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:02.239967900</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.1992728373109,32.105605979924384,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:02.239967900</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.197528356739305,32.1053088,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:02.507458700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.1992728373109,32.105605979924384,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:02.507458700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.197528356739305,32.1053088,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:02.774900</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.1992728373109,32.105605979924384,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:02.774900</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.197528356739305,32.1053088,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:03.041101200</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.1992728373109,32.105605979924384,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:03.041101200</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.197528356739305,32.1053088,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:03.310528200</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.1992728373109,32.105605979924384,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:03.310528200</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.197528356739305,32.1053088,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:03.596409200</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.1992728373109,32.105605979924384,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:03.596409200</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.2016888087167,32.10601755126051,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:03.856760200</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.1992728373109,32.105605979924384,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:03.856760200</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.2016888087167,32.10601755126051,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:04.118544600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18893516072167,32.10655929420479,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:04.118544600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.2016888087167,32.10601755126051,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:04.375032300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18893516072167,32.10655929420479,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:04.375032300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.197528356739305,32.1053088,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:04.636475200</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18893516072167,32.10655929420479,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:04.636475200</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.197528356739305,32.1053088,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:04.904747200</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18893516072167,32.10655929420479,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:04.904747200</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.197528356739305,32.1053088,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:05.163753900</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18893516072167,32.10655929420479,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:05.163753900</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19341035835351,32.10610841680672,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:05.428156400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18893516072167,32.10655929420479,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:05.428156400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19341035835351,32.10610841680672,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:05.700754900</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18893516072167,32.10655929420479,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:05.700754900</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19341035835351,32.10610841680672,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:05.958979500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18887602099216,32.106442371080114,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:05.958979500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18958953510896,32.10785303529412,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:06.225400100</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20539663533063,32.10283686555705,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:06.225400100</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18958953510896,32.10785303529412,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:06.504670800</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20539663533063,32.10283686555705,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:06.504670800</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18958953510896,32.10785303529412,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:06.763223600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20539663533063,32.10283686555705,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:06.763223600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18958953510896,32.10785303529412,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:07.028057800</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20539663533063,32.10283686555705,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:07.034716400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18753053591606,32.10378225882353,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:07.304797600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20539663533063,32.10283686555705,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:07.304797600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18753053591606,32.10378225882353,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:07.573782600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20539663533063,32.10283686555705,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:07.573782600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18753053591606,32.10378225882353,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:07.842497300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20539663533063,32.10283686555705,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:07.842497300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18910131880549,32.103618700840336,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:08.105220700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20539663533063,32.10283686555705,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:08.105220700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18910131880549,32.103618700840336,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:08.367476100</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20539663533063,32.10283686555705,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:08.367476100</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19597880064568,32.10154696638656,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:08.640215300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20539663533063,32.10283686555705,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:08.640215300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19597880064568,32.10154696638656,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:08.902724300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20539663533063,32.10283686555705,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:08.902724300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20319591121872,32.1031462,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:09.169844800</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20539663533063,32.10283686555705,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:09.169844800</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20319591121872,32.1031462,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:09.430335700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19974476565152,32.105686374989176,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:09.430335700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20319591121872,32.1031462,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:09.709032</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19974476565152,32.105686374989176,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:09.709032</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20319591121872,32.1031462,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:09.990822700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19974476565152,32.105686374989176,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:09.990822700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20746249717514,32.10254648739496,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:10.261255500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19974476565152,32.105686374989176,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:10.261255500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20746249717514,32.10254648739496,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:10.526139900</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19974476565152,32.105686374989176,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:10.526139900</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20792948668281,32.10470908739496,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:10.787266</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19974476565152,32.105686374989176,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:10.787266</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20792948668281,32.10470908739496,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:11.051401500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19974476565152,32.105686374989176,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:11.051401500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20792948668281,32.10470908739496,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:11.310626700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19974476565152,32.105686374989176,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:11.310626700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20582803389831,32.10625380168067,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:11.571501700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19974476565152,32.105686374989176,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:11.571501700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20582803389831,32.10625380168067,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:11.838539700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19974476565152,32.105686374989176,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:11.838539700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.20582803389831,32.10625380168067,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:12.108511800</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#apple</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.188500771222955,32.10570047767076,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:12.108511800</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.2016888087167,32.10601755126051,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:12.375258300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#apple</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.188500771222955,32.10570047767076,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:12.375258300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.197528356739305,32.1053088,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:12.639805100</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#apple</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.188500771222955,32.10570047767076,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:12.639805100</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19341035835351,32.10610841680672,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:12.907642800</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#apple</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.188500771222955,32.10570047767076,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:12.907642800</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18958953510896,32.10785303529412,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:13.193388900</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#apple</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.188500771222955,32.10570047767076,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:13.193388900</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18958953510896,32.10785303529412,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:13.457951900</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19565793847981,32.10567199049591,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:13.457951900</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18753053591606,32.10378225882353,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:13.727620400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19565793847981,32.10567199049591,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:13.727620400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18958953510896,32.10785303529412,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:14.008228200</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19565793847981,32.10567199049591,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:14.008228200</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18958953510896,32.10785303529412,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:14.273892700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19565793847981,32.10567199049591,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:14.273892700</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19341035835351,32.10610841680672,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:14.540157100</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.188827745913414,32.10634692808267,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:14.540157100</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.197528356739305,32.1053088,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:14.804842300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.188827745913414,32.10634692808267,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:14.804842300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19341035835351,32.10610841680672,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:15.064144500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.188827745913414,32.10634692808267,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:15.064144500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19341035835351,32.10610841680672,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:15.323636500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18867176864414,32.103663427809636,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:15.323636500</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18958953510896,32.10785303529412,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:15.586929400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18867176864414,32.103663427809636,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:15.586929400</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18753053591606,32.10378225882353,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:15.855103600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18867176864414,32.103663427809636,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:15.855103600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18753053591606,32.10378225882353,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:16.120217600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19580080140219,32.10564424993287,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:16.120217600</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18910131880549,32.103618700840336,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:16.395073300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19580080140219,32.10564424993287,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:16.395073300</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18753053591606,32.10378225882353,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:16.678624800</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#banana</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.19580080140219,32.10564424993287,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"          <Placemark>\n" + 
				"            <TimeStamp>\n" + 
				"              <when> 2020-01-23T16:21:16.678624800</when>\n" + 
				"            </TimeStamp>\n" + 
				"              <styleUrl>#robot</styleUrl>\n" + 
				"            <Point>\n" + 
				"              <coordinates>35.18958953510896,32.10785303529412,0.0</coordinates>\n" + 
				"            </Point>\n" + 
				"          </Placemark>\n" + 
				"  </Document>\n" + 
				"</kml>";
		game.sendKML(remark); // Should be your KML (will not work on case -1).
		System.out.println(res);
	}
	/** 
	 * Moves each of the robots along the edge, 
	 * in case the robot is on a node the next destination (next edge) is chosen (randomly).
	 * @param game
	 * @param gg
	 * @param log
	 */
	private static void moveRobots(game_service game, oop_graph gg) {
		List<String> log = game.move();
		ArrayList<OOP_Point3D> rs = new ArrayList<OOP_Point3D>();
		List<String> fs =  game.getFruits();
				if(log!=null) {
			long t = game.timeToEnd();
			
			for(int i=0;i<log.size();i++) {
				String robot_json = log.get(i);
				try {
					JSONObject line = new JSONObject(robot_json);
					JSONObject ttt = line.getJSONObject("Robot");
					int rid = ttt.getInt("id");
					int src = ttt.getInt("src");
					int dest = ttt.getInt("dest");
					String p = ttt.getString("pos");
					OOP_Point3D pp = new OOP_Point3D(p);
					rs.add(pp);
					double speed =  ttt.getInt("speed");
								
					if(dest==-1) {			
						dest = nextNode(gg, src);
						game.chooseNextEdge(rid, dest);
			//			System.out.println("Turn to node: "+dest+"  time to end:"+(t/1000));
					}
				} 
				catch (JSONException e) {e.printStackTrace();}
			}
		}
	}
	/**
	 * a very simple random walk implementation!
	 * @param g
	 * @param src
	 * @return
	 */
	private static int nextNode(oop_graph g, int src) {
		int ans = -1;
		Collection<oop_edge_data> ee = g.getE(src);
		Iterator<oop_edge_data> itr = ee.iterator();
		int s = ee.size();
		int r = (int)(Math.random()*s);
		int i=0;
		while(i<r) {itr.next();i++;}
		ans = itr.next().getDest();
		return ans;
	}
	private void init(game_service game) {
		
		String g = game.getGraph();
		List<String> fruits = game.getFruits();
		OOP_DGraph gg = new OOP_DGraph();
		gg.init(g);

		String info = game.toString();
		JSONObject line;
		try {
			line = new JSONObject(info);
			JSONObject ttt = line.getJSONObject("GameServer");
			int rs = ttt.getInt("robots");
			System.out.println(info);
			// the list of fruits should be considered in your solution
			Iterator<String> f_iter = game.getFruits().iterator();
			while(f_iter.hasNext()) {System.out.println(f_iter.next());}	
			int src_node = 0;  // arbitrary node, you should start at one of the fruits
			for(int a = 0;a<rs;a++) {
				game.addRobot(a);
			}
		}
		catch (JSONException e) {e.printStackTrace();}
		
	}
}
