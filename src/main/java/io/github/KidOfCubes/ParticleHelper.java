package io.github.KidOfCubes;

import com.destroystokyo.paper.ParticleBuilder;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static io.github.KidOfCubes.RpgPlugin.logger;

public class ParticleHelper {
    private static final ExecutorService executorService = Executors.newFixedThreadPool(10);
    public static final double TAU = Math.PI*2;
    public static void particleRing(Particle particle, Color color, Location location, int particlesInRing, float radius){
        long startTime = System.nanoTime();
        ParticleBuilder builder = new ParticleBuilder(particle)
                .color(color).location(location)
                .receivers((int) (radius+32));


        executorService.execute(() -> {
            //double offset = (Math.random()*(Math.PI*2 / particlesInRing));
            //offset = (offset*2)-(offset);
            double increment = TAU / particlesInRing;
            for (double radians = 0; radians < TAU; radians += increment) {
                builder.location(location.getWorld(), location.getX()+Math.cos(radians) * radius,location.getY(),location.getZ()+Math.sin(radians) * radius).spawn();
            }
        });
        long endTime = System.nanoTime();
        double duration = (endTime - startTime)/1000000.0;
        logger.info("particles took "+duration+" with radius of "+radius);
    }
}
