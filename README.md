# Embers Rekindled
Embers is best described as a dwarven magic mod. It features a smattering of magical and technical content, from staples such as ore doubling and item transport to alchemy and magical ray guns. All centered around the core mechanic of Ember, a limited form of power that you must extract from the world's core near bedrock.

## Builds
You can download the latest files here on curseforge. https://minecraft.curseforge.com/projects/embers-rekindled

I don't know yet where the port will be released but for now there is an alpha build available here: https://github.com/RCXcrafter/EmbersRekindled/releases/tag/Alpha-1.20.1-0.0.4

## Contact
You can also talk about Embers Rekindled and contact me about the port on Bord's discord in the Embers channel here: https://discord.gg/J4bn3FG

# So about that port
I've decided to port Embers Rekindled to a Minecraft version beyond 1.12.2, I don't know yet which version it will release on but we'll see when we get there. 

## Things I have changed:
- Ember Dials don't display a decimal if it's zero (I don't think there was a good reason for that)
- Linking emitters and receivers no longer requires shift-clicking and you click on the emitter first now
- All blocks that are not full blocks can be waterlogged (why not I guess)
- Ember Receptors no longer say they accept ember if they're over half full, giving them plenty of buffer for long distance connections
- The mechanical core and machine accessor have been merged into one block as described in the elucent plans doc
- The bin is now made of lead since it's related to item transport/storage
- The checkmarks in the codex have been replaced with a dawnstone dot on uncompleted entries
- The mixer centrifuge now has small holes on the side to show what fluids are inside
- Stampers now stop working when they're trying to output to a bin that's full
- The pressure refinery now gives a maximum of 3x multiplication instead of 6x
- The ember bore now requires at least 3 bedrock touching the blades to mine ember
- The ember cluster is now worth 4400 ember instead of 3600
- Alchemy has been completely redesigned, you now have to match aspecti to ingredients and alchemical waste gives hints similar to the ones you get in the game mastermind
- The ember injector can now be placed up to one block away from a crystal seed to allow picking up drops with 6 injectors on one crystal
- Metal crystal seeds now generate in small ruins instead of being craftable, their metal type can be changed with an alchemy recipe
- Catalytic plugs now run on steam or dwarven gas, wildfire stirlings can run on dwarven gas too
- The limit on most machine upgrades has been removed and replaced with decreased effectiveness above a certain amount
- Machine upgrades can now be proxied up to 3 blocks away from a machine with mechanical cores, most upgrades lose effectiveness with distance
- The field chart can now be toggled between displaying the current ember and base ember instead of displaying both at the same time

## Things I want to change:
- There's a cool document with some of Elucent's plans for Embers, quite a bit of it has already been implemented by bord but there's also some ideas for some major changes and a port would be an ideal moment to implement them: https://github.com/RCXcrafter/EmbersRekindled/blob/rekindled/docs/elucent-plans.txt
- The extra metals should be more useful or they should be removed
- More alloys? Without external mods the mixer centrifuge is just a dawnstone machine
- Don't port overworld quartz ore, Embers doesn't require quartz for anything early game

## Textures
Minecraft got a big texture update sometime after 1.12 and quite a few mods are changing their textures to fit better with the new style, however I don't think Embers Rekindled should follow this example. In my opinion Embers has great textures that actually fit the aesthetic of the modern Minecraft textures better than the old textures and I think the port should stick with these textures except in a few cases. Here is a list of textures I do intend on changing:
- Ores, they should use the new stone textures and maybe also have unique shapes like the new ore textures
- Ingots and maybe nuggets, I want them to fit in Tinkers' Construct casts and the new ingot shape is slightly different
- Fluids, all of them are either an edit of lava or water and I'd like to make them more unique

# Food for thought:
## What the hell do I do with Mystical Mechanics?
That might seem like a weird question with an obvious answer but the reason I struggle to answer it for myself is Create. Create is the second Minecraft mod that does a good job at adding mechanical power related game mechanics to Minecraft (the first being Mystical Mechanics) and it currently dominates this market since Mystical Mechanics left a power vacuum after not being ported. This in itself is not such a big deal, Create is a great mod and I think it deserves its position at the top of the food chain of mechanical power mods. The issue with porting Embers to a version where create also exists is that Mystical Mechanics is an important part of Embers and I don't want want to leave it out of the port. This leaves me with a few options, none of which I particularly like:

### Leave all mechanical content out of the port
All of the Mystical Mechanics related content in the current version of embers is completely optional which makes this an easy option but as I said I don't want to leave it out because it's cool.

### Make mechanical content depend on Create instead of Mystical Mechanics
This is not going to be easy since the Create power system in inherently different from Mystical Mechanics in a balance sense. Mystical Mechanics is focused on active power generation, this means the machines take fuel and stop running when they run out, Create is focused on passive power generation, you can build a machine and it will just run forever without needing any fuel. Passive power generation works great for Create because it's entirely balanced around that but for Embers it doesn't work so well, not without major changes at least.
Also I don't want so much content to depend on a mod developed by an external team.

### Just pretend Create doesn't exist and port Mystical Mechanics 
Doing this would probably leave a lot of people wondering why the two systems aren't compatible and would probably cause a lot of people to ask questions. In the worst case someone might make an addon for a Create to Mystical Mechanics power converter which would then end up in every modpack that contains Embers Rekindled (since modpacks without Create don't exist) and in all of those modpacks the mechanical content from embers would be completely unbalanced because people would just use Create to generate the power.
