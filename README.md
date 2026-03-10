# demo2DGame
Practice shortest path algorithm. A*.

2 player game.


**Windows exe (recommended):**  
`.\packaging\build-windows.ps1` — builds JAR, runs jpackage with launcher fixes, copies `Run Demonic Contractor.bat`. Zip `dist\Demonic Contractor` for itch.io.

**Manual:**  
`mvn clean package`  
`New-Item -ItemType Directory staging -Force; Copy-Item target\demonic-contractor-1.0-SNAPSHOT.jar staging\`  
`& "$env:JAVA_HOME\bin\jpackage.exe" --input staging --main-jar demonic-contractor-1.0-SNAPSHOT.jar --name "Demonic Contractor" --app-version 1.0 --type app-image --dest dist --java-options "-Xmx512m" --win-console --jlink-options "--strip-debug --no-man-pages --no-header-files --compress zip-6"`  
Then add `win.norestart=true` under `[Application]` in `dist\Demonic Contractor\app\Demonic Contractor.cfg` and copy `packaging\Run Demonic Contractor.bat` into `dist\Demonic Contractor\`.