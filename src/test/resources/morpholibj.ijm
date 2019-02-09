// Create a 3D test image.
//
newImage("3d-test", "8-bit black", 100, 100, 100);
setForegroundColor(255, 255, 255);
setSlice(20);
makeOval(20, 26, 5, 5);
run("Fill", "slice");
setSlice(40);
makeOval(53, 60, 17, 18);
run("Fill", "slice");
setSlice(60)
makeRectangle(43, 26, 45, 3);
run("Fill", "slice");
run("Maximum 3D...", "x=2 y=2 z=2");

// Segment and measure.
run("Connected Components Labeling", "connectivity=6 type=[8 bits]");
run("Analyze Regions 3D", "volume surface_area mean_breadth sphericity euler_number centroid inertia_ellipsoid ellipsoid_elongations max._inscribed surface_area_method=[Crofton (13 dirs.)] euler_connectivity=C26");
