# Introduction

Learning a new framework can be a cumbersome enterprise, especially when the framework is new, the API still changing and not much documentation can be found. In this tutorial we want to have a closer look, how to adopt the Eclipse Graphical Editing Framework, short GEF. In little steps we will develop a graphical editor for mind maps.

But first, let me explain, what GEF really is:

## About GEF

##The Code

I strongly advise to follow the instructions and create the code on your own. Each class will be shown in the tutorial, so you can copy/paste parts as you like. If you want to see the final projects after a step, you can find the code of this tutorial on [Github](https://github.com/hannesN/gef-mindmap-tutorial). For each step in the tutorial a branch exists, so you'll be able to compare the your own code with the branch belonging to the steps.

## Convention

The tutorial will contain a lot of code and UI descriptions. It will use the following convention:

In a text `variables`, `method names`, and `class names` will be emphasized as code in the text.

Whole method will be rendered in a code block like this:

```java
public void implementMe(boolean done) {
	// nothing to show for now
}
``` 

But let's start with the requirements for developing a GEF application.