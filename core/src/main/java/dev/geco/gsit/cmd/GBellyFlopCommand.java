package dev.geco.gsit.cmd;

import org.jetbrains.annotations.*;

import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.util.*;

import dev.geco.gsit.GSitMain;
import dev.geco.gsit.objects.*;

public class GBellyFlopCommand implements CommandExecutor {

    private final GSitMain GPM;

    public GBellyFlopCommand(GSitMain GPluginMain) { GPM = GPluginMain; }

    @Override
    public boolean onCommand(@NotNull CommandSender Sender, @NotNull Command Command, @NotNull String Label, String[] Args) {

        if(!(Sender instanceof Player)) {

            return true;
        }

        Player player = (Player) Sender;

        if(!GPM.getPManager().hasPermission(Sender, "BellyFlop")) {

            return true;
        }

        if(!GPM.getPoseManager().isAvailable()) {

            String v = Bukkit.getServer().getClass().getPackage().getName();
            v = v.substring(v.lastIndexOf('.') + 1);

            return true;
        }

        if(GPM.getPoseManager().isPosing(player) && GPM.getPoseManager().getPose(player).getPose() == Pose.SWIMMING) {

            GPM.getPoseManager().removePose(player, GetUpReason.GET_UP);
            return true;
        }

        if(!player.isValid() || player.isSneaking() || !player.isOnGround() || player.isInsideVehicle() || player.isSleeping()) {

            return true;
        }

        if(!GPM.getEnvironmentUtil().isInAllowedWorld(player)) {

            return true;
        }

        Location playerLocation = player.getLocation();

        Block block = playerLocation.getBlock().isPassable() ? playerLocation.clone().subtract(0, 0.0625, 0).getBlock() : playerLocation.getBlock();

        if(GPM.getCManager().MATERIALBLACKLIST.contains(block.getType())) {

            return true;
        }

        boolean overSize = false;

        try {

            for(BoundingBox boundingBox : block.getCollisionShape().getBoundingBoxes()) if(boundingBox.getMaxY() > 1.25) overSize = true;
        } catch (Exception | Error ignored) { }

        if(!GPM.getCManager().ALLOW_UNSAFE && !(block.getRelative(BlockFace.UP).isPassable() && !overSize && (!block.isPassable() || !GPM.getCManager().CENTER_BLOCK))) {

            return true;
        }

        if(!GPM.getPManager().hasPermission(Sender, "ByPass.Region", "ByPass.*")) {

            if(GPM.getWorldGuardLink() != null && !GPM.getWorldGuardLink().checkFlag(block.getLocation(), GPM.getWorldGuardLink().getFlag("pose"))) {

                return true;
            }

            if(GPM.getGriefPreventionLink() != null && !GPM.getGriefPreventionLink().check(block.getLocation(), player)) {

                return true;
            }

            if(GPM.getPlotSquaredLink() != null && !GPM.getPlotSquaredLink().canCreateSeat(block.getLocation(), player)) {

                return true;
            }
        }

        if(!GPM.getCManager().SAME_BLOCK_REST && !GPM.getPoseManager().kickPose(block, player)) {

            return true;
        }

        if(GPM.getPoseManager().createPose(block, player, Pose.SWIMMING) == null) {
        }
        return true;
    }

}