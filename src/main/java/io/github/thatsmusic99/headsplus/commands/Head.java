package io.github.thatsmusic99.headsplus.commands;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.maincommand.DebugPrint;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMainConfig.SelectorList;
import io.github.thatsmusic99.headsplus.config.HeadsPlusMessagesManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@CommandInfo(
        commandname = "head",
        permission = "headsplus.head",
        subcommand = "Head",
        maincommand = false,
        usage = "/head <IGN> [Player]"
)
public class Head implements CommandExecutor, IHeadsPlusCommand, TabCompleter {

    private final HeadsPlus hp = HeadsPlus.getInstance();
    private final HeadsPlusMessagesManager hpc = hp.getMessagesConfig();

    private List<String> selectors = Arrays.asList("@a", "@p", "@s", "@r");

    private boolean startsWithSelector(String arg) {
        for(String selector : selectors) {
            if(arg.startsWith(selector)) return true;
        }
        return false;
    }

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return fire(args, sender);
    }

	private void giveHead(Player p, String n) {
		ItemStack skull = hp.getNMS().getSkullMaterial(1);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta = hp.getNMS().setSkullOwner(n, meta);

        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', hp.getHeadsConfig().getConfig().getString("player.display-name").replaceAll("\\{player}", n)));
        skull.setItemMeta(meta);
        Location playerLoc = (p).getLocation();
        double playerLocY = playerLoc.getY() + 1;
        playerLoc.setY(playerLocY);
        World world = (p).getWorld();
        world.dropItem(playerLoc, skull).setPickupDelay(0);
	}

	private void giveH(String[] args, CommandSender sender, Player p) {
        Player p2 = sender instanceof Player ? (Player) sender : null;
	    SelectorList blacklist = hp.getConfiguration().getHeadsBlacklist();
        SelectorList whitelist = hp.getConfiguration().getHeadsWhitelist();
        List<String> bl = new ArrayList<>();
        for (String str : blacklist.list) {
            bl.add(str.toLowerCase());
        }
        List<String> wl = new ArrayList<>();
        for (String str : whitelist.list) {
            wl.add(str.toLowerCase());
        }

        boolean blacklistOn = blacklist.enabled;
        boolean wlOn = whitelist.enabled;
        String head = args[0].toLowerCase();
        if (p.getInventory().firstEmpty() == -1) {
            sender.sendMessage(hpc.getString("commands.head.full-inv", p2));
            return;
        }
        if (wlOn) {
            if (blacklistOn) {
                if (wl.contains(head)) {
                    if (!bl.contains(head)) {
                        giveHead(p, args[0]);
                    } else if (sender.hasPermission("headsplus.bypass.blacklist")) {
                        giveHead(p, args[0]);
                    } else {
                        sender.sendMessage(hpc.getString("commands.head.blacklist-head", p2));
                    }
                } else if (sender.hasPermission("headsplus.bypass.whitelist")) {
                    if (!bl.contains(head)) {
                        giveHead(p, args[0]);
                    } else if (sender.hasPermission("headsplus.bypass.blacklist")) {
                        giveHead(p, args[0]);
                    } else {
                        sender.sendMessage(hpc.getString("commands.head.blacklist-head", p2));
                    }
                } else {
                    sender.sendMessage(hpc.getString("commands.head.whitelist-head", p2));
                }
            } else {
                if (wl.contains(head)) {
                    giveHead(p, args[0]);
                } else if (sender.hasPermission("headsplus.bypass.whitelist")){
                    giveHead(p, args[0]);
                } else {
                    sender.sendMessage(hpc.getString("commands.head.whitelist-head", p2));
                }
            }
        } else {
            if (blacklistOn) {
                if (!bl.contains(head)) {
                    giveHead(p, args[0]);
                } else if (sender.hasPermission("headsplus.bypass.blacklist")){
                    giveHead(p, args[0]);
                } else {
                    sender.sendMessage(hpc.getString("commands.head.blacklist-head", p2));
                }
            } else {
                giveHead(p, args[0]);
            }
        }
    }

    @Override
    public String getCmdDescription(CommandSender sender) {
        return hpc.getString("descriptions.head", sender);
    }

    @Override
    public boolean fire(String[] args, CommandSender sender) {
        getDebug().startTimings(sender, "head");
	    try {
	        if (sender.hasPermission("headsplus.head")) {
	            if (args.length > 1) {
	                if (sender.hasPermission("headsplus.head.others")) {
                        if (sender instanceof BlockCommandSender && startsWithSelector(args[0]) && startsWithSelector(args[1])) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "minecraft:execute as " + args[1] + " run head " + args[0] + " " + args[1]);
                        } else if (hp.getNMS().getPlayer(args[1]) != null) {

                            boolean b = args[0].matches("^[A-Za-z0-9_]+$") && (2 < args[0].length()) && (args[0].length() < 17);
                            if (b) {
                                String[] s = new String[2];
                                s[0] = args[0];
                                s[1] = args[1];
                                giveH(s, sender, hp.getNMS().getPlayer(args[1]));
                                return true;
                            } else if (!args[0].matches("^[A-Za-z0-9_]+$")) {
                                sender.sendMessage(hpc.getString("commands.head.alpha-names", sender));
                            } else if (args[0].length() < 3) {
                                sender.sendMessage(hpc.getString("commands.head.head-too-short", sender));
                            } else {
                                sender.sendMessage(hpc.getString("commands.head.head-too-long", sender));
                            }
                        } else {
                            sender.sendMessage(hpc.getString("commands.errors.player-offline", sender));
                        }
	                    return true;
	                } else {
	                    sender.sendMessage(hpc.getString("commands.errors.no-perm", sender));
	                }
	            } else if (args.length > 0) {
	                if (sender instanceof Player) {
	                    Player p = (Player) sender;
	                    boolean b = args[0].matches("^[A-Za-z0-9_]+$") && (2 < args[0].length()) && (args[0].length() < 17);
	                    if (b) {
	                        giveH(args, sender, p);
	                        return true;
	                    }
	                } else {
	                    sender.sendMessage(hpc.getString("commands.errors.not-a-player", sender));
	                }
	            } else {
	                sender.sendMessage(hpc.getString("commands.errors.invalid-args", sender));
	            }
	        } else {
	            sender.sendMessage(hpc.getString("commands.errors.no-perm", sender));
	        }

        } catch (Exception e) {
	        DebugPrint.createReport(e, "Command (head)", true, sender);
        }
	    getDebug().stopTimings(sender, "head");
        return false;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> results = new ArrayList<>();
        if (args.length == 2) {
            StringUtil.copyPartialMatches(args[1], IHeadsPlusCommand.getPlayers(), results);
        } else if (args.length == 3 && sender.hasPermission("headsplus.head.others")) {
            StringUtil.copyPartialMatches(args[2], IHeadsPlusCommand.getPlayers(), results);
        }
        return results;
    }
}
	
