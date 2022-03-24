package io.github.KidOfCubes;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

import static io.github.KidOfCubes.RpgPlugin.logger;

public class ParticleHelper {
    public static void particleRing(Particle particle, Color color, Location location, int particlesInRing, float radius){
        for (int degrees = 0; degrees < 360; degrees += 360f / particlesInRing) {
            double offset = (Math.random()*(360f / particlesInRing));
            offset = (offset*2)-(offset);
            new ParticleBuilder(particle)
                    .color(color)
                    .location(location.getWorld(),location.getX()+Math.cos(Math.toRadians(degrees+offset)) * radius,
                            location.getY(),
                            location.getZ()+Math.sin(Math.toRadians(degrees+offset))  * radius)
                    .receivers(64)
                    .spawn();
        }
    }
}
