import java.util.*;

/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    QuadTree bananas = new QuadTree();
    LinkedList plswork;
    // Recommended: QuadTree instance variable. You'll need to make
    //              your own QuadTree since there is no built-in quadtree in Java.

    public class QuadTree {
        Node root;

        public QuadTree() {
            double upperLeftlong = MapServer.ROOT_LRLON;
            double upperLeftlat = MapServer.ROOT_ULLAT;
            double bottomRightlong = MapServer.ROOT_ULLON;
            double bottomRightlat = MapServer.ROOT_LRLAT;
            root = new Node(upperLeftlong, upperLeftlat, bottomRightlong, bottomRightlat, 0,  "root");
        }


        public class Node {
            double upperLeftlong;
            double upperLeftlat;
            double bottomRightlong;
            double bottomRightlat;
            double middleMidlong;
            double middleMidlat;
            double depth;
            Node topLeft;
            Node topRight;
            Node bottomLeft;
            Node bottomRight;
            String Filename;


            public Node(double upperLeftlong, double upperLeftlat, double bottomRightlong, double bottomRightlat, double depth, String Filename) {
                this.upperLeftlong = upperLeftlong;
                this.upperLeftlat = upperLeftlat;
                this.bottomRightlong = bottomRightlong;
                this.bottomRightlat = bottomRightlat;
                this.Filename = Filename;
                this.depth = depth;
                childNode();

            }

            public void middleSpot() {
                double longMidtop = (this.upperLeftlong + this.bottomRightlong) / 2;
                double latMidleft = (this.upperLeftlat + this.bottomRightlat) / 2;
                middleMidlong = longMidtop;
                middleMidlat = latMidleft;
            }

            public void childNode() {
                if (Filename.equals("root")) {
                    Filename = "";
                }
                if (this.Filename.length() == 7) {
                    return;
                }
                topLeft = new Node(upperLeftlong, upperLeftlat, middleMidlong, middleMidlat, Filename.length(), Filename + "1");
                topRight = new Node(upperLeftlong, middleMidlat, middleMidlong, bottomRightlat, Filename.length(), Filename + "2");
                bottomLeft = new Node(middleMidlong, upperLeftlong, bottomRightlong, middleMidlat, Filename.length(), Filename + "3");
                bottomRight = new Node(middleMidlong, middleMidlat, bottomRightlong, bottomRightlat, Filename.length(), Filename + "4");


            }

            public Node[] getNode() {
                Node[] lol = {topLeft, topRight, bottomLeft, bottomRight};
                return lol;
            }

            public double LonDPP() {
                return (bottomRightlong - upperLeftlong) / 256;
            }

            public boolean intersect(double lon, double lat) {
                if (upperLeftlong <= lon || lon <= bottomRightlong) {
                    if (upperLeftlat <= lat || lat <= bottomRightlat) {
                        return true;
                    }
                }
                return false;
            }
        }

    }

    public void checkIt(QuadTree.Node root, Map<String, Double> params) {
        plswork = new LinkedList<QuadTree.Node>();
        double lrlon = params.get("lrlon");
        double ullon = params.get("ullon");
        double lrlat = params.get("lrlat");
        double ullat = params.get("ullat");
        double w = params.get("w");
        if (root.Filename == "root") {
            if (root.intersect(ullon, lrlat)) {
                if (root.LonDPP() <= (lrlon - ullon / w)) {
                    System.out.println("pooooooop");
                    plswork.add(root);
                }

            }
        }
        else {
            if (root.Filename.length() < 7) {
                for (int i = 0; i < 4; i++) {
                    QuadTree.Node[] dick = root.getNode();
                    if (dick[i].intersect(ullon, ullat)) {
                        System.out.println(dick[i].Filename);
                        if (dick[i].LonDPP() <= (lrlon - ullon / w)) {
                            System.out.println("poop");
                            plswork.add(dick[i]);
                        }
                        else {
                            QuadTree.Node poops = dick[i];
                            checkIt(poops, params);
                        }
                    }
                }
            }
            else {
                plswork.add(root);
                return;
        }
    }

//        for (int i = 0; i < plswork.size(); i++) {
//            QuadTree.Node kok = (QuadTree.Node) plswork.get(i);
//            System.out.println(kok.Filename);
    }





    /** imgRoot is the name of the directory containing the images.
     *  You may not actually need this for your class. */
    public Rasterer(String imgRoot) {
        // YOUR CODE HERE
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     * </p>
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified:
     * "render_grid"   -> String[][], the files to display
     * "raster_ul_lon" -> Number, the bounding upper left longitude of the rastered image <br>
     * "raster_ul_lat" -> Number, the bounding upper left latitude of the rastered image <br>
     * "raster_lr_lon" -> Number, the bounding lower right longitude of the rastered image <br>
     * "raster_lr_lat" -> Number, the bounding lower right latitude of the rastered image <br>
     * "depth"         -> Number, the 1-indexed quadtree depth of the nodes of the rastered image.
     *                    Can also be interpreted as the length of the numbers in the image
     *                    string. <br>
     * "query_success" -> Boolean, whether the query was able to successfully complete. Don't
     *                    forget to set this to true! <br>
     * @see #REQUIRED_RASTER_REQUEST_PARAMS
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        bananas.root.middleSpot();
        System.out.println("pls work");
        checkIt(bananas.root, params);

        HashSet lat = new HashSet();
        HashSet lon = new HashSet();
        for (int i = 0; i < plswork.size(); i ++) {
            QuadTree.Node lol = (QuadTree.Node) plswork.get(i);
            lat.add(lol.upperLeftlat);
            lon.add(lol.upperLeftlong);
        }
        for (int i = 0; i < plswork.size(); i++) {
            QuadTree.Node kok = (QuadTree.Node) plswork.get(i);
            System.out.println(kok.Filename);
        }
        System.out.println(plswork.size());
        Collections.sort(plswork);
        String[][] itsok = new String[lon.size()][lat.size()];
        int count = 0;
        for (int i = 0; i < lon.size(); i++) {
            for (int e = 0; e < lat.size(); e++) {
                QuadTree.Node render_grid = (QuadTree.Node) plswork.get(count);
                itsok[i][e] = render_grid.Filename;
                count++;
            }
        }
        for (int i = 0; i < plswork.size(); i++) {
            QuadTree.Node kok = (QuadTree.Node) plswork.get(i);
            System.out.println(kok.Filename);
        }
        QuadTree.Node first = (QuadTree.Node) plswork.get(1);
        QuadTree.Node last = (QuadTree.Node) plswork.get(plswork.size());
        double raster_ul_lon = first.upperLeftlong;
        double raster_ul_lat = first.upperLeftlat;
        double raster_lr_lon = last.bottomRightlong;
        double raster_lr_lat = last.bottomRightlat;
        double depth = last.Filename.length();
        boolean query_success = true;
        System.out.println(raster_lr_lat);
        System.out.println(raster_lr_lon);
        System.out.println(raster_ul_lat);
        System.out.println(raster_ul_lon);
        System.out.println(params);
        Map<String, Object> results = new HashMap<>();
        results.put("render_grid", itsok);
        results.put("raster_lr_lon", raster_lr_lon);
        results.put("raster_lr_lat", raster_lr_lat);
        results.put("raster_ul_lon", raster_ul_lon);
        results.put("raster_ul_lat", raster_ul_lat);
        results.put("depth", depth);
        results.put("query_success", query_success);

        System.out.println("Since you haven't implemented getMapRaster, nothing is displayed in "
                           + "your browser.");
        return results;
    }


//    public void main(String[] args) {
//        Map dick = new HashMap();
//        dick.put("lrlon", 122.24053369025242);
//        dick.put("ullon", 122.24163047377972);
//        dick.put("w", 892.0);
//        dick.put("h", 875.0);
//        dick.put("ullat", 37.87655856892288);
//        dick.put("lrlat", 37.87548268822065);
//        getMapRaster(dick);
//    }
}

