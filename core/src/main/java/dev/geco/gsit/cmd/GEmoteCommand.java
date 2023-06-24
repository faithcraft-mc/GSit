package dev.geco.gsit.cmd;

import org.jetbrains.annotations.*;

import org.bukkit.command.*;
import org.bukkit.entity.*;

import dev.geco.gsit.GSitMain;
import dev.geco.gsit.objects.*;

public class GEmoteCommand implements CommandExecutor {

    private final GSitMain GPM;

    public GEmoteCommand(GSitMain GPluginMain) { GPM = GPluginMain; }

    @Override
    public boolean onCommand(@NotNull CommandSender Sender, @NotNull Command Command, @NotNull String Label, String[] Args) {

        if(!(Sender instanceof Player)) {

            return true;
        }

        Player player = (Player) Sender;

        if(!GPM.getPManager().hasPermission(Sender, "Emote")) {

            return true;
        }

        if(Args.length == 0) {

            if(!GPM.getEmoteManager().isEmoting(player)) {

                return true;
            }

            if(!GPM.getEmoteManager().stopEmote(player)) {

                return true;
            }

            return true;
        }

        GEmote emote = GPM.getEmoteManager().getEmoteByName(Args[0]);

        if(emote == null) {

            return true;
        }

        if(!player.isValid()) {

            return true;
        }

        if(!GPM.getEnvironmentUtil().isInAllowedWorld(player)) {

            return true;
        }

        if(!GPM.getPManager().hasPermission(Sender, "ByPass.Region", "ByPass.*")) {

            if(GPM.getWorldGuardLink() != null && !GPM.getWorldGuardLink().checkFlag(player.getLocation(), GPM.getWorldGuardLink().getFlag("emote"))) {

                return true;
            }
        }

        GPM.getEmoteManager().startEmote(player, emote);
        return true;
    }

}