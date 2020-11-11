package me.noaz.testplugin.weapons.tacticals;

import me.noaz.testplugin.TestPlugin;
import me.noaz.testplugin.player.PlayerExtension;
import me.noaz.testplugin.weapons.ThrowableItem;
import net.minecraft.server.v1_14_R1.MobEffect;
import net.minecraft.server.v1_14_R1.MobEffectList;
import net.minecraft.server.v1_14_R1.PacketPlayOutEntityEffect;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;

public class SensorGrenade extends ThrowableItem implements Tactical {
        private int duration = 20 * 5;

        public SensorGrenade(PlayerExtension playerExtension, TestPlugin plugin, int cooldownTimeInTicks) {
            super(playerExtension, playerExtension.getPlayer().getWorld(), plugin, 6, 1.3f, itemSlot, cooldownTimeInTicks, 0);
        }

        @Override
        protected void activateItem(Location itemLocation) {
            world.spawnParticle(Particle.FLASH, itemLocation, 10, 1, 0.20, 1, 1);
            Collection<Entity> entitiesInWorld = world.getEntities();
            for (Entity entity : entitiesInWorld) {
                if (entity instanceof Player
                        && ((Player) entity).getGameMode() != GameMode.SPECTATOR &&
                        isInRange((Player) entity, itemLocation) &&
                        playerExtension.playerIsOnEnemyTeam((Player) entity)) {

                    Player player = (Player) entity;

                    PotionEffect currentGlowEffect = player.getPotionEffect(PotionEffectType.GLOWING);
                    if(currentGlowEffect != null && currentGlowEffect.getDuration() < 5) {
                        player.removePotionEffect(PotionEffectType.GLOWING);
                    }

                    /*PacketPlayOutEntityEffect effect = new PacketPlayOutEntityEffect(player.getEntityId(),
                            new MobEffect(MobEffectList.fromId(24), 1000, 20, false, false));

                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(effect);

                    See: https://bukkit.org/threads/glowing-for-one-person.446790/
                    */

                    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration, 4, false, false, false));
                }
            }
        }

        private boolean isInRange(Player player, Location center) {
            return Math.sqrt(Math.pow((center.getX()-player.getLocation().getX()),2) +
                    Math.pow((center.getZ()-player.getLocation().getZ()),2)) < 2.4 &&
                    (player.getLocation().getY() - center.getY()) < 1 && (player.getLocation().getY() - center.getY()) > -2;
        }
    }


