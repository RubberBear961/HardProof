package net.bieluuu.hardproof.item;

import net.bieluuu.hardproof.HardProof;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item PARMEZAN = registerItem("parmezan",new Item(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(HardProof.MOD_ID,"parmezan")))));

    private static Item registerItem(String name, Item item){
        return Registry.register(Registries.ITEM, Identifier.of(HardProof.MOD_ID, name), item);
    }


    public static void registerModItems() {
        HardProof.LOGGER.info("Initializing items from: " + HardProof.MOD_ID);
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            entries.add(PARMEZAN);
        });
    }

}
