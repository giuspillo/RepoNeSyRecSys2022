KALE - customized version.

Our version has been customized in two features:
1. It now handles FOL rules with 4 atoms (x and y and z => w)
2. It read learning parameters by a file named "setting.txt"

The following guidelines, with the file "readme_original_repo.md" should be sufficient to perform graph embedding.

user-item and user-item-prop configurations (no rules):
1. preprocess as described in the original readme
2. set the setting files following the source code "kale.trip.Program"
3. execute the class "Program"

user-item-prop + rules configurations:
1. preprocess as described in the original readme
2. set the setting files following the source code "kale.joint.Program"
3. execute the class "KALEProgram"