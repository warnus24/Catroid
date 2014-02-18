@echo off

for /R "assets" %%f in (*.source) do (
  sed.exe -r "s/^([[:space:]]*)(when |broadcast |print |wait |repeat |end of loop)/\1And \2/g" %%f > %%f.feature
)
