# What's this?
This is an android app to track predictions, inspired by 
[predictionbook](https://predictionbook.com/).  

# Motivation
 * I wanted to track predictions similarly to [predictionbook,](https://predictionbook.com/) 
 anytime, anywhere ...
 * ... but predictionbook is not that mobile friendly
 * ... and is dependent on internet access

# Build
Before starting a build with gradle, ensure that the file `keystore.properties` exists and 
looks like [`keystore.properties.template.`](./keystore.properties.template)  For signed builds this 
file has to contain a valid 
[signing configuration.](https://developer.android.com/studio/publish/app-signing.html#sign-apk)
Otherwise the provided dummy data is sufficient.
