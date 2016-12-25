# WoW neural networks rotation helper

- end goal is to produce an addon that can import learned models

### Step 1: Generate/retrieve raw data
Two alternatives:
- Retrieve top x% logs from WacraftLogs (but: how to export raw logs?)
- Generate logs using simcraft and export the combat log
-- These should vary: E.g. heroism delayed by a random amount
==> maybe even "training as a service", given a set of talents + character

For now, simcraft logs are assumed. 

Implementation: Any language that can call simcraft on the command line

### Step 2a: Clean data
Many events in logs are unneccessary (e.g. "0.000 Character has arisen"). 
This step removes unneccessary events.

Implementation: Regex (any language)

### Step 2b: Transform into csv format
Take logs as input: For each relevant row in the log, generate one structured row in the csv. 
The row should include:
- The skill that was used/buff that was gained set to 1
- Current buffs (e.g. "Heating up")
- The label: The next ACTION the player should perform (this should not be "gain X buff")

A row should be something that can change the prediction of the next skill:
- Performing a skill
- Gaining a buff


Implementation: Is not main focus, should be flexible language: Will use Java


### Step 4: Train model (neural networks)
Taking the csv as input: train a network, either RNN or LSTM (if a different ML method is used: need additional ngram features).

Implementation: 

Maybe add features here?
Additional features (these may be optional): 
- Time since last skill use (may help with cooldowns such as combustions which are more seldemoly used)


### Step 5: Export model
The neural network that was trained should be parseable and usable by another language, e.g. should just be a matrix of weights. 


### Step 6: Import and use model
The model can be used within a WoW addon. The WoW addon continually parses the combat log (which has a different structure than the simcraft logs) and uses relevant rows as input for the addon. The output should be shown to the user in
- "productive" mode: Show
- "debug" mode: Show all class probabilities


Implementation: Lua 





