# Turn Layout Manager

A simple carousel for RecyclerView.

![Demo](https://github.com/cdflynn/turn-layout-manager/blob/master/app/img/turn_demo.gif?raw=true)

## Usage

Just create a new `TurnLayoutManager` using the constructor:
```java
TurnLayoutManager(context,              // provide a context
                  Gravity.START,        // from which direction should the list items orbit? 
                  Orientation.VERTICAL, // Is this a vertical or horizontal scroll?
                  radius,               // The radius of the item rotation
                  peek,                 // Extra offset distance
                  shouldRotate);        // should list items angle towards the center? true/false.
```

Just like a `LinearLayoutManager`, a `TurnLayoutManager` specifies an orientation, either `VERTICAL` or `HORIZONTAL` for vertical and horizontal scrolling respectively.  

In addition to orientation, supply a `Gravity` (either `START` or `END`).  Together, these define the axis of rotation.

```
Gravity.START
Orientation.VERTICAL

┏─────────┓
┃ x       ┃
┃  x      ┃
┃   x     ┃
┃   x     ┃
┃   x     ┃
┃  x      ┃
┃ x       ┃
┗─────────┛
```

```
Gravity.END
Orientation.VERTICAL
┏─────────┓
┃       x ┃
┃      x  ┃
┃     x   ┃
┃     x   ┃
┃     x   ┃
┃      x  ┃
┃       x ┃
┗─────────┛
     
```

```
Gravity.START
Orientation.HORIZONTAL
┏─────────┓
┃x       x┃
┃ x     x ┃
┃   xxx   ┃
┃         ┃
┃         ┃
┃         ┃
┃         ┃
┗─────────┛

```

```
Gravity.END
Orientation.HORIZONTAL
┏─────────┓
┃         ┃
┃         ┃
┃         ┃
┃         ┃
┃   xxx   ┃
┃ x     x ┃
┃x       x┃
┗─────────┛
```

## Install

Add the JitPack repository to your root build.gradle
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Add the dependency to your module's build.gradle
```
dependencies {
	        implementation 'com.github.cdflynn:turn-layout-manager:v1.3'
	}
```

## How It Works

`TurnLayoutManager` uses the base functionality of `LinearLayoutManager` with some slight modifications.  Child views are positioned normally as `LinearLayoutManager` does, but they're offset along the rotation radius and peek distance.  This involves some trade offs.

##### Benefits:

- Automatically **supports predictive animations**, including mutations to radius and peek distance.
- Inherits stable support for different scroll directions and therefore can introduce support for `Gravity`.
- Does not attempt to re-solve the huge variety of edge cases that `LinearLayoutManager` already solves, and thus avoids re-introducing those exceptions.

##### Drawbacks:

- It's less efficient than a from-scratch implementation of `LayoutManager`.  Specifically, `TurnLayoutManager` will have a strictly longer layout pass than `LinearLayoutManager`, and for very heavyweight list rows it may drop a frame that `LinearLayoutManager` otherwise would not.  _No matter what layout manager you use, try to keep your item layouts efficient._  
- List items are not forced to adjust their position parallel to the scroll direction, only their perpendicular offset.  Items enter and leave the screen a bit faster than they would on a real turning surface, though the effect is subtle.

A full re-implementation of a new `LayoutManager` could potentially solve those drawbacks.  

## License
```
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```
