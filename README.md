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
Note: if you want to use TimeCore, you have to use mojang mappings (at future I'll try to add special repository, which
will remap mod's jar file to use with other mappings). Here you can see how to use
them: https://github.com/alcatrazEscapee/mappificator
1. Add this maven repo in the `repositories` closure:

1.15.2:
```groovy
    maven {
        name = "TimeConqueror's Maven"
        url = "https://raw.githubusercontent.com/TimeConqueror/maven/master"
        artifactUrls 'https://github.com/TimeConqueror/maven/blob/master/' //fallback url
    }
    maven {
        name = 'sponge'
        url = 'https://repo.spongepowered.org/maven'
    }
```

1.16.4:
```groovy
    maven {
        name = "TimeConqueror's Maven"
        url = "https://repo.repsy.io/mvn/timeconqueror/mc/"
    }
```

2. Add this dependency in the `dependencies` closure:
1.15.2:
```groovy
implementation group: 'ru.timeconqueror', name: 'TimeCore', version: '1.15.2-<version-placeholder>', classifier: 'dev', changing: true
```

1.16.4:

```groovy
implementation fg.deobf("ru.timeconqueror:TimeCore:1.16.4-<version-placeholder>:dev")
```

3. Add this string as a `JVM Argument`:
   `-Dmixin.env.disableRefMap=true`

4. Optional: since TimeCore has kotlin libraries, it guarantees to work in runtime. For using it in dev workspace:

4.1. Add `plugins` closure:

```groovy
plugins {
    id "org.jetbrains.kotlin.jvm" version "1.3.72"
}
```

4.2 Add Maven Central's repository, if you don't have it.

```groovy
repositories {
    mavenCentral()
}
```

4.3 Add these dependencies:

```groovy
dependencies {
    implementation 'org.jetbrains:annotations:18.0.0'
    implementation 'org.jetbrains.kotlin:kotlin-stdlib-jdk8'
    implementation 'org.jetbrains.kotlin:kotlin-reflect'
}
```