package me.noaz.testplugin.maps;

import org.bukkit.Location;
import org.bukkit.World;

/**
 * Contains the position of a location, with a given location type, for example "ffaspawn" or "blueflag".
 *
 * @author Noa Zetterman
 * @version 2020-04-26
 */
public class CustomLocation {
    private String locationType;
    private double x;
    private double y;
    private double z;

    public CustomLocation(String locationType, double x, double y, double z) {
        this.locationType = locationType;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * @param world The world the Location should be in.
     * @return A location object with this CustomLocation's position.
     */
    public Location getLocation(World world) {
        return new Location(world, x, y, z);
    }

    public String getLocationType() {
        return locationType;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
