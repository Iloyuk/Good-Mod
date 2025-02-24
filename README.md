<h1 align="center">good mod</h1>

a good mod

**Contributors:**
- [TheHolyChickn (main developer)](https://github.com/TheHolyChickn/)
- [odtheking (refactored the entire project in kotlin with normal names/struture)](https://github.com/odtheking)
- [bonsai (made the STUPID GUI WORK)](https://github.com/freebonsai)
- [Iloyuk (fixed the code more times than  can count)](https://github.com/Iloyuk)
- [AzuredBlue (told me to google something)](https://github.com/AzuredBlue)
- [this tutorial and template odtheking told me to ~~steal~~ look at (u guys rlly thought i could set this all up on my own with no past experience??? lolllll)](https://moddev.nea.moe/)
- litdab (gave me advice i didnt listen to then gave me more advice)
- [ChatGPT (the main developer)](https://chatgpt.com/)
- kikias22 (artist)
- [Intellij Idea (nvim still better)](https://www.jetbrains.com/idea/)
- [Linux (it just makes doing things easier)](https://en.wikipedia.org/wiki/Linux)
- [AzureAaron (fixed something in the readme)](https://github.com/AzureAaron)

**IMPORTANT** *please only use the Hypixel API for now. Although the other API endpoints work, they will freeze your game every time you open a chest. The fix is not difficult but I have not had time to complete it yet.*

**IMPORTANT VERSION 0.0.2-BETA INFORMATION**
*This version does not use records of drops from Version 0.0.1-Beta.* All future versions will also not support your drops during Version 0.0.1-Beta. This is due to swapping both the location and the formatting of the drops.json file, the file where your drops are stored. The location is moved from your config folder to the goodmod subdirectory, which ensures compatability with other mods, and the reformatting is due to a rewrite of the config file management system, which makes the code understandable instead of whatever ChatGPT-powered mess I had before.

*IF YOU WANT TO RESTORE YOUR DUNGEON DROPS THAT WERE SAVED BY VERSION 0.0.1*, then after installing version 0.0.2-Beta and opening the mod, please navigate to your config folder and open both the drops.json file in your config folder, and the drops.json file in the goodmod subdirectory of your config folder. I recommend opening these with Notepad, as I had various encoding issues that Notepad avoids. Specifically, using VSCode messed up the encoding of the files. If you intend on using VSCode, please [switch the encoding](https://stackoverflow.com/questions/30082741/change-the-encoding-of-a-file-in-visual-studio-code) to ISO 8859-1 BEFORE you save the file. I have absolutely no idea how to do this in Vim or any other text-editor/IDE.

I intend on in the future implementing an (optional) log-scanning function, which will retroactively cache all drops and runs you have saved in your Minecraft logs. Assuming you don't delete logs, this WILL include any drops cached by the 0.0.1-Beta version. So if you do not want to restore the drops manually, feel free to wait for this functionality to be implemented. I intend on working on this after fixing the known bugs and implementing different ways to view your stored dungeon loot.

<h2><font color=#00FF99>Features</font></h2>
good mod offers two features I've wanted for a long time, but are not present in <a href="https://github.com/Skytils/SkytilsMod/tree/dev">Skytils</a> or <a href="https://skyblockextras.com/">SBE</a>.

1. **Good Profit Calculator (WIP)**<p>
All the profit calculators are kinda horrible, so I am working on my own. My solution is extremely customizable, from allowing you to select the specific API you want to use for profit calculation, all the way to allowing you to select which items to calculate as instasell, and which to calculate as instabuy. Features include:
    - Croesus Gui Highlights
      - Highlights unopened chests in green, opened (but still keyable) chests in gray, and keyed/not openable chests in black
      - *(WIP) A future release* will allow Colors to be customizable
      - *(WIP) A future release* will implement an option to use the Hypixel API to scan all of your Croesus chests before you even open them, allowing the mod to more intelligently highlighty what chests to open (eg: only highlighting in gray the chests you should use a key on, instead of all chests that are possible to use a key on)
    - Croesus Chest Highlights
      - Highlights the most profitable chest in green
      - If you haven't opened a chest yet and the second most profitable chest is profitable enough that using a chest key is profit, it will also be highlighted in gray
      - *(WIP) A future release* will implement kismet logic, meaning if using a kismet on the bedrock chest yields a positive EV (the average profit is greater than 0), it will be highlighted in a different color (blue maybe?)
      - *(WIP) A future release* will implement per-item selection of sell offer or instasell usage, meaning instead of making ALL of your items use either instasell or sell offer prices, you can choose specific items to use one of them and specific items to use the other (eg: Recombs use sell offer prices, but all enchanted books use instasell prices except for Legion, etc.)
      - *(WIP) A future release* will implement multiple options for the API used to calculate profit. Currently, only the Hypixel API works, but the config GUI has options **that I will eventually make functional** for using the <a href="https://api.hypixel.net/">Hypixel</a> API, <a href="https://sky.coflnet.com/api/index.html">COFL</a> API, and the <a href="https://github.com/Tricked-dev/lowestbins">API that Skytils uses</a>, which is developed and hosted by <a href="https://github.com/Tricked-dev">Tricked dev</a>. The API selection will default to the Hypixel API to avoid putting excessive strain on any third party API. If you would like me to add another API, please open an issue and I will decide whether to add it or not
    - *(WIP)* Dungeon Chest Profit Display
      - Not complete yet, but will mimic the SBE profit display when complete</p>
2. **Dungeon Drops Tracker**<p>
<a href="https://skyblockextras.com/">SBE</a> actually has this option, but it's extremely outdated and randomly resets itself sometimes. This solution has both a GUI and a chat command element.
 - I intend to redo this feature almost completely, also adding a log for the amount of profit you have made, and an optional log for all drops from all runs that you claim.</p>

In the future, I may implement anti-ironman dark auction alerts, so you can outbid any time an ironman player is winning in the dark auction.

<h2><font color=#00FF99>Installation</font></h2>

good mod is a [Forge](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.8.9.html) mod, so if you don't already have Forge, [download the latest 1.8.9 release](https://files.minecraftforge.net/net/minecraftforge/forge/index_1.8.9.html) here, and follow the installer's instructions. If you've never downloaded forge before, notice that the download button redirects you to an ad, and you have to wait 5 seconds (see top right corner) before you can proceed to download. **ONLY** click the button in the top right corner, or you could get a virus. After you install forge, find the <tt>1.8.9-forge1.8.9-11.15.1.2318-1.8.9</tt> version in your Minecraft launcher (you may have to go over to Installations and create a new one), and run it. After it loads, close your game. I still have to set up Minecraft on my laptop's Windows partition, so I may record a video of installing forge and post it here later.

Download the <b><font color=#FF0000>.jar</font></b> file in the [latest release](https://google.com). Locate your <tt>.minecraft</tt> folder. On windows, you can find this by opening File Explorer, and searching for <tt>%appdata%</tt>. On Linux, your <tt>.minecraft</tt> is probably located at <tt>~/.minecraft</tt>. On MacOS, I have no idea. Once inside your <tt>.minecraft</tt> folder, find and open your <tt>mods</tt> folder. Now, open another File Explorer window and navigate to your <tt>Downloads</tt> folder. Locate the **good mod .jar file**, and drag it into your <tt>mods</tt> folder.</p>

<h2><font color=#00FF99>Usage</font></h2>


The configuration menu can be accessed with `/goodmod`. If you are using the Hypixel API, you can force update the pricing data with `/updateauctions`. To list dungeon drops in chat, you can run /getitems. To avoid conflicts with other mods, you can change the alias for any of these commands in the goodmod menu. If you forget your alias for a command, you can run `/goodmod:{command}` where `{command}` is the *original name* of the command. You can also run `/goodmod:commands` to return a list of all command aliases.

<h2>Known Bugs</h2>
<p>If you see a bug, report it, and I'll add it here until I fix it!</p>

- The "stuff display" GUI: This display will likely break or have items cropped out if you do not play in fullscreen, or if you play in a resolution below 1080p. Additionally, you might find the display too small on some large monitors. Automatic scaling will be implemented in the future.
 Sometimes, loot just does not register. This appears to only occur when claiming loot after a dungeon run, but it does not happen every time. For now, please claim from Croesus, and report any instances of loot not logging.

<h2>Future Plans</h2>

I plan to do the following:

- Fix bugs
- Clean up the stuff display and make it look nicer
- Implement toggleable options for the stuff display: either the current display, or display drops on each floor (like split up the recombs you drop per floor)
- implement an option to export dungeon drops to a nicely formatted image file you can share with your friends to flex
- customizability of what items are sent on /getitems
- a toggleable gui element which tracks a list of items you can customize
- tracking kismets per floor
- improve profit calc

<h2>Licensing</h2>

MIT License

Copyright (c) 2024 The Holy Chicken

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
