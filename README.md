# TimeCore

## Provided features (1.16.5+):

* JSON-Based Bedrock Entity Animations - Easy-to-use system that allows you to play complex entity animations, which are
  parsed from JSON. More info here:(click)
* JSON Bedrock Entity Models - Allows you to use Bedrock JSON Entity Models in Minecraft Java Edition! You can use
  Blockbench for creating them.
* On-Fly Blockstate/Model Generator - Allows you to generate simple item/block models and blockstates, so you don't need
  place them in resources now!
* Structure Revealer - Can be used for debug purposes, shows bounding box of the every subscribed structure piece.
* Comfortable system for Block/Item/TileEntity/Packet/Config Registering - Provides a simple way of creating and
  registering all specified stuff.
* Improved Config-Building System - More comfortable way of creating configs.
* Safe Network System - No more exploits of sending packets to the wrong side.
* Simple-to-use Reflection system - automatically unlocks fields, methods and constructors, when you're accessing them
  via Reflection.
* Client-side Commands - provides the way of creating commands, that exist only on client side.

## How to add TimeCore as a gradle dependency:

Add following to your `build.gradle` script.

```groovy
plugins {
    // Adds the Kotlin Gradle plugin
    id 'org.jetbrains.kotlin.jvm' version '1.8.22'
    // OPTIONAL Kotlin Serialization plugin
    id 'org.jetbrains.kotlin.plugin.serialization' version '1.8.22'
}

repositories {
    maven { url = "https://repo.repsy.io/mvn/timeconqueror/mc/" }
    maven { url = 'https://thedarkcolour.github.io/KotlinForForge/' }
}

dependencies {
    // Adds KFF as dependency and Kotlin libs
    implementation 'thedarkcolour:kotlinforforge:3.12.0'
    implementation fg.deobf('ru.timeconqueror:TimeCore:1.19.2-3.6.0.0')
}
```

## Contribution

As per the Github Terms of Service, you grant us the right to use your contribution under the same license as this
project.

In addition, we request that you give us the right to change the license in the future.

TimeCore uses a specialized mapping set which adds the readable parameter names to the functions.
See [Mappificator Project](https://github.com/alcatrazEscapee/Mappificator) in order to install that. You need to
generate the mappings, using the command below, while you are in the root folder of that project.

```
py src/mappificator.py -p -v 1 --mc-version 1.19.2 --providers yarn parchment --yarn-version 9 --parchment-version 2022.11.27-1.19.2 
```