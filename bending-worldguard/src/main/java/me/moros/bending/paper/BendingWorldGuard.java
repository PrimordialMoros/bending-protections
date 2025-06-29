/*
 * Copyright 2020-2025 Moros
 *
 * This file is part of Bending.
 *
 * Bending is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bending is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with Bending. If not, see <https://www.gnu.org/licenses/>.
 */

package me.moros.bending.paper;

import java.util.Objects;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.Association;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import me.moros.bending.api.platform.block.Block;
import me.moros.bending.api.platform.entity.LivingEntity;
import me.moros.bending.api.platform.entity.player.Player;
import me.moros.bending.api.protection.AbstractProtection;
import me.moros.bending.api.registry.Registries;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class BendingWorldGuard extends JavaPlugin {
  private static final String FLAG_NAME = "bending";
  private static final boolean DEFAULT_STATE = false;

  @Override
  public void onLoad() {
    FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
    try {
      StateFlag flag = new StateFlag(FLAG_NAME, DEFAULT_STATE);
      registry.register(flag);
      registerProtection(flag);
    } catch (FlagConflictException e) {
      Flag<?> existing = registry.get(FLAG_NAME);
      if (existing instanceof StateFlag flag) {
        registerProtection(flag);
      } else {
        throw new RuntimeException(e);
      }
    }
  }

  private void registerProtection(StateFlag flag) {
    Registries.PROTECTIONS.register(new WorldGuardProtection(flag));
  }

  private static final class WorldGuardProtection extends AbstractProtection {
    private final WorldGuard worldGuard;
    private final StateFlag bendingFlag;

    private WorldGuardProtection(StateFlag bendingFlag) {
      super("worldguard");
      this.worldGuard = WorldGuard.getInstance();
      this.bendingFlag = bendingFlag;
    }

    @Override
    public boolean canBuild(LivingEntity entity, Block block) {
      RegionQuery query = worldGuard.getPlatform().getRegionContainer().createQuery();
      Location location = adapt(block);
      if (entity instanceof Player player) {
        LocalPlayer localPlayer = adapt(player);
        if (worldGuard.getPlatform().getSessionManager().hasBypass(localPlayer, localPlayer.getWorld())) {
          return true;
        }
        return query.testState(location, localPlayer, bendingFlag);
      }
      // Query WorldGuard to see if a non-member (entity) can build in a region.
      return query.testState(location, list -> Association.NON_MEMBER, bendingFlag);
    }

    private static Location adapt(Block block) {
      var w = Objects.requireNonNull(Bukkit.getWorld(block.world().key()));
      return BukkitAdapter.adapt(new org.bukkit.Location(w, block.blockX(), block.blockY(), block.blockZ()));
    }

    private static LocalPlayer adapt(Player player) {
      var p = Objects.requireNonNull(Bukkit.getPlayer(player.uuid()));
      return WorldGuardPlugin.inst().wrapPlayer(p);
    }
  }
}
