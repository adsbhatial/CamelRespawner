
# CamelRespawner
This plugin checks for natural cat spawns in desert village and a Camel is spawned in the cat's place.


## Config.yml

Following are the variables which are available in the config.yml for the plugin.
 - **`debug`**: *true/false*
	 - Sets the debug mode as true or false
- **`console.longpluginname`**: *true/false*
	- Set whether the full name of plugin in the console logs or the short name(CR)
- **`cat_to_camel_chance`**: *0 - 1*
	- The chance of conversion of a natural cat in a desert village to a camel. Anyvalue above 1 will result in a 100% chance
- **`camels.check_camels`**: *true/false*
	- Whether to check nearby camels while spawning a new one 
- **`camels.max_camels_nearby`**: *any integer value*
	- Maximum number of camels which can spawn in the village in the search radius
- **`camels.search_radius`**: *any integer value*
	- Search radius for searching for nearby camels

## Commands
All commands for the plugin start with camelrespawnner. A few of the commands require specific permissions which can be any permission plugin. OP have all the permissions by default.
- **`camelrespawner`**
	- shows the list of commands
- **`camelrespawner help`**
	- shows the docs link. Upcoming
- **`camelrespawner commands`**
	- shows the list of commands
- **`camelrespawner reloadconfig`**
	- reloads the config.yml and messages.yml file
- **`camelrespawner viewconfig`**
	- shows the current config values
- **`camelrespawner [v|version]`**
	- shows the plugin version, system version and a lot of other information

## Permissions
Following are the list of permissions required for using specific commands
- **camelrespawner.viewversions**
	- permission for using the camelrespawner version command
-  **camelrespawner.reloadConfig**
	- permission for using the reloadconfig command
- **camelrespawner.viewconfig**
	- permission for using the viewconfig command

## Compiling
If you want to compile the project yourself, you can clone the repository and use the `maven compile` command in the root of the project to compile. The jars will be generated in the target folder in the root of the project

## Contributing

This is an open-source project available under the MIT license. You can contribute to the project if you find a bug or open a new issue in the project. Please note that the project requires verified commits to your fork if you are planning on contributing. 

## Work based on 

This work is based on [ShulkerRespawner](https://github.com/JoelGodOfwar/ShulkerRespawner) by [JoelGodOfWar](https://github.com/JoelGodOfwar). Without their plugin, this plugin would not exist and a huge thanks goes to them on creating the ShulkerRespawner plugin.
This work also takes some libraries from [DriveBackupV2](https://github.com/MaxMaeder/DriveBackupV2) by [MaxMaeder](https://github.com/MaxMaeder). Huge thanks for their work as well. 
