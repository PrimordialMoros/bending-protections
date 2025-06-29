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
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.popcraft.bolt.BoltAPI;
import org.popcraft.bolt.util.Permission;

public final class BendingBolt extends JavaPlugin {
  @Override
  public void onLoad() {
    Plugin plugin = Objects.requireNonNull(getServer().getPluginManager().getPlugin("Bolt"));
    Registries.PROTECTIONS.register(new BoltProtection(plugin));
  }

  private static final class BoltProtection extends AbstractProtection {
    private final BoltAPI bolt;

    private BoltProtection(Plugin plugin) {
      super(plugin.getName());
      bolt = plugin.getServer().getServicesManager().load(BoltAPI.class);
    }

    @Override
    public boolean canBuild(LivingEntity entity, Block block) {
      if (entity instanceof Player player) {
        var b = adapt(block);
        return bolt.canAccess(bolt.findProtection(b), player.uuid(), Permission.DESTROY);
      }
      return true;
    }

    private static org.bukkit.block.Block adapt(Block block) {
      var w = Objects.requireNonNull(Bukkit.getWorld(block.world().key()));
      return w.getBlockAt(block.blockX(), block.blockY(), block.blockZ());
    }
  }
}
