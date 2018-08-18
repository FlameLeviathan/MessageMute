package dev.FlameKnight15;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class ChatMute {
	Main main;
	public ChatMute(Main m){
		main = m;
	}
	
	public void muteChat(){
		
		main.protocolManager.addPacketListener(new PacketAdapter(main, ListenerPriority.NORMAL, PacketType.Play.Server.CHAT) {
			@SuppressWarnings("rawtypes")
			@Override
            public void onPacketSending(PacketEvent e) {
				JSONParser parser = new JSONParser();
				
				if (e.getPacketType() == PacketType.Play.Server.CHAT){
					PacketContainer packet = e.getPacket(); //Packet being delivered
					StructureModifier<WrappedChatComponent> components = packet.getChatComponents(); //Components from the message grabbed by ProtLib
					String message = "Something is wrong. Contact the dev of MessageMute!"; //Message sent in text that will be taken from JSON file
					ArrayList<String> msgArray = new ArrayList<String>();
					
					try{
	                    JSONObject json =(JSONObject) parser.parse(components.read(0).getJson()); //Get the components and write them to a JSON file
	                    
	                    /*try (FileWriter file = new FileWriter("plugins/MessageMute/file.json")) {
	            			file.write(json.toJSONString());
	            			//System.out.println("Json File");
	                    } catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
*/	                    
	                   
	                    //JSONObject jObject = new JSONObject();
						Set keys = json.keySet();
	                    Iterator a = keys.iterator();
	                    while(a.hasNext()) {
	                    	String key = (String)a.next();
	                        // loop to get the dynamic key
	                        String value = (String)json.get(key).toString();
	                        String[] textVal = value.split(",");
	                        //System.out.println("Val: " + textVal.length);
	                        for(int i =0; i < textVal.length; i++){
	                        	if(textVal[i].contains("text")){
	                        		if(textVal != null || textVal.length != 0) {
										try {
											String o = textVal[i];
											//System.out.println("O - " + i + ": " + o);//Test Output
											String indexed = o.substring(o.indexOf("\"text\"") + 8);
											String mess;
											if(indexed.contains("\"")) {
												mess = indexed.substring(0, indexed.indexOf('"'));
											} else{
												mess = indexed;
											}


											if (mess.contains("\\/")) {
												mess = mess.replace("\\/", "/");
											}
											//System.out.println("O(text) - " + i + ": " + indexed.substring(0, indexed.indexOf('"')));//Test Output
											msgArray.add(mess);
										} catch (StringIndexOutOfBoundsException excep) {
											excep.printStackTrace();
										}
									}
	                        	}
	                        }
	                        StringBuilder builder = new StringBuilder();
	                        for(String str : msgArray){
		            			builder.append(str);
		            		}
		            		message = builder.toString();
	                    }
	                  }catch(ParseException ex){
	                    ex.printStackTrace();
	                    main.getLogger().info("PLEASE CONTACT THE DEV SOMETHING HAS GONE WRONG AND THEY NEED TO FIX IT");
	                 }
					
					
					for(Player p : Bukkit.getServer().getOnlinePlayers()){
						ArrayList<String> blockedMsgs = new ArrayList<String>();
						
						if(p.hasPermission("mutemsg.mute.muteall")){//If they have the perm to mute all messages cancel all msg sending
							if(e.getPlayer() == p){
								e.setCancelled(true);//getRecipients().remove(p);
							}
						}
				
						for(String s : main.config.getConfigurationSection("Messages").getKeys(false)){
							if(p.hasPermission("mutemsg.mute."+s)){
								for(String a : main.config.getStringList("Messages." + s + ".Msg")){
									blockedMsgs.add(a);
								}
							}
						}
						
						for(String msg : blockedMsgs){
							// Go through and see how much of a sentence matches a muted entry taking into account the ignored phrases
							msg = ChatColor.stripColor(msg);
							//System.out.println("MSG: " + message);
							if(msg.contains(main.ignoreVar)){
								String[] ignoreMsg = msg.split(" "); //Split the msg from the config up into an array
								String[] msgSent = message.split(" "); //Message sent by the player split into an array
								//System.out.println("Ignore: "+ignoreMsg.length);
								//System.out.println("Msg: " +msgSent.length);
								int numCorrect = 0; //Number of words that match the muted sentence
								int ignoreMsgCorrect = ignoreMsg.length; //Length of the ignoreMsg - We will check to see how many words match to determine if we mute the text or not
								
								if(ignoreMsg.length == msgSent.length){
									//Check how many words match from the sentence while taking into account the ignored portions of the text
									for(int b = 0; b < ignoreMsg.length; b++){
										if(!(ignoreMsg[b].contains(main.ignoreVar))){
											if(!(ignoreMsg[b].equals(msgSent[b]))){
												break;
											} else{
												numCorrect++;
											}
										}
										else{
											ignoreMsgCorrect--;
										}
									}
									//System.out.println("Correct: " + numCorrect);
									//System.out.println("Inorrect: " + ignoreMsgCorrect);
									
								}
								
								if(numCorrect == ignoreMsgCorrect){
									if(e.getPlayer() == p){
										e.setCancelled(true);;
										break;
									}
								}
							} else
							if(message.equals(msg)){
								if(e.getPlayer() == p){
									e.setCancelled(true);
									break;
								}
							}
						}
					}
				}
			}
		});
	}
}
