package me.frostingly.gequests;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class TestEvent implements Listener {

    /*
        This class is primarily to test certain events/features before adding them to wide-scale, nothing here is considered a "feature".
     */

    /*
    @EventHandler
    public void onMoveE(PlayerMoveEvent e) {
        Location fromLocation = e.getFrom();
        Location toLocation = e.getTo();

        Double fromXValue = fromLocation.getX();
        Double fromYValue = fromLocation.getY();
        Double fromZValue = fromLocation.getZ();

        Double toXValue = toLocation.getX();
        Double toYValue = toLocation.getY();
        Double toZValue = toLocation.getZ();
        if (fromXValue.intValue() != toXValue.intValue() || fromYValue.intValue() != toYValue.intValue() || fromZValue.intValue() != toZValue.intValue()) {
            System.out.println("Passed OK, moved a block!");
        }
    }*/

}
