package dev.FlameKnight15;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;


public class Main extends JavaPlugin implements Listener{
	
	File cFile = new File(getDataFolder(), "config.yml");
	public FileConfiguration config = YamlConfiguration.loadConfiguration(cFile);
	String ignoreVar;
	String prefix;
	ProtocolManager protocolManager;
	
	@Override
	public void onEnable(){
		//Check to see if a config is loaded, if not, load the default one
		//if the config is loaded save it
		//if(this.getConfig() == null){
			this.getLogger().info("Generating Config...");
			saveDefaultConfig();
			config.options().copyDefaults(true);
	        config = YamlConfiguration.loadConfiguration(cFile);
	        protocolManager = ProtocolLibrary.getProtocolManager();
	       // auth();
	        
	        
	        
	        ChatMute mute = new ChatMute(this);
	        mute.muteChat();
		//registerEvents(this);
		registerEvents(this,  this, this);
		
        prefix = ChatColor.translateAlternateColorCodes('&', config.getString("chat-Prefix"));
        ignoreVar = ChatColor.translateAlternateColorCodes('&', config.getString("ignore-Var"));
	}
	
	@Override
	public void onDisable(){
		//mute.protocolManager.removePacketListeners(this);
		getLogger().info("onDisable has been invoked!");
		config = YamlConfiguration.loadConfiguration(cFile);
		config.options().copyDefaults(true);
		
	}
	
	public static void registerEvents(org.bukkit.plugin.Plugin plugin, Listener... listeners) {
		for (Listener listener : listeners) {
		Bukkit.getServer().getPluginManager().registerEvents(listener, plugin);
		}
	}
	

	
	
	
    /**
     * Handles the use of commands
     */
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("mm") || cmd.getName().equalsIgnoreCase("messagemute")){
			if(args.length == 1){
				if (args[0].equalsIgnoreCase("reload")) {
					if(sender.hasPermission("messagemute.reload")){
						if(YamlConfiguration.loadConfiguration(cFile) != null){
							//reloadConfig();
							config = YamlConfiguration.loadConfiguration(cFile);
							sender.sendMessage(prefix + ChatColor.GREEN + " MessageMute has been reloaded!");
						}else{
							saveDefaultConfig();
							config.options().copyDefaults(true);
							sender.sendMessage(prefix + ChatColor.GREEN + " MessageMute has been reloaded!");
						}
						
						
			            
			            return true;
					}
					else{
						sender.sendMessage(prefix + ChatColor.RED + " You may not perform this command!");
					}
				}
			} else
				if(args.length == 0){
					sender.sendMessage(prefix + ChatColor.GREEN + " Here are the available commands:");
					sender.sendMessage(prefix + ChatColor.RED + " Reload" + ChatColor.YELLOW + " reloads the plugin");
				}
		}
		//if(cmd.getName().equalsIgnoreCase("spawn")){
			//.out.println("FIRES from command - SPAWN");
			if(sender instanceof ConsoleCommandSender){
				//System.out.println("FIRES from command");
			    String command = cmd.toString();
			    String[] arugs = command.split(" ");
			    ArrayList<String> bCmds = new ArrayList<String>();
			    Player p = null;
			    
			    for(Player player : Bukkit.getServer().getOnlinePlayers()){
			    	for(int i = 0; i < arugs.length; i++){
			    		if(player.getName().equalsIgnoreCase(arugs[i])){
			    			p = player;
			    			break;
			    		}
			    	}
			    }
			    
			    for(String s : config.getConfigurationSection("Commands").getKeys(false)){
			    	if(p != null){
						if(p.hasPermission("mutemsg.cmd."+s)){
							for(String a : config.getStringList("Commands." + s + ".command")){
								bCmds.add(a);
							}
						}
			    	}
				}
			    for (String bCmd : bCmds) 
			    {
			        if(args[0].equalsIgnoreCase(bCmd))
			        {
			        	System.out.println("[MessageMute] The Command " + bCmd + " was cancelled for the player " + p.getName());
			            return false;
			            
			        }
			    }
			}
		//}
		return true;
	}
	
	@EventHandler
	public void cancelCommand(ServerCommandEvent e){
		//System.out.println("FIRES");
	    String command = e.getCommand();
	    String[] args = command.split(" ");
	    ArrayList<String> bCmds = new ArrayList<String>();
	    Player p = null;
	    
	    for(Player player : Bukkit.getServer().getOnlinePlayers()){
	    	for(int i = 0; i < args.length; i++){
	    		if(player.getName().equalsIgnoreCase(args[i])){
	    			p = player;
	    			break;
	    		}
	    	}
	    }
	    
	    for(String s : config.getConfigurationSection("Commands").getKeys(false)){
	    	if(p != null){
				if(p.hasPermission("mutemsg.cmd."+s)){
					for(String a : config.getStringList("Commands." + s + ".command")){
						bCmds.add(a);
					}
				}
	    	}
		}
	    for (String bCmd : bCmds) 
	    {
	        if(args[0].equalsIgnoreCase(bCmd))
	        {
	            e.setCancelled(true);
	            System.out.println("[MessageMute] The Command " + bCmd + " was cancelled for the player " + p.getName());
	        }
	    }
	}

	@EventHandler
	public void onTeleport (PlayerTeleportEvent e){
		for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
			if (ste.toString().contains("me.clip.ezprestige")) {
			    Player p = e.getPlayer();
                if(p.hasPermission("mutemsg.cmd.spawn")){
                    e.setCancelled(true);
                }
			}
		}
	}
	
/*	public static String uid = "%%__USER__%%";
	public boolean sts = true;
	     public void auth()
	     {
	       try
	       {
	         URLConnection localURLConnection = new URL("Your website link").openConnection();
	         localURLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
	         localURLConnection.connect();
	        
	         BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(localURLConnection.getInputStream(), Charset.forName("UTF-8")));
	        
	         StringBuilder localStringBuilder = new StringBuilder();
	         String str1;
	         while ((str1 = localBufferedReader.readLine()) != null) {
	           localStringBuilder.append(str1);
	         }
	         String str2 = localStringBuilder.toString();
	         if (str2.contains(String.valueOf(uid)))
	         {
	           disableLeak();
	           return;
	         }
	         this.sts = true;
	       }
	       catch (IOException localIOException)
	       {
	         localIOException.printStackTrace();
	         disableNoInternet();
	         return;
	       }
	     }
	    
	     public void disableLeak()
	     {
	         int x = 0;
	         while(x != 5000){
	           Bukkit.broadcastMessage(ChatColor.RED + "You leaked my plugin, 5k broadcast!");
	           x++;
	         }
	       getServer().getPluginManager().disablePlugin(this);
	       sts = false;
	     }
	    
	     public void disableNoInternet() {
	         Bukkit.broadcastMessage(ChatColor.RED + "You don't have a valid internet connection, please connect to the internet for the plugin to work!");
	         getServer().getPluginManager().disablePlugin(this);
	         sts = false;
	     }*/
}
