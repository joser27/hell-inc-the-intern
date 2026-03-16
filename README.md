<p align="center">
  <img src="https://joser27-portfolio.s3.us-east-1.amazonaws.com/images/pageArt.png" alt="Hell Inc. logo" width="400">
</p>

# Hell Inc. — The Intern

![Hell Inc. — The Intern](https://joser27-portfolio.s3.us-east-1.amazonaws.com/gifs/hell-inc.gif)

You're a demon intern with a quota of eight souls and a boss named Gary who will not stop emailing you.

No weapons. No combat. Just a clipboard, a town full of people, and the one thing that actually closes deals — finding out what someone *really* wants. Knock on doors. Have real conversations. Get them to sign before the town figures out something is wrong.

**Every NPC is powered by Claude AI.** There's no script, no magic phrase. You have to listen and pitch — or get a door slammed in your face and a passive-aggressive message from Gary about your close rate.

---

## Play / Download

**[Play on itch.io → https://joser27.itch.io/hell-inc-the-intern](https://joser27.itch.io/hell-inc-the-intern)**

Windows build (zip). If Windows shows "Windows protected your PC," click *More info* → *Run anyway* — normal for unsigned apps.

---

## The residents

Eleanor won't stop mentioning her late husband. Gerald knows everything about everyone. Casey streams and won't stop talking about it. Aldous wants his competitor destroyed. Marta wants her neighbor Agnes to suffer. Cassius was important once. Father Creed doesn't want anything — which is your problem.

**Modes:** Campaign (8 souls to win) or Endless (escalating quotas; see how long you last). The only pressure is suspicion: hit 100% and the town reports you.

**Controls:** WASD to move · E to knock · Type + Enter to talk · Escape to leave

---

## For developers

- **Stack:** Java 17, Maven. NPC dialogue via Claude API (or Lambda proxy). Maps: Tiled (TMX). Profiles: `res/npcs.json`.
- **Run:** `mvn clean package` then `java -jar target/demonic-contractor-1.0-SNAPSHOT.jar`
- **Windows build for itch:** `.\packaging\build-windows.ps1` — then zip `dist\Demonic Contractor`. See script for manual jpackage steps.
