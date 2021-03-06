package io.github.thatsmusic99.headsplus.listeners;

import io.github.thatsmusic99.headsplus.HeadsPlus;
import io.github.thatsmusic99.headsplus.api.HPPlayer;
import io.github.thatsmusic99.headsplus.commands.SellHead;
import io.github.thatsmusic99.headsplus.config.HeadsPlusConfigHeads;
import io.github.thatsmusic99.headsplus.nms.NMSIndex;
import io.github.thatsmusic99.headsplus.nms.NMSManager;
import io.github.thatsmusic99.headsplus.reflection.NBTManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.UUID;

public class MaskEvent implements Listener {

    private static final HashMap<UUID, BukkitRunnable> maskMonitors = new HashMap<>();

    @EventHandler
    public void onMaskPutOn(InventoryClickEvent e) {
        HeadsPlus hp = HeadsPlus.getInstance();
        ItemStack item;
        boolean shift = e.isShiftClick();
        // If we're shift clicking
        if (shift) {
            // We need to get the current item
            item = e.getCurrentItem();
            ItemStack currHelmet = e.getWhoClicked().getInventory().getHelmet();
            if (!(currHelmet == null || currHelmet.getType().equals(Material.AIR))) return;
        } else {
            if (e.getAction().equals(InventoryAction.PICKUP_ALL)) return;
            item = e.getCursor();
        }
        if (hp.getConfiguration().getPerks().mask_powerups) {
            if (e.getRawSlot() == getSlot() || (shift && e.getRawSlot() != getSlot())) {
                checkMask((Player) e.getWhoClicked(), item);
            }
        }
    }

    public static void checkMask(Player player, ItemStack item) {
        HeadsPlus hp = HeadsPlus.getInstance();
        ConfigurationSection maskSettings = hp.getConfiguration().getMechanics().getConfigurationSection("masks");
        int period = maskSettings.getInt("check-interval");
        int reset = maskSettings.getInt("reset-after-x-intervals");
        NMSManager nms = hp.getNMS();
        if (item != null) {
            if (nms.isSkull(item)) {
                String s = NBTManager.getType(item);
                if (hp.getConfiguration().getMechanics().getBoolean("sellhead-ids-case-sensitive")) {
                    s = s.toLowerCase();
                }
                if (SellHead.getRegisteredIDs().contains(s)) {
                    HPPlayer pl = HPPlayer.getHPPlayer(player);
                    UUID uuid = player.getUniqueId();
                    if (maskMonitors.containsKey(uuid)) {
                        pl.clearMask();
                        maskMonitors.get(uuid).cancel();
                        maskMonitors.remove(uuid);
                    }
                    pl.addMask(s);
                    final String type = s;
                    maskMonitors.put(uuid, new BukkitRunnable() {

                        private int currentInterval = 0;

                        @Override
                        public void run() {
                            ItemStack helmet = player.getInventory().getHelmet();
                            currentInterval++;
                            if (helmet == null
                                    || helmet.getType() == Material.AIR
                                    || !NBTManager.getType(helmet).equals(type)
                                    || !player.isOnline()) {
                                pl.clearMask();
                                maskMonitors.remove(uuid);
                                cancel();
                            } else if (!maskMonitors.containsKey(uuid)) {
                                pl.clearMask();
                                cancel();
                            } else if (currentInterval == reset) {
                                pl.refreshMasks();
                                currentInterval = 0;
                            }
                        }
                    });
                    maskMonitors.get(uuid).runTaskTimer(hp, period, period);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerFall(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            if (Bukkit.getPlayer(player.getUniqueId()) == null) return; // Citizens NPC
            HeadsPlus hp = HeadsPlus.getInstance();
            if (hp.getConfiguration().getPerks().mask_powerups) {
                HPPlayer pl = HPPlayer.getHPPlayer((Player) e.getEntity());
                if (pl.isIgnoringFallDamage()) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        if (maskMonitors.containsKey(e.getPlayer().getUniqueId())) {
            HPPlayer player = HPPlayer.getHPPlayer(e.getPlayer());
            player.clearAllMasks();
            maskMonitors.remove(e.getPlayer().getUniqueId());
        }
    }

    private int getSlot() {
        NMSIndex nms = HeadsPlus.getInstance().getNMSVersion();
        if (nms.getOrder() == 9 || nms.getOrder() == 10) {
            return 39;
        } else {
            return 5;
        }
    }
}
