# Tunnel

![Tunnel CI](https://github.com/RotBolt/Tunnel/workflows/Tunnel%20CI/badge.svg)
[![codecov](https://codecov.io/gh/RotBolt/Tunnel/branch/master/graph/badge.svg?token=0W22SELVXO)](https://codecov.io/gh/RotBolt/Tunnel)

Tunnel is Pub-Sub library inspired from [NYBus](https://github.com/MindorksOpenSource/NYBus)
It made with Kotlin Coroutines, Channes and Flow

## Architecture 

![Tunnel Architecture](https://user-images.githubusercontent.com/24780524/91075553-e06eed80-e65b-11ea-8df3-8694db040cb3.png)

### Information

- **Tunnel-Compiler** is annotation procesor that processes `Subsribe` annotation in Client Code and based on that creates Target Map (Message Delivery Map).
Target Map is generated and perisisted as `TunnelMap` class.

- `TunnelMap` is used by **Tunnel** module to post the message object to correct `Subscribed` target


## Usage

- Register the class 
```
        Tunnel.get().register(RegistryClass(this))

```

- Create method to receive the posted messages
```
    @Subscribe(dispatcherType = DispatcherType.IO, channelIds = ["CHANNEL_1", "CHANNEL_2"])
    fun onEvent(name: Bundle) {
     
    }
```

- Post the message object 
```
        Tunnel.get().post(TunnelMessage(Bundle().apply {
                putString("PUI", "Hola")
            }), "CHANNEL_1")

```

- You can create separate channels as per need and can subscribe to multiple channels
- Supported Dispatchers  (Uses Coroutine Dispatcher)
  - IO
  - Main 
  - Computation 
  - Post
  - SingleThreadExecutor
  
 ## Ongoing
 - [X] Unit Tests
  
 ## TODO
 - [ ] Error checks for invalid method
 - [ ] Support for logging
