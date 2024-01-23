package org.sidoh.reactor_simulator.simulator;

import java.util.Arrays;
import java.util.List;

import erogenousbeef.bigreactors.api.IHeatEntity;
import erogenousbeef.bigreactors.api.registry.ReactorConversions;
import erogenousbeef.bigreactors.api.registry.ReactorInterior;
import erogenousbeef.bigreactors.api.registry.TurbineCoil;
import erogenousbeef.bigreactors.common.BigReactors;
import erogenousbeef.bigreactors.common.data.StandardReactants;
import erogenousbeef.bigreactors.common.multiblock.helpers.RadiationHelper;
import net.minecraft.item.EnumRarity;
import net.minecraftforge.fluids.Fluid;
import org.sidoh.reactor_simulator.simulator.monitors.CoolantTemperatureMonitor;
import org.sidoh.reactor_simulator.simulator.monitors.FuelConsumptionMonitor;
import org.sidoh.reactor_simulator.simulator.monitors.FuelFertilityMonitor;
import org.sidoh.reactor_simulator.simulator.monitors.FuelHeatMonitor;
import org.sidoh.reactor_simulator.simulator.monitors.MonitorUtils;
import org.sidoh.reactor_simulator.simulator.monitors.OutputMonitor;
import org.sidoh.reactor_simulator.simulator.monitors.ReactorHeatMonitor;

import static org.sidoh.reactor_simulator.simulator.monitors.TimeSeriesSimulationMonitor.Factory.factoryOf;

public class BigReactorSimulator {
  private boolean activelyCooled;

  // Number of negative deltas that should be seen before a simulation is considered stable.
  private static final double STABILITY_THRESHOLD = 200;

  private static final List<SimulationMonitor.Factory> MONITORS = Arrays.<SimulationMonitor.Factory>asList(
      factoryOf(CoolantTemperatureMonitor.class),
      factoryOf(FuelConsumptionMonitor.class),
      factoryOf(FuelFertilityMonitor.class),
      factoryOf(FuelHeatMonitor.class),
      factoryOf(OutputMonitor.class),
      factoryOf(ReactorHeatMonitor.class)
  );

  private int ticks;

  public BigReactorSimulator(boolean activelyCooled, int ticks) {
    this.activelyCooled = activelyCooled;
    this.ticks = ticks;
  }

  public static void init() {
    // Data from Extreme Reactors 2 for 1.20.1
    // https://github.com/ZeroNoRyouki/ExtremeReactors2/blob/1.20/src/main/java/it/zerono/mods/extremereactors/gamecontent/ReactorGameData.java#L96
    // was
    // https://github.com/erogenousbeef-zz/BigReactors/blob/master/src/main/java/erogenousbeef/bigreactors/common/BigReactors.java#L824
    
    ReactorInterior.registerBlock("forge:storage_block/apatite", 0.48f, 0.73f, 1.30f, IHeatEntity.conductivityStone);
    ReactorInterior.registerBlock("forge:storage_block/cinnabar", 0.48f, 0.75f, 1.32f, IHeatEntity.conductivityStone);
    ReactorInterior.registerBlock("forge:storage_blocks/iron", 0.50f, 0.75f, 1.40f, IHeatEntity.conductivityIron);
    ReactorInterior.registerBlock("forge:storage_blocks/manasteel", 0.60f, 0.75f, 1.50f, IHeatEntity.conductivityIron);
    ReactorInterior.registerBlock("forge:storage_blocks/elementium", 0.61f, 0.77f, 1.52f, IHeatEntity.conductivityEmerald);
    ReactorInterior.registerBlock("forge:storage_blocks/nickel", 0.51f, 0.77f, 1.40f, IHeatEntity.conductivityIron);
    ReactorInterior.registerBlock("forge:storage_blocks/gold", 0.52f, 0.80f, 1.45f, IHeatEntity.conductivityGold);
    ReactorInterior.registerBlock("forge:storage_blocks/diamond", 0.55f, 0.85f, 1.50f, IHeatEntity.conductivityDiamond);
    ReactorInterior.registerBlock("forge:storage_blocks/netherite", 0.55f, 0.95f, 1.65f, IHeatEntity.conductivityDiamond);
    ReactorInterior.registerBlock("forge:storage_blocks/terrasteel", 0.57f, 0.87f, 1.52f, IHeatEntity.conductivityDiamond);
    ReactorInterior.registerBlock("forge:storage_blocks/emerald", 0.55f, 0.85f, 1.50f, IHeatEntity.conductivityEmerald);
    ReactorInterior.registerBlock("forge:glass/colorless", 0.20f, 0.25f, 1.10f, IHeatEntity.conductivityGlass);
    ReactorInterior.registerBlock("forge:storage_blocks/copper", 0.50f, 0.75f, 1.40f, IHeatEntity.conductivityCopper);
    ReactorInterior.registerBlock("forge:storage_blocks/brass", 0.52f, 0.78f, 1.42f, IHeatEntity.conductivityCopper);
    ReactorInterior.registerBlock("forge:storage_blocks/osmium", 0.51f, 0.77f, 1.41f, IHeatEntity.conductivityCopper);
    ReactorInterior.registerBlock("forge:storage_blocks/refined_obsidian", 0.53f, 0.79f, 1.42f, IHeatEntity.conductivityCopper);
    ReactorInterior.registerBlock("forge:storage_blocks/refined_glowstone", 0.54f, 0.79f, 1.44f, IHeatEntity.conductivityEmerald);
    ReactorInterior.registerBlock("forge:storage_blocks/bronze", 0.51f, 0.77f, 1.41f, IHeatEntity.conductivityCopper);
    ReactorInterior.registerBlock("forge:storage_blocks/zinc", 0.51f, 0.77f, 1.41f, IHeatEntity.conductivityCopper);
    ReactorInterior.registerBlock("forge:storage_blocks/aluminum",0.50f, 0.78f, 1.42f, IHeatEntity.conductivityIron);
    ReactorInterior.registerBlock("forge:storage_blocks/steel", 0.50f, 0.78f, 1.42f, IHeatEntity.conductivityIron);
    ReactorInterior.registerBlock("forge:storage_blocks/invar", 0.50f, 0.79f, 1.43f, IHeatEntity.conductivityIron);
    ReactorInterior.registerBlock("forge:storage_blocks/tin", 0.50f, 0.73f, 1.38f, IHeatEntity.conductivitySilver);
    ReactorInterior.registerBlock("forge:storage_blocks/silver", 0.51f, 0.79f, 1.43f, IHeatEntity.conductivitySilver);
    ReactorInterior.registerBlock("forge:storage_blocks/signalum", 0.51f, 0.75f, 1.42f, IHeatEntity.conductivityCopper);
    ReactorInterior.registerBlock("forge:storage_blocks/lumium", 0.51f, 0.79f, 1.45f, IHeatEntity.conductivitySilver);
    ReactorInterior.registerBlock("forge:storage_blocks/lead", 0.75f, 0.75f, 1.75f, IHeatEntity.conductivitySilver);
    ReactorInterior.registerBlock("forge:storage_blocks/electrum", 0.53f, 0.82f, 1.47f, 2.2f); // Between gold and emerald
    ReactorInterior.registerBlock("forge:storage_blocks/platinum", 0.57f, 0.86f, 1.58f, IHeatEntity.conductivityEmerald);
    ReactorInterior.registerBlock("forge:storage_blocks/enderium", 0.60f, 0.88f, 1.60f, IHeatEntity.conductivityDiamond);
    //blockTitanium
    //blockDraconium
    //blockDraconiumAwakened
    ReactorInterior.registerBlock("forge:storage_blocks/graphite", 0.10f, 0.50f, 2.00f, IHeatEntity.conductivityGold);
    ReactorInterior.registerBlock("minecraft:ice", 0.33f, 0.33f, 1.15f, IHeatEntity.conductivityWater);
    ReactorInterior.registerBlock("bigreactors:dry_ice", 0.42f, 0.52f, 1.32f, IHeatEntity.conductivityWater);

    // fluids

    ReactorInterior.registerFluid("bigreactors:cryomisi", 0.75f, 0.55f, 1.60f, IHeatEntity.conductivityEmerald);

    ReactorInterior.registerFluid("bigreactors:tangerium", 0.90f, 0.75f, 2.00f, IHeatEntity.conductivityGold);

    ReactorInterior.registerFluid("bigreactors:redfrigium", 0.66f, 0.95f, 6.00f, IHeatEntity.conductivityDiamond);

    ReactorInterior.registerFluid("minecraft:water", RadiationHelper.waterData.absorption, RadiationHelper.waterData.heatEfficiency, RadiationHelper.waterData.moderation, IHeatEntity.conductivityWater);

    // Astral Sorcery
    ReactorInterior.registerFluid("astralsorcery:liquid_starlight", 0.92f, 0.80f, 2.00f, IHeatEntity.conductivityDiamond);

    // Blood Magic
    ReactorInterior.registerFluid("bloodmagic:life_essence_fluid", 0.80f, 0.55f, 1.75f, IHeatEntity.conductivityEmerald);

    // Mekanism
    ReactorInterior.registerFluid("mekanism:hydrofluoric_acid", 0.68f, 0.45f, 1.40f, IHeatEntity.conductivityEmerald);
    ReactorInterior.registerFluid("mekanism:sodium", 0.28f, 0.60f, 1.70f, IHeatEntity.conductivityCopper);
    ReactorInterior.registerFluid("mekanism:hydrogen_chloride", 0.38f, 0.65f, 1.70f, IHeatEntity.conductivityCopper);
    ReactorInterior.registerFluid("mekanism:ethene", 0.45f, 0.65f, 1.90f, IHeatEntity.conductivitySilver); // Etilene

    // Thermal
    ReactorInterior.registerFluid("thermal:ender", 0.92f, 0.76f, 2.02f, IHeatEntity.conductivityGold); // Resonant Ender
    ReactorInterior.registerFluid("thermal:redstone", 0.77f, 0.56f, 1.61f, IHeatEntity.conductivityEmerald); // Destabilized Redstone
    StandardReactants.register();

    // Register reactant => reactant conversions for making cyanite
    ReactorConversions.register(StandardReactants.yellorium, StandardReactants.cyanite);
    ReactorConversions.register(StandardReactants.blutonium, StandardReactants.cyanite);

    // Turbine stuff, NYI
    TurbineCoil.registerBlock("blockIron", 1f, 1f, 1f);
    TurbineCoil.registerBlock("blockGold", 2f, 1f, 1.75f);

    TurbineCoil.registerBlock("blockCopper", 1.2f, 1f, 1.2f);  // TE, lots of mods
    TurbineCoil.registerBlock("blockOsmium", 1.2f, 1f, 1.2f);  // Mekanism
    TurbineCoil.registerBlock("blockZinc", 1.35f, 1f, 1.3f);
    TurbineCoil.registerBlock("blockLead", 1.35f, 1.01f, 1.3f);// TE, Mekanism, some others
    TurbineCoil.registerBlock("blockBrass", 1.4f, 1f, 1.2f);  // Metallurgy
    TurbineCoil.registerBlock("blockBronze", 1.4f, 1f, 1.2f);  // Mekanism, many others
    TurbineCoil.registerBlock("blockAluminum", 1.5f, 1f, 1.3f);  // TiCo, couple others
    TurbineCoil.registerBlock("blockSteel", 1.5f, 1f, 1.3f);  // Metallurgy, Mek, etc.
    TurbineCoil.registerBlock("blockInvar", 1.5f, 1f, 1.4f);  // TE
    TurbineCoil.registerBlock("blockSilver", 1.7f, 1f, 1.5f);  // TE, lots of mods
    TurbineCoil.registerBlock("blockElectrum", 2.5f, 1f, 2.0f);  // TE, lots of mods
    TurbineCoil.registerBlock("blockElectrumFlux", 2.5f, 1.01f, 2.2f);  // Redstone Arsenal, note small energy bonus (7% at 1000RF/t output)
    TurbineCoil.registerBlock("blockPlatinum", 3.0f, 1f, 2.5f);  // TE, lots of mods
    TurbineCoil.registerBlock("blockShiny", 3.0f, 1f, 2.5f);  // TE
    TurbineCoil.registerBlock("blockTitanium", 3.1f, 1f, 2.7f);  // Mariculture
    TurbineCoil.registerBlock("blockEnderium", 3.0f, 1.02f, 3.0f);  // TE, note tiny energy bonus!	(14% at 1000RF/t output)

    TurbineCoil.registerBlock("blockLudicrite", 3.5f, 1.02f, 3.5f);

    // Metallurgy fantasy metals
    TurbineCoil.registerBlock("blockMithril", 2.2f, 1f, 1.5f);
    TurbineCoil.registerBlock("blockOrichalcum", 2.3f, 1f, 1.7f);
    TurbineCoil.registerBlock("blockQuicksilver", 2.6f, 1f, 1.8f);
    TurbineCoil.registerBlock("blockHaderoth", 3.0f, 1f, 2.0f);
    TurbineCoil.registerBlock("blockCelenegil", 3.3f, 1f, 2.25f);
    TurbineCoil.registerBlock("blockTartarite", 3.5f, 1f, 2.5f);
    TurbineCoil.registerBlock("blockManyullyn", 3.5f, 1f, 2.5f);

    Fluid fluidSteam = new Fluid("steam");
    fluidSteam.setUnlocalizedName("steam");
    fluidSteam.setTemperature(1000); // For consistency with TE
    fluidSteam.setGaseous(true);
    fluidSteam.setLuminosity(0);
    fluidSteam.setRarity(EnumRarity.common);
    fluidSteam.setDensity(6);

    BigReactors.fluidSteam = fluidSteam;

    //    StandardReactants.yelloriumMapping = Reactants.registerSolid("ingotYellorium", StandardReactants.yellorium);
    //    StandardReactants.cyaniteMapping = Reactants.registerSolid("ingotCyanite", StandardReactants.cyanite);
    //
    //    ItemStack blockYellorium = blockMetal.getItemStackForMaterial("Yellorium");
    //    Reactants.registerSolid(blockYellorium, StandardReactants.yellorium, Reactants.standardSolidReactantAmount * 9);
    //
    //    ItemStack blockBlutonium = blockMetal.getItemStackForMaterial("Blutonium");
    //    Reactants.registerSolid(blockBlutonium, StandardReactants.blutonium, Reactants.standardSolidReactantAmount * 9);
  }

  public ReactorResult simulate(IFakeReactorWorld world) {
    final MultiblockReactorSimulator simulator = new MultiblockReactorSimulator(world, "yellorium", activelyCooled);
    final List<SimulationMonitor> monitors = MonitorUtils.instantiate(MONITORS);

    double lastValue = 0;
    int numNegativeDeltas = 0;

    System.out.println("Energy generated, Fuel Consumed, Fuel Richness, Fuel Fertility, Fuel Heat, Reactor Heat");
    for (int i = 0; i < this.ticks; i++) {
      simulator.updateServer();
      System.out.println(String.join(", ",Float.toString(simulator.getEnergyGeneratedLastTick()), Float.toString(simulator.getFuelConsumedLastTick()), Float.toString(simulator.getFuelRichness()), Float.toString(simulator.getFuelFertility()), Float.toString(simulator.getFuelHeat()), Float.toString(simulator.getReactorHeat())));

      final double energyValue = simulator.getEnergyGeneratedLastTick();
      final double energyDelta  = (energyValue - lastValue);

      if (energyDelta < 0) {
        numNegativeDeltas++;
      }

      if (numNegativeDeltas >= STABILITY_THRESHOLD) {
        break;
      }

      for (SimulationMonitor monitor : monitors) {
        monitor.offer(simulator);
      }

      lastValue = energyValue;
    }

    final ReactorResult result = new ReactorResult();
    for (SimulationMonitor monitor : monitors) {
      monitor.report(result);
    }
    return result;
  }

  /*public static void main(String[] args) {
    BigReactorSimulator.init();
    String reactor = "OOOOOOOOOORROOOOOOOOOOOOOOOOOOOXXXOOOOOOOOGGXCXGGOOOOOOGGXXXGGOOOOOXXXCXCXXXOOOOXCXXXXXCXOOOOXXXCXCXXXOOOOOGGXXXGGOOOOOOGGXCXGGOOOOOOOOXXXOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO";
    final ReactorDefinition definition = new ReactorDefinition(15, 15, 7, reactor, false, (short)0);

    System.out.println(new LinearControlRodOptimizer(
        ResultMetrics.efficiency(),
        new BigReactorSimulator(false, 10000)
    ).optimizeInsertion(definition));
  }*/
}
