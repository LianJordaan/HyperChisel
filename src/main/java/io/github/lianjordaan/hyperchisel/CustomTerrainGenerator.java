package io.github.lianjordaan.hyperchisel;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Arrays;
import java.util.Random;

public class CustomTerrainGenerator extends ChunkGenerator {
    @Override
    public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
        ChunkData chunkData = createChunkData(world);

        for (int blockX = 0; blockX < 16; blockX++) {
            for (int blockZ = 0; blockZ < 16; blockZ++) {
                chunkData.setBlock(blockX, -64, blockZ, Material.BEDROCK);
            }
        }

        return chunkData;
    }

    @Override
    public boolean shouldGenerateStructures() {
        return false;
    }
}
