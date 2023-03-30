package dev.geco.gsit.manager;

import java.util.*;
import java.util.concurrent.*;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.scheduler.*;

import io.papermc.paper.threadedregions.scheduler.*;

import dev.geco.gsit.GSitMain;

public class TManager {

    private final GSitMain GPM;

    public TManager(GSitMain GPluginMain) { GPM = GPluginMain; }

    private final HashMap<UUID, Object> tasks = new HashMap<>();

    public List<UUID> getTasks() { return new ArrayList<>(tasks.keySet()); }

    public UUID run(Callback Call) { return run(Call, true, null); }

    public UUID run(Callback Call, boolean Sync) { return run(Call, Sync, null); }

    public UUID run(Callback Call, Entity Entity) { return run(Call, true, Entity); }

    public UUID run(Callback Call, boolean Sync, Entity Entity) {
        UUID uuid = UUID.randomUUID();
        if(GPM.isFoliaBased()) {
            if(Entity != null) {
                tasks.put(uuid, Entity.getScheduler().run(GPM, scheduledTask -> {
                    Call.call();
                    tasks.remove(uuid);
                }, null));
                return uuid;
            }
            ScheduledTask task;
            if(Sync) task = Bukkit.getGlobalRegionScheduler().run(GPM, scheduledTask -> {
                Call.call();
                tasks.remove(uuid);
            });
            else task = Bukkit.getAsyncScheduler().runNow(GPM, scheduledTask -> {
                Call.call();
                tasks.remove(uuid);
            });
            tasks.put(uuid, task);
        } else {
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    Call.call();
                    tasks.remove(uuid);
                }
            };
            tasks.put(uuid, task);
            if(Sync) task.runTask(GPM);
            else task.runTaskAsynchronously(GPM);
        }
        return uuid;
    }

    public UUID runDelayed(Callback Call, long Ticks) { return runDelayed(Call, true, null, Ticks); }

    public UUID runDelayed(Callback Call, boolean Sync, long Ticks) { return runDelayed(Call, Sync, null, Ticks); }

    public UUID runDelayed(Callback Call, Entity Entity, long Ticks) { return runDelayed(Call, true, Entity, Ticks); }

    public UUID runDelayed(Callback Call, boolean Sync, Entity Entity, long Ticks) {
        UUID uuid = UUID.randomUUID();
        if(GPM.isFoliaBased()) {
            if(Ticks <= 0) return run(Call, Sync, Entity);
            if(Entity != null) {
                tasks.put(uuid, Entity.getScheduler().runDelayed(GPM, scheduledTask -> {
                    Call.call();
                    tasks.remove(uuid);
                }, null, Ticks));
                return uuid;
            }
            ScheduledTask task;
            if(Sync) task = Bukkit.getGlobalRegionScheduler().runDelayed(GPM, scheduledTask -> {
                Call.call();
                tasks.remove(uuid);
            }, Ticks);
            else task = Bukkit.getAsyncScheduler().runDelayed(GPM, scheduledTask -> {
                Call.call();
                tasks.remove(uuid);
            }, Ticks * 50, TimeUnit.MILLISECONDS);
            tasks.put(uuid, task);
        } else {
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    Call.call();
                    tasks.remove(uuid);
                }
            };
            tasks.put(uuid, task);
            if(Sync) task.runTaskLater(GPM, Ticks);
            else task.runTaskLaterAsynchronously(GPM, Ticks);
        }
        return uuid;
    }

    public UUID runAtFixedRate(Callback Call, long Delay, long Ticks) { return runAtFixedRate(Call, true, null, Delay, Ticks); }

    public UUID runAtFixedRate(Callback Call, boolean Sync, long Delay, long Ticks) { return runAtFixedRate(Call, Sync, null, Delay, Ticks); }

    public UUID runAtFixedRate(Callback Call, Entity Entity, long Delay, long Ticks) { return runAtFixedRate(Call, true, Entity, Delay, Ticks); }

    public UUID runAtFixedRate(Callback Call, boolean Sync, Entity Entity, long Delay, long Ticks) {
        UUID uuid = UUID.randomUUID();
        if(GPM.isFoliaBased()) {
            if(Entity != null) {
                tasks.put(uuid, Entity.getScheduler().runAtFixedRate(GPM, scheduledTask -> {
                    Call.call();
                }, null, Delay <= 0 ? 1 : Delay, Ticks <= 0 ? 1 : Ticks));
                return uuid;
            }
            ScheduledTask task;
            if(Sync) task = Bukkit.getGlobalRegionScheduler().runAtFixedRate(GPM, scheduledTask -> {
                Call.call();
            }, Delay <= 0 ? 1 : Delay, Ticks <= 0 ? 1 : Ticks);
            else task = Bukkit.getAsyncScheduler().runAtFixedRate(GPM, scheduledTask -> {
                Call.call();
            }, Delay <= 0 ? 1 : Delay * 50, (Ticks <= 0 ? 1 : Ticks) * 50, TimeUnit.MILLISECONDS);
            tasks.put(uuid, task);
        } else {
            BukkitRunnable task = new BukkitRunnable() {
                @Override
                public void run() {
                    Call.call();
                }
            };
            tasks.put(uuid, task);
            if(Sync) task.runTaskTimer(GPM, Delay, Ticks);
            else task.runTaskTimerAsynchronously(GPM, Delay, Ticks);
        }
        return uuid;
    }

    public void cancel(UUID Task) {
        if(tasks.containsKey(Task)) {
            Object task = tasks.get(Task);
            if(task instanceof BukkitRunnable) ((BukkitRunnable) task).cancel();
            else ((ScheduledTask) task).cancel();
            tasks.remove(Task);
        }
    }

    public interface Callback { void call(); }

}