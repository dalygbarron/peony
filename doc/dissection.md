# Dissection
## State so Far
### Profiler results
I did a profile a minute ago, and unsurprisingly the two big things that take
time are rendering functions and trigonometric functions (which are mostly used
in rendering admittedly). Trigonometry is kind of hard to make more efficient in
it's own right, unless I was desperate enough to include a lookup table which I
am not.

However, a large reason that there are so many trigonometric operations is
the fact that the renderer maintains that stack of transformations which must be
applied one by one in every object's rendering functions. Every single
application of a transformation requires an atan2, a sin, and a cos.

Therefore, I could reduce the number of trigonometric function calls quite
significantly if I could find a correct way of combining and decombining
transformations, either within the current system or by turning them into
matrices. Actually, now that I come to think about it, if I made transformations
into matrices, there would be no trigonometric functions called at all since
rotation and everything is actually effected by the matrix. Shiet.

## Things that would have been better
### ViewListener interface
Keeping track of and having to create millions of functions for various
listeners was quite foolish. It would have been a lot wiser to create a
ViewListener interface, then make it that the view listens to it's own objects,
and when they do things that are relevant for our purposes, it would call a
function on it's view listeners (ie the main app object).

As well as not having to create all those add listener functions, it would also
make it that the main functionality of the viewlistener is in functions, not in
clojures that are added from code, so the organisation of the viewlistener would
be much better than it is currently.

Actually it would get rid of even more functions because since there would be
space for processing before whacking the ViewListener, we would no longer need
most if not all getters and setters.



