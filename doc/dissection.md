# Dissection
## State so Far
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



