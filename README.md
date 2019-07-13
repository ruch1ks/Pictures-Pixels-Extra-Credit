# Pictures-Pixels-Extra-Credit
An application built using Java and Java Swing that allows users to modify a pre-loaded image in different ways. 

# Motivation
This was the extra credit project for my Foundations of Programming course.

# Features
Users can choose between a FramePuzzle, ImageAdjuster, and PixelInspector tools. The FramePuzzle allows the user to shift pre-determined regions of the image using the arrow keys. The ImageAdjuster lets the user blur the image, adjust its saturation, and change its brightness. The PixelInspector allows the user to click any pixel on the image and displays the pixel's location, RBG color values, and its brightness on a scale from 0 (black) to 1 (white). The ImageAdjuster class is multithreaded, where one thread solely handles the logic for refreshing the picture, and the main thread handles all other logic. 

# Installation
This project requires Java 1.8 or higher. It can be cloned into and ran from any IDE of your choice. The main methods for each feature are located in FramePuzzle.java, ImageAdjuster.java, and PixelInspector.java.
