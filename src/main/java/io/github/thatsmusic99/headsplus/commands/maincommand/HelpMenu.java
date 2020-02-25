package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigTextMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(
        commandname = "help",
        permission = "headsplus.maincommand",
        subcommand = "Help",
        maincommand = true,
        usage = "/hp <help|Page No.> [Page No.]"
)
public class HelpMenu implements IHeadsPlusCommand {

    // I

	private void helpNoArgs(CommandSender sender) {
        HeadsPlusConfigTextMenu.HelpMenuTranslator.translateHelpMenu(sender, 1);
	}
	private void helpNo(CommandSender sender, String str) {
        HeadsPlusConfigTextMenu.HelpMenuTranslator.translateHelpMenu(sender, Integer.parseInt(str));
	}

	private void helpCmd(CommandSender cs, String cmdName) {
        if (cs.hasPermission("headsplus.maincommand")) {
            IHeadsPlusCommand pe = null;
            for (IHeadsPlusCommand key : HeadsPlus.getInstance().getCommands()) {
                if (key.getClass().getAnnotation(CommandInfo.class).commandname().equalsIgnoreCase(cmdName)) {
                    pe = key;
                    break;
                }
            }
            if (pe != null) {
                cs.sendMessage(HeadsPlusConfigTextMenu.HelpMenuTranslator.translateCommandHelp(pe, cs));
            } else {
                helpNoArgs(cs);
            }
        }
    }

	@Override
	public String getCmdDescription(CommandSender cs) {
		return HeadsPlus.getInstance().getMessagesConfig().getString("descriptions.hp.help", cs);
	}

	@Override
	public boolean fire(String[] args, CommandSender sender) {
        getDebug().startTimings(sender, "help");
	    try {
            if (args.length == 0) {
                helpNoArgs(sender);
            } else if (args.length == 1) {
                if (args[0].matches("^[0-9]+$")) {
                    helpNo(sender, args[0]);
                } else if (args[0].equalsIgnoreCase("help")) {
                    helpNoArgs(sender);
                } else {
                    helpNoArgs(sender);
                }
            } else {
                if (args[0].equalsIgnoreCase("help")) {
                    if (args[1].matches("^[0-9]+$")) {
                        helpNo(sender, args[1]);
                    } else {
                        helpCmd(sender, args[1]);
                    }
                } else {
                    helpNoArgs(sender);
                }
            }
        } catch (Exception e) {
	        DebugPrint.createReport(e, "Subcommand (help)", true, sender);
        }

        getDebug().stopTimings(sender, "help");
        return true;
	}

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length == 2) {
            List<String> commands = new ArrayList<>();
            for (IHeadsPlusCommand key : HeadsPlus.getInstance().getCommands()) {
                CommandInfo command = key.getClass().getAnnotation(CommandInfo.class);
                if (sender.hasPermission(command.permission())) {
                    if (command.maincommand()) {
                        commands.add(command.subcommand());
                    }
                }
            }
            List<String> results = new ArrayList<>();
            StringUtil.copyPartialMatches(args[1], commands, results);
            return results;
        }
        return new ArrayList<>();
    }
}
