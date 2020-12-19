package me.lolieg.battleboys.generators;

import com.mojang.serialization.Codec;
import me.lolieg.battleboys.Battleboys;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class BiomeMixChuckGenerator extends ChunkGenerator {
    private final Battleboys plugin;

    public BiomeMixChuckGenerator(Battleboys battleboys) {
        this.plugin = battleboys;
    }

    @Override
    public @NotNull ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int x, int z, @NotNull BiomeGrid biome) {
            ChunkData chunkData = generateChunkData(world, random, x, z, biome);


        return super.generateChunkData(world, random, x, z, biome);
    }
}
