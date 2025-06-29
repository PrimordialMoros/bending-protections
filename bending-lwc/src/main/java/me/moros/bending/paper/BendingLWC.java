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

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.model.Protection;
import me.moros.bending.api.platform.block.Block;
import me.moros.bending.api.platform.entity.LivingEntity;
import me.moros.bending.api.platform.entity.player.Player;
import me.moros.bending.api.protection.AbstractProtection;
import me.moros.bending.api.registry.Registries;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class BendingLWC extends JavaPlugin {
  @Override
  public void onLoad() {
    Plugin plugin = Objects.requireNonNull(getServer().getPluginManager().getPlugin("LWC"));
    Registries.PROTECTIONS.register(new LWCProtection(plugin));
  }

  private static final class LWCProtection extends AbstractProtection {
    private final LWC lwc;

    private LWCProtection(Plugin plugin) {
      super(plugin.getName());
      lwc = ((LWCPlugin) plugin).getLWC();
    }

    @Override
    public boolean canBuild(LivingEntity entity, Block block) {
      if (entity instanceof Player player) {
        Protection protection = lwc.getProtectionCache().getProtection(adapt(block));
        return protection == null || lwc.canAccessProtection(adapt(player), protection);
      }
      return true;
    }

    private static org.bukkit.block.Block adapt(Block block) {
      var w = Objects.requireNonNull(Bukkit.getWorld(block.world().key()));
      return w.getBlockAt(block.blockX(), block.blockY(), block.blockZ());
    }

    private static org.bukkit.entity.Player adapt(Player player) {
      return Objects.requireNonNull(Bukkit.getPlayer(player.uuid()));
    }
  }
}
