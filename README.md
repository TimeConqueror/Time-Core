# TimeCore

<br>
&#x1F534; <span>Requires MixinBootstrap as a dependency</span>

## Provided features (1.15.2+):
* JSON-Based Entity Animations - Easy-to-use system that allows you to play complex entity animations, which are parsed from JSON. More info here: (click)
* JSON Models for Entities - Allows you to use Bedrock JSON Entity Models in Minecraft Java Edition! You can use Blockbench for creating them.
* On-Fly Blockstate/Model Generator - Allows you to generate simple item/block models and blockstates, so you don't need place them in resources now!
* Structure Revealer - Can be used for debug purposes, shows bounding box of the every subscribed structure piece.
* Comfortable system for Block/Item/TileEntity/Packet/Config Registering - Provides a simple way of creating and registering all specified stuff.
* Improved Config-Building System - More comfortable way of creating configs.
* Safe Network System - No more exploits of sending packets to the wrong side.
* Simple-to-use Reflection system - automatically unlocks fields, methods and constructors, when you're accessing them via Reflection.
* Client-side Commands - provides the way of creating commands, that exist only on client side.

## How to add TimeCore as a gradle dependency:
1. Add this maven repo in the `repositories` closure:

```groovy
    maven {
        name = "TimeConqueror's Maven"
        url = "https://raw.githubusercontent.com/TimeConqueror/maven/master"
    }
    maven {
        name = 'sponge'
        url = 'https://repo.spongepowered.org/maven'
    }
```

2. Add this dependency in the `dependencies` closure:
```groovy
implementation group: 'ru.timeconqueror', name: 'TimeCore', version: '1.15.2-2.0.0.+', changing: true
```

3. Add this string as a `JVM Argument`:
`-Dmixin.env.disableRefMap=true`
