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

import me.moros.bending.api.platform.block.Block;
import me.moros.bending.api.platform.entity.LivingEntity;
import me.moros.bending.api.platform.entity.player.Player;
import me.moros.bending.api.protection.AbstractProtection;
import me.moros.bending.api.registry.Registries;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class BendingGriefPrevention extends JavaPlugin {
  @Override
  public void onLoad() {
    Plugin plugin = Objects.requireNonNull(getServer().getPluginManager().getPlugin("GriefPrevention"));
    Registries.PROTECTIONS.register(new GriefPreventionProtection(plugin));
  }

  private static final class GriefPreventionProtection extends AbstractProtection {
    private final GriefPrevention griefPrevention;

    public GriefPreventionProtection(Plugin plugin) {
      super(plugin.getName());
      griefPrevention = (GriefPrevention) plugin;
    }

    @Override
    public boolean canBuild(LivingEntity entity, Block block) {
      if (entity instanceof Player player) {
        var loc = adapt(block);
        var bukkitPlayer = adapt(player);
        String reason = griefPrevention.allowBuild(bukkitPlayer, loc);
        Claim claim = griefPrevention.dataStore.getClaimAt(loc, true, null);
        return reason == null || claim == null || claim.siegeData != null;
      }
      return true;
    }

    private static Location adapt(Block block) {
      var w = Objects.requireNonNull(Bukkit.getWorld(block.world().key()));
      return new Location(w, block.blockX(), block.blockY(), block.blockZ());
    }

    private static org.bukkit.entity.Player adapt(Player player) {
      return Objects.requireNonNull(Bukkit.getPlayer(player.uuid()));
    }
  }
}
