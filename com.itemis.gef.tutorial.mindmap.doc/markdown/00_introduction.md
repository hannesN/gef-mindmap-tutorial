# Introduction

Since 2010 a new code base of the Graphical Editing Framework (GEF) was developed under the name GEF4 and finally reached version 1.0.0 in the current (Neon) Eclipse release train. 

In this tutorial series we will dive into the new GEF API and develop a simple mind map editor.

## Brief History of GEF

The Graphical Editing Framework (GEF) is a framework to build user interfaces, which aren't possible with native widgets. Its most common use is to develop diagram editors, like the simple mind map editor we will create in this tutorials.

GEF was developed by IBM and released as open source project in 2002. GEF consisted of two components: Draw2D, a graphical visualization library on top of SWT and the GEF (MVC) component.    

In 2010 the GEF team started developing a new version of GEF in parallel. It was called GEF4 and used JavaFX instead of SWT as rendering framework. To get more information  , please refer to the [GEF Github Wiki](https://github.com/eclipse/gef/wiki#developer-documentation).

GEF4 and the old GEF, now called GEF Legacy are part of the Eclipse release train. In the Neon release, GEF4 reached version 1.0.0 and used the project namespace `org.eclipse.gef4`  to prevent collisions with the old GEF code. However, in the next release train, the framework now called GEF4 will be GEF version 5.0.0 and switch to the GEF namespace `org.eclipse.gef`.


## The Code

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