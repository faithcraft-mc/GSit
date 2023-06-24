package dev.geco.gsit.cmd;

import org.jetbrains.annotations.*;

import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;

import dev.geco.gsit.GSitMain;
import dev.geco.gsit.objects.*;

public class GCrawlCommand implements CommandExecutor {

    private final GSitMain GPM;

    public GCrawlCommand(GSitMain GPluginMain) { GPM = GPluginMain; }

    @Override
    public boolean onCommand(@NotNull CommandSender Sender, @NotNull Command Command, @NotNull String Label, String[] Args) {

        if(!(Sender instanceof Player)) {

            return true;
        }

        Player player = (Player) Sender;

        if(!GPM.getPManager().hasPermission(Sender, "Crawl")) {

            return true;
        }

        if(!GPM.getCrawlManager().isAvailable()) {

            String v = Bukkit.getServer().getClass().getPackage().getName();
            v = v.substring(v.lastIndexOf('.') + 1);

            return true;
        }

        if(Args.length == 0) {

            if(GPM.getCrawlManager().isCrawling(player)) {

                GPM.getCrawlManager().stopCrawl(player, GetUpReason.GET_UP);
                return true;
            }

            if(!player.isValid() || !player.isOnGround() || player.isInsideVehicle() || player.isSleeping()) {

                return true;
            }

            if(!GPM.getPManager().hasPermission(Sender, "ByPass.Region", "ByPass.*")) {

                if(!GPM.getEnvironmentUtil().isInAllowedWorld(player)) {

                    return true;
                }
            }

            if(GPM.getWorldGuardLink() != null && !GPM.getWorldGuardLink().checkFlag(player.getLocation(), GPM.getWorldGuardLink().getFlag("crawl"))) {

                return true;
            }

            if(GPM.getCrawlManager().startCrawl(player) == null) {
            }
            return true;
        }

        if(Args[0].equalsIgnoreCase("toggle") && GPM.getCManager().C_DOUBLE_SNEAK) {

            if(GPM.getPManager().hasPermission(Sender, "CrawlToggle")) {

                if(GPM.getToggleManager().canCrawl(player.getUniqueId())) {

                    GPM.getToggleManager().setCanCrawl(player.getUniqueId(), false);

                } else {

                    GPM.getToggleManager().setCanCrawl(player.getUniqueId(), true);

                }
            } else Bukkit.dispatchCommand(Sender, Label);
        } else Bukkit.dispatchCommand(Sender, Label);

        return true;
    }

}