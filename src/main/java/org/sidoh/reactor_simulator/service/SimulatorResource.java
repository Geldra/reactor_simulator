package org.sidoh.reactor_simulator.service;

import org.sidoh.reactor_simulator.simulator.BigReactorSimulator;
import org.sidoh.reactor_simulator.simulator.FakeReactorWorld;
import org.sidoh.reactor_simulator.simulator.ReactorDefinition;
import org.sidoh.reactor_simulator.simulator.ReactorResult;
import restx.annotations.POST;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.security.PermitAll;

@Component
@RestxResource
public class SimulatorResource {
  @PermitAll
  @POST("/simulate") //now able to accommodate larger requests
  public ReactorResult simulate(ReactorDefinition definition) {
    long startTime = System.currentTimeMillis();
    System.out.println("1 New request for " + definition + " (+" + (System.currentTimeMillis() - startTime) + "ms)");
    startTime = System.currentTimeMillis();
    SimulatorServer.validateReactorDefinition(definition);
    System.out.println("2 Definition validated" + " (+" + (System.currentTimeMillis() - startTime) + "ms)");
    startTime = System.currentTimeMillis();

    BigReactorSimulator simulator = new BigReactorSimulator(
        definition.isActivelyCooled(),
        SimulatorServer.MAX_NUMBER_OF_TICKS
    );

    System.out.println("3 New BR sim spun up" + " (+" + (System.currentTimeMillis() - startTime) + "ms)");
    startTime = System.currentTimeMillis();

    FakeReactorWorld fakeReactorWorld = FakeReactorWorld.makeReactor(
        definition.getLayout(),
        definition.getxSize(),
        definition.getzSize(),
        definition.getHeight(),
        definition.getControlRodInsertion()
    );

    System.out.println("4 Fake reactor defined" + " (+" + (System.currentTimeMillis() - startTime) + "ms)");
    startTime = System.currentTimeMillis();

    ReactorResult rawResult = simulator.simulate(fakeReactorWorld);

    System.out.println("5 Simulation complete" + " (+" + (System.currentTimeMillis() - startTime) + "ms)");
    startTime = System.currentTimeMillis();

    return new ReactorResult()
        .setCoolantTemperature(rawResult.coolantTemperature)
        .setFuelConsumption(rawResult.fuelConsumption)
        // for display purposes
        .setFuelFertility(rawResult.fuelFertility * 100)
        .setFuelHeat(rawResult.fuelHeat)
        .setOutput(rawResult.output)
        .setReactorDefinition(definition)
        .setReactorHeat(rawResult.reactorHeat);
  }
}
