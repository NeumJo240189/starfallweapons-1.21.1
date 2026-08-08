# Starfall Weapons

Code-only NeoForge 1.21.1 mod project. This project no longer depends on MCreator.

## Structure

- `registry/` – NeoForge item and creative-tab registrations
- `weapon/` – immutable weapon, skill, passive and rarity definitions
- `client/tooltip/` – tooltip state, data model and cosmic renderer
- `mixin/client/` – tooltip rendering plus keyboard and mouse input hooks
- `assets/starfallweapons/` – models, translations and pixel-art ability icons

`WeaponRegistry` connects registered Minecraft items to their immutable `WeaponDefinition`.
Add a new weapon by registering its item in `ModItems` and its definition in `WeaponRegistry`.

## Development

Requires Java 21. Run the client with:

```powershell
.\gradlew.bat runClient
```

Build a distributable JAR with:

```powershell
.\gradlew.bat build
```
