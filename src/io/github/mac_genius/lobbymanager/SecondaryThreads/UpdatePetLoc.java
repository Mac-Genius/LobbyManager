package io.github.mac_genius.lobbymanager.SecondaryThreads;

import io.github.mac_genius.lobbymanager.ServerSettings;
import io.github.mac_genius.lobbymanager.Util.ReflectionUtil;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Created by Mac on 10/25/2015.
 */
public class UpdatePetLoc implements Runnable {
    private ServerSettings settings;

    public UpdatePetLoc(ServerSettings settings) {
        this.settings = settings;
    }

    @Override
    public void run() {
        for (Player player : settings.getPlayerPets().keySet()) {
            if (player.getLocation().distance(settings.getPlayerPets().get(player).getLocation()) > 5) {
                navigate((LivingEntity) settings.getPlayerPets().get(player), player.getLocation(), 1.2);
            }
        }
    }

    private void navigate(LivingEntity entity, Location location, double velocity) {
        try {
            Object entityLiving = ReflectionUtil.getMethod("getHandle", entity.getClass(), 0).invoke(entity);
            Object nav = ReflectionUtil.getMethod("getNavigation", entityLiving.getClass(), 0).invoke(entityLiving);
            ReflectionUtil.getMethod("a", nav.getClass(), 4).invoke(nav, location.getX(), location.getY(), location.getZ(), velocity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
