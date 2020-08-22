package me.noaz.testplugin.weapons.tacticals;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.weapons.ThrowableItem;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;

public class Flashbang extends ThrowableItem implements Tactical {
    private int duration = 20 * 5;


    public Flashbang(PlayerExtension playerExtension, TestPlugin plugin, int cooldownTimeInTicks) {
        super(playerExtension, playerExtension.getPlayer().getWorld(), plugin, 4, 1.3f, itemSlot, cooldownTimeInTicks, 0);
    }

    @Override
    protected void activateItem(Location itemLocation) {
        world.spawnParticle(Particle.FLASH, itemLocation, 10, 1, 0.20, 1, 1);
        //System.out.println(Particle.FLASH.getDataType()); = Void

        Collection<Entity> entitiesInWorld = world.getEntities();
        for (Entity entity : entitiesInWorld) {
            if (entity instanceof Player
                    && ((Player) entity).getGameMode() != GameMode.SPECTATOR &&
                    isInRange((Player) entity, itemLocation) &&
                    playerExtension.playerIsOnEnemyTeam((Player) entity)) {

                Player player = (Player) entity;

                PotionEffect currentBlindnessEffect = player.getPotionEffect(PotionEffectType.BLINDNESS);
                if(currentBlindnessEffect != null && currentBlindnessEffect.getDuration() < 5) {
                    player.removePotionEffect(PotionEffectType.BLINDNESS);
                }

                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 4, false, false, false));
            }
        }
    }

    private boolean isInRange(Player player, Location center) {
        return Math.sqrt(Math.pow((center.getX()-player.getLocation().getX()),2) +
                Math.pow((center.getZ()-player.getLocation().getZ()),2)) < 3.4 &&
                (player.getLocation().getY() - center.getY()) < 1 && (player.getLocation().getY() - center.getY()) > -2;
    }
}
