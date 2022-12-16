[![forthebadge](https://forthebadge.com/images/badges/works-on-my-machine.svg)](https://forthebadge.com) [![forthebadge](https://forthebadge.com/images/badges/compatibility-betamax.svg)](https://forthebadge.com) [![forthebadge](https://forthebadge.com/images/badges/made-with-crayons.svg)](https://forthebadge.com)

Reactor Simulator
=================

Simulates designs for the popular MineCraft mod [BigReactors](http://github.com/erogenousbeef/BigReactors). Based on code by [pwestling](http://github.com/pwestling).

## Running

You can start the server with

```
./gradlew run
```

## Running (docker)

You can pull the docker image from `sidoh/reactor_simulator:latest`, then:

```
docker run sidoh/reactor_simulator
```

## Usage

Runs on port 8081. This is currently hardcoded. Serves from localhost:8081/api

`POST` to `localhost:8081/api/simulate` with payload:
```json
{
    "xSize":7,
    "zSize":7,
    "height":7,
    "layout":"XXXXXXXXXXXXXXXXXXXXXXXXX",
    "isActivelyCooled":false,
    "controlRodInsertion":0
}
```

## TODO

- Add Xms/Xmx args to spinoff gradle process