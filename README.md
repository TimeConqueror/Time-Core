# TimeCore

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
1. Modify your buildscript section by adding extra classpath to the buildscript.dependencies closure:

```groovy
buildscript {
   ...
   dependencies {
      ...
      classpath 'gradle.plugin.com.github.jengelman.gradle.plugins:shadow:7.0.0'
      classpath 'org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10'
   }
}
```

2. Add a couple of plugins right after buildscript closure:

```groovy
apply plugin: 'kotlin'
apply plugin: 'com.github.johnrengelman.shadow'
apply from: 'https://raw.githubusercontent.com/TimeConqueror/Time-Core/1.18/gradle/scripts/timecore.gradle'
```

3. Create file "timecore.properties" and place there a property, which defines the TimeCore version to be used in
   project.

```properties
timecore.version=<VERSION_PLACEHOLDER>
```

Example:

```properties
timecore.version=1.18.1-3.5.0.0
```

## Contribution

As per the Github Terms of Service, you grant us the right to use your contribution under the same license as this
project.

In addition, we request that you give us the right to change the license in the future.

TimeCore uses a specialized mapping set which adds the readable parameter names to the functions.
See [Mappificator Project](https://github.com/alcatrazEscapee/Mappificator) in order to install that. You need to
generate the mappings, using the command below, while you are in the root folder of that project.

```
py src/mappificator.py -p -v 1 --mc-version 1.18.1 --providers yarn parchment --yarn-version 12 --parchment-version 2021.12.19-1.18.1
```