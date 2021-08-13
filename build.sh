#!/bin/bash

./gradlew publishToMavenLocal
mv ~/.m2/repository/com/yourname/modid/PortalGunForge/1.0/PortalGunForge-1.0.jar /Users/nickmarino/Library/Application\ Support/minecraft/mods
