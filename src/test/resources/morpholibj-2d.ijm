// Create image
//
run("Close All");
run("Blobs (25K)");
run("Invert LUT");
run("Duplicate...", "title=mask");
setThreshold(111, 255);
run("Convert to Mask");

// Segment and measure using MorphoLibJ
//
run("Connected Components Labeling", "connectivity=4 type=[16 bits]");
run("Analyze Regions", "area perimeter circularity euler_number centroid inertia_ellipse ellipse_elong. convexity max._feret oriented_box oriented_box_elong. geodesic tortuosity max._inscribed_disc geodesic_elong.");
selectWindow("mask");
close();

// Explore results
//
run("Explore MorphoLibJ Segmentation", "labelmaskimageplus=mask-lbl intensityimageplus=blobs.gif resultstabletitle=mask-lbl-Morphometry");
