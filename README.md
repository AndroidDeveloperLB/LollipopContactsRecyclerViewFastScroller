# LollipopContactsRecyclerViewFastScroller
A sample of how to mimic the way that the contacts app handles a Fast-Scroller for a RecyclerView

This POC is based on [this website's tutorial](https://blog.stylingandroid.com/recyclerview-fastscroll-part-2/#comment-67201), but with a lot of fixes, optimizations, cleaner code, and of course styling like on Lollipop's contacts app.

Demo:

![enter image description here](https://raw.githubusercontent.com/AndroidDeveloperLB/LollipopContactsRecyclerViewFastScroller/master/demo.gif)

**Screenshots**

Here's how the contacts app of Android Lollipop (5.0.x) looks like:
![enter image description here](https://raw.githubusercontent.com/AndroidDeveloperLB/LollipopContactsRecyclerViewFastScroller/master/lollipop%20contacts%20app.png)

and here's how I managed to mimic its fast-scroller:
![enter image description here](https://raw.githubusercontent.com/AndroidDeveloperLB/LollipopContactsRecyclerViewFastScroller/master/demo.png)

**Known issues and TODOs**

 1. It's a POC, so it's not so comfortable to customize, but on the other hand, you can put any style you wish.
 2. I think there are issues when changing the orientation.
 3. Not a library. I hope I can think of a way that this could be a nice library
 4. Minimal API is 11 . I'd like it to be lower.
 5. Supports only LinearLayoutManager for now. Wish it would also support Gridlayoutmanager .
