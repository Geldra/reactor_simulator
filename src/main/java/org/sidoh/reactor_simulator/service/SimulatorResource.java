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
    System.out.println("New request for " + definition);
    SimulatorServer.validateReactorDefinition(definition);

    BigReactorSimulator simulator = new BigReactorSimulator(
        definition.isActivelyCooled(),
        SimulatorServer.MAX_NUMBER_OF_TICKS
    );
    FakeReactorWorld fakeReactorWorld = FakeReactorWorld.makeReactor(
        definition.getLayout(),
        definition.getxSize(),
        definition.getzSize(),
        definition.getHeight(),
        definition.getControlRodInsertion()
    );
    ReactorResult rawResult = simulator.simulate(fakeReactorWorld);

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
