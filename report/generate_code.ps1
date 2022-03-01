Push-Location ..
render50 -o report/code.pdf -f -i "*.java" --size "A4 landscape" src/main/java/*
render50 -o report/data.pdf -f -i "*.glsl" --size "A4 landscape" src/main/resources/*
Pop-Location
