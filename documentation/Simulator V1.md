```mermaid
classDiagram

namespace BigReactorSimulator {
	class simulate {
		IFakeReactorWorld world
		
	}
}

namespace MultiblockReactorSimulator {
	class updateServer
}

simulate --> updateServer

```