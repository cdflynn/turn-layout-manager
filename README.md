# Turn Layout Manager

A simple carousel for RecyclerView.

##Usage

Just create a new `TurnLayoutManager` using the constructor:
```
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


##How It Works

`TurnLayoutManager` uses the base functionality of `LinearLayoutManager` with some slight modifications.  Child views are positioned normally as `LinearLayoutManager` does, but they're offset along the rotation radius and peek distance.  This involves some trade offs.

#####Benefits:

- Automatically **supports predictive animations**, including mutations to radius and peek distance.
- Inherits stable support for different scroll directions and therefore can introduce support for `Gravity`.
- Does not attempt to re-solve the huge variety of edge cases that `LinearLayoutManager` already solves, and thus avoids re-introducing those exceptions.

#####Drawbacks:

- It's less efficient than a from-scratch implementation of `LayoutManager`.  Specifically, `TurnLayoutManager` will have a strictly longer layout pass than `LinearLayoutManager`, and for very heavyweight list rows it may drop a frame that `LinearLayoutManager` otherwise would not.  _No matter what layout manager you use, try to keep your item layouts efficient._  
- List items are not forced to adjust their position parallel to the scroll direction, only their perpendicular offset.  Items enter and leave the screen a bit faster than they would on a real turning surface, though the effect is subtle.

A full re-implementation of a new `LayoutManager` could potentially solve those drawbacks.  


