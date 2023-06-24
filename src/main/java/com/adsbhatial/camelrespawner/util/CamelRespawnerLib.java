package com.adsbhatial.camelrespawner.util;

import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.world.level.levelgen.structure.*;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R1.CraftChunk;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.entity.Entity;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.HolderSet.Direct;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;


public class CamelRespawnerLib {


    public static boolean entityInsideDesertVillage(Entity entity, boolean debug) {
        CraftWorld world = (CraftWorld) entity.getWorld();

        boolean insidestructure = insideStructure(world, entity.getLocation(), BuiltinStructures.VILLAGE_DESERT, 16, 0, debug);

        if (!insidestructure) {
            //if(debug){	log("failed to find End City.");	};
            return false;
        }
        return true;
    }

    @SuppressWarnings("unused")
    private static boolean insideStructure(
            World world,
            Location location,
            ResourceKey<Structure> bukkitStructureType,
            int searchRadius,
            int size,
            boolean debug
    ) {
        //if(debug){	log("is - location=" + location);	};
        Location structureLocation;
        Pair<BlockPos, Holder<Structure>> a = locateStructure((CraftWorld) world, location, bukkitStructureType, searchRadius, false, debug);
        if(a == null) {
            //if(debug){	log("a=null");	};
            return false;
        }
        BlockPos bp = a.getFirst();
        //if(debug){	log("x=" + bp.getX());	};
        //if(debug){	log("y=" + bp.getY());	};
        //if(debug){	log("z=" + bp.getZ());	};
        structureLocation = new Location(world, bp.getX(), bp.getY(), bp.getZ());
        if (structureLocation == null) {
            structureLocation = location;
        }
        // Get the chunk at the structure location and cast to CraftChunk
        CraftChunk craftChunk = (CraftChunk) world.getChunkAt(structureLocation);

        // Get the chunk from the reference
        ChunkAccess chunk = craftChunk.getHandle(ChunkStatus.FULL);

        if (chunk == null) {
            //if(debug){	log("chunk=null");	};
            return false;
        }
        // Get the structure start map from the NMS Chunk
        // Suppress warnings are again needed due to generic cast and type erasure
        Map<Structure, StructureStart> structureStartMap = chunk.getAllStarts();
        chunk.getAllStarts();
        chunk.getAllStarts();
        chunk.getAllStarts();
        chunk.getAllStarts();

        if (structureStartMap == null||structureStartMap.isEmpty()) {
            //if(debug){	log("structureStartMap=null");	};
            return false;
        }
        StructureStart structureStart = null;
        //if(debug){	log("structureStartMap.size()=" + structureStartMap.size());	};
        for (Structure structure : structureStartMap.keySet()) {
            // Check type of structure feature
            //if(debug){	log("structure=" + structure.toString());	};
            //if(debug){	log("structureStart=" + structureStartMap.get(structure).toString());	};
            structureStart = structureStartMap.get(structure);
            break;

        }
        // Check if the structure was actually found
        if (structureStart == null) {
            //if(debug){	log("structureStart=null");	};
            return false;
        }
        int i = 0;
        // Finally, get the bounding box of the structure
        for (StructurePiece piece : chunk.getStartForStructure(structureStart.getStructure()).getPieces()) {
            //if(debug){	log("i=" + piece.getBoundingBox().maxX() + "," + piece.getBoundingBox().minX());	};
            //if(debug){	log("i=" + piece.getBoundingBox().maxZ() + "," + piece.getBoundingBox().minZ());	};
            //if(debug){	log("i=" + piece.getType().toString());	};
            if(piece.getBoundingBox().isInside(new Vec3i( location.getBlockX(), location.getBlockY(), location.getBlockZ()))) {
                return true;
            }
            i++;
        }
        //if(debug){	log("i=" + i);	};
        boolean inside = false;

        BoundingBox sboundingBox =  structureStart.getBoundingBox();
        BoundingBox boundingBox;
        int MinX = sboundingBox.minX();
        int MinY = sboundingBox.minY();
        int MinZ = sboundingBox.minZ();
        int MaxX = sboundingBox.maxX();
        int MaxY = sboundingBox.maxY();
        int MaxZ = sboundingBox.maxZ();
        //if(debug){	log("MinX=" + MinX);	};
        //if(debug){	log("MinY=" + MinY);	};
        //if(debug){	log("MinZ=" + MinZ);	};
        //if(debug){	log("MaxX=" + MaxX);	};
        //if(debug){	log("MaxY=" + MaxY);	};
        //if(debug){	log("MaxZ=" + MaxZ);	};
        Vec3i vec = new Vec3i(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        return inside; //sboundingBox.isInside(vec);


    }

    @SuppressWarnings({ "unused" })
    private static  @Nullable Pair<BlockPos, Holder<Structure>> locateStructure(CraftWorld craftWorld, Location origin, ResourceKey<Structure> key, int i, boolean flag, boolean debug) {
        ServerLevel worldServer = craftWorld.getHandle();
        if(worldServer == null) {
            //if(debug){	log("worldServer == null");	};
        }
        //if(debug){	log("StructureType.END_CITY.getKey()=" + StructureType.END_CITY.getKey());	};
        //NamespacedKey nsk = NamespacedKey.fromString(key);
        //if(debug){	log("nsk=" + nsk);	};
        assert worldServer != null;
        Registry<Structure> registry = worldServer.registryAccess().registryOrThrow(Registries.STRUCTURE);
        //ResourceLocation resourceLocation = key.toString();
        //ResourceKey<Structure> resourceKey = ResourceKey.create( Registries.STRUCTURE, resourceLocation );
        Holder<Structure> resourceKeyHolder = registry.getHolder(key).get();
        Direct<Structure> holderSet = HolderSet.direct(resourceKeyHolder);
        BlockPos originPos = new BlockPos(origin.getBlockX(), origin.getBlockY(), origin.getBlockZ() );
        BlockPos location = worldServer.getChunkSource().getGenerator().findNearestMapStructure(worldServer, holderSet, originPos, i, flag).getFirst();
        return worldServer.getChunkSource().getGenerator().findNearestMapStructure(worldServer, holderSet, originPos, i, flag);
    }

}
