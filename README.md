# ClanCats
![Time](https://hackatime-badge.hackclub.com/U0922GMGGTU/ClanCats?label=Time+I+spent+on+this)

**Commands**:
- /clan:
  - alias: /c
  - subcommands:
    - create:
      - you can only run this when you aren't in a clan
      - usage: /clan create <clan name>
    - disband:
      - you can only run this when you are in a clan, and you're the owner
      - usage: /clan disband
    - invite:
      - you can only run this when you have owner/admin and there's players online with no clans
      - usage: /clan invite <player>
    - accept:
      - you can only run this when you have received any invites to any clans (can be multiple, tab complete shows it)
      - usage: /clan accept <clan name>
    - promote:
      - you can only run this, when you have at least admin, and there are players in the clan with member role that you can promote
      - usage: /clan promote <player>
    - demote:
      - you can only run this if you have owner and there are admins in the clan that you can demote
      - usage: /clan demote <player>
    - transfer:
      - you can only use this if you are not lonely in the clan
      - usage: /clan transfer <player>
    - kick:
      - you can only use this if there are players in the clan with lower role than you (so admin can only kick members, owner can kick both admins and regular members)
      - usage: /clan kick <player>
    - leave:
      - you can only use this if you are in a clan
      - usage: /clan leave
    - You will also need the permission clancats.command.clan.<subcommand> to run the command, not just the scenario I described earlier
  - by default this opens a gui containing members of the group
  - permission: clancats.command.clan
    **Features**:
  - you can't hit your clanmates
  - that's it

**Building**:
```shell
git clone https://github.com/JGJ52/ClanCats.git
cd ClanCats
mvn clean package
```
**Include this plugin in your project**:
```xml
<repository>
  <id>jgj52-repo</id>
  <url>https://maven.jgj52.hu/repository/maven-releases/</url>
</repository>
```
```xml
<dependency>
  <groupId>hu.jgj52</groupId>
  <artifactId>ClanCats</artifactId>
  <version>1.0</version>
  <scope>provided</scope>
</dependency>
```
Example to get the name of a clan that the player "player214" is in:

```java
import hu.jgj52.clanCats.Types.Clan;
import org.bukkit.Bukkit;

Clan clan = Clan.fromPlayer(Bukkit.getPlayer("player214")); // this can be null if player does not have a clan!
String clanName = clan.getName();
```
you can use this to display it behind their name etc.