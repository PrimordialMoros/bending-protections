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

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownyPermission;
import com.palmergames.bukkit.towny.utils.PlayerCacheUtil;
import me.moros.bending.api.platform.block.Block;
import me.moros.bending.api.platform.entity.LivingEntity;
import me.moros.bending.api.platform.entity.player.Player;
import me.moros.bending.api.protection.AbstractProtection;
import me.moros.bending.api.registry.Registries;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

public final class BendingTowny extends JavaPlugin {
  @Override
  public void onLoad() {
    Registries.PROTECTIONS.register(new TownyProtection());
  }

  private static final class TownyProtection extends AbstractProtection {
    private final TownyAPI api;

    private TownyProtection() {
      super("towny");
      api = TownyAPI.getInstance();
    }

    @Override
    public boolean canBuild(LivingEntity entity, Block block) {
      var loc = adapt(block);
      if (entity instanceof Player player) {
        var bukkitPlayer = adapt(player);
        return PlayerCacheUtil.getCachePermission(bukkitPlayer, loc, Material.DIRT, TownyPermission.ActionType.BUILD);
      }
      TownBlock townBlock = api.getTownBlock(loc);
      return townBlock == null || !townBlock.hasTown();
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
