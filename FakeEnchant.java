package net.gc.getdown;

import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.core.BlockPosition;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.IInventory;
import net.minecraft.world.entity.player.PlayerInventory;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.ContainerAccess;
import net.minecraft.world.inventory.ContainerEnchantTable;
import net.minecraft.world.inventory.ContainerProperty;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentManager;
import net.minecraft.world.item.enchantment.WeightedRandomEnchant;
import net.minecraft.world.level.block.BlockEnchantmentTable;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R3.inventory.CraftItemStack;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftNamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.entity.Player;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

import static net.gc.getdown.GetDown.originRadius;

//this class is alternative to ContainerEnchantTable
class FakeEnchant extends ContainerEnchantTable {

    private static BlockPosition[] position = {new BlockPosition(originRadius, 100, originRadius), new BlockPosition(originRadius, 110, originRadius), new BlockPosition(originRadius, 120, originRadius)};

    public FakeEnchant(int i, Player player, int level) {
        //fJ() is getInventory(), a is constructor'ish return thing, H is getWorld()
        super(i, ((CraftPlayer) player).getHandle().fJ(), ContainerAccess.a(((CraftPlayer) player).getHandle().H, position[level - 1]));
        this.checkReachable = false;
    }

    public IInventory getInventory() {
        FakeEnchant c = this;
        try {
            Field fieldN = c.getClass().getSuperclass().getDeclaredField("n");
            fieldN.setAccessible(true);
            return (IInventory) fieldN.get(c);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Bukkit.broadcastMessage("The FakeEnchant couldn't reflect the inventory from Parent class");
        }
        return null;
    }

    public ContainerProperty getContainerProperty() throws NoSuchFieldException, IllegalAccessException {
        FakeEnchant c = this;
        Field fieldL = c.getClass().getSuperclass().getDeclaredField("q");
        fieldL.setAccessible(true);
        return (ContainerProperty) fieldL.get(c);
    }

    public Method getMethod() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException {
        ContainerEnchantTable c = this;
        Method methodA = c.getClass().getSuperclass().getMethod("a", IInventory.class);
        methodA.setAccessible(true);
        return methodA;
    }


}
