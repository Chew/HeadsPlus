package io.github.thatsmusic99.headsplus.commands.maincommand;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.commands.CommandInfo;
import io.github.thatsmusic99.headsplus.commands.IHeadsPlusCommand;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigTextMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@CommandInfo(
		commandname = "info",
		permission = "headsplus.maincommand.info",
		subcommand = "Info",
		maincommand = true,
		usage = "/hp info"
)
public class Info implements IHeadsPlusCommand {

	// D
	@Override
	public String getCmdDescription(CommandSender cs) {
		return HeadsPlus.getInstance().getMessagesConfig().getString("descriptions.hp.info", cs);
	}

	@Override
	public boolean fire(String[] args, CommandSender sender) {
		getDebug().startTimings(sender, "info");
		try {
			sender.sendMessage(HeadsPlusConfigTextMenu.InfoTranslator.translate(sender));
        } catch (Exception e) {
		    DebugPrint.createReport(e, "Subcommand (info)", true, sender);
        }
		getDebug().stopTimings(sender, "info");
		return true;
	}

	@Override
	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
		return new ArrayList<>();
	}
}
