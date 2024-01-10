package org.sidoh.reactor_simulator.simulator;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class ReactorParser {

  public static final char CONTROL_ROD = 'X';
  private FakeReactorWorld world;

  public ReactorParser(int height, short insertion, String[] lines) {
    String first = lines[0].replaceAll(" ", "");
    this.world = new FakeReactorWorld(lines.length + 1, height, first.length() + 1, insertion);
    for (int i = 0; i < lines.length; i++) {
      String line = lines[i].replaceAll(" ", "");
      for (int j = 0; j < line.length(); j++) {
        char c = line.charAt(j);
        parse(i, j, c, world);
      }

    }
  }

  public static void parse(int i, int j, char c, FakeReactorWorld world) {
    if (c == CONTROL_ROD) {
      world.makeControlRod(i + 1, j + 1);
    } else if (c == 'O') {
      // Air column
    } else {
      String coolant = getCoolant(c);
      world.makeCoolantColumn(i + 1, j + 1, coolant);
    }
  }

  public FakeReactorWorld getWorld() {
    return world;
  }

  private static String getCoolant(char c) {
    return mappings.get(c);
  }

  public static BiMap<Character, String> mappings = HashBiMap.create();

  static {
    mappings.put('X', "entity:controlRod");
    mappings.put('O', "fluid:air");
    mappings.put('A', "forge:storage_block/apatite");
    mappings.put('Y', "forge:storage_block/cinnabar");
    mappings.put('I', "forge:storage_blocks/iron");
    mappings.put('M', "forge:storage_blocks/manasteel");
    mappings.put('Q', "forge:storage_blocks/elementium");
    mappings.put('N', "forge:storage_blocks/nickel");
    mappings.put('L', "forge:storage_blocks/gold");
    mappings.put('D', "forge:storage_blocks/diamond");
    mappings.put('J', "forge:storage_blocks/netherite");
    mappings.put('T', "forge:storage_blocks/terrasteel");
    mappings.put('K', "forge:storage_blocks/emerald");
    mappings.put('0', "forge:glass/colorless");
    mappings.put('1', "forge:storage_blocks/copper");
    mappings.put('B', "forge:storage_blocks/brass");
    mappings.put('2', "forge:storage_blocks/osmium");
    mappings.put('3', "forge:storage_blocks/refined_obsidian");
    mappings.put('4', "forge:storage_blocks/refined_glowstone");
    mappings.put('5', "forge:storage_blocks/bronze");
    mappings.put('Z', "forge:storage_blocks/zinc");
    mappings.put('U', "forge:storage_blocks/aluminum");
    mappings.put('S', "forge:storage_blocks/steel");
    mappings.put('V', "forge:storage_blocks/invar");
    mappings.put('6', "forge:storage_blocks/tin");
    mappings.put('7', "forge:storage_blocks/silver");
    mappings.put('8', "forge:storage_blocks/signalum");
    mappings.put('9', "forge:storage_blocks/lumium");
    mappings.put('!', "forge:storage_blocks/lead");
    mappings.put('@', "forge:storage_blocks/electrum");
    mappings.put('P', "forge:storage_blocks/platinum");
    mappings.put('#', "forge:storage_blocks/enderium");
    mappings.put('G', "forge:storage_blocks/graphite");
    mappings.put('$', "minecraft:ice");
    mappings.put('%', "bigreactors:dry_ice");
    mappings.put('C', "bigreactors:cryomisi");
    mappings.put('^', "bigreactors:tangerium");
    mappings.put('&', "bigreactors:redfrigium");
    mappings.put('W', "minecraft:water");
    mappings.put('*', "astralsorcery:liquid_starlight");
    mappings.put('-', "bloodmagic:life_essence_fluid");
    mappings.put('F', "mekanism:hydrofluoric_acid");
    mappings.put('_', "mekanism:sodium");
    mappings.put('H', "mekanism:hydrogen_chloride");
    mappings.put('+', "mekanism:ethene");
    mappings.put('E', "thermal:ender");
    mappings.put('R', "thermal:redstone");
  }
}
